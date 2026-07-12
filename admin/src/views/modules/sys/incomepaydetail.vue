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
				userName:'',
				phone: '',
				storeName: '',
				payType: '',
				cardNature: '',
				sdate:'',
				edate:'',
				createdDate:[]
			},
			searchForm: [
				{ type: "input",label:'', placeholder: "姓名", prop: "userName", width: 200 },
				{ type: "input",label:'', placeholder: "手机号", prop: "phone", width: 200 },
				{ type: "input",label:'', placeholder: "开通门店", prop: "storeName", width: 200 },
				{
					type: 'select',
					label: '',
					placeholder: '支付方式',
					prop: 'payType',
					width: 200,
					options: [
						{ value: '2', label: '微信支付' },
						{ value: '13', label: '自动续费' }
					]
				},
				{
					type: 'select',
					label: '',
					placeholder: '卡片分类',
					prop: 'cardNature',
					width: 200,
					options: [
						{ value: '0', label: '普通卡' },
						{ value: '1', label: '权益卡' }
					]
				},
				{ type: "daterange", placeholder: "注册时间", prop: "createdDate", width: 200, change:e=> {
					console.log(e)
					if(e){
						this.searchData.sdate = e[0] + ' 00:00:00'			
						this.searchData.edate = e[1] + ' 23:59:59'
					}else{
						this.searchData.sdate = this.searchData.edate = ''
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
				{ label: '姓名', prop: 'userName' },
				{ label: '手机号码', prop: 'phone' },
				{ label: "头像", prop: "headImgUrl", type: 'image' },
				{ label: '所属门店', prop: 'storeName' },
				{ label: '会员类型', prop: 'ctName' },
				{ label: '卡片分类', prop: 'cardNature', formatter: e => { return e.cardNature == 1 ? '权益卡' : '普通卡' } },
				{
					label: '是否自动续费',
					prop: 'renewSourceDesc',
					type: 'html',
					html: e => {
						const map = {
							'自动': 'success',
							'用户': 'info',
							'后台': 'warning'
						}
						const type = map[e.renewSourceDesc] || 'info'
						return e.renewSourceDesc ? `<span class="el-tag el-tag--${type} el-tag--small">${e.renewSourceDesc}</span>` : '--'
					}
				},
				{ label: '支付方式', prop: 'payTypeDesc', type:'html', html: e => { return e.payTypeDesc } },
				{ label: '订单金额', prop: 'paySum' },
				//{ label: '卡券', prop: 'couponName' },
				{ label: '卡券金额', prop: 'couponMoney'},
				{ label: '支付金额', prop: 'money'},
				{ label: '有效期(旧)', prop: 'oldValidityDate', formatter: e => { return this.parseTime(e.oldValidityDate, '{y}-{m}-{d}') }},
				{ label: '有效期(新)', prop: 'validityDate', formatter: e => { return this.parseTime(e.validityDate, '{y}-{m}-{d}') }},
				{ label: '购卡时间', prop: 'transactionTime', formatter: e => { return this.parseTime(e.transactionTime) } }
			],
			tableHandles: [
				{
				  label: "导出数据",
				  type: "primary",
				  handle: e => {
				  	window.open('/sys/incomepaydetail/export?page=1&limit=999999999&' + new URLSearchParams(this.searchData).toString())
				  } ,
				  isShow: e => this.checkBtn('sys:incomepaydetail:export')
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
			var res = await this.apis.incomepaydetail(Object.assign({
				page: this.pagination.offset,
				limit: this.pagination.limit,				
			}, this.searchData, {createdDate: null}))
			var list = res.page.list || []
			list.map(item => {
				item.renewSourceDesc = item.renewSourceDesc || this.resolveRenewSourceDesc(item)
				item.payTypeDesc = this.resolvePayTypeDesc(item)
				item.ctName = item.cardType == 0 ? '月卡' : item.cardType == 1 ? '季卡' : item.cardType == 2 ? '半年卡' : item.cardType == 3 ? '年卡' : '次卡'
				item.couponMoney = item.paySum ? (item.paySum - item.money).toFixed(2) * 1 : 0
			})
			this.tableData = list
			this.pagination.total = res.page.totalCount
		},
		page() {
			this.getData()
		},
		resolveRenewSourceDesc(item) {
			if (item.payType == 13 || item.tradeType == 13) {
				return '自动'
			}
			if ([1, 2, 3].includes(Number(item.tradeType))) {
				return '用户'
			}
			if (item.payType == 2) {
				return '用户'
			}
			return '后台'
		},
		resolvePayTypeDesc(item) {
			const source = item.renewSourceDesc || this.resolveRenewSourceDesc(item)
			let label = '后台续费'
			if (source === '自动') {
				label = '自动续费'
			} else if (source === '用户') {
				if (item.tradeType == 3) {
					label = '支付宝'
				} else if (item.tradeType == 1) {
					label = '钱包支付'
				} else {
					label = '微信支付'
				}
			}
			const count = item.buyCount == null || item.buyCount === '' ? 0 : item.buyCount
			return label + '<br>第' + count + '次'
		}
	}
}
</script>

<style scoped></style>
