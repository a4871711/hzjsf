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
        <el-form-item label="停卡开始">{{ detailData.startTime ? parseTime(detailData.startTime) : '—' }}</el-form-item>
        <el-form-item label="恢复时间">{{ detailData.endTime ? parseTime(detailData.endTime) : '—' }}</el-form-item>
        <el-form-item label="停卡天数">{{ detailData.pauseDays }}</el-form-item>
        <el-form-item label="本年已停次数">{{ detailData.yearCount }}/12</el-form-item>
        <el-form-item label="状态">{{ statusText(detailData.status) }}</el-form-item>
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
        status: ''
      },
      searchForm: [
        { type: "input", placeholder: "会员手机", prop: "phone", width: 200 },
        { type: "select", placeholder: "状态", prop: "status", width: 160, options: [
          { value: 0, label: '停卡中' },
          { value: 1, label: '已恢复' },
          { value: 2, label: '已取消' }
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
        { label: "停卡开始", prop: "startTime", formatter: e => this.parseTime(e.startTime) },
        { label: "恢复时间", prop: "endTime", formatter: e => e.endTime ? this.parseTime(e.endTime) : '—' },
        { label: "本年已停次数", prop: "yearCount", formatter: e => e.yearCount + '/12' },
        { label: "状态", prop: "status", type: "html", html: e => this.statusTag(e.status) },
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
    // 状态标签：0停卡中橙 / 1已恢复绿 / 2已取消灰
    statusTag(status) {
      if (status == 0) {
        return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:#fdf6ec;color:#E6A23C;border:1px solid #faecd8">停卡中</span>';
      }
      if (status == 1) {
        return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:#f0f9eb;color:#67C23A;border:1px solid #e1f3d8">已恢复</span>';
      }
      return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:#f4f4f5;color:#909399;border:1px solid #e9e9eb">已取消</span>';
    },
    statusText(status) {
      if (status == 0) return '停卡中';
      if (status == 1) return '已恢复';
      return '已取消';
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
