<template>
  <transition name="fade">
    <router-view></router-view>
  </transition>
</template>

<script>
  export default {
    data(){
      return{

      }
    },
    mounted(){
      var that=this;
      if(window.btns_arr){
        this.setBtn(window.btns_arr);
      }else{
        window.btns_arr_call=function (res) {
          that.setBtn(res);
        }
      }
    },
    methods:{
      setBtn(list){
        var obj={};
        list.map(res=>{
          obj[res]=true;
        });

        console.log(obj);
        this.$store.commit('setButtons',obj)
      }
    }
  }
</script>
<style lang="scss">
	.el-button [class*=el-icon-]+span {
	    margin-left: 0px;
	}
	.el-dialog__header{
		border-bottom: 1px solid #EBEEF5;
	}


  .cus{
    position: fixed;
    left:0;
    top:0;
    right:0;
    bottom:0;
    background: rgba(0,0,0,0.5);
    z-index: 1021;
  .cus_box{
    width:800px;
    position: absolute;
    left:50%;
    top:68px;
    transform: translate(-50%, 0);
    background: #fff;
    padding:0 20px 20px 20px;
    border-radius: 8px;
  .title{
    height:40px;
    line-height: 40px;
    border-bottom:1px solid #e5e5e5;
  span{
    color:#17B3A3;
  }
  i{
    font-size:20px;
    float:right;
    cursor: pointer;
    margin-top:10px;
  }
  }
  .content{
    margin-top:15px;
	    max-height: 75vh;
    overflow: auto;
  .btns{
    text-align: center;
    margin-top:15px;
  }
  }
  }
  }

  .tangram-suggestion-main{
    z-index: 10000;
  }

  .zZindex {
    z-index:3000 !important;
  }


  .color1{
    background:red;
  }
</style>
