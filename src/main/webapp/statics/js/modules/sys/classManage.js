$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/storeClass/list',
        datatype: "json",
        colModel: [			
			{ label: '编号', name: 'arrangeClassId', index: 'arrangeClassId', width: 50, key: true },
            { label: '订单号', name: 'orderNo', index: 'orderNo', width: 80 },
			{ label: '上课门店名称', name: 'storeName', index: 'storeName', width: 80},
            { label: '课程名称', name: 'className', index: 'className', width: 80},
            { label: '学员昵称', name: 'nickname', index: 'nickname', width: 80 },
            { label: '学员电话', name: 'stuPhone', index: 'stuPhone', width: 80 },
			{ label: '教练名称', name: 'coachName', index: 'coachName', width: 80 },
			{ label: '教练电话', name: 'coachPhone', index: 'coachPhone', width: 80 },
            { label: '可约/节', name: 'askNumber', index: 'askNumber', width: 80 },
			{ label: '已购/节数', name: 'buyNumber', index: 'buyNumber', width: 80 },
			{ label: '上课时间', name: 'classTime', index: 'classTime', width: 80 },
			{ label: '上课时长/分钟', name: 'duration', index: 'duration', width: 50 },
			{ label: '消耗能量/kCal', name: 'energy', index: 'energy', width: 50 },
			{ label: '课程过期时间', name: 'validityDate', index: 'validityDate', width: 80 },
			{ label: '课程状态', name: 'classStatus', index: 'classStatus', width: 50,
                formatter: function(value){
			        if(value == 0){
                        return '<span class="label label-danger">进行中</span>';
                    }else if(value == 1){
                        return '<span class="label label-danger">已完成</span>';
                    }else if(value == 2){
                        return '<span class="label label-danger">已取消</span>';
                    }else if(value == 3){
                        return '<span class="label label-danger">已过期</span>';
                    }else{
                        return '<span class="label label-danger"></span>';
                    }

                }
            }
        ],
		viewrecords: true,
        height: 'auto',
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
		list: {},
        status:null,
        q:{
            stuPhone:null,
            coachPhone:null,
            orderNo:null,
            classStatus:0
        }
},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			/*vm.showList = false;
			vm.title = "新增";
			vm.walletDetail = {};*/
			//window.href="./modules/sys/paramset.html";
            window.open(baseURL + "index.html#modules/sys/paramset.html");
		},
		update: function (event) {
			var id = getSelectedRow();
			if(id == null){
				return ;
			}
			console.log(id);
            vm.getInfo(id)

            if(vm.status!=1){
               alert("请选择审核中的记录")
                return;
            }
            vm.showList = false;
            vm.title = "修改";


		},
        pay:function(){
            var arrangeClassId = getSelectedRow();
            if(arrangeClassId == null){
                return ;
            }

            confirm('是否确认当前课程？确认后资金将按提成比例转入教练收益', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/storeClass/update",
                    contentType: "application/json",
                    data: JSON.stringify(arrangeClassId),
                    success: function(result){
                        if(result.code == 0){
                            alert('操作成功', function(index){
                                $("#jqGrid").trigger("reloadGrid");
                            });
                        }else{
                            alert(result.msg);
                        }
                        vm.reload();
                    }
                });
            });

        },


		getInfo: function(id){
            $.ajaxSettings.async = false;
			$.get(baseURL + "sys/walletdetail/info/"+id, function(result){
                vm.walletDetail = result.walletDetail;
                vm.status = result.walletDetail.status;


                console.log(result.walletDetail);
                console.log(vm.walletDetail);
               // vm.walletDetail.status = 2;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page,
                postData:{'orderNo':vm.q.orderNo,'stuPhone':vm.q.stuPhone,"coachPhone":vm.q.coachPhone,
                    "classStatus": vm.q.classStatus }
            }).trigger("reloadGrid");
		}
	}
});