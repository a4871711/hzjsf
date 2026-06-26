<template>
  <div>
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
    <r-form labelWidth="100px" :isHandle="true" :formRules="formRules" :formCols="formCols" :formHandle="formHandle" :formData="formData" ref="elForm" :inline="false" dialogWidth="750px" />

  </div>
</template>

<script>
export default {
  components: {
    
  },
  data() {
    return {
      tableLoading: false,
      searchData: {
        
      },
      searchForm: [
        
      ],
      searchHandle: [
        {
          label: "搜索",
          type: "primary",
          handle: e => this.getData()
        },
      ],
      tableHandles: [
        
      ],
      tableData: [],
      tableCols: [
        //{ label: "ID", prop: "pId" },
        { label: "标题", prop: "typeDesc" },		
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
              handle: (row) => this.elFormDetail(row)
            }
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      formData: {
	    pId: '',
        protocols: '',
      },
      formCols: [	
		{ type: "tinymce", label: "内容", width: 350, prop: "protocols"},
      ],
      formRules: {
        protocols: [
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
  },
  methods: {
    async getData() {
      var res = await this.apis.protocol_list(Object.assign({
        page: this.pagination.offset,
        limit: this.pagination.limit
      }, this.searchData));
      var list = res.page.list || [];
	  list.map(res => {
	  	res.typeDesc = res.type == 0 ? '用户协议' : res.type == 1 ? '隐私协议': res.type == 2 ? '矢历连续包月协议' : res.type == 3 ? '矢历连续会员协议' : res.type == 4 ? '矢历连续次卡会员协议' : res.type == 5 ? '管理端服务协议' : '管理端隐私协议' 
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
      if (!this.formData.pId) {
        var res = await this.apis.protocol_save(this.formData);
      } else {
        var res = await this.apis.protocol_update(this.formData);
      }
      this.elFormVisible();

      this.getData();

    }
  },
};
</script>

<style scoped lang="scss">
</style>
