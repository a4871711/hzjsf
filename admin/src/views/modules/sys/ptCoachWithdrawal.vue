<template>
  <div class="withdrawal-page">
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
      @refresh="page" />

    <el-dialog title="提现审核" :visible.sync="dialogVisible" width="720px" :close-on-click-modal="false">
      <el-form v-if="formData" label-width="125px" size="small">
        <el-form-item label="教练">
          <span>{{ formData.coachName || '-' }}</span>
          <span class="muted">{{ formData.coachNo || '' }} {{ formData.mobile || '' }}</span>
        </el-form-item>
        <el-form-item label="银行卡">
          <span>{{ formData.bankName || '-' }} / {{ formData.bankCardNo || '-' }}</span>
        </el-form-item>
        <el-form-item label="收款人">
          <span>{{ formData.accountName || '-' }}</span>
        </el-form-item>
        <el-form-item label="申请金额">
          <span>¥{{ money(formData.requestedAmount) }}</span>
        </el-form-item>
        <el-form-item label="结算金额" required>
          <el-input v-model="formData.settlementAmount" style="width:220px" :disabled="formData.status !== 0">
            <template slot="prepend">¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="实际结算金额" required>
          <el-input v-model="formData.actualSettlementAmount" style="width:220px" :disabled="formData.status !== 0">
            <template slot="prepend">¥</template>
          </el-input>
          <span class="form-tip">审核通过后按此金额扣减，低于冻结金额时差额自动释放。</span>
        </el-form-item>
        <el-form-item label="审核附件">
          <el-upload
            :action="uploadAction"
            name="file"
            :headers="uploadHeaders"
            multiple
            :file-list="uploadFileList"
            :on-success="onUploadSuccess"
            :on-remove="onUploadRemove"
            :on-error="onUploadError">
            <el-button size="mini" type="primary" plain :disabled="formData.status !== 0">上传附件</el-button>
            <span slot="tip" class="el-upload__tip">支持图片或凭证文件，审核时一并保存。</span>
          </el-upload>
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input v-model="formData.reviewRemark" type="textarea" :rows="3" maxlength="500" show-word-limit :disabled="formData.status !== 0" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button v-if="formData && formData.status === 0" type="danger" :loading="reviewLoading" @click="submitReview(1)">驳回</el-button>
        <el-button v-if="formData && formData.status === 0" type="primary" :loading="reviewLoading" @click="submitReview(2)">审核通过</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchData: { coachName: '', status: '' },
      searchForm: [
        { type: 'input', placeholder: '教练姓名', prop: 'coachName', width: 180 },
        { type: 'select', placeholder: '审核状态', prop: 'status', width: 160, options: [
          { value: 0, label: '待审核' },
          { value: 1, label: '已驳回' },
          { value: 2, label: '已通过' }
        ] }
      ],
      searchHandle: [
        { type: 'primary', label: '搜索', handle: () => { this.pagination.offset = 1; this.getData(); } }
      ],
      tableLoading: false,
      tableData: [],
      tableCols: [
        { label: '教练', prop: 'coachName', formatter: e => (e.coachName || '-') + ' ' + (e.coachNo || '') },
        { label: '申请金额', prop: 'requestedAmount', width: 110, formatter: e => '¥' + this.money(e.requestedAmount) },
        { label: '结算金额', prop: 'settlementAmount', width: 110, formatter: e => '¥' + this.money(e.settlementAmount) },
        { label: '实际结算', prop: 'actualSettlementAmount', width: 110, formatter: e => e.actualSettlementAmount == null ? '-' : '¥' + this.money(e.actualSettlementAmount) },
        { label: '银行卡', prop: 'bankCardNo', width: 140 },
        { label: '状态', prop: 'status', width: 90, type: 'html', html: e => this.statusTag(e.status) },
        { label: '申请时间', prop: 'createdAt', width: 160 },
        { label: '操作', type: 'button', width: 90, fixed: 'right', btnList: [
          { label: '审核', type: 'primary', size: 'mini', isShow: () => this.checkBtn('sys:ptCoachWithdrawal:review'), handle: row => this.openDetail(row) }
        ] }
      ],
      tableHandles: [],
      pagination: { limit: 10, offset: 1, total: 1 },
      dialogVisible: false,
      reviewLoading: false,
      formData: null,
      uploadFileList: []
    };
  },
  computed: {
    uploadAction() {
      return (process.env.NODE_ENV !== 'production' ? 'proxyApi/' : process.env.VUE_APP_URL) + '/sys/uploads';
    },
    uploadHeaders() {
      return { token: this.$cookie.get('token') };
    }
  },
  mounted() {
    this.getData();
  },
  methods: {
    getData() {
      this.tableLoading = true;
      this.apis.ptCoachWithdrawal_list(Object.assign({
        page: this.pagination.offset,
        limit: this.pagination.limit
      }, this.searchData)).then(res => {
        this.tableData = (res.page && res.page.list) || [];
        this.pagination.total = (res.page && res.page.totalCount) || 0;
      }).finally(() => { this.tableLoading = false; });
    },
    page() {
      this.getData();
    },
    openDetail(row) {
      this.apis.ptCoachWithdrawal_info({ id: row.id }).then(res => {
        this.formData = Object.assign({}, res.withdrawal || {});
        this.uploadFileList = this.parseAttachments(this.formData.attachmentUrls);
        this.dialogVisible = true;
      });
    },
    submitReview(status) {
      if (this.reviewLoading || !this.formData) return;
      if (status === 2) {
        if (!this.positive(this.formData.settlementAmount) || !this.positive(this.formData.actualSettlementAmount)) {
          this.$message.error('结算金额和实际结算金额必须大于0');
          return;
        }
      }
      this.reviewLoading = true;
      this.apis.ptCoachWithdrawal_review({
        id: this.formData.id,
        status,
        settlementAmount: status === 2 ? this.formData.settlementAmount : null,
        actualSettlementAmount: status === 2 ? this.formData.actualSettlementAmount : null,
        attachmentUrls: JSON.stringify(this.uploadFileList.map(item => item.url || item.path).filter(Boolean)),
        reviewRemark: this.formData.reviewRemark || ''
      }).then(() => {
        this.$message.success(status === 2 ? '审核通过' : '已驳回');
        this.dialogVisible = false;
        this.getData();
      }).finally(() => { this.reviewLoading = false; });
    },
    onUploadSuccess(res, file) {
      if (!res || !res.path || !res.path.length) {
        this.$message.error((res && res.msg) || '上传失败');
        return;
      }
      file.url = res.path[0];
      if (!this.uploadFileList.some(item => (item.url || item.path) === res.path[0])) {
        this.uploadFileList.push({ name: file.name, url: res.path[0] });
      }
    },
    onUploadRemove(file) {
      const url = file.url || file.path;
      this.uploadFileList = this.uploadFileList.filter(item => (item.url || item.path) !== url);
    },
    onUploadError() {
      this.$message.error('附件上传失败');
    },
    parseAttachments(value) {
      if (!value) return [];
      try {
        const list = JSON.parse(value);
        return Array.isArray(list) ? list.map((url, index) => ({ name: '附件' + (index + 1), url })) : [];
      } catch (e) {
        return [];
      }
    },
    positive(value) {
      return value !== null && value !== undefined && value !== '' && Number(value) > 0;
    },
    money(value) {
      const amount = Number(value || 0);
      return isNaN(amount) ? '0.00' : amount.toFixed(2);
    },
    statusTag(status) {
      const map = {
        0: ['待审核', '#E6A23C'],
        1: ['已驳回', '#F56C6C'],
        2: ['已通过', '#67C23A']
      };
      const item = map[status] || ['未知', '#909399'];
      return '<span style="color:' + item[1] + '">' + item[0] + '</span>';
    }
  }
};
</script>

<style scoped>
.muted { color: #909399; margin-left: 10px; }
.form-tip { margin-left: 12px; color: #909399; font-size: 12px; }
.el-upload__tip { margin-left: 10px; color: #909399; }
</style>
