<template>
  <div>
    <el-tabs v-model="activeTab">
      <!-- Tab1 转化名单 -->
      <el-tab-pane label="转化名单" name="lead">
        <r-search ref="search" :searchData="searchData" :searchForm="searchForm" :searchHandle="searchHandle" />
        <r-table
          :isSelection="false"
          :isHandle="false"
          :isPagination="true"
          :tableData="leadData"
          :tableCols="leadCols"
          :tablePage="pagination"
          :loading="leadLoading"
          @refresh="pageLead" />
      </el-tab-pane>

      <!-- Tab2 转化规则 -->
      <el-tab-pane label="识别规则" name="rule">
        <div style="margin-bottom:16px">
          <el-button v-if="checkBtn('sys:groupToPrivate:save')" type="primary" size="small" icon="el-icon-plus" @click="openRuleAdd">新增规则</el-button>
        </div>
        <el-table :data="ruleData" v-loading="ruleLoading" border size="medium"
          :header-cell-style="{ backgroundColor: '#f5f7fa', fontWeight: 'bold', color: '#909399' }">
          <el-table-column label="规则名称" prop="ruleName" align="center"></el-table-column>
          <el-table-column label="出勤维度" align="center">
            <template slot-scope="scope">{{ scope.row.attendanceDays }}天内出勤 ≥ {{ scope.row.attendanceThreshold }}次</template>
          </el-table-column>
          <el-table-column label="购课维度" align="center">
            <template slot-scope="scope">{{ scope.row.purchaseDays }}天内购课 ≥ {{ scope.row.purchaseThreshold }}次</template>
          </el-table-column>
          <el-table-column label="备注" prop="remark" align="center" show-overflow-tooltip></el-table-column>
          <el-table-column label="规则状态" align="center">
            <template slot-scope="scope">
              <el-tag :type="Number(scope.row.status) === 1 ? 'success' : 'info'" size="mini">{{ Number(scope.row.status) === 1 ? '启用' : '停用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="180">
            <template slot-scope="scope">
              <el-button v-if="checkBtn('sys:groupToPrivate:update')" type="success" size="mini" icon="el-icon-edit" @click="openRuleEdit(scope.row)">编辑</el-button>
              <el-button v-if="checkBtn('sys:groupToPrivate:delete')" type="danger" size="mini" icon="el-icon-delete" @click="delRule(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 规则弹窗 -->
    <el-dialog :title="ruleForm.id ? '编辑规则' : '新增规则'" :visible.sync="ruleVisible" width="640px" :close-on-click-modal="false">
      <el-form label-width="130px" size="small">
        <el-form-item label="规则名称" required>
          <el-input v-model="ruleForm.ruleName" style="width:320px" placeholder="请输入规则名称"></el-input>
        </el-form-item>
        <el-form-item label="出勤统计周期">
          <el-input-number v-model="ruleForm.attendanceDays" :min="0" :max="365"></el-input-number>
          <span class="gp-tip">天</span>
        </el-form-item>
        <el-form-item label="出勤次数阈值">
          <el-input-number v-model="ruleForm.attendanceThreshold" :min="0" :max="9999"></el-input-number>
          <span class="gp-tip">次（≥）</span>
        </el-form-item>
        <el-form-item label="购课统计周期">
          <el-input-number v-model="ruleForm.purchaseDays" :min="0" :max="365"></el-input-number>
          <span class="gp-tip">天</span>
        </el-form-item>
        <el-form-item label="购课次数阈值">
          <el-input-number v-model="ruleForm.purchaseThreshold" :min="0" :max="9999"></el-input-number>
          <span class="gp-tip">次（≥）</span>
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

    <!-- 发券弹窗 -->
    <el-dialog title="发放私教体验券" :visible.sync="couponVisible" width="520px" :close-on-click-modal="false">
      <el-form label-width="90px" size="small">
        <el-form-item label="会员">{{ curRow.memberInfo || curRow.memberNickname || '-' }}</el-form-item>
        <el-form-item label="选择券">
          <el-select v-model="couponForm.couponId" filterable clearable placeholder="请选择优惠券" style="width:100%">
            <el-option v-for="op in couponOptions" :label="op.label" :value="op.value" :key="op.value"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="couponVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCoupon">确认发放</el-button>
      </span>
    </el-dialog>

    <!-- 跟进弹窗 -->
    <el-dialog title="跟进" :visible.sync="followVisible" width="680px" :close-on-click-modal="false">
      <el-descriptions :column="2" border size="small" style="margin-bottom:16px">
        <el-descriptions-item label="会员信息">{{ curRow.memberInfo || curRow.memberNickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所属门店">{{ curRow.storeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="团课出勤">{{ curRow.attendanceCount }} 次</el-descriptions-item>
        <el-descriptions-item label="团课购课">{{ curRow.purchaseCount }} 次</el-descriptions-item>
        <el-descriptions-item label="高意向原因" :span="2">{{ curRow.intentionReason || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="followList.length" class="gp-history">
        <div class="gp-history-title">历史跟进</div>
        <div v-for="(f, idx) in followList" :key="idx" class="gp-history-item">
          <span class="gp-history-time">{{ parseTime(f.createTime || f.createdDate) }}</span>
          <el-tag size="mini" style="margin:0 8px">{{ followText(f.followStatus) }}</el-tag>
          <span>{{ f.followRemark }}</span>
        </div>
      </div>
      <el-form label-width="90px" size="small">
        <el-form-item label="跟进状态">
          <el-radio-group v-model="followForm.followStatus">
            <el-radio :label="1">已跟进</el-radio>
            <el-radio :label="3">已放弃</el-radio>
          </el-radio-group>
        </el-form-item>
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
      activeTab: 'lead',
      storeOptions: [],
      couponOptions: [],
      // ---- 名单 Tab ----
      leadLoading: false,
      leadData: [],
      searchData: {
        memberKeyword: '',
        storeId: '',
        followStatus: ''
      },
      searchForm: [
        { type: 'input', placeholder: '会员姓名/手机', prop: 'memberKeyword', width: 160 },
        { type: 'select', placeholder: '门店', prop: 'storeId', width: 160, options: [] },
        { type: 'select',
          placeholder: '跟进状态',
          prop: 'followStatus',
          width: 140,
          options: [
            { value: 0, label: '待跟进' },
            { value: 1, label: '已跟进' },
            { value: 2, label: '已转化' },
            { value: 3, label: '已放弃' }
          ] }
      ],
      searchHandle: [
        { label: '搜索', type: 'primary', handle: e => this.getLead() }
      ],
      leadCols: [
        { label: '会员信息', prop: 'memberInfo', formatter: e => (e.memberInfo || e.memberNickname || '-') },
        { label: '所属门店', prop: 'storeName' },
        { label: '团课出勤(次)', prop: 'attendanceCount', width: 110 },
        { label: '团课购课(次)', prop: 'purchaseCount', width: 110 },
        { label: '高意向原因', prop: 'intentionReason', width: 200 },
        { label: '体验券状态', prop: 'experienceCouponStatus', type: 'tag', theme: e => this.couponTheme(e.experienceCouponStatus), formatter: e => this.couponText(e.experienceCouponStatus) },
        { label: '跟进状态', prop: 'followStatus', type: 'tag', theme: e => this.followTheme(e.followStatus), formatter: e => this.followText(e.followStatus) },
        { label: '跟进人', prop: 'followByName', formatter: e => (e.followByName || e.followBy || '-') },
        { label: '最近跟进', prop: 'lastFollowTime', width: 160, formatter: e => (e.lastFollowTime ? this.parseTime(e.lastFollowTime) : '-') },
        {
          label: '操作',
          type: 'button',
          width: 240,
          fixed: 'right',
          btnList: [
            { label: '发券', type: 'primary', size: 'mini', disabled: row => Number(row.experienceCouponStatus) !== 0, isShow: row => this.checkBtn('sys:groupToPrivate:sendCoupon'), handle: row => this.openCoupon(row) },
            { label: '跟进', type: 'warning', size: 'mini', isShow: row => this.checkBtn('sys:groupToPrivate:follow'), handle: row => this.openFollow(row) },
            { label: '已转化', type: 'success', size: 'mini', disabled: row => Number(row.followStatus) === 2, isShow: row => this.checkBtn('sys:groupToPrivate:markConverted'), handle: row => this.markConverted(row) }
          ]
        }
      ],
      pagination: { limit: 10, page: 1, offset: 1, total: 0 },
      // ---- 规则 Tab ----
      ruleLoading: false,
      ruleData: [],
      ruleVisible: false,
      ruleForm: this.blankRule(),
      // ---- 弹窗 ----
      curRow: {},
      couponVisible: false,
      couponForm: { leadId: '', couponId: '' },
      followVisible: false,
      followList: [],
      followForm: { leadId: '', followStatus: 1, followRemark: '' }
    }
  },
  mounted () {
    this.getStoreList()
    this.getCouponList()
    this.getLead()
    this.getRule()
  },
  methods: {
    blankRule () {
      return {
        id: '',
        ruleName: '',
        attendanceDays: 30,
        attendanceThreshold: 3,
        purchaseDays: 30,
        purchaseThreshold: 1,
        status: 1,
        remark: ''
      }
    },
    couponText (s) {
      var n = Number(s)
      return n === 1 ? '已发放' : (n === 2 ? '已使用' : '未发放')
    },
    couponTheme (s) {
      var n = Number(s)
      return n === 2 ? 'success' : (n === 1 ? 'primary' : 'info')
    },
    followText (s) {
      var n = Number(s)
      return n === 1 ? '已跟进' : (n === 2 ? '已转化' : (n === 3 ? '已放弃' : '待跟进'))
    },
    followTheme (s) {
      var n = Number(s)
      return n === 2 ? 'success' : (n === 1 ? 'primary' : (n === 3 ? 'info' : 'warning'))
    },
    async getStoreList () {
      var res = await this.apis.store_list({ page: 1, limit: 999 })
      var list = (res.page && res.page.list) || []
      var opts = list.map(function (item) { return { value: item.storeAddrId, label: item.storeName } })
      this.storeOptions = opts
      this.searchForm[this.searchIndex(this.searchForm, '门店')].options = opts
    },
    async getCouponList () {
      try {
        var res = await this.apis.mkCoupon_list({ page: 1, limit: 999 })
        var list = (res.page && res.page.list) || []
        this.couponOptions = list.map(function (c) { return { value: c.id, label: c.couponName || c.name } })
      } catch (e) { this.couponOptions = [] }
    },
    // ---- 名单 ----
    async getLead () {
      this.leadLoading = true
      try {
        var res = await this.apis.groupToPrivate_leadList({
          page: this.pagination.offset,
          limit: this.pagination.limit,
          memberKeyword: this.searchData.memberKeyword,
          storeId: this.searchData.storeId,
          followStatus: this.searchData.followStatus
        })
        this.leadData = (res.page && res.page.list) || []
        this.pagination.total = res.page ? res.page.totalCount : 0
      } finally {
        this.leadLoading = false
      }
    },
    pageLead (p) {
      if (p) this.pagination.offset = p.page
      this.getLead()
    },
    openCoupon (row) {
      this.curRow = row
      this.couponForm = { leadId: row.id, couponId: '' }
      this.couponVisible = true
    },
    async submitCoupon () {
      if (!this.couponForm.couponId) return this.$message.warning('请选择优惠券')
      try {
        var res = await this.apis.groupToPrivate_sendCoupon(this.couponForm)
        if (res && res.code === 0) {
          this.$message.success('发放成功')
          this.couponVisible = false
          this.getLead()
        }
      } catch (e) { /* 拦截器已弹错 */ }
    },
    async openFollow (row) {
      this.curRow = row
      this.followForm = { leadId: row.id, followStatus: 1, followRemark: '' }
      this.followList = []
      this.followVisible = true
      try {
        var res = await this.apis.groupToPrivate_leadInfo({ id: row.id })
        this.followList = (res && res.followList) || []
        if (res && res.lead) this.curRow = Object.assign({}, row, res.lead)
      } catch (e) { /* 忽略 */ }
    },
    async submitFollow () {
      if (!this.followForm.followRemark) return this.$message.warning('请输入跟进内容')
      try {
        var res = await this.apis.groupToPrivate_follow(this.followForm)
        if (res && res.code === 0) {
          this.$message.success('跟进成功')
          this.followVisible = false
          this.getLead()
        }
      } catch (e) { /* 拦截器已弹错 */ }
    },
    markConverted (row) {
      this.$confirm('确认标记该会员为已转化？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.groupToPrivate_markConverted({ leadId: row.id })
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('操作成功')
          this.getLead()
        }
      }).catch(() => {})
    },
    // ---- 规则 ----
    async getRule () {
      this.ruleLoading = true
      try {
        var res = await this.apis.groupToPrivate_ruleList({ page: 1, limit: 999 })
        this.ruleData = (res.page && res.page.list) || []
      } finally {
        this.ruleLoading = false
      }
    },
    openRuleAdd () {
      this.ruleForm = this.blankRule()
      this.ruleVisible = true
    },
    openRuleEdit (row) {
      var form = this.blankRule()
      form.id = row.id
      form.ruleName = row.ruleName
      form.attendanceDays = row.attendanceDays || 0
      form.attendanceThreshold = row.attendanceThreshold || 0
      form.purchaseDays = row.purchaseDays || 0
      form.purchaseThreshold = row.purchaseThreshold || 0
      form.status = Number(row.status)
      form.remark = row.remark || ''
      this.ruleForm = form
      this.ruleVisible = true
    },
    async submitRule () {
      var f = this.ruleForm
      if (!f.ruleName) return this.$message.warning('请输入规则名称')
      var attOn = f.attendanceDays > 0 && f.attendanceThreshold > 0
      var purOn = f.purchaseDays > 0 && f.purchaseThreshold > 0
      if (!attOn && !purOn) return this.$message.warning('出勤维度与购课维度至少配置一组')
      try {
        var res = f.id ? await this.apis.groupToPrivate_ruleUpdate(f) : await this.apis.groupToPrivate_ruleSave(f)
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
        return this.apis.groupToPrivate_ruleDelete([row.id])
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('删除成功')
          this.getRule()
        }
      }).catch(() => {})
    }
  }
}
</script>

<style scoped lang="scss">
.gp-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}
.gp-history {
  margin-bottom: 16px;
  padding: 10px 14px;
  background: #f5f7fa;
  border-radius: 4px;
  max-height: 180px;
  overflow-y: auto;
  .gp-history-title {
    color: #909399;
    font-size: 12px;
    margin-bottom: 8px;
  }
  .gp-history-item {
    padding: 4px 0;
    font-size: 13px;
    .gp-history-time {
      color: #909399;
    }
  }
}
</style>
