<template>
  <div>
    <div class="page-head">
      <h2 class="page-title">会员储值</h2>
      <p class="page-sub">私教专用储值账户（充值/冻结/解冻/查流水）；账户由会员首充自动创建，后台不建/删账户</p>
    </div>

    <el-tabs v-model="activeTab" @tab-click="onTabClick">
      <!-- Tab1 储值账户 -->
      <el-tab-pane label="储值账户" name="account">
        <!-- 统计卡:总余额 / 总充值 / 总消费(res.stat) -->
        <el-row :gutter="16" class="stat-row">
          <el-col :span="8">
            <div class="stat-card">
              <div class="stat-label">总余额</div>
              <div class="stat-value">{{ money(stat.totalBalance) }}</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="stat-card">
              <div class="stat-label">总充值</div>
              <div class="stat-value text-success">{{ money(stat.totalRecharge) }}</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="stat-card">
              <div class="stat-label">总消费</div>
              <div class="stat-value text-danger">{{ money(stat.totalConsume) }}</div>
            </div>
          </el-col>
        </el-row>

        <r-search ref="accSearch" :searchData="accSearchData" :searchForm="accSearchForm" :searchHandle="accSearchHandle" />
        <r-table
          :isSelection="false"
          :isHandle="false"
          :isPagination="true"
          :tableData="accTableData"
          :tableCols="accTableCols"
          :tablePage="accPagination"
          :loading="accLoading"
          @refresh="accPage()" />
      </el-tab-pane>

      <!-- Tab2 储值流水 -->
      <el-tab-pane label="储值流水" name="flow">
        <r-search ref="flowSearch" :searchData="flowSearchData" :searchForm="flowSearchForm" :searchHandle="flowSearchHandle" />
        <r-table
          :isSelection="false"
          :isHandle="false"
          :isPagination="true"
          :tableData="flowTableData"
          :tableCols="flowTableCols"
          :tablePage="flowPagination"
          :loading="flowLoading"
          @refresh="flowPage()" />
      </el-tab-pane>
    </el-tabs>

    <!-- 充值弹窗:金额 + 备注 -->
    <el-dialog title="账户充值" :visible.sync="rechargeVisible" width="480px" :append-to-body="true">
      <div v-if="rechargeRow" class="recharge-tip">
        <p>会员：<b>{{ (rechargeRow.memberName || '—') + ' ' + (rechargeRow.memberMobile || '') }}</b></p>
        <p>当前余额：<b>{{ money(rechargeRow.balanceAmount) }}</b></p>
      </div>
      <el-form :model="rechargeForm" label-width="90px" size="medium">
        <el-form-item label="充值金额" required>
          <el-input v-model="rechargeForm.amount" placeholder="请输入充值金额（元）" style="width:280px">
            <template slot="prepend">¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="rechargeForm.remark" type="textarea" :rows="3" placeholder="线下收款补录备注" style="width:280px"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="rechargeVisible = false">取消</el-button>
        <el-button type="primary" :loading="rechargeLoading" @click="submitRecharge">确认充值</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  components: {},
  data() {
    return {
      activeTab: 'account',
      stat: { totalBalance: '', totalRecharge: '', totalConsume: '' },

      // ===== Tab1 储值账户 =====
      accLoading: false,
      accSearchData: {
        keyword: '',
        status: ''
      },
      accSearchForm: [
        { type: "input", placeholder: "会员姓名/手机号", prop: "keyword", width: 200 },
        { type: "select", placeholder: "账户状态", prop: "status", width: 160, options: [
          { value: 1, label: '正常' },
          { value: 2, label: '冻结' }
        ] },
      ],
      accSearchHandle: [
        { label: "搜索", type: "primary", handle: e => { this.accPagination.offset = 1; this.getAccData(); } },
      ],
      accTableData: [],
      accTableCols: [
        { label: "会员", prop: "memberName", formatter: e => (e.memberName || '') + ' ' + (e.memberMobile || '') },
        { label: "储值余额", prop: "balanceAmount", width: 120, type: "html", html: e => '<strong style="color:#67C23A">' + this.money(e.balanceAmount) + '</strong>' },
        { label: "累计充值", prop: "totalRechargeAmount", width: 110, formatter: e => this.money(e.totalRechargeAmount) },
        { label: "累计消费", prop: "totalConsumeAmount", width: 110, formatter: e => this.money(e.totalConsumeAmount) },
        { label: "状态", prop: "status", width: 90, type: "html", html: e => this.walletStatusTag(e.status) },
        { label: "更新时间", prop: "updatedAt", width: 160, formatter: e => e.updatedAt || '—' },
        {
          label: "操作",
          type: "button",
          width: 240,
          fixed: "right",
          btnList: [
            { label: "充值", type: "success", size: "mini", isShow: () => this.checkBtn('sys:wallet:recharge'), handle: (row) => this.openRecharge(row) },
            { label: "流水", type: "primary", size: "mini", isShow: () => this.checkBtn('sys:wallet:flowlist'), handle: (row) => this.gotoFlow(row) },
            // 正常账户可冻结
            { label: "冻结", type: "warning", size: "mini", isShow: (row) => row.status == 1 && this.checkBtn('sys:wallet:freeze'), handle: (row) => this.freeze(row) },
            // 冻结账户可解冻
            { label: "解冻", type: "success", size: "mini", isShow: (row) => row.status == 2 && this.checkBtn('sys:wallet:unfreeze'), handle: (row) => this.unfreeze(row) },
          ]
        },
      ],
      accPagination: { limit: 10, offset: 1, total: 1 },

      // ===== Tab2 储值流水 =====
      flowLoaded: false,
      flowLoading: false,
      flowSearchData: {
        memberId: '',
        flowType: ''
      },
      flowSearchForm: [
        { type: "input", placeholder: "会员ID", prop: "memberId", width: 160 },
        { type: "select", placeholder: "流水类型", prop: "flowType", width: 160, options: [
          { value: 1, label: '充值' },
          { value: 2, label: '消费' },
          { value: 3, label: '退款' },
          { value: 4, label: '冲正' }
        ] },
      ],
      flowSearchHandle: [
        { label: "搜索", type: "primary", handle: e => { this.flowPagination.offset = 1; this.getFlowData(); } },
      ],
      flowTableData: [],
      flowTableCols: [
        { label: "流水编号", prop: "id", width: 90 },
        { label: "会员", prop: "memberName", formatter: e => (e.memberName || '') + ' ' + (e.memberMobile || '') },
        { label: "流水类型", prop: "flowType", width: 90, type: "html", html: e => this.flowTypeTag(e.flowType) },
        { label: "变动金额", prop: "changeAmount", width: 110, type: "html", html: e => this.changeAmountHtml(e.changeAmount) },
        { label: "变动前", prop: "beforeBalance", width: 100, formatter: e => this.money(e.beforeBalance) },
        { label: "变动后", prop: "afterBalance", width: 100, type: "html", html: e => '<strong>' + this.money(e.afterBalance) + '</strong>' },
        { label: "关联业务", prop: "bizId", formatter: e => this.bizText(e) },
        { label: "备注", prop: "remark", formatter: e => e.remark || '—' },
        { label: "创建时间", prop: "createdAt", width: 160, formatter: e => e.createdAt || '—' },
      ],
      flowPagination: { limit: 10, offset: 1, total: 1 },

      // ===== 充值弹窗 =====
      rechargeVisible: false,
      rechargeRow: null,
      rechargeLoading: false,
      rechargeForm: { amount: '', remark: '' },
    };
  },
  mounted() {
    this.getAccData();
  },
  methods: {
    money(v) {
      return (v === null || v === undefined || v === '') ? '¥0.00' : '¥' + v;
    },
    // 账户状态:1正常绿 / 2冻结灰
    walletStatusTag(status) {
      var map = {
        1: ['正常', '#f0f9eb', '#67C23A', '#e1f3d8'],
        2: ['冻结', '#f4f4f5', '#909399', '#e9e9eb']
      };
      var s = map[status] || ['—', '#f4f4f5', '#909399', '#e9e9eb'];
      return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:' + s[1] + ';color:' + s[2] + ';border:1px solid ' + s[3] + '">' + s[0] + '</span>';
    },
    // 流水类型:1充值绿 / 2消费红 / 3退款橙 / 4冲正灰
    flowTypeTag(flowType) {
      var map = {
        1: ['充值', '#f0f9eb', '#67C23A', '#e1f3d8'],
        2: ['消费', '#fef0f0', '#F56C6C', '#fde2e2'],
        3: ['退款', '#fdf6ec', '#E6A23C', '#faecd8'],
        4: ['冲正', '#f4f4f5', '#909399', '#e9e9eb']
      };
      var s = map[flowType] || ['—', '#f4f4f5', '#909399', '#e9e9eb'];
      return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:' + s[1] + ';color:' + s[2] + ';border:1px solid ' + s[3] + '">' + s[0] + '</span>';
    },
    // 变动金额:带正负,正绿负红
    changeAmountHtml(v) {
      var n = Number(v);
      if (isNaN(n)) return '—';
      if (n >= 0) return '<span style="color:#67C23A">+¥' + v + '</span>';
      return '<span style="color:#F56C6C">-¥' + Math.abs(n).toFixed(2) + '</span>';
    },
    // 关联业务文案:biz_type 1订单/2退款单/3人工调整 + biz_id
    bizText(e) {
      var map = { 1: '订单', 2: '退款单', 3: '人工调整' };
      var t = map[e.bizType];
      if (!t) return '—';
      return e.bizId ? (t + ' #' + e.bizId) : t;
    },
    onTabClick(tab) {
      // 首次切到流水 Tab 时才拉取(懒加载)
      if (tab.name === 'flow' && !this.flowLoaded) {
        this.getFlowData();
      }
    },
    // ===== Tab1 账户 =====
    async getAccData() {
      this.accLoading = true;
      try {
        var res = await this.apis.wallet_list({
          page: this.accPagination.offset,
          limit: this.accPagination.limit,
          keyword: this.accSearchData.keyword,
          status: this.accSearchData.status
        });
        var list = (res.page && res.page.list) || [];
        this.accTableData = list;
        this.accPagination.total = res.page ? res.page.totalCount : 0;
        // 统计卡同级 res.stat:总余额/总充值/总消费
        if (res.stat) {
          this.stat = {
            totalBalance: res.stat.totalBalance,
            totalRecharge: res.stat.totalRecharge,
            totalConsume: res.stat.totalConsume
          };
        }
      } finally {
        this.accLoading = false;
      }
    },
    accPage() {
      this.getAccData();
    },
    // ===== Tab2 流水 =====
    async getFlowData() {
      this.flowLoading = true;
      this.flowLoaded = true;
      try {
        var res = await this.apis.wallet_flowList({
          page: this.flowPagination.offset,
          limit: this.flowPagination.limit,
          memberId: this.flowSearchData.memberId,
          flowType: this.flowSearchData.flowType
        });
        var list = (res.page && res.page.list) || [];
        this.flowTableData = list;
        this.flowPagination.total = res.page ? res.page.totalCount : 0;
      } finally {
        this.flowLoading = false;
      }
    },
    flowPage() {
      this.getFlowData();
    },
    // 账户行「流水」:切到流水 Tab 并按 memberId 过滤
    gotoFlow(row) {
      this.flowSearchData.memberId = row.memberId;
      this.flowSearchData.flowType = '';
      this.flowPagination.offset = 1;
      this.activeTab = 'flow';
      this.getFlowData();
    },
    // ===== 充值 =====
    openRecharge(row) {
      this.rechargeRow = row;
      this.rechargeForm = { amount: '', remark: '' };
      this.rechargeVisible = true;
    },
    submitRecharge() {
      var amt = Number(this.rechargeForm.amount);
      if (!this.rechargeForm.amount || isNaN(amt) || amt <= 0) {
        this.$message.warning('请输入正确的充值金额');
        return;
      }
      var data = {
        memberId: this.rechargeRow.memberId,
        amount: this.rechargeForm.amount,
        remark: this.rechargeForm.remark
      };
      this.rechargeLoading = true;
      this.apis.wallet_recharge(data).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('充值成功');
          this.rechargeVisible = false;
          this.getAccData();
        }
      }).catch(() => { /* 失败已由响应拦截器弹错误提示 */ }).then(() => {
        this.rechargeLoading = false;
      });
    },
    // ===== 冻结 / 解冻 =====
    freeze(row) {
      this.$confirm('确定冻结该会员储值账户吗？冻结后不可充值/扣款。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.wallet_freeze({ memberId: row.memberId });
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('已冻结');
          this.getAccData();
        }
      }).catch(() => { /* 失败已由响应拦截器弹错误提示 */ });
    },
    unfreeze(row) {
      this.$confirm('确定解冻该会员储值账户吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.wallet_unfreeze({ memberId: row.memberId });
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('已解冻');
          this.getAccData();
        }
      }).catch(() => { /* 失败已由响应拦截器弹错误提示 */ });
    },
  },
};
</script>

<style scoped lang="scss">
.page-head { margin-bottom: 12px; }
.page-title { margin: 0; font-size: 20px; }
.page-sub { margin: 4px 0 0; color: #909399; font-size: 13px; }
.stat-row { margin-bottom: 16px; }
.stat-card { background: #f5f7fa; border-radius: 4px; padding: 16px 20px; }
.stat-label { color: #909399; font-size: 13px; margin-bottom: 8px; }
.stat-value { font-size: 24px; font-weight: bold; color: #303133; }
.text-danger { color: #F56C6C; }
.text-success { color: #67C23A; }
.recharge-tip { background: #f5f7fa; padding: 10px 14px; border-radius: 4px; margin-bottom: 14px; line-height: 24px; font-size: 13px; }
.recharge-tip p { margin: 0; }
</style>
