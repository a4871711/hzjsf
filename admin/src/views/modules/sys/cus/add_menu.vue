<template>
    <div class="cus">
        <div class="cus_box">
          <div class="title"><span>{{res.id?'编辑':'添加'}}菜单</span><i @click="$emit('add_menu_call')" class="el-icon-close"></i></div>
          <div class="content">
            <el-form ref="formData" :model="formData" :rules="rules" label-width="auto">
              <el-form-item label="类型" prop="type">
                <el-radio-group v-model="formData.type">
                  <el-radio  :label="0">目录</el-radio>
                  <el-radio  :label="1">菜单</el-radio>
                  <el-radio  :label="2">按钮</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="菜单名称" prop="name">
                <el-input v-model="formData.name" type="text" placeholder="请输入"></el-input>
              </el-form-item>
              <!--<el-form-item label="范围" prop="useType">
                <el-select v-model="formData.useType" placeholder="请选择">
                  <el-option label="三者共用" value=0></el-option>
                  <el-option label="总平台" value=1></el-option>
                  <el-option label="仅运营商" value=2></el-option>
                  <el-option label="仅区域代理" value=3></el-option>
                  <el-option label="仅商家" value=4></el-option>
                </el-select>
              </el-form-item>-->
              <el-form-item label="上级菜单" prop="parentId">
                <el-button @click="show_parent_meun">{{formData.label}}</el-button>
              </el-form-item>
              <el-form-item label="菜单URL" prop="url">
                <el-input v-model="formData.url" type="text" placeholder="请输入"></el-input>
              </el-form-item>
              <!--<el-form-item label="组件路径" prop="component">
                <el-input v-model="formData.component" type="text" placeholder="请输入"></el-input>
              </el-form-item>
              <el-form-item v-if="formData.menuType=='F'" label="页面对应id" prop="buttonCode">
                <el-input v-model="formData.buttonCode" type="text" placeholder="请输入"></el-input>
              </el-form-item>-->
              <el-form-item label="授权标识" prop="perms">
                <el-input v-model="formData.perms" type="text" placeholder="请输入"></el-input>
              </el-form-item>
              <el-form-item label="图标" prop="icon">
                <!-- <el-input v-model="formData.icon" type="text" placeholder="请输入"></el-input> -->
				<el-popover
                placement="bottom-start"
                width="460"
                trigger="click"
                @show="$refs['iconSelect'].reset()"
              >
                <IconSelect ref="iconSelect" @selected="selected" :active-icon="formData.icon" />
                <el-input slot="reference" v-model="formData.icon" placeholder="点击选择图标" readonly>
                  <svg
                    v-if="formData.icon"
                    slot="prefix"
                    :class="formData.icon"
                    style="width: 25px;"
                  />
                  <i v-else slot="prefix" class="el-icon-search el-input__icon" />
                </el-input>
              </el-popover>
              </el-form-item>
              <el-form-item label="排序" prop="orderNum">
                <el-input v-model="formData.orderNum" type="text" placeholder="请输入"></el-input>
              </el-form-item>
			  <!--<el-form-item label="是否隐藏" prop="visible">
                <el-select v-model="formData.visible" placeholder="请选择">
                  <el-option label="显示" value=0></el-option>
                  <el-option label="隐藏" value=1></el-option>
                </el-select>
              </el-form-item>
			  <el-form-item label="是否停用" prop="status">
                <el-select v-model="formData.status" placeholder="请选择">
                  <el-option label="启用" value=0></el-option>
                  <el-option label="停用" value=1></el-option>
                </el-select>
              </el-form-item>-->
            </el-form>

            <div class="btns">
              <el-button @click="$emit('add_menu_call')">取消</el-button>
              <el-button @click="submitForm('formData')" type="primary">确定</el-button>
            </div>

          </div>
        </div>

        <parent_meun :res="parent_meun" v-if="parent_meun.show" v-on:parent_meun_call="parent_meun_call"></parent_meun>
    </div>
</template>

<script>
	import { getMenu, addMenu, updateMenu } from '@/api/system/menu'
  import parent_meun from "./parent_meun";
  import IconSelect from "@/components/IconSelect";
    export default {
      components:{
        parent_meun,
		IconSelect
      },
      data(){
          return{
            parent_meun:{
              show:false
            },
            parentData:{
              label:'请选择',
              value:''
            },
            formData:{
			  menuId:'',
              type:0,
              name:'',
              url:'',
			  //component:'',
              perms:'',
              label:'请选择',
              parentId:'',
			  icon:'',
			  //visible:'0',
			  //status:'0'
            },
            rules:{
              
            }
          }
      },
      props:['res'],
      mounted(){

        if(this.res.id){
          this.getData();
        }

      },
      methods:{
	  	// 选择图标
		selected(name) {
		  this.formData.icon = name;
		},
        async getData(){
          var res=await getMenu(this.res.id);
          var data=res.menu;
          console.log(data);
          this.formData = data
          this.formData.label=data.parentName||'无';
        },
        submitForm(formName) {
          console.log(formName);
          this.$refs[formName].validate((valid) => {
            if (valid) {
              if(!this.res.id){
                this.add_menu();
              }else{
                this.edit_menu();
              }

            }
          });
        },
        async edit_menu(){
          var res=await updateMenu(this.formData);
          this.$emit('add_menu_call',true);
        },
        async add_menu(){
          var res=await addMenu(this.formData);
          console.log(res);
          this.$emit('add_menu_call',true);
        },
        show_parent_meun(){
          this.parent_meun.show=true;
        },
        parent_meun_call(res){
          this.parent_meun.show=false;
          if(res){
            this.formData.label=res.name;
            this.formData.parentId=res.menuId;
          }
        }
      }
    }
</script>

<style scoped lang="scss">

</style>
