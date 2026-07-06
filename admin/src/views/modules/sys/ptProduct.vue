<template>
  <div>
    <!-- 顶部标题 + 副标题 -->
    <div class="page-head">
      <div class="page-title">私教商品</div>
      <div class="page-desc">维护私教商品主数据：基础信息、价格权益、课时有效期、预约规则、适用门店/教练、分期与附赠团课配置，支持上架/下架、复制。上架前需通过后端上架校验（门店、可约教练、售价、课时等）。</div>
    </div>

    <!-- 统计卡片行：后端 list 未返回 stat，此处按「当前页数据」前端汇总 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="6" v-for="(card, idx) in statCards" :key="idx">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-label">{{ card.label }}</div>
          <div class="stat-value" :style="{ color: card.color }">{{ card.value }}</div>
          <div class="stat-unit">{{ card.unit }}</div>
        </el-card>
      </el-col>
    </el-row>

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

    <!-- 商品新增/编辑：自定义大宽度抽屉（7 组表单，配置驱动不够，用分组 el-form）-->
    <el-drawer
      :title="drawerTitle"
      :visible.sync="drawerVisible"
      size="820px"
      :destroy-on-close="true"
      :wrapperClosable="false">
      <div class="drawer-body" v-loading="drawerLoading">
        <el-form :model="form" :rules="formRules" ref="productForm" label-width="130px" size="small">

          <!-- ① 基础信息 -->
          <div class="group-title">基础信息</div>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="商品名称" prop="productName">
                <el-input v-model="form.productName" placeholder="2-30 个字符" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="副标题" prop="productSubtitle">
                <el-input v-model="form.productSubtitle" placeholder="选填，0-50 字" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="商品类型" prop="productTypeId">
                <el-select v-model="form.productTypeId" placeholder="请选择商品类型" filterable style="width:100%;">
                  <el-option v-for="op in typeOptions" :key="op.value" :label="op.label" :value="op.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="服务类型" prop="serviceType">
                <el-radio-group v-model="form.serviceType" @change="onServiceTypeChange">
                  <el-radio :label="1">一对一</el-radio>
                  <el-radio :label="2">一对多</el-radio>
                </el-radio-group>
                <span class="tip" v-if="form.serviceType === 1">一对一：单时段可预约人数锁定为 1</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="商品分类" prop="categoryName">
                <el-input v-model="form.categoryName" placeholder="增肌/减脂/塑形/康复/综合训练等" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="封面图" prop="coverUrl">
                <el-upload
                  class="cover-uploader"
                  :action="uploadUrl"
                  :show-file-list="false"
                  :on-success="onCoverSuccess"
                  :before-upload="beforeCoverUpload"
                  name="file">
                  <img v-if="form.coverUrl" :src="form.coverUrl | formatImg" class="cover-img" />
                  <i v-else class="el-icon-plus cover-uploader-icon"></i>
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="商品简介" prop="productIntro">
                <el-input type="textarea" :rows="2" v-model="form.productIntro" placeholder="选填，填写则需 20-200 字" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="训练目标" prop="targetDesc">
                <el-input type="textarea" :rows="2" v-model="form.targetDesc" placeholder="选填，训练目标说明" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="商品详情" prop="productDetail">
                <!-- 富文本：复用全局 r-tinymce 组件 -->
                <r-tinymce v-model="form.productDetail" :height="260" :width="640"></r-tinymce>
              </el-form-item>
            </el-col>
          </el-row>

          <!-- ② 价格与库存 -->
          <div class="group-title">价格与库存</div>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="原价（划线价）" prop="originalPrice">
                <el-input v-model="form.originalPrice" placeholder="选填，展示用划线价">
                  <template slot="prepend">¥</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="售价" prop="salePrice">
                <el-input v-model="form.salePrice" placeholder="必填，≥0">
                  <template slot="prepend">¥</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="会员优惠价" prop="memberPrice">
                <el-input v-model="form.memberPrice" placeholder="选填">
                  <template slot="prepend">¥</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="新人价" prop="newUserPrice">
                <el-input v-model="form.newUserPrice" placeholder="选填">
                  <template slot="prepend">¥</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="每人限购" prop="purchaseLimit">
                <el-input v-model="form.purchaseLimit" placeholder="留空=不限购" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="可售库存" prop="saleStock">
                <el-input v-model="form.saleStock" placeholder="留空=不限量" />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- ③ 课时与有效期 -->
          <div class="group-title">课时与有效期</div>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="课时数量" prop="lessonCount">
                <el-input v-model="form.lessonCount" placeholder="必填，>0" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="单节时长(分钟)" prop="durationMinutes">
                <!-- duration_minutes 单字段唯一可编辑处；预约规则区只读联动展示 -->
                <el-input v-model="form.durationMinutes" placeholder="默认 60" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="有效期" prop="validityLong">
                <el-radio-group v-model="form.validityLong" @change="onValidityLongChange">
                  <el-radio :label="0">按天数</el-radio>
                  <el-radio :label="1">长期有效</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="有效期天数" prop="validityDays" v-if="form.validityLong === 0">
                <el-input v-model="form.validityDays" placeholder="天数，>0" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="是否支持退款" prop="refundType">
                <el-radio-group v-model="form.refundType">
                  <el-radio :label="1">支持</el-radio>
                  <el-radio :label="2">不支持</el-radio>
                  <el-radio :label="3">人工审核</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="24" v-if="form.refundType === 1">
              <el-form-item label="退款规则" prop="refundRule">
                <el-input type="textarea" :rows="2" v-model="form.refundRule" placeholder="支持退款时必填，说明退款条件" />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- ④ 预约规则 -->
          <div class="group-title">预约规则</div>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="单次服务时长">
                <!-- 与「单节时长」同一字段(duration_minutes)，此处只读联动，避免双写 -->
                <el-input :value="form.durationMinutes" disabled placeholder="同「单节时长」">
                  <template slot="append">分钟</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="预约间隔(分钟)" prop="bookingGapMinutes">
                <el-input v-model="form.bookingGapMinutes" placeholder="默认 0" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="单时段可约人数" prop="bookingCapacity">
                <el-input v-model="form.bookingCapacity" :disabled="form.serviceType === 1" placeholder="一对一固定为 1" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="最晚可预约(小时)" prop="latestBookingHours">
                <el-input v-model="form.latestBookingHours" placeholder="默认 2" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="最晚无责取消(小时)" prop="latestFreeCancelHours">
                <el-input v-model="form.latestFreeCancelHours" placeholder="默认 2" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="爽约是否扣课" prop="noShowDeduct">
                <el-radio-group v-model="form.noShowDeduct">
                  <el-radio :label="1">扣课</el-radio>
                  <el-radio :label="0">不扣</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="预约需教练确认">
                <el-switch v-model="form.coachConfirmRequired" :active-value="1" :inactive-value="0" />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- ⑤ 适用门店 / 指定教练 / 可见人群 -->
          <div class="group-title">适用范围</div>
          <el-row :gutter="16">
            <el-col :span="24">
              <el-form-item label="适用门店" prop="storeIds">
                <el-select v-model="form.storeIds" multiple filterable placeholder="必选至少一个门店" style="width:100%;">
                  <el-option v-for="op in storeOptions" :key="op.value" :label="op.label" :value="op.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="指定教练">
                <el-select v-model="form.coachIds" multiple filterable placeholder="留空=不限教练（符合条件的教练都可约）" style="width:100%;">
                  <el-option v-for="op in coachOptions" :key="op.value" :label="op.label" :value="op.value" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="可见人群">
                <el-checkbox-group v-model="form.visibleGroupsArr">
                  <el-checkbox label="member">会员</el-checkbox>
                  <el-checkbox label="new_user">新用户</el-checkbox>
                  <el-checkbox label="student">学生</el-checkbox>
                </el-checkbox-group>
                <span class="tip">不选=全部可见</span>
              </el-form-item>
            </el-col>
          </el-row>

          <!-- ⑥ 分期与支付设置 -->
          <div class="group-title">分期与支付设置</div>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="是否支持分期">
                <el-switch v-model="form.installmentEnabled" :active-value="1" :inactive-value="0" />
              </el-form-item>
            </el-col>
            <template v-if="form.installmentEnabled === 1">
              <el-col :span="12">
                <el-form-item label="首付金额" prop="installmentDownPaymentAmount">
                  <el-input v-model="form.installmentDownPaymentAmount" placeholder="需 >0 且 <售价">
                    <template slot="prepend">¥</template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="分期期数" prop="installmentCount">
                  <el-input v-model="form.installmentCount" placeholder="≥2" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="每期间隔(月)" prop="installmentIntervalMonths">
                  <el-input v-model="form.installmentIntervalMonths" placeholder="≥1，默认按月" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="逾期暂停预约">
                  <el-switch v-model="form.installmentOverduePauseBooking" :active-value="1" :inactive-value="0" />
                </el-form-item>
              </el-col>
            </template>
          </el-row>

          <!-- ⑦ 附赠团课权益 + 上下架设置 -->
          <div class="group-title">附赠团课权益</div>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="是否赠送团课">
                <el-switch v-model="form.groupBenefitEnabled" :active-value="1" :inactive-value="0" />
              </el-form-item>
            </el-col>
            <template v-if="form.groupBenefitEnabled === 1">
              <el-col :span="12">
                <el-form-item label="赠送团课次数" prop="groupBenefitGiftCount">
                  <el-input v-model="form.groupBenefitGiftCount" placeholder=">0" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="权益有效期(天)" prop="groupBenefitValidityDays">
                  <el-input v-model="form.groupBenefitValidityDays" placeholder=">0" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="适用团课范围">
                  <el-radio-group v-model="form.groupBenefitScopeType">
                    <el-radio :label="1">全部团课</el-radio>
                    <el-radio :label="2">指定团课商品</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="24" v-if="form.groupBenefitScopeType === 2">
                <el-form-item label="指定团课商品" prop="groupProductIds">
                  <el-select v-model="form.groupProductIds" multiple filterable placeholder="范围=指定时必选" style="width:100%;">
                    <el-option v-for="op in groupClassOptions" :key="op.value" :label="op.label" :value="op.value" />
                  </el-select>
                </el-form-item>
              </el-col>
            </template>
          </el-row>

          <div class="group-title">上下架设置</div>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="上架状态" prop="listingStatus">
                <el-radio-group v-model="form.listingStatus">
                  <el-radio :label="1">已上架</el-radio>
                  <el-radio :label="0">未上架</el-radio>
                </el-radio-group>
                <span class="tip" v-if="form.listingStatus === 1">保存时将执行上架校验</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="排序权重" prop="sortNo">
                <el-input v-model="form.sortNo" placeholder="越大越靠前" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="定时上架时间">
                <el-date-picker v-model="form.listingAt" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选填，到点自动上架" style="width:100%;" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="定时下架时间">
                <el-date-picker v-model="form.unlistingAt" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选填，到点自动下架" style="width:100%;" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="私教入口推荐">
                <el-switch v-model="form.recommendPrivate" :active-value="1" :inactive-value="0" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="首页推荐">
                <el-switch v-model="form.recommendHome" :active-value="1" :inactive-value="0" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <div class="drawer-footer">
          <el-button @click="drawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="submitForm">保存</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
export default {
  components: {},
  data () {
    return {
      tableLoading: false,
      selection: [],
      // 下拉数据源
      typeOptions: [],
      storeOptions: [],
      coachOptions: [],
      groupClassOptions: [],
      // id -> name 映射（列表展示用）
      typeMap: {},
      searchData: {
        productName: '',
        productTypeId: '',
        serviceType: '',
        storeId: '',
        coachId: '',
        minPrice: '',
        maxPrice: '',
        listingStatus: '',
        createTime: []
      },
      searchForm: [
        { type: 'input', placeholder: '商品名称', prop: 'productName', width: 160 },
        { type: 'select', placeholder: '商品类型', prop: 'productTypeId', width: 150, options: [] },
        { type: 'select',
          placeholder: '服务类型',
          prop: 'serviceType',
          width: 130,
          options: [
          { value: 1, label: '一对一' },
          { value: 2, label: '一对多' }
          ] },
        { type: 'select', placeholder: '适用门店', prop: 'storeId', width: 160, options: [] },
        { type: 'select', placeholder: '指定教练', prop: 'coachId', width: 150, options: [] },
        { type: 'input', placeholder: '最低价', prop: 'minPrice', width: 110 },
        { type: 'input', placeholder: '最高价', prop: 'maxPrice', width: 110 },
        { type: 'select',
          placeholder: '上架状态',
          prop: 'listingStatus',
          width: 130,
          options: [
          { value: 1, label: '已上架' },
          { value: 0, label: '未上架' }
          ] },
        { type: 'daterange', placeholder: '创建时间', prop: 'createTime', width: 240, value_format: 'yyyy-MM-dd' }
      ],
      searchHandle: [
        { label: '搜索', type: 'primary', handle: e => this.getData() },
        { label: '重置', handle: e => this.reset() }
      ],
      tableHandles: [
        { label: '新增商品', type: 'primary', icon: 'el-icon-plus', isShow: () => this.checkBtn('sys:ptproduct:save'), handle: e => this.openAdd() },
        { label: '批量删除', type: 'danger', icon: 'el-icon-delete', isShow: () => this.checkBtn('sys:ptproduct:delete'), handle: e => this.delBatch() },
        { label: '导出', icon: 'el-icon-download', handle: e => this.exportData() }
      ],
      tableData: [],
      tableCols: [
        { label: '编号', prop: 'productNo', width: 140 },
        { label: '封面', type: 'image', prop: 'coverUrl', width: 80 },
        { label: '商品名称', type: 'html', width: 180, html: (row) => this.nameHtml(row) },
        { label: '类型', type: 'tag', width: 90, prop: 'typeName', theme: () => 'primary', formatter: e => e.typeName || '-' },
        { label: '服务类型', type: 'tag', width: 90, prop: 'serviceType', theme: (row) => row.serviceType === 1 ? 'info' : 'warning', formatter: e => e.serviceType === 1 ? '一对一' : '一对多' },
        // 适用门店 / 指定教练：后端 list 行未返回聚合名与 id，进「编辑」详情可见完整配置
        { label: '适用门店', width: 90, formatter: () => '详情查看' },
        { label: '指定教练', width: 90, formatter: () => '详情查看' },
        { label: '价格', type: 'html', width: 130, html: (row) => this.priceHtml(row) },
        { label: '课时', prop: 'lessonCount', width: 70 },
        { label: '单次时长', prop: 'durationMinutes', width: 90, formatter: e => (e.durationMinutes || 0) + '分钟' },
        { label: '有效期', prop: 'validityDays', width: 90, formatter: e => e.validityDays === -1 ? '长期' : ((e.validityDays || 0) + '天') },
        { label: '上架状态', type: 'switch', prop: 'listingStatus', width: 100, values: [1, 0], change: (row) => this.changeListing(row), isDisabled: () => !this.checkBtn('sys:ptproduct:update') },
        { label: '排序', prop: 'sortNo', width: 70 },
        { label: '创建时间', prop: 'createdAt', width: 160, formatter: e => this.parseTime(e.createdAt) },
        { label: '更新时间', prop: 'updatedAt', width: 160, formatter: e => this.parseTime(e.updatedAt) },
        {
          label: '操作',
          type: 'button',
          width: 220,
          fixed: 'right',
          btnList: [
            { label: '编辑', type: 'success', size: 'mini', icon: 'el-icon-edit', isShow: () => this.checkBtn('sys:ptproduct:update'), handle: (row) => this.openEdit(row) },
            { label: '复制', type: 'primary', size: 'mini', icon: 'el-icon-document-copy', isShow: () => this.checkBtn('sys:ptproduct:save'), handle: (row) => this.copyProduct(row) },
            { label: '删除', type: 'danger', size: 'mini', icon: 'el-icon-delete', isShow: () => this.checkBtn('sys:ptproduct:delete'), handle: (row) => this.del(row) }
          ]
        }
      ],
      pagination: { limit: 10, offset: 1, total: 0 },
      // ===== 抽屉表单 =====
      drawerVisible: false,
      drawerLoading: false,
      submitLoading: false,
      drawerTitle: '新增商品',
      uploadUrl: (process.env.NODE_ENV !== 'production' ? 'proxyApi/' : process.env.VUE_APP_URL) + '/sys/uploads',
      form: this.blankForm(),
      formRules: {
        productName: [
          { required: true, message: '请输入商品名称', trigger: 'blur' },
          { min: 2, max: 30, message: '商品名称长度 2-30 个字符', trigger: 'blur' }
        ],
        productTypeId: [
          { required: true, message: '请选择商品类型', trigger: 'change' }
        ],
        serviceType: [
          { required: true, message: '请选择服务类型', trigger: 'change' }
        ],
        salePrice: [
          { required: true, message: '请输入售价', trigger: 'blur' }
        ],
        lessonCount: [
          { required: true, message: '请输入课时数量', trigger: 'blur' }
        ],
        durationMinutes: [
          { required: true, message: '请输入单节时长', trigger: 'blur' }
        ],
        storeIds: [
          { required: true, type: 'array', min: 1, message: '请至少选择一个适用门店', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    statCards () {
      var total = this.tableData.length
      var priceSum = 0
      var listedNum = 0
      this.tableData.forEach(function (r) {
        priceSum += Number(r.salePrice) || 0
        if (r.listingStatus === 1) listedNum++
      })
      var avg = total ? (priceSum / total) : 0
      return [
        { label: '商品总数（当前页）', value: total, unit: '个', color: '#409eff' },
        { label: '总售价金额', value: '¥' + priceSum.toFixed(2), unit: '', color: '#e6a23c' },
        { label: '均价', value: '¥' + avg.toFixed(2), unit: '', color: '#67c23a' },
        { label: '已上架数', value: listedNum, unit: '个', color: '#f56c6c' }
      ]
    }
  },
  mounted () {
    this.getData()
    this.getTypeOptions()
    this.getStoreList()
    this.getCoachList()
    this.getGroupClassList()
  },
  methods: {
    // 表单初始值（product_no/sold_count 后端维护，不入表单）
    blankForm () {
      return {
        id: '',
        productName: '',
        productSubtitle: '',
        productTypeId: '',
        serviceType: 1,
        categoryName: '',
        coverUrl: '',
        productIntro: '',
        productDetail: '',
        targetDesc: '',
        originalPrice: '',
        salePrice: '',
        memberPrice: '',
        newUserPrice: '',
        lessonCount: '',
        durationMinutes: 60,
        validityLong: 0, // 前端辅助：0=按天数 1=长期(-1)
        validityDays: 365,
        refundType: 2,
        refundRule: '',
        visibleGroupsArr: [], // 前端辅助：提交时 join 成 visibleGroups 字符串
        purchaseLimit: '',
        saleStock: '',
        bookingGapMinutes: 0,
        bookingCapacity: 1,
        latestBookingHours: 2,
        latestFreeCancelHours: 2,
        noShowDeduct: 1,
        coachConfirmRequired: 0,
        storeIds: [],
        coachIds: [],
        installmentEnabled: 0,
        installmentDownPaymentAmount: '',
        installmentCount: '',
        installmentIntervalMonths: '',
        installmentOverduePauseBooking: 1,
        groupBenefitEnabled: 0,
        groupBenefitGiftCount: '',
        groupBenefitValidityDays: '',
        groupBenefitScopeType: 1,
        groupProductIds: [],
        listingStatus: 0,
        listingAt: '',
        unlistingAt: '',
        sortNo: 0,
        recommendPrivate: 0,
        recommendHome: 0
      }
    },
    // ===== 列表展示辅助 =====
    nameHtml (row) {
      var name = row.productName || '-'
      var sub = row.productSubtitle ? ('<div style="color:#909399;font-size:12px;">' + row.productSubtitle + '</div>') : ''
      var cate = row.categoryName ? ('<div style="color:#c0c4cc;font-size:12px;">' + row.categoryName + '</div>') : ''
      return '<div>' + name + '</div>' + sub + cate
    },
    priceHtml (row) {
      var sale = (row.salePrice === 0 || row.salePrice) ? row.salePrice : '-'
      var html = '<span style="font-weight:600;color:#f56c6c;">¥' + sale + '</span>'
      if (row.originalPrice) {
        html += '<div style="text-decoration:line-through;color:#999;font-size:12px;">¥' + row.originalPrice + '</div>'
      }
      return html
    },
    // ===== 列表 =====
    async getData () {
      this.tableLoading = true
      try {
        var params = {
          page: this.pagination.offset,
          limit: this.pagination.limit,
          productName: this.searchData.productName,
          productTypeId: this.searchData.productTypeId,
          serviceType: this.searchData.serviceType,
          storeId: this.searchData.storeId,
          coachId: this.searchData.coachId,
          minPrice: this.searchData.minPrice,
          maxPrice: this.searchData.maxPrice,
          listingStatus: this.searchData.listingStatus
        }
        var range = this.searchData.createTime || []
        if (range.length === 2) {
          // 后端 mapper 用 beginTime/endTime 过滤 created_at
          params.beginTime = range[0] + ' 00:00:00'
          params.endTime = range[1] + ' 23:59:59'
        }
        var res = await this.apis.ptProduct_list(params)
        var page = res.page || {}
        this.tableData = page.list || []
        this.pagination.total = page.totalCount || 0
      } finally {
        this.tableLoading = false
      }
    },
    page () {
      this.getData()
    },
    reset () {
      this.searchData = { productName: '', productTypeId: '', serviceType: '', storeId: '', coachId: '', minPrice: '', maxPrice: '', listingStatus: '', createTime: [] }
      this.pagination.offset = 1
      this.getData()
    },
    handleSelect (rows) {
      this.selection = rows || []
    },
    // ===== 下拉数据 =====
    async getTypeOptions () {
      var res = await this.apis.ptProductType_options()
      var list = res.list || []
      var map = {}
      var opts = list.map(function (r) {
        map[r.id] = r.typeName
        return { value: r.id, label: r.typeName }
      })
      this.typeMap = map
      this.typeOptions = opts
      this.searchForm[this.searchIndex(this.searchForm, '商品类型')].options = opts
    },
    async getStoreList () {
      var res = await this.apis.store_list({ page: 1, limit: 999 })
      var list = (res.page && res.page.list) || []
      var opts = list.map(function (item) {
        return { value: item.storeAddrId, label: item.storeName }
      })
      this.storeOptions = opts
      this.searchForm[this.searchIndex(this.searchForm, '适用门店')].options = opts
    },
    async getCoachList () {
      var res = await this.apis.ptCoach_list({ page: 1, limit: 999 })
      var list = (res.page && res.page.list) || []
      var opts = list.map(function (item) {
        return { value: item.id, label: item.coachName + (item.mobile ? ('（' + item.mobile + '）') : '') }
      })
      this.coachOptions = opts
      this.searchForm[this.searchIndex(this.searchForm, '指定教练')].options = opts
    },
    async getGroupClassList () {
      var res = await this.apis.ptProduct_groupClassOptions()
      var list = res.list || []
      // 团课实体：id + className
      this.groupClassOptions = list.map(function (item) {
        return { value: item.id, label: item.className || ('团课#' + item.id) }
      })
    },
    // ===== 服务类型 / 有效期联动 =====
    onServiceTypeChange (val) {
      // 一对一：单时段人数锁 1
      if (val === 1) this.form.bookingCapacity = 1
    },
    onValidityLongChange (val) {
      // 长期：validityDays 提交时置 -1；切回按天数给个默认
      if (val === 1) {
        // 长期不显示天数输入
      } else if (!this.form.validityDays || Number(this.form.validityDays) <= 0) {
        this.form.validityDays = 365
      }
    },
    // ===== 封面上传 =====
    beforeCoverUpload (file) {
      var isImg = file.type === 'image/jpeg' || file.type === 'image/png'
      if (!isImg) this.$message.error('封面只能是 JPG/PNG 格式')
      return isImg
    },
    onCoverSuccess (res) {
      if (res && res.code === 0 && res.path && res.path.length) {
        this.form.coverUrl = res.path[0]
      } else {
        this.$message.error((res && res.msg) || '上传失败')
      }
    },
    // ===== 新增 / 编辑 =====
    openAdd () {
      this.form = this.blankForm()
      this.drawerTitle = '新增商品'
      this.drawerVisible = true
      this.clearValidate()
    },
    openEdit (row) {
      this.drawerTitle = '编辑商品'
      this.drawerVisible = true
      this.drawerLoading = true
      this.form = this.blankForm()
      this.apis.ptProduct_info({ id: row.id }).then((res) => {
        if (res && res.code === 0) {
          this.fillForm(res.product || {})
        }
      }).finally(() => {
        this.drawerLoading = false
        this.clearValidate()
      })
    },
    // 把后端 product(扁平实体) 回填到表单
    fillForm (p) {
      var form = this.blankForm()
      Object.keys(form).forEach(function (key) {
        if (p[key] !== undefined && p[key] !== null) form[key] = p[key]
      })
      // 数组类字段
      form.storeIds = Array.isArray(p.storeIds) ? p.storeIds.slice() : []
      form.coachIds = Array.isArray(p.coachIds) ? p.coachIds.slice() : []
      form.groupProductIds = Array.isArray(p.groupProductIds) ? p.groupProductIds.slice() : []
      // 有效期：-1 -> 长期
      if (p.validityDays === -1) {
        form.validityLong = 1
        form.validityDays = 365
      } else {
        form.validityLong = 0
        form.validityDays = (p.validityDays === 0 || p.validityDays) ? p.validityDays : 365
      }
      // 可见人群字符串 -> 数组
      form.visibleGroupsArr = p.visibleGroups ? String(p.visibleGroups).split(',').filter(function (x) { return x }) : []
      form.id = p.id || ''
      this.form = form
    },
    clearValidate () {
      this.$nextTick(() => {
        if (this.$refs.productForm) this.$refs.productForm.clearValidate()
      })
    },
    // 组装提交 payload（扁平 PtProductEntity）
    buildPayload () {
      var f = this.form
      var data = Object.assign({}, f)
      // 移除前端辅助字段
      delete data.validityLong
      delete data.visibleGroupsArr
      // 有效期
      data.validityDays = f.validityLong === 1 ? -1 : (f.validityDays === '' ? '' : Number(f.validityDays))
      // 可见人群数组 -> 字符串（后端字段为 String）
      data.visibleGroups = (f.visibleGroupsArr || []).join(',')
      // 一对一强制单时段 1
      if (Number(f.serviceType) === 1) data.bookingCapacity = 1
      return data
    },
    submitForm () {
      this.$refs.productForm.validate((valid) => {
        if (!valid) {
          this.$message.warning('请完善必填项')
          return
        }
        // 前端补充校验：分期/附赠开启时的必填与范围
        var f = this.form
        if (f.installmentEnabled === 1) {
          var down = Number(f.installmentDownPaymentAmount)
          var cnt = Number(f.installmentCount)
          var itv = Number(f.installmentIntervalMonths)
          if (!(down > 0) || !(cnt >= 2) || !(itv >= 1)) {
            this.$message.error('分期已开启：首付需>0、期数≥2、每期间隔≥1')
            return
          }
          if (f.salePrice !== '' && down >= Number(f.salePrice)) {
            this.$message.error('首付金额需小于售价')
            return
          }
        }
        if (f.groupBenefitEnabled === 1) {
          if (!(Number(f.groupBenefitGiftCount) > 0) || !(Number(f.groupBenefitValidityDays) > 0)) {
            this.$message.error('附赠团课已开启：赠送次数与有效期需>0')
            return
          }
          if (f.groupBenefitScopeType === 2 && (!f.groupProductIds || !f.groupProductIds.length)) {
            this.$message.error('适用范围为「指定团课商品」时，请至少选择一个团课')
            return
          }
        }
        if (f.refundType === 1 && !f.refundRule) {
          this.$message.error('支持退款时，请填写退款规则')
          return
        }
        this.submit()
      })
    },
    async submit () {
      var data = this.buildPayload()
      this.submitLoading = true
      try {
        var res = !data.id
          ? await this.apis.ptProduct_save(data)
          : await this.apis.ptProduct_update(data)
        if (res && res.code === 0) {
          this.$message.success('操作成功')
          this.drawerVisible = false
          this.getData()
        }
      } catch (e) { /* 失败已由响应拦截器弹错误提示（含上架校验缺项） */ } finally {
        this.submitLoading = false
      }
    },
    // ===== 上架 / 下架（表格开关）=====
    changeListing (row) {
      var toUp = row.listingStatus === 1
      var p = toUp ? this.apis.ptProduct_onCard([row.id]) : this.apis.ptProduct_offCard([row.id])
      p.then((res) => {
        if (res && res.code === 0) {
          this.$message.success(toUp ? '已上架' : '已下架')
          this.getData()
        } else {
          row.listingStatus = toUp ? 0 : 1 // 非成功回滚开关
        }
      }).catch(() => {
        row.listingStatus = toUp ? 0 : 1 // 异常/校验不过回滚（提示已由拦截器弹出）
      })
    },
    // ===== 复制 =====
    copyProduct (row) {
      this.$confirm('确定要复制商品【' + (row.productName || '') + '】吗？复制后为未上架状态。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.ptProduct_copy({ id: row.id })
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('复制成功')
          this.getData()
        }
      }).catch(() => {})
    },
    // ===== 删除 =====
    del (row) {
      this.$confirm('确定要删除商品【' + (row.productName || '') + '】吗？已产生订单/预约的商品不可删除。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.ptProduct_delete([row.id])
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('删除成功')
          this.getData()
        }
      }).catch(() => {})
    },
    delBatch () {
      if (!this.selection.length) {
        this.$message.warning('请先勾选要删除的商品')
        return
      }
      var ids = this.selection.map(function (r) { return r.id })
      this.$confirm('确定要批量删除选中的 ' + ids.length + ' 个商品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return this.apis.ptProduct_delete(ids)
      }).then((res) => {
        if (res && res.code === 0) {
          this.$message.success('删除成功')
          this.getData()
        }
      }).catch(() => {})
    },
    exportData () {
      // TODO: 导出接口未提供，先占位提示
      this.$message.info('导出功能待接入')
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
.stat-row {
  margin-bottom: 16px;
}
.stat-card {
  text-align: center;
  .stat-label {
    font-size: 13px;
    color: #909399;
  }
  .stat-value {
    font-size: 26px;
    font-weight: 700;
    margin: 8px 0 2px;
  }
  .stat-unit {
    font-size: 12px;
    color: #c0c4cc;
  }
}
// 抽屉表单
.drawer-body {
  padding: 0 20px 80px;
}
.group-title {
  position: relative;
  padding-left: 10px;
  margin: 18px 0 14px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 2px;
    bottom: 2px;
    width: 4px;
    border-radius: 2px;
    background: #409eff;
  }
}
.tip {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}
.cover-uploader {
  ::v-deep .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    width: 100px;
    height: 100px;
    display: flex;
    align-items: center;
    justify-content: center;
    &:hover { border-color: #409eff; }
  }
}
.cover-uploader-icon {
  font-size: 24px;
  color: #8c939d;
}
.cover-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  display: block;
}
.drawer-footer {
  position: fixed;
  bottom: 0;
  right: 0;
  width: 820px;
  padding: 12px 20px;
  background: #fff;
  border-top: 1px solid #ebeef5;
  text-align: right;
  z-index: 10;
}
</style>
