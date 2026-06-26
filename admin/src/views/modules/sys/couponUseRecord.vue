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
      searchData: {
        phone:'',
		storeName: '',
		startTime:'',
		couponType:'',
		couponNew:'',
		endTime:'',
		regTime: []
      },
      searchForm: [
        { type: "input", placeholder: "手机号", prop: "phone", width: 200 },    
        { type: "input", placeholder: "使用门店", prop: "storeName", width: 200 },   
		
		{ type: "select", placeholder: "类型", prop: "couponType", width: 200,options: [
			{value: 0, 'label': '普通'},
			{value: 1, 'label': '抖音'},
			{value: 2, 'label': '美团'},
		]},
		{ type: "select", placeholder: "是否新人券", prop: "couponNew", width: 200, options:[
			{value: 1, label: '是'},
			{value: 0, label: '否'}
		] },
        { type: "daterange", placeholder: "使用时间", prop: "regTime", width: 200, change:e=> {
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
        { label: "ID", prop: "couponId" },
        { label: "手机号", prop: "phone" },
		{
			label: '类型',
			prop: 'typeDesc'
		},
		{ label: "兑券码", prop: "goodsId" },
		{
			label: '是否新人券',
			prop: 'couponNew',
			formatter: e => { return e.couponNew == 1 ? '是' : '否' }
		},
		{ label: "优惠金额", prop: "couponPrice" },
        { label: "使用门槛", prop: "limitPrice" },
        { label: "使用门店", prop: "storeName" },
		{ label: "使用时间", prop: "payTime", formatter: e => { return this.parseTime(e.payTime) } }
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
	  tableHandles: [
		{
          label: "导出",
          type: "primary",
          handle: e => {
			const data = { ...this.searchData, couponStatus: 1, used: 1 }
			delete data.regTime
			const parts = []
			for (const key in data) {
				const val = data[key]
				if (val !== '' && val !== null && val !== undefined) {
					parts.push(key + '=' + encodeURIComponent(val))
				}
			}
			window.open('/sys/user/exportCouponList?' + parts.join('&'))
		  }
        }
	  ],	  
	  formData: {},
	  formCols: [],
	  formHandle: [],
	  formRules: {},
	};
  },
  created() { },
  mounted() {
    this.getData();
  },
  methods: {
    async getData() {
      var res = await this.apis.couponList(Object.assign({
        page: this.pagination.offset,
        limit: this.pagination.limit,
		couponStatus: 1,
		used: 1
      }, this.searchData, {regTime: null}));
      var list = res.page.list || [];
	  let typeList = [
			{value: 0, 'label': '普通'},
			{value: 1, 'label': '抖音'},
			{value: 2, 'label': '美团'},
	  ];
	  list.map(res => {
		const typeItem = typeList.find(rres => rres.value == res.couponType)
		res.typeDesc = typeItem ? typeItem.label : '-'
	  })
      this.tableData = list;
      this.pagination.total = res.page.totalCount;
    },
    page() {
      this.getData();
    }
  },
};
</script>

<style scoped lang="scss">
</style>
