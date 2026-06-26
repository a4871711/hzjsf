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
				operName:'',
				// businessType: '',
				// params: ''
			},
			searchForm: [
				{ type: "input",label:'', placeholder: "用户名、用户操作", prop: "operName", width: 200 },
				/*{
					type: 'select',
					label: '',
					placeholder: '操作类型',
					prop: 'businessType',
					width: 200,
					options: [
						{ value: '0', label: '其它' },
						{ value: '1', label: '新增' },
						{ value: '2', label: '修改' },
						{ value: '3', label: '删除' },
						{ value: '4', label: '授权' },
						{ value: '5', label: '导出' },
						{ value: '6', label: '导入' },
						{ value: '7', label: '强退' },
						{ value: '8', label: '生成代码' },
						{ value: '9', label: '清空数据' }
					]
				}*/
				// { type: "date",label:'', placeholder: "时间范围", prop: "params", width: 200 },
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
				{ label: '操作类型', prop: 'operation' },
				{ label: '操作人员', prop: 'username' },
				{ label: '请求方法', prop: 'method' },
				{ label: '请求参数', prop: 'params' },
				{ label: '执行时长/毫秒', prop: 'time' },
				{ label: '登录次数', prop: 'loginCount' },
				{ label: 'IP地址', prop: 'ip' },
				{ label: '日志时间', prop: 'createDate', formatter: e => { return this.parseTime(e.createDate) } }
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
			var res = await this.apis.operlog_list({
				page: this.pagination.offset,
				limit: this.pagination.limit,
				key: this.searchData.operName,
				// businessType: this.searchData.businessType
				// params:this.searchData.params,
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
