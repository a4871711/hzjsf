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
				storeName:''
			  },
			  searchForm: [
				{ type: "input", placeholder: "门店名称", prop: "storeName", width: 200 },
			  ],
			searchHandle: [
				{ type: 'primary', label: '搜索', handle: e => this.getData() }
				// {type:'primary',label:'重置', handle: e => this.elFormSubmit(),}
			],
			searchRules: {
				//
			},

			tableLoading: false,
			tableData: [],
			tableCols: [
				{ label: '门店名称', prop: 'storeName' },
				{ label: '门店地址', prop: 'storeAddr' },
				{ label: '本月新增会员数', prop: 'userMonth' },
				{ label: '自动续费统计', prop: 'payUserMonth' },
				{ label: '今日办卡会员数', prop: 'cardToday' },
				{ label: '本月即将过期会员', prop: 'monthCount' },
				{ label: '次日即将过期会员', prop: 'nextCount' },
				{ label: '总过期会员数', prop: 'expireCount' },
				{ label: '在籍会员数', prop: 'validityCount' },
				{ label: '今日收入', prop: 'moneyToday' },
				{ label: '本月收入', prop: 'moneyMonth' },
				{ label: '累计收入', prop: 'moneyTotal' },
				{ label: '合伙人', prop: 'agent5Name' },
				{ label: '门店管理员', prop: 'agent6Name' }
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
			var res = await this.apis.store_list({
				page: this.pagination.offset,
				limit: this.pagination.limit,
				storeName:this.searchData.storeName,
				stats: 1
			})
			var list = res.page.list || []
			list.map(res => {
				var temp = []
				if(res.sysUser5 && res.sysUser5.length > 0){
					res.sysUser5.map(rres => {
						temp.push(rres.username)
					})
				}
				res.agent5Name = temp.join(', ');

				var temp = []
				if(res.sysUser6 && res.sysUser6.length > 0){
					res.sysUser6.map(rres => {
						temp.push(rres.username)
					})
				}
				res.agent6Name = temp.join(', ');				
				
				res = Object.assign(res, res.stats || {});
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
