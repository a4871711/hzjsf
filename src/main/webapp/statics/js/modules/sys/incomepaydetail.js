$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/incomepaydetail/list',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'incomePayDetailId', index: 'incomePayDetailId', width: 50, key: true},
            /*	{ label: '用户ID', name: 'userId', index: 'userId', width: 80 }, */
            {label: '用户', name: 'userName', index: 'userName', width: 80},
            {label: '手机号', name: 'phone', index: 'phone', width: 80},
            {label: '所属门店', name: 'storeName', index: 'storeName', width: 80},
            {
                label: '是否自动续费', name: 'renewSourceDesc', index: 'renewSourceDesc', width: 80,
                formatter: function (val) {
                    if (val === '自动') {
                        return '<span class="label label-success">自动</span>';
                    }
                    if (val === '用户') {
                        return '<span class="label label-info">用户</span>';
                    }
                    if (val === '后台') {
                        return '<span class="label label-warning">后台</span>';
                    }
                    return val == null || val === '' ? '--' : val;
                }
            },
            {label: '订单号', name: 'orderNo', index: 'orderNo', width: 80},
            /*   {label: '流水号', name: 'transactionNumber', index: 'transactionNumber', width: 80},*/
            {
                label: '支付用途',
                name: 'payType',
                index: 'payType',
                width: 80,
                formatter: function (val) {
                    if (val == 1) {
                        return "<label>提现</label>"
                    }
                    if (val == 2) {
                        return "<label>会员卡购买</label>"
                    } else if (val == 3) {
                        return "<label>商城购买</label>"
                    } else if (val == 4) {
                        return "<label>私教课购买</label>"
                    } else if (val == 5) {
                        return "<label>团体课购买</label>"
                    } else if (val == 6) {
                        return "<label>充值</label>"
                    } else if (val == 7) {
                        return "<label>门店消费</label>"
                    } else if (val == 8) {
                        return "<label>积分兑换</label>"
                    } else if (val == 9) {
                        return "<label>商城退款</label>"
                    } else if (val == 11) {
                        return "<label>提现失败退款</label>"
                    }else if (val == 12) {
                        return "<label>转卡交易</label>"
                    }else if (val == 13) {
                        return "<label>会员卡自动续费</label>"
                    } else {
                        return "<label>其他</label>"
                    }

                }
            },
            {label: '金额数', name: 'money', index: 'money', width: 80,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            {label: '收支时间', name: 'tradeDate', index: 'tradeDate', width: 80},
            {
                label: '支付方式', name: 'tradeType', index: 'tradeType', width: 80,
                formatter: function (val) {
                    if (val == 1) {
                        return "<label class='label label-warning'>钱包</label>"
                    } else if (val == 2) {
                        return "<label class='label label-success'>微信</label>"
                    } else if (val == 3) {
                        return "<label class='label label-info'>支付宝</label>"
                    }else {
                        return "<label class='label label-warning'>线下</label>"
                    }

                }
            },
            /* {label: '微信openId', name: 'openId', index: 'openId', width: 80},*/
            {
                label: '状态', name: 'tradeStatus', index: 'tradeStatus', width: 80,
                formatter: function (val) {
                    if (val == 3) {
                        return "<label class='label label-success'>已完成</label>"
                    } else if (val == 7) {
                        return "<label class='label label-danger'>已退款</label>"
                    } else {
                        return "<label class='label label-danger'>失败</label>"
                    }
                }
            },
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
            $("#jqGrid").setGridWidth($("#jqGrid").width());
        },
        loadComplete: function (data) {
            // console.log(data)
            //console.log(data.pageCountMoney)//为所有数据行，具体取决于reader配置的root或者服务器返回的内容
            $("#pageMoney").text(data.pageCountMoney);
        }
    });
    $.get(baseURL + 'sys/incomepaydetail/countMoney', function (r) {
        $("#countMoney").text(r.countMoney);
    });
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        incomePayDetail: {},
        q: {
            payType: "",
            tradeType: "",
            tradeStatus: "",
            storeName: null
        },
        orderInfo: {
            tradeStatus: "",
            orderNo: "",
        },
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
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: vm.q,
                page: page
            }).trigger("reloadGrid");
        },

        updateOrderStatus: function (){
            var id = getSelectedRow();
            console.log('选择了', id);
            if(id == null){return;}
            var rowData = $("#jqGrid").jqGrid('getRowData', id);
            if(rowData == null){
                layer.msg("请重新选择");
                return;
            }
            vm.orderInfo.incomePayDetailId = rowData.incomePayDetailId;
            vm.orderInfo.orderNo = rowData.orderNo;
            console.log('订单信息', vm.orderInfo );
            layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-molv',
                title: "更新订单状态",
                area: ['380px', '180px'],
                shadeClose: false,
                content: $("#updateStatusWindow"),
            });
        },
        //确认变更会员信息
        doUpdateOrderStatus: function (event) {
            console.log('变更会员信息', vm.orderInfo)
            confirm('确定更新订单状态？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/incomepaydetail/updateTradeStatus",
                    contentType: "application/json",
                    data: JSON.stringify(vm.orderInfo),
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
            });
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