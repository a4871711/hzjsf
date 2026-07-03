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

  </div>
</template>

<script>
export default {
  components: {
    
  },
  data() {
    var checkPhone = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('手机号不能为空'));
      } else {
        const reg = /^1[3|4|5|7|8][0-9]\d{8}$/
        if (reg.test(value)) {
          callback();
        } else {
          return callback(new Error('请输入正确的手机号'));
        }
      }
    };
    return {
      tableLoading: false,
      searchData: {
	  	cardName:'',
        status:''
      },
      searchForm: [
	  	{ type: "input", placeholder: "卡名称", prop: "cardName", width: 200 },
        { type: "select", placeholder: "状态", prop: "status", width: 200, options:[
			{value: 1, label: '上架'},
			{value: 2, label: '下架'}
		] },
      ],
      searchHandle: [
        {
          label: "搜索",
          type: "primary",
          handle: e => this.getData()
        },
      ],
      tableHandles: [
        {
          label: "添加",
          type: "primary",
          handle: e => {this.elFormVisible()} ,
		  isShow: e => this.checkBtn('sys:fitcard:save')
        }
      ],
      tableData: [],
      tableCols: [
        { label: "ID", prop: "fitCardId" },
        { label: "卡片类型", prop: "ctName" },
        { label: "卡名称", prop: "cardName" },
        { label: "单价", prop: "cardPrice" },
        { label: "新人价", prop: "newUserPrice" },
        { label: "原价", prop: "costPrice" },
        { label: "权益卡价格", prop: "benefitPrice" },
        { label: "有效天数", prop: "validity" },
        { label: "次卡次数", prop: "useCount" },
        { label: "自动续费", prop: "autoPay", formatter: e => { return e.autoPay > 0 ? '是' : '否' } },
        { label: "卡片备注", prop: "cardRule" },
		{
			label: '状态',
			type: "switch",
			prop: 'status',
			values: [1, 2],
			change: (row) => this.changeStatus(row),
			isDisabled: e => !this.checkBtn('sys:fitcard:status')
		},
        { label: "创建时间", prop: 'createdDate', formatter: e => { return this.parseTime(e.createdDate) } },		
        {
          label: "操作",
          type: "button",
          width: 260,
          btnList: [
            {
              label: "编辑",
              type: "success",
              size: "mini",
              icon: "el-icon-edit",
              handle: (row) => this.elFormDetail(row),
			  isShow: e => this.checkBtn('sys:fitcard:update')
            },
            {
              label: "删除",
              type: "danger",
              size: "mini",
              icon: "el-icon-delete",
              handle: (row) => this.del(row),
			  isShow: e => this.checkBtn('sys:fitcard:delete')
            }
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      formData: {
        fitCardId: '',
		cardName: '',
        cardType: "",
		cardPrice: '',
		newUserPrice: '',
		costPrice: '',
		benefitPrice: '',
		validity: '',
		useCount: '',
		autoPay: 0,
		storeAddrIds: [],
		showStoreAddrIds: [],
		nextPriceTitle: '',
		nextPrice: '',
		nextPriceTitle2: '',
		nextPrice2: '',
		nextPriceTitle3: '',
		nextPrice3: '',
		cardRule: '',
		status: 1
      },
      formCols: [
	  	{ type: "select", label: "卡片类型", width: 350, prop: "cardType", options: [
			{value: 0, label: '月卡'},
			{value: 1, label: '季卡'},
			{value: 2, label: '半年卡'},
			{value: 3, label: '年卡'},
			{value: 10, label: '次卡'}
		] },
        { type: "input", label: "卡名称", width: 350, prop: "cardName" },
        { type: "input", label: "单价", width: 350, prop: "cardPrice" },
        { type: "input", label: "新人价", width: 350, prop: "newUserPrice" },
        { type: "input", label: "原价", width: 350, prop: "costPrice" },
        { type: "input", label: "权益卡价格", placeholder:'权益会员专享价，留空则不启用', width: 350, prop: "benefitPrice" },	
        { type: "input", label: "有效天数", width: 350, prop: "validity" },
        { type: "input", label: "次卡次数", width: 350, prop: "useCount" },
        { type: "radio", label: "自动续费", width: 350, prop: "autoPay", radios: [
			{value: 0, label: '否'},
			{value: 1, label: '一级'},
			{value: 2, label: '二级'},
			{value: 3, label: '三级'}
		]  },
		{ type: "select", label: "可用门店", width: 350, prop: "storeAddrIds", multiple: true },
		{ type: "select", label: "可见门店", width: 350, prop: "showStoreAddrIds", multiple: true },	
        { type: "input", label: "一级起月份", placeholder:'英文逗号分隔，如：1,2,3', width: 350, prop: "nextPriceTitle" },
        { type: "input", label: "一级起价格", width: 350, prop: "nextPrice" },		
        { type: "input", label: "二级起月份", placeholder:'英文逗号分隔，如：4,5,6', width: 350, prop: "nextPriceTitle2" },
        { type: "input", label: "二级起价格", width: 350, prop: "nextPrice2" },		
        { type: "input", label: "三级起月份", placeholder:'英文逗号分隔，如：7,8,9', width: 350, prop: "nextPriceTitle3" },
        { type: "input", label: "三级起价格", width: 350, prop: "nextPrice3" },	
        { type: "input", label: "会员规则", width: 350, prop: "cardRule" },
        { type: "radio", label: "状态", width: 350, prop: "status", radios: [
			{value: 1, label: '上架'},
			{value: 2, label: '下架'}
		] }
      ],
      formRules: {
        cardType: [
          { required: true, message: '请输入', trigger: 'blur' },
        ],
        cardPrice: [
          { required: false, message: '请输入', trigger: 'blur' },
        ],
        validity: [
          { required: true, message: '请输入', trigger: 'blur' },
        ],
        useCount: [
          { required: true, message: '请输入', trigger: 'blur' },
        ],
      },
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
    };
  },
  created() { },
  mounted() {
    this.getData();
	this.getStoreList()
  },
  methods: {
    del(row) {
      this.$confirm('确定要删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        var res = await this.apis.fitcard_delete(
          [row.fitCardId]
        );
        this.$message({
          type: 'success',
          message: '删除成功!'
        });
        this.getData();
      }).catch(() => {

      });
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


      this.formCols[this.labIndex(this.formCols, '可用门店')].options = list;
      this.formCols[this.labIndex(this.formCols, '可见门店')].options = list;
	},
    async getData() {
      var res = await this.apis.fitcard_list({
        page: this.pagination.offset,
        limit: this.pagination.limit,
		status: this.searchData.status
      });
      var list = res.pages.list || [];
	  list.map(res => {
	  	res.storeAddrIds = res.storeAddrIds ? res.storeAddrIds.split(',') : []
		res.storeAddrIds = res.storeAddrIds.map(rres => {
			return rres * 1
		})
		res.showStoreAddrIds = res.showStoreAddrIds ? res.showStoreAddrIds.split(',') : []
		res.showStoreAddrIds = res.showStoreAddrIds.map(rres => {
			return rres * 1
		})
	  })
	  console.log(list)
      this.tableData = list;
      this.pagination.total = res.pages.totalCount;
    },
    page() {
      this.getData();
    },
    createdAcc() {
      this.elFormVisible();
    },
    async elFormDetail(row) {

      Object.keys(this.formData).forEach((key) => {
		this.formData[key] = row[key];
	  });

      this.elFormVisible(this.formData.fitCardId ? '编辑' : '新增')
    },
    elFormSubmit() {
      this.$refs.elForm.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.submit();
        }
      })
    },
    async submit() {
	  let formData = Object.assign({}, this.formData)
	  formData.nextPriceTitle = formData.nextPriceTitle.replace(/，/ig, ',')
	  formData.nextPriceTitle2 = formData.nextPriceTitle2.replace(/，/ig, ',')
	  formData.nextPriceTitle3 = formData.nextPriceTitle3.replace(/，/ig, ',')
	  formData.storeAddrIds = formData.storeAddrIds.join(',')
	  formData.showStoreAddrIds = formData.showStoreAddrIds.join(',')
      if (!formData.fitCardId) {
        var res = await this.apis.fitcard_save(formData);
      } else {
        var res = await this.apis.fitcard_update(formData);
      }
      this.elFormVisible();

      this.getData();

    },
	async changeStatus(row) {
		row.storeAddrIds = row.storeAddrIds.join(',')
		row.showStoreAddrIds = row.showStoreAddrIds.join(',')
		var res = await this.apis.fitcard_update(row);
		//this.getData();
	}
  },
};
</script>

<style scoped lang="scss">
</style>
