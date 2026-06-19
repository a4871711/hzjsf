$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/goodsOrder/list',
        datatype: "json",
        colModel: [
            {label: '编号', name: 'goodsOrderId', index: 'arrangeClassId', width: 50, key: true},
            {
                label: '订单号', name: 'orderNo', index: 'orderNo', width: 90
                /*formatter: function (value) {
                    return '<a href="javascript:void(0)" onclick="vm.lookDetail('+ value +')"> ' + value + ' </a>'
                }*/
            },
            {label: '商品名称', name: 'goodsName', index: 'goodsName', width: 50},
            {label: '收货人姓名', name: 'receiveName', index: 'storeName', width: 50},
            {label: '收货人电话', name: 'receivePhone', index: 'className', width: 80},
            {label: '收货详细地址', name: 'receiveAddr', index: 'nickname', width: 80},
            {label: '商品总金额', name: 'goodsSum', index: 'goodsSum', width: 40,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            {label: '优惠金额', name: 'couponMoney', index: 'couponMoney', width: 40,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            {label: '运费', name: 'fare', index: 'fare', width: 40,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            {label: '实际付款', name: 'realPayment', index: 'realPayment', width: 40,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            {label: '物流公司', name: 'logisticsName', index: 'logisticsName', width: 80},
            {label: '物流单号', name: 'logisticsNo', index: 'logisticsNo', width: 80},
            {
                label: '交易日期', name: 'createdDate', index: 'createdDate', width: 50,
                formatter: function (value) {
                    return value == "" || value == null ? "" : formatDate(new Date(value))
                }
            },
            {
                label: '发货时间', name: 'deliveryTime', index: 'deliveryTime', width: 50,
                formatter: function (value) {
                    return value == "" || value == null ? "" : formatDate(new Date(value))
                }
            },
            {
                label: '订单状态', name: 'status', index: 'status', width: 40,
                formatter: function (value) {
                    if (value == 0) {
                        return '<span class="label label-danger">待付款</span>';
                    } else if (value == 1) {
                        return '<span class="label label-danger">待发货</span>';
                    } else if (value == 2) {
                        return '<span class="label label-danger">待收货</span>';
                    } else if (value == 3) {
                        return '<span class="label label-danger">已完成</span>';
                    } else if (value == 4) {
                        return '<span class="label label-danger">已取消</span>';
                    } else if (value == 5) {
                        return '<span class="label label-danger">已评价</span>';
                    } else if (value == 6) {
                        return '<span class="label label-danger">已退款</span>';
                    } else {
                        return '<span class="label label-danger"></span>';
                    }


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
        showDetail: true,
        title: null,
        list: {},
        orderDetail: [],
        orderDetailZhu: {},
        myOrder: {
            goodsName: null,
            size: null,
            goodsNum: 0

        },
        lg: {
            id: null,
            logisticsName: null,
            logisticsNo: null
        },
        status: null,
        q: {
            orderNo: null,
            receiveName: null,
            receivePhone: null,
            status: 1
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
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
            //vm.displayLogistics(id);
            vm.list.goodsOrderId = id;
            layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-molv',
                title: "添加",
                area: ['700px', '320px'],
                shade: 0,
                shadeClose: false,
                content: $("#anewFileWindow"),
            });
        },
        closeAllLayer: function () {
            layer.closeAll();
        },
        saveLogistics: function () {
            if (vm.validata()) {
                return;
            }
            vm.list.status = 2;   //改成待收货
            confirm('是否确认发货？请确保物流单号正确无误,提交后将不可修改', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/goodsOrder/update",
                    contentType: "application/json",
                    data: JSON.stringify(vm.list),
                    success: function (result) {
                        if (result.code == 0) {
                            alert('操作成功', function (index) {
                                $("#jqGrid").trigger("reloadGrid");
                                vm.closeAllLayer();
                            });
                        } else {
                            alert(result.msg);
                        }
                        vm.reload();
                    }
                });
            });

        },
        lookDetail: function () {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            vm.orderDetailZhu = $("#jqGrid").jqGrid('getRowData', id);
            vm.showList = false;
            vm.showDetail = false;
            vm.title = "订单详情";
            vm.getInfo(id);

        },

        getInfo: function (value) {
            $.ajaxSettings.async = false;
            $.get(baseURL + "sys/goodsOrder/info/" + value, function (result) {
                vm.orderDetail = result.orderDetail;
                console.log(result.orderDetail);
                console.log(vm.orderDetail);
                // vm.walletDetail.status = 2;
            });
        },

        /*getInfo: function(id){
            $.ajaxSettings.async = false;
            $.get(baseURL + "sys/goodsOrder/info/"+id, function(result){
                vm.walletDetail = result.walletDetail;
                vm.status = result.walletDetail.status;
                console.log(result.walletDetail);
                console.log(vm.walletDetail);
               // vm.walletDetail.status = 2;
            });
        },*/
        reload: function (event) {
            vm.showList = true;
            vm.showDetail = true;
            vm.orderDetailZhu = {};
            $("#evaluate").hide();
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page,
                postData: {
                    'orderNo': vm.q.orderNo, 'receiveName': vm.q.receiveName, "receivePhone": vm.q.receivePhone,
                    "status": vm.q.status
                }
            }).trigger("reloadGrid");
        },
        validata: function () {
            var re = /^[0-9]\d*$/;
            if (isBlank(vm.list.logisticsName)) {
                alert("物流公司名称不能为空");
                return true;
            }
            if (isBlank(vm.list.logisticsNo)) {
                alert("物流编号不能为空");
                return true;
            }
        },
        evaluate: function () {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "订单详情";
            var rowData = $("#jqGrid").jqGrid("getRowData", id);
            var orderNo = rowData.orderNo;
            vm.getEvaluateInfo(orderNo);
            $("#evaluate").show();
        },
        getEvaluateInfo: function (orderNo) {
            console.log(orderNo);
            var page = $("#jqGridEvaluate").jqGrid('getGridParam', 'page');
            $("#jqGridEvaluate").jqGrid('setGridParam', {
                page: page,
                postData: {
                    "orderNo": orderNo
                }
            }).trigger("reloadGrid");
        }
    }
});

$("#evaluate").hide();
$("#jqGridEvaluate").jqGrid({
    url: baseURL + 'sys/goodsevaluate/list',
    datatype: "json",
    colModel: [
        {label: '序号', name: 'goodsEvaluatId', index: 'goodsEvaluatId', width: 50, key: true},
        /*  { label: '商品ID', name: 'goodsId', index: 'goodsId', width: 80 },
          { label: '订单编号', name: 'orderNo', index: 'orderNo', width: 80 },
          { label: '评论用户ID', name: 'userId', index: 'userId', width: 80 },
          { label: '头像', name: 'headImgUrl', index: 'headImgUrl', width: 80 },*/
        {label: '昵称', name: 'nickname', index: 'nickname', width: 80},
        {label: '评价等级(*级)', name: 'evLevel', index: 'evLevel', width: 80},
        {label: '评价内容', name: 'evContent', index: 'evContent', width: 80},
        {
            label: '评论图', name: 'evaluatImgUrl', index: 'evaluatImgUrl', width: 80,
            formatter: function (val) {
                if (val == null || val == "") {
                    return ""
                } else {
                    var tempImgList = val.split(",");
                    var tempImgStr="";
                    for (var i=0;i<tempImgList.length;i++){
                        tempImgStr+="<img src='"+tempImgList[i]+"' width='40pk' height='40pk'/>"
                    }
                    return tempImgStr;
                }
            }
        },
        {label: '评论时间', name: 'evaluatDate', index: 'evaluatDate', width: 80}

    ],
    viewrecords: true,
    rowNum: 10,
    rowList: [10, 30, 50],
    rownumbers: true,
    rownumWidth: 25,
    autowidth: true,
    height: 'auto',
    multiselect: true,
    pager: "#jqGridPageEvaluate",
    jsonReader: {
        root: "page.list",
        pages: "page.currPage",
        total: "page.totalPage",
        records: "page.totalCount"
    },
    prmNames: {
        pages: "page",
        rows: "limit",
        order: "order"
    },
    gridComplete: function () {
        //隐藏grid底部滚动条
        $("#jqGridEvaluate").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        $("#jqGridEvaluate").setGridWidth($(window).width() * 0.99);
    }
});