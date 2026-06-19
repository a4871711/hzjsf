$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/storeTeamClass/list',
        datatype: "json",
        colModel: [			
			{ label: '编号', name: 'teamClassId', index: 'stuTeamClassId', width: 50, key: true },
			{ label: '门店名称', name: 'storeName', index: 'storeName', width: 80},
            { label: '课程名称', name: 'teamClassName', index: 'teamClassName', width: 80},
            { label: '课程类型', name: 'teamClassType', index: 'teamClassType', width: 80,
                formatter: function(value){
                    if(value == 1){
                        return '<span class="label label-warning">精品</span>';
                    }else{
                        return '<span class="label label-success">免费</span>';
                    }
                }
            },
            { label: '课程价格(单位：元)', name: 'classPrice', index: 'classPrice', width: 80 },
            { label: '报名总人数', name: 'totalNum', index: 'totalNum', width: 80 },
			{ label: '教练名称', name: 'coachName', index: 'coachName', width: 80 },
			{ label: '上课时间', name: 'classTime', index: 'classTime', width: 80 },
			{ label: '上课地点', name: 'place', index: 'place', width: 80 },
			{ label: '消耗能量/kCal', name: 'energy', index: 'energy', width: 50 },
			{ label: '课程状态', name: 'classStatus', index: 'classStatus', width: 80,
                formatter: function(value){
			        if(value == 0){
                        return '<span class="label label-danger">待上课</span>';
                    }else{
                        return '<span class="label label-danger">已完成</span>';
                    }

                }
            }
        ],
		viewrecords: true,
        height: 'auto',
        rowNum: 10,
		rowList : [10,30,50,200,500,2000,99999999],
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
var id = null;
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		list: {},
        stuDetail: [],
        status:null,
        qq: {},
        q:{
            teamClassId:null,
            teamClassName:null,
            classStatus:0
        }
},
	methods: {
		query: function () {
			vm.reload();
		},
        queryM: function () {
            vm.reload2();
        },
        endClass:function(){
            var stuTeamClassId = getSelectedRows();
            if(stuTeamClassId == null){
                return ;
            }

            confirm('是否确认结束当前课程？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/storeTeamClass/updateEnd",
                    contentType: "application/json",
                    data: JSON.stringify(stuTeamClassId),
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

		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page,
                postData:{'teamClassId':vm.q.teamClassId,'teamClassName':vm.q.teamClassName,"classStatus": vm.q.classStatus }
            }).trigger("reloadGrid");
		},
        reload2: function (event) {
            var page = $("#jqGridAdd").jqGrid('getGridParam', 'stuDetailPage');
            $("#jqGridAdd").jqGrid('setGridParam', {
                page: page,
                postData: vm.qq
            }).trigger("reloadGrid");

        },
        //签到
        lookStuDetail: function (event) {
            //签到
            var id = getSelectedRow();
            if(id == null){return;}

            var a = layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-molv',
                title: "学员列表",
                area: ['750px', '500px'],
                shadeClose: false,
                content: $("#signClass")
            });
            vm.initJQqrid(id);
        },
        //确认签到
        signIn: function (event) {
            var stuTeamClassIds = getSelectedWindowRows();
            if(stuTeamClassIds == null){return;}
            for(var i=0; i<stuTeamClassIds.length; i++){
                var rowData = $("#jqGridAdd").jqGrid('getRowData',stuTeamClassIds[i]);
                if(rowData.classStatus == 1){
                    layer.msg("'已签到'会员不可重复此操作");
                    return;
                }
            }

            confirm('是否确认已签到？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/storeTeamClass/signInStu",
                    contentType: "application/json",
                    data: JSON.stringify(stuTeamClassIds),
                    success: function(result){
                        if(result.code == 0){
                            alert('操作成功', function(index){
                                vm.reload2();
                                vm.reload();
                            });
                        }else{
                            alert(result.msg);
                        }
                        vm.reload();
                    }
                });
            });
        },
        initJQqrid: function (id) {
            $("#jqGridAdd").jqGrid({
                url: baseURL + 'sys/storeTeamClass/info',
                datatype: "json",
                colModel: [
                    {label: '报名编号', name: 'stuTeamClassId', index: 'stuTeamClassId', width: 10, key: true},
                    {label: '学员昵称', name: 'nickname', index: 'nickname', width: 30},
                    {label: '手机号', name: 'phone', index: 'phone', width: 30},
                    {label: '状态', name: 'classStatus', index: 'classStatus', width: 30,
                        formatter:function (value) {
                            switch (value){
                                case 0: return '<span class="label label-info">未签到</span>';
                                case 1: return '<span class="label label-success">已签到</span>';
                            }
                        }
                    },
                    {label: '', name: 'classStatus', index: 'classStatus', width: 5, hidden: true}
                ],

                viewrecords: true,
                height: 'auto',
                rowNum: 10,
                rowList : [10,30,50,200,500,2000,99999999],
                rownumbers: true,
                rownumWidth: 25,
                autowidth: true,
                multiselect: true,
                postData:{'classId': id},
                pager: "#jqGridPagerAdd",
                jsonReader: {
                    root: "stuDetailPage.list",
                    page: "stuDetailPage.currPage",
                    total: "stuDetailPage.totalPage",
                    records: "stuDetailPage.totalCount"
                },
                prmNames : {
                    page:"page",
                    rows:"limit",
                    order: "order"
                },
                gridComplete: function () {
                    //隐藏grid底部滚动条
                    $("#jqGridAdd").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
                    $("#jqGridAdd").setGridWidth($("#signClass").width());
                }

            });
            var page = $("#jqGridAdd").jqGrid('getGridParam', 'page');
            $("#jqGridAdd").jqGrid('setGridParam', {
                page: page,
                postData:{"classId":id}
            }).trigger("reloadGrid");
            for (var i = 0, len = 10; i < len; i++) {
                $("#jqGridAdd").jqGrid('addRowData', i + 1, i + 1);
            }
        },
	}
});

$("#signClass").hide();