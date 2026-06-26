<template>
  <div>
    <!--<r-search ref="search" :searchData="searchData" :searchForm="searchForm" :searchHandle="searchHandle" />-->
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
        {
          label: "添加",
          type: "primary",
          handle: e => {this.elFormVisible()} ,
        }
      ],
      tableData: [],
      tableCols: [
        { label: "ID", prop: "advId" },
        { label: "标题", prop: "advTitle" },
        { label: "图片", prop: "advMainImg", type: 'image' },
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
              handle: (row) => this.elFormDetail(row)
            },
            {
              label: "删除",
              type: "danger",
              size: "mini",
              icon: "el-icon-delete",
              handle: (row) => this.del(row)
            }
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      formData: {
	    advId: '',
		advType: 1,
        advTitle: '',
        advMainImg: "",
      },
      formCols: [	
        { type: "input", label: "标题", width: 350, prop: "advTitle" },
		{ type: "upload", label: "图片", placeholder:'宽高：550*200，格式：png, jpg, jpeg', width: 350, prop: "advMainImg", success: (res, file) => {
						console.log(res)
						this.$refs.elForm.loading = false
						this.formData.advMainImg = res.path[0]
					} },
      ],
      formRules: {
        advTitle: [
          { required: true, message: '请输入', trigger: 'blur' },
        ],
        advMainImg: [
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
    del(row) {
      this.$confirm('确定要删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        var res = await this.apis.advertising_delete(
          [row.advId]
        );
        this.$message({
          type: 'success',
          message: '删除成功!'
        });
        this.getData();
      }).catch(() => {

      });
    },
    async getData() {
      var res = await this.apis.advertising_list(Object.assign({
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
      if (!this.formData.advId) {
        var res = await this.apis.advertising_save(this.formData);
      } else {
        var res = await this.apis.advertising_update(this.formData);
      }
      this.elFormVisible();

      this.getData();

    }
  },
};
</script>

<style scoped lang="scss">
</style>
