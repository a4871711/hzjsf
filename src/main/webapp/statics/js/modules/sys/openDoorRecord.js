$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/store/openDoorRecordlist',
        datatype: "json",
        colModel: [			
			{ label: '编号', name: 'id', width: 20, key: true},
			{ label: '用户昵称', name: 'nickname', width: 50 },
			{ label: '手机号', name: 'phone', width: 60 },
            { label: '门店', name: 'storeName', width: 80 },
            { label: '会员卡所属门店', name: 'deviceStoreName', width: 80 },
            { label: '距离(米)', name: 'distance', width: 40 },
            { label: '开门结果', name: 'result', width: 40, formatter: function (value){
                return value == 1 ? '<span style="color: #00a65a;">成功</span>' : '<span style="color: #ff0000;">失败</span>';
                } },
            { label: '原因', name: 'remark', width: 80 },
			{ label: '扫码时间', name: 'createTime', width: 60, formatter:function (value) {
                return value == "" || value == null ? "" : value;
            }}
        ],
		viewrecords: true,
        height: 'auto',
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: false,
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
			phone: null,
            store_name: null
		},
        total: 0,
	},
    created: function() {
        this.getTotalPeople();
    },
	methods: {
		query: function () {
			vm.reload();
		},

		reload: function (event) {
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
				postData:{
                    'phone': vm.q.phone,
                    'store_name': vm.q.store_name,
                },
                page:page
            }).trigger("reloadGrid");
		},
        exportAll: function (event) {
            location.href = baseURL + "sys/store/exportOpenDoorRecord";
        },
        getTotalPeople: function (){
            $.ajax({
                type: "GET",
                url: baseURL + "sys/store/getStorePeopleTotal",
                contentType: "application/json",
                success: function(result){
                    if(result.code === 0){
                        console.log('返回数据', result);
                        let total = result.total || 0;
                        $('#total').text(total);
                    }
                }
            });
        },

	}
});