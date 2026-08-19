<template>
  <div>
    <div class="page-head">
      <h2 class="page-title">会员私教权益</h2>
      <p class="page-sub">会员已购私教课时账本查看；支持批量调整有效期、变更未来归属门店和所属服务人</p>
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
      ref="benefitTable"
      :isSelection="true"
      :isHandle="true"
      :isPagination="true"
      :tableData="tableData"
      :tableCols="tableCols"
      :tablePage="pagination"
      :loading="tableLoading"
      :tableHandles="tableHandles"
      @select="tableSelect"
      @selectAll="tableSelect"
      @refresh="page()" />

    <el-dialog
      :title="validityDialog.operation === 'increase' ? '批量增加有效期' : '批量减少有效期'"
      :visible.sync="validityDialog.show"
      width="460px"
      :close-on-click-modal="false">
      <el-form label-width="100px">
        <el-form-item label="调整天数">
          <el-input-number v-model="validityDialog.days" :min="1" :max="36500" :step="1" controls-position="right" />
          <span class="unit-text">天</span>
        </el-form-item>
        <div class="dialog-tip">将按每条选中权益当前的到期时间逐条增加或减少，不会统一改成同一个日期。</div>
      </el-form>
      <span slot="footer">
        <el-button @click="validityDialog.show = false">取消</el-button>
        <el-button type="primary" @click="submitValidity">确定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="批量变更门店"
      :visible.sync="storeDialog.show"
      width="460px"
      :close-on-click-modal="false">
      <el-form label-width="100px">
        <el-form-item label="目标门店">
          <el-select v-model="storeDialog.storeAddrId" filterable clearable placeholder="请选择目标门店" style="width: 280px">
            <el-option v-for="item in storeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <div class="dialog-tip">只变更权益后续使用的门店，已有预约记录仍保留原上课门店。</div>
      </el-form>
      <span slot="footer">
        <el-button @click="storeDialog.show = false">取消</el-button>
        <el-button type="primary" @click="submitStore">确定</el-button>
      </span>
    </el-dialog>

    <el-dialog
      title="批量变更服务人"
      :visible.sync="coachDialog.show"
      width="520px"
      :close-on-click-modal="false">
      <el-form label-width="100px">
        <el-form-item label="所属服务人">
          <el-select
            v-model="coachDialog.coachId"
            filterable
            remote
            clearable
            reserve-keyword
            :remote-method="searchCoachOptions"
            :loading="coachOptionLoading"
            placeholder="输入姓名、手机号或ID搜索；清空表示取消指定"
            style="width: 350px">
            <el-option
              v-for="item in coachOptions"
              :key="item.id"
              :label="coachOptionLabel(item)"
              :value="item.id" />
          </el-select>
        </el-form-item>
        <div class="dialog-tip">服务人必须在所选权益的当前所属门店任职；已有预约的实际授课教练不受影响。</div>
      </el-form>
      <span slot="footer">
        <el-button @click="coachDialog.show = false">取消</el-button>
        <el-button type="primary" @click="submitCoach">确定</el-button>
      </span>
    </el-dialog>

    <!-- 权益详情抽屉:课时四态 + 来源订单 -->
    <el-drawer title="权益详情" :visible.sync="detailVisible" size="560px" :append-to-body="true">
      <div class="detail-wrap" v-if="detail">
        <div class="detail-section-title">权益信息</div>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">权益编号</span>{{ detail.benefitNo || '—' }}</el-col>
          <el-col :span="12"><span class="lab">课程状态</span><span v-html="statusTag(detail.status)"></span></el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">会员</span>{{ detail.memberName || '—' }}</el-col>
          <el-col :span="12"><span class="lab">手机号</span>{{ detail.memberMobile || '—' }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="8"><span class="lab">私教商品</span>{{ detail.productName || '—' }}</el-col>
          <el-col :span="8"><span class="lab">所属门店</span>{{ detail.storeName || '—' }}</el-col>
          <el-col :span="8"><span class="lab">服务人</span>{{ detail.coachName || '未指定' }}</el-col>
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
      selectList: [],
      storeOptions: [],
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
        { type: "select", placeholder: "课程状态", prop: "status", width: 160, options: [
          { value: 1, label: '生效中' },
          { value: 2, label: '已用完' },
          { value: 3, label: '已过期' },
          { value: 4, label: '已退款' }
        ] },
      ],
      searchHandle: [
        { label: "搜索", type: "primary", handle: e => { this.pagination.offset = 1; this.getData(); } },
      ],
      tableHandles: [
        { label: '批量增加有效期', type: 'primary', icon: 'el-icon-plus', handle: () => this.openValidityDialog('increase') },
        { label: '批量减少有效期', type: 'warning', icon: 'el-icon-minus', handle: () => this.openValidityDialog('decrease') },
        { label: '批量变更门店', type: 'success', icon: 'el-icon-office-building', handle: () => this.openStoreDialog() },
        { label: '批量变更服务人', type: 'info', icon: 'el-icon-user', handle: () => this.openCoachDialog() }
      ],
      tableData: [],
      tableCols: [
        { label: "会员", prop: "memberName", formatter: e => (e.memberName || '') + ' ' + (e.memberMobile || '') },
        { label: "来源订单", prop: "orderNo", width: 170 },
        { label: "私教商品", prop: "productName" },
        { label: "所属门店", prop: "storeName" },
        { label: "所属服务人", prop: "coachName", width: 120, formatter: e => e.coachName || '未指定' },
        { label: "总课时", prop: "totalLessons", width: 80 },
        { label: "已用课时", prop: "usedLessons", width: 80 },
        { label: "冻结课时", prop: "frozenLessons", width: 90, type: "html", html: e => e.frozenLessons > 0 ? '<span style="color:#E6A23C">' + e.frozenLessons + '</span>' : String(e.frozenLessons) },
        { label: "剩余课时", prop: "remainingLessons", width: 90, type: "html", html: e => e.remainingLessons > 0 ? '<span style="color:#67C23A">' + e.remainingLessons + '</span>' : String(e.remainingLessons) },
        { label: "生效时间", prop: "effectiveAt", width: 160, formatter: e => e.effectiveAt || '—' },
        { label: "到期时间", prop: "expireAt", width: 160, formatter: e => e.expireAt || '长期' },
        { label: "课程状态", prop: "status", width: 90, type: "html", html: e => this.statusTag(e.status) },
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
      validityDialog: { show: false, operation: 'increase', days: 1 },
      storeDialog: { show: false, storeAddrId: '' },
      coachDialog: { show: false, coachId: '' },
      coachOptions: [],
      coachOptionLoading: false,
      coachSearchSeq: 0,
    };
  },
  mounted() {
    this.getData();
    this.getStoreList();
  },
  methods: {
    // 课程状态:1生效中绿 / 2已用完灰 / 3已过期橙 / 4已退款红
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
      this.storeOptions = opts;
      this.searchForm[2].options = opts;
    },
    tableSelect(rows) {
      this.selectList = rows || [];
    },
    selectedBenefitIds() {
      if (!this.selectList.length) {
        this.$message.warning('请至少选择一条权益');
        return null;
      }
      return this.selectList.map(function (item) { return item.id; });
    },
    clearSelection() {
      if (this.$refs.benefitTable) {
        this.$refs.benefitTable.clearSelection();
      }
      this.selectList = [];
    },
    openValidityDialog(operation) {
      if (!this.selectedBenefitIds()) return;
      this.validityDialog.operation = operation;
      this.validityDialog.days = 1;
      this.validityDialog.show = true;
    },
    submitValidity() {
      var benefitIds = this.selectedBenefitIds();
      var days = Number(this.validityDialog.days);
      if (!benefitIds || !Number.isInteger(days) || days <= 0) {
        this.$message.warning('调整天数必须是大于 0 的整数');
        return;
      }
      var operation = this.validityDialog.operation;
      this.$confirm('将按每条权益当前到期时间逐条调整 ' + days + ' 天，确认继续吗？', '批量调整有效期', {
        type: 'warning'
      }).then(async () => {
        try {
          await this.apis.memberBenefit_batchAdjustExpireDate({ benefitIds: benefitIds, operation: operation, days: days });
          this.$message.success('批量调整成功');
          this.validityDialog.show = false;
          this.clearSelection();
          this.getData();
        } catch (e) {
          this.$message.error((e && (e.msg || e.message)) || '批量调整失败');
        }
      }).catch((e) => {
        if (e !== 'cancel' && e !== 'close') {
          this.$message.error('批量调整未完成');
        }
      });
    },
    openStoreDialog() {
      if (!this.selectedBenefitIds()) return;
      this.storeDialog.storeAddrId = '';
      this.storeDialog.show = true;
    },
    submitStore() {
      var benefitIds = this.selectedBenefitIds();
      if (!benefitIds || !this.storeDialog.storeAddrId) {
        this.$message.warning('请选择目标门店');
        return;
      }
      this.$confirm('确认将选中权益的未来归属门店变更为所选门店吗？', '批量变更门店', {
        type: 'warning'
      }).then(async () => {
        try {
          await this.apis.memberBenefit_batchChangeStore({ benefitIds: benefitIds, storeAddrId: this.storeDialog.storeAddrId });
          this.$message.success('批量变更成功');
          this.storeDialog.show = false;
          this.clearSelection();
          this.getData();
        } catch (e) {
          this.$message.error((e && (e.msg || e.message)) || '批量变更门店失败');
        }
      }).catch((e) => {
        if (e !== 'cancel' && e !== 'close') {
          this.$message.error('批量变更门店未完成');
        }
      });
    },
    openCoachDialog() {
      if (!this.selectedBenefitIds()) return;
      this.coachDialog.coachId = '';
      this.coachOptions = [];
      this.coachSearchSeq++;
      this.coachOptionLoading = false;
      this.coachDialog.show = true;
    },
    coachOptionLabel(item) {
      var mobile = item.mobile ? ' ' + item.mobile : '';
      var stores = item.storeNames ? ' · ' + item.storeNames : '';
      return (item.coachName || '未命名教练') + mobile + '（ID:' + item.id + '）' + stores;
    },
    async searchCoachOptions(keyword) {
      var value = (keyword || '').trim();
      var seq = ++this.coachSearchSeq;
      if (!value) {
        this.coachOptions = [];
        this.coachOptionLoading = false;
        return;
      }
      this.coachOptionLoading = true;
      try {
        var res = await this.apis.memberBenefit_coachOptions({ keyword: value });
        if (seq === this.coachSearchSeq) {
          this.coachOptions = (res && res.list) || [];
        }
      } catch (e) {
        if (seq === this.coachSearchSeq) {
          this.coachOptions = [];
        }
      } finally {
        if (seq === this.coachSearchSeq) {
          this.coachOptionLoading = false;
        }
      }
    },
    submitCoach() {
      var benefitIds = this.selectedBenefitIds();
      if (!benefitIds) return;
      var coachId = this.coachDialog.coachId || '';
      var message = coachId ? '确认将选中权益的所属服务人变更为所选教练吗？' : '确认清空选中权益的所属服务人吗？';
      this.$confirm(message, '批量变更服务人', { type: 'warning' }).then(async () => {
        try {
          await this.apis.memberBenefit_batchChangeCoach({ benefitIds: benefitIds, coachId: coachId });
          this.$message.success('批量变更服务人成功');
          this.coachDialog.show = false;
          this.clearSelection();
          this.getData();
        } catch (e) {
          this.$message.error((e && (e.msg || e.message)) || '批量变更服务人失败');
        }
      }).catch((e) => {
        if (e !== 'cancel' && e !== 'close') {
          this.$message.error('批量变更服务人未完成');
        }
      });
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
.unit-text { margin-left: 8px; color: #606266; }
.dialog-tip { color: #909399; font-size: 13px; line-height: 22px; padding-left: 100px; }
.detail-wrap { padding: 0 20px 20px; }
.detail-section-title { font-weight: bold; color: #303133; margin: 18px 0 10px; padding-left: 8px; border-left: 3px solid #409EFF; }
.detail-row { line-height: 32px; color: #606266; }
.detail-row .lab { display: inline-block; color: #909399; margin-right: 8px; min-width: 66px; }
.lesson-row { text-align: center; background: #f5f7fa; border-radius: 4px; padding: 12px 0; }
.lesson-num { font-size: 22px; font-weight: bold; }
.lesson-lab { color: #909399; font-size: 12px; margin-top: 4px; }
</style>
