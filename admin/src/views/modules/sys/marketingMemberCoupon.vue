<template>
  <div>
    <!-- 顶部标题 + 副标题 -->
    <div class="page-head">
      <div class="page-title">会员领券记录</div>
      <div class="page-desc">会员领取优惠券与使用情况的只读追溯：领取/到期/使用时间、使用状态（未使用/已使用/已过期）、使用的私教订单。记录由系统发券/领券生成，后台不可手改。</div>
    </div>

    <r-search ref="search" :searchData="searchData" :searchForm="searchForm" :searchHandle="searchHandle" />
    <r-table
      :isSelection="false"
      :isHandle="false"
      :isPagination="true"
      :tableData="tableData"
      :tableCols="tableCols"
      :tablePage="pagination"
      :loading="tableLoading"
      @refresh="page()"
      ref="rtable" />
  </div>
</template>

<script>
export default {
  components: {},
  data() {
    return {
      tableLoading: false,
      searchData: {
        memberKeyword: '',
        couponName: '',
        useStatus: ''
      },
      searchForm: [
        { type: 'input', placeholder: '会员昵称/手机号', prop: 'memberKeyword', width: 200 },
        { type: 'input', placeholder: '优惠券名称', prop: 'couponName', width: 180 },
        { type: 'select', placeholder: '使用状态', prop: 'useStatus', width: 150, options: [
          { value: 0, label: '未使用' },
          { value: 1, label: '已使用' },
          { value: 2, label: '已过期' }
        ] }
      ],
      searchHandle: [
        { label: '搜索', type: 'primary', handle: e => this.getData() },
        { label: '重置', handle: e => this.reset() }
      ],
      tableData: [],
      tableCols: [
        { label: 'ID', prop: 'id', width: 70 },
        { label: '会员信息', prop: 'memberName', formatter: e => this.memberSummary(e) },
        { label: '优惠券', prop: 'couponName' },
        { label: '券类型', type: 'tag', prop: 'couponType', width: 90, theme: (row) => row.couponType === 1 ? 'info' : 'warning', formatter: e => e.couponType === 1 ? '满减券' : '折扣券' },
        { label: '领取时间', prop: 'receiveTime', width: 160, formatter: e => this.parseTime(e.receiveTime) },
        { label: '到期时间', prop: 'expireTime', width: 160, formatter: e => this.parseTime(e.expireTime) },
        {
          label: '使用状态',
          type: 'tag',
          prop: 'useStatus',
          width: 100,
          theme: (row) => row.useStatus === 1 ? 'success' : (row.useStatus === 2 ? 'info' : 'primary'),
          formatter: e => this.useStatusText(e.useStatus)
        },
        { label: '使用订单', prop: 'usedOrderNo', width: 180, formatter: e => e.usedOrderNo || '-' },
        { label: '使用时间', prop: 'usedTime', width: 160, formatter: e => e.usedTime ? this.parseTime(e.usedTime) : '-' }
      ],
      pagination: { limit: 10, offset: 1, total: 1 }
    };
  },
  mounted() {
    this.getData();
  },
  methods: {
    // ===== 列表展示辅助 =====
    memberSummary(row) {
      var name = row.memberName || row.nickname || '';
      var phone = row.phone || row.mobile || '';
      if (name && phone) return name + '（' + phone + '）';
      return name || phone || ('#' + (row.memberId || '-'));
    },
    useStatusText(s) {
      if (s === 1) return '已使用';
      if (s === 2) return '已过期';
      return '未使用';
    },
    // ===== 列表 =====
    async getData() {
      this.tableLoading = true;
      try {
        var res = await this.apis.mkMemberCoupon_list(Object.assign({
          page: this.pagination.offset,
          limit: this.pagination.limit
        }, this.searchData));
        var list = (res.page && res.page.list) || [];
        this.tableData = list;
        this.pagination.total = res.page ? res.page.totalCount : 0;
      } finally {
        this.tableLoading = false;
      }
    },
    page() {
      this.getData();
    },
    reset() {
      this.searchData = { memberKeyword: '', couponName: '', useStatus: '' };
      this.pagination.offset = 1;
      this.getData();
    }
  }
};
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
</style>
