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
import { handleTree } from '@/utils/common'
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
        username:'',
        mobile:'',
		status: '',
        //roleName:''
      },
      searchForm: [
        { type: "input", placeholder: " 合伙人名称", prop: "username", width: 200 },
        { type: "input", placeholder: "手机号", prop: "mobile", width: 200 },
        { type: "select", placeholder: "启用状态", prop: "status", width: 200,options:[{
		value:1, label:'正常'
		},{
		value:0, label:'禁用'
		}] },
        //{ type: "select", placeholder: "所属角色", prop: "roleName", width: 200,options:[] },
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
          label: "添加 合伙人",
          type: "primary",
          handle: e => {
		    this.formRules.password[0].required = true
		  	this.elFormVisible()
		  } ,
		  isShow: e => this.checkBtn('sys:partner:add')
        }
      ],
      tableData: [],
      tableCols: [
        { label: "ID", prop: "userId" },
        { label: " 合伙人名称", prop: "username" },
        //{ label: "姓名", prop: "name" },
        { label: "手机号", prop: "mobile" },
        //{ label: "所属角色", prop: "roleName" },
		{label: '关联门店', prop: 'storeNames'},
        { label: "创建时间", prop: 'createTime', formatter: e => { return this.parseTime(e.createTime) } },
		{
			label: '启用状态',
			type: "switch",
			prop: 'status',
			values: [1, 2],
			change: (row) => this.changeStatus(row),
			isDisabled: e => !this.checkBtn('sys:partner:status')
		},
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
			  isShow: e => this.checkBtn('sys:partner:edit')
            },
            {
              label: "删除",
              type: "danger",
              size: "mini",
              icon: "el-icon-delete",
              handle: (row) => this.del(row),
			  isShow: e => this.checkBtn('sys:partner:del')
            }
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      formData: {
        username: '',
        password: "",
		deptName: '',
		deptId: '',
        roleId: [],
        //name: "",
        mobile: "",
		storeIds: []
      },
      formCols: [
        { type: "input", label: " 合伙人名称", width: 350, prop: "username" },
        //{ type: "input", label: "登录账号", width: 350, prop: "name" },
        { type: "input", label: "手机号码", width: 350, prop: "mobile" },
		{ type:'popover', label: "选择部门", prop: "deptName",width: 350,options:[],handleNodeClick:e=>this.nodeClick(e) },
        { type: "select", label: "选择角色", width: 350, prop: "roleId", multiple: true, options: [] },
		{ type: "select", label: "关联门店", width: 350, prop: "storeIds", multiple: true },	
        { type: "input", label: "密码", width: 350, prop: "password" }
      ],
      formRules: {
        username: [
          { required: true, message: '请输入', trigger: 'blur' },
        ],
        mobile: [
          { required: false, message: '请输入', trigger: 'blur' },
        ],
        /*name: [
          { required: false, message: '请输入', trigger: 'blur' },
        ],*/
        password: [
          { required: true, message: '请输入', trigger: 'blur' },
        ],
        deptId: [
          { required: true, message: '请选择', trigger: 'blur' },
        ],
        roleId: [
          { required: true, message: '请选择', trigger: 'blur' },
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
    this.getRoleList();
	this.getDeptList()
	this.getStoreList()
  },
  methods: {
    del(row) {
      this.$confirm('确定要删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        var res = await this.apis.user_delete(
          [row.userId]
        );
        this.$message({
          type: 'success',
          message: '删除成功!'
        });
        this.getData();
      }).catch(() => {

      });
    },
    async getRoleList() {
      var res = await this.apis.getRoleList({page: 1, limit: 999});
      var list = res.page.list || [];
      let list2=[];
      list.map(res => {
        res.value = res.roleId;
        res.label = res.roleName;
        let obj={
          value:res.roleName,
          label:res.roleName
        }
        list2.push(obj);
      });



      this.formCols[this.labIndex(this.formCols, '选择角色')].options = list;
      //this.searchForm[this.labIndex2(this.searchForm, 'roleName')].options = list2;
    },
	async getDeptList() {
		var res = await this.apis.getDeptList({});
		  let list = handleTree(res, 'deptId')
		  list.forEach(item=>{
				item.id = item.deptId
				item.text = item.name
				if(item.children && item.children.length > 0){
					item.children = item.children
					item.children.forEach(rres => {
						rres.id = rres.deptId
						rres.text = rres.name
					})
				}
			})


      this.formCols[this.labIndex(this.formCols, '选择部门')].options = list;
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


      this.formCols[this.labIndex(this.formCols, '关联门店')].options = list;
	},
    async getData() {
      var res = await this.apis.user_list({
        page: this.pagination.offset,
        limit: this.pagination.limit,
        username:this.searchData.username,
        mobile:this.searchData.mobile,
        status:this.searchData.status,
		type:2
      });
      var list = res.page.list || [];
	  list.map(res => {
	  	res.status = res.status == 1 ? 1 : 2
		res.roleId = []
		res.roleList.map(rres => {
			res.roleId.push(rres.roleId)
		})
		res.storeIds = res.storeIds ? res.storeIds.split(',') : []
		res.storeIds = res.storeIds.map(rres => {
			return rres * 1
		})		
		var temp = []
		res.storeList.map(rres => {
			temp.push(rres.storeName)
		})
		res.storeNames = temp.join(',')
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

      this.formData.id = row.userId;
      //this.formData.name=row.name;
      this.formData.mobile=row.mobile;
      this.formData.username=row.username;
      this.formData.deptId=row.deptId;
      this.formData.roleId=row.roleId;
	  this.formData.deptName=row.deptName
	  this.formData.storeIds=row.storeIds
	  this.formRules.password[0].required = false
      this.elFormVisible()
    },
    elFormSubmit() {
      this.$refs.elForm.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.submit();
        }
      })
    },
	nodeClick(data){
		this.formData.deptName = data.text
		this.formData.deptId = data.id
	},
    async submit() {
      if (!this.formData.id) {
        var res = await this.apis.user_save({
          username: this.formData.username,
          //name: this.formData.name,
          mobile: this.formData.mobile,
          password: this.formData.password,
		  deptId: this.formData.deptId,
          roleIdList: this.formData.roleId,
		  status: 1,
		  type: 2,
		  storeIds: this.formData.storeIds.join(',')
        });
      } else {
        var res = await this.apis.user_update({
          username: this.formData.username,
          //name: this.formData.name,
          mobile: this.formData.mobile,
          password: this.formData.password,
		  deptId: this.formData.deptId,
          roleIdList: this.formData.roleId,
          userId: this.formData.id,
		  storeIds: this.formData.storeIds.join(',')
        });
      }
      this.elFormVisible();

      this.getData();


    },
	async changeStatus(row) {
		var res = await this.apis.user_update({
          userId: row.userId,
          username: row.username,
          //name: row.name,
          mobile: row.mobile,
		  roleIdList: row.roleId,
		  status: row.status == 1 ? 1 : 0
        });
		//this.getData();
	}
  },
};
</script>

<style scoped lang="scss">
</style>
