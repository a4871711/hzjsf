$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/moeny/list',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'dataMapId', index: 'dataMapId', width: 50,key: true },
            {label: '名称', name: 'dataName', index: 'dataName', width: 50},
            {label: '金额', name: 'price', index: 'price', width: 50,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
        ],
        viewrecords: true,
        rowNum: 10,
        rowList: [10, 30, 50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth: true,
        height:'auto',
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader: {
            root: "pages.list",
            pages: "pages.currPage",
            total: "pages.totalPage",
            records: "pages.totalCount"
        },
        prmNames: {
            pages: "pages",
            rows: "limit",
            order: "order"
        },
        gridComplete: function () {
            /*vm.priceTotal = 0;
            var arr = $("#jqGrid").jqGrid('getRowData');
            var arrid = $("#jqGrid").jqGrid('getDataIDs');
            if(arrid.length == 0)return;
            arr.push($("#jqGrid").jqGrid('getRowData',arrid[arrid.length-1]));
            for(var i=0;i<arr.length;i++){
                vm.priceTotal += parseFloat(arr[i].count);
                vm.num = Math.round(vm.priceTotal * 100)/100;
            }*/

            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });

    //设置日期时间控件
    /*function Datetime() {
        $('#datetimepicker1').datetimepicker({
            format: 'YYYY-MM-DD',
            locale: moment.locale('zh-cn')
        });
    }*/


});

function but(values){
    alert(values);
}

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        dataMapEntityList:{}
    },
    methods: {
        query: function () {
            vm.reload();
        },

        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.dataMapEntityList = {};
        },
        update: function (event) {
            var dataMapId = getSelectedRow();
            if (dataMapId == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";
            vm.getInfo(dataMapId)
        },
        saveOrUpdate: function (event) {
            if (vm.validator()) {
                return;
            }
            var url = vm.dataMapEntityList.dataMapId == null ? "sys/save" : "sys/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.dataMapEntityList),
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
            var ids = getSelectedRows();
            if (ids == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/creditset/delete",
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
        getInfo: function (dataMapId) {
            $.get(baseURL + "sys/info/" + dataMapId, function (result) {
                vm.dataMapEntityList = result.dataMapEntity;
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            vm.list={};
            vm.showList = true;
            var pages = $("#jqGrid").jqGrid('getGridParam', 'pages');
            $("#jqGrid").jqGrid('setGridParam', {
                pages: pages,
                postData: {'fitCard': vm.list, "validity": $("#validity").val()}
            }).trigger("reloadGrid");
        },
        validator: function () {
            var r = /^\+?[1-9][0-9]*$/;
            if (vm.dataMapEntityList.price==null || vm.dataMapEntityList.dataName=="" || vm.dataMapEntityList.price<1 || !r.test(vm.dataMapEntityList.price)){
                layer.msg('请输入提现金额(最低为1元)整数');
                return true;
            }

        }

    }
});