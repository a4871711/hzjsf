<template>
  <div class="container">
    <div class="btns">
      <el-button @click="add_menu.id=null,add_menu.show=true" type="primary">新增</el-button>
      <div class="table">
        <el-table
          :data="tableData"
          style="width: 100%;margin-bottom: 20px;"
          height="650"
          row-key="id"
          border
          default-expand-all
          :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
          <el-table-column
            prop="menuId"
            label="菜单ID"
            width="180">
          </el-table-column>
          <el-table-column
            prop="name"
            label="菜单名称"
            width="180">
          </el-table-column>
          <el-table-column
            prop="parentName"
            label="上级菜单">
          </el-table-column>
          <el-table-column
            prop="type_des"
            label="类型">
          </el-table-column>
          <el-table-column
            prop="url"
            label="菜单URL">
          </el-table-column>
		  <!--<el-table-column
            prop="component"
            label="组件路径">
          </el-table-column>-->
          <el-table-column
            prop="perms"
            label="授权标识">
          </el-table-column>
          <el-table-column
            prop="orderNum"
            label="排序">
          </el-table-column>
		  <!--<el-table-column
				prop="visible"
				label="是否隐藏"
				width="180">
				<template slot-scope="scope">
				  <span>{{scope.row.visible==0?'显示':'隐藏'}}</span>
				</template>
			  </el-table-column>
		  <el-table-column
				prop="status"
				label="是否停用"
				width="180">
				<template slot-scope="scope">
				  <span>{{scope.row.status==0?'启用':'停用'}}</span>
				</template>
			  </el-table-column>-->
          <el-table-column
		  	width="160"
            prop="perms"
            label="操作">
            <template slot-scope="scope">
              <el-button @click="edit(scope.row)" size="mini" type="success">编辑</el-button>
<!--              <el-button @click="edit(scope.row,2)" size="mini" type="primary">添加</el-button>-->
              <el-button @click="sys_menu_delete(scope.row)" size="mini" type="danger">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <add_menu :res="add_menu" v-if="add_menu.show" v-on:add_menu_call="add_menu_call"></add_menu>
  </div>
</template>


<script>
	import { listMenu, delMenu } from '@/api/system/menu'
    import add_menu from "./cus/add_menu";
    export default {
      components:{
          add_menu
        },
        data(){
          return{
            add_menu:{
              show:false,
              id:null,
              data:null,
              type:''
            },
            tableData: []
          }
        },
        mounted(){
            this.getData();
        },
        methods:{
          async getData(){
            var res=await listMenu();
            var menu_list=[];
            res.map((res1,index)=>{
              res1.id=index+1;
              res1.type_des=res1.type==0?'目录':res1.type==1?'菜单':'按钮'
			  var hasChild = false
			  res.map(res2 => {
			  	if(res2.type == 1 && res2.parentId == res1.menuId)hasChild = true
			  })
              if(res1.type==0 || (res1.type == 1 && res1.parentId == 0 && !hasChild)){
                res1.children=[];
                menu_list.push(res1);
              }
            });
            menu_list.map((res1)=>{
              res.map((res2)=>{
                if(res2.parentId==res1.menuId&&res2.type==1){
                  res2.children=[];
                  res1.children.push(res2);
                  res.map((res3)=>{
                    if(res3.type==2&&res3.parentId==res2.menuId){
                      res2.children.push(res3);
                    }
                  })
                }
              })
            });


            menu_list.sort(function (a,b) {
              return a.orderNum-b.orderNum;
            })


            this.tableData=menu_list;

          },
          edit(row,t){
            if(t==2){
              this.add_menu.type=t;
              this.add_menu.data=row;
            }else{
              this.add_menu.id=row.menuId;
            }
            this.add_menu.show=true;
          },
          sys_menu_delete(row){
            this.$confirm('确定要删除吗?', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              delMenu(row.menuId).then(()=>{
                this.$message({
                  type: 'success',
                  message: '删除成功!'
                });
                this.getData();
              });

            }).catch(() => {
              this.$message({
                type: 'info',
                message: '已取消删除'
              });
            });
          },
          add_menu_call(res){
            this.add_menu.show=false;
            if(res){
              this.getData();
            }
          }
        }
    }
</script>

<style scoped>
  .table{
    margin-top:20px;
  }
</style>
