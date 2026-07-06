<template>
  <div>
    <div class="page-head">
      <h2 class="page-title">分期管理</h2>
      <p class="page-sub">私教订单分期计划查看与催缴（计划/账单由订单与回调驱动，后台只读+催缴，不手改）</p>
    </div>
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

    <!-- 分期详情抽屉:上半区计划汇总 + 下半区账单明细表 -->
    <el-drawer title="分期详情" :visible.sync="detailVisible" size="720px" :append-to-body="true">
      <div class="detail-wrap" v-if="plan">
        <div class="detail-section-title">
          计划概况
          <el-tag v-if="overduePaused" type="danger" size="mini" style="margin-left:10px;">逾期已暂停新预约</el-tag>
        </div>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">分期单号</span>{{ plan.orderId || '—' }}</el-col>
          <el-col :span="12"><span class="lab">计划状态</span><span v-html="planStatusTag(plan.status)"></span></el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">会员</span>{{ plan.memberName || '—' }}</el-col>
          <el-col :span="12"><span class="lab">手机号</span>{{ plan.memberMobile || '—' }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">关联商品</span>{{ plan.productName || '—' }}</el-col>
          <el-col :span="12"><span class="lab">分期进度</span>{{ (plan.currentPeriod || 0) + '/' + (plan.installmentCount || 0) }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">总金额</span>{{ money(plan.totalAmount) }}</el-col>
          <el-col :span="12"><span class="lab">首付</span>{{ money(plan.downPaymentAmount) }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">已付</span><span class="text-success">{{ money(plan.paidAmount) }}</span></el-col>
          <el-col :span="12"><span class="lab">未付</span><span :class="{ 'text-danger': plan.unpaidAmount > 0 }">{{ money(plan.unpaidAmount) }}</span></el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">激活时间</span>{{ plan.activatedAt || '—' }}</el-col>
          <el-col :span="12"><span class="lab">创建时间</span>{{ plan.createdAt || '—' }}</el-col>
        </el-row>

        <div class="detail-section-title">账单明细</div>
        <el-table :data="bills" size="mini" border style="width:100%">
          <el-table-column label="期数" prop="periodNo" width="70" align="center" />
          <el-table-column label="应付" prop="dueAmount" align="center" :formatter="(row) => money(row.dueAmount)" />
          <el-table-column label="实付" prop="paidAmount" align="center" :formatter="(row) => money(row.paidAmount)" />
          <el-table-column label="应付日期" prop="dueDate" width="120" align="center" :formatter="(row) => row.dueDate || '—'" />
          <el-table-column label="支付时间" prop="paidTime" width="160" align="center" :formatter="(row) => row.paidTime || '—'" />
          <el-table-column label="状态" width="90" align="center">
            <template slot-scope="scope"><span v-html="billStatusTag(scope.row.status)"></span></template>
          </el-table-column>
        </el-table>
        <div v-if="!bills.length" class="detail-empty">暂无账单</div>
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
      searchData: {
        planNo: '',
        memberKeyword: '',
        status: '',
        dateRange: []
      },
      searchForm: [
        { type: "input", placeholder: "分期单号", prop: "planNo", width: 200 },
        { type: "input", placeholder: "会员姓名/手机号", prop: "memberKeyword", width: 200 },
        { type: "select", placeholder: "分期状态", prop: "status", width: 160, options: [
          { value: 1, label: '进行中' },
          { value: 2, label: '已结清' },
          { value: 3, label: '已逾期' },
          { value: 4, label: '已关闭' }
        ] },
        { type: "daterange", placeholder: "创建时间", prop: "dateRange", width: 260 },
      ],
      searchHandle: [
        // 搜索时重置回第 1 页,避免翻页后再加筛选停在越界空页
        { label: "搜索", type: "primary", handle: e => { this.pagination.offset = 1; this.getData(); } },
      ],
      tableData: [],
      tableCols: [
        { label: "分期单号", prop: "orderId", width: 160 },
        { label: "会员", prop: "memberName", formatter: e => (e.memberName || '') + ' ' + (e.memberMobile || '') },
        { label: "关联商品", prop: "productName" },
        { label: "总金额", prop: "totalAmount", width: 100, formatter: e => this.money(e.totalAmount) },
        { label: "首付", prop: "downPaymentAmount", width: 100, formatter: e => this.money(e.downPaymentAmount) },
        { label: "已付", prop: "paidAmount", width: 100, type: "html", html: e => '<span style="color:#67C23A">' + this.money(e.paidAmount) + '</span>' },
        { label: "未付", prop: "unpaidAmount", width: 100, type: "html", html: e => e.unpaidAmount > 0 ? '<span style="color:#F56C6C">' + this.money(e.unpaidAmount) + '</span>' : this.money(e.unpaidAmount) },
        { label: "分期进度", prop: "currentPeriod", width: 90, formatter: e => (e.currentPeriod || 0) + '/' + (e.installmentCount || 0) },
        { label: "分期状态", prop: "status", width: 90, type: "html", html: e => this.planStatusTag(e.status) },
        { label: "创建时间", prop: "createdAt", width: 160, formatter: e => e.createdAt || '—' },
        {
          label: "操作",
          type: "button",
          width: 120,
          fixed: "right",
          btnList: [
            { label: "详情", type: "primary", size: "mini", isShow: () => this.checkBtn('sys:installment:info'), handle: (row) => this.openDetail(row) },
            {
              label: "催缴",
              type: "warning",
              size: "mini",
              // 仅 已逾期(3) 行显示
              isShow: (row) => row.status == 3 && this.checkBtn('sys:installment:remind'),
              handle: (row) => this.remind(row)
            },
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      detailVisible: false,
      plan: null,
      bills: [],
    };
  },
  mounted() {
    this.getData();
  },
  methods: {
    money(v) {
      return (v === null || v === undefined || v === '') ? '¥0.00' : '¥' + v;
    },
    // 分期计划状态:1进行中蓝 / 2已结清绿 / 3已逾期红 / 4已关闭灰
    planStatusTag(status) {
      var map = {
        1: ['进行中', '#ecf5ff', '#409EFF', '#d9ecff'],
        2: ['已结清', '#f0f9eb', '#67C23A', '#e1f3d8'],
        3: ['已逾期', '#fef0f0', '#F56C6C', '#fde2e2'],
        4: ['已关闭', '#f4f4f5', '#909399', '#e9e9eb']
      };
      var s = map[status] || ['未知', '#f4f4f5', '#909399', '#e9e9eb'];
      return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:' + s[1] + ';color:' + s[2] + ';border:1px solid ' + s[3] + '">' + s[0] + '</span>';
    },
    // 账单状态:0待支付灰 / 1已支付绿 / 2已逾期红 / 3已关闭灰
    billStatusTag(status) {
      var map = {
        0: ['待支付', '#f4f4f5', '#909399', '#e9e9eb'],
        1: ['已支付', '#f0f9eb', '#67C23A', '#e1f3d8'],
        2: ['已逾期', '#fef0f0', '#F56C6C', '#fde2e2'],
        3: ['已关闭', '#f4f4f5', '#909399', '#e9e9eb']
      };
      var s = map[status] || ['—', '#f4f4f5', '#909399', '#e9e9eb'];
      return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:' + s[1] + ';color:' + s[2] + ';border:1px solid ' + s[3] + '">' + s[0] + '</span>';
    },
    async getData() {
      this.tableLoading = true;
      try {
        // daterange 绑定为 [起, 止] 数组(清空为 null),拆成后端要的 startTime/endTime
        var dr = this.searchData.dateRange || [];
        var res = await this.apis.installment_list({
          page: this.pagination.offset,
          limit: this.pagination.limit,
          planNo: this.searchData.planNo,
          memberKeyword: this.searchData.memberKeyword,
          status: this.searchData.status,
          startTime: dr[0] || '',
          endTime: dr[1] || ''
        });
        var list = (res.page && res.page.list) || [];
        this.tableData = list;
        this.pagination.total = res.page ? res.page.totalCount : 0;
      } finally {
        this.tableLoading = false;
      }
    },
    page() {
      this.getData();
    },
    async openDetail(row) {
      this.plan = null;
      this.bills = [];
      this.detailVisible = true;
      var res = await this.apis.installment_info({ id: row.id });
      this.plan = (res && res.plan) || null;
      this.bills = (res && res.bills) || [];
    },
    remind(row) {
      this.$confirm('确定向该会员发送分期逾期催缴通知吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.installment_remind({ id: row.id });
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('催缴通知已发送');
        }
      }).catch(() => { /* 失败已由响应拦截器弹错误提示 */ });
    },
  },
  computed: {
    // 逾期且规则暂停约课时,详情概况区提示"逾期已暂停新预约"
    overduePaused() {
      return this.plan && this.plan.status == 3;
    }
  },
};
</script>

<style scoped lang="scss">
.page-head { margin-bottom: 12px; }
.page-title { margin: 0; font-size: 20px; }
.page-sub { margin: 4px 0 0; color: #909399; font-size: 13px; }
.detail-wrap { padding: 0 20px 20px; }
.detail-section-title { font-weight: bold; color: #303133; margin: 18px 0 10px; padding-left: 8px; border-left: 3px solid #409EFF; }
.detail-row { line-height: 32px; color: #606266; }
.detail-row .lab { display: inline-block; color: #909399; margin-right: 8px; min-width: 66px; }
.detail-empty { color: #909399; padding: 6px 8px; }
.text-danger { color: #F56C6C; }
.text-success { color: #67C23A; }
</style>
