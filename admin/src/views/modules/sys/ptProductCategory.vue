<template>
  <div>
    <div class="page-head">
      <div class="page-title">商品分类管理</div>
      <div class="page-desc">维护私教商品的业务分类（增肌、减脂、塑形、康复等）。停用后新增/编辑商品时不可选，已使用的商品不受影响。</div>
    </div>
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
      @refresh="page()"
      ref="rtable" />
    <r-form labelWidth="120px" :isHandle="true" :formRules="formRules" :formCols="formCols" :formHandle="formHandle" :formData="formData" ref="elForm" :inline="false" dialogWidth="500px" />
  </div>
</template>

<script>
export default {
  components: {},
  data () {
    return {
      tableLoading: false,
      searchData: {
        categoryName: '',
        status: ''
      },
      searchForm: [
        { type: 'input', placeholder: '分类名称', prop: 'categoryName', width: 200 },
        { type: 'select',
          placeholder: '状态',
          prop: 'status',
          width: 160,
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
        { label: '新增分类', type: 'primary', icon: 'el-icon-plus', isShow: () => this.checkBtn('sys:ptproductcategory:save'), handle: e => this.openAdd() }
      ],
      tableData: [],
      tableCols: [
        { label: 'ID', prop: 'id', width: 80 },
        { label: '分类名称', prop: 'categoryName' },
        { label: '排序权重', prop: 'sortNo', width: 100 },
        { label: '状态', type: 'tag', width: 90, prop: 'status', theme: (row) => row.status === 1 ? 'success' : 'info', formatter: e => e.status === 1 ? '启用' : '停用' },
        { label: '创建时间', prop: 'createdAt', width: 170, formatter: e => this.parseTime(e.createdAt) },
        {
          label: '操作',
          type: 'button',
          width: 210,
          fixed: 'right',
          btnList: [
            { label: '编辑', type: 'success', size: 'mini', icon: 'el-icon-edit', isShow: () => this.checkBtn('sys:ptproductcategory:update'), handle: (row) => this.elFormDetail(row) },
            { label: (row) => row.status === 1 ? '停用' : '启用', type: (row) => row.status === 1 ? 'warning' : 'primary', size: 'mini', isShow: () => this.checkBtn('sys:ptproductcategory:update'), handle: (row) => this.toggleStatus(row) },
            { label: '删除', type: 'danger', size: 'mini', icon: 'el-icon-delete', isShow: () => this.checkBtn('sys:ptproductcategory:delete'), handle: (row) => this.del(row) }
          ]
        }
      ],
      pagination: { limit: 10, offset: 1, total: 0 },
      formData: this.blankForm(),
      formCols: [
        { type: 'input', label: '分类名称', width: 300, prop: 'categoryName', placeholder: '2-20 个字符，不可重复' },
        { type: 'input', label: '排序权重', width: 300, prop: 'sortNo', placeholder: '越大越靠前' },
        { type: 'radio',
          label: '状态',
          prop: 'status',
          radios: [
          { value: 1, label: '启用' },
          { value: 0, label: '停用' }
          ] }
      ],
      formRules: {
        categoryName: [
          { required: true, message: '请输入分类名称', trigger: 'blur' },
          { min: 2, max: 20, message: '分类名称长度 2-20 个字符', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
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
  },
  methods: {
    blankForm () {
      return {
        id: '',
        categoryName: '',
        sortNo: 0,
        status: 1
      }
    },
    async getData () {
      this.tableLoading = true
      try {
        var res = await this.apis.ptProductCategory_list({
          page: this.pagination.offset,
          limit: this.pagination.limit,
          categoryName: this.searchData.categoryName,
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
      this.searchData = { categoryName: '', status: '' }
      this.pagination.offset = 1
      this.getData()
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
      this.formData = form
      this.elFormVisible('编辑')
    },
    elFormSubmit () {
      this.$refs.elForm.$refs.ruleForm.validate((valid) => {
        if (valid) this.submit()
      })
    },
    async submit () {
      var data = Object.assign({}, this.formData)
      data.sortNo = data.sortNo === '' ? 0 : Number(data.sortNo)
      try {
        var res = !data.id
          ? await this.apis.ptProductCategory_save(data)
          : await this.apis.ptProductCategory_update(data)
        if (res && res.code === 0) {
          this.$message.success('操作成功')
          this.elFormVisible()
          this.getData()
        }
      } catch (e) { /* 失败已由响应拦截器提示 */ }
    },
    toggleStatus (row) {
      var target = row.status === 1 ? 0 : 1
      var word = target === 1 ? '启用' : '停用'
      this.$confirm('确定要' + word + '分类【' + (row.categoryName || '') + '】吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.ptProductCategory_updateStatus({ id: row.id, status: target })
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('已' + word)
          this.getData()
        }
      }).catch(() => {})
    },
    del (row) {
      this.$confirm('确定要删除分类【' + (row.categoryName || '') + '】吗？已被商品使用的分类不可删除。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.ptProductCategory_delete([row.id])
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
