<template>
  <div>
    <!-- 顶部标题 + 副标题 -->
    <div class="page-head">
      <div class="page-title">优惠券</div>
      <div class="page-desc">维护私教商品优惠券（满减 / 折扣）：适用门店与商品范围、使用门槛、有效天数、新人券，支持上架/下架与后台手动发券。券状态 1 上架 / 0 下架；下架不影响已领券使用到期。</div>
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
      // storeId -> storeName / productId -> productName（列表展示与回显用）
      storeOptions: [],
      productOptions: [],
      searchData: {
        couponName: '',
        couponType: '',
        status: '',
        isNewUserCoupon: ''
      },
      searchForm: [
        { type: 'input', placeholder: '券名称', prop: 'couponName', width: 180 },
        { type: 'select', placeholder: '券类型', prop: 'couponType', width: 140, options: [
          { value: 1, label: '满减券' },
          { value: 2, label: '折扣券' }
        ] },
        { type: 'select', placeholder: '状态', prop: 'status', width: 130, options: [
          { value: 1, label: '上架' },
          { value: 0, label: '下架' }
        ] },
        { type: 'select', placeholder: '是否新人券', prop: 'isNewUserCoupon', width: 150, options: [
          { value: 1, label: '是' },
          { value: 0, label: '否' }
        ] }
      ],
      searchHandle: [
        { label: '搜索', type: 'primary', handle: e => this.getData() },
        { label: '重置', handle: e => this.reset() }
      ],
      tableHandles: [
        { label: '新增优惠券', type: 'primary', icon: 'el-icon-plus', isShow: e => this.checkBtn('sys:mkCoupon:save'), handle: e => this.openAdd() },
        { label: '批量删除', type: 'danger', icon: 'el-icon-delete', isShow: e => this.checkBtn('sys:mkCoupon:delete'), handle: e => this.delBatch() }
      ],
      tableData: [],
      tableCols: [
        { label: 'ID', prop: 'id', width: 70 },
        { label: '券名称', prop: 'couponName' },
        { label: '券类型', type: 'tag', prop: 'couponType', width: 90, theme: (row) => row.couponType === 1 ? 'info' : 'warning', formatter: e => e.couponType === 1 ? '满减券' : '折扣券' },
        { label: '优惠', prop: 'discountAmount', width: 150, formatter: e => this.discountSummary(e) },
        { label: '使用门槛', prop: 'useThresholdAmount', width: 100, formatter: e => this.thresholdSummary(e) },
        { label: '有效天数', prop: 'validDays', width: 90, formatter: e => (e.validDays || 0) + '天' },
        { label: '可用门店', prop: 'storeList', width: 160, formatter: e => this.storeSummary(e) },
        { label: '适用范围', prop: 'scopeType', width: 90, formatter: e => e.scopeType === 2 ? '指定商品' : '全部商品' },
        { label: '新人券', prop: 'isNewUserCoupon', width: 80, formatter: e => e.isNewUserCoupon === 1 ? '是' : '否' },
        {
          label: '状态',
          type: 'switch',
          prop: 'status',
          width: 90,
          values: [1, 0],
          change: (row) => this.changeStatus(row),
          isDisabled: e => !this.checkBtn('sys:mkCoupon:update')
        },
        { label: '创建时间', prop: 'createdAt', width: 160, formatter: e => this.parseTime(e.createdAt) },
        {
          label: '操作',
          type: 'button',
          width: 240,
          fixed: 'right',
          btnList: [
            { label: '编辑', type: 'success', size: 'mini', icon: 'el-icon-edit', isShow: e => this.checkBtn('sys:mkCoupon:update'), handle: (row) => this.openEdit(row) },
            { label: '发券', type: 'primary', size: 'mini', icon: 'el-icon-upload', isShow: (row) => row.status === 1 && this.checkBtn('sys:mkCoupon:grant'), handle: (row) => this.openGrant(row) },
            { label: '删除', type: 'danger', size: 'mini', icon: 'el-icon-delete', isShow: e => this.checkBtn('sys:mkCoupon:delete'), handle: (row) => this.del(row) }
          ]
        }
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      // ===== r-form：新增/编辑 与 发券 共用同一个弹窗，进入前切换配置 =====
      formData: this.blankForm(),
      formCols: [],
      formRules: {},
      formHandle: [],
      // 优惠券新增/编辑配置
      couponForm: {
        formData: this.blankForm(),
        formCols: [
          { type: 'input', label: '券名称', width: 350, prop: 'couponName' },
          { type: 'radio', label: '券类型', width: 350, prop: 'couponType', radios: [
            { value: 1, label: '满减券' },
            { value: 2, label: '折扣券' }
          ] },
          // 满减券：优惠金额（券类型=1时显示）
          { type: 'input', label: '优惠金额（元）', width: 350, prop: 'discountAmount', placeholder: '满减券必填，>0', isShow: () => Number(this.formData.couponType) === 1 },
          // 折扣券：折扣值 + 最高优惠（券类型=2时显示）
          { type: 'input', label: '折扣值', width: 350, prop: 'discountRate', placeholder: '折扣券必填，8.50=8.5折', isShow: () => Number(this.formData.couponType) === 2 },
          { type: 'input', label: '最高优惠金额（元）', width: 350, prop: 'maxDiscountAmount', placeholder: '折扣券选填，封顶金额', isShow: () => Number(this.formData.couponType) === 2 },
          { type: 'input', label: '使用门槛（元）', width: 350, prop: 'useThresholdAmount', placeholder: '选填，0或不填=无门槛' },
          { type: 'input', label: '有效天数', width: 350, prop: 'validDays', placeholder: '领券后有效天数，≥1' },
          { type: 'select', label: '可用门店', width: 350, prop: 'storeIds', multiple: true, options: [] },
          { type: 'radio', label: '适用范围', width: 350, prop: 'scopeType', radios: [
            { value: 1, label: '全部商品' },
            { value: 2, label: '指定商品' }
          ] },
          // 指定商品（适用范围=2时显示）
          { type: 'select', label: '指定商品', width: 350, prop: 'productIds', multiple: true, options: [], isShow: () => Number(this.formData.scopeType) === 2 },
          { type: 'radio', label: '新人券', width: 350, prop: 'isNewUserCoupon', radios: [
            { value: 1, label: '是' },
            { value: 0, label: '否' }
          ] },
          { type: 'input', label: '美团团购ID', width: 350, prop: 'mtGroupBuyId', placeholder: '选填' },
          { type: 'input', label: '抖音团购ID', width: 350, prop: 'dyGroupBuyId', placeholder: '选填' },
          { type: 'radio', label: '状态', width: 350, prop: 'status', radios: [
            { value: 1, label: '上架' },
            { value: 0, label: '下架' }
          ] }
        ],
        formRules: {
          couponName: [{ required: true, message: '请输入券名称', trigger: 'blur' }],
          couponType: [{ required: true, message: '请选择券类型', trigger: 'change' }],
          validDays: [{ required: true, message: '请输入有效天数', trigger: 'blur' }],
          storeIds: [{ required: true, type: 'array', min: 1, message: '请至少选择一个可用门店', trigger: 'change' }]
        },
        formHandle: [
          { label: '确认', type: 'primary', icon: 'el-icon-circle-plus-outline', handle: e => this.elFormSubmit() },
          { label: '取消', icon: 'el-icon-circle-close', handle: e => this.elFormVisible() }
        ]
      },
      // 发券配置：券名（只读）+ 会员ID列表
      grantForm: {
        formData: { id: '', couponName: '', memberIdsText: '' },
        formCols: [
          { type: 'input', label: '优惠券', width: 350, prop: 'couponName', isDisabled: () => true },
          { type: 'textarea', label: '会员ID列表', width: 350, prop: 'memberIdsText', placeholder: '多个会员ID用逗号、空格或换行分隔' }
        ],
        formRules: {
          memberIdsText: [{ required: true, message: '请输入会员ID列表', trigger: 'blur' }]
        },
        formHandle: [
          { label: '发放', type: 'primary', icon: 'el-icon-circle-plus-outline', handle: e => this.grantSubmit() },
          { label: '取消', icon: 'el-icon-circle-close', handle: e => this.elFormVisible() }
        ]
      }
    };
  },
  mounted() {
    this.getData();
    this.getStoreList();
    this.getProductList();
  },
  methods: {
    // 优惠券表单初始值（sold/发放记录由系统维护，不入表单）
    blankForm() {
      return {
        id: '',
        couponName: '',
        couponType: 1,
        discountAmount: '',
        discountRate: '',
        maxDiscountAmount: '',
        useThresholdAmount: '',
        validDays: 30,
        storeIds: [],
        scopeType: 1,
        productIds: [],
        isNewUserCoupon: 0,
        mtGroupBuyId: '',
        dyGroupBuyId: '',
        status: 1
      };
    },
    // ===== 列表展示辅助 =====
    discountSummary(row) {
      if (row.couponType === 1) {
        return '减¥' + (row.discountAmount || 0);
      }
      var s = (row.discountRate || 0) + '折';
      if (row.maxDiscountAmount !== null && row.maxDiscountAmount !== undefined && row.maxDiscountAmount !== '') {
        s += '·最高¥' + row.maxDiscountAmount;
      }
      return s;
    },
    thresholdSummary(row) {
      var t = Number(row.useThresholdAmount);
      return t > 0 ? ('满¥' + row.useThresholdAmount) : '无门槛';
    },
    storeSummary(row) {
      // 后端 list 行返回 storeList（[{storeId,storeName}] 或名称数组）
      var list = row.storeList || [];
      if (!list.length) return '-';
      var names = list.map(function (s) { return s.storeName || s.name || s; });
      if (names.length <= 2) return names.join('、');
      return names.slice(0, 2).join('、') + ' +' + (names.length - 2);
    },
    // ===== 列表 =====
    async getData() {
      this.tableLoading = true;
      try {
        var res = await this.apis.mkCoupon_list(Object.assign({
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
      this.searchData = { couponName: '', couponType: '', status: '', isNewUserCoupon: '' };
      this.pagination.offset = 1;
      this.getData();
    },
    handleSelect(rows) {
      this.selection = rows || [];
    },
    // ===== 下拉数据 =====
    async getStoreList() {
      var res = await this.apis.store_list({ page: 1, limit: 999 });
      var list = (res.page && res.page.list) || [];
      var opts = list.map(function (item) {
        return { value: item.storeAddrId, label: item.storeName };
      });
      this.storeOptions = opts;
      this.couponForm.formCols[this.labIndex(this.couponForm.formCols, '可用门店')].options = opts;
    },
    async getProductList() {
      var res = await this.apis.ptProduct_list({ page: 1, limit: 999 });
      var list = (res.page && res.page.list) || [];
      var opts = list.map(function (item) {
        return { value: item.id, label: item.productName };
      });
      this.productOptions = opts;
      this.couponForm.formCols[this.labIndex(this.couponForm.formCols, '指定商品')].options = opts;
    },
    // ===== 新增 / 编辑 =====
    useCouponForm() {
      this.formCols = this.couponForm.formCols;
      this.formRules = this.couponForm.formRules;
      this.formHandle = this.couponForm.formHandle;
    },
    openAdd() {
      this.useCouponForm();
      this.formData = this.blankForm();
      this.elFormVisible('新增优惠券');
    },
    openEdit(row) {
      this.useCouponForm();
      var form = this.blankForm();
      // 先用列表行回填基础字段
      Object.keys(form).forEach(function (key) {
        if (row[key] !== undefined && row[key] !== null) form[key] = row[key];
      });
      form.id = row.id;
      form.storeIds = Array.isArray(row.storeIds) ? row.storeIds.slice() : [];
      form.productIds = Array.isArray(row.productIds) ? row.productIds.slice() : [];
      this.formData = form;
      this.elFormVisible('编辑优惠券');
      // 详情补全门店/商品关联（列表行可能不含 storeIds/productIds）
      this.apis.mkCoupon_info({ id: row.id }).then((res) => {
        if (res && res.code === 0 && res.entity) {
          var e = res.entity;
          var f = Object.assign({}, this.formData);
          Object.keys(this.blankForm()).forEach(function (key) {
            if (e[key] !== undefined && e[key] !== null) f[key] = e[key];
          });
          f.id = row.id;
          f.storeIds = Array.isArray(e.storeIds) ? e.storeIds.slice() : f.storeIds;
          f.productIds = Array.isArray(e.productIds) ? e.productIds.slice() : f.productIds;
          this.formData = f;
        }
      });
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
      // 前端补充校验：满减/折扣二选一
      if (Number(f.couponType) === 1) {
        if (!(Number(f.discountAmount) > 0)) {
          this.$message.error('满减券：优惠金额必填且需大于 0');
          return;
        }
      } else if (Number(f.couponType) === 2) {
        var rate = Number(f.discountRate);
        if (!(rate > 0 && rate < 10)) {
          this.$message.error('折扣券：折扣值必填且需在 0~10 之间（如 8.50=8.5折）');
          return;
        }
        if (f.maxDiscountAmount !== '' && f.maxDiscountAmount !== null && !(Number(f.maxDiscountAmount) > 0)) {
          this.$message.error('折扣券：最高优惠金额填写时需大于 0');
          return;
        }
      }
      if (!(Number(f.validDays) >= 1)) {
        this.$message.error('有效天数需 ≥ 1');
        return;
      }
      if (Number(f.scopeType) === 2 && (!f.productIds || !f.productIds.length)) {
        this.$message.error('适用范围为「指定商品」时，请至少选择一个商品');
        return;
      }
      var data = Object.assign({}, f);
      // 满减/折扣互斥字段清理
      if (Number(f.couponType) === 1) {
        data.discountRate = null;
        data.maxDiscountAmount = null;
      } else {
        data.discountAmount = null;
      }
      // 全部商品时清空指定商品
      if (Number(f.scopeType) === 1) {
        data.productIds = [];
      }
      try {
        var res = !data.id
          ? await this.apis.mkCoupon_save(data)
          : await this.apis.mkCoupon_update(data);
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
        var res = await this.apis.mkCoupon_changeStatus({ id: row.id, status: row.status });
        if (res && res.code === 0) {
          this.$message.success(row.status === 1 ? '已上架' : '已下架');
        } else {
          row.status = row.status === 1 ? 0 : 1;
        }
      } catch (e) {
        row.status = row.status === 1 ? 0 : 1;
      }
    },
    // ===== 发券 =====
    openGrant(row) {
      this.grantForm.formData.id = row.id;
      this.grantForm.formData.couponName = row.couponName;
      this.grantForm.formData.memberIdsText = '';
      this.formData = this.grantForm.formData;
      this.formCols = this.grantForm.formCols;
      this.formRules = this.grantForm.formRules;
      this.formHandle = this.grantForm.formHandle;
      // 弹窗当前为关闭态：此时 r-form 内 ruleForm 不存在，mixin 的重置分支会被跳过，
      // 故直接调用 elFormVisible 打开即可保留刚赋值的 grantForm.formData（与 openEdit 同理）
      this.elFormVisible('发放优惠券');
    },
    grantSubmit() {
      this.$refs.elForm.$refs.ruleForm.validate((valid) => {
        if (!valid) return;
        var text = this.formData.memberIdsText || '';
        var ids = text.split(/[\s,，、]+/).map(function (x) { return (x || '').trim(); }).filter(function (x) { return x; });
        ids = ids.map(function (x) { return x * 1; }).filter(function (x) { return x && !isNaN(x); });
        if (!ids.length) {
          this.$message.error('请输入有效的会员ID');
          return;
        }
        this.$confirm('确定要给这 ' + ids.length + ' 个会员发放优惠券【' + this.formData.couponName + '】吗？（本期允许重复发放）', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          return this.apis.mkCoupon_grant({ couponId: this.formData.id, memberIds: ids });
        }).then((res) => {
          if (res && res.code === 0) {
            var n = res.grantCount !== undefined ? res.grantCount : ids.length;
            this.$message.success('发放成功，共发放 ' + n + ' 张');
            this.elFormVisible();
          }
        }).catch(() => {});
      });
    },
    // ===== 删除 =====
    del(row) {
      this.$confirm('确定要删除优惠券【' + (row.couponName || '') + '】吗？已有领取记录的券建议下架而非删除。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.mkCoupon_delete([row.id]);
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('删除成功');
          this.getData();
        }
      }).catch(() => {});
    },
    delBatch() {
      if (!this.selection.length) {
        this.$message.warning('请先勾选要删除的优惠券');
        return;
      }
      var ids = this.selection.map(function (r) { return r.id; });
      this.$confirm('确定要批量删除选中的 ' + ids.length + ' 张优惠券吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.mkCoupon_delete(ids);
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
