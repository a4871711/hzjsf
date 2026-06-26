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
			],
			searchRules: {
				
			},

			tableLoading: false,
			tableData: [],
			tableCols: [
				{ label: '姓名', prop: 'nickname' },
				{ label: '手机号码', prop: 'phone' },
				{ label: "头像", prop: "headImgUrl", type: 'image' },
				{ label: '所属门店', prop: 'storeName' },
				{ label: '操作人', prop: 'username' },
				{ label: '操作时间', prop: 'delTime', formatter: e => { return this.parseTime(e.delTime) } }
			],
			tableHandles: [
				{
					label: '导出',
					type: 'primary',
					handle: e => {
						const data = { ...this.searchData }
						delete data.createdDate
						const parts = []
						for (const key in data) {
							const val = data[key]
							if (val !== '' && val !== null && val !== undefined) {
								parts.push(key + '=' + encodeURIComponent(val))
							}
						}
						window.open('/sys/user/exportDelList?' + parts.join('&'))
					}
				}
			],
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
			var res = await this.apis.delUserRecordList(Object.assign({
				page: this.pagination.offset,
				limit: this.pagination.limit,
			}, this.searchData, { createdDate : null}))
			var list = res.page.list || []
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
