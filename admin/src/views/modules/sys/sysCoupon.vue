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
    <r-form labelWidth="150px" :isHandle="true" :formRules="formRules" :formCols="formCols" :formHandle="formHandle" :formData="formData" ref="elForm" :inline="false" dialogWidth="700px" />

  </div>
</template>

<script>
export default {
  components: {
    
  },
  data() {
    return {
      tableLoading: false,
      searchData: {
        couponTitle:'',
        couponStatus:'',
		couponNew:'',
		goodsId:'',
		douyinSkuId:''
      },
      searchForm: [
        { type: "input", placeholder: "券名称", prop: "couponTitle", width: 200 },
        { type: "input", placeholder: "美团团购ID", prop: "goodsId", width: 200 },
        { type: "input", placeholder: "抖音团购ID", prop: "douyinSkuId", width: 200 },
		{ type: "select", placeholder: "是否新人券", prop: "couponNew", width: 200, options:[
			{value: 1, label: '是'},
			{value: 0, label: '否'}
		] },
        { type: "select", placeholder: "状态", prop: "couponStatus", width: 200, options:[
			{value: 1, label: '上架'},
			{value: 2, label: '下架'}
		] },
      ],
      searchHandle: [
        {
          label: "搜索",
          type: "primary",
          handle: e => this.getData()
        },
      ],
      tableHandles: [
        {
          label: "添加",
          type: "primary",
          handle: e => {
		  	this.formData = this.addOrUpdateFormData.formData
		  	this.formCols = this.addOrUpdateFormData.formCols
		  	this.formRules = this.addOrUpdateFormData.formRules
			this.formHandle = this.addOrUpdateFormData.formHandle
		  	this.elFormVisible()
		  } ,
		  isShow: e => this.checkBtn('sys:sysCoupon:add')
        }
      ],
      tableData: [],
      tableCols: [
        { label: "ID", prop: "sysCouponId" },
		{ label: '二维码', prop: 'sysCouponId', type: 'qrCode', formatter: e => {
			return process.env.VUE_APP_URL + '/coupon/?id=' + e.sysCouponId
		}},
        { label: "券名称", prop: "couponTitle" },
        { label: "美团团购ID", prop: "goodsId" },
        { label: "抖音团购ID", prop: "douyinSkuId" },
        { label: "优惠金额", prop: "couponPrice" },
        { label: "使用门槛", prop: "limitPrice" },
		{ label: '有效天数', prop: 'validity' },
		{
			label: '是否新人券',
			prop: 'couponNew',
			formatter: e => { return e.couponNew == 1 ? '是' : '否' }
		},
        {
			label: '状态',
			type: "switch",
			prop: 'couponStatus',
			values: [1, 2],
			change: (row) => this.changeStatus(row),
			isDisabled: e => !this.checkBtn('sys:sysCoupon:status')
		},
        { label: "创建时间", prop: 'createdDate', formatter: e => { return this.parseTime(e.createdDate) } },		
        {
          label: "操作",
          type: "button",
          width: 360,
          btnList: [
            {
              label: "编辑",
              type: "success",
              size: "mini",
              icon: "el-icon-edit",
              handle: (row) => {
			    this.formData = this.addOrUpdateFormData.resetData
		  	    this.formCols = this.addOrUpdateFormData.formCols
		  	    this.formRules = this.addOrUpdateFormData.formRules
		  	    this.formHandle = this.addOrUpdateFormData.formHandle
			  	this.elFormDetail(row)
			  },
			  isShow: e => this.checkBtn('sys:sysCoupon:edit')
            },
            {
              label: "删除",
              type: "danger",
              size: "mini",
              icon: "el-icon-delete",
              handle: (row) => this.del(row),
			  isShow: e => this.checkBtn('sys:sysCoupon:del')
            },
            {
              label: "发放优惠券",
              type: "primary",
              size: "mini",
              icon: "el-icon-upload",
              handle: (row) => this.send(row),
			  isShow: e => e.couponStatus == 1 && this.checkBtn('sys:sysCoupon:send')
            }
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
	  formData: {},
	  formCols: [],
	  formRules: {},
	  formHandle: [],
      addOrUpdateFormData: {
		  resetData: {
			sysCouponId:'',
			couponTitle:'',
			goodsId:'',
			douyinSkuId:'',
			couponPrice: '',
			limitPrice: "",
			validity:'',
			storeAddrIds:'',
			couponStatus: 1
		  },
		  formData: {
			sysCouponId:'',
			couponTitle:'',
			goodsId:'',
			douyinSkuId:'',
			couponPrice: '',
			limitPrice: "",
			validity:'',
			storeAddrIds:'',
			couponStatus: 1,
			couponNew:0
		  },
		  formCols: [
			{ type: "input", label: "券名称", width: 350, prop: "couponTitle" },
			{ type: "input", label: "美团团购ID", width: 350, prop: "goodsId" },
			{ type: "input", label: "抖音团购ID", width: 350, prop: "douyinSkuId" },
			{ type: "input", label: "优惠金额", width: 350, prop: "couponPrice" },	
			{ type: "input", label: "使用门槛", width: 350, prop: "limitPrice" },	
			{ type: "input", label: "有效天数", width: 350, prop: "validity" },	
			{ type: "select", label: "可用门店", width: 350, prop: "storeAddrIds", multiple: true },	
			{ type: "radio", label: "是否新人券", width: 350, prop: "couponNew", radios: [
				{value: 1, label: '是'},
				{value: 0, label: '否'}
			] },
			{ type: "radio", label: "状态", width: 350, prop: "couponStatus", radios: [
				{value: 1, label: '上架'},
				{value: 2, label: '下架'}
			] },
		  ],
		  formRules: {
			couponTitle: [
			  { required: true, message: '请输入', trigger: 'blur' },
			],
			couponPrice: [
			  { required: true, message: '请输入', trigger: 'blur' },
			],
		  },
		  formHandle: [
			{
			  label: "确认",
			  type: "primary",
			  icon: "el-icon-circle-plus-outline",
			  handle: e => this.elFormSubmit(),
			},
			{
			  label: "取消",
			  icon: "el-icon-circle-close",
			  handle: e => this.elFormVisible(),
			},
		  ],
	  },
	  sendFormData: {
		  resetData: {
			sysCouponId:'',
			couponTitle:'',
			userIds: '',
		  },
		  formData: {
			sysCouponId:'',
			couponTitle:'',
			userIds: '',
		  },
		  formCols: [
			{ type: "input", label: "券名称", width: 350, prop: "couponTitle", isDisabled: e => { return true } },
			{ type: "select", label: "选择用户", width: 350, prop: "userIds", multiple: true, 
			pageSize: 30,
			handleSizeChange: e => {
				this.getUserList()
			},
			handleCurrentChange: e => {
				this.getUserList(e, 30)
			},
			handleSearch: e => {
				this.getUserList(1, 30, e)
			},
			currentPage: 1,
			total: 0
			}					
		  ],
		  formRules: {
			userIds: [
			  { required: true, message: '请输入', trigger: 'blur' },
			],
		  },
		  formHandle: [
			{
			  label: "确认",
			  type: "primary",
			  icon: "el-icon-circle-plus-outline",
			  handle: e => this.elSendSubmit(),
			},
			{
			  label: "取消",
			  icon: "el-icon-circle-close",
			  handle: e => this.elFormVisible(),
			},
		  ],
	  }
    };
  },
  created() { },
  mounted() {
    this.getData();
	this.getStoreList()
	this.getUserList(1, 30);
  },
  methods: {
    del(row) {
      this.$confirm('确定要删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        var res = await this.apis.sysCoupon_delete(
          [row.sysCouponId]
        );
        this.$message({
          type: 'success',
          message: '删除成功!'
        });
        this.getData();
      }).catch(() => {

      });
    },
	send(row){
		this.formData = this.sendFormData.resetData
		this.formCols = this.sendFormData.formCols
		this.formRules = this.sendFormData.formRules
		this.formHandle = this.sendFormData.formHandle
		this.elFormDetail(row, '发放优惠券')
	},
	async getStoreList() {
		var res = await this.apis.store_list({
			page: 1,
        	limit: 999,
		});
		  let list = res.page.list
		  list.forEach(item=>{
				item.value = item.storeAddrId
				item.label = item.storeName
			})


      this.addOrUpdateFormData.formCols[this.labIndex(this.addOrUpdateFormData.formCols, '可用门店')].options = list;
	},
	async getUserList(page, limit, phone) {
		var res = await this.apis.user_list2({
			page: page,
        	limit: limit,
			phone: phone || ''
		});
		  let list = res.page.list
		  list.forEach(item=>{
				item.value = item.userId
				item.label = item.phone
			})


      this.sendFormData.formCols[this.labIndex(this.sendFormData.formCols, '选择用户')].options = list;
	  this.sendFormData.formCols[this.labIndex(this.sendFormData.formCols, '选择用户')].total = res.page.totalCount
	  this.sendFormData.formCols[this.labIndex(this.sendFormData.formCols, '选择用户')].currentPage = page
	  this.formCols = this.sendFormData.formCols
	},
    async getData() {
      var res = await this.apis.sysCoupon_list(Object.assign({
	  	type: 5,
        page: this.pagination.offset,
        limit: this.pagination.limit
      }, this.searchData));
      var list = res.page.list || [];
	  list.map(res => {
	  	res.storeAddrIds = res.storeAddrIds ? res.storeAddrIds.split(',') : []
		res.storeAddrIds = res.storeAddrIds.map(rres => {
			return rres * 1
		})
	  })
      this.tableData = list;
      this.pagination.total = res.page.totalCount;
    },
    page() {
      this.getData();
    },
    createdAcc() {
      this.elFormVisible();
    },
    async elFormDetail(row, tit) {

      Object.keys(this.formData).forEach((key) => {
		this.formData[key] = row[key];
	  });

      this.elFormVisible(tit || '编辑')
    },
    elFormSubmit() {
      this.$refs.elForm.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.submit();
        }
      })
    },
    async submit() {
	  let formData = Object.assign({}, this.formData)
	  formData.storeAddrIds = formData.storeAddrIds.join(',')
      if (!formData.sysCouponId) {
        var res = await this.apis.sysCoupon_save(formData);
      } else {
        var res = await this.apis.sysCoupon_update(formData);
      }
      this.elFormVisible();

      this.getData();

    },
	async changeStatus(row) {
		var res = await this.apis.sysCoupon_update({
			sysCouponId: row.sysCouponId,
			couponStatus: row.couponStatus
		});
		//this.getData();
	},
    async elSendSubmit() {
      this.$refs.elForm.$refs.ruleForm.validate(async(valid) => {
        if (valid) {
		  let formData = Object.assign({}, this.formData)
		  formData.userIds = formData.userIds.join(',')
          const res = await this.apis.sysCoupon_send(Object.assign(formData, {couponTitle : null}));
		  const failCount = res.failCount || 0
		  const successCount = res.successCount || 0
		  if (failCount > 0) {
			this.$message({
			  type: successCount > 0 ? 'warning' : 'error',
			  message: res.msg || '发放完成',
			  duration: 8000,
			  showClose: true
			})
		  } else {
			this.$message({
			  type: 'success',
			  message: res.msg || '发放成功'
			})
		  }
		  if (successCount > 0) {
			this.getData()
		  }
		  this.elFormVisible();
        }
      })
    }
  },
};
</script>

<style scoped lang="scss">
</style>
