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
    <r-form labelWidth="130px" :isHandle="true" :formRules="formRules" :formCols="formCols" :formHandle="formHandle" :formData="formData" ref="elForm" :inline="false" dialogWidth="640px" />
  </div>
</template>

<script>
export default {
  components: {},
  data () {
    return {
      tableLoading: false,
      coachMap: {},
      storeMap: {},
      productMap: {},
      searchData: {
        coachName: '',
        ruleType: '',
        storeId: '',
        status: ''
      },
      searchForm: [
        { type: 'input', placeholder: '教练姓名', prop: 'coachName', width: 160 },
        { type: 'select',
          placeholder: '提成方式',
          prop: 'ruleType',
          width: 150,
          options: [
          { value: 1, label: '按课时提成' },
          { value: 2, label: '整单提成' }
          ] },
        { type: 'select', placeholder: '适用门店', prop: 'storeId', width: 180, options: [] },
        { type: 'select',
          placeholder: '状态',
          prop: 'status',
          width: 140,
          options: [
          { value: 1, label: '启用' },
          { value: 0, label: '停用' }
          ] }
      ],
      searchHandle: [
        { label: '搜索', type: 'primary', handle: e => this.getData() },
        { label: '重置', handle: e => this.reset() }
      ],
      tableHandles: [
        { label: '新增分成规则', type: 'primary', icon: 'el-icon-plus', isShow: () => this.checkBtn('sys:commission:save'), handle: e => this.openAdd() }
      ],
      tableData: [],
      tableCols: [
        { label: 'ID', prop: 'id', width: 70 },
        { label: '规则名称', prop: 'ruleName' },
        { label: '教练', prop: 'coachName', width: 100, formatter: e => e.coachName || this.coachMap[e.coachId] || ('#' + e.coachId) },
        { label: '适用门店', prop: 'storeId', width: 130, formatter: e => e.storeId ? (e.storeName || this.storeMap[e.storeId] || ('#' + e.storeId)) : '全部门店' },
        { label: '适用课程', prop: 'productId', width: 140, formatter: e => e.productId ? (e.productName || this.productMap[e.productId] || ('#' + e.productId)) : '全部课程' },
        { label: '提成方式', type: 'tag', width: 180, prop: 'ruleType', theme: (row) => row.ruleType === 1 ? '' : 'warning', formatter: e => this.ruleTypeText(e) },
        { label: '提成比例', prop: 'commissionRate', width: 160, formatter: e => this.amountText(e) },
        { label: '生效时间', prop: 'effectiveTime', width: 160, formatter: e => e.effectiveTime ? this.parseTime(e.effectiveTime) : '立即生效' },
        {
          label: '状态',
          type: 'switch',
          width: 100,
          prop: 'status',
          values: [1, 0],
          isDisabled: () => !this.checkBtn('sys:commission:changeStatus'),
          change: (row) => this.changeStatus(row)
        },
        {
          label: '操作',
          type: 'button',
          width: 150,
          fixed: 'right',
          btnList: [
            { label: '编辑', type: 'success', size: 'mini', icon: 'el-icon-edit', isShow: () => this.checkBtn('sys:commission:update'), handle: (row) => this.elFormDetail(row) },
            { label: '删除', type: 'danger', size: 'mini', icon: 'el-icon-delete', isShow: () => this.checkBtn('sys:commission:delete'), handle: (row) => this.del(row) }
          ]
        }
      ],
      pagination: { limit: 10, offset: 1, total: 0 },
      formData: this.blankForm(),
      formCols: [
        { type: 'input', label: '规则名称', width: 320, prop: 'ruleName' },
        { type: 'select', label: '教练', width: 320, prop: 'coachId', options: [] },
        { type: 'select', label: '适用门店', width: 320, prop: 'storeIds', multiple: true, options: [], placeholder: '留空=全部门店' },
        { type: 'select', label: '适用课程', width: 320, prop: 'productIds', multiple: true, options: [], placeholder: '留空=全部非包月课程' },
        { type: 'radio',
          label: '提成方式',
          prop: 'ruleType',
          radios: [
          { value: 1, label: '按课时提成' },
          { value: 2, label: '整单提成' }
          ] },
        { type: 'input', label: '提成比例（%）', width: 320, prop: 'commissionRate', placeholder: '请输入 0-100 之间的数字' },
        { type: 'dateTime', label: '生效时间', width: 320, prop: 'effectiveTime', placeholder: '留空=立即生效' },
        { type: 'radio',
          label: '规则状态',
          prop: 'status',
          radios: [
          { value: 1, label: '启用' },
          { value: 0, label: '停用' }
          ] }
      ],
      formRules: {
        ruleName: [
          { required: true, message: '请输入规则名称', trigger: 'blur' }
        ],
        coachId: [
          { required: true, message: '请选择教练', trigger: 'change' }
        ],
        ruleType: [
          { required: true, message: '请选择提成方式', trigger: 'change' }
        ]
      },
      formHandle: [
        { label: '确认', type: 'primary', icon: 'el-icon-circle-plus-outline', handle: e => this.elFormSubmit() },
        { label: '取消', icon: 'el-icon-circle-close', handle: e => this.elFormVisible() }
      ]
    }
  },
  mounted () {
    this.getData()
    this.getStoreList()
    this.getRuleOptions()
  },
  methods: {
    blankForm () {
      return {
        id: '',
        ruleName: '',
        coachId: '',
        storeIds: [],
        productIds: [],
        ruleType: 1,
        lessonFee: '',
        commissionRate: '',
        effectiveTime: '',
        status: 1
      }
    },
    amountText (row) {
      if (row.commissionRate != null && Number(row.commissionRate) > 0) {
        return row.commissionRate + '%'
      }
      // 兼容历史固定课时费数据；编辑保存后会转换为百分比规则。
      if (row.ruleType === 1 && row.lessonFee != null && Number(row.lessonFee) > 0) {
        return '历史固定 ¥' + row.lessonFee + '/节（待改比例）'
      }
      return '待配置'
    },
    ruleTypeText (row) {
      var text = row.ruleType === 1 ? '按课时提成' : '整单提成'
      return Number(row.conflictCount) > 0 ? text + '（冲突，请停用一种）' : text
    },
    async getData () {
      this.tableLoading = true
      try {
        var res = await this.apis.commission_list({
          page: this.pagination.offset,
          limit: this.pagination.limit,
          coachName: this.searchData.coachName,
          ruleType: this.searchData.ruleType,
          storeId: this.searchData.storeId,
          status: this.searchData.status
        })
        var page = res.page || {}
        this.tableData = page.list || []
        this.pagination.total = page.totalCount || 0
      } finally {
        this.tableLoading = false
      }
    },
    page () {
      this.getData()
    },
    reset () {
      this.searchData = { coachName: '', ruleType: '', storeId: '', status: '' }
      this.pagination.offset = 1
      this.getData()
    },
    async getStoreList () {
      var res = await this.apis.store_list({ page: 1, limit: 999 })
      var list = (res.page && res.page.list) || []
      var map = {}
      var opts = list.map(function (item) {
        map[item.storeAddrId] = item.storeName
        return { value: item.storeAddrId, label: item.storeName }
      })
      this.storeMap = map
      this.searchForm[this.searchIndex(this.searchForm, '适用门店')].options = opts
      this.formCols[this.labIndex(this.formCols, '适用门店')].options = opts
    },
    async getRuleOptions () {
      var res = await this.apis.commission_options()
      var list = res.coaches || []
      var map = {}
      var opts = list.map(function (item) {
        map[item.id] = item.coachName
        return { value: item.id, label: item.coachName }
      })
      this.coachMap = map
      this.formCols[this.labIndex(this.formCols, '教练')].options = opts
      // 包月课程由教练资料中的专用提成配置维护，普通规则页面不允许选择。
      var products = (res.products || []).filter(function (item) {
        return Number(item.productTypeId) !== 3
      })
      var productMap = {}
      var productOpts = products.map(function (item) {
        productMap[item.id] = item.productName
        return { value: item.id, label: item.productName }
      })
      this.productMap = productMap
      this.formCols[this.labIndex(this.formCols, '适用课程')].options = productOpts
    },
    openAdd () {
      this.formData = this.blankForm()
      this.elFormVisible()
    },
    elFormDetail (row) {
      var form = this.blankForm()
      Object.keys(form).forEach(function (key) {
        if (row[key] !== undefined && row[key] !== null) form[key] = row[key]
      })
      // 单条规则回显：门店/课程用其单值填入多选数组（0 视为全部=空数组）
      form.storeIds = (row.storeId && row.storeId !== 0) ? [row.storeId] : []
      form.productIds = (row.productId && row.productId !== 0) ? [row.productId] : []
      form.effectiveTime = row.effectiveTime ? this.parseTime(row.effectiveTime) : ''
      this.formData = form
      this.elFormVisible('编辑')
    },
    elFormSubmit () {
      this.$refs.elForm.$refs.ruleForm.validate((valid) => {
        if (!valid) return
        var rate = Number(this.formData.commissionRate)
        if (this.formData.commissionRate === '' || rate <= 0 || rate > 100) {
          this.$message.error('提成比例需在 0-100 之间')
          return
        }
        var message = this.formData.id
          ? '确定保存当前单条分成规则的修改？'
          : '将按「门店 × 课程」组合生成多条规则，确定保存？'
        this.$confirm(message, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'info'
        }).then(() => {
          this.submit()
        }).catch(() => {})
      })
    },
    async submit () {
      var data = Object.assign({}, this.formData)
      if (data.id) {
        if (data.storeIds.length > 1 || data.productIds.length > 1) {
          this.$message.error('编辑单条规则时，门店和课程最多各选一个；多组合请使用新增')
          return
        }
        data.storeId = data.storeIds.length ? data.storeIds[0] : 0
        data.productId = data.productIds.length ? data.productIds[0] : 0
      }
      data.commissionRate = Number(data.commissionRate)
      data.lessonFee = 0
      if (!data.effectiveTime) data.effectiveTime = null
      try {
        var res = !data.id
          ? await this.apis.commission_save(data)
          : await this.apis.commission_update(data)
        if (res && res.code === 0) {
          this.$message.success('操作成功')
          this.elFormVisible()
          this.getData()
        }
      } catch (e) { /* 失败已由响应拦截器弹错误提示 */ }
    },
    async changeStatus (row) {
      try {
        var res = await this.apis.commission_changeStatus({ id: row.id, status: row.status })
        if (res && res.code === 0) {
          this.$message.success(row.status === 1 ? '已启用' : '已停用')
        } else {
          row.status = row.status === 1 ? 0 : 1
        }
      } catch (e) {
        row.status = row.status === 1 ? 0 : 1
      }
    },
    del (row) {
      this.$confirm('确定要删除规则【' + (row.ruleName || '') + '】吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.commission_delete([row.id])
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('删除成功')
          this.getData()
        }
      }).catch(() => {})
    }
  }
}
</script>
