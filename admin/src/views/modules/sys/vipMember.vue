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

    <!-- 更新有效期 -->
    <el-dialog title="更新有效期" :visible.sync="validityDialog.show" width="420px">
      <el-date-picker
        v-model="validityDialog.expireTime"
        type="datetime"
        placeholder="选择新的到期时间"
        value-format="yyyy-MM-dd HH:mm:ss"
        style="width: 100%;" />
      <span slot="footer">
        <el-button @click="validityDialog.show = false">取消</el-button>
        <el-button type="primary" @click="doUpdateValidity()">确定</el-button>
      </span>
    </el-dialog>

    <!-- 更换开通门店 -->
    <el-dialog title="更换开通门店" :visible.sync="storeDialog.show" width="420px">
      <el-select v-model="storeDialog.storeAddrId" placeholder="选择新的开通门店" style="width: 100%;">
        <el-option v-for="item in storeDialog.options" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
      <span slot="footer">
        <el-button @click="storeDialog.show = false">取消</el-button>
        <el-button type="primary" @click="doChangeStore()">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  components: {},
  data() {
    return {
      tableLoading: false,
      // storeAddrId -> storeName，用于「开通门店/可用门店」展示与更换门店下拉
      storeMap: {},
      storeList: [],
      searchData: {
        phone: '',
        status: '',
        dateRange: []
      },
      searchForm: [
        { type: "input", placeholder: "开通手机号码", prop: "phone", width: 200 },
        { type: "select", placeholder: "权益状态", prop: "status", width: 160, options: [
          { value: 0, label: '生效中' },
          { value: 2, label: '已冻结' },
          { value: 3, label: '已过期' },
          { value: 4, label: '已注销' }
        ] },
        { type: "daterange", placeholder: "购买时间", prop: "dateRange", width: 260 },
      ],
      searchHandle: [
        // 搜索时重置回第 1 页,避免翻页后再加筛选停在越界空页
        { label: "搜索", type: "primary", handle: e => { this.pagination.offset = 1; this.getData(); } },
      ],
      tableData: [],
      tableCols: [
        { label: "ID", prop: "vipBenefitId", width: 70 },
        { label: "开通手机号码", prop: "phone", formatter: e => (e.nickname || '') + ' ' + (e.phone || '') },
        { label: "开通门店", prop: "storeName", formatter: e => e.storeName || '—' },
        { label: "可用门店", prop: "storeAddrIds", formatter: e => this.storeNames(e.storeAddrIds) },
        { label: "权益名称", prop: "cardName" },
        { label: "权益状态", prop: "status", type: "html", html: e => this.statusTag(e.status) },
        { label: "到期时间", prop: "expireTime", formatter: e => e.expireTime ? this.parseTime(e.expireTime) : '—' },
        { label: "购买时间", prop: "createdDate", formatter: e => e.createdDate ? this.parseTime(e.createdDate) : '—' },
        { label: "备注", prop: "remark", formatter: e => e.remark || '—' },
        {
          label: "操作",
          type: "button",
          width: 300,
          btnList: [
            {
              label: "停用",
              type: "danger",
              size: "mini",
              icon: "el-icon-lock",
              handle: (row) => this.doFreeze(row, 1),
              isShow: e => e.status == 0
            },
            {
              label: "启用",
              type: "success",
              size: "mini",
              icon: "el-icon-unlock",
              handle: (row) => this.doFreeze(row, 0),
              isShow: e => e.status == 2
            },
            {
              label: "备注",
              type: "success",
              size: "mini",
              icon: "el-icon-edit",
              handle: (row) => this.doRemark(row)
            },
            {
              label: "更新有效期",
              type: "primary",
              size: "mini",
              icon: "el-icon-edit",
              handle: (row) => {
                this.validityDialog.row = row;
                this.validityDialog.expireTime = row.expireTime || '';
                this.validityDialog.show = true;
              },
              isShow: e => e.status == 0 || e.status == 2 || e.status == 3
            },
            {
              label: "更换门店",
              type: "success",
              size: "mini",
              icon: "el-icon-edit",
              handle: (row) => this.openStoreDialog(row),
              isShow: e => e.status == 0 || e.status == 2 || e.status == 3
            },
            {
              label: "注销",
              type: "danger",
              size: "mini",
              icon: "el-icon-delete",
              handle: (row) => this.doCancel(row),
              isShow: e => e.status == 0 || e.status == 2 || e.status == 3
            },
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      tableHandles: [
        {
          label: "导出",
          type: "primary",
          handle: e => {
            // 导出条件与列表搜索一致;走后端 /sys/vipMember/export 直接下载 xls(与门店会员导出同一方式)
            var dr = this.searchData.dateRange || [];
            var params = { phone: this.searchData.phone, status: this.searchData.status, startDate: dr[0] || '', endDate: dr[1] || '' };
            var parts = [];
            for (var key in params) {
              var val = params[key];
              if (val !== '' && val !== null && val !== undefined) {
                parts.push(key + '=' + encodeURIComponent(val));
              }
            }
            window.open('/sys/vipMember/export?' + parts.join('&'));
          }
        }
      ],
      validityDialog: { show: false, row: null, expireTime: '' },
      storeDialog: { show: false, row: null, storeAddrId: '', options: [] },
    };
  },
  mounted() {
    this.getStoreList();
    this.getData();
  },
  methods: {
    // 状态标签:0生效中绿 / 2已冻结橙 / 3已过期灰 / 4已注销灰
    // (status=1「已转出」是设计字典里定义、但过户走「改归属人」实现从不写入的死状态,不作筛选/标签)
    statusTag(status) {
      var map = {
        0: ['生效中', '#f0f9eb', '#67C23A', '#e1f3d8'],
        2: ['已冻结', '#fdf6ec', '#E6A23C', '#faecd8'],
        3: ['已过期', '#f4f4f5', '#909399', '#e9e9eb'],
        4: ['已注销', '#f4f4f5', '#909399', '#e9e9eb']
      };
      var s = map[status] || ['未知', '#f4f4f5', '#909399', '#e9e9eb'];
      return '<span style="padding:2px 8px;border-radius:3px;font-size:12px;background:' + s[1] + ';color:' + s[2] + ';border:1px solid ' + s[3] + '">' + s[0] + '</span>';
    },
    // 逗号分隔的 storeAddrId 串 -> 门店名，与 vipCard.vue 的展示一致
    storeNames(ids) {
      if (!ids) return '—';
      var map = this.storeMap;
      return String(ids).split(',').map(function (id) { return map[id] || id; }).join('、');
    },
    // 门店地址下拉（与 vipCard.vue 同源 /sys/store/list）
    async getStoreList() {
      var res = await this.apis.store_list({ page: 1, limit: 999 });
      var list = (res.page && res.page.list) || [];
      var map = {};
      list.forEach(function (item) {
        item.value = item.storeAddrId;
        item.label = item.storeName;
        map[item.storeAddrId] = item.storeName;
      });
      this.storeMap = map;
      this.storeList = list;
    },
    async getData() {
      this.tableLoading = true;
      try {
        // daterange 控件绑定为 [起, 止] 数组(清空为 null),拆成后端要的 startDate/endDate
        var dr = this.searchData.dateRange || [];
        var res = await this.apis.vipMember_list({
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
    doFreeze(row, disable) {
      this.$confirm(disable ? '确定要停用该权益吗?停用后会员权益立即失效。' : '确定要启用该权益吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await this.apis.vipMember_freeze({ vipBenefitId: row.vipBenefitId, disable: disable });
        this.msgSuccess('操作成功!');
        this.getData();
      }).catch(() => {});
    },
    doRemark(row) {
      this.$prompt('请输入备注', '编辑备注', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: row.remark || ''
      }).then(async ({ value }) => {
        await this.apis.vipMember_remark({ vipBenefitId: row.vipBenefitId, remark: value || '' });
        this.msgSuccess('操作成功!');
        this.getData();
      }).catch(() => {});
    },
    async doUpdateValidity() {
      if (!this.validityDialog.expireTime) {
        this.msgError('请选择新的到期时间');
        return;
      }
      try {
        // 失败时 $http 拦截器已弹错误 toast;这里吞掉 reject,保留弹窗让用户重试
        await this.apis.vipMember_updateValidity({
          vipBenefitId: this.validityDialog.row.vipBenefitId,
          expireTime: this.validityDialog.expireTime
        });
        this.validityDialog.show = false;
        this.msgSuccess('操作成功!');
        this.getData();
      } catch (e) {}
    },
    openStoreDialog(row) {
      // 只允许换到该权益的可用门店内;权益卡没配可用门店则全部可选
      var options = this.storeList;
      if (row.storeAddrIds) {
        var ids = String(row.storeAddrIds).split(',');
        options = this.storeList.filter(function (item) { return ids.indexOf(String(item.storeAddrId)) >= 0; });
      }
      this.storeDialog.options = options;
      this.storeDialog.row = row;
      this.storeDialog.storeAddrId = row.storeAddrId || '';
      this.storeDialog.show = true;
    },
    async doChangeStore() {
      if (!this.storeDialog.storeAddrId) {
        this.msgError('请选择门店');
        return;
      }
      try {
        await this.apis.vipMember_changeStore({
          vipBenefitId: this.storeDialog.row.vipBenefitId,
          storeAddrId: this.storeDialog.storeAddrId
        });
        this.storeDialog.show = false;
        this.msgSuccess('操作成功!');
        this.getData();
      } catch (e) {}
    },
    doCancel(row) {
      this.$confirm('确定要注销该权益吗?注销后不可恢复。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await this.apis.vipMember_cancel({ vipBenefitId: row.vipBenefitId });
        this.msgSuccess('操作成功!');
        this.getData();
      }).catch(() => {});
    },
  },
};
</script>

<style scoped lang="scss">
</style>
