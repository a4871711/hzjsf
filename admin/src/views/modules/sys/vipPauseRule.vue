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
      <el-form-item label="分档价目表">
        <table class="tier-table">
          <thead>
            <tr>
              <th>停卡天数（天）</th>
              <th>价格（元）</th>
              <th class="op">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(t, i) in tiers" :key="i">
              <td><el-input v-model="t.days" size="small" class="fee-input" placeholder="如 3" /></td>
              <td><el-input v-model="t.price" size="small" class="fee-input" placeholder="如 100" /></td>
              <td class="op"><span class="tier-del" @click="removeTier(i)">删除</span></td>
            </tr>
          </tbody>
        </table>
        <div class="tier-add">
          <el-button size="small" plain type="primary" icon="el-icon-plus" @click="addTier">添加档位</el-button>
        </div>
        <div class="tier-tip">会员付费停卡时按档位选择停卡天数并支付对应价格。天数为 1~365 的正整数且不可重复，价格必须大于 0。</div>
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
        { label: "ID", prop: "pauseRuleId", width: 70 },
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
        pauseRuleId: '',
        ruleName: '',
        status: 1,
        tiersJson: ''
      },
      // 分档数组(每项 {days, price})，单独管理，提交时序列化进 tiersJson
      tiers: [{ days: '', price: '' }],
      formCols: [
        { type: "input", label: "规则名称", width: 350, prop: "ruleName", placeholder: "如：标准停卡收费" },
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
    // tiers_json -> "3天¥100 / 7天¥200"
    tiersSummary(tiersJson) {
      if (!tiersJson) return '—';
      var arr;
      try { arr = JSON.parse(tiersJson); } catch (e) { return tiersJson; }
      if (!arr || !arr.length) return '—';
      return arr.map(function (t) {
        return t.days + '天¥' + t.price;
      }).join(' / ');
    },
    openAdd() {
      this.tiers = [{ days: '', price: '' }];
      this.elFormVisible();
    },
    async getData() {
      var res = await this.apis.vipPauseRule_list(Object.assign({
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
      this.tiers = (arr && arr.length)
        ? arr.map(function (t) { return { days: String(t.days), price: String(t.price) }; })
        : [{ days: '', price: '' }];
      this.elFormVisible('编辑');
    },
    addTier() {
      this.tiers.push({ days: '', price: '' });
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
      // 前端校验分档：至少 1 档；days 为 1~365 正整数且不重复；price > 0
      var tiers = [];
      var daysSeen = {};
      for (var i = 0; i < this.tiers.length; i++) {
        var rawDays = this.tiers[i].days;
        var rawPrice = this.tiers[i].price;
        var days = Number(rawDays);
        var price = Number(rawPrice);
        if (rawDays === '' || rawDays === null || isNaN(days) || days !== Math.floor(days) || days < 1 || days > 365) {
          this.$message.error('第 ' + (i + 1) + ' 档停卡天数必须是 1~365 的正整数');
          return;
        }
        if (daysSeen[days]) {
          this.$message.error('第 ' + (i + 1) + ' 档停卡天数与其他档位重复');
          return;
        }
        daysSeen[days] = true;
        if (rawPrice === '' || rawPrice === null || isNaN(price) || price <= 0) {
          this.$message.error('第 ' + (i + 1) + ' 档价格必须是大于 0 的数字');
          return;
        }
        tiers.push({ days: days, price: price });
      }
      if (!tiers.length) {
        this.$message.error('至少设置一个档位');
        return;
      }
      var data = Object.assign({}, this.formData, { tiersJson: JSON.stringify(tiers) });
      try {
        var res = !data.pauseRuleId
          ? await this.apis.vipPauseRule_save(data)
          : await this.apis.vipPauseRule_update(data);
        if (res && res.code === 0) {
          this.$message.success('操作成功');
          this.elFormVisible();
          this.getData();
        }
      } catch (e) { /* 失败已由响应拦截器弹错误提示 */ }
    },
    del(row) {
      this.$confirm('确定要删除该停卡规则吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.vipPauseRule_delete([row.pauseRuleId]);
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
