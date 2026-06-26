<template>
  <div>
    <r-search ref="search" :searchData="searchData" :searchForm="searchForm" :searchHandle="searchHandle" />
    <r-table
	  ref="myTable"
      :isSelection="true"
	  @select="tableSelect"
	  @selectAll="tableSelect"
      :isHandle="true"
      :isPagination="true"
      :tableData="tableData"
      :tableCols="tableCols"
      :tablePage="pagination"
      :loading="tableLoading"
      :tableHandles="tableHandles"
      @refresh="page()" />
    <r-form labelWidth="150px" :isHandle="true" :formRules="formRules" :formCols="formCols" :formHandle="formHandle" :formData="formData" ref="elForm" :inline="false" dialogWidth="700px" />


	<transferCard :res="transferCard" v-if="transferCard.show" v-on:transferCard_call="transferCard_call"></transferCard>
  </div>
</template>

<script>
import transferCard from "./cus/transferCard";
export default {
  components: {
    transferCard
  },
  data() {
    return {
		transferCard:{
              show:false,
              data:null
            },
      tableLoading: false,
	  selectList: [],
	  storeList: [],
      searchData: {
        phone:'',
        nickname:'',
		storeName: '',
        wtState:'',
		auditStatus: '',
		autoPay: '',
		status: '',
		remark:'',
		startTime:'',
		endTime:'',
		regTime: []
      },
      searchForm: [
        { type: "input", placeholder: "姓名", prop: "nickname", width: 200 },
        { type: "input", placeholder: "手机号", prop: "phone", width: 200 },
        { type: "input", placeholder: "开通门店", prop: "storeName", width: 200 },
        { type: "select", placeholder: "签约状态", prop: "wtState", width: 200,options: [
			{value: 0, 'label': '未签约'},
			{value: 1, 'label': '已签约'},
			{value: 2, 'label': '已解约'}
		]},
		{ type: "select", placeholder: "状态", prop: "auditStatus", width: 200,options: [
			{value: 1, 'label': '正常'},
			{value: 2, 'label': '黑名单'}
		]},
		{ type: "select", placeholder: "会员状态", prop: "status", width: 200,options: [
			//{value: 0, 'label': '待确认'},
			{value: 1, 'label': '生效中'},
			{value: 2, 'label': '已停用'},
			//{value: 3, 'label': '已转卡'},
			{value: 4, 'label': '已过期'},
			{value: 5, 'label': '已注销'}
		]},
		{ type: "select", placeholder: "自动续费", prop: "autoPay", width: 200,options: [
			{value: 1, 'label': '是'},
			{value: 0, 'label': '否'}
		]},
        //{ type: "input", placeholder: "备注", prop: "remark", width: 200 },
        { type: "daterange", placeholder: "到期时间", prop: "regTime", width: 200, change:e=> {
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
        { label: "开通门店", prop: "storeName" },
        { label: "可用门店", prop: "supportStoreName" },
        { label: "会员类型", prop: "ctName" },
        { label: "卡名称", prop: "cardName" },
		{
			label: '签约状态',
			prop: 'wtStateDesc',			
		},
		{
			label: '会员状态',
			prop: 'statusDesc'
		},
		{
			label: '自动续费',
			prop: 'autoPayDesc'
		},
		{
			label: '状态',
			prop: 'auditStatusDesc'
		},
		{ label: "到期时间", prop: "validityDate", formatter: e => { return this.parseTime(e.validityDate, '{y}-{m}-{d}') } },
		{ label: "购卡时间", prop: "buyTime", formatter: e => { return this.parseTime(e.buyTime) } },
        { label: "注册时间", prop: "createdDate", formatter: e => { return this.parseTime(e.createdDate) } },
        { label: "备注", prop: "remark" },
        {
          label: "操作",
          type: "button",
          width: 260,
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
				isShow: e => this.checkBtn('sys:user:auditStatus')
			},
			{    
			  des: 'statusDes',
              type: e => {
			  	return e.status != 2 ? 'danger' : "success"
			  },
              size: "mini",
              icon: "el-icon-lock",
              handle: (row) => {
			  	this.$confirm(row.status != 2 ? '确定要停用用户吗?' : '确定要启用用户吗', '提示', {
						confirmButtonText: '确定',
						cancelButtonText: '取消',
						type: 'warning'
					  }).then(async () => {
					  	if(row.status != 2){
							var res = await this.apis.updateMemberStatus(
							  [row.deviceId]
							);
						}else{
							var res = await this.apis.updateMemberStart(
							  [row.deviceId]
							);
						}
						this.$message({
						  type: 'success',
						  message: '操作成功!'
						});
						this.getData();
					}).catch(() => {

					  });
            	},
				isShow: e => e.deviceId > 0 && this.checkBtn('sys:user:updateMemberStatus')
			},			
            /*{
              label: "解绑微信",
              type: "primary",
              size: "mini",
              icon: "el-icon-mobile",
              handle: (row) => {
			  		this.$confirm('确定要解绑微信吗?', '提示', {
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
			  isShow: e => !!e.phone && this.checkBtn('')
            },*/
			{
              label: "解绑续费",
              type: "primary",
              size: "mini",
              icon: "el-icon-mobile",
              handle: (row) => {
			  		this.$confirm('确定要解绑续费吗?', '提示', {
						confirmButtonText: '确定',
						cancelButtonText: '取消',
						type: 'warning'
					  }).then(async () => {
						var res = await this.apis.user_del_contract(
						  {
						  contractId: row.contractId,
						  userId: row.userId
						  }
						);
						this.$message({
						  type: 'success',
						  message: '解除成功!'
						});
						this.getData();
					  }).catch(() => {

					  });
			  },
			  isShow: e => e.wtState == 1 && this.checkBtn('sys:user:delContract')
            },
			{
              label: "备注",
              type: "success",
              size: "mini",
              icon: "el-icon-edit",
              handle: (row) => {
			  	this.formData = this.formRemark.formData
			  	this.formCols = this.formRemark.formCols
			  	this.formHandle = this.formRemark.formHandle
			  	this.formRules = this.formRemark.formRules
				this.elFormDetail(row, '编辑备注')
			  },
			  isShow: e => this.checkBtn('sys:user:remark')
            },
			/*{
              label: "转卡",
              type: "success",
              size: "mini",
              icon: "el-icon-edit",
              handle: (row) => {
			  	this.transferCard.data = row
				this.transferCard.show = true
			  },
			  isShow: e => e.deviceId > 0 && this.checkBtn('')
            },*/
			{
              label: "更新有效期",
              type: "primary",
              size: "mini",
              icon: "el-icon-edit",
              handle: (row) => {
			    this.$refs.myTable.clearSelection();
			  	this.selectList = []
			  	this.formData = this.formValidity.formData
			  	this.formCols = this.formValidity.formCols
			  	this.formHandle = this.formValidity.formHandle
			  	this.formRules = this.formValidity.formRules
				this.elFormDetail(row, '更新有效期')
			  },
			  isShow: e => e.deviceId > 0 && this.checkBtn('sys:user:updateMemberValidity')
            },
			{
              label: "更改开通门店",
              type: "success",
              size: "mini",
              icon: "el-icon-edit",
              handle: (row) => {
			    this.$refs.myTable.clearSelection();
			  	this.selectList = []
				
				let list = []
				if(row.supportStoreName){
					let temp = row.supportStoreName.split(',')
					this.storeList.map(res => {
						if(temp.includes(res.label))list.push(res)
					})
				}else{
					list = this.storeList
				}
				this.formChange.formCols[this.labIndex(this.formChange.formCols, '更改开通门店')].options = list;
				
			  	this.formData = this.formChange.formData
			  	this.formCols = this.formChange.formCols
			  	this.formHandle = this.formChange.formHandle
			  	this.formRules = this.formChange.formRules
				this.elFormDetail(row, '更改开通门店')
			  },
			  isShow: e => e.deviceId > 0 && this.checkBtn('sys:user:updateMemberChange')
            },
			{
              label: "注销",
              type: "danger",
              size: "mini",
              icon: "el-icon-delete",
              handle: (row) => {
			  	this.$confirm('确定要注销用户吗?', '提示', {
						confirmButtonText: '确定',
						cancelButtonText: '取消',
						type: 'warning'
					  }).then(async () => {
					  	var res = await this.apis.cancelMemberCard(
							 [row.deviceId]
						);
						this.$message({
						  type: 'success',
						  message: '操作成功!'
						});
						this.getData();
					}).catch(() => {

					  });
			  },
			  isShow: e => this.checkBtn('sys:user:cancelMemberCard')
			  //isShow: e => e.deviceId > 0 && (e.status == 0 || e.status == 1)
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
			const data = { ...this.searchData }
			delete data.regTime
			const parts = []
			for (const key in data) {
				const val = data[key]
				if (val !== '' && val !== null && val !== undefined) {
					parts.push(key + '=' + encodeURIComponent(val))
				}
			}
			window.open('/sys/user/exportData?' + parts.join('&'))
		  }
        },
	  	{
          label: "更新有效期",
          type: "primary",
          handle: e => { 
		  	if(this.selectList.length == 0){
				this.$message({
				  type: 'error',
				  message: '请选择!'
				});
				return
			}
			let numCount = ''
			this.selectList.map(res => {
				if(res.deviceId){
					numCount ++
				}
			})
			if(numCount <= 0){
				this.$message({
				  type: 'error',
				  message: '选择的会员不可更改!'
				});
				return
			}
			
			this.formData = this.formValidity.formData
			this.formCols = this.formValidity.formCols
			this.formHandle = this.formValidity.formHandle
			this.formRules = this.formValidity.formRules
			this.elFormDetail(this.selectList[0], '更新有效期')
		  } ,
		  isShow: e => this.checkBtn('sys:user:updateMemberValidity')
        },
		/*{
          label: "更改开通门店",
          type: "success",
          handle: e => { 
		  	if(this.selectList.length == 0){
				this.$message({
				  type: 'error',
				  message: '请选择!'
				});
				return
			}
			let numCount = ''
			this.selectList.map(res => {
				if(res.deviceId){
					numCount ++
				}
			})
			if(numCount <= 0){
				this.$message({
				  type: 'error',
				  message: '选择的会员不可更改!'
				});
				return
			}
			
			this.formData = this.formChange.formData
			this.formCols = this.formChange.formCols
			this.formHandle = this.formChange.formHandle
			this.formRules = this.formChange.formRules
			this.elFormDetail(this.selectList[0], '更改开通门店')
		  } ,
		  isShow: e => this.checkBtn('sys:user:updateMemberChange')
        }*/
	  ],
	  
	  formData: {},
	  formCols: [],
	  formHandle: [],
	  formRules: {},
	  
	  formRemark: {
	  	  formData: {
			userId: '',
			remark: ''
		  },
		  formCols: [
			{ type: "input", label: "备注", width: 350, prop: "remark" },
		  ],
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
		  formRules: {
			remark: [
			  { required: true, message: '请输入', trigger: 'blur' },
			],
		  }
	  },
	  formValidity: {
	  	  formData: {
			deviceId: '',
			newValidityDate: '',
			userId: '',
			validityDate: '',
			newValidityType: '',
			newValidityDay: ''
		  },
		  formCols: [
		  	{ type: "radio", label: "修改方式", width: 350, prop: "newValidityType", radios: [
				{value: 1, label: '日期'},
				{value: 0, label: '天数'}
			] },
			{ type: "input", label: "增加天数", width: 350, prop: "newValidityDay" },
			{ type: "date", label: "有效期", width: 350, prop: "newValidityDate" },
		  ],
		  formHandle: [
			{
			  label: "确认",
			  type: "primary",
			  icon: "el-icon-circle-plus-outline",
			  handle: e => this.elFormSubmitValidity(),
			},
			{
			  label: "取消",
			  icon: "el-icon-circle-close",
			  handle: e => this.elFormVisible(),
			},
		  ],
		  formRules: {
			newValidityDate: [
			  { required: true, message: '请输入', trigger: 'blur' },
			],
		  }
	  },
	  formChange: {
	  	  formData: {
			deviceId: '',
			phone: '',
			userId: '',
			storeName: '',
			storeId: '',
			storeAddrId: '',
			newStoreAddrId: '',
		  },
		  formCols: [
		  	{ type: "input", label: "会员手机号", width: 350, prop: "phone", isDisabled: e => {
				return true;
			}},
			{ type: "input", label: "开通门店", width: 350, prop: "storeName", isDisabled: e => {
				return true;
			} },
			{ type: "select", label: "更改开通门店", width: 350, prop: "storeAddrId", change: (value, e) => {
				let temp = e.options.find(item => item.storeAddrId == value)
				this.formData.storeId = temp ? temp.storeId : ''
				this.formData.newStoreAddrId = temp ? temp.storeAddrId : ''
				console.log(temp, this.formData)
			} },
		  ],
		  formHandle: [
			{
			  label: "确认",
			  type: "primary",
			  icon: "el-icon-circle-plus-outline",
			  handle: e => this.elFormSubmitChange(),
			},
			{
			  label: "取消",
			  icon: "el-icon-circle-close",
			  handle: e => this.elFormVisible(),
			},
		  ],
		  formRules: {
			
		  }
	  }
	};
  },
  created() { },
  mounted() {
    this.getData();
	this.getStoreList()
  },
  methods: {
    async getData() {
      var res = await this.apis.user_list3(Object.assign({
        page: this.pagination.offset,
        limit: this.pagination.limit        
      }, this.searchData, {regTime: null}));
      var list = res.page.list || [];
	  let statusList = [
			{value: 0, 'label': '待确认'},
			{value: 1, 'label': '生效中'},
			{value: 2, 'label': '已停用'},
			{value: 3, 'label': '已转卡'},
			{value: 4, 'label': '已过期'},
			{value: 5, 'label': '已注销'}
	  ];
	  list.map(res => {
	  	res.wtStateDesc = res.wtState == 0 ? '未签约' : res.wtState == 1 ? '已签约' : '已解约'
		res.statusDesc = statusList.filter(rres => { return rres.value == res.status })[0]['label']
		res.autoPayDesc = res.autoPay == 0 ? '否' : '是'
		res.auditStatusDesc = res.auditStatus == 1 ? '正常' : '黑名单'
		res.auditDes = res.auditStatus == 1 ? '拉黑' : '解除黑名单'
		res.statusDes = res.status == 2 ? '启用' : '停用'
	  })
      this.tableData = list;
      this.pagination.total = res.page.totalCount;
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
		  this.storeList = list
      
	},
    page() {
      this.getData();
    },
	async elFormDetail(row, tit) {

      Object.keys(this.formData).forEach((key) => {
		this.formData[key] = row[key];
	  });
	  this.formData.newValidityDate = row.validityDate
	  this.formData.newValidityType = 0

      this.elFormVisible(tit)
    },
    elFormSubmit() {
      this.$refs.elForm.$refs.ruleForm.validate(async(valid) => {
        if (valid) {
          if (this.formData.userId) {
			var res = await this.apis.user_update2(this.formData);
		  }
		  this.elFormVisible();

		  this.getData();
        }
      })
    },
    elFormSubmitValidity() {
	  if(this.formData.newValidityType == 0){
	  	let dateString = this.formData.validityDate;
		let daysToAdd = this.formData.newValidityDay;
		if(!daysToAdd || daysToAdd == 0){
			this.$message({
			  type: 'error',
			  message: '请填写天数!'
			});
			return;
		}
		let date = new Date(dateString);
		date.setDate(date.getDate() + daysToAdd * 1);
		let newDateString = date.toISOString().split('T')[0];
	  	this.formData.newValidityDate = newDateString
	  }	
      this.$refs.elForm.$refs.ruleForm.validate(async(valid) => {
        if (valid) {
          if (this.formData.userId) {
		  	if(this.selectList.length > 0){
		  		this.selectList.map(async item => {
					this.formData.userId = item.userId
					await this.apis.updateMemberValidity(this.formData);
				})
		  	}else{
				await this.apis.updateMemberValidity(this.formData);
			}
		  }
		  this.elFormVisible();
		  
		  this.$refs.myTable.clearSelection();
		  this.selectList = []

		  setTimeout(() => { this.getData(); }, 300)
        }
      })
    },
    elFormSubmitChange() {
	  if(this.formData.newStoreAddrId <= 0){	  	
		this.$message({
		  type: 'error',
		  message: '请选择更改门店!'
		});
		return
	  }	
      this.$refs.elForm.$refs.ruleForm.validate(async(valid) => {
        if (valid) {
          if (this.formData.userId) {
		  	if(this.selectList.length > 0){
		  		this.selectList.map(async item => {
					this.formData.userId = item.userId
					await this.apis.updateMemberChange(this.formData);
				})
		  	}else{
				await this.apis.updateMemberChange(this.formData);
			}
		  }
		  console.log(this.formData)
		  this.elFormVisible();
		  
		  this.$refs.myTable.clearSelection();
		  this.selectList = []

		  setTimeout(() => { this.getData(); }, 300)
        }
      })
    },
	  transferCard_call(res){
		this.transferCard.show=false;
		if(res){
		  this.getData();
		}
	  },
	tableSelect(e){
		console.log(e)
		this.selectList = e
	}
  },
};
</script>

<style scoped lang="scss">
</style>
