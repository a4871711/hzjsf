<template>
  <div>
    <div class="page-head">
      <h2 class="page-title">预约记录</h2>
      <p class="page-sub">私教预约查看、完成核销、取消与教练代约课</p>
    </div>

    <!-- 顶部统计卡:取 list 返回的 stat -->
    <el-row :gutter="12" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-num" style="color:#409EFF">{{ stat.bookedCount || 0 }}</div>
          <div class="stat-label">待上课</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-num" style="color:#67C23A">{{ stat.finishedCount || 0 }}</div>
          <div class="stat-label">已完成</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-num" style="color:#909399">{{ stat.cancelledCount || 0 }}</div>
          <div class="stat-label">已取消</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-num" style="color:#F56C6C">{{ stat.noShowCount || 0 }}</div>
          <div class="stat-label">爽约</div>
        </el-card>
      </el-col>
    </el-row>

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

    <!-- 预约详情抽屉 -->
    <el-drawer title="预约详情" :visible.sync="detailVisible" size="560px" :append-to-body="true">
      <div class="detail-wrap" v-if="detail">
        <div class="detail-section-title">预约信息</div>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">预约编号</span>{{ detail.appointmentNo || '—' }}</el-col>
          <el-col :span="12"><span class="lab">预约状态</span><span v-html="statusTag(detail.appointmentStatus)"></span></el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">会员</span>{{ detail.memberName || '—' }}</el-col>
          <el-col :span="12"><span class="lab">手机号</span>{{ detail.memberMobile || '—' }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">私教商品</span>{{ detail.productName || '—' }}</el-col>
          <el-col :span="12"><span class="lab">关联权益</span>{{ detail.benefitNo || '—' }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">教练</span>{{ detail.coachName || '—' }}</el-col>
          <el-col :span="12"><span class="lab">上课门店</span>{{ detail.storeName || '—' }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">预约日期</span>{{ detail.appointmentDate || '—' }}</el-col>
          <el-col :span="12"><span class="lab">上课时间</span>{{ detail.startTime }}–{{ detail.endTime }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">消耗课时</span>{{ detail.lessonCount }}</el-col>
          <el-col :span="12"><span class="lab">预约来源</span>{{ detail.proxyBook == 1 ? '代约' : '自约' }}</el-col>
        </el-row>

        <div class="detail-section-title">状态时间</div>
        <el-row class="detail-row">
          <el-col :span="24"><span class="lab">取消原因</span>{{ detail.cancelReason || '—' }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="12"><span class="lab">取消时间</span>{{ detail.cancelAt || '—' }}</el-col>
          <el-col :span="12"><span class="lab">完成时间</span>{{ detail.finishAt || '—' }}</el-col>
        </el-row>
        <el-row class="detail-row">
          <el-col :span="24"><span class="lab">预约时间</span>{{ detail.createdAt || '—' }}</el-col>
        </el-row>
      </div>
    </el-drawer>

    <!-- 取消弹窗:填原因 -->
    <el-dialog title="取消预约" :visible.sync="cancelVisible" width="480px" :append-to-body="true">
      <el-form label-width="90px" size="medium">
        <el-form-item label="取消原因">
          <el-input v-model="cancelReason" type="textarea" :rows="3" placeholder="请填写取消原因（选填）" style="width:320px"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="cancelVisible = false">取消</el-button>
        <el-button type="primary" :loading="cancelLoading" @click="submitCancel">确认取消预约</el-button>
      </div>
    </el-dialog>

    <!-- 教练代约课抽屉 -->
    <el-drawer title="教练代约课" :visible.sync="bookVisible" size="480px" :append-to-body="true">
      <div class="book-wrap">
        <el-alert type="info" :closable="false" show-icon
          title="代约以 ID 录入，提交后由后端校验教练/会员/商品匹配、权益课时与时段容量。"
          style="margin-bottom:16px" />
        <el-form :model="bookForm" label-width="100px" size="medium">
          <el-form-item label="教练ID" required>
            <el-input v-model="bookForm.coachId" placeholder="预约教练ID" style="width:260px"></el-input>
          </el-form-item>
          <el-form-item label="会员ID" required>
            <el-input v-model="bookForm.memberId" placeholder="会员ID" style="width:260px"></el-input>
          </el-form-item>
          <el-form-item label="私教商品ID" required>
            <el-input v-model="bookForm.productId" placeholder="私教商品ID" style="width:260px"></el-input>
          </el-form-item>
          <el-form-item label="预约日期" required>
            <el-date-picker v-model="bookForm.appointmentDate" type="date" value-format="yyyy-MM-dd"
              placeholder="选择日期" style="width:260px"></el-date-picker>
          </el-form-item>
          <el-form-item label="开始时间" required>
            <el-time-select v-model="bookForm.startTime"
              :picker-options="{ start: '06:00', step: '00:15', end: '23:00' }"
              placeholder="开始时间" style="width:125px"></el-time-select>
          </el-form-item>
          <el-form-item label="结束时间" required>
            <el-time-select v-model="bookForm.endTime"
              :picker-options="{ start: '06:00', step: '00:15', end: '23:59' }"
              placeholder="结束时间" style="width:125px"></el-time-select>
          </el-form-item>
        </el-form>
        <div class="book-footer">
          <el-button @click="bookVisible = false">取消</el-button>
          <el-button type="primary" :loading="bookLoading" @click="submitBook">提交代约</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
export default {
  components: {},
  data() {
    return {
      tableLoading: false,
      stat: {},
      searchData: {
        memberKeyword: '',
        coachName: '',
        storeId: '',
        appointmentStatus: '',
        dateRange: []
      },
      searchForm: [
        { type: "input", placeholder: "会员姓名/手机号", prop: "memberKeyword", width: 200 },
        { type: "input", placeholder: "教练姓名", prop: "coachName", width: 160 },
        { type: "select", placeholder: "上课门店", prop: "storeId", width: 180, options: [] },
        { type: "select", placeholder: "预约状态", prop: "appointmentStatus", width: 160, options: [
          { value: 1, label: '已预约' },
          { value: 3, label: '已完成' },
          { value: 2, label: '已取消' },
          { value: 4, label: '爽约' }
        ] },
        { type: "daterange", placeholder: "上课日期", prop: "dateRange", width: 260 },
      ],
      searchHandle: [
        { label: "搜索", type: "primary", handle: e => { this.pagination.offset = 1; this.getData(); } },
      ],
      tableHandles: [
        { label: "教练代约课", type: "primary", icon: "el-icon-plus", isShow: () => this.checkBtn('sys:privateAppointment:coachBook'), handle: e => this.openBook() },
      ],
      tableData: [],
      tableCols: [
        { label: "预约编号", prop: "appointmentNo", width: 180 },
        { label: "会员", prop: "memberName", formatter: e => (e.memberName || '') + ' ' + (e.memberMobile || '') },
        { label: "私教商品", prop: "productName" },
        { label: "关联权益", prop: "benefitNo", width: 170 },
        { label: "教练", prop: "coachName", width: 100 },
        { label: "上课门店", prop: "storeName" },
        { label: "预约日期", prop: "appointmentDate", width: 110 },
        { label: "上课时间", prop: "startTime", width: 120, formatter: e => (e.startTime || '') + '–' + (e.endTime || '') },
        { label: "消耗课时", prop: "lessonCount", width: 80 },
        { label: "预约状态", prop: "appointmentStatus", width: 90, type: "html", html: e => this.statusTag(e.appointmentStatus) },
        { label: "来源", prop: "proxyBook", width: 80, formatter: e => e.proxyBook == 1 ? '代约' : '自约' },
        { label: "预约时间", prop: "createdAt", width: 160, formatter: e => e.createdAt || '—' },
        {
          label: "操作",
          type: "button",
          width: 180,
          fixed: "right",
          btnList: [
            { label: "查看", type: "primary", size: "mini", handle: (row) => this.openDetail(row) },
            {
              label: "完成",
              type: "success",
              size: "mini",
              isShow: () => this.checkBtn('sys:privateAppointment:finish'),
              disabled: (row) => row.appointmentStatus != 1,
              handle: (row) => this.finish(row)
            },
            {
              label: "取消",
              type: "danger",
              size: "mini",
              isShow: () => this.checkBtn('sys:privateAppointment:cancel'),
              disabled: (row) => row.appointmentStatus != 1,
              handle: (row) => this.openCancel(row)
            },
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      detailVisible: false,
      detail: null,
      cancelVisible: false,
      cancelRow: null,
      cancelReason: '',
      cancelLoading: false,
      bookVisible: false,
      bookLoading: false,
      bookForm: { coachId: '', memberId: '', productId: '', appointmentDate: '', startTime: '', endTime: '' },
    };
  },
  mounted() {
    this.getData();
    this.getStoreList();
  },
  methods: {
    // 预约状态:1已预约蓝 / 3已完成绿 / 2已取消灰 / 4爽约红
    statusTag(status) {
      var map = {
        1: ['已预约', '#ecf5ff', '#409EFF', '#d9ecff'],
        3: ['已完成', '#f0f9eb', '#67C23A', '#e1f3d8'],
        2: ['已取消', '#f4f4f5', '#909399', '#e9e9eb'],
        4: ['爽约', '#fef0f0', '#F56C6C', '#fde2e2']
      };
      var s = map[status] || ['未知', '#f4f4f5', '#909399', '#e9e9eb'];
      return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:' + s[1] + ';color:' + s[2] + ';border:1px solid ' + s[3] + '">' + s[0] + '</span>';
    },
    async getData() {
      this.tableLoading = true;
      try {
        // daterange 绑定为 [起, 止] 数组,拆成后端要的 startDate/endDate
        var dr = this.searchData.dateRange || [];
        var res = await this.apis.privateAppointment_list({
          page: this.pagination.offset,
          limit: this.pagination.limit,
          memberKeyword: this.searchData.memberKeyword,
          coachName: this.searchData.coachName,
          storeId: this.searchData.storeId,
          appointmentStatus: this.searchData.appointmentStatus,
          startDate: dr[0] || '',
          endDate: dr[1] || ''
        });
        var list = (res.page && res.page.list) || [];
        this.tableData = list;
        this.pagination.total = res.page ? res.page.totalCount : 0;
        // 统计卡:stat 与 page 同级(controller put("stat", ...))
        this.stat = res.stat || {};
      } finally {
        this.tableLoading = false;
      }
    },
    page() {
      this.getData();
    },
    async getStoreList() {
      var res = await this.apis.store_list({ page: 1, limit: 999 });
      var list = (res.page && res.page.list) || [];
      var opts = list.map(function (item) { return { value: item.storeAddrId, label: item.storeName }; });
      this.searchForm[2].options = opts;
    },
    async openDetail(row) {
      this.detail = null;
      this.detailVisible = true;
      var res = await this.apis.privateAppointment_info({ id: row.id });
      this.detail = (res && res.entity) || null;
    },
    // 完成核销
    finish(row) {
      this.$confirm('确认将该预约标记为已完成（核销课时）？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.privateAppointment_finish({ appointmentId: row.id });
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('核销成功');
          this.getData();
        }
      }).catch(() => { /* 取消或失败(拦截器已弹错) */ });
    },
    openCancel(row) {
      this.cancelRow = row;
      this.cancelReason = '';
      this.cancelVisible = true;
    },
    submitCancel() {
      this.cancelLoading = true;
      this.apis.privateAppointment_cancel({
        appointmentId: this.cancelRow.id,
        cancelReason: this.cancelReason
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('已取消预约');
          this.cancelVisible = false;
          this.getData();
        }
      }).catch(() => {}).then(() => {
        this.cancelLoading = false;
      });
    },
    openBook() {
      this.bookForm = { coachId: '', memberId: '', productId: '', appointmentDate: '', startTime: '', endTime: '' };
      this.bookVisible = true;
    },
    submitBook() {
      var f = this.bookForm;
      if (!f.coachId || !f.memberId || !f.productId || !f.appointmentDate || !f.startTime || !f.endTime) {
        this.$message.warning('请完整填写教练/会员/商品/日期/时段');
        return;
      }
      this.bookLoading = true;
      this.apis.privateAppointment_coachBook({
        coachId: f.coachId,
        memberId: f.memberId,
        productId: f.productId,
        appointmentDate: f.appointmentDate,
        startTime: f.startTime,
        endTime: f.endTime
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('代约成功');
          this.bookVisible = false;
          this.getData();
        }
      }).catch(() => {}).then(() => {
        this.bookLoading = false;
      });
    },
  },
};
</script>

<style scoped lang="scss">
.page-head { margin-bottom: 12px; }
.page-title { margin: 0; font-size: 20px; }
.page-sub { margin: 4px 0 0; color: #909399; font-size: 13px; }
.stat-row { margin-bottom: 16px; }
.stat-card { text-align: center; }
.stat-num { font-size: 26px; font-weight: bold; line-height: 1.2; }
.stat-label { color: #909399; font-size: 13px; margin-top: 6px; }
.detail-wrap { padding: 0 20px 20px; }
.detail-section-title { font-weight: bold; color: #303133; margin: 18px 0 10px; padding-left: 8px; border-left: 3px solid #409EFF; }
.detail-row { line-height: 32px; color: #606266; }
.detail-row .lab { display: inline-block; color: #909399; margin-right: 8px; min-width: 66px; }
.book-wrap { padding: 0 20px 20px; }
.book-footer { text-align: right; margin-top: 10px; }
</style>
