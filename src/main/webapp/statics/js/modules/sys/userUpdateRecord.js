$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/user/validityRecordList',
        datatype: "json",
        colModel: [			
			/*{ label: 'id', name: 'id', width: 30, key: true },*/
			{ label: '编号', name: 'uprId', width: 30, key: true },
			{ label: '用户昵称', name: 'nickname', width: 50 },
			{ label: '手机', name: 'phone', width: 50 },
			{ label: '所属门店', name: 'storeName', width: 50 },
            { label: '有效期（旧）', name: 'oldValidityDate', width: 60 },
            { label: '有效期（新）', name: 'newValidityDate', width: 60 },
			{ label: '操作时间', name: 'createdDate', width: 90,
                formatter:function (value) {
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