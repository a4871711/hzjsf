$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/protocol/list',
        datatype: "json",
        colModel: [
            {label: '编号', name: 'pId', index: 'pId', width: 10, key: true},
            {label: '协议类型', name: 'type', index: 'type', width: 10,
                formatter: function (values,o,row) {
                    switch (values){
                        case 0: return '用户协议';
                        case 1: return '隐私协议';
                        case 2: return '矢历连续包月协议';
                        case 3: return '矢历连续会员协议';
                        case 4: return '矢历连续次卡会员协议';
                    }
                }
            },
            {
                label: '服务条款', name: 'protocols', index: 'protocols', width: 10,
                formatter: function (values,o,row) {
                    return "<input type='Button' name='name' value='详情' class='btn btn-info but' onclick='but("+row.pId+")'/>"
                }
            },
            {label: '服务条款', name: 'protocols', index: 'protocols', width: 10, hidden: true},
            {label: '勾选状态(默认)', name: 'selected', index: 'selected', width: 10,
                formatter: function (values,o,row) {
                    switch (values){
                        case 0: return '<span class="label label-warning">不勾选</span>';
                        case 1: return '<span class="label label-success">勾选</span>';
                    }
                }
            }
        ],
        viewrecords: true,
        height: 500,
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
function but(values){
    var rowData = $("#jqGrid").jqGrid('getRowData',values);
    alert(rowData.protocols);
}
var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        protocol: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            $("#_input").attr("disabled", false);
            $("#select").attr("disabled", false);
            vm.showList = false;
            vm.title = "新增";
            vm.protocol = {};
        },
        a: function () {
            $("#_input").val($("#select option:selected").text());
        },
        update: function (event) {
            var pId = getSelectedRow();
            if (pId == null) {
                return;
            }
            $("#_input").attr("disabled", true);
            $("#select").attr("disabled", true);
            vm.showList = false;
            vm.title = "修改";
            vm.getInfo(pId)
        },

         //alert(ue.getContent());//获取编辑器html内容  alert(ue.getPlainTxt());//获取保留换行/空格等格式的纯文本alert(ue.getContentTxt());//获取纯文本内容

        saveOrUpdate: function (event) {
            if(vm.protocol.type == 3){
                vm.protocol.protocols = ue.getContentTxt();
            }else{
                vm.protocol.protocols = ue.getContent();
            }
            if(isBlank(vm.protocol.protocols)){
                layer.msg("条款内容不可为空");
                return;
            }
            if(vm.protocol.selected == null){
                layer.msg("请选择勾选状态");
                return;
            }
            if(vm.protocol.type == null){
                layer.msg("请选择协议类型");
                return;
            }
            var url = vm.protocol.pId == null ? "sys/protocol/save" : "sys/protocol/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.protocol),
                success: function (r) {
                    if (r.code === 0) {
                        alert("成功", function (index) {
                            vm.reload();
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        del: function (event) {
            var pIds = getSelectedRows();
            if (pIds == null) {
                return;
            }
            confirm('确定删除此协议吗？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/protocol/delete",
                    contentType: "application/json",
                    data: JSON.stringify(pIds),
                    success: function (r) {
                        if (r.code == 0) {
                            alert("成功", function (index) {
                                vm.reload();
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        getInfo: function (pId) {
            $.get(baseURL + "sys/protocol/info/" + pId, function (r) {
                console.log(r);
                vm.protocol = r.protocol;
                ue.setContent(vm.protocol.protocols);
            });
        },
        reload: function (event) {
            UE.getEditor('editor').execCommand('cleardoc');
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page
            }).trigger("reloadGrid");
        }
    }
});