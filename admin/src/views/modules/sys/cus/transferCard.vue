<template>
	<div>
    <div class="cus">
        <div class="cus_box">
          <div class="title"><span>转卡</span><i @click="$emit('transferCard_call')" class="el-icon-close"></i></div>
          <div class="content">
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
        </div>
		</div>
    </div>
</template>

<script>
    export default {
      components:{
	  
      },
      data(){
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
			//{ label: "备注", prop: "remark" },
			//{ label: "openId", prop: "openId" },
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
			  width: 260,
			  btnList: [
				{
				  label: "转卡",
				  type: "primary",
				  size: "mini",
				  icon: "el-icon-mobile",
				  handle: (row) => {
						this.$confirm('确定要转卡到该用户吗?', '提示', {
							confirmButtonText: '确定',
							cancelButtonText: '取消',
							type: 'warning'
						  }).then(async () => {
							var res = await this.apis.transferCard(
							  {
							  trUserId: this.res.data.userId,
							  taUserId: row.userId
							  }
							);
							
							this.$message({
							  type: 'success',
							  message: '转卡成功!'
							});
							this.$emit('transferCard_call',true);
						  }).catch(() => {

						  });
				  },
				  isShow: e => e.userId != this.res.data.userId
				}
			  ]
			},
		  ],
		  pagination: { limit: 10, offset: 1, total: 1 },
		  tableHandles: [],
		  formData: {},
		  formCols: [],
		  formHandle: [],
		  formRules: {}
      	}
	  },
      props:['res'],
      mounted(){

        this.getData();

      },
      methods:{	  	
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
			}
      }
    }
</script>

<style scoped lang="scss">

</style>
