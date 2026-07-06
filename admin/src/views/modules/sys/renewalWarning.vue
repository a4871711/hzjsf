<template>
  <div>
    <el-tabs v-model="activeTab">
      <!-- Tab1 预警规则 -->
      <el-tab-pane label="预警规则" name="rule">
        <div style="margin-bottom:16px">
          <el-button v-if="checkBtn('sys:renewalWarning:save')" type="primary" size="small" icon="el-icon-plus" @click="openRuleAdd">新增规则</el-button>
        </div>
        <el-table :data="ruleData" v-loading="ruleLoading" border size="medium"
          :header-cell-style="{ backgroundColor: '#f5f7fa', fontWeight: 'bold', color: '#909399' }">
          <el-table-column label="规则名称" prop="ruleName" align="center"></el-table-column>
          <el-table-column label="课时预警" align="center">
            <template slot-scope="scope">
              <span v-if="Number(scope.row.lessonWarningEnabled) === 1">剩余课时 ≤ {{ scope.row.lessonThreshold }}</span>
              <span v-else>未开启</span>
            </template>
          </el-table-column>
          <el-table-column label="有效期预警" align="center">
            <template slot-scope="scope">
              <span v-if="Number(scope.row.daysWarningEnabled) === 1">剩余天数 &lt; {{ scope.row.daysThreshold }}</span>
              <span v-else>未开启</span>
            </template>
          </el-table-column>
          <el-table-column label="提醒对象" align="center">
            <template slot-scope="scope"><el-tag size="mini">教练</el-tag></template>
          </el-table-column>
          <el-table-column label="适用门店" prop="applicableStores" align="center"
            :formatter="(row) => storeText(row)"></el-table-column>
          <el-table-column label="状态" align="center">
            <template slot-scope="scope">
              <el-tag :type="Number(scope.row.status) === 1 ? 'success' : 'info'" size="mini">{{ Number(scope.row.status) === 1 ? '启用' : '停用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="180">
            <template slot-scope="scope">
              <el-button v-if="checkBtn('sys:renewalWarning:update')" type="success" size="mini" icon="el-icon-edit" @click="openRuleEdit(scope.row)">编辑</el-button>
              <el-button v-if="checkBtn('sys:renewalWarning:delete')" type="danger" size="mini" icon="el-icon-delete" @click="delRule(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- Tab2 预警记录 -->
      <el-tab-pane label="预警记录" name="record">
        <r-search ref="search" :searchData="searchData" :searchForm="searchForm" :searchHandle="searchHandle" />
        <r-table
          :isSelection="false"
          :isHandle="false"
          :isPagination="true"
          :tableData="recordData"
          :tableCols="recordCols"
          :tablePage="pagination"
          :loading="recordLoading"
          @refresh="pageRecord" />
      </el-tab-pane>
    </el-tabs>

    <!-- 规则弹窗 -->
    <el-dialog :title="ruleForm.id ? '编辑规则' : '新增规则'" :visible.sync="ruleVisible" width="640px" :close-on-click-modal="false">
      <el-form label-width="130px" size="small">
        <el-form-item label="规则名称" required>
          <el-input v-model="ruleForm.ruleName" style="width:320px" placeholder="请输入规则名称"></el-input>
        </el-form-item>
        <el-form-item label="剩余课时预警">
          <el-switch v-model="ruleForm.lessonWarningEnabled" :active-value="1" :inactive-value="0"></el-switch>
        </el-form-item>
        <el-form-item label="课时阈值" v-if="Number(ruleForm.lessonWarningEnabled) === 1">
          <el-input-number v-model="ruleForm.lessonThreshold" :min="1" :max="9999"></el-input-number>
          <span class="rw-tip">剩余课时 ≤ 该值时预警</span>
        </el-form-item>
        <el-form-item label="剩余天数预警">
          <el-switch v-model="ruleForm.daysWarningEnabled" :active-value="1" :inactive-value="0"></el-switch>
        </el-form-item>
        <el-form-item label="天数阈值" v-if="Number(ruleForm.daysWarningEnabled) === 1">
          <el-input-number v-model="ruleForm.daysThreshold" :min="1" :max="9999"></el-input-number>
          <span class="rw-tip">剩余天数 &lt; 该值时预警</span>
        </el-form-item>
        <el-form-item label="提醒对象">
          <el-tag size="small">教练（固定）</el-tag>
        </el-form-item>
        <el-form-item label="适用门店">
          <el-select v-model="ruleForm.storeIds" multiple filterable clearable placeholder="留空=全部门店" style="width:320px">
            <el-option v-for="op in storeOptions" :label="op.label" :value="op.value" :key="op.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="规则状态">
          <el-radio-group v-model="ruleForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="ruleVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRule">确认</el-button>
      </span>
    </el-dialog>

    <!-- 跟进抽屉 -->
    <el-dialog title="跟进" :visible.sync="followVisible" width="680px" :close-on-click-modal="false">
      <el-descriptions :column="2" border size="small" style="margin-bottom:16px">
        <el-descriptions-item label="会员信息">{{ curRow.memberInfo || curRow.memberNickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="关联教练">{{ curRow.coachName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属门店">{{ curRow.storeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="私教商品">{{ curRow.productName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="剩余课时">{{ curRow.remainingLessons }}</el-descriptions-item>
        <el-descriptions-item label="剩余天数">{{ curRow.remainingDays }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="curRow.followRemark" class="rw-history">
        <div class="rw-history-title">最近跟进备注</div>
        <div>{{ curRow.followRemark }}</div>
      </div>
      <el-form label-width="90px" size="small">
        <el-form-item label="跟进内容">
          <el-input type="textarea" :rows="4" v-model="followForm.followRemark" placeholder="请输入跟进内容"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="followVisible = false">取消</el-button>
        <el-button type="primary" @click="submitFollow">提交跟进</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data () {
    return {
      activeTab: 'rule',
      storeOptions: [],
      // ---- 规则 Tab ----
      ruleLoading: false,
      ruleData: [],
      ruleVisible: false,
      ruleForm: this.blankRule(),
      // ---- 记录 Tab ----
      recordLoading: false,
      recordData: [],
      searchData: {
        storeId: '',
        memberKeyword: '',
        coachName: '',
        followStatus: ''
      },
      searchForm: [
        { type: 'select', placeholder: '门店', prop: 'storeId', width: 160, options: [] },
        { type: 'input', placeholder: '会员昵称/手机', prop: 'memberKeyword', width: 160 },
        { type: 'input', placeholder: '教练姓名', prop: 'coachName', width: 160 },
        { type: 'select',
          placeholder: '跟进状态',
          prop: 'followStatus',
          width: 140,
          options: [
            { value: 0, label: '待跟进' },
            { value: 1, label: '已跟进' },
            { value: 2, label: '已续费' },
            { value: 3, label: '已忽略' }
          ] }
      ],
      searchHandle: [
        { label: '搜索', type: 'primary', handle: e => this.getRecord() }
      ],
      recordCols: [
        { label: '会员信息', prop: 'memberInfo', formatter: e => (e.memberInfo || e.memberNickname || '-') },
        { label: '关联教练', prop: 'coachName' },
        { label: '所属门店', prop: 'storeName' },
        { label: '私教商品', prop: 'productName' },
        { label: '剩余课时', prop: 'remainingLessons', itemClass: e => this.lessonDanger(e) },
        { label: '剩余天数', prop: 'remainingDays', itemClass: e => this.daysDanger(e) },
        { label: '预警类型', prop: 'warningType', type: 'tag', theme: e => 'warning', formatter: e => this.warnTypeText(e.warningType) },
        { label: '跟进状态', prop: 'followStatus', type: 'tag', theme: e => this.followTheme(e.followStatus), formatter: e => this.followText(e.followStatus) },
        { label: '预警时间', prop: 'lastWarningAt', width: 160, formatter: e => this.parseTime(e.lastWarningAt) },
        {
          label: '操作',
          type: 'button',
          width: 220,
          fixed: 'right',
          btnList: [
            { label: '跟进', type: 'primary', size: 'mini', isShow: row => this.checkBtn('sys:renewalWarning:update'), handle: row => this.openFollow(row) },
            { label: '已续费', type: 'success', size: 'mini', isShow: row => this.checkBtn('sys:renewalWarning:update'), handle: row => this.markStatus(row, 2) },
            { label: '忽略', type: 'info', size: 'mini', isShow: row => this.checkBtn('sys:renewalWarning:update'), handle: row => this.markStatus(row, 3) }
          ]
        }
      ],
      pagination: { limit: 10, page: 1, offset: 1, total: 0 },
      curRow: {},
      followVisible: false,
      followForm: { recordId: '', followRemark: '' }
    }
  },
  mounted () {
    this.getStoreList()
    this.getRule()
    this.getRecord()
  },
  methods: {
    blankRule () {
      return {
        id: '',
        ruleName: '',
        lessonWarningEnabled: 1,
        lessonThreshold: 3,
        daysWarningEnabled: 1,
        daysThreshold: 7,
        status: 1,
        storeIds: []
      }
    },
    warnTypeText (t) {
      var n = Number(t)
      return n === 1 ? '课时不足' : (n === 2 ? '有效期不足' : (n === 3 ? '同时命中' : '-'))
    },
    followText (s) {
      var n = Number(s)
      return n === 1 ? '已跟进' : (n === 2 ? '已续费' : (n === 3 ? '已忽略' : '待跟进'))
    },
    followTheme (s) {
      var n = Number(s)
      return n === 2 ? 'success' : (n === 1 ? 'primary' : (n === 3 ? 'info' : 'warning'))
    },
    lessonDanger (row) {
      return (Number(row.warningType) === 1 || Number(row.warningType) === 3) ? 'rw-danger' : ''
    },
    daysDanger (row) {
      return (Number(row.warningType) === 2 || Number(row.warningType) === 3) ? 'rw-danger' : ''
    },
    storeText (row) {
      var s = row.applicableStores
      if (!s || (Array.isArray(s) && !s.length)) return '全部门店'
      return Array.isArray(s) ? s.join('、') : s
    },
    async getStoreList () {
      var res = await this.apis.store_list({ page: 1, limit: 999 })
      var list = (res.page && res.page.list) || []
      var opts = list.map(function (item) { return { value: item.storeAddrId, label: item.storeName } })
      this.storeOptions = opts
      this.searchForm[this.searchIndex(this.searchForm, '门店')].options = opts
    },
    // ---- 规则 ----
    async getRule () {
      this.ruleLoading = true
      try {
        var res = await this.apis.renewalWarning_ruleList({ page: 1, limit: 999 })
        this.ruleData = (res.page && res.page.list) || []
      } finally {
        this.ruleLoading = false
      }
    },
    openRuleAdd () {
      this.ruleForm = this.blankRule()
      this.ruleVisible = true
    },
    async openRuleEdit (row) {
      var form = this.blankRule()
      form.id = row.id
      form.ruleName = row.ruleName
      form.lessonWarningEnabled = Number(row.lessonWarningEnabled)
      form.lessonThreshold = row.lessonThreshold || 1
      form.daysWarningEnabled = Number(row.daysWarningEnabled)
      form.daysThreshold = row.daysThreshold || 1
      form.status = Number(row.status)
      // 拉详情取 storeIds 回显
      try {
        var res = await this.apis.renewalWarning_ruleInfo({ id: row.id })
        var rule = (res && res.rule) || {}
        form.storeIds = rule.storeIds || []
      } catch (e) { form.storeIds = [] }
      this.ruleForm = form
      this.ruleVisible = true
    },
    async submitRule () {
      var f = this.ruleForm
      if (!f.ruleName) return this.$message.warning('请输入规则名称')
      var lessonOn = Number(f.lessonWarningEnabled) === 1
      var daysOn = Number(f.daysWarningEnabled) === 1
      if (!lessonOn && !daysOn) return this.$message.warning('课时预警与天数预警至少启用一个')
      if (lessonOn && !(f.lessonThreshold > 0)) return this.$message.warning('请填写课时阈值(>0)')
      if (daysOn && !(f.daysThreshold > 0)) return this.$message.warning('请填写天数阈值(>0)')
      var data = {
        id: f.id,
        ruleName: f.ruleName,
        lessonWarningEnabled: f.lessonWarningEnabled,
        lessonThreshold: lessonOn ? f.lessonThreshold : null,
        daysWarningEnabled: f.daysWarningEnabled,
        daysThreshold: daysOn ? f.daysThreshold : null,
        status: f.status,
        storeIds: f.storeIds || []
      }
      try {
        var res = f.id ? await this.apis.renewalWarning_ruleUpdate(data) : await this.apis.renewalWarning_ruleSave(data)
        if (res && res.code === 0) {
          this.$message.success('操作成功')
          this.ruleVisible = false
          this.getRule()
        }
      } catch (e) { /* 拦截器已弹错 */ }
    },
    delRule (row) {
      this.$confirm('确定要删除该规则吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.renewalWarning_ruleDelete([row.id])
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('删除成功')
          this.getRule()
        }
      }).catch(() => {})
    },
    // ---- 记录 ----
    async getRecord () {
      this.recordLoading = true
      try {
        var res = await this.apis.renewalWarning_recordList({
          page: this.pagination.offset,
          limit: this.pagination.limit,
          storeId: this.searchData.storeId,
          memberKeyword: this.searchData.memberKeyword,
          coachName: this.searchData.coachName,
          followStatus: this.searchData.followStatus
        })
        this.recordData = (res.page && res.page.list) || []
        this.pagination.total = res.page ? res.page.totalCount : 0
      } finally {
        this.recordLoading = false
      }
    },
    pageRecord (p) {
      if (p) this.pagination.offset = p.page
      this.getRecord()
    },
    openFollow (row) {
      this.curRow = row
      this.followForm = { recordId: row.id, followRemark: '' }
      this.followVisible = true
    },
    async submitFollow () {
      if (!this.followForm.followRemark) return this.$message.warning('请输入跟进内容')
      try {
        var res = await this.apis.renewalWarning_follow(this.followForm)
        if (res && res.code === 0) {
          this.$message.success('跟进成功')
          this.followVisible = false
          this.getRecord()
        }
      } catch (e) { /* 拦截器已弹错 */ }
    },
    markStatus (row, status) {
      var tip = status === 2 ? '标记为已续费？' : '忽略该预警？'
      this.$confirm(tip, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.renewalWarning_markStatus({ recordId: row.id, followStatus: status })
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('操作成功')
          this.getRecord()
        }
      }).catch(() => {})
    }
  }
}
</script>

<style scoped lang="scss">
.rw-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}
.rw-history {
  margin-bottom: 16px;
  padding: 10px 14px;
  background: #f5f7fa;
  border-radius: 4px;
  .rw-history-title {
    color: #909399;
    font-size: 12px;
    margin-bottom: 6px;
  }
}
</style>
<style>
.rw-danger { color: #f56c6c; font-weight: bold; }
</style>
