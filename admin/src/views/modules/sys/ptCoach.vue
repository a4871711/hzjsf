<template>
  <div>
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
    <r-form labelWidth="120px" :isHandle="true" :formRules="formRules" :formCols="formCols" :formHandle="formHandle" :formData="formData" :fileList="certFileList" @imgs="onCertImgs" ref="elForm" :inline="false" dialogWidth="1050px">
      <template slot="monthlyCommissionRules">
        <div class="monthly-rule-list">
          <el-alert
            type="info"
            :closable="false"
            title="仅适用于包月服务；教练未达到标准课节时按固定单节提成，达到标准后按课程价格、比例和实际完课数计算。"
            class="monthly-rule-tip" />
          <div v-if="formData.monthlyCommissionRules.length" class="monthly-rule-header">
            <span class="monthly-course-col">课程</span>
            <span>标准课节</span>
            <span>提成比例(%)</span>
            <span>小于标准单节提成</span>
            <span class="monthly-action-col">操作</span>
          </div>
          <div v-for="(rule, index) in formData.monthlyCommissionRules" :key="rule.rowKey" class="monthly-rule-row">
            <el-select v-model="rule.productId" filterable placeholder="选择包月课程" class="monthly-course-col">
              <el-option
                v-for="option in availableMonthlyProducts(rule.productId)"
                :key="option.value"
                :label="option.label"
                :value="option.value" />
            </el-select>
            <el-input-number v-model="rule.standardLessonCount" :min="1" :max="9999" :controls="false" />
            <el-input-number v-model="rule.commissionRate" :min="0.01" :max="100" :precision="2" :controls="false" />
            <el-input-number v-model="rule.belowStandardLessonFee" :min="0" :max="999999.99" :precision="2" :controls="false" />
            <el-button type="text" class="monthly-action-col" @click="removeMonthlyRule(index)">删除</el-button>
          </div>
          <el-button size="mini" type="primary" plain icon="el-icon-plus" @click="addMonthlyRule">添加包月课程提成</el-button>
          <span v-if="!monthlyProductOptions.length" class="monthly-empty-tip">请先在私教商品中新增“包月服务”课程</span>
        </div>
      </template>
    </r-form>

    <!-- 预约只读抽屉 -->
    <el-drawer title="最近预约" :visible.sync="apptVisible" size="42%" :destroy-on-close="true">
      <div style="padding: 0 16px;">
        <el-table :data="apptList" v-loading="apptLoading" border size="mini">
          <el-table-column label="会员" prop="memberName" align="center" />
          <el-table-column label="商品" prop="productName" align="center" />
          <el-table-column label="门店" prop="storeName" align="center" />
          <el-table-column label="预约日期" prop="appointmentDate" align="center" />
          <el-table-column label="时段" align="center">
            <template slot-scope="scope">{{ scope.row.startTime }}<span v-if="scope.row.endTime"> - {{ scope.row.endTime }}</span></template>
          </el-table-column>
          <el-table-column label="状态" align="center">
            <template slot-scope="scope">{{ appointmentStatusText(scope.row.appointmentStatus) }}</template>
          </el-table-column>
        </el-table>
        <div style="color:#909399;font-size:12px;margin-top:10px;">只读视图，核销/取消请在交易域操作。</div>
      </div>
    </el-drawer>

    <!-- 手机端会员账号绑定 -->
    <el-dialog :title="bindRow && bindRow.userId ? '换绑会员账号' : '绑定会员账号'"
      :visible.sync="bindVisible" width="520px" :close-on-click-modal="false">
      <el-alert type="info" :closable="false" show-icon
        title="绑定后，该会员登录手机端时将获得此教练的工作台身份。"
        style="margin-bottom:18px" />
      <el-form label-width="100px" size="medium">
        <el-form-item label="当前教练">
          <span>{{ bindRow ? bindRow.coachName : '-' }}</span>
          <span style="color:#909399;margin-left:8px;">{{ bindRow ? bindRow.coachNo : '' }}</span>
        </el-form-item>
        <el-form-item label="会员账号" required>
          <el-select v-model="bindUserId" filterable remote clearable reserve-keyword
            :remote-method="searchBindMembers" :loading="bindMemberLoading"
            placeholder="输入会员ID、昵称或手机号"
            style="width:350px">
            <el-option v-for="item in bindMemberOptions" :key="item.id"
              :label="memberOptionLabel(item)" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button v-if="bindRow && bindRow.userId" type="danger" plain :loading="bindSubmitting" @click="unbindMember">解除绑定</el-button>
        <el-button @click="bindVisible = false">取消</el-button>
        <el-button type="primary" :loading="bindSubmitting" @click="submitBinding">保存绑定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  components: {},
  data () {
    return {
      tableLoading: false,
      exporting: false,
      selection: [],
      // 门店下拉缓存
      storeOptions: [],
      monthlyProductOptions: [],
      monthlyRuleRowSeq: 0,
      defaultCoachLevel: '',
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
        { label: '绑定账号', type: 'html', width: 180, html: (row) => this.bindingHtml(row) },
        { label: '所属门店', type: 'html', html: (row) => this.storeTagsHtml(row) },
        { label: '等级', prop: 'coachLevel', width: 100, formatter: e => e.coachLevel || '-' },
        { label: '状态', type: 'tag', width: 80, prop: 'status', theme: (row) => this.statusTheme(row.status), formatter: e => this.statusText(e.status) },
        { label: '排序', prop: 'sortNo', width: 70 },
        { label: '创建时间', prop: 'createdAt', width: 160, formatter: e => this.parseTime(e.createdAt) },
        {
          label: '操作',
          type: 'button',
          width: 330,
          fixed: 'right',
          btnList: [
            { label: '预约', type: 'info', size: 'mini', icon: 'el-icon-date', handle: (row) => this.openAppt(row) },
            { label: (row) => row.userId ? '换绑' : '绑定', type: 'primary', size: 'mini', icon: 'el-icon-link', isShow: () => this.checkBtn('sys:ptCoach:update'), disabled: (row) => row.status === 3, handle: (row) => this.openBind(row) },
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
        { type: 'slot', label: '包月课程提成', prop: 'monthlyCommissionRules', name: 'monthlyCommissionRules' },
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
      apptList: [],
      // 手机端会员账号绑定弹窗
      bindVisible: false,
      bindRow: null,
      bindUserId: '',
      bindMemberOptions: [],
      bindMemberLoading: false,
      bindSubmitting: false,
      bindSearchSeq: 0
    }
  },
  mounted () {
    this.getData()
    this.getStoreList()
    this.getLevelOptions()
    this.getMonthlyProductOptions()
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
        certificateUrls: '[]',
        sortNo: 0,
        status: 1,
        disableReason: '',
        monthlyCommissionRules: []
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
      var self = this
      var ids = String(row.storeIdCsv || '').split(',').filter(function (id) { return id })
      var names = ids.map(function (id) {
        var hit = self.storeOptions.filter(function (op) { return String(op.value) === String(id) })[0]
        return hit ? hit.label : ('门店#' + id)
      })
      if (!names.length && row.storeNames) names = [row.storeNames]
      if (!names.length) return '<span style="color:#909399;">-</span>'
      return names.map(function (n) {
        return '<span style="display:inline-block;margin:2px;padding:0 8px;line-height:22px;background:#ecf5ff;color:#409eff;border:1px solid #d9ecff;border-radius:4px;font-size:12px;">' + n + '</span>'
      }).join('')
    },
    escapeHtml (value) {
      return String(value === undefined || value === null ? '' : value)
        .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;').replace(/'/g, '&#39;')
    },
    bindingHtml (row) {
      if (!row.userId) return '<span style="color:#909399;">未绑定</span>'
      var name = this.escapeHtml(row.boundMemberName || '未命名会员')
      var mobile = this.escapeHtml(row.boundMemberMobile || '-')
      var id = this.escapeHtml(row.userId)
      return '<div style="line-height:20px;"><strong>' + name + '</strong><br>' +
        '<span style="color:#909399;">ID ' + id + ' · ' + mobile + '</span></div>'
    },
    appointmentStatusText (status) {
      return status === 1 ? '已预约' : status === 2 ? '已取消' : status === 3 ? '已完成' : status === 4 ? '爽约' : '-'
    },
    // ===== 列表 =====
    buildListParams (page, limit) {
      var params = {
        coachName: this.searchData.coachName,
        mobile: this.searchData.mobile,
        storeId: this.searchData.storeId,
        status: this.searchData.status,
        coachLevel: this.searchData.coachLevel
      }
      if (page !== undefined) params.page = page
      if (limit !== undefined) params.limit = limit
      var range = this.searchData.createTime || []
      if (range.length === 2) {
        params.beginTime = range[0]
        params.endTime = range[1]
      }
      return params
    },
    async getData () {
      this.tableLoading = true
      try {
        var params = this.buildListParams(this.pagination.offset, this.pagination.limit)
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
      var defaultLevel = ''
      var opts = list.map(function (r) {
        var name = r.levelName !== undefined ? r.levelName : r.label
        if (r.isDefault === 1) defaultLevel = name
        return { value: name, label: name }
      })
      this.defaultCoachLevel = defaultLevel
      this.searchForm[this.searchIndex(this.searchForm, '教练等级')].options = opts
      this.formCols[this.labIndex(this.formCols, '教练等级')].options = opts
    },
    async getMonthlyProductOptions () {
      var res = await this.apis.ptCoach_monthlyProductOptions()
      var list = res.list || []
      this.monthlyProductOptions = list.map(function (item) {
        var statusText = Number(item.listingStatus) === 1 ? '已上架' : '未上架'
        return {
          value: item.productId,
          label: item.productName + '（' + item.productLessonCount + '节 / ¥' + item.salePrice + ' / ' + statusText + '）'
        }
      })
    },
    availableMonthlyProducts (currentId) {
      var selected = {}
      ;(this.formData.monthlyCommissionRules || []).forEach(function (rule) {
        if (rule.productId !== '' && rule.productId !== null && rule.productId !== undefined) {
          selected[String(rule.productId)] = true
        }
      })
      return this.monthlyProductOptions.filter(function (item) {
        return String(item.value) === String(currentId) || !selected[String(item.value)]
      })
    },
    addMonthlyRule () {
      if (!this.monthlyProductOptions.length) {
        this.$message.warning('当前没有可配置的包月服务课程')
        return
      }
      this.monthlyRuleRowSeq++
      this.formData.monthlyCommissionRules.push({
        rowKey: 'monthly-new-' + this.monthlyRuleRowSeq,
        productId: '',
        standardLessonCount: 1,
        commissionRate: 0.01,
        belowStandardLessonFee: 0
      })
    },
    removeMonthlyRule (index) {
      this.formData.monthlyCommissionRules.splice(index, 1)
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
      this.formData.coachLevel = this.defaultCoachLevel
      this.certFileList = []
      this.elFormVisible()
    },
    async elFormDetail (row) {
      if (row.status === 3) {
        this.$message.warning('离职教练不可编辑')
        return
      }
      var res = await this.apis.ptCoach_info({ id: row.id })
      var detail = res && res.coach
      if (!detail) {
        this.$message.error('获取教练详情失败')
        return
      }
      var form = this.blankForm()
      Object.keys(form).forEach(function (key) {
        if (detail[key] !== undefined && detail[key] !== null) form[key] = detail[key]
      })
      // 列表接口只有门店名称，编辑时必须使用详情接口返回的门店 ID 才能正确回显。
      form.storeIds = Array.isArray(detail.storeIds) ? detail.storeIds.slice() : []
      form.certificateUrls = detail.certificateUrls || '[]'
      form.monthlyCommissionRules = Array.isArray(detail.monthlyCommissionRules)
        ? detail.monthlyCommissionRules.map((rule) => {
          this.monthlyRuleRowSeq++
          return {
            rowKey: 'monthly-edit-' + this.monthlyRuleRowSeq,
            productId: rule.productId,
            standardLessonCount: rule.standardLessonCount,
            commissionRate: Number(rule.commissionRate),
            belowStandardLessonFee: Number(rule.belowStandardLessonFee)
          }
        }) : []
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
          var productIds = {}
          for (var i = 0; i < this.formData.monthlyCommissionRules.length; i++) {
            var rule = this.formData.monthlyCommissionRules[i]
            if (!rule.productId || !Number.isInteger(Number(rule.standardLessonCount)) || Number(rule.standardLessonCount) <= 0) {
              this.$message.error('请完整填写包月课程和大于0的整数标准课节')
              return
            }
            if (!(Number(rule.commissionRate) > 0 && Number(rule.commissionRate) <= 100)) {
              this.$message.error('包月课程提成比例必须大于0且不超过100')
              return
            }
            if (!(Number(rule.belowStandardLessonFee) >= 0)) {
              this.$message.error('小于标准时的单节提成不能小于0')
              return
            }
            if (productIds[String(rule.productId)]) {
              this.$message.error('同一包月课程不能重复配置')
              return
            }
            productIds[String(rule.productId)] = true
          }
          this.submit()
        }
      })
    },
    async submit () {
      var data = Object.assign({}, this.formData)
      // MySQL 字段为 JSON；即使未上传证书，也必须提交合法的空数组，不能提交空字符串。
      data.certificateUrls = JSON.stringify(this.certUrlArray())
      data.sortNo = data.sortNo === '' ? 0 : Number(data.sortNo)
      data.monthlyCommissionRules = (data.monthlyCommissionRules || []).map(function (rule) {
        return {
          productId: Number(rule.productId),
          standardLessonCount: Number(rule.standardLessonCount),
          commissionRate: Number(rule.commissionRate),
          belowStandardLessonFee: Number(rule.belowStandardLessonFee)
        }
      })
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
    // ===== 手机端会员账号绑定 =====
    openBind (row) {
      this.bindRow = row
      this.bindUserId = row.userId || ''
      this.bindMemberOptions = row.userId ? [{
        id: row.userId,
        nickname: row.boundMemberName,
        phone: row.boundMemberMobile
      }] : []
      this.bindSearchSeq++
      this.bindVisible = true
    },
    memberOptionLabel (item) {
      var name = item.nickname || '未命名会员'
      var phone = item.phone || '无手机号'
      var store = item.storeName ? (' · ' + item.storeName) : ''
      return name + ' · ' + phone + ' · ID ' + item.id + store
    },
    searchBindMembers (keyword) {
      var value = String(keyword || '').trim()
      var seq = ++this.bindSearchSeq
      if (!value) {
        this.bindMemberOptions = []
        this.bindMemberLoading = false
        return
      }
      this.bindMemberLoading = true
      this.apis.ptCoach_memberOptions({ keyword: value }).then((res) => {
        if (seq === this.bindSearchSeq) this.bindMemberOptions = res.list || []
      }).catch(() => {
        if (seq === this.bindSearchSeq) this.bindMemberOptions = []
      }).then(() => {
        if (seq === this.bindSearchSeq) this.bindMemberLoading = false
      })
    },
    async submitBinding () {
      if (!this.bindUserId) {
        this.$message.warning('请先选择要绑定的会员账号')
        return
      }
      this.bindSubmitting = true
      try {
        var res = await this.apis.ptCoach_bindMember({ coachId: this.bindRow.id, userId: this.bindUserId })
        if (res && res.code === 0) {
          this.$message.success('会员账号绑定成功')
          this.bindVisible = false
          this.getData()
        }
      } finally {
        this.bindSubmitting = false
      }
    },
    unbindMember () {
      this.$confirm('解绑后，该会员账号将立即失去手机端教练工作台。确定继续吗？', '解除绑定', {
        confirmButtonText: '确定解绑',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        this.bindSubmitting = true
        try {
          var res = await this.apis.ptCoach_bindMember({ coachId: this.bindRow.id, userId: null })
          if (res && res.code === 0) {
            this.$message.success('已解除绑定')
            this.bindVisible = false
            this.getData()
          }
        } finally {
          this.bindSubmitting = false
        }
      }).catch(() => {})
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
    async exportData () {
      if (this.exporting) {
        this.$message.warning('正在导出，请勿重复操作')
        return
      }
      this.exporting = true
      try {
        var response = await this.apis.ptCoach_export(this.buildListParams())
        var contentType = response.headers['content-type'] || ''
        if (contentType.indexOf('application/json') !== -1) {
          throw new Error('后端未返回 Excel 文件')
        }
        var disposition = response.headers['content-disposition'] || ''
        var fileName = '教练列表.xlsx'
        var utf8Match = /filename\*=UTF-8''([^;]+)/i.exec(disposition)
        var legacyMatch = /filename="?([^";]+)"?/i.exec(disposition)
        if (utf8Match) {
          fileName = decodeURIComponent(utf8Match[1])
        } else if (legacyMatch) {
          fileName = legacyMatch[1]
        }
        var blob = new Blob([response.data], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        })
        var downloadUrl = window.URL.createObjectURL(blob)
        var link = document.createElement('a')
        link.href = downloadUrl
        link.download = fileName
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(downloadUrl)
        this.$message.success('导出成功')
      } catch (error) {
        this.$message.error('导出失败，请稍后重试')
      } finally {
        this.exporting = false
      }
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

<style scoped>
.monthly-rule-list {
  width: 850px;
}
.monthly-rule-tip {
  margin-bottom: 12px;
}
.monthly-rule-header,
.monthly-rule-row {
  display: grid;
  grid-template-columns: 300px 120px 120px 160px 50px;
  column-gap: 10px;
  align-items: center;
  margin-bottom: 10px;
}
.monthly-rule-header {
  color: #606266;
  font-size: 13px;
}
.monthly-rule-row .el-input-number {
  width: 100%;
}
.monthly-course-col {
  width: 300px;
}
.monthly-action-col {
  text-align: center;
}
.monthly-empty-tip {
  margin-left: 12px;
  color: #909399;
  font-size: 12px;
}
</style>
