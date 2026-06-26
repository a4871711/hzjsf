$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/store/deviceV2',
        datatype: "json",
        colModel: [
            { label: 'ID', name: 'id', hidden: true, key: true},
            { label: '设备编号', name: 'deviceId'},
			{ label: '设备名称', name: 'deviceName'},
            { label: '门店', name: 'storeName'},
            { label: '来源', name: 'source', formatter: function (value){
                let source = '--';
                if(value > 300 && value < 400){
                    source = '力动';
                }
                return source;
                } },
            { label: '设备状态', name: 'state', width: 80, formatter: function (value){
                    return value ? '<strong style="color: #00a65a;">启动</strong>' : '<span>待机</span>';
                } },
			{ label: '创建时间', name: 'createTime', formatter:function (value) {
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
			deviceId: null
		},
        device: {},
	},
	methods: {
		query: function () {
			vm.reload();
		},
		reload: function (event) {
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
				postData:{'deviceId': vm.q.deviceId},
                page:page
            }).trigger("reloadGrid");
		},

        //添加设备信息
        addDevice: function (event) {
            vm.device = {};   //清空上次记录
            vm.device.id = 0;
            layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-molv',
                title: "添加设备信息",
                area: ['380px', '300px'],
                shadeClose: false,
                content: $("#dialogDevice"),
            });
        },

        //更新设备信息
        updateDevice: function (event) {
            var id = getSelectedRow();
            console.log('选择了', id);
            if(id == null){return;}
            vm.device = {};   //清空上次记录
            var rowData = $("#jqGrid").jqGrid('getRowData', id);
            if(rowData == null){
                layer.msg("请重新选择");
                return;
            }
            //赋值更新对象
            vm.device.id = rowData.id;
            vm.device.deviceId = rowData.deviceId;
            vm.device.deviceName = rowData.deviceName;
            vm.device.source = rowData.source;
            layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-molv',
                title: "更新设备信息",
                area: ['380px', '300px'],
                shadeClose: false,
                content: $("#dialogDevice"),
            });
        },

        //确认变更会员信息
        doUpdateDevice: function (event) {
            $.ajax({
                type: "POST",
                url: baseURL + "sys/store/updateDevice",
                contentType: "application/json",
                data: JSON.stringify(vm.device),
                success: function(result){
                    if(result.code === 0){
                        alert('操作成功', function(index){
                            vm.reload();
                            layer.closeAll();
                        });
                    }else{
                        alert(result.msg);
                    }
                }
            });
        },

	}
});