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

    <el-dialog title="拉黑会员" :visible.sync="dialogVisible" width="700px">
      <el-form label-width="120px">
        <el-form-item label="会员手机号" required>
          <el-input v-model="form.phone" placeholder="请输入会员手机号" style="width:350px" />
        </el-form-item>
        <el-form-item label="拉黑原因">
          <el-input v-model="form.reason" type="textarea" :rows="3" placeholder="请输入拉黑原因" style="width:350px" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="saveBlacklist()">确认</el-button>
        <el-button @click="dialogVisible = false">取消</el-button>
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
      dialogVisible: false,
      form: {
        phone: '',
        reason: ''
      },
      searchData: {
        phone: ''
      },
      searchForm: [
        { type: "input", placeholder: "会员手机", prop: "phone", width: 200 },
      ],
      searchHandle: [
        { label: "搜索", type: "primary", handle: e => this.getData() },
      ],
      tableHandles: [
        { label: "拉黑会员", type: "primary", handle: e => this.openAdd() },
      ],
      tableData: [],
      tableCols: [
        { label: "ID", prop: "id", width: 70 },
        { label: "会员", prop: "nickname", formatter: e => (e.nickname || '') + ' ' + (e.phone || '') },
        { label: "拉黑原因", prop: "reason" },
        { label: "操作人", prop: "operator" },
        { label: "拉黑时间", prop: 'createdDate', formatter: e => this.parseTime(e.createdDate) },
        { label: "状态", prop: "status", type: "html", html: e => e.status == 1
            ? '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:#fef0f0;color:#F56C6C;border:1px solid #fde2e2">生效中</span>'
            : '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:#f4f4f5;color:#909399;border:1px solid #e9e9eb">已解除</span>' },
        {
          label: "操作",
          type: "button",
          width: 140,
          btnList: [
            { label: "解除黑名单", type: "danger", size: "mini", isShow: (row) => row.status == 1, handle: (row) => this.remove(row) },
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
    };
  },
  mounted() {
    this.getData();
  },
  methods: {
    async getData() {
      this.tableLoading = true;
      try {
        var res = await this.apis.memberBlacklist_list(Object.assign({
          page: this.pagination.offset,
          limit: this.pagination.limit
        }, this.searchData));
        this.tableData = (res.pages && res.pages.list) || [];
        this.pagination.total = res.pages ? res.pages.totalCount : 0;
      } finally {
        this.tableLoading = false;
      }
    },
    page() {
      this.getData();
    },
    openAdd() {
      this.form = { phone: '', reason: '' };
      this.dialogVisible = true;
    },
    async saveBlacklist() {
      if (!this.form.phone) {
        this.$message.error('请输入会员手机号');
        return;
      }
      try {
        var res = await this.apis.memberBlacklist_save({ phone: this.form.phone, reason: this.form.reason });
        if (res && res.code === 0) {
          this.$message.success('操作成功');
          this.dialogVisible = false;
          this.getData();
        }
      } catch (e) { /* 失败已由响应拦截器弹错误提示 */ }
    },
    remove(row) {
      this.$confirm('确定要解除该会员的黑名单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.memberBlacklist_remove({ id: row.id });
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('已解除黑名单');
          this.getData();
        }
      }).catch(() => {});
    },
  },
};
</script>

<style scoped lang="scss">
</style>
