<template>
  <div>
    <div class="page-head">
      <h2 class="page-title">会员私教权益</h2>
      <p class="page-sub">会员已购私教课时账本查看（纯只读，课时变更仅由下单/预约/退款链路驱动）</p>
    </div>

    <!-- 顶部统计卡:取 list 返回的 stat -->
    <el-row :gutter="12" class="stat-row">
      <el-col :span="5">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-num" style="color:#67C23A">{{ stat.activeCount || 0 }}</div>
          <div class="stat-label">生效中权益</div>
        </el-card>
      </el-col>
      <el-col :span="5">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-num" style="color:#909399">{{ stat.usedUpCount || 0 }}</div>
          <div class="stat-label">已用完</div>
        </el-card>
      </el-col>
      <el-col :span="5">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-num" style="color:#E6A23C">{{ stat.expiredCount || 0 }}</div>
          <div class="stat-label">已过期</div>
        </el-card>
      </el-col>
      <el-col :span="5">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-num" style="color:#F56C6C">{{ stat.refundedCount || 0 }}</div>
          <div class="stat-label">已退款</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-num" style="color:#409EFF">{{ stat.totalRemainingLessons || 0 }}</div>
          <div class="stat-label">总剩余课时</div>
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
      @refresh="page()" />

    <!-- 权益详情抽屉:课时四态 + 来源订单 -->
    <el-drawer title="权益详情" :visible.sync="detailVisible" size="560px" :append-to-body="true">
      <div class="detail-wrap" v-if="detail">
        <div class="detail-section-title">权益信息</div>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">权益编号</span>{{ detail.benefitNo || '—' }}</el-col>
          <el-col :span="12"><span class="lab">权益状态</span><span v-html="statusTag(detail.status)"></span></el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">会员</span>{{ detail.memberName || '—' }}</el-col>
          <el-col :span="12"><span class="lab">手机号</span>{{ detail.memberMobile || '—' }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">私教商品</span>{{ detail.productName || '—' }}</el-col>
          <el-col :span="12"><span class="lab">所属门店</span>{{ detail.storeName || '—' }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="24"><span class="lab">来源订单</span>{{ detail.orderNo || '—' }}</el-col>
        </el-row>

        <div class="detail-section-title">课时账本（总 = 已用 + 冻结 + 剩余）</div>
        <el-row class="detail-row lesson-row">
          <el-col :span="6"><div class="lesson-num">{{ detail.totalLessons }}</div><div class="lesson-lab">总课时</div></el-col>
          <el-col :span="6"><div class="lesson-num" style="color:#909399">{{ detail.usedLessons }}</div><div class="lesson-lab">已用</div></el-col>
          <el-col :span="6"><div class="lesson-num" style="color:#E6A23C">{{ detail.frozenLessons }}</div><div class="lesson-lab">冻结</div></el-col>
          <el-col :span="6"><div class="lesson-num" style="color:#67C23A">{{ detail.remainingLessons }}</div><div class="lesson-lab">剩余</div></el-col>
        </el-row>

        <div class="detail-section-title">时间</div>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">生效时间</span>{{ detail.effectiveAt || '—' }}</el-col>
          <el-col :span="12"><span class="lab">到期时间</span>{{ detail.expireAt || '长期有效' }}</el-col>
        </el-row>
      </div>
    </el-drawer>
  </div>
</template>

<script>
export default {
  components: {},
  data() {
    return {
      tableLoading: false,
      stat: {},
      searchData: {
        memberKeyword: '',
        productKeyword: '',
        storeId: '',
        status: ''
      },
      searchForm: [
        { type: "input", placeholder: "会员姓名/手机号", prop: "memberKeyword", width: 200 },
        { type: "input", placeholder: "私教商品", prop: "productKeyword", width: 180 },
        { type: "select", placeholder: "所属门店", prop: "storeId", width: 180, options: [] },
        { type: "select", placeholder: "权益状态", prop: "status", width: 160, options: [
          { value: 1, label: '生效中' },
          { value: 2, label: '已用完' },
          { value: 3, label: '已过期' },
          { value: 4, label: '已退款' }
        ] },
      ],
      searchHandle: [
        { label: "搜索", type: "primary", handle: e => { this.pagination.offset = 1; this.getData(); } },
      ],
      tableData: [],
      tableCols: [
        { label: "权益编号", prop: "benefitNo", width: 180 },
        { label: "会员", prop: "memberName", formatter: e => (e.memberName || '') + ' ' + (e.memberMobile || '') },
        { label: "来源订单", prop: "orderNo", width: 170 },
        { label: "私教商品", prop: "productName" },
        { label: "所属门店", prop: "storeName" },
        { label: "总课时", prop: "totalLessons", width: 80 },
        { label: "已用课时", prop: "usedLessons", width: 80 },
        { label: "冻结课时", prop: "frozenLessons", width: 90, type: "html", html: e => e.frozenLessons > 0 ? '<span style="color:#E6A23C">' + e.frozenLessons + '</span>' : String(e.frozenLessons) },
        { label: "剩余课时", prop: "remainingLessons", width: 90, type: "html", html: e => e.remainingLessons > 0 ? '<span style="color:#67C23A">' + e.remainingLessons + '</span>' : String(e.remainingLessons) },
        { label: "生效时间", prop: "effectiveAt", width: 160, formatter: e => e.effectiveAt || '—' },
        { label: "到期时间", prop: "expireAt", width: 160, formatter: e => e.expireAt || '长期' },
        { label: "权益状态", prop: "status", width: 90, type: "html", html: e => this.statusTag(e.status) },
        {
          label: "操作",
          type: "button",
          width: 90,
          fixed: "right",
          btnList: [
            { label: "详情", type: "primary", size: "mini", handle: (row) => this.openDetail(row) },
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      detailVisible: false,
      detail: null,
    };
  },
  mounted() {
    this.getData();
    this.getStoreList();
  },
  methods: {
    // 权益状态:1生效中绿 / 2已用完灰 / 3已过期橙 / 4已退款红
    statusTag(status) {
      var map = {
        1: ['生效中', '#f0f9eb', '#67C23A', '#e1f3d8'],
        2: ['已用完', '#f4f4f5', '#909399', '#e9e9eb'],
        3: ['已过期', '#fdf6ec', '#E6A23C', '#faecd8'],
        4: ['已退款', '#fef0f0', '#F56C6C', '#fde2e2']
      };
      var s = map[status] || ['未知', '#f4f4f5', '#909399', '#e9e9eb'];
      return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:' + s[1] + ';color:' + s[2] + ';border:1px solid ' + s[3] + '">' + s[0] + '</span>';
    },
    async getData() {
      this.tableLoading = true;
      try {
        var res = await this.apis.memberBenefit_list({
          page: this.pagination.offset,
          limit: this.pagination.limit,
          memberKeyword: this.searchData.memberKeyword,
          productKeyword: this.searchData.productKeyword,
          storeId: this.searchData.storeId,
          status: this.searchData.status
        });
        var list = (res.page && res.page.list) || [];
        this.tableData = list;
        this.pagination.total = res.page ? res.page.totalCount : 0;
        // 统计卡:stat 与 page 同级(controller put("stat", ...))
        this.stat = res.stat || {};
      } finally {
        this.tableLoading = false;
      }
    },
    page() {
      this.getData();
    },
    async getStoreList() {
      var res = await this.apis.store_list({ page: 1, limit: 999 });
      var list = (res.page && res.page.list) || [];
      var opts = list.map(function (item) { return { value: item.storeAddrId, label: item.storeName }; });
      this.searchForm[2].options = opts;
    },
    async openDetail(row) {
      this.detail = null;
      this.detailVisible = true;
      var res = await this.apis.memberBenefit_info({ id: row.id });
      this.detail = (res && res.entity) || null;
    },
  },
};
</script>

<style scoped lang="scss">
.page-head { margin-bottom: 12px; }
.page-title { margin: 0; font-size: 20px; }
.page-sub { margin: 4px 0 0; color: #909399; font-size: 13px; }
.stat-row { margin-bottom: 16px; }
.stat-card { text-align: center; }
.stat-num { font-size: 26px; font-weight: bold; line-height: 1.2; }
.stat-label { color: #909399; font-size: 13px; margin-top: 6px; }
.detail-wrap { padding: 0 20px 20px; }
.detail-section-title { font-weight: bold; color: #303133; margin: 18px 0 10px; padding-left: 8px; border-left: 3px solid #409EFF; }
.detail-row { line-height: 32px; color: #606266; }
.detail-row .lab { display: inline-block; color: #909399; margin-right: 8px; min-width: 66px; }
.lesson-row { text-align: center; background: #f5f7fa; border-radius: 4px; padding: 12px 0; }
.lesson-num { font-size: 22px; font-weight: bold; }
.lesson-lab { color: #909399; font-size: 12px; margin-top: 4px; }
</style>
