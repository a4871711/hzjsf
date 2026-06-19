$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/pointsexchange/list',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'pointsExchangeId', index: 'pointsExchangeId', width: 50, key: true},
            {label: '用户昵称', name: 'nickname', index: 'nickname', width: 80},
            {label: '手机号', name: 'phone', index: 'phone', width: 80},
            {label: '所属门店', name: 'storeName', index: 'storeName', width: 80},
            {label: '兑换积分数', name: 'pointsCount', index: 'pointsCount', width: 80},
            {label: '兑换金额', name: 'exchangeMoney', index: 'exchangeMoney', width: 80,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            {
                label: '兑换时间', name: 'exchangeDate', index: 'exchangeDate', width: 80,
                formatter: function (value) {
                    return value == "" || value == null ? "" : formatDate(new Date(value))
                }
            }
        ],
        viewrecords: true,
        height: 385,
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
        pointsExchange: {},
        q: {
            nickname: "",
            phone: "",
            storeName: null
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.pointsExchange = {};
        },
        update: function (event) {
            var pointsExchangeId = getSelectedRow();
            if (pointsExchangeId == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(pointsExchangeId)
        },
        saveOrUpdate: function (event) {
            var url = vm.pointsExchange.pointsExchangeId == null ? "sys/pointsexchange/save" : "sys/pointsexchange/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.pointsExchange),
                success: function (result) {
                    if (result.code === 0) {
                        alert('操作成功', function (index) {
                            vm.reload();
                        });
                    } else {
                        alert(result.msg);
                    }
                }
            });
        },
        del: function (event) {
            var pointsExchangeIds = getSelectedRows();
            if (pointsExchangeIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/pointsexchange/delete",
                    contentType: "application/json",
                    data: JSON.stringify(pointsExchangeIds),
                    success: function (result) {
                        if (result.code == 0) {
                            alert('操作成功', function (index) {
                                $("#jqGrid").trigger("reloadGrid");
                            });
                        } else {
                            alert(result.msg);
                        }
                    }
                });
            });
        },
        getInfo: function (pointsExchangeId) {
            $.get(baseURL + "sys/pointsexchange/info/" + pointsExchangeId, function (result) {
                vm.pointsExchange = result.pointsExchange;
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page,
                postData: {"nickname": vm.q.nickname,"phone":vm.q.phone,"storeName":vm.q.storeName}
            }).trigger("reloadGrid");
        }
    }
});