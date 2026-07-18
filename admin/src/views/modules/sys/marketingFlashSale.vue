<template>
  <div>
    <!-- 顶部标题 + 副标题 -->
    <div class="page-head">
      <div class="page-title">限时秒杀</div>
      <div class="page-desc">配置限时秒杀（会员卡 / 权益卡）：可一次挑选多个商品，分别设置秒杀价与库存；支持单次生效或按周循环投放，可配预热倒计时、单用户限购、售罄后处理策略。上架后立即在会员端展示，下架则保存为草稿不投放。</div>
    </div>

    <r-search ref="search" :searchData="searchData" :searchForm="searchForm" :searchHandle="searchHandle" />
    <r-table
      :isSelection="true"
      :isHandle="true"
      :isPagination="true"
      :tableData="tableData"
      :tableCols="tableCols"
      :tablePage="pagination"
      :loading="tableLoading"
      :tableHandles="tableHandles"
      @select="handleSelect"
      @refresh="page()"
      ref="rtable" />

    <!-- ===== 创建 / 编辑秒杀弹窗（自定义表单，照设计稿还原）===== -->
    <el-dialog
      :title="form.id ? '编辑秒杀活动' : '创建秒杀活动'"
      :visible.sync="dialogVisible"
      width="900px"
      :close-on-click-modal="false"
      custom-class="fs-dialog"
      append-to-body>
      <el-form :model="form" label-width="140px" size="small" class="fs-form">
        <!-- 1. 秒杀名称 -->
        <el-form-item label="秒杀名称" required>
          <el-input v-model="form.activityName" maxlength="30" style="width:420px;" placeholder="如：开业狂欢·月卡秒杀" />
        </el-form-item>

        <!-- 2. 秒杀首图 -->
        <el-form-item label="秒杀首图">
          <el-upload
            class="fs-cover-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="onCoverSuccess"
            :before-upload="beforeCoverUpload"
            name="file">
            <img v-if="form.coverUrl" :src="form.coverUrl | formatImg" class="fs-cover-img" />
            <div v-else class="fs-cover-empty">
              <i class="el-icon-plus"></i>
              <div class="fs-cover-tip">上传首图</div>
            </div>
          </el-upload>
          <div class="fs-help">建议 750×750，用于会员端卡片展示</div>
        </el-form-item>

        <!-- 3. 商品类型 -->
        <el-form-item label="商品类型" required>
          <el-radio-group v-model="form.bizType" @change="onBizTypeChange">
            <el-radio :label="2">会员卡</el-radio>
            <el-radio :label="3">权益卡</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 4. 选择商品 -->
        <el-form-item label="选择商品" required>
          <el-button type="text" class="fs-pick-btn" icon="el-icon-plus" @click="openPicker">
            从「{{ bizTypeName }}」中选择（最多100）
          </el-button>
          <el-table
            v-if="form.products.length"
            :data="form.products"
            border
            size="mini"
            class="fs-products-table">
            <el-table-column label="商品名称" prop="name" min-width="180" show-overflow-tooltip />
            <el-table-column label="原价(元)" width="110" align="center">
              <template slot-scope="s">¥{{ s.row.price === 0 || s.row.price ? s.row.price : '-' }}</template>
            </el-table-column>
            <el-table-column label="秒杀价(元)" width="140" align="center">
              <template slot-scope="s">
                <el-input v-model="s.row.flashSalePrice" size="mini" class="fs-price-input" placeholder="秒杀价" />
              </template>
            </el-table-column>
            <el-table-column label="秒杀库存" width="130" align="center">
              <template slot-scope="s">
                <el-input v-model="s.row.activityStock" size="mini" placeholder="库存" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template slot-scope="s">
                <el-button type="text" class="fs-remove-btn" @click="removeProduct(s.$index)">移除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="fs-help">秒杀价不得高于该商品对外售价。</div>
        </el-form-item>

        <!-- 5. 投放方式 -->
        <el-form-item label="投放方式" required>
          <el-radio-group v-model="form.deliveryType">
            <el-radio :label="1">单次生效</el-radio>
            <el-radio :label="2">循环生效</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 5-a 单次生效 -->
        <template v-if="form.deliveryType === 1">
          <el-form-item label="生效时间" required>
            <el-date-picker
              v-model="form.startTime"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="开始时间"
              style="width:200px;" />
            <span class="fs-tilde">~</span>
            <el-date-picker
              v-model="form.endTime"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="结束时间"
              style="width:200px;" />
            <div class="fs-help">连续时段，单次时长≤23小时；同商品两次秒杀间隔≥1小时</div>
          </el-form-item>
        </template>

        <!-- 5-b 循环生效 -->
        <template v-if="form.deliveryType === 2">
          <el-form-item label="活动周期" required>
            <el-date-picker
              v-model="form.activityStartDate"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="开始日期"
              style="width:180px;" />
            <span class="fs-tilde">~</span>
            <el-date-picker
              v-model="form.activityEndDate"
              type="date"
              value-format="yyyy-MM-dd"
              placeholder="结束日期"
              style="width:180px;" />
            <div class="fs-help">周期≤90天</div>
          </el-form-item>

          <el-form-item label="生效日" required>
            <span
              v-for="d in weekOptions"
              :key="d.value"
              class="fs-chip"
              :class="{ active: form.weekDays.indexOf(d.value) > -1 }"
              @click="toggleWeekDay(d.value)">{{ d.label }}</span>
          </el-form-item>

          <el-form-item label="每日投放时段" required>
            <div v-for="(slot, i) in form.timeSlots" :key="i" class="fs-slot-row">
              <el-time-select
                v-model="slot.startHm"
                :picker-options="{ start: '00:00', step: '00:30', end: '23:30' }"
                placeholder="开始"
                style="width:130px;" />
              <span class="fs-tilde">~</span>
              <el-time-select
                v-model="slot.endHm"
                :picker-options="{ start: '00:00', step: '00:30', end: '23:59' }"
                placeholder="结束"
                style="width:130px;" />
              <el-button
                type="text"
                class="fs-remove-btn"
                icon="el-icon-delete"
                v-if="form.timeSlots.length > 1"
                @click="removeSlot(i)">删除</el-button>
            </div>
            <el-button type="text" icon="el-icon-plus" @click="addSlot">添加时段</el-button>
            <div class="fs-help">每日可多个时段，单日累计≤18小时</div>
          </el-form-item>

          <el-form-item label="库存设置方式" required>
            <el-radio-group v-model="form.stockMode">
              <el-radio :label="2">投放总量</el-radio>
              <el-radio :label="1">每日投放库存</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>

        <!-- 6. 单用户单品限购 -->
        <el-form-item label="单用户单品限购">
          <el-radio-group v-model="form.purchaseLimitMode">
            <el-radio :label="0">不限制</el-radio>
            <el-radio :label="1">限制份数</el-radio>
          </el-radio-group>
          <span v-if="form.purchaseLimitMode === 1" class="fs-inline-input">
            限 <el-input v-model="form.purchaseLimit" size="mini" style="width:70px;" /> 份/人
          </span>
        </el-form-item>

        <!-- 7. 秒杀前倒计时 -->
        <el-form-item label="秒杀前倒计时">
          <el-radio-group v-model="form.countdownEnabled">
            <el-radio :label="0">关闭</el-radio>
            <el-radio :label="1">开启</el-radio>
          </el-radio-group>
          <span v-if="form.countdownEnabled === 1" class="fs-inline-input">
            预热 <el-input v-model="form.countdownMinutes" size="mini" style="width:70px;" /> 分钟后开抢
          </span>
        </el-form-item>

        <!-- 8. 活动库存售罄后 -->
        <el-form-item label="活动库存售罄后">
          <el-radio-group v-model="form.soldOutAction" class="fs-radio-column">
            <el-radio :label="1">自动结束，恢复原价售卖</el-radio>
            <el-radio :label="2">显示「售罄」，待投放结束后恢复原价售卖</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 9. 是否上架 -->
        <el-form-item label="是否上架">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
          <div class="fs-help">开启后立即在会员端展示；关闭则保存为草稿不投放</div>
        </el-form-item>
      </el-form>

      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">
          {{ form.status === 1 ? '保存并上架' : '保存草稿' }}
        </el-button>
      </div>
    </el-dialog>

    <!-- ===== 选择商品弹窗 ===== -->
    <el-dialog
      :title="'从「' + bizTypeName + '」中选择'"
      :visible.sync="pickerVisible"
      width="560px"
      :close-on-click-modal="false"
      append-to-body>
      <el-table
        ref="pickerTable"
        :data="productOptions"
        v-loading="pickerLoading"
        height="420"
        row-key="id"
        @selection-change="onPickerSelectionChange">
        <el-table-column type="selection" width="50" :reserve-selection="true" />
        <el-table-column label="商品名称" prop="name" min-width="200" show-overflow-tooltip />
        <el-table-column label="对外售价" width="140" align="right">
          <template slot-scope="s">¥{{ s.row.price === 0 || s.row.price ? s.row.price : '-' }}</template>
        </el-table-column>
      </el-table>
      <div class="fs-picker-count">已选 {{ pickerSelection.length }} / 100</div>
      <div slot="footer">
        <el-button @click="pickerVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPicker">确定</el-button>
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
      saving: false,
      selection: [],
      productNameMap: {},
      productPriceMap: {},
      productOptions: [],
      uploadUrl: (process.env.NODE_ENV !== 'production' ? 'proxyApi/' : process.env.VUE_APP_URL) + '/sys/uploads',
      // ===== 弹窗状态 =====
      dialogVisible: false,
      pickerVisible: false,
      pickerLoading: false,
      pickerSelection: [],
      weekOptions: [
        { value: 1, label: '周一' },
        { value: 2, label: '周二' },
        { value: 3, label: '周三' },
        { value: 4, label: '周四' },
        { value: 5, label: '周五' },
        { value: 6, label: '周六' },
        { value: 7, label: '周日' }
      ],
      form: this.blankForm(),
      // ===== 列表 =====
      searchData: {
        activityName: '',
        bizType: '',
        status: ''
      },
      searchForm: [
        { type: 'input', placeholder: '活动名称', prop: 'activityName', width: 180 },
        { type: 'select', placeholder: '商品类型', prop: 'bizType', width: 150, options: [
          { value: 2, label: '会员卡' },
          { value: 3, label: '权益卡' }
        ] },
        { type: 'select', placeholder: '状态', prop: 'status', width: 130, options: [
          { value: 1, label: '上架' },
          { value: 0, label: '下架' }
        ] }
      ],
      searchHandle: [
        { label: '搜索', type: 'primary', handle: e => this.getData() },
        { label: '重置', handle: e => this.reset() }
      ],
      tableHandles: [
        { label: '创建秒杀', type: 'primary', icon: 'el-icon-plus', isShow: e => this.checkBtn('sys:mkFlashSale:save'), handle: e => this.openAdd() },
        { label: '批量删除', type: 'danger', icon: 'el-icon-delete', isShow: e => this.checkBtn('sys:mkFlashSale:delete'), handle: e => this.delBatch() }
      ],
      tableData: [],
      tableCols: [
        { label: '秒杀活动', type: 'html', width: 260, html: (row) => this.activityCell(row) },
        { label: '商品类型', prop: 'bizType', width: 90, formatter: e => this.bizTypeText(e.bizType) },
        { label: '投放方式', prop: 'deliveryType', width: 100, formatter: e => this.deliveryText(e.deliveryType) },
        { label: '限购', prop: 'purchaseLimit', width: 90, formatter: e => this.limitText(e.purchaseLimit) },
        { label: '倒计时预热', prop: 'countdownEnabled', width: 120, formatter: e => this.countdownText(e) },
        { label: '售罄后', prop: 'soldOutAction', width: 150, formatter: e => this.soldOutText(e.soldOutAction) },
        { label: '活动时间', prop: 'startTime', width: 260, formatter: e => this.timeText(e) },
        {
          label: '状态',
          type: 'switch',
          prop: 'status',
          width: 90,
          values: [1, 0],
          change: (row) => this.changeStatus(row),
          isDisabled: e => !this.checkBtn('sys:mkFlashSale:update')
        },
        {
          label: '操作',
          type: 'button',
          width: 180,
          fixed: 'right',
          btnList: [
            { label: '编辑', type: 'success', size: 'mini', icon: 'el-icon-edit', isShow: e => this.checkBtn('sys:mkFlashSale:update'), handle: (row) => this.openEdit(row) },
            { label: '停止', type: 'warning', size: 'mini', icon: 'el-icon-video-pause', isShow: e => e.status === 1 && this.checkBtn('sys:mkFlashSale:update'), handle: (row) => this.stopActivity(row) },
            { label: '删除', type: 'danger', size: 'mini', icon: 'el-icon-delete', isShow: e => this.checkBtn('sys:mkFlashSale:delete'), handle: (row) => this.del(row) }
          ]
        }
      ],
      pagination: { limit: 10, offset: 1, total: 1 }
    };
  },
  computed: {
    bizTypeName() {
      return this.bizTypeText(this.form.bizType);
    }
  },
  mounted() {
    this.getData();
  },
  methods: {
    // 表单初始值
    blankForm() {
      return {
        id: '',
        activityName: '',
        coverUrl: '',
        bizType: 2,
        products: [],
        deliveryType: 1,
        startTime: '',
        endTime: '',
        activityStartDate: '',
        activityEndDate: '',
        weekDays: [],
        timeSlots: [{ startHm: '', endHm: '' }],
        stockMode: 2,
        purchaseLimitMode: 0,
        purchaseLimit: '',
        countdownEnabled: 0,
        countdownMinutes: 5,
        soldOutAction: 1,
        status: 1
      };
    },
    // ===== 列表展示辅助 =====
    escapeHtml(str) {
      return String(str == null ? '' : str)
        .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
    },
    imgSrc(url) {
      if (!url) return '';
      return url.indexOf('http') === 0 ? url : this.baseUrl + url;
    },
    // 秒杀活动列：首图缩略图 + 活动名 + ID
    activityCell(row) {
      var img = this.imgSrc(row.coverUrl);
      var thumb = img
        ? '<img src="' + this.escapeHtml(img) + '" style="width:44px;height:44px;border-radius:6px;object-fit:cover;flex:none;" />'
        : '<div style="width:44px;height:44px;border-radius:6px;background:#f0f2f5;flex:none;"></div>';
      return '<div style="display:flex;align-items:center;text-align:left;">' + thumb +
        '<div style="margin-left:8px;min-width:0;overflow:hidden;">' +
        '<div style="color:#303133;font-weight:500;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">' + this.escapeHtml(row.activityName || '-') + '</div>' +
        '<div style="color:#909399;font-size:12px;">ID: ' + this.escapeHtml(row.id) + '</div>' +
        '</div></div>';
    },
    // 商品类型：1私教商品 / 2会员卡 / 3权益卡
    bizTypeText(v) {
      var map = { 1: '私教商品', 2: '会员卡', 3: '权益卡' };
      return map[v] || (v == null || v === '' ? '-' : v);
    },
    deliveryText(v) {
      return Number(v) === 2 ? '循环生效' : '单次生效';
    },
    limitText(v) {
      return (v === null || v === undefined || v === '') ? '不限' : ('限' + v + '份');
    },
    countdownText(row) {
      if (Number(row.countdownEnabled) === 1) {
        return '开启·' + (row.countdownMinutes || 0) + '分钟';
      }
      return '关闭';
    },
    soldOutText(v) {
      return Number(v) === 2 ? '显示售罄待结束' : '自动结束恢复原价';
    },
    dateOnly(v) {
      return v ? String(v).slice(0, 10) : '';
    },
    weekDaysText(csv) {
      if (!csv && csv !== 0) return '';
      var map = { 1: '周一', 2: '周二', 3: '周三', 4: '周四', 5: '周五', 6: '周六', 7: '周日' };
      return String(csv).split(',').filter(function (d) { return d !== ''; }).map(function (d) { return map[d] || d; }).join('、');
    },
    timeText(row) {
      if (Number(row.deliveryType) === 2) {
        var period = (this.dateOnly(row.activityStartDate) || '-') + ' ~ ' + (this.dateOnly(row.activityEndDate) || '-');
        var days = this.weekDaysText(row.weekDays);
        return period + (days ? ('｜' + days) : '');
      }
      var s = this.parseTime(row.startTime) || row.startTime || '-';
      var e = this.parseTime(row.endTime) || row.endTime || '-';
      return s + ' ~ ' + e;
    },
    // ===== 列表加载 =====
    async getData() {
      this.tableLoading = true;
      try {
        var res = await this.apis.mkFlashSale_list(Object.assign({
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
      this.searchData = { activityName: '', bizType: '', status: '' };
      this.pagination.offset = 1;
      this.getData();
    },
    handleSelect(rows) {
      this.selection = rows || [];
    },
    // ===== 商品数据源（按类型加载，复用现有 sys 列表接口）=====
    // bizType: 2会员卡 / 3权益卡
    async loadProducts(bizType) {
      var req, mapper;
      // 只取已上架商品(status=1)：下架的会员卡/权益卡不可配秒杀，与后端 queryProductBrief 校验口径一致
      if (Number(bizType) === 3) {
        req = this.apis.vipCard_list({ page: 1, limit: 999, status: 1 });
        mapper = function (item) {
          return { id: item.vipCardId, name: item.cardName, price: (item.currentPrice === 0 || item.currentPrice) ? item.currentPrice : item.price };
        };
      } else {
        req = this.apis.fitcard_list({ page: 1, limit: 999, status: 1 });
        mapper = function (item) { return { id: item.fitCardId, name: item.cardName, price: item.cardPrice }; };
      }
      var res = await req;
      var list = (res.pages && res.pages.list) || (res.page && res.page.list) || [];
      var priceMap = {};
      var nameMap = {};
      var opts = list.map(function (raw) {
        var item = mapper(raw);
        priceMap[item.id] = Number(item.price);
        nameMap[item.id] = item.name;
        return { id: item.id, name: item.name, price: (item.price === 0 || item.price) ? Number(item.price) : '' };
      });
      this.productPriceMap = priceMap;
      this.productNameMap = nameMap;
      this.productOptions = opts;
    },
    // 切换商品类型：清空已选商品并重载可选商品
    async onBizTypeChange(val) {
      this.form.products = [];
      await this.loadProducts(val);
    },
    // ===== 选择商品弹窗 =====
    async openPicker() {
      this.pickerVisible = true;
      this.pickerLoading = true;
      try {
        if (!this.productOptions.length) {
          await this.loadProducts(this.form.bizType);
        }
      } finally {
        this.pickerLoading = false;
      }
      // 回显已选：按 id 勾选
      this.$nextTick(() => {
        if (this.$refs.pickerTable) this.$refs.pickerTable.clearSelection();
        var selectedIds = this.form.products.map(function (p) { return p.productId; });
        var self = this;
        this.productOptions.forEach(function (opt) {
          if (selectedIds.indexOf(opt.id) > -1) {
            self.$refs.pickerTable.toggleRowSelection(opt, true);
          }
        });
      });
    },
    onPickerSelectionChange(rows) {
      this.pickerSelection = rows || [];
    },
    confirmPicker() {
      if (this.pickerSelection.length > 100) {
        this.$message.warning('最多只能选择 100 个商品');
        return;
      }
      // 保留已选商品已填写的秒杀价/库存
      var existMap = {};
      this.form.products.forEach(function (p) { existMap[p.productId] = p; });
      this.form.products = this.pickerSelection.map(function (opt) {
        var old = existMap[opt.id];
        return {
          productId: opt.id,
          name: opt.name,
          price: opt.price,
          flashSalePrice: old ? old.flashSalePrice : '',
          activityStock: old ? old.activityStock : ''
        };
      });
      this.pickerVisible = false;
    },
    removeProduct(idx) {
      this.form.products.splice(idx, 1);
    },
    // ===== 生效日 / 时段 =====
    toggleWeekDay(v) {
      var i = this.form.weekDays.indexOf(v);
      if (i > -1) {
        this.form.weekDays.splice(i, 1);
      } else {
        this.form.weekDays.push(v);
      }
    },
    addSlot() {
      this.form.timeSlots.push({ startHm: '', endHm: '' });
    },
    removeSlot(idx) {
      this.form.timeSlots.splice(idx, 1);
    },
    // ===== 秒杀首图上传（复用 /sys/uploads，回传 path[0]）=====
    beforeCoverUpload(file) {
      var isImg = file.type === 'image/jpeg' || file.type === 'image/png';
      if (!isImg) this.$message.error('首图只能是 JPG/PNG 格式');
      return isImg;
    },
    onCoverSuccess(res) {
      if (res && res.code === 0 && res.path && res.path.length) {
        this.form.coverUrl = res.path[0];
      } else {
        this.$message.error((res && res.msg) || '图片上传失败');
      }
    },
    // ===== 新增 / 编辑 =====
    async openAdd() {
      this.form = this.blankForm();
      await this.loadProducts(this.form.bizType);
      this.dialogVisible = true;
    },
    async openEdit(row) {
      // 先取详情，回填全部字段
      var res = await this.apis.mkFlashSale_info({ id: row.id });
      var a = (res && res.activity) || {};
      var form = this.blankForm();
      form.id = a.id != null ? a.id : row.id;
      form.activityName = a.activityName || '';
      form.coverUrl = a.coverUrl || '';
      form.bizType = (a.bizType === null || a.bizType === undefined || a.bizType === '') ? 2 : Number(a.bizType);
      form.deliveryType = Number(a.deliveryType) === 2 ? 2 : 1;
      form.startTime = a.startTime || '';
      form.endTime = a.endTime || '';
      form.activityStartDate = this.dateOnly(a.activityStartDate);
      form.activityEndDate = this.dateOnly(a.activityEndDate);
      form.weekDays = (a.weekDays === null || a.weekDays === undefined || a.weekDays === '')
        ? []
        : String(a.weekDays).split(',').filter(function (d) { return d !== ''; }).map(function (d) { return Number(d); });
      var slots = a.timeSlots && a.timeSlots.length
        ? a.timeSlots.map(function (t) { return { startHm: t.startHm || '', endHm: t.endHm || '' }; })
        : [{ startHm: '', endHm: '' }];
      form.timeSlots = slots;
      form.stockMode = Number(a.stockMode) === 1 ? 1 : 2;
      var hasLimit = !(a.purchaseLimit === null || a.purchaseLimit === undefined || a.purchaseLimit === '');
      form.purchaseLimitMode = hasLimit ? 1 : 0;
      form.purchaseLimit = hasLimit ? a.purchaseLimit : '';
      form.countdownEnabled = Number(a.countdownEnabled) === 1 ? 1 : 0;
      form.countdownMinutes = (a.countdownMinutes === 0 || a.countdownMinutes) ? a.countdownMinutes : 5;
      form.soldOutAction = Number(a.soldOutAction) === 2 ? 2 : 1;
      form.status = Number(a.status) === 1 ? 1 : 0;

      // 加载可选商品，回显已选商品表格（名称/原价优先取详情，回退取映射）
      await this.loadProducts(form.bizType);
      var self = this;
      var products = (a.products || []).map(function (p) {
        var pid = p.productId;
        return {
          productId: pid,
          name: p.productName || p.name || self.productNameMap[pid] || ('#' + pid),
          price: (p.price === 0 || p.price) ? p.price : (self.productPriceMap[pid] === 0 || self.productPriceMap[pid] ? self.productPriceMap[pid] : ''),
          flashSalePrice: (p.flashSalePrice === 0 || p.flashSalePrice) ? p.flashSalePrice : '',
          activityStock: (p.activityStock === 0 || p.activityStock) ? p.activityStock : ''
        };
      });
      form.products = products;
      this.form = form;
      this.dialogVisible = true;
    },
    // ===== 提交 =====
    submit() {
      var f = this.form;
      // 活动名称
      if (!f.activityName || !String(f.activityName).trim()) {
        this.$message.error('请输入秒杀名称');
        return;
      }
      // 商品：至少 1 个，且价格/库存合法
      if (!f.products.length) {
        this.$message.error('请至少选择 1 个商品');
        return;
      }
      for (var i = 0; i < f.products.length; i++) {
        var p = f.products[i];
        var price = Number(p.flashSalePrice);
        if (!(price > 0)) {
          this.$message.error('【' + p.name + '】秒杀价需大于 0');
          return;
        }
        if ((p.price === 0 || p.price) && price > Number(p.price)) {
          this.$message.error('【' + p.name + '】秒杀价不能高于对外售价 ¥' + p.price);
          return;
        }
        if (!(Number(p.activityStock) > 0)) {
          this.$message.error('【' + p.name + '】秒杀库存需大于 0');
          return;
        }
      }
      // 投放方式
      if (f.deliveryType === 1) {
        if (!f.startTime || !f.endTime) {
          this.$message.error('请选择生效起止时间');
          return;
        }
        if (f.startTime >= f.endTime) {
          this.$message.error('开始时间需早于结束时间');
          return;
        }
      } else {
        if (!f.activityStartDate || !f.activityEndDate) {
          this.$message.error('请选择活动周期');
          return;
        }
        if (f.activityStartDate >= f.activityEndDate) {
          this.$message.error('活动周期开始日需早于结束日');
          return;
        }
        if (!f.weekDays.length) {
          this.$message.error('请至少选择 1 个生效日');
          return;
        }
        var validSlots = f.timeSlots.filter(function (t) { return t.startHm && t.endHm; });
        if (!validSlots.length) {
          this.$message.error('请至少配置 1 个投放时段');
          return;
        }
        for (var j = 0; j < validSlots.length; j++) {
          if (validSlots[j].startHm >= validSlots[j].endHm) {
            this.$message.error('投放时段的开始时间需早于结束时间');
            return;
          }
        }
      }
      // 限购
      if (f.purchaseLimitMode === 1 && !(Number(f.purchaseLimit) > 0)) {
        this.$message.error('限购份数需为正整数');
        return;
      }
      // 倒计时
      if (f.countdownEnabled === 1 && !(Number(f.countdownMinutes) > 0)) {
        this.$message.error('预热分钟数需为正整数');
        return;
      }

      // 拼提交体（按冻结契约）
      var data = {
        activityName: String(f.activityName).trim(),
        bizType: f.bizType,
        coverUrl: f.coverUrl,
        deliveryType: f.deliveryType,
        stockMode: f.stockMode,
        purchaseLimit: f.purchaseLimitMode === 1 ? Number(f.purchaseLimit) : null,
        countdownEnabled: f.countdownEnabled,
        countdownMinutes: f.countdownEnabled === 1 ? Number(f.countdownMinutes) : null,
        soldOutAction: f.soldOutAction,
        status: f.status,
        products: f.products.map(function (p) {
          return {
            productId: p.productId,
            flashSalePrice: Number(p.flashSalePrice),
            activityStock: Number(p.activityStock)
          };
        })
      };
      if (f.id) data.id = f.id;
      if (f.deliveryType === 1) {
        data.startTime = f.startTime;
        data.endTime = f.endTime;
        data.activityStartDate = null;
        data.activityEndDate = null;
        data.weekDays = null;
        data.timeSlots = [];
      } else {
        data.startTime = null;
        data.endTime = null;
        data.activityStartDate = f.activityStartDate;
        data.activityEndDate = f.activityEndDate;
        // 按周一=1…周日=7 升序输出 CSV
        data.weekDays = f.weekDays.slice().sort(function (a, b) { return a - b; }).join(',');
        data.timeSlots = f.timeSlots
          .filter(function (t) { return t.startHm && t.endHm; })
          .map(function (t) { return { startHm: t.startHm, endHm: t.endHm }; });
      }

      this.doSave(data);
    },
    async doSave(data) {
      this.saving = true;
      try {
        var res = !data.id
          ? await this.apis.mkFlashSale_save(data)
          : await this.apis.mkFlashSale_update(data);
        if (res && res.code === 0) {
          this.$message.success('操作成功');
          this.dialogVisible = false;
          this.getData();
        }
      } catch (e) { /* 失败已由响应拦截器弹错误提示 */ } finally {
        this.saving = false;
      }
    },
    // ===== 状态开关（乐观翻转，失败回滚）=====
    async changeStatus(row) {
      try {
        var res = await this.apis.mkFlashSale_changeStatus({ id: row.id, status: row.status });
        if (res && res.code === 0) {
          this.$message.success(row.status === 1 ? '已上架' : '已下架');
        } else {
          row.status = row.status === 1 ? 0 : 1;
        }
      } catch (e) {
        row.status = row.status === 1 ? 0 : 1;
      }
    },
    // 「停止」= 直接下架（status→0），操作列快捷入口（仅上架中可见）
    stopActivity(row) {
      this.$confirm('确定要停止（下架）秒杀活动【' + (row.activityName || '') + '】吗？下架后会员端不可继续购买。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.mkFlashSale_changeStatus({ id: row.id, status: 0 });
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('已下架');
          this.getData();
        }
      }).catch(() => {});
    },
    // ===== 删除 =====
    del(row) {
      this.$confirm('确定要删除秒杀活动【' + (row.activityName || '') + '】吗？已产生关联订单的活动建议下架而非删除。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.mkFlashSale_delete([row.id]);
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('删除成功');
          this.getData();
        }
      }).catch(() => {});
    },
    delBatch() {
      if (!this.selection.length) {
        this.$message.warning('请先勾选要删除的活动');
        return;
      }
      var ids = this.selection.map(function (r) { return r.id; });
      this.$confirm('确定要批量删除选中的 ' + ids.length + ' 个秒杀活动吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.mkFlashSale_delete(ids);
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('删除成功');
          this.getData();
        }
      }).catch(() => {});
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

.fs-help {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}
.fs-tilde {
  margin: 0 8px;
  color: #909399;
}

/* 首图上传 */
.fs-cover-uploader ::v-deep .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  width: 110px;
  height: 110px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fs-cover-uploader ::v-deep .el-upload:hover {
  border-color: #409eff;
}
.fs-cover-img {
  width: 110px;
  height: 110px;
  object-fit: cover;
  display: block;
}
.fs-cover-empty {
  text-align: center;
  color: #8c939d;
}
.fs-cover-empty .el-icon-plus {
  font-size: 24px;
}
.fs-cover-tip {
  font-size: 12px;
  margin-top: 4px;
}

/* 选择商品 */
.fs-pick-btn {
  padding: 0;
}
.fs-products-table {
  margin-top: 8px;
  width: 100%;
}
.fs-price-input ::v-deep input {
  color: #ff8c00;
  font-weight: 600;
}
.fs-remove-btn {
  color: #f56c6c;
}

/* 生效日 chip */
.fs-chip {
  display: inline-block;
  min-width: 44px;
  padding: 5px 10px;
  margin: 0 8px 8px 0;
  text-align: center;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  color: #606266;
  cursor: pointer;
  user-select: none;
  font-size: 13px;
}
.fs-chip.active {
  background: #409eff;
  border-color: #409eff;
  color: #fff;
}

/* 时段行 */
.fs-slot-row {
  margin-bottom: 8px;
}

.fs-inline-input {
  margin-left: 12px;
  color: #606266;
}
.fs-radio-column ::v-deep .el-radio {
  display: block;
  margin-left: 0;
  margin-bottom: 8px;
  line-height: 1.6;
}

.fs-picker-count {
  margin-top: 8px;
  font-size: 13px;
  color: #606266;
  text-align: right;
}
</style>
