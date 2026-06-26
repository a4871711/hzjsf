<template>
  <div>
		<r-table
			:isHandle="true"
			:isBorder="true"
			:tableData="tableData"
			:tableCols="tableCols"
			:tableHandles="tableHandles"			
			row-key="deptId"
			:treeProps="{children: 'children', hasChildren: 'hasParent'}"/>
			
			<r-form
			  :isHandle="true"
			  :formRules="formRules"
			  :formCols="formCols"
			  :formHandle="formHandle"
			  :formData="formData"
				labelWidth="150px"
			  dialogWidth="700px"
				:destroy="false"
			  :inline="false"
			  ref="elForm"/>
  </div>
</template>

<script>
import { handleTree } from '@/utils/common'
export default {
  data() {
    return {
			tableData: [],
			tableCols: [
				{ label: "上级目录", prop: "parentName",align:'left'},
				{ label: "部门名称", prop: "name",align:'left' },
				{ label: "排序", prop: "orderNum",align:'left'},
				{label: "操作",type: "button",btnList: [
					{type: "primary",size: "mini",label: "编辑",handle: row => this.elFormDetail(row), isShow: e => this.checkBtn('sys:dept:update,sys:dept:select')},
					{type: "danger",size: "mini",label: "删除",handle: row => this.TableDel('/sys/dept/delete',{deptId:row.deptId}),isShow: e => this.checkBtn('sys:dept:delete')},
				]}
			],
			tableHandles: [
				{
					label: "添加部门",
					type: "primary",
					handle: (that) => this.elFormVisible() ,
					isShow: e => this.checkBtn('sys:dept:save,sys:dept:select')
				},
			],
			formData: {
				deptId: '',
			  	name: "",
				parentName:'',
				parentId:0,
				orderNum:0
			},
			formCols: [
			  { type: "input", label: "部门名称", width: 350, prop: "name" },
				{ type:'popover', label: "上级目录", prop: "parentName",width: 350,options:[],handleNodeClick:e=>this.nodeClick(e) },
				{ type:'number', label: "排序", prop: "orderNum",width: 200,  },
			],
			formRules: {
			  name: [
			    { required: true, message: "请输入角色名称", trigger: "blur" },
			  ],
				parentId: [
			    { required: true, message: "请选择上级目录", trigger: "change" },
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
  created() {},
  mounted() {
		
		this.init()
	},
  methods: {
		init(){
			this.$http.get("/sys/dept/list").then(data => {
			  
			  let list = []
			  data.forEach(item=>{
					if(item.parentId == 0){
						list.push({
							id: item.deptId,
							text: item.name
						})
					}
				})
				
			  	this.tableData = handleTree(data, 'deptId');
				this.formCols[this.labIndex(this.formCols,'上级目录')].options = list
			});
		},
		nodeClick(data){
			this.formData.parentName = data.text
			this.formData.parentId = data.id
		},
		elFormDetail(row) {
		  Object.keys(this.formData).forEach((key) => {
		    this.formData[key] = row[key];
		  });			
		  this.elFormVisible();
		},
		elFormSubmit() {
		  if(this.formData.deptId){
		  	if(this.formData.deptId == this.formData.parentId){
				this.$message({
						  type: 'error',
						  message: '上级不能选择自己!'
						});
				return;
			}
			if(this.formData.parentId > 0){
				let hasChild = false
				this.tableData.map(item => {
					if(item.parentId == this.formData.deptId)hasChild = true
				})
				if(hasChild){
					this.$message({
						  type: 'error',
						  message: '已有下级，不可选择上级!'
						});
					return;
				}
			}
		  }
		
		  this.$refs.elForm.$refs.ruleForm.validate((valid) => {
		    if (valid) {
		      let api = this.formData.deptId?'/sys/dept/update':'/sys/dept/save'
		      this.$http({
					url: this.$http.adornUrl(api),
					method: 'post',
					contentType: 'json',
					data: this.$http.adornData({
						...this.formData
					}, false, 'json')
				}).then(res=>{
						if(res && res.code==0){
							this.$message({
								type: 'success',
								message: this.formData.deptId ? "编辑成功!" : "添加成功!",
								duration:1300,
								onClose:res=>{
									this.elFormVisible()
									this.init()
								}
							});
						}
		      	
		      })
		    }
		  });
		},
	},
};
</script>

<style scoped lang="scss">
</style>
