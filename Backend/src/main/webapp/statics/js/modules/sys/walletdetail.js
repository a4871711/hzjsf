$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/walletdetail/list',
        datatype: "json",
        colModel: [
            {label: 'id', name: 'id', index: 'id', width: 50, key: true},
            /*    {label: '用户账号', name: 'phone', index: 'phone', width: 80},*/
            {
                label: '用户类型', name: 'type', index: 'type', width: 80,
                formatter: function (value) {
                    if (value == 1) {
                        return '普通用户';
                    } else if (value == 2) {
                        return '教练';
                    }
                }
            },
            {label: '姓名', name: 'realName', index: 'realName', width: 80},
            {label: '电话', name: 'phone', index: 'phone', width: 80},
            /*{ label: '明细类型（1 提现 2销售收入 3充值 4广告消费）', name: 'type', index: 'type', width: 80 }, 		*/
            {
                label: '金额数', name: 'money', index: 'money', width: 80,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            /*	{ label: '提现到的微信号', name: 'wxCount', index: 'wxCount', width: 80 }, 			*/
            /*	{ label: '流水号', name: 'transactionNumber', index: 'transactionNumber', width: 80 },*/
            {label: '提现订单号', name: 'orderNo', index: 'orderNo', width: 80},
            {label: '转入账号', name: 'aliAccount', index: 'aliAccount', width: 80},
            {label: '失败原因', name: 'reason', index: 'reason', width: 80},
            {
                label: '状态', name: 'status', index: 'status', width: 80,
                formatter: function (value) {
                    if (value == 1) {
                        return '<span class="label label-info">审核中</span>';
                    } else if (value == 2) {
                        return '<span class="label label-danger">审核失败</span>';
                    } else if (value == 3) {
                        return '<span class="label label-success">已完成</span>';
                    } else if (value == 4) {
                        return '<span class="label label-info">提现中</span>';
                    } else if (value == 5) {
                        return '<span class="label label-info">提现失败</span>';
                    }

                }
            },

            {
                label: '申请时间', name: 'createTime', index: 'createTime', width: 80,
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
        walletDetail: {},
        q: {
            aliAccount: null,
            status: null,
            phone: null
        },
        status: null
    },
    methods: {
        query: function () {
            vm.q.sdate = $("#sdate").val();
            vm.q.edate = $("#edate").val();
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
            vm.getInfo(id)

            if (vm.status != 1) {
                alert("请选择审核中的记录")
                return;
            }
            vm.showList = false;
            vm.title = "修改";


        },
        pay: function () {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }

            confirm('确定要通过选中的提现记录吗？,通过后需要将资金人工转入支付宝账号', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/walletdetail/payToUser",
                    contentType: "application/json",
                    data: JSON.stringify(vm.walletDetail),
                    success: function (result) {
                        if (result.code == 0) {
                            alert('操作成功', function (index) {
                                $.getJSON(baseURL + "sys/message/list", function (r) {
                                    console.log(r.page.length);
                                    console.log(window.parent.document.getElementById("newMsg"))
                                    window.parent.document.getElementById("newMsg").innerHTML = "您有" + r.page.length + "条待审核消息";

                                });
                                $("#jqGrid").trigger("reloadGrid");

                            });
                        } else {
                            alert(result.msg);
                        }
                    }
                });
            });


            vm.getInfo(id)
        },

        saveOrUpdate: function (event) {
            if (isBlank(vm.walletDetail.reason)) {
                layer.msg("请填写失败原因")
                return;
            }

            var url = "sys/walletdetail/update";
            confirm('审核失败后资金将原路返回到用户钱包', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + url,
                    contentType: "application/json",
                    data: JSON.stringify(vm.walletDetail),
                    success: function (result) {
                        if (result.code === 0) {
                            alert('操作成功', function (index) {
                                $.getJSON(baseURL + "sys/message/list", function (r) {
                                    console.log(r.page.length);
                                    console.log(window.parent.document.getElementById("newMsg"))
                                    window.parent.document.getElementById("newMsg").innerHTML = "您有" + r.page.length + "条待审核消息";

                                });
                                vm.reload();
                            });
                        } else {
                            alert(result.msg);
                        }
                    }
                });
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
                    url: baseURL + "sys/walletdetail/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
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
                postData: {
                    'aliAccount': vm.q.aliAccount,
                    "status": vm.q.status,
                    "phone": vm.q.phone,
                    "sdate": vm.q.sdate,
                    "edate": vm.q.edate
                }
            }).trigger("reloadGrid");
        }
    }
});