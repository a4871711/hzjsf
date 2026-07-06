<template>
  <div>
    <el-tabs v-model="activeTab" @tab-click="onTab">
      <!-- Tab1 预警记录 -->
      <el-tab-pane label="预警记录" name="record">
        <!-- 统计卡片 -->
        <el-row :gutter="16" class="ew-stat">
          <el-col :span="6">
            <div class="ew-card ew-card-blue">
              <div class="ew-num">{{ stat.todayNew || 0 }}</div>
              <div class="ew-label">今日新增</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="ew-card ew-card-orange">
              <div class="ew-num">{{ stat.frequentCancel || 0 }}</div>
              <div class="ew-label">频繁取消预约</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="ew-card ew-card-red">
              <div class="ew-num">{{ stat.lessonAbnormal || 0 }}</div>
              <div class="ew-label">课时消耗异常</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="ew-card ew-card-green">
              <div class="ew-num">{{ stat.enabledRuleCount || 0 }}</div>
              <div class="ew-label">启用规则数</div>
            </div>
          </el-col>
        </el-row>

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

      <!-- Tab2 预警规则 -->
      <el-tab-pane label="预警规则" name="rule">
        <div style="margin-bottom:16px">
          <el-button v-if="checkBtn('sys:exceptionWarning:save')" type="primary" size="small" icon="el-icon-plus" @click="openRuleAdd">新增预警规则</el-button>
        </div>
        <el-table :data="ruleData" v-loading="ruleLoading" border size="medium"
          :header-cell-style="{ backgroundColor: '#f5f7fa', fontWeight: 'bold', color: '#909399' }">
          <el-table-column label="规则名称" prop="ruleName" align="center"></el-table-column>
          <el-table-column label="预警类型" align="center">
            <template slot-scope="scope">
              <el-tag :type="Number(scope.row.warningType) === 1 ? 'warning' : 'danger'" size="mini">{{ warnTypeText(scope.row.warningType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="统计周期" align="center">
            <template slot-scope="scope">{{ scope.row.periodDays }} 天</template>
          </el-table-column>
          <el-table-column label="触发阈值" align="center">
            <template slot-scope="scope">≥ {{ scope.row.triggerThreshold }} 次</template>
          </el-table-column>
          <el-table-column label="适用门店" align="center" :formatter="(row) => storeText(row)"></el-table-column>
          <el-table-column label="备注" prop="remark" align="center" show-overflow-tooltip></el-table-column>
          <el-table-column label="规则状态" align="center">
            <template slot-scope="scope">
              <el-tag :type="Number(scope.row.status) === 1 ? 'success' : 'info'" size="mini">{{ Number(scope.row.status) === 1 ? '启用' : '停用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="180">
            <template slot-scope="scope">
              <el-button v-if="checkBtn('sys:exceptionWarning:update')" type="success" size="mini" icon="el-icon-edit" @click="openRuleEdit(scope.row)">编辑</el-button>
              <el-button v-if="checkBtn('sys:exceptionWarning:delete')" type="danger" size="mini" icon="el-icon-delete" @click="delRule(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 规则弹窗 -->
    <el-dialog :title="ruleForm.id ? '编辑预警规则' : '新增预警规则'" :visible.sync="ruleVisible" width="640px" :close-on-click-modal="false">
      <el-form label-width="120px" size="small">
        <el-form-item label="规则名称" required>
          <el-input v-model="ruleForm.ruleName" style="width:320px" placeholder="请输入规则名称"></el-input>
        </el-form-item>
        <el-form-item label="预警类型">
          <el-radio-group v-model="ruleForm.warningType">
            <el-radio :label="1">频繁取消预约</el-radio>
            <el-radio :label="2">课时消耗异常</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="统计周期">
          <el-input-number v-model="ruleForm.periodDays" :min="1" :max="365"></el-input-number>
          <span class="ew-tip">天</span>
        </el-form-item>
        <el-form-item label="触发阈值">
          <el-input-number v-model="ruleForm.triggerThreshold" :min="1" :max="9999"></el-input-number>
          <span class="ew-tip">次</span>
        </el-form-item>
        <el-form-item label="适用门店">
          <el-select v-model="ruleForm.applicableStoreIds" multiple filterable clearable placeholder="留空=全部门店" style="width:320px">
            <el-option v-for="op in storeOptions" :label="op.label" :value="op.value" :key="op.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="规则状态">
          <el-radio-group v-model="ruleForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" :rows="3" v-model="ruleForm.remark" style="width:320px"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="ruleVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRule">确认</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data () {
    return {
      activeTab: 'record',
      storeOptions: [],
      stat: {},
      // ---- 记录 Tab ----
      recordLoading: false,
      recordData: [],
      searchData: {
        warningType: '',
        memberKeyword: '',
        storeId: '',
        coachName: '',
        times: []
      },
      searchForm: [
        { type: 'select',
          placeholder: '预警类型',
          prop: 'warningType',
          width: 150,
          options: [
            { value: 1, label: '频繁取消预约' },
            { value: 2, label: '课时消耗异常' }
          ] },
        { type: 'input', placeholder: '会员昵称/手机', prop: 'memberKeyword', width: 160 },
        { type: 'input', placeholder: '教练姓名', prop: 'coachName', width: 160 },
        { type: 'select', placeholder: '门店', prop: 'storeId', width: 160, options: [] },
        { type: 'daterange', placeholder: '预警时间', prop: 'times', width: 260 }
      ],
      searchHandle: [
        { label: '搜索', type: 'primary', handle: e => this.getRecord() }
      ],
      recordCols: [
        { label: '预警编号', prop: 'id', width: 80 },
        { label: '预警类型', prop: 'warningType', type: 'tag', theme: e => (Number(e.warningType) === 1 ? 'warning' : 'danger'), formatter: e => this.warnTypeText(e.warningType) },
        { label: '会员', prop: 'memberInfo', formatter: e => (e.memberInfo || e.memberNickname || '-') },
        { label: '关联教练', prop: 'coachName' },
        { label: '门店', prop: 'storeName' },
        { label: '统计周期', prop: 'period', formatter: e => this.periodText(e) },
        { label: '触发原因', prop: 'triggerDesc', width: 200 },
        { label: '触发值', prop: 'triggerValue', width: 80 },
        { label: '预警时间', prop: 'warningTime', width: 160, formatter: e => this.parseTime(e.warningTime) }
      ],
      pagination: { limit: 10, page: 1, offset: 1, total: 0 },
      // ---- 规则 Tab ----
      ruleLoading: false,
      ruleData: [],
      ruleVisible: false,
      ruleForm: this.blankRule()
    }
  },
  mounted () {
    this.getStoreList()
    this.getStat()
    this.getRecord()
    this.getRule()
  },
  methods: {
    blankRule () {
      return {
        id: '',
        ruleName: '',
        warningType: 1,
        periodDays: 7,
        triggerThreshold: 3,
        applicableStoreIds: [],
        status: 1,
        remark: ''
      }
    },
    warnTypeText (t) {
      return Number(t) === 1 ? '频繁取消预约' : (Number(t) === 2 ? '课时消耗异常' : '-')
    },
    periodText (row) {
      if (row.periodStart && row.periodEnd) {
        return this.parseTime(row.periodStart, '{y}-{m}-{d}') + ' ~ ' + this.parseTime(row.periodEnd, '{y}-{m}-{d}')
      }
      return '-'
    },
    storeText (row) {
      var s = row.applicableStoreIds
      if (!s || (Array.isArray(s) && !s.length)) return '全部门店'
      if (Array.isArray(s)) {
        var self = this
        return s.map(function (id) { return self.storeName(id) }).join('、')
      }
      return s
    },
    storeName (id) {
      var hit = this.storeOptions.filter(function (o) { return String(o.value) === String(id) })[0]
      return hit ? hit.label : id
    },
    onTab () {
      if (this.activeTab === 'record') {
        this.getStat()
      }
    },
    async getStoreList () {
      var res = await this.apis.store_list({ page: 1, limit: 999 })
      var list = (res.page && res.page.list) || []
      var opts = list.map(function (item) { return { value: item.storeAddrId, label: item.storeName } })
      this.storeOptions = opts
      this.searchForm[this.searchIndex(this.searchForm, '门店')].options = opts
    },
    async getStat () {
      try {
        var res = await this.apis.exceptionWarning_stat({})
        this.stat = (res && res.stat) || {}
      } catch (e) { this.stat = {} }
    },
    // ---- 记录 ----
    async getRecord () {
      this.recordLoading = true
      try {
        var times = this.searchData.times || []
        var res = await this.apis.exceptionWarning_recordList({
          page: this.pagination.offset,
          limit: this.pagination.limit,
          warningType: this.searchData.warningType,
          memberKeyword: this.searchData.memberKeyword,
          coachName: this.searchData.coachName,
          storeId: this.searchData.storeId,
          beginTime: times[0] || '',
          endTime: times[1] || ''
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
    // ---- 规则 ----
    async getRule () {
      this.ruleLoading = true
      try {
        var res = await this.apis.exceptionWarning_ruleList({ page: 1, limit: 999 })
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
      form.warningType = Number(row.warningType)
      form.periodDays = row.periodDays || 1
      form.triggerThreshold = row.triggerThreshold || 1
      form.status = Number(row.status)
      form.remark = row.remark || ''
      try {
        var res = await this.apis.exceptionWarning_ruleInfo({ id: row.id })
        var rule = (res && res.rule) || {}
        form.applicableStoreIds = rule.applicableStoreIds || []
      } catch (e) { form.applicableStoreIds = [] }
      this.ruleForm = form
      this.ruleVisible = true
    },
    async submitRule () {
      var f = this.ruleForm
      if (!f.ruleName) return this.$message.warning('请输入规则名称')
      if (!(f.periodDays > 0)) return this.$message.warning('统计周期需大于0')
      if (!(f.triggerThreshold > 0)) return this.$message.warning('触发阈值需大于0')
      var data = {
        id: f.id,
        ruleName: f.ruleName,
        warningType: f.warningType,
        periodDays: f.periodDays,
        triggerThreshold: f.triggerThreshold,
        applicableStoreIds: f.applicableStoreIds || [],
        status: f.status,
        remark: f.remark
      }
      try {
        var res = f.id ? await this.apis.exceptionWarning_ruleUpdate(data) : await this.apis.exceptionWarning_ruleSave(data)
        if (res && res.code === 0) {
          this.$message.success('操作成功')
          this.ruleVisible = false
          this.getRule()
          this.getStat()
        }
      } catch (e) { /* 拦截器已弹错 */ }
    },
    delRule (row) {
      this.$confirm('确定要删除该规则吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.exceptionWarning_ruleDelete([row.id])
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('删除成功')
          this.getRule()
          this.getStat()
        }
      }).catch(() => {})
    }
  }
}
</script>

<style scoped lang="scss">
.ew-stat {
  margin-bottom: 20px;
}
.ew-card {
  padding: 18px 20px;
  border-radius: 6px;
  color: #fff;
  .ew-num {
    font-size: 28px;
    font-weight: bold;
    line-height: 1.2;
  }
  .ew-label {
    margin-top: 6px;
    font-size: 13px;
    opacity: 0.9;
  }
}
.ew-card-blue { background: #409eff; }
.ew-card-orange { background: #e6a23c; }
.ew-card-red { background: #f56c6c; }
.ew-card-green { background: #67c23a; }
.ew-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}
</style>
