$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/incomepaydetail/storedList',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'incomePayDetailId', index: 'incomePayDetailId', width: 50, key: true},
            /*	{ label: '用户ID', name: 'userId', index: 'userId', width: 80 }, */
            {label: '用户', name: 'userName', index: 'userName', width: 80},
            {label: '订单号', name: 'orderNo', index: 'orderNo', width: 80},
            /*   {label: '流水号', name: 'transactionNumber', index: 'transactionNumber', width: 80},*/
            {label: '金额数', name: 'money', index: 'money', width: 80},
           //{label: '订单详情', name: 'goodsName', index: 'goodsName', width: 80},

            /*    {label: '审核通过时间', name: 'checkedTime', index: 'checkedTime', width: 80},*/
            /*{label: '交易完成时间', name: 'transactionTime', index: 'transactionTime', width: 80},*/
           {label: '创建时间', name: 'createdDate', index: 'createdDate', width: 80}
        ],
        viewrecords: true,
        height: 'auto',
        rowNum: 10,
        rowList: [10, 30, 200],
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
        },
        loadComplete: function (data) {
            // console.log(data)
            //console.log(data.pageCountMoney)//为所有数据行，具体取决于reader配置的root或者服务器返回的内容
            $("#pageMoney").text(data.pageCountMoney);
        }
    });
    $.get(baseURL + 'sys/incomepaydetail/storedCountMoney', function (r) {
        $("#countMoney").text(r.countMoney);
    });
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        showDetail: true,
        title: null,
        orderDetail: [],
        incomePayDetail: {},
        q: {
            payType: "",
            tradeType: "",
            tradeStatus: ""
        }
    },
    methods: {
        query: function () {
            vm.q.sdate = $("#sdate").val();
            vm.q.edate = $("#edate").val();
            vm.reload();
        },
        lookOrderDetail: function () {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.showDetail = false;
            vm.title = "交易详情";
            vm.getDetail(id);
        },
        getDetail: function (value) {
            $.ajaxSettings.async = false;
            $.get(baseURL + "sys/incomepaydetail/orderInfo/" + value, function (result) {
                vm.orderDetail = result.orderDetail;
            });
        },

        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.incomePayDetail = {};
        },
        update: function (event) {
            var incomePayDetailId = getSelectedRow();
            if (incomePayDetailId == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(incomePayDetailId)
        },
        saveOrUpdate: function (event) {
            var url = vm.incomePayDetail.incomePayDetailId == null ? "sys/incomepaydetail/save" : "sys/incomepaydetail/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.incomePayDetail),
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
            var incomePayDetailIds = getSelectedRows();
            if (incomePayDetailIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/incomepaydetail/delete",
                    contentType: "application/json",
                    data: JSON.stringify(incomePayDetailIds),
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
        export: function (event) {
            var incomePayDetailIds = getSelectedRows();
            if (incomePayDetailIds == null) {
                return;
            }

            location.href = baseURL + "sys/incomepaydetail/export?incomePayDetailIds=" + incomePayDetailIds;
            /*    $.get(baseURL + "sys/incomepaydetail/export", JSON.stringify(incomePayDetailIds), function () {
                    $("#jqGrid").trigger("reloadGrid");
                });*/
            $.ajax({
                type: "POST",
                url: baseURL + "sys/incomepaydetail/export",
                data: JSON.stringify(incomePayDetailIds),
                success: function (r) {
                    alert('操作成功', function (index) {
                        $("#jqGrid").trigger("reloadGrid");
                    });
                }
            });
        },
        exportAll: function (event) {
            location.href = baseURL + "sys/incomepaydetail/export";
        },
        getInfo: function (incomePayDetailId) {
            $.get(baseURL + "sys/incomepaydetail/info/" + incomePayDetailId, function (r) {
                vm.incomePayDetail = r.incomePayDetail;
            });
        },
        reload: function (event) {
            vm.showList = true;
            vm.showDetail = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: vm.q,
                page: page
            }).trigger("reloadGrid");
        },
        validator: function () {

            if (isBlank(vm.incomePayDetail.userId)) {
                layer.msg("用户ID不能为空");
                return false;
            }
            if (isBlank(vm.incomePayDetail.orderNo)) {
                layer.msg("订单号不能为空");
                return false;
            }
            if (isBlank(vm.incomePayDetail.transactionNumber)) {
                layer.msg("流水号不能为空");
                return false;
            }
            if (isBlank(vm.incomePayDetail.payType)) {
                layer.msg("支付用途（1 提现 2.会员卡购买 3.商城购买 4.私教课购买 5.团体课购买 6.充值 7.广告消费 8.积分兑换 9.商城退款 10.其他）不能为空");
                return false;
            }
            if (isBlank(vm.incomePayDetail.money)) {
                layer.msg("金额数不能为空");
                return false;
            }
            if (isBlank(vm.incomePayDetail.tradeDate)) {
                layer.msg("收支时间不能为空");
                return false;
            }
            if (isBlank(vm.incomePayDetail.tradeType)) {
                layer.msg("收支方式(1:钱包 2：微信支付 3：支付宝支付)不能为空");
                return false;
            }
            if (isBlank(vm.incomePayDetail.openId)) {
                layer.msg("微信openId不能为空");
                return false;
            }
            if (isBlank(vm.incomePayDetail.tradeStatus)) {
                layer.msg("状态（1为审核中 2审核失败 3已完成  4提现中 5提现失败,6 充值失败）不能为空");
                return false;
            }
            if (isBlank(vm.incomePayDetail.checkedTime)) {
                layer.msg("审核通过时间不能为空");
                return false;
            }
            if (isBlank(vm.incomePayDetail.transactionTime)) {
                layer.msg("交易完成时间不能为空");
                return false;
            }
            if (isBlank(vm.incomePayDetail.createdDate)) {
                layer.msg("创建时间不能为空");
                return false;
            }
            return true;
        }
    }
});