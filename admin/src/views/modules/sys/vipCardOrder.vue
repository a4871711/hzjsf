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
        dateRange: []
      },
      searchForm: [
        { type: "input", placeholder: "会员手机(购买人/持有人)", prop: "phone", width: 240 },
        { type: "select", placeholder: "状态", prop: "status", width: 160, options: [
          { value: 0, label: '正常' },
          { value: 2, label: '已冻结' },
          { value: 3, label: '已过期' },
          { value: 4, label: '已注销' }
        ] },
        { type: "daterange", placeholder: "购买时间", prop: "dateRange", width: 260 },
      ],
      searchHandle: [
        // 搜索时重置回第 1 页,避免翻页后再加筛选停在越界空页(表格空白但分页条却显示有数据)
        { label: "搜索", type: "primary", handle: e => { this.pagination.offset = 1; this.getData(); } },
      ],
      tableData: [],
      tableCols: [
        { label: "ID", prop: "vipBenefitId", width: 70 },
        { label: "购买人", prop: "originNickname", formatter: e => (e.originNickname || '') + ' ' + (e.originPhone || '') },
        { label: "当前持有人", prop: "holderNickname", formatter: e => (e.holderNickname || '') + ' ' + (e.holderPhone || '') },
        { label: "权益卡", prop: "cardName" },
        { label: "门店", prop: "storeName", formatter: e => e.storeName || '—' },
        { label: "购买售价", prop: "originPrice", formatter: e => e.originPrice != null && e.originPrice !== '' ? '¥' + e.originPrice : '—' },
        { label: "购买时间", prop: "createdDate", formatter: e => e.createdDate ? this.parseTime(e.createdDate) : '—' },
        { label: "生效", prop: "startTime", formatter: e => e.startTime ? this.parseTime(e.startTime) : '—' },
        { label: "到期", prop: "expireTime", formatter: e => e.expireTime ? this.parseTime(e.expireTime) : '—' },
        { label: "转让次数", prop: "transferCount", width: 90 },
        { label: "状态", prop: "status", type: "html", html: e => this.statusTag(e.status) },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      tableHandles: [
        {
          label: "导出",
          type: "primary",
          handle: e => {
            // 导出条件与列表搜索一致;走后端 /sys/vipCardOrder/export 直接下载 xls(与权益会员导出同一方式)
            var dr = this.searchData.dateRange || [];
            var params = { phone: this.searchData.phone, status: this.searchData.status, startDate: dr[0] || '', endDate: dr[1] || '' };
            var parts = [];
            for (var key in params) {
              var val = params[key];
              if (val !== '' && val !== null && val !== undefined) {
                parts.push(key + '=' + encodeURIComponent(val));
              }
            }
            window.open('/sys/vipCardOrder/export?' + parts.join('&'));
          }
        }
      ],
    };
  },
  mounted() {
    this.getData();
  },
  methods: {
    // 状态标签:9待支付灰 / 0正常绿 / 2已冻结橙 / 3已过期灰 / 4已注销灰
    // (转让痕迹看「购买人≠持有人」与「转让次数」列;status=1「已转出」是过户走改归属实现从不写入的死状态,不作标签)
    statusTag(status) {
      var map = {
        9: ['待支付', '#f4f4f5', '#909399', '#e9e9eb'],
        0: ['正常', '#f0f9eb', '#67C23A', '#e1f3d8'],
        2: ['已冻结', '#fdf6ec', '#E6A23C', '#faecd8'],
        3: ['已过期', '#f4f4f5', '#909399', '#e9e9eb'],
        4: ['已注销', '#f4f4f5', '#909399', '#e9e9eb']
      };
      var s = map[status] || ['未知', '#f4f4f5', '#909399', '#e9e9eb'];
      return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:' + s[1] + ';color:' + s[2] + ';border:1px solid ' + s[3] + '">' + s[0] + '</span>';
    },
    async getData() {
      this.tableLoading = true;
      try {
        // daterange 控件绑定为 [起, 止] 数组(清空为 null),拆成后端要的 startDate/endDate
        var dr = this.searchData.dateRange || [];
        var res = await this.apis.vipCardOrder_list({
          page: this.pagination.offset,
          limit: this.pagination.limit,
          phone: this.searchData.phone,
          status: this.searchData.status,
          startDate: dr[0] || '',
          endDate: dr[1] || ''
        });
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
  },
};
</script>

<style scoped lang="scss">
</style>
