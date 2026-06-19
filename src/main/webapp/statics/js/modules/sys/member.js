$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/member/list',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'dataMapId', index: 'dataMapId', width: 50, key: true, hidden: true},
            /*      {label: '兴趣标签', name: 'idxLabel', index: 'idxLabel', width: 50 },
               {label: '二级标签', name: 'nextLabel', index: 'nextLabel', width: 80},
               {label: '名称', name: 'dataName', index: 'dataName', width: 50},*/
            {label: '名称', name: 'dataName', index: 'dataName', width: 50},
            {label: '每日积分上限（卡类用户）', name: 'price', index: 'price', width: 50},
            {label: '每日积分上限（非卡类用户）', name: 'val', index: 'val', width: 50},
            {
                label: '说明', name: 'description', index: 'description', width: 50,
                formatter: function (values,o,row) {
                    return "<input type='Button' name='name' value='详情' class='btn btn-info but' onclick='but("+row.dataMapId+")'/>"
                }
            },
            {label: '说明', name: 'description', index: 'description', width: 10, hidden:true}
        ],
        viewrecords: true,
        rowNum: 10,
        rowList: [10, 30, 50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth: true,
        height: 'auto',
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

            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });

});
function but(values){
    var rowData = $("#jqGrid").jqGrid('getRowData',values);
    alert(rowData.description);
}
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
            vm.dataMapEntityList.description = ue.getContent();
            if (vm.validator()) {
                return;
            }
            var url = vm.dataMapEntityList.dataMapId == null ? null : "sys/member/update";
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
                ue.setContent(vm.dataMapEntityList.description);
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
            if (vm.dataMapEntityList.price == null || !r.test(vm.dataMapEntityList.price) || vm.dataMapEntityList.price < 0) {
                layer.msg('请输入正整数');
                return true;
            }
            if (vm.dataMapEntityList.val == null || !r.test(vm.dataMapEntityList.val) || vm.dataMapEntityList.val < 0) {
                layer.msg('请输入正整数');
                return true;
            }
        }
    }
});