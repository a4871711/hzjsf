$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/user/couponList',
        datatype: "json",
        colModel: [
            {label: '编号', name: 'couponId', index: 'couponId', width: 50},
            {label: '用户', name: 'nickName', index: 'nickName', width: 80},
            {label: '手机号', name: 'phone', index: 'phone', width: 80},
            {label: '操作门店', name: 'operateStore', index: 'operateStore', width: 80},
            {
                label: '优惠券状态', name: 'couponStatus', index: 'couponStatus', width: 50,
                formatter: function (v) {
                    if (v == 0) {
                        return "未使用"
                    }
                    else if (v == 1) {
                        return "已使用"
                    }
                    else if (v == 2) {
                        return "已过期"
                    }
                }
            },
            {label: '优惠券金额', name: 'couponPrice', index: 'couponPrice', width: 50,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },

            {
                label: '优惠券有效期', name: 'validityTime', index: "validityTime", width: 50, formatter: function (value) {
                return value == "" || value == null ? "" : formatDate(new Date(value))
            }
            },
            {
                label: '创建时间', name: 'createdDate', index: "createdDate", width: 50, formatter: function (value) {
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
            $("#jqGrid").setGridWidth($("#jqGrid").width());
        }
    });
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        showDetail: true,
        title: null,
        couponDetail: [],
        list: {},
        coupon: {
            couponMoney: 0,
            validDay: 0
        },
        status: null,
        q: {
            phone: null,
            couponStatus:""
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        reload: function (event) {
            vm.showList = true;
            vm.showDetail = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page,
                postData: {
                    'phone': vm.q.phone,'couponStatus':vm.q.couponStatus, "startTime": $("#sdate").val(),
                    "endTime": $("#edate").val()
                }
            }).trigger("reloadGrid");
        }
    }
});