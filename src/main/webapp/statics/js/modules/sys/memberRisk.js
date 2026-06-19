$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/store/memberRisk',
        datatype: "json",
        colModel: [
            { label: 'ID', name: 'id', width: 50, key: true},
            { label: '账户ID', name: 'userId', width: 50},
			{ label: '昵称', name: 'nickname'},
            { label: '手机号', name: 'phone'},
            { label: '门店', name: 'storeName'},
            { label: '异常登录时间', name: 'createTime'},
            { label: '异常扫码开门时间', name: 'firstTime'},
            { label: '上一次开门时间', name: 'lastTime'},
            { label: '核实结果', name: 'check', formatter: function (value){
                    let check = '';
                    switch (value){
                        case 1: check = '<span style="color: #00a65a;">有效</span>';    break;
                        case 2: check = '<span style="color: #ff0000;">无效</span>';    break;
                        default: check = '';
                    }
                    return check;
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
		q:{
            phone: null
		},
        device: {},
        log: {
		    id: 0,
            check: 0,
        },
	},
	methods: {
		query: function () {
			vm.reload();
		},
		reload: function (event) {
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
				postData:{'phone': vm.q.phone},
                page:page
            }).trigger("reloadGrid");
		},

        //审核弹窗
        checkRisk: function (){
            var id = getSelectedRow();
            console.log('选择了', id);
            if(id == null){return;}
            var rowData = $("#jqGrid").jqGrid('getRowData', id);
            if(rowData == null){
                layer.msg("请重新选择");
                return;
            }
            this.log.id = id;
            layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-molv',
                title: "更新审核结果",
                area: ['380px', '180px'],
                shadeClose: false,
                content: $("#checkWindow"),
            });
        },

        //更新审核结果
        doCheckIds: function (){
            confirm('确定更新记录审核结果？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/store/checkRisk",
                    // contentType: "application/x-www-form-urlencoded",
                    data: vm.log,
                    success: function(result){
                        if(result.code === 0){
                            alert('操作成功', function(index){
                                vm.reload();
                                layer.closeAll();
                                vm.log = {
                                    id: 0,
                                    check: 0,
                                };
                            });
                        }else{
                            alert(result.msg);
                        }
                    }
                });
            });
        },


	}
});