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
    <r-form labelWidth="120px" :isHandle="true" :formRules="formRules" :formCols="formCols" :formHandle="formHandle" :formData="formData" ref="elForm" :inline="false" dialogWidth="680px">
      <el-form-item label="分档费用表">
        <table class="tier-table">
          <thead>
            <tr>
              <th>从第几次转让起</th>
              <th>收费（元）</th>
              <th class="op">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(t, i) in tiers" :key="i">
              <td>第 {{ i + 1 }} 次<span v-if="i === tiers.length - 1 && tiers.length > 1"> 及以后</span></td>
              <td><el-input v-model="t.fee" size="small" class="fee-input" placeholder="0" /></td>
              <td class="op"><span class="tier-del" @click="removeTier(i)">删除</span></td>
            </tr>
          </tbody>
        </table>
        <div class="tier-add">
          <el-button size="small" plain type="primary" icon="el-icon-plus" @click="addTier">添加档位</el-button>
        </div>
        <div class="tier-tip">按该权益「已被转让的次数」命中对应档位；最后一档兜底所有更高次数。第 1 次可设为 0 = 免费。</div>
      </el-form-item>
    </r-form>
  </div>
</template>

<script>
export default {
  components: {},
  data() {
    return {
      tableLoading: false,
      searchData: {
        ruleName: '',
        status: ''
      },
      searchForm: [
        { type: "input", placeholder: "规则名称", prop: "ruleName", width: 200 },
        { type: "select", placeholder: "状态", prop: "status", width: 160, options: [
          { value: 1, label: '启用' },
          { value: 0, label: '停用' }
        ] },
      ],
      searchHandle: [
        { label: "搜索", type: "primary", handle: e => this.getData() },
      ],
      tableHandles: [
        { label: "添加", type: "primary", handle: e => this.openAdd() },
      ],
      tableData: [],
      tableCols: [
        { label: "ID", prop: "feeRuleId", width: 70 },
        { label: "规则名称", prop: "ruleName" },
        { label: "分档摘要", prop: "tiersJson", formatter: e => this.tiersSummary(e.tiersJson) },
        { label: "状态", prop: "status", type: "html", html: e => e.status == 1
            ? '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:#f0f9eb;color:#67C23A;border:1px solid #e1f3d8">启用</span>'
            : '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:#f4f4f5;color:#909399;border:1px solid #e9e9eb">停用</span>' },
        { label: "创建时间", prop: 'createdDate', formatter: e => this.parseTime(e.createdDate) },
        {
          label: "操作",
          type: "button",
          width: 160,
          btnList: [
            { label: "编辑", type: "success", size: "mini", icon: "el-icon-edit", handle: (row) => this.elFormDetail(row) },
            { label: "删除", type: "danger", size: "mini", icon: "el-icon-delete", handle: (row) => this.del(row) },
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      formData: {
        feeRuleId: '',
        ruleName: '',
        status: 1,
        tiersJson: ''
      },
      // 分档数组(每项 {fee}，fromCount 按行序号自动生成)，单独管理，提交时序列化进 tiersJson
      tiers: [{ fee: '0' }],
      formCols: [
        { type: "input", label: "规则名称", width: 350, prop: "ruleName", placeholder: "如：标准转让费用" },
        { type: "radio", label: "状态", width: 350, prop: "status", radios: [
          { value: 1, label: '启用' },
          { value: 0, label: '停用' }
        ] },
      ],
      formRules: {
        ruleName: [
          { required: true, message: '请输入规则名称', trigger: 'blur' },
        ],
      },
      formHandle: [
        { label: "确认", type: "primary", icon: "el-icon-circle-plus-outline", handle: e => this.elFormSubmit() },
        { label: "取消", icon: "el-icon-circle-close", handle: e => this.elFormVisible() },
      ],
    };
  },
  mounted() {
    this.getData();
  },
  methods: {
    // tiers_json -> "第1次起免费 / 第2次起¥50 / 第3次起¥100"
    tiersSummary(tiersJson) {
      if (!tiersJson) return '—';
      var arr;
      try { arr = JSON.parse(tiersJson); } catch (e) { return tiersJson; }
      if (!arr || !arr.length) return '—';
      return arr.map(function (t) {
        var fee = Number(t.fee) === 0 ? '免费' : '¥' + t.fee;
        return '第' + t.fromCount + '次起' + fee;
      }).join(' / ');
    },
    openAdd() {
      this.tiers = [{ fee: '0' }];
      this.elFormVisible();
    },
    async getData() {
      var res = await this.apis.vipFeeRule_list(Object.assign({
        page: this.pagination.offset,
        limit: this.pagination.limit
      }, this.searchData));
      var list = res.pages.list || [];
      this.tableData = list;
      this.pagination.total = res.pages.totalCount;
    },
    page() {
      this.getData();
    },
    async elFormDetail(row) {
      Object.keys(this.formData).forEach((key) => {
        this.formData[key] = row[key];
      });
      var arr = [];
      try { arr = row.tiersJson ? JSON.parse(row.tiersJson) : []; } catch (e) { arr = []; }
      this.tiers = (arr && arr.length) ? arr.map(function (t) { return { fee: String(t.fee) }; }) : [{ fee: '0' }];
      this.elFormVisible('编辑');
    },
    addTier() {
      this.tiers.push({ fee: '0' });
    },
    removeTier(i) {
      if (this.tiers.length <= 1) {
        this.$message.warning('至少保留一个档位');
        return;
      }
      this.tiers.splice(i, 1);
    },
    elFormSubmit() {
      this.$refs.elForm.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.submit();
        }
      });
    },
    async submit() {
      // 前端先校验分档(与后端 ERROR_VIP_FEE_RULE_FORMAT 同口径)，fromCount 按行序号生成必为升序
      var tiers = [];
      for (var i = 0; i < this.tiers.length; i++) {
        var raw = this.tiers[i].fee;
        var fee = Number(raw);
        if (raw === '' || raw === null || isNaN(fee) || fee < 0) {
          this.$message.error('第 ' + (i + 1) + ' 档收费必须是 ≥0 的数字');
          return;
        }
        tiers.push({ fromCount: i + 1, fee: fee });
      }
      var data = Object.assign({}, this.formData, { tiersJson: JSON.stringify(tiers) });
      try {
        var res = !data.feeRuleId
          ? await this.apis.vipFeeRule_save(data)
          : await this.apis.vipFeeRule_update(data);
        if (res && res.code === 0) {
          this.$message.success('操作成功');
          this.elFormVisible();
          this.getData();
        }
      } catch (e) { /* 失败已由响应拦截器弹错误提示 */ }
    },
    del(row) {
      this.$confirm('确定要删除该费用规则吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.vipFeeRule_delete([row.feeRuleId]);
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('删除成功');
          this.getData();
        }
      }).catch(() => {});
    },
  },
};
</script>

<style scoped lang="scss">
.tier-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
  border: 1px solid #EBEEF5;
  th, td {
    padding: 8px 12px;
    border-bottom: 1px solid #EBEEF5;
    text-align: left;
  }
  thead th {
    background: #FAFAFA;
    color: #303133;
    font-weight: 700;
  }
  .op { width: 80px; }
  .fee-input { width: 120px; }
  .tier-del {
    color: #F56C6C;
    cursor: pointer;
    font-size: 13px;
  }
}
.tier-add { margin-top: 10px; }
.tier-tip {
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
  line-height: 1.6;
}
</style>
