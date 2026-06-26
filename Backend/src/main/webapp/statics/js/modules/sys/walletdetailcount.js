$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/walletdetailcount/list',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'id', index: 'id', width: 50, key: true},
           /* {label: '用户ID', name: 'userId', index: 'userId', width: 80},*/
            {label: '用户', name: 'realName', index: 'realName', width: 80},
            {label: '手机号', name: 'phone', index: 'phone', width: 80},
            {
                label: '明细类型', name: 'type', index: 'type', width: 80,
                formatter: function (val) {
                    if (val == 1) {
                        return "<label>用户提现</label>"
                    } else if (val == 2) {
                        return "<label>教练提现</label>"
                    }
                }
            },
            {label: '金额数', name: 'money', index: 'money', width: 80,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            {label: '提现的支付宝账号', name: 'aliAccount', index: 'AliAccount', width: 80},
           /* {label: '提现到的微信号', name: 'wxCount', index: 'wxCount', width: 80},*/
            /*{label: '流水号', name: 'transactionNumber', index: 'transactionNumber', width: 80},*/
         /*   {label: '失败原因', name: 'reason', index: 'reason', width: 80},*/
            {label: '提现订单号', name: 'orderNo', index: 'orderNo', width: 80},
            {
                label: '状态', name: 'status', index: 'status', width: 80,
                formatter: function (val) {
                    if (val == 1) {
                        return "<label>审核中</label>"
                    } else if (val == 2) {
                        return "<label>审核失败</label>"
                    } else if (val == 3) {
                        return "<label>已完成</label>"
                    }
                    else if (val == 4) {
                        return "<label>提现中</label>"
                    }
                    else if (val == 5) {
                        return "<label>提现失败</label>"
                    }
                }
            },
          /*  {label: '微信openId', name: 'openId', index: 'openId', width: 80},*/
            {label: '审核通过时间', name: 'checkedTime', index: 'checkedTime', width: 80},
          /*  {label: '交易完成时间', name: 'transactionTime', index: 'transactionTime', width: 80},*/
         /*   {label: '创建时间', name: 'createTime', index: 'createTime', width: 80}*/
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
        q:{
            status:"",
            type:""
        }
    },
    methods: {
        query: function () {
            vm.q.sdate=$("#sdate").val();
            vm.q.edate=$("#edate").val();
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.walletDetail = {};
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
            var url = vm.walletDetail.id == null ? "sys/walletdetail/save" : "sys/walletdetail/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.walletDetail),
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
                    url: baseURL + "sys/walletdetail/delete",
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
            $.get(baseURL + "sys/walletdetail/info/" + id, function (r) {
                vm.walletDetail = r.walletDetail;
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData:vm.q,
                page: page
            }).trigger("reloadGrid");
        },
        validator: function () {

            if (isBlank(vm.walletDetail.userId)) {
                layer.msg("用户ID不能为空");
                return false;
            }
            if (isBlank(vm.walletDetail.type)) {
                layer.msg("明细类型（1.(用户) 提现 2.（教练）提现 3.(用户）充值）不能为空");
                return false;
            }
            if (isBlank(vm.walletDetail.money)) {
                layer.msg("金额数不能为空");
                return false;
            }
            if (isBlank(vm.walletDetail.aliAccount)) {
                layer.msg("提现到的支付宝号不能为空");
                return false;
            }
            if (isBlank(vm.walletDetail.wxCount)) {
                layer.msg("提现到的微信号不能为空");
                return false;
            }
            if (isBlank(vm.walletDetail.transactionNumber)) {
                layer.msg("流水号不能为空");
                return false;
            }
            if (isBlank(vm.walletDetail.reason)) {
                layer.msg("失败原因不能为空");
                return false;
            }
            if (isBlank(vm.walletDetail.orderNo)) {
                layer.msg("提现,充值订单号不能为空");
                return false;
            }
            if (isBlank(vm.walletDetail.status)) {
                layer.msg("状态（1为审核中 2审核失败 3已完成  4提现中 5提现失败,6 充值失败 0：充值中）不能为空");
                return false;
            }
            if (isBlank(vm.walletDetail.openId)) {
                layer.msg("微信openId不能为空");
                return false;
            }
            if (isBlank(vm.walletDetail.checkedTime)) {
                layer.msg("审核通过时间不能为空");
                return false;
            }
            if (isBlank(vm.walletDetail.transactionTime)) {
                layer.msg("交易完成时间不能为空");
                return false;
            }
            if (isBlank(vm.walletDetail.createTime)) {
                layer.msg("创建时间不能为空");
                return false;
            }
            return true;
        }
    }
});