$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/storegoods/list',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'goodsId', index: 'goodsId', width: 50, key: true},
            /*		{ label: '门店ID', name: 'storeId', index: 'storeId', width: 80 }, 			*/
            {label: '商品名称', name: 'name', index: 'name', width: 80},
            {label: '商品价格', name: 'price', index: 'price', width: 80},
            {label: '商品型号', name: 'style', index: 'style', width: 80},
            {label: '库存量', name: 'total', index: 'total', width: 80},
            {label: '商品备注', name: 'remark', index: 'remark', width: 80},
            {label: '条形码', name: 'barCode', index: 'barCode', width: 80},
            {label: '创建时间', name: 'createdDate', index: 'createdDate', width: 80}
        ],
        viewrecords: true,
        height: 385,
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
        storeGoods: {},
        q:{}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.storeGoods = {};
        },
        update: function (event) {
            var goodsId = getSelectedRow();
            if (goodsId == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(goodsId)
        },
        saveOrUpdate: function (event) {
            if (!vm.validator()) {
                return;
            }
            var url = vm.storeGoods.goodsId == null ? "sys/storegoods/save" : "sys/storegoods/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.storeGoods),
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
            var goodsIds = getSelectedRows();
            if (goodsIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/storegoods/delete",
                    contentType: "application/json",
                    data: JSON.stringify(goodsIds),
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
        getInfo: function (goodsId) {
            $.get(baseURL + "sys/storegoods/info/" + goodsId, function (r) {
                vm.storeGoods = r.storeGoods;
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

            if (isBlank(vm.storeGoods.name) || vm.storeGoods.name.length>30) {
                alert("商品名称不能为空，30字以内");
                return false;
            }
            var retype= /^(-?\d+)(\.\d{1,2})?$/;
            var engNum=/^[a-zA-Z0-9]+$/;
            if (vm.storeGoods.price==null||vm.storeGoods.price=="") {
                alert("请输入价格");
                return false;
            }
            if(isBlank(vm.storeGoods.price) || !retype.test(vm.storeGoods.price) || vm.storeGoods.price < 0){
                alert("请输入正确金额数值,若输入金额包含小数位，最多不超过2位小数");
                return false;
            }
            if(isBlank(vm.storeGoods.style) || vm.storeGoods.style.length>15){
                alert("请输入商品型号,15字以内");
                return false;
            }
            if(isNumber(vm.storeGoods.total)||(vm.storeGoods.total+"").indexOf(".")!=-1){
                alert("库存量请输入整数");
                return false;
            }
            if(isBlank(vm.storeGoods.barCode)){
                alert("请输入商品条形码");
                return false;
            }

            if(!engNum.test(vm.storeGoods.barCode)){
                alert("条形码只能包含英文，数字");
                return false;
            }
          /*  $.ajax({
                type: "POST",
                url: baseURL + "sys/storegoods/queryCountByBarCode",
                data: vm.storeGoods.barCode,
                success: function (r) {
                    if (r.code == 0) {

                    } else {
                        alert(r.msg);
                        return false;
                    }
                }
            });*/
            if(isBlank(vm.storeGoods.remark)){
                alert("请输入商品描述");
                return false;
            }

            return true;
        }
    }
});