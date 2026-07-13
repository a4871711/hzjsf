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
      @refresh="page()" />
    <el-dialog title="停卡详情" :visible.sync="detailVisible" width="600px">
      <el-form label-width="120px" :model="detailData" size="small">
        <el-form-item label="会员">{{ detailData.nickname }} {{ detailData.phone }}</el-form-item>
        <el-form-item label="卡">{{ detailData.cardName }}</el-form-item>
        <el-form-item label="类型">{{ detailData.pauseType == 1 ? '付费停卡' : '免费停卡' }}</el-form-item>
        <el-form-item label="金额">{{ amountText(detailData) }}</el-form-item>
        <el-form-item label="支付单号">{{ detailData.payOrderNo || '—' }}</el-form-item>
        <el-form-item label="停卡开始">{{ detailData.startTime ? parseTime(detailData.startTime) : '—' }}</el-form-item>
        <el-form-item label="计划结束时间">{{ detailData.endTime ? parseTime(detailData.endTime) : '—' }}</el-form-item>
        <el-form-item label="停卡天数">{{ detailData.pauseDays }}</el-form-item>
        <el-form-item label="取消时间">{{ detailData.cancelTime ? parseTime(detailData.cancelTime) : '—' }}</el-form-item>
        <el-form-item label="实际已停天数">{{ (detailData.actualDays === null || detailData.actualDays === undefined) ? '—' : detailData.actualDays }}</el-form-item>
        <el-form-item label="状态">{{ statusText(detailData.displayStatus) }}</el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="detailVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  components: {},
  data() {
    return {
      tableLoading: false,
      searchData: {
        phone: '',
        status: '',
        pauseType: ''
      },
      searchForm: [
        { type: "input", placeholder: "会员手机", prop: "phone", width: 200 },
        { type: "select", placeholder: "状态", prop: "status", width: 160, options: [
          { value: 0, label: '停卡中' },
          { value: 99, label: '已结束' },
          { value: 1, label: '已恢复(历史)' },
          { value: 2, label: '已取消' }
        ] },
        { type: "select", placeholder: "类型", prop: "pauseType", width: 160, options: [
          { value: 0, label: '免费' },
          { value: 1, label: '付费' }
        ] },
      ],
      searchHandle: [
        { label: "搜索", type: "primary", handle: e => this.getData() },
      ],
      tableHandles: [],
      tableData: [],
      tableCols: [
        { label: "ID", prop: "pauseId", width: 70 },
        { label: "会员", prop: "nickname", formatter: e => (e.nickname || '') + ' ' + (e.phone || '') },
        { label: "卡", prop: "cardName" },
        { label: "类型", prop: "pauseType", type: "html", html: e => this.pauseTypeTag(e.pauseType) },
        { label: "金额", prop: "amount", formatter: e => this.amountText(e) },
        { label: "停卡开始", prop: "startTime", formatter: e => this.parseTime(e.startTime) },
        { label: "计划结束时间", prop: "endTime", formatter: e => e.endTime ? this.parseTime(e.endTime) : '—' },
        { label: "停卡天数", prop: "pauseDays" },
        { label: "取消时间/实际已停", prop: "cancelTime", formatter: e => this.cancelText(e) },
        { label: "状态", prop: "displayStatus", type: "html", html: e => this.statusTag(e.displayStatus) },
        {
          label: "操作",
          type: "button",
          width: 120,
          btnList: [
            { label: "详情", type: "primary", size: "mini", handle: (row) => this.openDetail(row) },
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      detailVisible: false,
      detailData: {}
    };
  },
  mounted() {
    this.getData();
  },
  methods: {
    // 类型标签：0免费灰 / 1付费橙
    pauseTypeTag(pauseType) {
      if (pauseType == 1) {
        return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:#fdf6ec;color:#E6A23C;border:1px solid #faecd8">付费</span>';
      }
      return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:#f4f4f5;color:#909399;border:1px solid #e9e9eb">免费</span>';
    },
    // 金额：免费显示 —；付费显示 ¥金额
    amountText(row) {
      if (row.pauseType != 1) return '—';
      return (row.amount === null || row.amount === undefined || row.amount === '') ? '—' : '¥' + row.amount;
    },
    // 取消时间 + 实际已停天数
    cancelText(row) {
      if (!row.cancelTime) return '—';
      var s = this.parseTime(row.cancelTime);
      if (row.actualDays !== null && row.actualDays !== undefined) {
        s += '（已停' + row.actualDays + '天）';
      }
      return s;
    },
    // 状态标签：10待支付橙 / 0停卡中蓝 / 99已结束灰 / 1已恢复绿 / 2已取消灰 / 3已关闭灰
    statusTag(displayStatus) {
      if (displayStatus == 10) {
        return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:#fdf6ec;color:#E6A23C;border:1px solid #faecd8">待支付</span>';
      }
      if (displayStatus == 0) {
        return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:#ecf5ff;color:#409EFF;border:1px solid #d9ecff">停卡中</span>';
      }
      if (displayStatus == 1) {
        return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:#f0f9eb;color:#67C23A;border:1px solid #e1f3d8">已恢复</span>';
      }
      return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:#f4f4f5;color:#909399;border:1px solid #e9e9eb">' + this.statusText(displayStatus) + '</span>';
    },
    statusText(displayStatus) {
      if (displayStatus == 10) return '待支付';
      if (displayStatus == 0) return '停卡中';
      if (displayStatus == 99) return '已结束';
      if (displayStatus == 1) return '已恢复(历史)';
      if (displayStatus == 2) return '已取消';
      if (displayStatus == 3) return '已关闭';
      return '—';
    },
    async getData() {
      this.tableLoading = true;
      try {
        var res = await this.apis.cardPause_list(Object.assign({
          page: this.pagination.offset,
          limit: this.pagination.limit
        }, this.searchData));
        var list = (res.pages && res.pages.list) || [];
        this.tableData = list;
        this.pagination.total = res.pages ? res.pages.totalCount : 0;
      } finally {
        this.tableLoading = false;
      }
    },
    page() {
      this.getData();
    },
    openDetail(row) {
      this.detailData = Object.assign({}, row);
      this.detailVisible = true;
    },
  },
};
</script>

<style scoped lang="scss">
</style>
