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
      @refresh="page" />

    <!-- 回复抽屉 -->
    <el-dialog title="回复评价" :visible.sync="replyVisible" width="640px" :close-on-click-modal="false">
      <el-form label-width="90px" size="small">
        <el-form-item label="教练">{{ curRow.coachName }}</el-form-item>
        <el-form-item label="会员">{{ memberInfo(curRow) }}</el-form-item>
        <el-form-item label="评分">
          <el-rate :value="curRow.score * 1" disabled></el-rate>
        </el-form-item>
        <el-form-item label="评价内容">
          <div class="cc-readonly">{{ curRow.commentContent || '-' }}</div>
        </el-form-item>
        <el-form-item label="回复内容">
          <el-input type="textarea" :rows="4" v-model="replyForm.replyContent" placeholder="请输入回复内容"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">确认回复</el-button>
      </span>
    </el-dialog>

    <!-- 处理抽屉 -->
    <el-dialog title="标记处理" :visible.sync="handleVisible" width="640px" :close-on-click-modal="false">
      <el-form label-width="90px" size="small">
        <el-form-item label="教练">{{ curRow.coachName }}</el-form-item>
        <el-form-item label="会员">{{ memberInfo(curRow) }}</el-form-item>
        <el-form-item label="评分">
          <el-rate :value="curRow.score * 1" disabled></el-rate>
        </el-form-item>
        <el-form-item label="评价内容">
          <div class="cc-readonly">{{ curRow.commentContent || '-' }}</div>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-radio-group v-model="handleForm.handleStatus">
            <el-radio :label="0">待处理</el-radio>
            <el-radio :label="1">已跟进</el-radio>
            <el-radio :label="2">已忽略</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input type="textarea" :rows="3" v-model="handleForm.handleRemark" placeholder="仅后台可见"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="handleVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确认</el-button>
      </span>
    </el-dialog>

    <!-- 编辑抽屉（改评价内容/处理状态） -->
    <el-dialog title="编辑评价" :visible.sync="editVisible" width="640px" :close-on-click-modal="false">
      <el-form label-width="90px" size="small">
        <el-form-item label="教练">{{ curRow.coachName }}</el-form-item>
        <el-form-item label="会员">{{ memberInfo(curRow) }}</el-form-item>
        <el-form-item label="评价内容">
          <el-input type="textarea" :rows="4" v-model="editForm.commentContent"></el-input>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-radio-group v-model="editForm.handleStatus">
            <el-radio :label="0">待处理</el-radio>
            <el-radio :label="1">已跟进</el-radio>
            <el-radio :label="2">已忽略</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input type="textarea" :rows="3" v-model="editForm.handleRemark" placeholder="仅后台可见"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </span>
    </el-dialog>

    <!-- 新增评价抽屉 -->
    <el-dialog title="新增评价" :visible.sync="addVisible" width="640px" :close-on-click-modal="false">
      <el-form label-width="90px" size="small">
        <el-form-item label="预约ID">
          <el-input v-model="addForm.appointmentId" placeholder="关联已完成预约ID"></el-input>
        </el-form-item>
        <el-form-item label="教练">
          <el-select v-model="addForm.coachId" filterable clearable placeholder="请选择教练" style="width:100%">
            <el-option v-for="op in coachOptions" :label="op.label" :value="op.value" :key="op.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="会员ID">
          <el-input v-model="addForm.memberId" placeholder="会员ID"></el-input>
        </el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="addForm.score"></el-rate>
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input type="textarea" :rows="4" v-model="addForm.commentContent"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确认</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data () {
    return {
      tableLoading: false,
      storeOptions: [],
      coachOptions: [],
      searchData: {
        coachName: '',
        memberKeyword: '',
        score: '',
        handleStatus: '',
        storeId: '',
        times: []
      },
      searchForm: [
        { type: 'input', placeholder: '教练姓名', prop: 'coachName', width: 160 },
        { type: 'input', placeholder: '会员昵称/手机', prop: 'memberKeyword', width: 160 },
        { type: 'select',
          placeholder: '评分',
          prop: 'score',
          width: 120,
          options: [
          { value: 5, label: '5星' },
          { value: 4, label: '4星' },
          { value: 3, label: '3星' },
          { value: 2, label: '2星' },
          { value: 1, label: '1星' }
          ] },
        { type: 'select',
          placeholder: '处理状态',
          prop: 'handleStatus',
          width: 140,
          options: [
          { value: 0, label: '待处理' },
          { value: 1, label: '已回复' },
          { value: 2, label: '已忽略' }
          ] },
        { type: 'select', placeholder: '门店', prop: 'storeId', width: 160, options: [] },
        { type: 'daterange', placeholder: '评价时间', prop: 'times', width: 260 }
      ],
      searchHandle: [
        { label: '搜索', type: 'primary', handle: e => this.getData() }
      ],
      tableHandles: [
        { label: '新增评价', type: 'primary', isShow: () => this.checkBtn('sys:coachComment:save'), handle: e => this.openAdd() }
      ],
      tableData: [],
      tableCols: [
        { label: '评价ID', prop: 'id', width: 70 },
        { label: '教练', prop: 'coachName' },
        { label: '会员信息', prop: 'memberInfo', formatter: e => this.memberInfo(e) },
        { label: '评分', prop: 'score', type: 'rate', isDisabled: () => true, width: 150 },
        { label: '评价内容',
          prop: 'commentContent',
          width: 280,
          overflow: true,
          itemClass: e => (e.score * 1 === 1 ? 'cc-bad' : '') },
        { label: '回复状态',
          prop: 'replyStatus',
          type: 'tag',
          theme: e => (Number(e.replyStatus) === 1 ? 'success' : 'info'),
          formatter: e => (Number(e.replyStatus) === 1 ? '已回复' : '未回复') },
        { label: '处理状态',
          prop: 'handleStatus',
          type: 'tag',
          theme: e => this.handleTheme(e.handleStatus),
          formatter: e => this.handleText(e.handleStatus) },
        { label: '评价时间', prop: 'commentTime', width: 160, formatter: e => this.parseTime(e.commentTime) },
        {
          label: '操作',
          type: 'button',
          width: 240,
          fixed: 'right',
          btnList: [
            { label: '回复', type: 'primary', size: 'mini', isShow: row => this.checkBtn('sys:coachComment:update'), handle: row => this.openReply(row) },
            { label: '处理', type: 'warning', size: 'mini', isShow: row => this.checkBtn('sys:coachComment:update'), handle: row => this.openHandle(row) },
            { label: '编辑', type: 'success', size: 'mini', isShow: row => this.checkBtn('sys:coachComment:update'), handle: row => this.openEdit(row) },
            { label: '删除', type: 'danger', size: 'mini', isShow: row => this.checkBtn('sys:coachComment:delete'), handle: row => this.del(row) }
          ]
        }
      ],
      pagination: { limit: 10, page: 1, offset: 1, total: 0 },
      curRow: {},
      replyVisible: false,
      replyForm: { commentId: '', replyContent: '' },
      handleVisible: false,
      handleForm: { commentId: '', handleStatus: 1, handleRemark: '' },
      editVisible: false,
      editForm: { id: '', commentContent: '', handleStatus: 0, handleRemark: '' },
      addVisible: false,
      addForm: { appointmentId: '', coachId: '', memberId: '', score: 5, commentContent: '' }
    }
  },
  mounted () {
    this.getData()
    this.getStoreList()
    this.getCoachList()
  },
  methods: {
    memberInfo (row) {
      if (!row) return '-'
      if (row.memberInfo) return row.memberInfo
      var name = row.memberNickname || ''
      var mobile = row.memberMobile || ''
      return (name + (mobile ? '（' + mobile + '）' : '')) || '-'
    },
    handleText (s) {
      return Number(s) === 1 ? '已跟进' : (Number(s) === 2 ? '已忽略' : '待处理')
    },
    handleTheme (s) {
      return Number(s) === 1 ? 'success' : (Number(s) === 2 ? 'info' : 'warning')
    },
    async getData () {
      this.tableLoading = true
      try {
        var times = this.searchData.times || []
        var res = await this.apis.coachComment_list({
          page: this.pagination.offset,
          limit: this.pagination.limit,
          coachName: this.searchData.coachName,
          memberKeyword: this.searchData.memberKeyword,
          score: this.searchData.score,
          handleStatus: this.searchData.handleStatus,
          storeId: this.searchData.storeId,
          beginTime: times[0] || '',
          endTime: times[1] || ''
        })
        this.tableData = (res.page && res.page.list) || []
        this.pagination.total = res.page ? res.page.totalCount : 0
      } finally {
        this.tableLoading = false
      }
    },
    page (p) {
      if (p) this.pagination.offset = p.page
      this.getData()
    },
    async getStoreList () {
      var res = await this.apis.store_list({ page: 1, limit: 999 })
      var list = (res.page && res.page.list) || []
      var opts = list.map(function (item) { return { value: item.storeAddrId, label: item.storeName } })
      this.storeOptions = opts
      this.searchForm[this.searchIndex(this.searchForm, '门店')].options = opts
    },
    async getCoachList () {
      // 教练下拉：复用私教教练列表
      try {
        var res = await this.apis.ptCoach_list({ page: 1, limit: 999 })
        var list = (res.page && res.page.list) || []
        this.coachOptions = list.map(function (c) { return { value: c.id, label: c.coachName } })
      } catch (e) { /* 无教练接口时忽略，手填ID */ }
    },
    openReply (row) {
      this.curRow = row
      this.replyForm = { commentId: row.id, replyContent: '' }
      this.replyVisible = true
    },
    async submitReply () {
      if (!this.replyForm.replyContent) return this.$message.warning('请输入回复内容')
      try {
        var res = await this.apis.coachComment_reply(this.replyForm)
        if (res && res.code === 0) {
          this.$message.success('回复成功')
          this.replyVisible = false
          this.getData()
        }
      } catch (e) { /* 拦截器已弹错 */ }
    },
    openHandle (row) {
      this.curRow = row
      this.handleForm = { commentId: row.id, handleStatus: row.handleStatus == null ? 1 : row.handleStatus * 1, handleRemark: row.handleRemark || '' }
      this.handleVisible = true
    },
    async submitHandle () {
      try {
        var res = await this.apis.coachComment_handle(this.handleForm)
        if (res && res.code === 0) {
          this.$message.success('操作成功')
          this.handleVisible = false
          this.getData()
        }
      } catch (e) { /* 拦截器已弹错 */ }
    },
    openEdit (row) {
      this.curRow = row
      this.editForm = {
        id: row.id,
        commentContent: row.commentContent || '',
        handleStatus: row.handleStatus == null ? 0 : row.handleStatus * 1,
        handleRemark: row.handleRemark || ''
      }
      this.editVisible = true
    },
    async submitEdit () {
      try {
        var res = await this.apis.coachComment_update(this.editForm)
        if (res && res.code === 0) {
          this.$message.success('保存成功')
          this.editVisible = false
          this.getData()
        }
      } catch (e) { /* 拦截器已弹错 */ }
    },
    openAdd () {
      this.addForm = { appointmentId: '', coachId: '', memberId: '', score: 5, commentContent: '' }
      this.addVisible = true
    },
    async submitAdd () {
      if (!this.addForm.appointmentId) return this.$message.warning('请填写预约ID')
      try {
        var res = await this.apis.coachComment_save(this.addForm)
        if (res && res.code === 0) {
          this.$message.success('新增成功')
          this.addVisible = false
          this.getData()
        }
      } catch (e) { /* 拦截器已弹错 */ }
    },
    del (row) {
      this.$confirm('确定要删除该评价吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.coachComment_delete([row.id])
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
.cc-readonly {
  white-space: pre-wrap;
  line-height: 1.6;
  color: #606266;
}
</style>
<style>
.cc-bad { color: #f56c6c; font-weight: bold; }
</style>
