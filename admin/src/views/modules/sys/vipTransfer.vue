<template>
  <div>
    <r-search ref="search" :searchData="searchData" :searchForm="searchForm" :searchHandle="searchHandle" />
    <r-table
      :isSelection="false"
      :isHandle="true"
      :isPagination="true"
      :tableData="tableData"
      :tableCols="tableCols"
      :tablePage="pagination"
      :loading="tableLoading"
      :tableHandles="tableHandles"
      @refresh="page()" />

    <!-- 审核弹窗 -->
    <el-dialog title="转让审核" :visible.sync="auditVisible" width="720px" append-to-body>
      <table class="info-table" v-if="current">
        <tbody>
          <tr>
            <td class="lab">转让单号</td><td>{{ current.transferId }}</td>
            <td class="lab">发起时间</td><td>{{ parseTime(current.createdDate) }}</td>
          </tr>
          <tr>
            <td class="lab">权益卡</td><td>{{ current.cardName }}</td>
            <td class="lab">当前状态</td><td><span v-html="statusTag(current.status)"></span></td>
          </tr>
          <tr>
            <td class="lab">转让人</td><td>{{ personText(current.fromNickname, current.fromPhone) }}</td>
            <td class="lab">受让人</td><td>{{ personText(current.toNickname, current.toPhone) }}</td>
          </tr>
          <tr>
            <td class="lab">转让费用</td><td>{{ feeText(current.serviceFee) }}</td>
            <td class="lab">确认截止</td><td>{{ current.confirmDeadline ? parseTime(current.confirmDeadline) : '—' }}</td>
          </tr>
        </tbody>
      </table>
      <div class="audit-tip">点击「通过」后系统将自动复核资质（有效期/封禁/黑名单/适用门店/卡状态等），复核不通过将自动驳回并原路退款。</div>
      <div class="remark-box">
        <div class="remark-lab">驳回理由（驳回时必填）</div>
        <el-input type="textarea" v-model="auditRemark" :rows="3" placeholder="如需驳回，请填写驳回理由" maxlength="200" show-word-limit />
      </div>
      <div slot="footer">
        <el-button type="danger" @click="doReject">驳回</el-button>
        <el-button type="primary" @click="doPass">通过</el-button>
      </div>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="转让详情" :visible.sync="detailVisible" width="720px" append-to-body>
      <table class="info-table" v-if="current">
        <tbody>
          <tr>
            <td class="lab">转让单号</td><td>{{ current.transferId }}</td>
            <td class="lab">发起时间</td><td>{{ parseTime(current.createdDate) }}</td>
          </tr>
          <tr>
            <td class="lab">权益卡</td><td>{{ current.cardName }}</td>
            <td class="lab">当前状态</td><td><span v-html="statusTag(current.status)"></span></td>
          </tr>
          <tr>
            <td class="lab">转让人</td><td>{{ personText(current.fromNickname, current.fromPhone) }}</td>
            <td class="lab">受让人</td><td>{{ personText(current.toNickname, current.toPhone) }}</td>
          </tr>
          <tr>
            <td class="lab">转让费用</td><td>{{ feeText(current.serviceFee) }}</td>
            <td class="lab">转让次数</td><td>{{ current.transferCount }}</td>
          </tr>
          <tr>
            <td class="lab">确认截止</td><td>{{ current.confirmDeadline ? parseTime(current.confirmDeadline) : '—' }}</td>
            <td class="lab">生效时间</td><td>{{ current.effectTime ? parseTime(current.effectTime) : '—' }}</td>
          </tr>
          <tr>
            <td class="lab">审核备注</td><td colspan="3">{{ current.auditRemark || '—' }}</td>
          </tr>
        </tbody>
      </table>
      <div slot="footer">
        <el-button @click="detailVisible = false">关闭</el-button>
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
        status: '',
        phone: '',
        fromDate: '',
        toDate: ''
      },
      searchForm: [
        { type: "select", placeholder: "状态", prop: "status", width: 160, options: [
          { value: 10, label: '待付费' },
          { value: 20, label: '待审核' },
          { value: 31, label: '已驳回' },
          { value: 40, label: '待受让人确认' },
          { value: 51, label: '已拒绝' },
          { value: 52, label: '已超时' },
          { value: 60, label: '已撤回' },
          { value: 70, label: '已生效' }
        ] },
        { type: "input", placeholder: "转让人/受让人手机", prop: "phone", width: 200 },
        { type: "date", placeholder: "发起起始日期", prop: "fromDate", width: 170 },
        { type: "date", placeholder: "发起结束日期", prop: "toDate", width: 170 },
      ],
      searchHandle: [
        { label: "搜索", type: "primary", handle: e => this.getData() },
      ],
      tableHandles: [],
      tableData: [],
      tableCols: [
        { label: "转让单号", prop: "transferId", width: 90 },
        { label: "权益卡", prop: "cardName" },
        { label: "转让人", prop: "fromNickname", formatter: e => this.personText(e.fromNickname, e.fromPhone) },
        { label: "受让人", prop: "toNickname", formatter: e => this.personText(e.toNickname, e.toPhone) },
        { label: "转让费用", prop: "serviceFee", formatter: e => this.feeText(e.serviceFee) },
        { label: "状态", prop: "status", type: "html", html: e => this.statusTag(e.status) },
        { label: "发起时间", prop: 'createdDate', formatter: e => this.parseTime(e.createdDate) },
        {
          label: "操作",
          type: "button",
          width: 150,
          btnList: [
            { label: "审核", type: "primary", size: "mini", isShow: row => row.status == 20, handle: (row) => this.openAudit(row) },
            { label: "详情", size: "mini", handle: (row) => this.openDetail(row) },
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      auditVisible: false,
      detailVisible: false,
      current: null,
      auditRemark: '',
    };
  },
  mounted() {
    this.getData();
  },
  methods: {
    // 昵称 + 空格 + 手机
    personText(nickname, phone) {
      var n = nickname || '';
      var p = phone || '';
      return (n + ' ' + p).trim() || '—';
    },
    // 转让费用：>0 显示 ¥值，否则免费
    feeText(fee) {
      return Number(fee) > 0 ? '¥' + fee : '免费';
    },
    // 状态文案
    statusText(status) {
      var map = {
        10: '待付费',
        20: '待审核',
        31: '已驳回',
        40: '待受让人确认',
        51: '已拒绝',
        52: '已超时',
        60: '已撤回',
        70: '已生效'
      };
      return map[status] || status;
    },
    // 状态带色标签（type:html + 弹窗复用）
    statusTag(status) {
      // 10/20 橙；40 蓝；70 绿；31 红；51/52/60 灰
      var color = {
        10: 'bg:#fdf6ec;color:#E6A23C;border:#faecd8',
        20: 'bg:#fdf6ec;color:#E6A23C;border:#faecd8',
        40: 'bg:#ecf5ff;color:#409EFF;border:#d9ecff',
        70: 'bg:#f0f9eb;color:#67C23A;border:#e1f3d8',
        31: 'bg:#fef0f0;color:#F56C6C;border:#fde2e2',
        51: 'bg:#f4f4f5;color:#909399;border:#e9e9eb',
        52: 'bg:#f4f4f5;color:#909399;border:#e9e9eb',
        60: 'bg:#f4f4f5;color:#909399;border:#e9e9eb'
      };
      var c = color[status] || 'bg:#f4f4f5;color:#909399;border:#e9e9eb';
      var parts = c.split(';');
      var bg = parts[0].split(':')[1];
      var fc = parts[1].split(':')[1];
      var bd = parts[2].split(':')[1];
      return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:' + bg + ';color:' + fc + ';border:1px solid ' + bd + '">' + this.statusText(status) + '</span>';
    },
    async getData() {
      this.tableLoading = true;
      try {
        var res = await this.apis.vipTransfer_list(Object.assign({
          page: this.pagination.offset,
          limit: this.pagination.limit
        }, this.searchData));
        var list = (res.pages && res.pages.list) || [];
        this.tableData = list;
        this.pagination.total = res.pages ? res.pages.totalCount : 0;
      } finally {
        this.tableLoading = false;
      }
    },
    page() {
      this.getData();
    },
    openAudit(row) {
      this.current = row;
      this.auditRemark = '';
      this.auditVisible = true;
    },
    openDetail(row) {
      this.current = row;
      this.detailVisible = true;
    },
    // 通过：二次确认后提交（pass=1）
    doPass() {
      this.$confirm('确定审核通过该转让申请吗？系统将自动复核资质。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.submitAudit(1);
      }).catch(() => {});
    },
    // 驳回：校验理由非空后提交（pass=0）
    doReject() {
      if (!this.auditRemark || !this.auditRemark.trim()) {
        this.$message.warning('请填写驳回理由');
        return;
      }
      this.submitAudit(0);
    },
    async submitAudit(pass) {
      try {
        var res = await this.apis.vipTransfer_audit({
          transferId: this.current.transferId,
          pass: pass,
          remark: this.auditRemark
        });
        if (res && res.code === 0) {
          this.$message.success(pass === 1 ? '审核已通过' : '已驳回');
          this.auditVisible = false;
          this.getData();
        }
      } catch (e) { /* 失败已由响应拦截器弹错误提示 */ }
    },
  },
};
</script>

<style scoped lang="scss">
.info-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  border: 1px solid #EBEEF5;
  td {
    padding: 8px 12px;
    border: 1px solid #EBEEF5;
    text-align: left;
    color: #303133;
  }
  .lab {
    background: #FAFAFA;
    color: #909399;
    width: 100px;
  }
}
.audit-tip {
  margin-top: 14px;
  padding: 8px 12px;
  font-size: 12px;
  color: #909399;
  line-height: 1.6;
  background: #f4f4f5;
  border-radius: 4px;
}
.remark-box {
  margin-top: 14px;
  .remark-lab {
    font-size: 13px;
    color: #606266;
    margin-bottom: 6px;
  }
}
</style>
