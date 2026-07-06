<template>
  <div>
    <!-- 顶部标题 + 副标题 -->
    <div class="page-head">
      <div class="page-title">教练管理</div>
      <div class="page-desc">维护私教教练档案：基础信息、所属门店、等级与状态（停用/离职）。</div>
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
    <r-form labelWidth="120px" :isHandle="true" :formRules="formRules" :formCols="formCols" :formHandle="formHandle" :formData="formData" :fileList="certFileList" @imgs="onCertImgs" ref="elForm" :inline="false" dialogWidth="640px" />

    <!-- 预约只读抽屉 -->
    <el-drawer title="最近预约" :visible.sync="apptVisible" size="42%" :destroy-on-close="true">
      <div style="padding: 0 16px;">
        <el-table :data="apptList" v-loading="apptLoading" border size="mini">
          <el-table-column label="会员" prop="memberName" align="center" />
          <el-table-column label="商品" prop="productName" align="center" />
          <el-table-column label="门店" prop="storeName" align="center" />
          <el-table-column label="预约日期" prop="apptDate" align="center" />
          <el-table-column label="时段" align="center">
            <template slot-scope="scope">{{ scope.row.startTime }}<span v-if="scope.row.endTime"> - {{ scope.row.endTime }}</span></template>
          </el-table-column>
          <el-table-column label="状态" prop="statusName" align="center" />
        </el-table>
        <div style="color:#909399;font-size:12px;margin-top:10px;">只读视图，核销/取消请在交易域操作。</div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
export default {
  components: {},
  data () {
    return {
      tableLoading: false,
      selection: [],
      // 门店下拉缓存
      storeOptions: [],
      searchData: {
        coachName: '',
        mobile: '',
        storeId: '',
        status: '',
        coachLevel: '',
        createTime: []
      },
      searchForm: [
        { type: 'input', placeholder: '教练姓名', prop: 'coachName', width: 160 },
        { type: 'input', placeholder: '手机号', prop: 'mobile', width: 160 },
        { type: 'select', placeholder: '所属门店', prop: 'storeId', width: 180, options: [] },
        { type: 'select',
          placeholder: '教练状态',
          prop: 'status',
          width: 140,
          options: [
          { value: 1, label: '正常' },
          { value: 2, label: '停用' },
          { value: 3, label: '离职' }
          ] },
        { type: 'select', placeholder: '教练等级', prop: 'coachLevel', width: 160, options: [] },
        { type: 'daterange', placeholder: '创建时间', prop: 'createTime', width: 240, value_format: 'yyyy-MM-dd' }
      ],
      searchHandle: [
        { label: '搜索', type: 'primary', handle: e => this.getData() },
        { label: '重置', handle: e => this.reset() }
      ],
      tableHandles: [
        { label: '新增教练', type: 'primary', icon: 'el-icon-plus', isShow: () => this.checkBtn('sys:ptCoach:save'), handle: e => this.openAdd() },
        { label: '批量删除', type: 'danger', icon: 'el-icon-delete', isShow: () => this.checkBtn('sys:ptCoach:delete'), handle: e => this.delBatch() },
        { label: '导出', icon: 'el-icon-download', handle: e => this.exportData() }
      ],
      tableData: [],
      tableCols: [
        { label: '编号', prop: 'coachNo', width: 130 },
        { label: '头像', type: 'html', width: 70, html: (row) => this.avatarHtml(row) },
        { label: '姓名', prop: 'coachName', width: 90 },
        { label: '手机', prop: 'mobile', width: 120 },
        { label: '所属门店', type: 'html', html: (row) => this.storeTagsHtml(row) },
        { label: '等级', prop: 'coachLevel', width: 100, formatter: e => e.coachLevel || '-' },
        { label: '状态', type: 'tag', width: 80, prop: 'status', theme: (row) => this.statusTheme(row.status), formatter: e => this.statusText(e.status) },
        { label: '排序', prop: 'sortNo', width: 70 },
        { label: '创建时间', prop: 'createdAt', width: 160, formatter: e => this.parseTime(e.createdAt) },
        {
          label: '操作',
          type: 'button',
          width: 240,
          fixed: 'right',
          btnList: [
            { label: '预约', type: 'info', size: 'mini', icon: 'el-icon-date', handle: (row) => this.openAppt(row) },
            { label: '编辑', type: 'success', size: 'mini', icon: 'el-icon-edit', disabled: (row) => row.status === 3, handle: (row) => this.elFormDetail(row) },
            { label: (row) => row.status === 1 ? '停用' : '启用', type: (row) => row.status === 1 ? 'warning' : 'primary', size: 'mini', handle: (row) => this.toggleStatus(row) },
            { label: '删除', type: 'danger', size: 'mini', icon: 'el-icon-delete', handle: (row) => this.del(row) }
          ]
        }
      ],
      pagination: { limit: 10, offset: 1, total: 0 },
      formData: this.blankForm(),
      // 资格证书多图回显列表
      certFileList: [],
      formCols: [
        { type: 'input', label: '教练姓名', width: 320, prop: 'coachName' },
        { type: 'input', label: '手机号', width: 320, prop: 'mobile' },
        { type: 'radio',
          label: '性别',
          prop: 'gender',
          radios: [
          { value: 1, label: '男' },
          { value: 2, label: '女' },
          { value: 0, label: '未知' }
          ] },
        { type: 'upload', label: '头像', width: 320, prop: 'avatarUrl', success: (res) => this.onAvatarSuccess(res) },
        { type: 'select', label: '教练等级', width: 320, prop: 'coachLevel', options: [] },
        { type: 'select', label: '所属门店', width: 320, prop: 'storeIds', multiple: true, options: [] },
        { type: 'textarea', label: '简介', width: 320, prop: 'intro' },
        { type: 'uploadList', label: '资格证书', width: 400, prop: 'certificateUrls', placeholder: '可上传多张证书图片' },
        { type: 'input', label: '排序权重', width: 320, prop: 'sortNo', placeholder: '越大越靠前' },
        { type: 'radio',
          label: '教练状态',
          prop: 'status',
          radios: [
          { value: 1, label: '正常' },
          { value: 2, label: '停用' },
          { value: 3, label: '离职' }
          ] },
        { type: 'textarea', label: '停用/离职原因', width: 320, prop: 'disableReason', isShow: (item) => this.formData.status === 2 || this.formData.status === 3 }
      ],
      formRules: {
        coachName: [
          { required: true, message: '请输入教练姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '姓名长度 2-20 个字符', trigger: 'blur' }
        ],
        mobile: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        storeIds: [
          { required: true, type: 'array', message: '请选择所属门店', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择教练状态', trigger: 'change' }
        ]
      },
      formHandle: [
        { label: '确认', type: 'primary', icon: 'el-icon-circle-plus-outline', handle: e => this.elFormSubmit() },
        { label: '取消', icon: 'el-icon-circle-close', handle: e => this.elFormVisible() }
      ],
      // 预约抽屉
      apptVisible: false,
      apptLoading: false,
      apptList: []
    }
  },
  mounted () {
    this.getData()
    this.getStoreList()
    this.getLevelOptions()
  },
  methods: {
    // 表单初始值（coach_no 后端生成，不入表单）
    blankForm () {
      return {
        id: '',
        coachName: '',
        mobile: '',
        gender: 1,
        avatarUrl: '',
        coachLevel: '',
        storeIds: [],
        intro: '',
        certificateUrls: '',
        sortNo: 0,
        status: 1,
        disableReason: ''
      }
    },
    // ===== 展示辅助 =====
    statusText (s) {
      return s === 1 ? '正常' : s === 2 ? '停用' : s === 3 ? '离职' : '-'
    },
    statusTheme (s) {
      return s === 1 ? 'success' : s === 2 ? 'info' : 'danger'
    },
    avatarHtml (row) {
      var name = row.coachName || ''
      var initial = name ? name.charAt(0) : '?'
      if (row.avatarUrl) {
        var url = /^http/.test(row.avatarUrl) ? row.avatarUrl : (this.baseUrl + row.avatarUrl)
        return '<img src="' + url + '" style="width:36px;height:36px;border-radius:50%;object-fit:cover;" />'
      }
      return '<span style="display:inline-block;width:36px;height:36px;line-height:36px;border-radius:50%;background:#409eff;color:#fff;font-size:14px;">' + initial + '</span>'
    },
    storeTagsHtml (row) {
      var names = row.storeNames
      if (!names) return '<span style="color:#909399;">-</span>'
      return String(names).split(',').filter(function (n) { return n }).map(function (n) {
        return '<span style="display:inline-block;margin:2px;padding:0 8px;line-height:22px;background:#ecf5ff;color:#409eff;border:1px solid #d9ecff;border-radius:4px;font-size:12px;">' + n + '</span>'
      }).join('')
    },
    // ===== 列表 =====
    async getData () {
      this.tableLoading = true
      try {
        var params = {
          page: this.pagination.offset,
          limit: this.pagination.limit,
          coachName: this.searchData.coachName,
          mobile: this.searchData.mobile,
          storeId: this.searchData.storeId,
          status: this.searchData.status,
          coachLevel: this.searchData.coachLevel
        }
        var range = this.searchData.createTime || []
        if (range.length === 2) {
          params.createTimeStart = range[0]
          params.createTimeEnd = range[1]
        }
        var res = await this.apis.ptCoach_list(params)
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
      this.searchData = { coachName: '', mobile: '', storeId: '', status: '', coachLevel: '', createTime: [] }
      this.pagination.offset = 1
      this.getData()
    },
    handleSelect (rows) {
      this.selection = rows || []
    },
    // ===== 下拉数据 =====
    async getStoreList () {
      var res = await this.apis.store_list({ page: 1, limit: 999 })
      var list = (res.page && res.page.list) || []
      var opts = list.map(function (item) {
        return { value: item.storeAddrId, label: item.storeName }
      })
      this.storeOptions = opts
      this.searchForm[this.searchIndex(this.searchForm, '所属门店')].options = opts
      this.formCols[this.labIndex(this.formCols, '所属门店')].options = opts
    },
    async getLevelOptions () {
      var res = await this.apis.coachLevel_options()
      var list = res.list || []
      // 兼容后端返回 {id,levelName} 或 {value,label}
      var opts = list.map(function (r) {
        var name = r.levelName !== undefined ? r.levelName : r.label
        return { value: name, label: name }
      })
      this.searchForm[this.searchIndex(this.searchForm, '教练等级')].options = opts
      this.formCols[this.labIndex(this.formCols, '教练等级')].options = opts
    },
    // ===== 头像 / 证书上传回调 =====
    onAvatarSuccess (res) {
      if (res && res.code === 0 && res.path && res.path.length) {
        this.formData.avatarUrl = res.path[0]
      }
    },
    // r-form 的 uploadList 通过 @imgs 事件回传增删
    onCertImgs (payload) {
      var urls = this.certUrlArray()
      if (payload.type === 'success') {
        urls.push(payload.url)
      } else if (payload.type === 'remove') {
        var idx = urls.indexOf(payload.url)
        if (idx !== -1) urls.splice(idx, 1)
      }
      this.formData.certificateUrls = JSON.stringify(urls)
    },
    certUrlArray () {
      var v = this.formData.certificateUrls
      if (!v) return []
      try {
        var arr = JSON.parse(v)
        return Array.isArray(arr) ? arr : []
      } catch (e) {
        return String(v).split(',').filter(function (x) { return x })
      }
    },
    buildCertFileList () {
      var self = this
      this.certFileList = this.certUrlArray().map(function (u) {
        return { url: /^http/.test(u) ? u : (self.baseUrl + u) }
      })
    },
    // ===== 新增 / 编辑 =====
    openAdd () {
      this.formData = this.blankForm()
      this.certFileList = []
      this.elFormVisible()
    },
    elFormDetail (row) {
      if (row.status === 3) {
        this.$message.warning('离职教练不可编辑')
        return
      }
      var form = this.blankForm()
      Object.keys(form).forEach(function (key) {
        if (row[key] !== undefined && row[key] !== null) form[key] = row[key]
      })
      // storeIds：优先用行内数组；否则空
      form.storeIds = Array.isArray(row.storeIds) ? row.storeIds.slice() : []
      form.certificateUrls = row.certificateUrls || ''
      this.formData = form
      this.buildCertFileList()
      this.elFormVisible('编辑')
    },
    elFormSubmit () {
      this.$refs.elForm.$refs.ruleForm.validate((valid) => {
        if (valid) {
          if ((this.formData.status === 2 || this.formData.status === 3) && !this.formData.disableReason) {
            this.$message.error('停用/离职时必须填写原因')
            return
          }
          this.submit()
        }
      })
    },
    async submit () {
      var data = Object.assign({}, this.formData)
      data.sortNo = data.sortNo === '' ? 0 : Number(data.sortNo)
      try {
        var res = !data.id
          ? await this.apis.ptCoach_save(data)
          : await this.apis.ptCoach_update(data)
        if (res && res.code === 0) {
          this.$message.success('操作成功')
          this.elFormVisible()
          this.getData()
        }
      } catch (e) { /* 失败已由响应拦截器弹错误提示 */ }
    },
    // ===== 状态切换 =====
    toggleStatus (row) {
      if (row.status === 1) {
        // 正常 -> 停用，需填原因
        this.$prompt('请输入停用原因', '停用教练', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputValidator: (v) => (v && v.trim()) ? true : '停用原因不能为空'
        }).then(({ value }) => {
          return this.apis.ptCoach_changeStatus({ id: row.id, status: 2, disableReason: value })
        }).then((res) => {
          if (res && res.code === 0) {
            this.$message.success('已停用')
            this.getData()
          }
        }).catch(() => {})
      } else {
        // 停用/离职 -> 恢复正常
        this.$confirm('确定将该教练恢复为正常状态吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          return this.apis.ptCoach_changeStatus({ id: row.id, status: 1, disableReason: '' })
        }).then((res) => {
          if (res && res.code === 0) {
            this.$message.success('已启用')
            this.getData()
          }
        }).catch(() => {})
      }
    },
    // ===== 删除 =====
    del (row) {
      this.$confirm('确定要删除教练【' + (row.coachName || '') + '】吗？存在业务数据的教练不可删除。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.ptCoach_delete([row.id])
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('删除成功')
          this.getData()
        }
      }).catch(() => {})
    },
    delBatch () {
      if (!this.selection.length) {
        this.$message.warning('请先勾选要删除的教练')
        return
      }
      var ids = this.selection.map(function (r) { return r.id })
      this.$confirm('确定要批量删除选中的 ' + ids.length + ' 名教练吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.ptCoach_delete(ids)
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('删除成功')
          this.getData()
        }
      }).catch(() => {})
    },
    exportData () {
      // TODO: 导出接口未提供，先占位提示
      this.$message.info('导出功能待接入')
    },
    // ===== 预约只读抽屉 =====
    openAppt (row) {
      this.apptVisible = true
      this.apptLoading = true
      this.apptList = []
      this.apis.ptCoach_appointments({ id: row.id }).then((res) => {
        if (res && res.code === 0) {
          this.apptList = res.list || []
        }
      }).finally(() => {
        this.apptLoading = false
      })
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
