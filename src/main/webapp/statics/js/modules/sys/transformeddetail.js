$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/incomepaydetail/transformedList',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'incomePayDetailId', index: 'incomePayDetailId', width: 50, key: true},
            {label: '转卡用户', name: 'nickname', index: 'nickname', width: 80},
            {label: '转卡人手机号', name: 'phone', index: 'phone', width: 80},
            {label: '所属门店', name: 'storeName', index: 'storeName', width: 80},
            {label: '订单号', name: 'orderNo', index: 'orderNo', width: 80},
            {label: '金额数', name: 'money', index: 'money', width: 80,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            {label: '接转用户', name: 'takeName', index: 'takeName', width: 80},
            {label: '接转人手机', name: 'takePhone', index: 'takePhone', width: 80},
           {label: '创建时间', name: 'createdDate', index: 'createdDate', width: 80,
               formatter: function (value) {
                   return value == "" || value == null ? "" : formatDate(new Date(value))
               }
           }
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
        }
    });

});

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        incomePayDetail: {},
        q: {
        }
    },
    methods: {
        query: function () {
            vm.q.sdate = $("#sdate").val();
            vm.q.edate = $("#edate").val();
            vm.reload();
        },

        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: vm.q,
                page: page
            }).trigger("reloadGrid");
        }
    }
});