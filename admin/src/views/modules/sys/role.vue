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
import {
	Loading
} from 'element-ui'
import { listRole, getRole, delRole, addRole, updateRole, exportRole, dataScope, changeRoleStatus } from '@/api/system/role'
import { listMenu } from '@/api/system/menu'
import { treeDataTranslate } from '@/utils/index'
export default {
  components: {
    
  },
  data() {
    return {
      tableLoading: false,
      searchData: {
        roleName:''
      },
      searchForm: [
        { type: "input", placeholder: "角色名称", prop: "roleName", width: 200 }
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
          label: "添加角色",
          type: "primary",
          handle: e => {this.elFormVisible()} ,
		  isShow: e => this.checkBtn('sys:role:save,sys:menu:perms')
        }
      ],
      tableData: [],
      tableCols: [
        { label: "ID", prop: "roleId" },
        { label: "角色名称", prop: "roleName" },
        { label: "所属部门", prop: "deptName" },
        { label: "备注", prop: "remark" },
        { label: "创建时间", prop: "createTime", formatter: e => { return this.parseTime(e.createTime) } },
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
			  isShow: e => this.checkBtn('sys:role:update,sys:menu:perms')
            },
            {
              label: "删除",
              type: "danger",
              size: "mini",
              icon: "el-icon-delete",
              handle: (row) => this.del(row),
			  isShow: e => this.checkBtn('sys:role:delete')
            }
          ]
        },
      ],
      pagination: { limit: 10, offset: 1, total: 1 },
      formData: {
        roleId: '',
        roleName: "",
		deptName: '',
		deptId: '',
        remark: '',
		menuIdList: [],
		createTime: '',
		deptIdList: []
      },
      formCols: [
        { type: "input", label: "角色名称", width: 350, prop: "roleName" },
		{ type: "input", label: "备注", width: 350, prop: "remark" },
		{ type:'popover', label: "选择部门", prop: "deptName",width: 350,options:[],handleNodeClick:e=>this.nodeClick(e) },
		{ type:'tree', label: "选择权限", prop: "menuList",width: 350,options:[],check2:e=>this.menuClick2(e),check:e=>this.menuClick(e) },
        
      ],
      formRules: {
        roleName: [
          { required: true, message: '请输入', trigger: 'blur' },
        ],
        deptId: [
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
	this.getDeptList()
	this.getRouters()
  },
  methods: {
    del(row) {
      this.$confirm('确定要删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        var res = await delRole(
          row.roleId
        );
        this.$message({
          type: 'success',
          message: '删除成功!'
        });
        this.getData();
      }).catch(() => {

      });
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
	async getRouters() {
		var res = await listMenu({});
		  let list = treeDataTranslate(res, 'menuId', 'parentId')
		  list.forEach(item=>{
				item.id = item.menuId
				item.text = item.name
				if(item.children && item.children.length > 0){
					item.children.forEach(rres => {
						rres.id = rres.menuId
						rres.text = rres.name
						if(rres.children && rres.children.length > 0){
							rres.children.forEach(rrres => {
								rrres.id = rrres.menuId
								rrres.text = rrres.name
							})
						}
					})
				}
			})


      this.formCols[this.labIndex(this.formCols, '选择权限')].options = list;
	},	
    async getData() {
      var res = await this.apis.getRoleList({
        page: this.pagination.offset,
        limit: this.pagination.limit,
        roleName:this.searchData.roleName
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
	  
	  if(row.roleId){
	  	let loading = Loading.service({
			lock: true,
			text: '加载中……',
			background: 'rgba(0, 0, 0, 0)'
		})
	  	let res = await getRole(row.roleId).catch(e => { loading.close() })
		loading.close()
		this.formCols[this.labIndex(this.formCols, '选择权限')].default_checked_keys = res.role.menuIdList
		this.formData.menuIdList = res.role.menuIdList
	  }

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
	menuClick(checkedNodes, checkedNodesKeys){
		//console.log(1, checkedNodes, checkedNodesKeys)
		
	},
	menuClick2(checkedNodes, checkedNodesKeys){
		console.log(2, checkedNodes, checkedNodesKeys)
		let temp = []
		temp.push(checkedNodes.id)		
		if(checkedNodes.children){
			checkedNodes.children.map(res => {
				temp.push(res.id)	
			})
		}
		temp.map(res => {
			let index = this.formData.menuIdList.indexOf(res)
			if(index == -1){
				this.formData.menuIdList.push(res)
			}else{
				this.formData.menuIdList.splice(index, 1)
			}
		})
	},
    async submit() {
		this.formData.deptIdList = []
      if (!this.formData.roleId) {
        var res = await addRole(this.formData);
      } else {
        var res = await updateRole(this.formData);
      }
      this.elFormVisible();

      this.getData();


    }
  },
};
</script>

<style scoped lang="scss">
</style>
