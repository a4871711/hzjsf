<template>
	<div>
		<r-search ref="search" :searchData="searchData" :searchForm="searchForm" :searchHandle="searchHandle" :inline="true" :searchRules="searchRules" />

		<r-table
			:isPagination="true"
			:isHandle="true"
			:tableData="tableData"
			:tableCols="tableCols"
			:tablePage="pagination"
			:loading="tableLoading"
			:tableHandles="tableHandles"
			:isSelection="false"
			@refresh="page"
		/>
	</div>
</template>

<script>
export default {
	data() {
		return {
			searchData: {
				nickname:'',
				phone: '',
				storeName: '',
				newStoreName: '',
				startTime:'',
				endTime:'',
				createdDate:[]
			},
			searchForm: [
				{ type: "input",label:'', placeholder: "姓名", prop: "nickname", width: 200 },
				{ type: "input",label:'', placeholder: "手机号码", prop: "phone", width: 200 },
				{ type: "input",label:'', placeholder: "开通门店", prop: "storeName", width: 200 },	
				{ type: "input",label:'', placeholder: "更换门店", prop: "newStoreName", width: 200 },				
				{ type: "daterange", placeholder: "操作时间", prop: "createdDate", width: 200, change:e=> {
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
				{ type: 'primary', label: '搜索', handle: e => this.getData() }
				// {type:'primary',label:'重置', handle: e => this.elFormSubmit(),}
			],
			searchRules: {
				
			},

			tableLoading: false,
			tableData: [],
			tableCols: [
				{ label: '姓名', prop: 'nickname' },
				{ label: '手机号码', prop: 'phone' },
				{ label: "头像", prop: "headImgUrl", type: 'image' },
				{ label: '开通门店', prop: 'storeName' },
				{ label: '更换门店', prop: 'newStoreName' },
				{ label: "可用门店", prop: "supportStoreName" },
				{ label: "会员类型", prop: "ctName" },
				{
					label: '签约状态',
					prop: 'wtStateDesc',			
				},
				{
					label: '自动续费',
					prop: 'autoPayDesc'
				},			
				{ label: '操作人', prop: 'username' },
				{ label: '操作时间', prop: 'changeDate', formatter: e => { return this.parseTime(e.changeDate) } }
			],
			tableHandles: [],
			pagination: { limit: 10, offset: 1, total: 1 },
			checkBox: []
		}
	},
	created() {},
	mounted() {
		this.getData()
	},
	methods: {
		async getData() {
			var res = await this.apis.changeRecordList(Object.assign({
				page: this.pagination.offset,
				limit: this.pagination.limit,				
			}, this.searchData, { createdDate : null}))
			var list = res.page.list || []
			list.map(res => {
				res.wtStateDesc = res.wtState == 0 ? '未签约' : res.wtState == 1 ? '已签约' : '已解约'
				res.autoPayDesc = res.autoPay == 0 ? '否' : '是'
			  })
			this.tableData = list
			this.pagination.total = res.page.totalCount
		},
		page() {
			this.getData()
		}
	}
}
</script>

<style scoped></style>
