<template>
  <div>
    <div class="page-head">
      <div class="page-title">教练等级管理</div>
      <div class="page-desc">配置教练等级主数据（供教练档案下拉与用户端排序展示），按排序权重倒序。</div>
    </div>
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
    <r-form labelWidth="120px" :isHandle="true" :formRules="formRules" :formCols="formCols" :formHandle="formHandle" :formData="formData" ref="elForm" :inline="false" dialogWidth="480px" />
  </div>
</template>

<script>
export default {
  components: {},
  data () {
    return {
      tableLoading: false,
      tableHandles: [
        { label: '新增等级', type: 'primary', icon: 'el-icon-plus', isShow: () => this.checkBtn('sys:coachLevel:save'), handle: e => this.openAdd() }
      ],
      tableData: [],
      tableCols: [
        { label: 'ID', prop: 'id', width: 70 },
        { label: '等级名称', prop: 'levelName', formatter: e => e.levelName + (e.isDefault === 1 ? '（默认）' : '') },
        { label: '排序权重', prop: 'sortNo', width: 100 },
        { label: '是否默认', type: 'tag', width: 100, prop: 'isDefault', theme: (row) => row.isDefault === 1 ? 'success' : 'info', formatter: e => e.isDefault === 1 ? '是' : '否' },
        {
          label: '状态',
          type: 'switch',
          width: 100,
          prop: 'status',
          values: [1, 0],
          isDisabled: () => !this.checkBtn('sys:coachLevel:changeStatus'),
          change: (row) => this.changeStatus(row)
        },
        { label: '关联教练数', prop: 'coachCount', width: 110, formatter: e => (e.coachCount === undefined || e.coachCount === null) ? 0 : e.coachCount },
        { label: '创建时间', prop: 'createdAt', width: 160, formatter: e => this.parseTime(e.createdAt) },
        {
          label: '操作',
          type: 'button',
          width: 150,
          fixed: 'right',
          btnList: [
            { label: '编辑', type: 'success', size: 'mini', icon: 'el-icon-edit', isShow: () => this.checkBtn('sys:coachLevel:update'), handle: (row) => this.elFormDetail(row) },
            { label: '删除', type: 'danger', size: 'mini', icon: 'el-icon-delete', disabled: (row) => row.isDefault === 1 || row.coachCount > 0, isShow: () => this.checkBtn('sys:coachLevel:delete'), handle: (row) => this.del(row) }
          ]
        }
      ],
      pagination: { limit: 10, offset: 1, total: 0 },
      formData: this.blankForm(),
      formCols: [
        { type: 'input', label: '等级名称', width: 300, prop: 'levelName' },
        { type: 'input', label: '排序权重', width: 300, prop: 'sortNo', placeholder: '越大越靠前' },
        { type: 'radio',
          label: '等级状态',
          prop: 'status',
          radios: [
          { value: 1, label: '启用' },
          { value: 0, label: '停用' }
          ] },
        { type: 'radio',
          label: '是否默认',
          prop: 'isDefault',
          radios: [
          { value: 1, label: '是' },
          { value: 0, label: '否' }
          ] }
      ],
      formRules: {
        levelName: [
          { required: true, message: '请输入等级名称', trigger: 'blur' }
        ],
        sortNo: [
          { required: true, message: '请输入排序权重', trigger: 'blur' }
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
        levelName: '',
        sortNo: 0,
        status: 1,
        isDefault: 0
      }
    },
    async getData () {
      this.tableLoading = true
      try {
        var res = await this.apis.coachLevel_list({
          page: this.pagination.offset,
          limit: this.pagination.limit
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
        if (valid) {
          this.submit()
        }
      })
    },
    async submit () {
      var data = Object.assign({}, this.formData)
      data.sortNo = data.sortNo === '' ? 0 : Number(data.sortNo)
      try {
        var res = !data.id
          ? await this.apis.coachLevel_save(data)
          : await this.apis.coachLevel_update(data)
        if (res && res.code === 0) {
          this.$message.success('操作成功')
          this.elFormVisible()
          this.getData()
        }
      } catch (e) { /* 失败已由响应拦截器弹错误提示 */ }
    },
    // 状态开关：el-switch v-model 已乐观翻到新值，失败须回滚
    async changeStatus (row) {
      try {
        var res = await this.apis.coachLevel_changeStatus({ id: row.id, status: row.status })
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
      this.$confirm('确定要删除等级【' + (row.levelName || '') + '】吗？被引用或默认等级不可删除。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.coachLevel_delete([row.id])
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
