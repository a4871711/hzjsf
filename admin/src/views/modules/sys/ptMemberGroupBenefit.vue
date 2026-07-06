<template>
  <div>
    <!-- 顶部标题 + 副标题 -->
    <div class="page-head">
      <div class="page-title">附赠团课权益</div>
      <div class="page-desc">会员购买私教商品后自动获赠的团课权益（只读查询）。权益由交易域下单/分期首付成功时自动发放，团课预约时自动核销，此处不做新增/编辑/删除。</div>
    </div>

    <!-- 统计卡片行：后端 list 未返回 stat，此处按「当前页数据」前端汇总 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="6" v-for="(card, idx) in statCards" :key="idx">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-label">{{ card.label }}</div>
          <div class="stat-value" :style="{ color: card.color }">{{ card.value }}</div>
          <div class="stat-unit">{{ card.unit }}</div>
        </el-card>
      </el-col>
    </el-row>

    <r-search ref="search" :searchData="searchData" :searchForm="searchForm" :searchHandle="searchHandle" />
    <r-table
      :isSelection="false"
      :isHandle="false"
      :isPagination="true"
      :tableData="tableData"
      :tableCols="tableCols"
      :tablePage="pagination"
      :loading="tableLoading"
      @refresh="page()"
      ref="rtable" />

    <!-- 权益详情 + 流水 只读抽屉 -->
    <el-drawer title="权益详情" :visible.sync="detailVisible" size="46%" :destroy-on-close="true">
      <div style="padding: 0 20px;" v-loading="detailLoading">
        <el-descriptions title="基础信息" :column="2" border size="medium" v-if="detail">
          <el-descriptions-item label="权益编号">{{ detail.id }}</el-descriptions-item>
          <el-descriptions-item label="会员">{{ detail.memberName || '-' }}<span v-if="detail.memberMobile"> / {{ detail.memberMobile }}</span></el-descriptions-item>
          <el-descriptions-item label="来源商品">{{ detail.sourceProductName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusTheme(detail.status)" size="mini">{{ statusText(detail.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="赠送次数">{{ detail.giftCount }}</el-descriptions-item>
          <el-descriptions-item label="已用 / 剩余">{{ detail.usedCount }} / {{ detail.remainingCount }}</el-descriptions-item>
          <el-descriptions-item label="生效时间">{{ parseTime(detail.effectiveTime) }}</el-descriptions-item>
          <el-descriptions-item label="到期时间">{{ parseTime(detail.expireTime) }}</el-descriptions-item>
        </el-descriptions>

        <div class="flow-title">权益流水</div>
        <el-table :data="flowList" border size="mini">
          <el-table-column label="类型" align="center" width="80">
            <template slot-scope="scope">
              <el-tag :type="flowTypeTheme(scope.row.flowType)" size="mini">{{ flowTypeText(scope.row.flowType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="变动" prop="changeCount" align="center" width="70" />
          <el-table-column label="变动前" prop="beforeCount" align="center" width="70" />
          <el-table-column label="变动后" prop="afterCount" align="center" width="70" />
          <el-table-column label="业务来源" align="center" width="90" :formatter="(row) => bizTypeText(row.bizType)" />
          <el-table-column label="业务ID" prop="bizId" align="center" width="80" :formatter="(row) => row.bizId || '-'" />
          <el-table-column label="时间" align="center" :formatter="(row) => parseTime(row.createdAt)" />
        </el-table>
        <div v-if="!flowList.length" style="color:#909399;font-size:12px;margin-top:10px;text-align:center;">暂无流水记录</div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
export default {
  components: {},
  data () {
    return {
      tableLoading: false,
      searchData: {
        memberKey: '',
        sourceProductKey: '',
        status: ''
      },
      searchForm: [
        { type: 'input', placeholder: '会员姓名/手机号', prop: 'memberKey', width: 200 },
        { type: 'input', placeholder: '来源私教商品', prop: 'sourceProductKey', width: 200 },
        { type: 'select',
          placeholder: '权益状态',
          prop: 'status',
          width: 160,
          options: [
          { value: 1, label: '生效中' },
          { value: 2, label: '已用完' },
          { value: 3, label: '已过期' },
          { value: 4, label: '已回收' }
          ] }
      ],
      searchHandle: [
        { label: '搜索', type: 'primary', handle: e => this.getData() },
        { label: '重置', handle: e => this.reset() }
      ],
      tableData: [],
      tableCols: [
        { label: '权益编号', prop: 'id', width: 90 },
        { label: '会员', type: 'html', width: 150, html: (row) => this.memberHtml(row) },
        { label: '来源商品', prop: 'sourceProductName', formatter: e => e.sourceProductName || '-' },
        { label: '赠送次数', prop: 'giftCount', width: 90 },
        { label: '已使用', prop: 'usedCount', width: 80 },
        { label: '剩余次数', type: 'html', width: 90, html: (row) => this.remainHtml(row) },
        { label: '生效时间', prop: 'effectiveTime', width: 160, formatter: e => this.parseTime(e.effectiveTime) },
        { label: '到期时间', prop: 'expireTime', width: 160, formatter: e => this.parseTime(e.expireTime) },
        { label: '状态', type: 'tag', width: 90, prop: 'status', theme: (row) => this.statusTheme(row.status), formatter: e => this.statusText(e.status) },
        {
          label: '操作',
          type: 'button',
          width: 90,
          fixed: 'right',
          btnList: [
            { label: '详情', type: 'info', size: 'mini', icon: 'el-icon-view', isShow: () => this.checkBtn('sys:ptmembergroupbenefit:info'), handle: (row) => this.openDetail(row) }
          ]
        }
      ],
      pagination: { limit: 10, offset: 1, total: 0 },
      // 统计卡（按当前页汇总）
      giftSum: 0,
      usedSum: 0,
      effectiveNum: 0,
      // 详情抽屉
      detailVisible: false,
      detailLoading: false,
      detail: null,
      flowList: []
    }
  },
  computed: {
    statCards () {
      return [
        { label: '权益总数（当前页）', value: this.tableData.length, unit: '条', color: '#409eff' },
        { label: '生效中', value: this.effectiveNum, unit: '条', color: '#67c23a' },
        { label: '累计赠送次数', value: this.giftSum, unit: '次', color: '#e6a23c' },
        { label: '累计核销次数', value: this.usedSum, unit: '次', color: '#f56c6c' }
      ]
    }
  },
  mounted () {
    this.getData()
  },
  methods: {
    // ===== 展示辅助 =====
    statusText (s) {
      return s === 1 ? '生效中' : s === 2 ? '已用完' : s === 3 ? '已过期' : s === 4 ? '已回收' : '-'
    },
    statusTheme (s) {
      return s === 1 ? 'success' : s === 2 ? 'info' : s === 3 ? 'warning' : s === 4 ? 'danger' : 'info'
    },
    flowTypeText (t) {
      return t === 1 ? '发放' : t === 2 ? '使用' : t === 3 ? '回收' : t === 4 ? '过期' : '-'
    },
    flowTypeTheme (t) {
      return t === 1 ? 'success' : t === 2 ? 'primary' : t === 3 ? 'danger' : t === 4 ? 'warning' : 'info'
    },
    bizTypeText (t) {
      return t === 1 ? '私教订单' : t === 2 ? '团课预约' : t === 3 ? '退款' : '-'
    },
    memberHtml (row) {
      var name = row.memberName || '-'
      var mobile = row.memberMobile ? ('<div style="color:#909399;font-size:12px;">' + row.memberMobile + '</div>') : ''
      return '<div>' + name + '</div>' + mobile
    },
    remainHtml (row) {
      var n = row.remainingCount
      if (n > 0) {
        return '<span style="color:#67c23a;font-weight:600;">' + n + '</span>'
      }
      return '<span style="color:#909399;">' + (n === 0 ? 0 : (n || '-')) + '</span>'
    },
    // ===== 列表 =====
    async getData () {
      this.tableLoading = true
      try {
        var params = {
          page: this.pagination.offset,
          limit: this.pagination.limit,
          memberKey: this.searchData.memberKey,
          sourceProductKey: this.searchData.sourceProductKey,
          status: this.searchData.status
        }
        var res = await this.apis.ptMemberGroupBenefit_list(params)
        var page = res.page || {}
        var list = page.list || []
        this.tableData = list
        this.pagination.total = page.totalCount || 0
        this.calcStat(list)
      } finally {
        this.tableLoading = false
      }
    },
    // 后端未返回 stat，按当前页数据前端汇总（仅统计本页，翻页会变化）
    calcStat (list) {
      var giftSum = 0
      var usedSum = 0
      var effectiveNum = 0
      list.forEach(function (r) {
        giftSum += Number(r.giftCount) || 0
        usedSum += Number(r.usedCount) || 0
        if (r.status === 1) effectiveNum++
      })
      this.giftSum = giftSum
      this.usedSum = usedSum
      this.effectiveNum = effectiveNum
    },
    page () {
      this.getData()
    },
    reset () {
      this.searchData = { memberKey: '', sourceProductKey: '', status: '' }
      this.pagination.offset = 1
      this.getData()
    },
    // ===== 详情 + 流水 =====
    openDetail (row) {
      this.detailVisible = true
      this.detailLoading = true
      this.detail = null
      this.flowList = []
      this.apis.ptMemberGroupBenefit_info({ id: row.id }).then((res) => {
        if (res && res.code === 0) {
          this.detail = res.benefit || null
          this.flowList = res.flowList || []
        }
      }).finally(() => {
        this.detailLoading = false
      })
    }
  }
}
</script>

<style scoped lang="scss">
.page-head {
  margin-bottom: 16px;
}
.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.page-desc {
  margin-top: 4px;
  font-size: 13px;
  color: #909399;
}
.stat-row {
  margin-bottom: 16px;
}
.stat-card {
  text-align: center;
  .stat-label {
    font-size: 13px;
    color: #909399;
  }
  .stat-value {
    font-size: 28px;
    font-weight: 700;
    margin: 8px 0 2px;
  }
  .stat-unit {
    font-size: 12px;
    color: #c0c4cc;
  }
}
.flow-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 20px 0 12px;
}
</style>
