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
        coachName:'',
        phone:'',
        status:'',
		storeName:'',
      },
      searchForm: [
        { type: "input", placeholder: "姓名", prop: "coachName", width: 200 },
        { type: "input", placeholder: "手机", prop: "phone", width: 200 },
        { type: "input", placeholder: "所属门店", prop: "storeName", width: 200 },
        { type: "select", placeholder: "状态", prop: "status", width: 200, options:[
			{value: 1, label: '正常'},
			{value: 2, label: '禁用'}
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
          label: "添加教练",
          type: "primary",
          handle: e => {this.elFormVisible()} ,
		  isShow: e => this.checkBtn('sys:storecoach:save')
        }
      ],
      tableData: [],
      tableCols: [
        { label: "ID", prop: "scId" },
        { label: "头像", prop: "headImgUrl", type: 'image' },
        { label: "姓名", prop: "coachName" },
        { label: "手机", prop: "phone" },
        { label: "所属门店", prop: "storeName" },
		{
			label: '状态',
			type: "switch",
			prop: 'status',
			values: [1, 2],
			change: (row) => this.changeStatus(row),
			isDisabled: e => !this.checkBtn('sys:storecoach:status')
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
			  isShow: e => this.checkBtn('sys:storecoach:infos,sys:storecoach:updates')
            },
            {
              label: "删除",
              type: "danger",
              size: "mini",
              icon: "el-icon-delete",
              handle: (row) => this.del(row),
			  isShow: e => this.checkBtn('sys:storecoach:delete')
            }
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      formData: {
	    scId: '',
		storeId: [],
        coachName: '',
        phone: "",
		status: 1,
		headImgUrl: '',
      },
      formCols: [
        { type: "select", label: "所属门店", width: 350, prop: "storeId", multiple: true, options: [] },	
        { type: "input", label: "姓名", width: 350, prop: "coachName" },
        { type: "input", label: "手机", width: 350, prop: "phone" },	
		{ type: "upload", label: "头像", placeholder:'宽高：702*1146，格式：png, jpg, jpeg', width: 350, prop: "headImgUrl", success: (res, file) => {
						console.log(res)
						this.$refs.elForm.loading = false
						this.formData.headImgUrl = res.path[0]
					} },
        { type: "radio", label: "状态", width: 350, prop: "status", radios: [
			{value: 1, label: '正常'},
			{value: 2, label: '禁用'}
		] }
      ],
      formRules: {
        storeId: [
          { required: true, message: '请输入', trigger: 'blur' },
        ],
        coachName: [
          { required: false, message: '请输入', trigger: 'blur' },
        ],
        phone: [
          { required: true, message: '请输入', trigger: 'blur' },
        ],
        headImgUrl: [
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
        var res = await this.apis.storecoach_delete(
          [row.scId]
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
				item.value = item.storeId
				item.label = item.storeName
			})


      this.formCols[this.labIndex(this.formCols, '所属门店')].options = list;
	},
    async getData() {
      var res = await this.apis.storecoach_list(Object.assign({
        page: this.pagination.offset,
        limit: this.pagination.limit
      }, this.searchData));
      var list = res.page.list || [];
	  list.map(res => {
	  	//
	  })
      this.tableData = list;
      this.pagination.total = res.page.totalCount;
    },
    page() {
      this.getData();
    },
    createdAcc() {
      this.elFormVisible();
    },
	position(e){
		console.log(e)
	},
    async elFormDetail(row) {

      Object.keys(this.formData).forEach((key) => {
		this.formData[key] = row[key];
	  });
	  this.formData.storeId = this.formData.storeId ? this.formData.storeId.split(',') : []	  
	  this.formData.storeId = this.formData.storeId.map(res => { return res * 1 })

      this.elFormVisible('编辑')
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
	  formData.storeId = formData.storeId ? formData.storeId.join(',') : ''
      if (!formData.scId) {
        var res = await this.apis.storecoach_save(formData);
      } else {
        var res = await this.apis.storecoach_update(formData);
      }
      this.elFormVisible();

      this.getData();

    },
	async changeStatus(row) {
		var res = await this.apis.storecoach_update(row);
		//this.getData();
	}
  },
};
</script>

<style scoped lang="scss">
</style>
