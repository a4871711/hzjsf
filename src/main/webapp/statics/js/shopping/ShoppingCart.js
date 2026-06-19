/*模拟数据*/
var shopCartdatas = {
	shopcartdatas:[]
}

var vm = new Vue({
	el: "#myVue",
	data: {
		 /*数据源*/
		 shopTableDatas:shopCartdatas.shopcartdatas,
		 userBuyData:[],//用户购买数据
		
		/*默认选择标签*/
		 checkedAll:false, //全选状态
		 stopDelete:"",//定时器id(用于清空定时器)

		/*关键字段初始化*/
		 goodNums:0,    //商品或者服务总数
		 totalMoney:0, //总价格
		 goodsNum:0,//商品的数量
         q:{
            barCode: null,
            wristId: null
         }
		 
	},
	methods: {
        query: function () {
            $.get("../../sys/storegoods/infoGoods/" + vm.q.barCode, function (r) {
                shopTableDatas:shopCartdatas.shopcartdatas.push(r.storeGoods);
            });
            vm.q.barCode = null;
            $("#bq").blur();
            $("#bq").focus();
        },
		/*商品数量增加减少函数*/
		goodNum:function(item,way){
			if(way == 1){
				 item.num++;
				 vm.countTotalMoney()
			}else{
				if(item.num < 2){
					item.num =1;
				}else{
					item.num--;
					vm.countTotalMoney()
				}
				
			}
		},
		/*单选函数*/
		checkedRadioBtn:function(tabledatas){
			this.countTotalMoney()
			/*单选计算商品或服务数量*/
			if(tabledatas.checked == true){
				this.goodsNum += 1;
			}else if(tabledatas.checked == false){
				this.goodsNum -= 1;
			}else{
				console.log("未知错误！")
			}
		},
		/*全选函数*/
		checkedAllBtn:function(checkedAll){
			var _this= this;
			/*全选计算商品或服务数量*/
			if(checkedAll == true){
				for(x in this.shopTableDatas){
					this.shopTableDatas[x].checked = true;
                    _this.goodsNum += 1;
				}
			}else{
				for(y in this.shopTableDatas){
					this.shopTableDatas[y].checked = false;
					this.goodsNum = 0;
				}
			}
			vm.countTotalMoney();
		},
		/*删除单个选中函数*/
		deletegoods:function(index){
			console.log(index);
			this.shopTableDatas.splice(index, 1); 
			vm.countTotalMoney();
		},
		/*删除多个选中函数*/
		deleteSelectAll:function(){
			for(var i = this.shopTableDatas.length-1 ; i >= 0 ; i--){
				if(this.shopTableDatas[i].checked  == true){
					this.shopTableDatas.splice(i, 1);
				}
			}
			vm.countTotalMoney();
		},

		/*多个商品移动函数*/
		saveSelectAll:function(){
			for(var i = 0 ; i <= this.shopTableDatas.length ; i++){
				if(this.shopTableDatas[i].checked  == true){
					console.log(this.shopTableDatas[i])
					this.stopDelete = setTimeout(function(){
						vm.deleteSelectAll();
						clearInterval(this.stopDelete)
					},10);
				}
			}
		},
      /*计算商品总价函数*/
		countTotalMoney:function(){
			var _this = this;
			_this.totalMoney = 0;
			this.shopTableDatas.forEach(function(item,index){
				if(item.checked == true){
					_this.totalMoney += item.num*item.price
				}
			})
		},
		/*保存购买数据*/
		saveData:function(){
			var _this = this;
			if(_this.goodsNum == 0){
                layer.msg("请选择结算商品");
                return;
            }
			if(vm.isBlankF(vm.q.wristId)){
                layer.msg("请刷手环");
                return;
            }
            var req = {totalMoney:null,wristId:null,orderList:[]};
            this.shopTableDatas.forEach(function(item,index){
                if(item.checked == true){
                    req.orderList.push(item);
                }
            })
            req.totalMoney = _this.totalMoney;
            req.wristId = vm.q.wristId;
            $.ajax({
                type: "POST",
                url: "../../sys/storegoods/goodsAccounts",
                contentType: "application/json",
                data:JSON.stringify(req),
                success: function (result) {
                    if (result.code == 0) {
                        layer.msg('结算成功', function (index) {
                            _this.shopTableDatas=null;
                            _this.q.wristId=null;
                            _this.userBuyData=null;
                            _this.goodsNum=0;
                            _this.totalMoney=0;
                            _this.checkedAll=false;

                        });
                    } else {
                        alert(result.msg);
                    }
                }
            });
			
			
		},
        //判断是否为空
        isBlankF: function(value) {
            return !value || !/\S/.test(value)
        },
		/*提示删除单个商品*/
		alertRadio:function(index){
        	this.$confirm('此操作将永久删除该商品, 是否继续?', '提示', {
          	confirmButtonText: '确定删除',
          	cancelButtonText: '取消',
         	type: 'warning'
       		}).then(() => {
	          	this.$message({
	            type: 'success',
	            message: '删除成功!',
	            callback : vm.deletegoods(index)
	         	});
        	}).catch(() => {
          		this.$message({
           		type: 'warning',
            	message: '已取消删除'
          });          
        });
      },
      /*提示多个删除函数*/
      alertMuch:function(){
        	this.$confirm('此操作将永久删除已选择商品或服务, 是否继续?', '提示', {
          	confirmButtonText: '确定删除',
          	cancelButtonText: '取消',
         	type: 'warning'
       		}).then(() => {
	          	this.$message({
	            type: 'success',
	            message: '删除成功!',
	            callback : vm.deleteSelectAll()
	         	});
        	}).catch(() => {
          		this.$message({
           		type: 'warning',
            	message: '已取消删除'
          });          
        });
      },
     
      /*提示单个商品移动到收藏函数*/
      alertmovesSavegoods:function(index){
        	this.$confirm('此操作将已选择商品或服务移到我的收藏, 是否继续?', '提示', {
          	confirmButtonText: '确定',
          	cancelButtonText: '取消',
         	type: 'warning'
       		}).then(() => {
	          	this.$message({
	            type: 'success',
            	message: '收藏成功!',
	            callback : vm.movesSave(index)
	         	});
        	}).catch(() => {
          		this.$message({
          		type: 'success',
	            message: '收藏成功!',
          });          
        });
      },
      /*提示收藏多个商品函数*/
      alertMuchgoods:function(){
        	this.$confirm('此操作将已选择商品或服务移到我的收藏, 是否继续?', '提示', {
          	confirmButtonText: '确定',
          	cancelButtonText: '取消',
         	type: 'warning'
       		}).then(() => {
	          	this.$message({
	            type: 'success',
            	message: '收藏成功!',
	            callback : vm.saveSelectAll()
	         	});
        	}).catch(() => {
          		this.$message({
          		type: 'success',
	            message: '收藏成功!',
          });          
        });
      }
	},
	/*金额过滤器*/
	filters:{
		moneyFiler:function(value){
			
			return "￥"+value.toFixed(2);
		}
	}
});

