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
    <r-form labelWidth="150px" :isHandle="true" :formRules="formRules" :formCols="formCols" :formHandle="formHandle" :formData="formData" ref="elForm" :inline="false" dialogWidth="700px" />
  </div>
</template>

<script>
export default {
  components: {},
  data() {
    return {
      tableLoading: false,
      // storeAddrId -> storeName，用于列表「适用门店」列展示
      storeMap: {},
      searchData: {
        cardName: '',
        status: ''
      },
      searchForm: [
        { type: "input", placeholder: "权益卡名称", prop: "cardName", width: 200 },
        { type: "select", placeholder: "状态", prop: "status", width: 160, options: [
          { value: 1, label: '上架' },
          { value: 2, label: '下架' }
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
        { label: "ID", prop: "vipCardId", width: 70 },
        { label: "权益卡名称", prop: "cardName" },
        { label: "售价", prop: "price", formatter: e => '¥' + e.price },
        { label: "有效天数", prop: "validityDays" },
        { label: "适用门店", prop: "storeAddrIds", formatter: e => this.storeNames(e.storeAddrIds) },
        { label: "显示购买人数", prop: "showBuyCount", formatter: e => e.showBuyCount == 1 ? '是' : '否' },
        { label: "动态定价", prop: "stepNum", formatter: e => this.dynamicSummary(e) },
        {
          label: "状态",
          type: "switch",
          prop: "status",
          values: [1, 2],
          change: (row) => this.changeStatus(row)
        },
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
      formData: this.blankForm(),
      formCols: [
        { type: "input", label: "权益卡名称", width: 350, prop: "cardName" },
        { type: "textarea", label: "权益内容描述", width: 350, prop: "benefitDesc" },
        { type: "input", label: "售价（元）", width: 350, prop: "price" },
        { type: "input", label: "有效天数", width: 350, prop: "validityDays" },
        { type: "select", label: "适用门店", width: 350, prop: "storeAddrIds", multiple: true },
        { type: "switch", label: "显示实时购买人数", prop: "showBuyCount", values: [1, 0] },
        { type: "input", label: "展示基数/首发起算人数", width: 350, prop: "baseBuyCount" },
        { type: "input", label: "每多少人涨价", width: 350, prop: "stepNum", placeholder: "0=不动态涨价" },
        { type: "input", label: "每档加价（元）", width: 350, prop: "stepAddPrice" },
        { type: "input", label: "封顶价（元）", width: 350, prop: "priceCap", placeholder: "留空=不封顶" },
        { type: "select", label: "关联转让费用规则", width: 350, prop: "feeRuleId" },
        { type: "radio", label: "状态", width: 350, prop: "status", radios: [
          { value: 1, label: '上架' },
          { value: 2, label: '下架' }
        ] }
      ],
      formRules: {
        cardName: [
          { required: true, message: '请输入权益卡名称', trigger: 'blur' },
        ],
        price: [
          { required: true, message: '请输入售价', trigger: 'blur' },
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
    this.getStoreList();
    this.getFeeRuleList();
  },
  methods: {
    // 表单初始值（sold_count 系统维护、不入表单）
    blankForm() {
      return {
        vipCardId: '',
        cardName: '',
        benefitDesc: '',
        price: '',
        validityDays: 365,
        storeAddrIds: [],
        feeRuleId: '',
        showBuyCount: 1,
        baseBuyCount: '',
        stepNum: '',
        stepAddPrice: '',
        priceCap: '',
        status: 1
      };
    },
    // 适用门店 id 数组 -> 门店名（空=全部门店）
    storeNames(ids) {
      if (!ids || !ids.length) return '全部门店';
      var self = this;
      return ids.map(function (id) { return self.storeMap[id] || id; }).join('、');
    },
    // 动态定价摘要：每N人+M/封顶¥X 或 未开启
    dynamicSummary(row) {
      var step = Number(row.stepNum);
      var add = Number(row.stepAddPrice);
      if (step > 0 && add > 0) {
        var s = '每' + step + '人+' + add;
        return row.priceCap !== null && row.priceCap !== undefined && row.priceCap !== ''
          ? s + '/封顶¥' + row.priceCap
          : s + '/不封顶';
      }
      return '未开启';
    },
    async getData() {
      this.tableLoading = true;
      try {
        var res = await this.apis.vipCard_list(Object.assign({
          page: this.pagination.offset,
          limit: this.pagination.limit
        }, this.searchData));
        var list = (res.pages && res.pages.list) || [];
        list.forEach(function (r) {
          // storeAddrIds 字符串 -> 数字数组（供多选回显）
          r.storeAddrIds = r.storeAddrIds ? r.storeAddrIds.split(',').map(function (x) { return x * 1; }) : [];
        });
        this.tableData = list;
        this.pagination.total = res.pages ? res.pages.totalCount : 0;
      } finally {
        this.tableLoading = false;
      }
    },
    page() {
      this.getData();
    },
    // 适用门店下拉（门店地址）
    async getStoreList() {
      var res = await this.apis.store_list({ page: 1, limit: 999 });
      var list = (res.page && res.page.list) || [];
      var map = {};
      list.forEach(function (item) {
        item.value = item.storeAddrId;
        item.label = item.storeName;
        map[item.storeAddrId] = item.storeName;
      });
      this.storeMap = map;
      this.formCols[this.labIndex(this.formCols, '适用门店')].options = list;
    },
    // 关联转让费用规则下拉
    async getFeeRuleList() {
      var res = await this.apis.vipFeeRule_list({ page: 1, limit: 999 });
      var list = (res.pages && res.pages.list) || [];
      var opts = list.map(function (r) { return { value: r.feeRuleId, label: r.ruleName }; });
      opts.unshift({ value: '', label: '不关联（转让免费）' });
      this.formCols[this.labIndex(this.formCols, '关联转让费用规则')].options = opts;
    },
    openAdd() {
      this.formData = this.blankForm();
      this.elFormVisible();
    },
    elFormDetail(row) {
      var form = this.blankForm();
      Object.keys(form).forEach(function (key) {
        if (row[key] !== undefined) form[key] = row[key];
      });
      // storeAddrIds 在 getData 已转成数组；feeRuleId 为空归一成 ''
      form.storeAddrIds = Array.isArray(row.storeAddrIds) ? row.storeAddrIds.slice() : [];
      form.feeRuleId = (row.feeRuleId === null || row.feeRuleId === undefined) ? '' : row.feeRuleId;
      this.formData = form;
      this.elFormVisible('编辑');
    },
    elFormSubmit() {
      this.$refs.elForm.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.submit();
        }
      });
    },
    async submit() {
      var data = Object.assign({}, this.formData);
      data.storeAddrIds = (data.storeAddrIds || []).join(',');
      try {
        var res = !data.vipCardId
          ? await this.apis.vipCard_save(data)
          : await this.apis.vipCard_update(data);
        if (res && res.code === 0) {
          this.$message.success('操作成功');
          this.elFormVisible();
          this.getData();
        }
      } catch (e) { /* 失败已由响应拦截器弹错误提示 */ }
    },
    // 状态开关 -> 上架/下架专用接口（sold_count 不受影响）
    // el-switch 用 v-model 绑 row.status，触发 change 前已乐观翻到新值；失败须回滚，否则 UI 与后端脱钩
    async changeStatus(row) {
      try {
        var res = row.status === 1
          ? await this.apis.vipCard_onCard([row.vipCardId])
          : await this.apis.vipCard_offCard([row.vipCardId]);
        if (res && res.code === 0) {
          this.$message.success(row.status === 1 ? '已上架' : '已下架');
        } else {
          row.status = row.status === 1 ? 2 : 1; // 非成功，回滚开关
        }
      } catch (e) {
        row.status = row.status === 1 ? 2 : 1; // 异常/拒绝，回滚开关（错误提示已由响应拦截器弹出）
      }
    },
    del(row) {
      this.$confirm('确定要删除该权益卡吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.vipCard_delete([row.vipCardId]);
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
</style>
