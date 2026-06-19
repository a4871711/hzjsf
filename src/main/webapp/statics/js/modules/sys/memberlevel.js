$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/memberLevel/list',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'dataMapId', index: 'dataMapId', width: 50, key: true,hidden:true},
            /*      {label: '兴趣标签', name: 'idxLabel', index: 'idxLabel', width: 50 },
               {label: '二级标签', name: 'nextLabel', index: 'nextLabel', width: 80},
               {label: '名称', name: 'dataName', index: 'dataName', width: 50},*/
            {label: '会员等级名称', name: 'dataName', index: 'dataName', width: 50},
            {label: '等级', name: 'price', index: 'price', width: 50},
            {label: '最低能量值(千卡)', name: 'val', index: 'val', width: 50},
            {label: '说明', name: 'description', index: 'description', width: 50
            }
        ],
        viewrecords: true,
        rowNum: 10,
        rowList: [10, 30, 50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth: true,
        height: 600,
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
  //  $("#jqGrid").setGridParam().hideCol("dataMapId");
});

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        dataMapEntityList: {}
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
                retur
            }
            vm.showList = false;
            vm.title = "修改";
            vm.getInfo(dataMapId)
        },
        saveOrUpdate: function (event) {
            if (vm.validator()) {
                return;
            }
            var url = vm.dataMapEntityList.dataMapId == null ? null : "sys/memberLevel/update";
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
                    url: baseURL + "sys/member/delete",
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
            $.get(baseURL + "sys/member/info/" + dataMapId, function (result) {
                vm.dataMapEntityList = result.dataMapEntity;
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page
            }).trigger("reloadGrid");
        },
        validator: function () {
            if (vm.dataMapEntityList.dataName.length > 15) {
                layer.msg("名称请输入15个字符串内");
                return true;
            }
            var r = /^\+?[0-9][0-9]*$/;
            if (vm.dataMapEntityList.price==null || !r.test(vm.dataMapEntityList.price) || vm.dataMapEntityList.price < 0) {
                layer.msg('请输入正整数');
                return true;
            }
            if (vm.dataMapEntityList.val==null || !r.test(vm.dataMapEntityList.val) || vm.dataMapEntityList.val < 0) {
                layer.msg('请输入正整数');
                return true;
            }
        }
    }
});