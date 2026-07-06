<template>
  <div>
    <!-- 顶部标题 + 副标题 -->
    <div class="page-head">
      <div class="page-title">会员卡权益</div>
      <div class="page-desc">按会员卡维度聚合展示三类权益：券类（优惠券）、团课次数类、VIP 权益类。本模块无独立数据表，仅做只读聚合视图，配置入口分散在对应域，后续迭代完善。</div>
    </div>

    <!-- 占位说明：本域不新建权益表，聚合三类权益来源 -->
    <el-alert
      class="benefit-alert"
      title="会员卡权益为聚合/占位视图，暂不持有独立数据"
      type="info"
      :closable="false"
      show-icon>
      <div slot="default" class="alert-body">
        <p>会员卡权益不新建独立表，而是聚合以下三类权益来源，新增/编辑请前往对应配置页：</p>
        <ul>
          <li><b>券类权益</b>：在「优惠券」页配置券模板，通过购买/赠送发券到会员领券记录。</li>
          <li><b>团课次数类权益</b>：在「私教商品 · 附赠团课权益」处配置（属交易/运营域）。</li>
          <li><b>VIP 权益类</b>：在「VIP 权益卡」相关页配置（属 VIP 域）。</li>
        </ul>
        <p class="alert-tip">本页为一期占位，后续迭代接入聚合数据后此列表将展示按会员卡聚合的权益明细。</p>
      </div>
    </el-alert>

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
        cardType: '',
        benefitType: '',
        chargeType: '',
        status: ''
      },
      searchForm: [
        { type: 'input', placeholder: '会员卡类型', prop: 'cardType', width: 180 },
        { type: 'select', placeholder: '权益类型', prop: 'benefitType', width: 150, options: [
          { value: 1, label: '优惠券' },
          { value: 2, label: '团课次数' }
        ] },
        { type: 'select', placeholder: '收费类型', prop: 'chargeType', width: 150, options: [
          { value: 1, label: '免费附赠' },
          { value: 2, label: '付费加购' }
        ] },
        { type: 'select', placeholder: '状态', prop: 'status', width: 130, options: [
          { value: 1, label: '启用' },
          { value: 0, label: '停用' }
        ] }
      ],
      searchHandle: [
        { label: '搜索', type: 'primary', handle: e => this.getData() },
        { label: '重置', handle: e => this.reset() }
      ],
      tableData: [],
      tableCols: [
        { label: '会员卡', prop: 'cardName', formatter: e => e.cardName || '-' },
        { label: '权益类型', prop: 'benefitType', width: 120, formatter: e => e.benefitType === 2 ? '团课次数' : (e.benefitType === 1 ? '优惠券' : '-') },
        { label: '绑定对象', prop: 'bindName', formatter: e => e.bindName || '-' },
        { label: '权益描述', prop: 'benefitDesc', formatter: e => e.benefitDesc || '-' },
        { label: '数量', prop: 'quantity', width: 90, formatter: e => this.quantityText(e.quantity) },
        { label: '收费类型', prop: 'chargeType', width: 130, formatter: e => this.chargeText(e) },
        { label: '有效期(天)', prop: 'validityDays', width: 110, formatter: e => (e.validityDays === 0 || e.validityDays) ? e.validityDays : '-' },
        { label: '状态', prop: 'status', width: 90, formatter: e => e.status === 1 ? '启用' : (e.status === 0 ? '停用' : '-') }
      ],
      pagination: { limit: 10, offset: 1, total: 1 }
    };
  },
  mounted() {
    this.getData();
  },
  methods: {
    // ===== 列表展示辅助 =====
    quantityText(q) {
      // 约定 999 表示「不限」
      if (q === 999) return '不限';
      return (q === 0 || q) ? q : '-';
    },
    chargeText(row) {
      if (row.chargeType === 1) return '免费附赠';
      if (row.chargeType === 2) return '加购¥' + (row.chargePrice || 0);
      return '-';
    },
    // ===== 列表（占位：后端一期可返回空 page）=====
    async getData() {
      this.tableLoading = true;
      try {
        var res = await this.apis.cardBenefit_list(Object.assign({
          page: this.pagination.offset,
          limit: this.pagination.limit
        }, this.searchData));
        var list = (res.page && res.page.list) || [];
        this.tableData = list;
        this.pagination.total = res.page ? res.page.totalCount : 0;
      } catch (e) {
        // 占位接口未实现时静默，不阻塞页面
        this.tableData = [];
        this.pagination.total = 0;
      } finally {
        this.tableLoading = false;
      }
    },
    page() {
      this.getData();
    },
    reset() {
      this.searchData = { cardType: '', benefitType: '', chargeType: '', status: '' };
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
.benefit-alert {
  margin-bottom: 16px;
  .alert-body {
    font-size: 13px;
    line-height: 1.7;
    p { margin: 4px 0; }
    ul { margin: 4px 0 4px 18px; }
    .alert-tip { color: #909399; }
  }
}
</style>
