$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/store/faceRecordlist',
        datatype: "json",
        colModel: [			
			{ label: '刷脸编号', name: 'firId', width: 50, key: true},
			{ label: '用户昵称', name: 'nickname', width: 50 },
			{ label: '手机号', name: 'phone', width: 60 },
			{ label: '刷脸设备编号', name: 'deviceNo', width: 80 },
			{ label: '刷脸时间', name: 'createTime', width: 60,formatter:function (value) {
                return value == "" || value == null ? "" : formatDate(new Date(value))
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
			phone: null
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
		}
	}
});