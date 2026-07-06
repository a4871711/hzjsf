<template>
  <div>
    <div class="page-head">
      <div class="page-title">教练排班</div>
      <div class="page-desc">配置教练每周固定可约时间池（只决定可用时间，不直接决定容量）。左侧选教练，右侧维护周排班。</div>
    </div>
    <r-search ref="search" :searchData="searchData" :searchForm="searchForm" :searchHandle="searchHandle" />
    <div class="sch-body">
      <!-- 左侧教练栏 -->
      <div class="sch-left" v-loading="coachLoading">
        <div
          v-for="c in coachList"
          :key="c.id"
          class="coach-item"
          :class="{ active: currentCoach && currentCoach.id === c.id }"
          @click="selectCoach(c)">
          <span class="coach-avatar">{{ c.coachName ? c.coachName.charAt(0) : '?' }}</span>
          <div class="coach-info">
            <div class="coach-name">{{ c.coachName }}</div>
            <div class="coach-sub">{{ c.storeNames || '未分配门店' }}<span v-if="c.coachLevel"> · {{ c.coachLevel }}</span></div>
          </div>
          <span class="coach-badge" :class="scheduleBadgeClass(c)">{{ scheduleBadge(c) }}</span>
        </div>
        <div v-if="!coachLoading && !coachList.length" class="coach-empty">暂无教练</div>
      </div>
      <!-- 右侧周网格 -->
      <div class="sch-right">
        <div class="sch-right-head">
          <span class="cur-title" v-if="currentCoach">{{ currentCoach.coachName }} 的每周排班</span>
          <span class="cur-title placeholder" v-else>请选择左侧教练</span>
          <el-button
            type="primary"
            size="small"
            icon="el-icon-plus"
            :disabled="!currentCoach"
            v-if="checkBtn('sys:schedule:save')"
            @click="openAdd">新增可约时间</el-button>
        </div>
        <div class="week-grid" v-loading="scheduleLoading">
          <div class="week-col" v-for="w in weekdays" :key="w.value">
            <div class="week-col-head">{{ w.label }}</div>
            <div class="week-col-body">
              <div
                v-for="s in slotsOf(w.value)"
                :key="s.id"
                class="slot-card"
                :class="{ disabled: s.isEnabled === 0 }">
                <div class="slot-time">{{ s.startTime }} - {{ s.endTime }}</div>
                <div class="slot-store">{{ s.storeName || storeMap[s.storeId] || ('门店#' + s.storeId) }}</div>
                <div class="slot-ops">
                  <el-switch
                    v-model="s.isEnabled"
                    :active-value="1"
                    :inactive-value="0"
                    :disabled="!checkBtn('sys:schedule:changeEnabled')"
                    @change="onToggleEnabled(s)" />
                  <i class="el-icon-delete slot-del" v-if="checkBtn('sys:schedule:delete')" @click="delSlot(s)"></i>
                </div>
              </div>
              <div v-if="currentCoach && !slotsOf(w.value).length" class="slot-empty">—</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增排班表单（含自定义多行时间段 slot） -->
    <r-form labelWidth="130px" :isHandle="true" :formRules="formRules" :formCols="formCols" :formHandle="formHandle" :formData="formData" ref="elForm" :inline="false" dialogWidth="560px">
      <template slot="timeRanges">
        <div class="tr-wrap">
          <div class="tr-row" v-for="(r, idx) in timeRanges" :key="idx">
            <el-time-select
              placeholder="开始"
              v-model="r.startTime"
              :picker-options="{ start: '00:00', step: '00:30', end: '23:30' }"
              style="width: 120px;" />
            <span class="tr-sep">至</span>
            <el-time-select
              placeholder="结束"
              v-model="r.endTime"
              :picker-options="{ start: '00:00', step: '00:30', end: '24:00', minTime: r.startTime }"
              style="width: 120px;" />
            <el-button type="text" icon="el-icon-remove-outline" class="tr-del" @click="removeRange(idx)" v-if="timeRanges.length > 1">删除</el-button>
          </div>
          <el-button type="text" icon="el-icon-circle-plus-outline" @click="addRange">添加时间段</el-button>
        </div>
      </template>
    </r-form>
  </div>
</template>

<script>
export default {
  components: {},
  data () {
    return {
      coachLoading: false,
      scheduleLoading: false,
      storeMap: {},
      searchData: {
        storeId: '',
        coachName: '',
        hasSchedule: ''
      },
      searchForm: [
        { type: 'select', placeholder: '所属门店', prop: 'storeId', width: 180, options: [] },
        { type: 'input', placeholder: '教练姓名', prop: 'coachName', width: 160 },
        { type: 'select',
          placeholder: '是否有排班',
          prop: 'hasSchedule',
          width: 150,
          options: [
          { value: 1, label: '有排班' },
          { value: 0, label: '无排班' }
          ] }
      ],
      searchHandle: [
        { label: '搜索', type: 'primary', handle: e => this.getCoachList() },
        { label: '重置', handle: e => this.resetSearch() }
      ],
      weekdays: [
        { value: 1, label: '周一' },
        { value: 2, label: '周二' },
        { value: 3, label: '周三' },
        { value: 4, label: '周四' },
        { value: 5, label: '周五' },
        { value: 6, label: '周六' },
        { value: 7, label: '周日' }
      ],
      coachList: [],
      currentCoach: null,
      scheduleList: [],
      // 新增表单
      formData: this.blankForm(),
      timeRanges: [{ startTime: '', endTime: '' }],
      formCols: [
        { type: 'checkbox',
          label: '星期',
          prop: 'weekdays',
          checkboxs: [
          { value: 1, label: '周一' },
          { value: 2, label: '周二' },
          { value: 3, label: '周三' },
          { value: 4, label: '周四' },
          { value: 5, label: '周五' },
          { value: 6, label: '周六' },
          { value: 7, label: '周日' }
          ] },
        { type: 'slot', label: '时间段', name: 'timeRanges' },
        { type: 'checkbox', label: '可预约门店', prop: 'storeIds', checkboxs: [] },
        { type: 'switch', label: '是否启用', prop: 'isEnabled', values: [1, 0] }
      ],
      formRules: {
        weekdays: [
          { required: true, type: 'array', message: '请至少选择一个星期', trigger: 'change' }
        ],
        storeIds: [
          { required: true, type: 'array', message: '请至少选择一个门店', trigger: 'change' }
        ]
      },
      formHandle: [
        { label: '确认', type: 'primary', icon: 'el-icon-circle-plus-outline', handle: e => this.elFormSubmit() },
        { label: '取消', icon: 'el-icon-circle-close', handle: e => this.elFormVisible() }
      ]
    }
  },
  mounted () {
    this.getStoreList()
    this.getCoachList()
  },
  methods: {
    blankForm () {
      return {
        weekdays: [],
        storeIds: [],
        isEnabled: 1
      }
    },
    // ===== 左侧教练栏 =====
    async getStoreList () {
      var res = await this.apis.store_list({ page: 1, limit: 999 })
      var list = (res.page && res.page.list) || []
      var map = {}
      var opts = list.map(function (item) {
        map[item.storeAddrId] = item.storeName
        return { value: item.storeAddrId, label: item.storeName }
      })
      this.storeMap = map
      this.searchForm[this.searchIndex(this.searchForm, '所属门店')].options = opts
    },
    async getCoachList () {
      this.coachLoading = true
      try {
        var res = await this.apis.schedule_coachList({
          page: 1,
          limit: 999,
          storeId: this.searchData.storeId,
          coachName: this.searchData.coachName,
          hasSchedule: this.searchData.hasSchedule
        })
        var page = res.page || {}
        this.coachList = page.list || []
        // 维持/重置选中项
        if (this.coachList.length) {
          var keep = this.currentCoach && this.coachList.filter(c => c.id === this.currentCoach.id)[0]
          this.selectCoach(keep || this.coachList[0])
        } else {
          this.currentCoach = null
          this.scheduleList = []
        }
      } finally {
        this.coachLoading = false
      }
    },
    resetSearch () {
      this.searchData = { storeId: '', coachName: '', hasSchedule: '' }
      this.getCoachList()
    },
    scheduleBadge (c) {
      var n = c.scheduleCount
      if (n === undefined || n === null) return ''
      return n > 0 ? (n + '段') : '无排班'
    },
    scheduleBadgeClass (c) {
      var n = c.scheduleCount
      if (n === undefined || n === null) return 'hide'
      return n > 0 ? 'has' : 'none'
    },
    selectCoach (c) {
      this.currentCoach = c
      this.loadSchedule()
    },
    // ===== 右侧排班 =====
    async loadSchedule () {
      if (!this.currentCoach) return
      this.scheduleLoading = true
      try {
        var res = await this.apis.schedule_list({ coachId: this.currentCoach.id })
        var page = res.page || {}
        this.scheduleList = page.list || []
      } finally {
        this.scheduleLoading = false
      }
    },
    slotsOf (weekday) {
      return this.scheduleList.filter(function (s) { return s.weekday === weekday })
    },
    async onToggleEnabled (s) {
      try {
        var res = await this.apis.schedule_changeEnabled({ id: s.id, isEnabled: s.isEnabled })
        if (res && res.code === 0) {
          this.$message.success(s.isEnabled === 1 ? '已启用' : '已停用')
        } else {
          s.isEnabled = s.isEnabled === 1 ? 0 : 1
        }
      } catch (e) {
        s.isEnabled = s.isEnabled === 1 ? 0 : 1
      }
    },
    delSlot (s) {
      this.$confirm('确定删除该排班段（' + s.startTime + '-' + s.endTime + '）吗？已被未来预约占用的段不可删除。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.schedule_delete([s.id])
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('删除成功')
          this.loadSchedule()
        }
      }).catch(() => {})
    },
    // ===== 新增排班 =====
    // 该教练的所属门店 -> 复选项（限定在其 pt_coach_store_rel 内）
    coachStoreOptions () {
      var self = this
      var ids = (this.currentCoach && this.currentCoach.storeIds) || []
      if (ids && ids.length) {
        return ids.map(function (id) { return { value: id, label: self.storeMap[id] || ('门店#' + id) } })
      }
      // 无 storeIds 时退化为门店名拆分（仅展示，保存以后端校验为准）
      var names = (this.currentCoach && this.currentCoach.storeNames) || ''
      return String(names).split(',').filter(function (n) { return n }).map(function (n) {
        // 反查 id
        var hit = Object.keys(self.storeMap).filter(function (k) { return self.storeMap[k] === n })[0]
        return { value: hit ? Number(hit) : n, label: n }
      })
    },
    openAdd () {
      if (!this.currentCoach) {
        this.$message.warning('请先选择教练')
        return
      }
      this.formData = this.blankForm()
      this.timeRanges = [{ startTime: '', endTime: '' }]
      this.formCols[this.labIndex(this.formCols, '可预约门店')].checkboxs = this.coachStoreOptions()
      this.elFormVisible('新增排班')
    },
    addRange () {
      this.timeRanges.push({ startTime: '', endTime: '' })
    },
    removeRange (idx) {
      this.timeRanges.splice(idx, 1)
    },
    elFormSubmit () {
      this.$refs.elForm.$refs.ruleForm.validate((valid) => {
        if (!valid) return
        // 校验时间段
        var ranges = this.timeRanges.filter(function (r) { return r.startTime && r.endTime })
        if (!ranges.length) {
          this.$message.error('请至少填写一个完整时间段')
          return
        }
        for (var i = 0; i < ranges.length; i++) {
          if (ranges[i].endTime <= ranges[i].startTime) {
            this.$message.error('结束时间必须晚于开始时间：' + ranges[i].startTime + '-' + ranges[i].endTime)
            return
          }
        }
        this.submit(ranges)
      })
    },
    // 后端 save 一次只吃单个时间段(weekdays×storeIds 展开)，故每个时间段各调一次
    async submit (ranges) {
      var self = this
      var base = {
        coachId: this.currentCoach.id,
        weekdays: this.formData.weekdays,
        storeIds: this.formData.storeIds,
        isEnabled: this.formData.isEnabled
      }
      try {
        for (var i = 0; i < ranges.length; i++) {
          var payload = Object.assign({}, base, {
            startTime: ranges[i].startTime,
            endTime: ranges[i].endTime
          })
          var res = await self.apis.schedule_save(payload)
          if (!res || res.code !== 0) {
            // 某段失败：中断并刷新（前面成功的已入库）
            self.loadSchedule()
            self.getCoachList()
            return
          }
        }
        this.$message.success('排班已生成')
        this.elFormVisible()
        this.loadSchedule()
        this.getCoachList()
      } catch (e) {
        // 失败已由响应拦截器弹错误提示；刷新以反映已成功的段
        self.loadSchedule()
        self.getCoachList()
      }
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
.sch-body {
  display: flex;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  min-height: 480px;
}
.sch-left {
  width: 260px;
  border-right: 1px solid #ebeef5;
  overflow-y: auto;
  max-height: 640px;
}
.coach-item {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  cursor: pointer;
  border-bottom: 1px solid #f5f7fa;
}
.coach-item:hover {
  background: #f5f7fa;
}
.coach-item.active {
  background: #ecf5ff;
}
.coach-avatar {
  flex-shrink: 0;
  width: 34px;
  height: 34px;
  line-height: 34px;
  text-align: center;
  border-radius: 50%;
  background: #409eff;
  color: #fff;
  font-size: 14px;
  margin-right: 10px;
}
.coach-info {
  flex: 1;
  min-width: 0;
}
.coach-name {
  font-size: 14px;
  color: #303133;
}
.coach-sub {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.coach-badge {
  flex-shrink: 0;
  font-size: 12px;
  padding: 1px 6px;
  border-radius: 3px;
}
.coach-badge.has {
  background: #f0f9eb;
  color: #67c23a;
}
.coach-badge.none {
  background: #f4f4f5;
  color: #909399;
}
.coach-badge.hide {
  display: none;
}
.coach-empty {
  padding: 20px;
  text-align: center;
  color: #909399;
  font-size: 13px;
}
.sch-right {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.sch-right-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
}
.cur-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
.cur-title.placeholder {
  color: #c0c4cc;
  font-weight: normal;
}
.week-grid {
  flex: 1;
  display: flex;
}
.week-col {
  flex: 1;
  border-right: 1px solid #f5f7fa;
  display: flex;
  flex-direction: column;
}
.week-col:last-child {
  border-right: none;
}
.week-col-head {
  text-align: center;
  padding: 8px 0;
  font-size: 13px;
  color: #606266;
  background: #f5f7fa;
  font-weight: 600;
}
.week-col-body {
  flex: 1;
  padding: 8px 6px;
}
.slot-card {
  background: #ecf5ff;
  border: 1px solid #d9ecff;
  border-radius: 4px;
  padding: 6px;
  margin-bottom: 8px;
}
.slot-card.disabled {
  background: #f4f4f5;
  border-color: #e9e9eb;
  opacity: 0.8;
}
.slot-time {
  font-size: 12px;
  font-weight: 600;
  color: #303133;
}
.slot-store {
  font-size: 12px;
  color: #909399;
  margin: 2px 0 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.slot-ops {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.slot-del {
  cursor: pointer;
  color: #f56c6c;
  font-size: 15px;
}
.slot-empty {
  text-align: center;
  color: #c0c4cc;
  padding: 12px 0;
}
.tr-wrap {
  width: 100%;
}
.tr-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
.tr-sep {
  margin: 0 8px;
  color: #909399;
}
.tr-del {
  margin-left: 10px;
  color: #f56c6c;
}
</style>
