$(function () {

    jQuery("#jqGrid").setGridParam().showCol("storeName").trigger("reloadGrid");
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/storedevice/list',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'sdId', index: 'sdId', width: 50, key: true},
            /*{ label: '门店id', name: 'storeId', index: 'storeId', width: 80 }, */
            {label: '门店', name: 'storeName', index: 'storeName', width: 60},
            {label: '设备名称', name: 'deviceName', index: 'deviceName', width: 60},
            {label: '设备编号', name: 'deviceNo', index: 'deviceNo', width: 80},
            {label: '创建时间', name: 'createTime', index: 'createTime', width: 60},
            {label: '相关操作', name: 'sdId', index: 'sdId', width: 60,
                formatter: function (value) {
                    return '<a href="javascript:void(0)" onclick="vm.getFaceRecord(' +value+ ')">设备刷脸记录</a>';
                }
            }
        ],
        viewrecords: true,
        height: 'auto',
        rowNum: 10,
        rowList: [10, 30, 50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth: true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader: {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames: {
            page: "page",
            rows: "limit",
            order: "order"
        },
        loadComplete: function (data) {

        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });
    $.getJSON(baseURL + '/sys/user/info', function (r) {
        vm.user = r.user;
        if (r.user.storeId != null && r.user.storeId != "") {
            //jQuery("#jqGrid").setGridParam().hideCol("storeName").trigger("reloadGrid");
            showHideCol("storeName");
        } else {
            $(".operate").hide();
        }
    });

    function showHideCol(e) {
        var colModel = $("#jqGrid").jqGrid('getGridParam', 'colModel');
        var width = 0;//获取当前列的列宽
        for (var i = 0; i < colModel.length; i++) {
            if (colModel[i]["name"] ==e) {
                width = colModel[i]["width"];
                break;
            }
        }
        $("#jqGrid").setGridWidth($("#jqGrid").getGridParam("width") + width);
        $("#jqGrid").setGridParam().hideCol(e).trigger("reloadGrid");


    }
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        storeDevice: {},
        user: {},
        qq: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        queryM: function () {
            console.log("quM");
            vm.reload2();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.storeDevice = {};
        },
        update: function (event) {
            var sdId = getSelectedRow();
            if (sdId == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(sdId)
        },
        saveOrUpdate: function (event) {
            if (!vm.validator()) {
                return;
            }
            var url = vm.storeDevice.sdId == null ? "sys/storedevice/save" : "sys/storedevice/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.storeDevice),
                success: function (r) {
                    if (r.code === 0) {
                        alert('操作成功', function (index) {
                            vm.reload();
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        del: function (event) {
            var sdIds = getSelectedRows();
            if (sdIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/storedevice/delete",
                    contentType: "application/json",
                    data: JSON.stringify(sdIds),
                    success: function (r) {
                        if (r.code == 0) {
                            alert('操作成功', function (index) {
                                $("#jqGrid").trigger("reloadGrid");
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        getInfo: function (sdId) {
            $.get(baseURL + "sys/storedevice/info/" + sdId, function (r) {
                vm.storeDevice = r.storeDevice;
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page
            }).trigger("reloadGrid");
        },
        validator: function () {

            if (isBlank(vm.storeDevice.deviceName)) {
                layer.msg("设备名称不能为空");
                return false;
            }
            if (isBlank(vm.storeDevice.deviceNo)) {
                layer.msg("设备编号不能为空");
                return false;
            }

            return true;
        },
        reload2: function (event) {
            console.log("reloas2")
            var page = $("#jqGridAdd").jqGrid('getGridParam', 'facePage');
            $("#jqGridAdd").jqGrid('setGridParam', {
                page: page,
                postData: vm.qq
            }).trigger("reloadGrid");

        },
        //查询该设备下的刷脸记录
        getFaceRecord: function (value) {
            var rowData = $("#jqGrid").jqGrid('getRowData',value);
            var deviceNo = rowData.deviceNo;
            if(isBlank(deviceNo)){layer.msg("暂无设备编号");return;}
            var a = layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-molv',
                title: "设备刷脸记录",
                area: ['750px', '500px'],
                shadeClose: false,
                content: $("#faceRecords")
            });
            vm.initJQqrid(deviceNo);
        },
        initJQqrid: function (value) {
            $("#jqGridAdd").jqGrid({
                url: baseURL + 'sys/store/faceRecordByNolist',
                datatype: "json",
                colModel: [
                    { label: '刷脸编号', name: 'firId', width: 50, key: true},
                    { label: '用户昵称', name: 'nickname', width: 50 },
                    { label: '手机号', name: 'phone', width: 60 },
                    { label: '刷脸时间', name: 'createTime', width: 60,formatter:function (value) {
                        return value == "" || value == null ? "" : formatDate(new Date(value))
                    }}
                ],

                viewrecords: true,
                height: 'auto',
                rowNum: 10,
                rowList : [10,30,50,200,500,2000],
                rownumbers: true,
                rownumWidth: 25,
                autowidth: true,
                multiselect: true,
                postData:{'deviceNo': value},
                pager: "#jqGridPagerAdd",
                jsonReader: {
                    root: "facePage.list",
                    page: "facePage.currPage",
                    total: "facePage.totalPage",
                    records: "facePage.totalCount"
                },

                prmNames : {
                    page:"page",
                    rows:"limit",
                    order: "order"
                },
                gridComplete: function () {
                    //隐藏grid底部滚动条
                    $("#jqGridAdd").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
                    $("#jqGridAdd").setGridWidth($("#faceRecords").width());
                }

            });
            var page = $("#jqGridAdd").jqGrid('getGridParam', 'page');
            $("#jqGridAdd").jqGrid('setGridParam', {
                page: page,
                postData:{"deviceNo":value}
            }).trigger("reloadGrid");

            for (var i = 0, len = 10; i < len; i++) {
                $("#jqGridAdd").jqGrid('addRowData', i + 1, i + 1);
            }
        },
        
    }
});


$("#faceRecords").hide();