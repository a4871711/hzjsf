$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/coachtradedetail/list',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'id', index: 'id', width: 50, key: true},
            {label: '教练', name: 'coachName', index: 'coachName', width: 80},
            {label: '手机号', name: 'phone', index: 'phone', width: 80},
            {
                label: '交易类型', name: 'tradeType', index: 'tradeType', width: 80,
                formatter: function (val) {
                    if (val == 1) {
                        return "课程回款"
                    } else if (val == 2) {
                        return "课程入账退款"
                    }
                }
            },
            {label: '课程原价', name: 'origMoney', index: 'origMoney', width: 60,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            {label: '提成比例(%)', name: 'percent', index: 'percent', width: 60,
                formatter: function (value) {
                    return value == null? "--" : value+"%"
                }
            },
            {label: '实收款', name: 'money', index: 'money', width: 80,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            /* {label: '提现到的支付宝号', name: 'aliAccount', index: 'AliAccount', width: 80},*/
            /* {label: '提现到的微信号', name: 'wxCount', index: 'wxCount', width: 80},*/
            {label: '流水号', name: 'transactionNumber', index: 'transactionNumber', width: 80},
            /*{label: '失败原因', name: 'reason', index: 'reason', width: 80},*/
            {label: '订单号', name: 'orderNo', index: 'orderNo', width: 80},
            {
                label: '状态', name: 'status', index: 'status', width: 80,
                formatter: function (val) {
                    if (val == 0) {
                        return "<label class='label label-warning'>进行中</label>"
                    }
                    else if (val == 1) {
                        return "<label class='label label-success'>已完成</label>"
                    }
                    else if (val == 2) {
                        return "<label class='label label-danger'>交易失败</label>"
                    }
                }
            },
            /*{ label: '审核通过时间', name: 'checkedTime', index: 'checkedTime', width: 80 },
            { label: '交易完成时间', name: 'transactionTime', index: 'transactionTime', width: 80 }, 	*/
            {label: '创建时间', name: 'createTime', index: 'createTime', width: 80}
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
            $("#jqGrid").setGridWidth($("#jqGrid").width());
        }
    });
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        coachTradeDetail: {},
        q:{
            status:""
        }
    },
    methods: {
        query: function () {
            vm.q.sdate = $("#sdate").val();
            vm.q.edate = $("#edate").val();
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.coachTradeDetail = {};
        },
        update: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(id)
        },
        saveOrUpdate: function (event) {
            if (!vm.validator()) {
                return;
            }
            var url = vm.coachTradeDetail.id == null ? "sys/coachtradedetail/save" : "sys/coachtradedetail/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.coachTradeDetail),
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
            var ids = getSelectedRows();
            if (ids == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/coachtradedetail/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
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
        getInfo: function (id) {
            $.get(baseURL + "sys/coachtradedetail/info/" + id, function (r) {
                vm.coachTradeDetail = r.coachTradeDetail;
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData:vm.q,
                page: page
            }).trigger("reloadGrid");
        }
    }
});