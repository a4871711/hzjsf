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
        name:'',
        phone:'',
        status:''
      },
      searchForm: [
        { type: "input", placeholder: "合伙人名称", prop: "name", width: 200 },
        { type: "input", placeholder: "手机号", prop: "phone", width: 200 },
        { type: "select", placeholder: "状态", prop: "status", width: 200, options:[
			{value: 1, label: '启用'},
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
          label: "添加合伙人",
          type: "primary",
          handle: e => {this.elFormVisible()} ,
        }
      ],
      tableData: [],
      tableCols: [
        { label: "ID", prop: "id" },
        { label: "合伙人名称", prop: "name" },
        { label: "手机号", prop: "phone" },
        { label: "门店数量", prop: "storeNum", formatter: e => { return e.storeNum || 0 } },
        { label: "本月门店累计办卡收入", prop: "storeMonthMoney", formatter: e => { return e.storeMonthMoney || 0 } },
		{
			label: '状态',
			type: "switch",
			prop: 'status',
			values: [1, 2],
			change: (row) => this.changeStatus(row)
		},
        { label: "创建时间", prop: 'createTime', formatter: e => { return this.parseTime(e.createTime) } },		
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
	  	id:'',
		type:5,
        name: '',
        password: "",
		phone: '',
		status: 1
      },
      formCols: [
        { type: "input", label: "合伙人名称", width: 350, prop: "name" },
        { type: "input", label: "手机号码", width: 350, prop: "phone" },		
        { type: "radio", label: "状态", width: 350, prop: "status", radios: [
			{value: 1, label: '启用'},
			{value: 2, label: '禁用'}
		] },
        //{ type: "input", label: "密码", width: 350, prop: "password" }
      ],
      formRules: {
        name: [
          { required: true, message: '请输入', trigger: 'blur' },
        ],
        phone: [
          { required: false, message: '请输入', trigger: 'blur' },
        ],
        password: [
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
        var res = await this.apis.agent_delete(
          [row.id]
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
      var res = await this.apis.agent_list({
	  	type: 5,
        page: this.pagination.offset,
        limit: this.pagination.limit,
        name:this.searchData.name,
        phone:this.searchData.phone,
		status: this.searchData.status
      });
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
    async elFormDetail(row) {

      Object.keys(this.formData).forEach((key) => {
		this.formData[key] = row[key];
	  });

      this.elFormVisible()
    },
    elFormSubmit() {
      this.$refs.elForm.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.submit();
        }
      })
    },
    async submit() {
      if (!this.formData.id) {
        var res = await this.apis.agent_save(this.formData);
      } else {
        var res = await this.apis.agent_update(this.formData);
      }
      this.elFormVisible();

      this.getData();

    },
	async changeStatus(row) {
		var res = await this.apis.agent_update({
			id: row.id,
			status: row.status
		});
		//this.getData();
	}
  },
};
</script>

<style scoped lang="scss">
</style>
