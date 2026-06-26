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
				startTime:'',
				endTime:'',
				createdDate:[]
			},
			searchForm: [
				{ type: "input",label:'', placeholder: "姓名", prop: "nickname", width: 200 },
				{ type: "input",label:'', placeholder: "手机号码", prop: "phone", width: 200 },
				{ type: "input",label:'', placeholder: "所属门店", prop: "storeName", width: 200 },				
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
				{ label: '所属门店', prop: 'storeName' },
				{ label: '操作', prop: 'operType' },
				{ label: '有效期(旧)', prop: 'oldValidityDate'},
				{ label: '有效期(新)', prop: 'newValidityDate'},
				{ label: '操作人', prop: 'username' },
				{ label: '操作时间', prop: 'createdDate', formatter: e => { return this.parseTime(e.createdDate) } }
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
			var res = await this.apis.validityRecordList(Object.assign({
				page: this.pagination.offset,
				limit: this.pagination.limit,				
			}, this.searchData, { createdDate : null}))
			var list = res.page.list || []
			list.map(item => {
				item.operType = '后台更改'
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
