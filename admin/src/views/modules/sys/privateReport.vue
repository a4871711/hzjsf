<template>
  <div class="pr-wrap">
    <!-- 顶部公共筛选 -->
    <el-form :inline="true" size="small" class="pr-filter">
      <el-form-item label="统计日期" required>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="yyyy-MM-dd"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="onDateChange">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="门店">
        <el-select v-model="filter.storeId" filterable clearable placeholder="全部门店" style="width:160px">
          <el-option v-for="op in storeOptions" :label="op.label" :value="op.value" :key="op.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="教练">
        <el-input v-model="filter.coachName" placeholder="教练姓名" style="width:140px"></el-input>
      </el-form-item>
      <el-form-item label="课程">
        <el-input v-model="filter.productName" placeholder="课程/商品" style="width:140px"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="query">查询</el-button>
      </el-form-item>
    </el-form>

    <!-- 口径说明 -->
    <div class="pr-caliber">
      口径说明：收入 = 净实收 ÷ 订单总课时 × 完成课时；成本 = 教练课时费；预约完成即核销（按完成日期统计）。
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="pr-stat">
      <el-col :span="6">
        <div class="pr-card">
          <div class="pr-num">{{ summary.totalLessons || 0 }}</div>
          <div class="pr-label">核销课时(节)</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="pr-card">
          <div class="pr-num">¥{{ money(summary.income) }}</div>
          <div class="pr-label">收入金额</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="pr-card">
          <div class="pr-num">¥{{ money(summary.cost) }}</div>
          <div class="pr-label">课时成本</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="pr-card pr-card-profit">
          <div class="pr-num">¥{{ money(summary.grossProfit) }}</div>
          <div class="pr-label">毛利 / 毛利率 {{ rate(summary.grossRate) }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 6 个报表 Tab -->
    <el-tabs v-model="activeTab" @tab-click="onTab">
      <el-tab-pane label="概览" name="summary">
        <el-descriptions :column="2" border size="medium">
          <el-descriptions-item label="核销课时">{{ summary.totalLessons || 0 }} 节</el-descriptions-item>
          <el-descriptions-item label="收入金额">¥{{ money(summary.income) }}</el-descriptions-item>
          <el-descriptions-item label="课时成本">¥{{ money(summary.cost) }}</el-descriptions-item>
          <el-descriptions-item label="毛利">
            <span class="pr-green">¥{{ money(summary.grossProfit) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="毛利率">
            <span class="pr-green">{{ rate(summary.grossRate) }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>

      <el-tab-pane label="门店报表" name="store">
        <r-table :isSelection="false" :isHandle="false" :isPagination="true"
          :tableData="tableData" :tableCols="storeCols" :tablePage="pagination" :loading="tableLoading" @refresh="pageTable" />
      </el-tab-pane>

      <el-tab-pane label="教练报表" name="coach">
        <r-table :isSelection="false" :isHandle="false" :isPagination="true"
          :tableData="tableData" :tableCols="coachCols" :tablePage="pagination" :loading="tableLoading" @refresh="pageTable" />
      </el-tab-pane>

      <el-tab-pane label="课程报表" name="course">
        <r-table :isSelection="false" :isHandle="false" :isPagination="true"
          :tableData="tableData" :tableCols="courseCols" :tablePage="pagination" :loading="tableLoading" @refresh="pageTable" />
      </el-tab-pane>

      <el-tab-pane label="明细报表" name="detail">
        <r-table :isSelection="false" :isHandle="false" :isPagination="true"
          :tableData="tableData" :tableCols="detailCols" :tablePage="pagination" :loading="tableLoading" @refresh="pageTable" />
      </el-tab-pane>

      <el-tab-pane label="异常数据" name="abnormal">
        <div class="pr-caliber">异常数据：完成核销行中缺门店 / 缺教练 / 未配课时费(成本按0)，不计入正式汇总。</div>
        <r-table :isSelection="false" :isHandle="false" :isPagination="true"
          :tableData="tableData" :tableCols="abnormalCols" :tablePage="pagination" :loading="tableLoading" @refresh="pageTable" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
export default {
  data () {
    return {
      activeTab: 'summary',
      storeOptions: [],
      dateRange: [],
      filter: {
        storeId: '',
        coachName: '',
        productName: ''
      },
      summary: {},
      tableLoading: false,
      tableData: [],
      pagination: { limit: 10, page: 1, offset: 1, total: 0 },
      storeCols: [
        { label: '门店', prop: 'storeName' },
        { label: '完成课时', prop: 'finishLessons' },
        { label: '收入', prop: 'income', formatter: e => '¥' + this.money(e.income) },
        { label: '成本', prop: 'cost', formatter: e => '¥' + this.money(e.cost) },
        { label: '毛利', prop: 'grossProfit', itemClass: () => 'pr-green', formatter: e => '¥' + this.money(e.grossProfit) },
        { label: '毛利率', prop: 'grossRate', itemClass: () => 'pr-green', formatter: e => this.rate(e.grossRate) }
      ],
      coachCols: [
        { label: '教练姓名', prop: 'coachName' },
        { label: '所属门店', prop: 'storeName' },
        { label: '完成课时', prop: 'finishLessons' },
        { label: '收入', prop: 'income', formatter: e => '¥' + this.money(e.income) },
        { label: '成本', prop: 'cost', formatter: e => '¥' + this.money(e.cost) },
        { label: '毛利', prop: 'grossProfit', itemClass: () => 'pr-green', formatter: e => '¥' + this.money(e.grossProfit) },
        { label: '毛利率', prop: 'grossRate', itemClass: () => 'pr-green', formatter: e => this.rate(e.grossRate) }
      ],
      courseCols: [
        { label: '课程名', prop: 'productName' },
        { label: '完成课时', prop: 'finishLessons' },
        { label: '收入', prop: 'income', formatter: e => '¥' + this.money(e.income) },
        { label: '成本', prop: 'cost', formatter: e => '¥' + this.money(e.cost) },
        { label: '毛利', prop: 'grossProfit', itemClass: () => 'pr-green', formatter: e => '¥' + this.money(e.grossProfit) },
        { label: '毛利率', prop: 'grossRate', itemClass: () => 'pr-green', formatter: e => this.rate(e.grossRate) }
      ],
      detailCols: [
        { label: '完成日期', prop: 'finishDate', width: 110 },
        { label: '门店', prop: 'storeName' },
        { label: '教练', prop: 'coachName' },
        { label: '课程', prop: 'productName' },
        { label: '用户', prop: 'memberInfo' },
        { label: '订单号', prop: 'orderNo', width: 160 },
        { label: '总课时', prop: 'totalLessons', width: 80 },
        { label: '本次完成', prop: 'finishLessons', width: 90 },
        { label: '单次收入', prop: 'unitIncome', formatter: e => '¥' + this.money(e.unitIncome) },
        { label: '单次成本', prop: 'unitCost', formatter: e => '¥' + this.money(e.unitCost) },
        { label: '单次毛利', prop: 'unitGross', itemClass: () => 'pr-green', formatter: e => '¥' + this.money(e.unitGross) }
      ],
      abnormalCols: [
        { label: '完成日期', prop: 'finishDate', width: 120 },
        { label: '订单号', prop: 'orderNo', width: 180 },
        { label: '课程', prop: 'productName' },
        { label: '门店ID', prop: 'storeId', width: 90 },
        { label: '教练ID', prop: 'coachId', width: 90 },
        { label: '异常原因', prop: 'abnormalReason', itemClass: () => 'pr-danger' }
      ]
    }
  },
  mounted () {
    this.initDate()
    this.getStoreList()
    this.query()
  },
  methods: {
    money (v) {
      var n = Number(v)
      if (isNaN(n)) return '0.00'
      return n.toFixed(2)
    },
    rate (v) {
      if (v === null || v === undefined || v === '') return '0%'
      var n = Number(v)
      if (isNaN(n)) return '0%'
      return n.toFixed(2) + '%'
    },
    initDate () {
      var now = new Date()
      var start = new Date(now.getTime() - 29 * 24 * 3600 * 1000)
      this.dateRange = [this.parseTime(start, '{y}-{m}-{d}'), this.parseTime(now, '{y}-{m}-{d}')]
    },
    onDateChange () {
      // 日期改变仅更新绑定，点查询才生效
    },
    baseParams () {
      var range = this.dateRange || []
      return {
        beginDate: range[0] || '',
        endDate: range[1] || '',
        storeId: this.filter.storeId,
        coachName: this.filter.coachName,
        productName: this.filter.productName
      }
    },
    async getStoreList () {
      var res = await this.apis.store_list({ page: 1, limit: 999 })
      var list = (res.page && res.page.list) || []
      this.storeOptions = list.map(function (item) { return { value: item.storeAddrId, label: item.storeName } })
    },
    query () {
      var range = this.dateRange || []
      if (!range[0] || !range[1]) {
        return this.$message.warning('请选择统计日期')
      }
      this.pagination.offset = 1
      this.pagination.page = 1
      this.getSummary()
      this.loadTab()
    },
    async getSummary () {
      try {
        var res = await this.apis.privateReport_summary(this.baseParams())
        this.summary = (res && res.summary) || {}
      } catch (e) { this.summary = {} }
    },
    onTab () {
      this.pagination.offset = 1
      this.pagination.page = 1
      this.loadTab()
    },
    pageTable (p) {
      if (p) this.pagination.offset = p.page
      this.loadTab()
    },
    loadTab () {
      if (this.activeTab === 'summary') {
        this.tableData = []
        return
      }
      var range = this.dateRange || []
      if (!range[0] || !range[1]) return
      var apiMap = {
        store: 'privateReport_storeReport',
        coach: 'privateReport_coachReport',
        course: 'privateReport_courseReport',
        detail: 'privateReport_detailReport',
        abnormal: 'privateReport_abnormalList'
      }
      var fn = apiMap[this.activeTab]
      if (!fn) return
      var params = Object.assign({}, this.baseParams(), {
        page: this.pagination.offset,
        limit: this.pagination.limit
      })
      this.tableLoading = true
      this.apis[fn](params).then((res) => {
        this.tableData = (res.page && res.page.list) || []
        this.pagination.total = res.page ? res.page.totalCount : 0
      }).catch(() => {
        this.tableData = []
        this.pagination.total = 0
      }).finally(() => {
        this.tableLoading = false
      })
    }
  }
}
</script>

<style scoped lang="scss">
.pr-wrap {
  padding: 6px 0;
}
.pr-filter {
  margin-bottom: 6px;
}
.pr-caliber {
  margin-bottom: 16px;
  padding: 8px 14px;
  background: #f4f4f5;
  border-radius: 4px;
  color: #909399;
  font-size: 13px;
}
.pr-stat {
  margin-bottom: 20px;
}
.pr-card {
  padding: 18px 20px;
  border-radius: 6px;
  background: #f5f7fa;
  .pr-num {
    font-size: 26px;
    font-weight: bold;
    color: #303133;
    line-height: 1.2;
  }
  .pr-label {
    margin-top: 6px;
    font-size: 13px;
    color: #909399;
  }
}
.pr-card-profit {
  background: #f0f9eb;
  .pr-num {
    color: #67c23a;
  }
  .pr-label {
    color: #67c23a;
  }
}
</style>
<style>
.pr-green { color: #67c23a; font-weight: bold; }
.pr-danger { color: #f56c6c; }
</style>
