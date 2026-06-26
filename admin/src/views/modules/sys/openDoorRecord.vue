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
				phone:'',
				device_store_name: '',
				store_name: '',
				startTime:'',
				endTime:'',
				createTime:[]
			},
			searchForm: [
				{ type: "input",label:'', placeholder: "手机号", prop: "phone", width: 200 },
				{ type: "input",label:'', placeholder: "会员门店", prop: "device_store_name", width: 200 },
				{ type: "input",label:'', placeholder: "开门门店", prop: "store_name", width: 200 },
				/*{
					type: 'select',
					label: '',
					placeholder: '开门结果',
					prop: 'result',
					width: 200,
					options: [
						{ value: 0, label: '失败' },
						{ value: 1, label: '成功' }
					]
				}*/
				{ type: "daterange", placeholder: "开门时间", prop: "createTime", width: 200, change:e=> {
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
				{ label: '手机号', prop: 'phone' },
				{ label: '会员门店', prop: 'deviceStoreName' },
				{ label: '开门门店', prop: 'storeName' },
				{ label: '距离/米', prop: 'distance' },
				{ label: '开门结果', prop: 'result', formatter: e => {return e.result == 1 ? '成功' : '失败'} },
				{ label: '失败原因', prop: 'remark' },
				{ label: '开门时间', prop: 'createTime', formatter: e => { return this.parseTime(e.createTime) } }
			],
			tableHandles: [
				{
					label: "导出",
					type: "primary",
					handle: e => {
						const data = { ...this.searchData }
						delete data.createTime
						const parts = []
						for (const key in data) {
							const val = data[key]
							if (val !== '' && val !== null && val !== undefined) {
								parts.push(key + '=' + encodeURIComponent(val))
							}
						}
						window.open('/sys/store/exportOpenDoorRecord?' + parts.join('&'))
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
			var res = await this.apis.openDoorRecordlist({
				page: this.pagination.offset,
				limit: this.pagination.limit,
				phone: this.searchData.phone,
				device_store_name: this.searchData.device_store_name,
				store_name:this.searchData.store_name,
				startTime:this.searchData.startTime,
				endTime:this.searchData.endTime,
			})
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
