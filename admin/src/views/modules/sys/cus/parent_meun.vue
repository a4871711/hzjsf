<template>
    <div class="cus">
      <div class="cus_box">
        <div class="title"><span>选择上级菜单</span><i @click="$emit('parent_meun_call')" class="el-icon-close"></i></div>
        <div class="content">
          <div style="max-height: 400px;overflow-y: scroll;">
            <el-tree :data="data" :props="defaultProps" @node-click="handleNodeClick"></el-tree>
          </div>
          <div class="btns">
            <el-button @click="$emit('parent_meun_call')">取消</el-button>
            <el-button @click="parent_meun_call" type="primary">确定</el-button>
          </div>
        </div>
      </div>
    </div>
</template>

<script>
	import { listMenu } from '@/api/system/menu'
    export default {
        name: "parent_meun",
      data(){
          return{
            resut:{},
            data: [],
            defaultProps: {
              children: 'children',
              label: 'name'
            },
          }
      },
      props:['res'],
      mounted(){
        this.getData();
      },
      methods:{
        async getData(){
          var res=await listMenu();
          var list=res||[];
          var children=[];
          list.map((res)=>{
            if(res.type==0){
			  var rres = {
			  	name: res.name,
				menuId: res.menuId
			  }
              rres.children=[];
              list.map((res2)=>{
                if(res.menuId==res2.parentId&&res2.type==1){
                  rres.children.push({
				  	name: res2.name,
					menuId: res2.menuId
				  });
                }
              })
              children.push(rres);
            }
          });


          children.sort(function (a,b) {
              return a.orderNum-b.orderNum
          })

          var data=[
            {
              name: '一级菜单',
              menuId:'0',
              children: children
            }
          ];
          this.data=data;
          // console.log(this.data);
        },
        handleNodeClick(data) {
          this.resut=data;
        },
        parent_meun_call(){
          this.$emit('parent_meun_call',this.resut)
        }
      }
    }
</script>

<style scoped>
    .cus_box{
      width:300px;
    }
</style>
