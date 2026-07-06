<template>
  <div>
    <div class="page-head">
      <h2 class="page-title">购买记录</h2>
      <p class="page-sub">私教商品购买订单查看与退款（后台不手工建/删订单，仅查看+退款）</p>
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

    <!-- 订单详情抽屉:双列字段 + 权益课时 + 券快照 -->
    <el-drawer title="订单详情" :visible.sync="detailVisible" size="640px" :append-to-body="true">
      <div class="detail-wrap" v-if="detail">
        <div class="detail-section-title">订单信息</div>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">订单编号</span>{{ detail.orderNo || '—' }}</el-col>
          <el-col :span="12"><span class="lab">订单状态</span><span v-html="orderStatusTag(detail.orderStatus)"></span></el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">会员</span>{{ detail.memberName || '—' }}</el-col>
          <el-col :span="12"><span class="lab">手机号</span>{{ detail.memberMobile || '—' }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">购买商品</span>{{ detail.productName || '—' }}</el-col>
          <el-col :span="12"><span class="lab">商品类型</span>{{ detail.productTypeName || '—' }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">服务类型</span>{{ serviceTypeText(detail.serviceType) }}</el-col>
          <el-col :span="12"><span class="lab">购买门店</span>{{ detail.storeName || '—' }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">课时数</span>{{ detail.lessonCount }}</el-col>
          <el-col :span="12"><span class="lab">单次时长</span>{{ detail.durationMinutes }} 分钟</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">有效期</span>{{ validityText(detail.validityDays) }}</el-col>
          <el-col :span="12"><span class="lab">支付方式</span>{{ payMethodText(detail.payMethod) }}</el-col>
        </el-row>

        <div class="detail-section-title">金额</div>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">原价</span>{{ money(detail.originalAmount) }}</el-col>
          <el-col :span="12"><span class="lab">优惠合计</span>{{ money(detail.discountAmount) }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">应付</span>{{ money(detail.payableAmount) }}</el-col>
          <el-col :span="12"><span class="lab">实付</span>{{ money(detail.paidAmount) }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">累计退款</span><span :class="{ 'text-danger': detail.refundAmount > 0 }">{{ money(detail.refundAmount) }}</span></el-col>
          <el-col :span="12"><span class="lab">营销活动</span>{{ marketingText(detail.marketingType) }}<span v-if="detail.marketingActivityName">（{{ detail.marketingActivityName }}）</span></el-col>
        </el-row>

        <div class="detail-section-title">优惠券</div>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">券名称</span>{{ detail.couponName || '未使用' }}</el-col>
          <el-col :span="12"><span class="lab">券抵扣</span>{{ money(detail.couponDiscountAmount) }}</el-col>
        </el-row>

        <div class="detail-section-title">关联权益（课时账本）</div>
        <template v-if="detail.benefitId">
          <el-row class="detail-row">
            <el-col :span="12"><span class="lab">权益编号</span>{{ detail.benefitNo || '—' }}</el-col>
            <el-col :span="12"><span class="lab">权益状态</span><span v-html="benefitStatusTag(detail.benefitStatus)"></span></el-col>
          </el-row>
          <el-row class="detail-row">
            <el-col :span="6"><span class="lab">总课时</span>{{ detail.totalLessons }}</el-col>
            <el-col :span="6"><span class="lab">已用</span>{{ detail.usedLessons }}</el-col>
            <el-col :span="6"><span class="lab">冻结</span>{{ detail.frozenLessons }}</el-col>
            <el-col :span="6"><span class="lab">剩余</span>{{ detail.remainingLessons }}</el-col>
          </el-row>
          <el-row class="detail-row">
            <el-col :span="24"><span class="lab">到期时间</span>{{ detail.expireAt || '长期有效' }}</el-col>
          </el-row>
        </template>
        <div v-else class="detail-empty">暂无关联权益（订单未支付或已取消）</div>

        <div class="detail-section-title">时间</div>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">下单时间</span>{{ detail.createdAt || '—' }}</el-col>
          <el-col :span="12"><span class="lab">支付时间</span>{{ detail.paidAt || '—' }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">结清时间</span>{{ detail.settledAt || '—' }}</el-col>
          <el-col :span="12"><span class="lab">退款时间</span>{{ detail.refundAt || '—' }}</el-col>
        </el-row>
      </div>
    </el-drawer>

    <!-- 退款弹窗:退款金额/退款课时/备注,顶部显示已付/已退 -->
    <el-dialog title="订单退款" :visible.sync="refundVisible" width="520px" :append-to-body="true">
      <div v-if="refundRow" class="refund-tip">
        <p>订单编号：<b>{{ refundRow.orderNo }}</b></p>
        <p>已付金额：<b>{{ money(refundRow.paidAmount) }}</b>　已退金额：<b class="text-danger">{{ money(refundRow.refundAmount) }}</b>　可退：<b>{{ money(refundableAmount) }}</b></p>
        <p>剩余可退课时：<b>{{ refundRow.remainingLessons != null ? refundRow.remainingLessons : '—' }}</b></p>
      </div>
      <el-form :model="refundForm" label-width="110px" size="medium">
        <el-form-item label="退款金额" required>
          <el-input v-model="refundForm.refundAmount" placeholder="不可超过可退金额" style="width:280px"></el-input>
        </el-form-item>
        <el-form-item label="退款课时">
          <el-input v-model="refundForm.refundLessons" placeholder="留空=按剩余课时全冲；填0=只退钱不冲课时" style="width:280px"></el-input>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="refundForm.remark" type="textarea" :rows="3" placeholder="退款原因/备注" style="width:280px"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="refundVisible = false">取消</el-button>
        <el-button type="primary" :loading="refundLoading" @click="submitRefund">确认退款</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  components: {},
  data() {
    return {
      tableLoading: false,
      searchData: {
        orderNo: '',
        memberKeyword: '',
        storeId: '',
        marketingType: '',
        orderStatus: '',
        dateRange: []
      },
      searchForm: [
        { type: "input", placeholder: "订单编号", prop: "orderNo", width: 200 },
        { type: "input", placeholder: "会员姓名/手机号", prop: "memberKeyword", width: 200 },
        { type: "select", placeholder: "购买门店", prop: "storeId", width: 180, options: [] },
        { type: "select", placeholder: "营销活动", prop: "marketingType", width: 160, options: [
          { value: 0, label: '普通购买' },
          { value: 1, label: '拼团' },
          { value: 2, label: '限时秒杀' }
        ] },
        { type: "select", placeholder: "订单状态", prop: "orderStatus", width: 160, options: [
          { value: 0, label: '待支付' },
          { value: 1, label: '首付已付' },
          { value: 2, label: '已结清' },
          { value: 3, label: '已取消' },
          { value: 4, label: '已退款' }
        ] },
        { type: "daterange", placeholder: "购买时间", prop: "dateRange", width: 260 },
      ],
      searchHandle: [
        // 搜索时重置回第 1 页,避免翻页后再加筛选停在越界空页
        { label: "搜索", type: "primary", handle: e => { this.pagination.offset = 1; this.getData(); } },
      ],
      tableData: [],
      tableCols: [
        { label: "订单编号", prop: "orderNo", width: 180 },
        { label: "会员", prop: "memberName", formatter: e => (e.memberName || '') + ' ' + (e.memberMobile || '') },
        { label: "购买商品", prop: "productName" },
        { label: "服务类型", prop: "serviceType", width: 90, formatter: e => this.serviceTypeText(e.serviceType) },
        { label: "购买门店", prop: "storeName" },
        { label: "营销活动", prop: "marketingType", width: 90, formatter: e => this.marketingText(e.marketingType) },
        { label: "支付方式", prop: "payMethod", width: 90, formatter: e => this.payMethodText(e.payMethod) },
        { label: "实付金额", prop: "paidAmount", width: 100, formatter: e => this.money(e.paidAmount) },
        { label: "退款金额", prop: "refundAmount", width: 100, type: "html", html: e => e.refundAmount > 0 ? '<span style="color:#F56C6C">' + this.money(e.refundAmount) + '</span>' : this.money(e.refundAmount) },
        { label: "课时(剩余/总)", prop: "remainingLessons", width: 110, formatter: e => e.benefitId ? (e.remainingLessons + '/' + e.totalLessons) : '—' },
        { label: "到期时间", prop: "expireAt", width: 160, formatter: e => e.benefitId ? (e.expireAt || '长期') : '—' },
        { label: "订单状态", prop: "orderStatus", width: 90, type: "html", html: e => this.orderStatusTag(e.orderStatus) },
        { label: "购买时间", prop: "createdAt", width: 160, formatter: e => e.createdAt || '—' },
        {
          label: "操作",
          type: "button",
          width: 150,
          fixed: "right",
          btnList: [
            { label: "详情", type: "primary", size: "mini", handle: (row) => this.openDetail(row) },
            {
              label: "退款",
              type: "danger",
              size: "mini",
              isShow: () => this.checkBtn('sys:privateOrder:refund'),
              // 仅 首付已付(1)/已结清(2) 可退;已退款/已取消/待支付禁用
              disabled: (row) => row.orderStatus != 1 && row.orderStatus != 2,
              handle: (row) => this.openRefund(row)
            },
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      detailVisible: false,
      detail: null,
      refundVisible: false,
      refundRow: null,
      refundLoading: false,
      refundForm: { refundAmount: '', refundLessons: '', remark: '' },
    };
  },
  computed: {
    // 本单还可退金额 = 已付 - 已退
    refundableAmount() {
      if (!this.refundRow) return 0;
      var v = Number(this.refundRow.paidAmount || 0) - Number(this.refundRow.refundAmount || 0);
      return v > 0 ? v : 0;
    }
  },
  mounted() {
    this.getData();
    this.getStoreList();
  },
  methods: {
    money(v) {
      return (v === null || v === undefined || v === '') ? '¥0.00' : '¥' + v;
    },
    serviceTypeText(t) {
      return t == 1 ? '一对一' : (t == 2 ? '一对多' : '—');
    },
    validityText(days) {
      return (days == -1) ? '长期有效' : (days != null ? days + ' 天' : '—');
    },
    payMethodText(m) {
      var map = { 1: '微信', 2: '支付宝', 3: '储值', 4: '分期', 9: '其他' };
      return map[m] || '—';
    },
    marketingText(t) {
      var map = { 0: '普通', 1: '拼团', 2: '秒杀' };
      return map[t] || '—';
    },
    // 订单状态:0待支付灰 / 1首付已付蓝 / 2已结清绿 / 3已取消灰 / 4已退款红
    orderStatusTag(status) {
      var map = {
        0: ['待支付', '#f4f4f5', '#909399', '#e9e9eb'],
        1: ['首付已付', '#ecf5ff', '#409EFF', '#d9ecff'],
        2: ['已结清', '#f0f9eb', '#67C23A', '#e1f3d8'],
        3: ['已取消', '#f4f4f5', '#909399', '#e9e9eb'],
        4: ['已退款', '#fef0f0', '#F56C6C', '#fde2e2']
      };
      var s = map[status] || ['未知', '#f4f4f5', '#909399', '#e9e9eb'];
      return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:' + s[1] + ';color:' + s[2] + ';border:1px solid ' + s[3] + '">' + s[0] + '</span>';
    },
    // 权益状态:1生效中绿 / 2已用完灰 / 3已过期橙 / 4已退款红
    benefitStatusTag(status) {
      var map = {
        1: ['生效中', '#f0f9eb', '#67C23A', '#e1f3d8'],
        2: ['已用完', '#f4f4f5', '#909399', '#e9e9eb'],
        3: ['已过期', '#fdf6ec', '#E6A23C', '#faecd8'],
        4: ['已退款', '#fef0f0', '#F56C6C', '#fde2e2']
      };
      var s = map[status] || ['—', '#f4f4f5', '#909399', '#e9e9eb'];
      return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:' + s[1] + ';color:' + s[2] + ';border:1px solid ' + s[3] + '">' + s[0] + '</span>';
    },
    async getData() {
      this.tableLoading = true;
      try {
        // daterange 绑定为 [起, 止] 数组(清空为 null),拆成后端要的 startTime/endTime
        var dr = this.searchData.dateRange || [];
        var res = await this.apis.privateOrder_list({
          page: this.pagination.offset,
          limit: this.pagination.limit,
          orderNo: this.searchData.orderNo,
          memberKeyword: this.searchData.memberKeyword,
          storeId: this.searchData.storeId,
          marketingType: this.searchData.marketingType,
          orderStatus: this.searchData.orderStatus,
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
    // 购买门店下拉
    async getStoreList() {
      var res = await this.apis.store_list({ page: 1, limit: 999 });
      var list = (res.page && res.page.list) || [];
      var opts = list.map(function (item) { return { value: item.storeAddrId, label: item.storeName }; });
      this.searchForm[2].options = opts;
    },
    async openDetail(row) {
      this.detail = null;
      this.detailVisible = true;
      var res = await this.apis.privateOrder_info({ id: row.id });
      this.detail = (res && res.entity) || null;
    },
    openRefund(row) {
      this.refundRow = row;
      this.refundForm = { refundAmount: '', refundLessons: '', remark: '' };
      this.refundVisible = true;
    },
    submitRefund() {
      var amt = Number(this.refundForm.refundAmount);
      if (!this.refundForm.refundAmount || isNaN(amt) || amt <= 0) {
        this.$message.warning('请输入正确的退款金额');
        return;
      }
      if (amt > this.refundableAmount) {
        this.$message.warning('退款金额不可超过可退金额 ' + this.money(this.refundableAmount));
        return;
      }
      var data = {
        orderId: this.refundRow.id,
        refundAmount: this.refundForm.refundAmount,
        remark: this.refundForm.remark
      };
      // 退款课时:留空不传(后端按剩余全冲);填了(含0)才传
      if (this.refundForm.refundLessons !== '' && this.refundForm.refundLessons !== null) {
        data.refundLessons = this.refundForm.refundLessons;
      }
      this.refundLoading = true;
      this.apis.privateOrder_refund(data).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('退款成功');
          this.refundVisible = false;
          this.getData();
        }
      }).catch(() => { /* 失败已由响应拦截器弹错误提示 */ }).then(() => {
        this.refundLoading = false;
      });
    },
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
.refund-tip { background: #f5f7fa; padding: 10px 14px; border-radius: 4px; margin-bottom: 14px; line-height: 24px; font-size: 13px; }
.refund-tip p { margin: 0; }
</style>
