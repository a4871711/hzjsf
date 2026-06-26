$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/storeCoach/list',
        datatype: "json",
        colModel: [
            {label: '编号', name: 'cpsId', index: 'cpsId', width: 50, key: true},
            {label: '服务门店', name: 'storeName', index: 'storeName', width: 80},
            {label: '教练名称', name: 'coachName', index: 'coachName', width: 80},
            {label: '教练电话', name: 'coachPhone', index: 'coachPhone', width: 80},
            {
                label: '性别', name: 'sex', index: 'sex', width: 80,
                formatter: function (value) {
                    if (value == 1) {
                        return '男';
                    } else if (value == 2) {
                        return '女';
                    } else {
                        return '未知';
                    }
                }
            },
            {
                label: '审核状态', name: 'grade', index: 'grade', width: 50,
                formatter: function (value) {
                    if (value == 1) {
                        return '<span class="label label-success">通过</span>';
                    } else if (value == 0) {
                        return '<span class="label label-info">待审核</span>';
                    } else {
                        return '<span class="label label-danger">失败</span>';
                    }

                }
            },
            {
                label: '审核状态', name: 'grade', index: 'grade', width: 50 ,hidden:true},
            {
                label: '申请时间', name: 'createdDate', index: 'createdDate', width: 50,
                formatter: function (value) {
                    return value == "" || value == null ? "" : formatDate(new Date(value))
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
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        list: {},
        status: null,
        q: {
            coachName: null,
            storeName: null,
            coachPhone: null
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            /*vm.showList = false;
            vm.title = "新增";
            vm.walletDetail = {};*/
            //window.href="./modules/sys/paramset.html";
            window.open(baseURL + "index.html#modules/sys/paramset.html");
        },
        update: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            console.log(id);
            //vm.getInfo(id)

            if (vm.status != 1) {
                alert("请选择审核中的记录")
                return;
            }
            vm.showList = false;
            vm.title = "修改";
        },
        closeAllLayer: function () {
            layer.closeAll();
        },

        pass: function () {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            var key = $("#jqGrid").jqGrid('getGridParam','selrow');
            var rowData = $("#jqGrid").jqGrid("getRowData", key);

            var grade = rowData.grade;
            if (grade != 0) {
                layer.msg("只有待审核的才可以审核通过");
                return;
            }
            confirm('是否确定通过审核,确定后请及时联系该教练告知审核结果？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/storeCoach/update?cpsId=" + id + "&grade=1",
                    contentType: "application/json",
                    dataType: "json",
                    success: function (result) {
                        if (result.code == 0) {
                            alert('操作成功', function (index) {
                                $("#jqGrid").trigger("reloadGrid");
                            });
                        } else {
                            alert(result.msg);
                        }
                        vm.reload();
                    }
                });
            });

        },

        unPass: function () {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            var key = $("#jqGrid").jqGrid('getGridParam','selrow');
            var rowData = $("#jqGrid").jqGrid("getRowData", key);

            var grade = rowData.grade;
            if (grade != 0) {
                layer.msg("只有待审核的才可以审核失败");
                return;
            }
            confirm('是否确定审核不通过,确定后请及时联系该教练告知审核结果？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/storeCoach/update?cpsId=" + id + "&grade=2",
                    contentType: "application/json",
                    dataType: "json",
                    success: function (result) {
                        if (result.code == 0) {
                            alert('操作成功', function (index) {
                                $("#jqGrid").trigger("reloadGrid");
                            });
                        } else {
                            alert(result.msg);
                        }
                        vm.reload();
                    }
                });
            });

        },

        getInfo: function (id) {
            $.ajaxSettings.async = false;
            $.get(baseURL + "sys/walletdetail/info/" + id, function (result) {
                vm.walletDetail = result.walletDetail;
                vm.status = result.walletDetail.status;


                console.log(result.walletDetail);
                console.log(vm.walletDetail);
                // vm.walletDetail.status = 2;
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page,
                postData: {'storeName': vm.q.storeName,'coachName': vm.q.coachName, "coachPhone": vm.q.coachPhone}
            }).trigger("reloadGrid");
        }
    }
});