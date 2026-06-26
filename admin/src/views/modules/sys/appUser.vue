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
        phone:'',
        nickname:'',
        wtState:'',
		auditStatus: '',
		remark:'',
		startTime:'',
		endTime:'',
		regTime: []
      },
      searchForm: [
        { type: "input", placeholder: "姓名", prop: "nickname", width: 200 },
        { type: "input", placeholder: "手机号", prop: "phone", width: 200 },
        { type: "select", placeholder: "签约状态", prop: "wtState", width: 200,options: [
			{value: 0, 'label': '未签约'},
			{value: 1, 'label': '已签约'},
			{value: 2, 'label': '已解约'}
		]},
		{ type: "select", placeholder: "状态", prop: "auditStatus", width: 200,options: [
			{value: 1, 'label': '正常'},
			{value: 2, 'label': '黑名单'}
		]},
        //{ type: "input", placeholder: "备注", prop: "remark", width: 200 },
        { type: "daterange", placeholder: "注册时间", prop: "regTime", width: 200, change:e=> {
			console.log(e)
			if(e){
				this.searchData.startTime = e[0] + ' 00:00:00'			
				this.searchData.endTime = e[1] + ' 23:59:59'
			}else{
				this.searchData.startTime = this.searchData.endTime = ''
			}
		} },
      ],
      searchHandle: [
        {
          label: "搜索",
          type: "primary",
          handle: e => this.getData()
        },
      ],
      tableData: [],
      tableCols: [
        { label: "ID", prop: "userId" },
        { label: "姓名", prop: "nickname" },
        { label: "手机号", prop: "phone" },
		{ label: "头像", prop: "headImgUrl", type: 'image' },
        //{ label: "备注", prop: "remark" },
        { label: "openId", prop: "openId" },
		{
			label: '签约状态',
			prop: 'wtStateDesc',			
		},
		{
			label: '是否会员',
			prop: 'faceStatusDesc'
		},
		{
			label: '状态',
			prop: 'auditStatusDesc'
		},
        { label: "注册时间", prop: "createdDate", formatter: e => { return this.parseTime(e.createdDate) } },
        {
          label: "操作",
          type: "button",
          width: 360,
          btnList: [
            {    
			  des: 'auditDes',
              type: e => {
			  	return e.auditStatus == 1 ? 'danger' : "success"
			  },
              size: "mini",
              icon: "el-icon-lock",
              handle: (row) => {
			  	this.$confirm(row.auditStatus == 1 ? '确定要拉黑用户吗?' : '确定要解除黑名单吗', '提示', {
						confirmButtonText: '确定',
						cancelButtonText: '取消',
						type: 'warning'
					  }).then(async () => {
			  			var res = await this.apis.user_update2(
						  {
						  auditStatus: row.auditStatus == 1 ? 2 : 1,
						  userId: row.userId
						  }
						);
						this.$message({
						  type: 'success',
						  message: '操作成功!'
						});
						this.getData();
					}).catch(() => {

					  });
            	},
				isShow: e => this.checkBtn('sys:appuser:auditStatus')
			},
            {
              label: "解绑手机",
              type: "primary",
              size: "mini",
              icon: "el-icon-mobile",
              handle: (row) => {
			  		this.$confirm('确定要解绑微信/手机吗?', '提示', {
						confirmButtonText: '确定',
						cancelButtonText: '取消',
						type: 'warning'
					  }).then(async () => {
						var res = await this.apis.user_update2(
						  {
						  phone: 'NULL',
						  userId: row.userId
						  }
						);
						this.$message({
						  type: 'success',
						  message: '解绑成功!'
						});
						this.getData();
					  }).catch(() => {

					  });
			  },
			  isShow: e => !!e.phone && this.checkBtn('sys:appuser:mobile')
            },
			{
              label: "清除用户数据",
              type: "danger",
              size: "mini",
              icon: "el-icon-delete",
              handle: (row) => this.openClearData(row),
			  isShow: e => this.checkBtn('sys:appuser:clearData')
            }
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
	  tableHandles: [
		{
          label: "导出",
          type: "primary",
          handle: e => {
			const data = { ...this.searchData }
			delete data.regTime
			const parts = []
			for (const key in data) {
				const val = data[key]
				if (val !== '' && val !== null && val !== undefined) {
					parts.push(key + '=' + encodeURIComponent(val))
				}
			}
			window.open('/sys/user/exportAppList?' + parts.join('&'))
		  }
        }
	  ],
	  formData: {},
	  formCols: [],
	  formHandle: [],
	  formRules: {},
	  clearDataForm: {
		formData: {
			userId: '',
			nickname: '',
			phone: '',
			password: '',
			clearTypes: []
		},
		formCols: [
			{ type: 'input', label: '用户', width: 500, prop: 'nickname', isDisabled: () => true },
			{ type: 'input', label: '手机号', width: 500, prop: 'phone', isDisabled: () => true },
			{ type: 'input', label: '登录密码确认', width: 500, prop: 'password', inputType: 'password', placeholder: '请输入当前管理员登录密码' },
			{ type: 'checkbox', label: '清除项', width: 500, prop: 'clearTypes', checkboxs: [
				{ value: 'memberCard', label: '会员卡' },
				{ value: 'coupon', label: '优惠券' },
				{ value: 'openDoor', label: '开关门记录' },
				{ value: 'incomePayDetail', label: '购卡记录（收支明细）' },
				{ value: 'cardOrder', label: '购卡订单' },
				{ value: 'validityChange', label: '有效期变更记录' },
				{ value: 'storeChange', label: '门店变更记录' },
				{ value: 'delUserRecord', label: '会员注销记录' }
			] }
		],
		formRules: {
			password: [{ required: true, message: '请输入登录密码', trigger: 'blur' }],
			clearTypes: [{ type: 'array', required: true, message: '请至少选择一项', trigger: 'change' }]
		},
		formHandle: [
			{ label: '确认清除', type: 'danger', icon: 'el-icon-delete', handle: () => this.submitClearData() },
			{ label: '取消', icon: 'el-icon-circle-close', handle: () => this.elFormVisible() }
		]
	  }
	};
  },
  created() { },
  mounted() {
    this.getData();
  },
  methods: {
    async getData() {
      var res = await this.apis.user_list2(Object.assign({
        page: this.pagination.offset,
        limit: this.pagination.limit        
      }, this.searchData, {regTime: null}));
      var list = res.page.list || [];
	  list.map(res => {
	  	res.wtStateDesc = res.wtState == 0 ? '未签约' : res.wtState == 1 ? '已签约' : '已解约'
		res.faceStatusDesc = res.faceStatus == 0 ? '否' : '是'
		res.auditStatusDesc = res.auditStatus == 1 ? '正常' : '黑名单'
		res.auditDes = res.auditStatus == 1 ? '拉黑' : '解除黑名单'
	  })
      this.tableData = list;
      this.pagination.total = res.page.totalCount;
    },
    page() {
      this.getData();
    },
	openClearData(row) {
		this.formData = {
			userId: row.userId,
			nickname: row.nickname,
			phone: row.phone,
			password: '',
			clearTypes: []
		}
		this.formCols = this.clearDataForm.formCols
		this.formRules = this.clearDataForm.formRules
		this.formHandle = this.clearDataForm.formHandle
		this.elFormVisible('清除用户数据')
	},
	submitClearData() {
		this.$refs.elForm.$refs.ruleForm.validate(async (valid) => {
			if (!valid) return
			const labels = {
				memberCard: '会员卡',
				coupon: '优惠券',
				openDoor: '开关门记录',
				incomePayDetail: '购卡记录',
				cardOrder: '购卡订单',
				validityChange: '有效期变更记录',
				storeChange: '门店变更记录',
				delUserRecord: '会员注销记录'
			}
			const names = this.formData.clearTypes.map(k => labels[k] || k).join('、')
			this.$confirm(
				`确定清除用户【${this.formData.nickname || this.formData.phone}】的以下数据吗？\n${names}\n此操作不可恢复！`,
				'危险操作',
				{ confirmButtonText: '确定清除', cancelButtonText: '取消', type: 'warning' }
			).then(async () => {
				try {
					const res = await this.apis.user_clearAppUserData({
						userId: this.formData.userId,
						password: this.formData.password,
						clearTypes: this.formData.clearTypes
					})
					this.$message({ type: 'success', message: res.msg || '清除完成' })
					this.elFormVisible()
					this.getData()
				} catch (e) {
					// 业务错误由 httpRequest 拦截器统一 Message 提示（code=-99）
				}
			}).catch(() => {})
		})
	}
  },
};
</script>

<style scoped lang="scss">
</style>
