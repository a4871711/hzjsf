$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/goodsevaluate/list',
        datatype: "json",
        colModel: [			
			{ label: '序号', name: 'goodsEvaluatId', index: 'goodsEvaluatId', width: 50, key: true },
	  		{ label: '商品ID', name: 'goodsId', index: 'goodsId', width: 80 }, 			
	  		{ label: '订单编号', name: 'orderNo', index: 'orderNo', width: 80 }, 			
	  		{ label: '评论用户ID', name: 'userId', index: 'userId', width: 80 }, 			
	  		{ label: '头像', name: 'headImgUrl', index: 'headImgUrl', width: 80 }, 			
	  		{ label: '昵称', name: 'nickname', index: 'nickname', width: 80 }, 			
	  		{ label: '评价等级(*级)', name: 'evLevel', index: 'evLevel', width: 80 }, 			
	  		{ label: '评价内容', name: 'evContent', index: 'evContent', width: 80 }, 			
	  		{ label: '评论图', name: 'evaluatImgUrl', index: 'evaluatImgUrl', width: 80 }, 			
	  		{ label: '评论时间(yyyy-MM-dd)', name: 'evaluatDate', index: 'evaluatDate', width: 80 }			
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		goodsEvaluate: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.goodsEvaluate = {};
		},
		update: function (event) {
			var goodsEvaluatId = getSelectedRow();
			if(goodsEvaluatId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(goodsEvaluatId)
		},
		saveOrUpdate: function (event) {
            if (!vm.validator()){
                return;
            }
			var url = vm.goodsEvaluate.goodsEvaluatId == null ? "sys/goodsevaluate/save" : "sys/goodsevaluate/update";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.goodsEvaluate),
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(index){
							vm.reload();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
		del: function (event) {
			var goodsEvaluatIds = getSelectedRows();
			if(goodsEvaluatIds == null){
				return ;
			}
			
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "sys/goodsevaluate/delete",
                    contentType: "application/json",
				    data: JSON.stringify(goodsEvaluatIds),
				    success: function(r){
						if(r.code == 0){
							alert('操作成功', function(index){
								$("#jqGrid").trigger("reloadGrid");
							});
						}else{
							alert(r.msg);
						}
					}
				});
			});
		},
		getInfo: function(goodsEvaluatId){
			$.get(baseURL + "sys/goodsevaluate/info/"+goodsEvaluatId, function(r){
                vm.goodsEvaluate = r.goodsEvaluate;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		}
	}
});