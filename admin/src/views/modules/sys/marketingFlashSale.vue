<template>
  <div>
    <!-- 顶部标题 + 副标题 -->
    <div class="page-head">
      <div class="page-title">限时秒杀</div>
      <div class="page-desc">配置私教商品限时秒杀：秒杀价（≤商品售价）、秒杀库存（必填，售罄不可买）、每人限购（默认1）、起止时间，支持上架/下架。已售数量由系统统计、只读。下架后不可继续购买。</div>
    </div>

    <r-search ref="search" :searchData="searchData" :searchForm="searchForm" :searchHandle="searchHandle" />
    <r-table
      :isSelection="true"
      :isHandle="true"
      :isPagination="true"
      :tableData="tableData"
      :tableCols="tableCols"
      :tablePage="pagination"
      :loading="tableLoading"
      :tableHandles="tableHandles"
      @select="handleSelect"
      @refresh="page()"
      ref="rtable" />

    <r-form
      labelWidth="130px"
      :isHandle="true"
      :formRules="formRules"
      :formCols="formCols"
      :formHandle="formHandle"
      :formData="formData"
      ref="elForm"
      :inline="false"
      dialogWidth="640px" />
  </div>
</template>

<script>
export default {
  components: {},
  data() {
    return {
      tableLoading: false,
      selection: [],
      productPriceMap: {},
      productNameMap: {},
      searchData: {
        activityName: '',
        status: '',
        productId: ''
      },
      searchForm: [
        { type: 'input', placeholder: '活动名称', prop: 'activityName', width: 180 },
        { type: 'select', placeholder: '关联商品', prop: 'productId', width: 200, options: [] },
        { type: 'select', placeholder: '状态', prop: 'status', width: 130, options: [
          { value: 1, label: '上架' },
          { value: 0, label: '下架' }
        ] }
      ],
      searchHandle: [
        { label: '搜索', type: 'primary', handle: e => this.getData() },
        { label: '重置', handle: e => this.reset() }
      ],
      tableHandles: [
        { label: '新增秒杀', type: 'primary', icon: 'el-icon-plus', isShow: e => this.checkBtn('sys:mkFlashSale:save'), handle: e => this.openAdd() },
        { label: '批量删除', type: 'danger', icon: 'el-icon-delete', isShow: e => this.checkBtn('sys:mkFlashSale:delete'), handle: e => this.delBatch() }
      ],
      tableData: [],
      tableCols: [
        { label: 'ID', prop: 'id', width: 70 },
        { label: '活动名称', prop: 'activityName' },
        { label: '关联商品', prop: 'productName', formatter: e => e.productName || this.productNameMap[e.productId] || ('#' + e.productId) },
        { label: '秒杀价', type: 'tag', prop: 'flashSalePrice', width: 100, theme: () => 'danger', formatter: e => '¥' + e.flashSalePrice },
        { label: '秒杀库存', prop: 'activityStock', width: 90 },
        { label: '已售数量', prop: 'soldCount', width: 90, formatter: e => (e.soldCount || 0) },
        { label: '每人限购', prop: 'purchaseLimit', width: 90, formatter: e => (e.purchaseLimit === null || e.purchaseLimit === undefined || e.purchaseLimit === '') ? '不限' : e.purchaseLimit },
        { label: '活动时间', prop: 'startTime', width: 300, formatter: e => this.timeRange(e) },
        {
          label: '状态',
          type: 'switch',
          prop: 'status',
          width: 90,
          values: [1, 0],
          change: (row) => this.changeStatus(row),
          isDisabled: e => !this.checkBtn('sys:mkFlashSale:update')
        },
        {
          label: '操作',
          type: 'button',
          width: 180,
          fixed: 'right',
          btnList: [
            { label: '编辑', type: 'success', size: 'mini', icon: 'el-icon-edit', isShow: e => this.checkBtn('sys:mkFlashSale:update'), handle: (row) => this.openEdit(row) },
            { label: '删除', type: 'danger', size: 'mini', icon: 'el-icon-delete', isShow: e => this.checkBtn('sys:mkFlashSale:delete'), handle: (row) => this.del(row) }
          ]
        }
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      formData: this.blankForm(),
      formCols: [
        { type: 'input', label: '活动名称', width: 350, prop: 'activityName' },
        { type: 'select', label: '关联商品', width: 350, prop: 'productId', options: [], change: (val) => this.onProductChange(val), remark: '' },
        { type: 'input', label: '秒杀价（元）', width: 350, prop: 'flashSalePrice', placeholder: '需 >0 且 ≤ 商品售价' },
        { type: 'input', label: '秒杀库存', width: 350, prop: 'activityStock', placeholder: '必填，>0' },
        { type: 'input', label: '每人限购', width: 350, prop: 'purchaseLimit', placeholder: '默认 1，留空=不限购' },
        { type: 'dateTime', label: '开始时间', width: 350, prop: 'startTime' },
        { type: 'dateTime', label: '结束时间', width: 350, prop: 'endTime' },
        { type: 'input', label: '美团团购ID', width: 350, prop: 'mtGroupBuyId', placeholder: '选填' },
        { type: 'input', label: '抖音团购ID', width: 350, prop: 'dyGroupBuyId', placeholder: '选填' },
        { type: 'radio', label: '状态', width: 350, prop: 'status', radios: [
          { value: 1, label: '上架' },
          { value: 0, label: '下架' }
        ] }
      ],
      formRules: {
        activityName: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
        productId: [{ required: true, message: '请选择关联商品', trigger: 'change' }],
        flashSalePrice: [{ required: true, message: '请输入秒杀价', trigger: 'blur' }],
        activityStock: [{ required: true, message: '请输入秒杀库存', trigger: 'blur' }],
        startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
      },
      formHandle: [
        { label: '确认', type: 'primary', icon: 'el-icon-circle-plus-outline', handle: e => this.elFormSubmit() },
        { label: '取消', icon: 'el-icon-circle-close', handle: e => this.elFormVisible() }
      ]
    };
  },
  mounted() {
    this.getData();
    this.getProductList();
  },
  methods: {
    // 表单初始值（sold_count 系统维护、不入表单；秒杀限购默认 1）
    blankForm() {
      return {
        id: '',
        activityName: '',
        productId: '',
        flashSalePrice: '',
        activityStock: '',
        purchaseLimit: 1,
        startTime: '',
        endTime: '',
        mtGroupBuyId: '',
        dyGroupBuyId: '',
        status: 1
      };
    },
    // ===== 列表展示辅助 =====
    timeRange(row) {
      var s = this.parseTime(row.startTime);
      var e = this.parseTime(row.endTime);
      return (s || '-') + ' ~ ' + (e || '-');
    },
    // ===== 列表 =====
    async getData() {
      this.tableLoading = true;
      try {
        var res = await this.apis.mkFlashSale_list(Object.assign({
          page: this.pagination.offset,
          limit: this.pagination.limit
        }, this.searchData));
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
    reset() {
      this.searchData = { activityName: '', status: '', productId: '' };
      this.pagination.offset = 1;
      this.getData();
    },
    handleSelect(rows) {
      this.selection = rows || [];
    },
    // ===== 下拉数据 =====
    async getProductList() {
      var res = await this.apis.ptProduct_list({ page: 1, limit: 999 });
      var list = (res.page && res.page.list) || [];
      var priceMap = {};
      var nameMap = {};
      var opts = list.map(function (item) {
        priceMap[item.id] = Number(item.salePrice);
        nameMap[item.id] = item.productName;
        return { value: item.id, label: item.productName + '（售价¥' + (item.salePrice === 0 || item.salePrice ? item.salePrice : '-') + '）' };
      });
      this.productPriceMap = priceMap;
      this.productNameMap = nameMap;
      this.searchForm[this.searchIndex(this.searchForm, '关联商品')].options = list.map(function (item) {
        return { value: item.id, label: item.productName };
      });
      this.formCols[this.labIndex(this.formCols, '关联商品')].options = opts;
    },
    onProductChange(val) {
      var price = this.productPriceMap[val];
      var idx = this.labIndex(this.formCols, '关联商品');
      this.formCols[idx].remark = (price === 0 || price) ? ('该商品售价 ¥' + price + '，秒杀价需 ≤ 此值') : '';
    },
    // ===== 新增 / 编辑 =====
    openAdd() {
      this.formData = this.blankForm();
      this.formCols[this.labIndex(this.formCols, '关联商品')].remark = '';
      this.elFormVisible('新增秒杀');
    },
    openEdit(row) {
      var form = this.blankForm();
      Object.keys(form).forEach(function (key) {
        if (row[key] !== undefined && row[key] !== null) form[key] = row[key];
      });
      form.id = row.id;
      this.formData = form;
      this.onProductChange(form.productId);
      this.elFormVisible('编辑秒杀');
    },
    elFormSubmit() {
      this.$refs.elForm.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.submit();
        }
      });
    },
    async submit() {
      var f = this.formData;
      var price = Number(f.flashSalePrice);
      if (!(price > 0)) {
        this.$message.error('秒杀价需大于 0');
        return;
      }
      var salePrice = this.productPriceMap[f.productId];
      if ((salePrice === 0 || salePrice) && price > salePrice) {
        this.$message.error('秒杀价（¥' + price + '）不能高于商品售价（¥' + salePrice + '）');
        return;
      }
      // 秒杀库存必填且 >0
      if (f.activityStock === '' || f.activityStock === null || !(Number(f.activityStock) > 0)) {
        this.$message.error('秒杀库存必填且需大于 0');
        return;
      }
      if (f.purchaseLimit !== '' && f.purchaseLimit !== null && !(Number(f.purchaseLimit) > 0)) {
        this.$message.error('每人限购需为正整数（留空表示不限购）');
        return;
      }
      if (!f.startTime || !f.endTime) {
        this.$message.error('请选择活动起止时间');
        return;
      }
      if (f.startTime >= f.endTime) {
        this.$message.error('开始时间需早于结束时间');
        return;
      }
      var data = Object.assign({}, f);
      data.activityStock = Number(f.activityStock);
      data.purchaseLimit = (f.purchaseLimit === '' || f.purchaseLimit === null) ? null : Number(f.purchaseLimit);
      try {
        var res = !data.id
          ? await this.apis.mkFlashSale_save(data)
          : await this.apis.mkFlashSale_update(data);
        if (res && res.code === 0) {
          this.$message.success('操作成功');
          this.elFormVisible();
          this.getData();
        }
      } catch (e) { /* 失败已由响应拦截器弹错误提示 */ }
    },
    // ===== 状态开关（乐观翻转，失败回滚）=====
    async changeStatus(row) {
      try {
        var res = await this.apis.mkFlashSale_changeStatus({ id: row.id, status: row.status });
        if (res && res.code === 0) {
          this.$message.success(row.status === 1 ? '已上架' : '已下架');
        } else {
          row.status = row.status === 1 ? 0 : 1;
        }
      } catch (e) {
        row.status = row.status === 1 ? 0 : 1;
      }
    },
    // ===== 删除 =====
    del(row) {
      this.$confirm('确定要删除秒杀活动【' + (row.activityName || '') + '】吗？已产生关联订单的活动建议下架而非删除。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.mkFlashSale_delete([row.id]);
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('删除成功');
          this.getData();
        }
      }).catch(() => {});
    },
    delBatch() {
      if (!this.selection.length) {
        this.$message.warning('请先勾选要删除的活动');
        return;
      }
      var ids = this.selection.map(function (r) { return r.id; });
      this.$confirm('确定要批量删除选中的 ' + ids.length + ' 个秒杀活动吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.mkFlashSale_delete(ids);
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('删除成功');
          this.getData();
        }
      }).catch(() => {});
    }
  }
};
</script>

<style scoped lang="scss">
.page-head {
  margin-bottom: 16px;
}
.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.page-desc {
  margin-top: 4px;
  font-size: 13px;
  color: #909399;
}
</style>
