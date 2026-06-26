$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/color/list',
        datatype: "json",
        colModel: [
            { label: '编号', name: 'colorId', index: 'colorId', width: 50, key: true },
            { label: '颜色名称', name: 'color', index: 'color', width: 80 }
        ],
        viewrecords: true,
        height: 'auto',
        rowNum: 10,
        rowList : [10,30,50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page",
            rows:"limit",
            order: "order"
        },
        gridComplete:function(){
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });
        }
    });

});

var vm = new Vue({
    el:'#rrapp',
    data:{
        showList: true,
        title: null,
        Color: {},
        q:{
            color:null
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function(){
            vm.showList = false;
            vm.title = "新增";
            vm.Color = {};
        },
        update: function (event) {
            var id = getSelectedRow();
            if(id == null){
                return ;
            }
            vm.showList = false;
            vm.title = "修改";
            vm.getInfo(id);
        },
        saveOrUpdate: function (event) {
            if(vm.validator()){
                return;
            }
            var url = vm.Color.colorId == null ? "sys/color/save" : "sys/color/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.Color),
                success: function(result){
                    if(result.code === 0){
                        alert('操作成功', function(index){
                            vm.reload();
                        });
                    }else{
                        alert(result.msg);
                    }
                }
            });
        },
        del: function (event) {
            var ids = getSelectedRows();
            if(ids == null){
                return ;
            }

            confirm('确定要删除选中的记录', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/color/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function(result){
                        if(result.code == 0){
                            alert('操作成功', function(index){
                                $("#jqGrid").trigger("reloadGrid");
                            });
                        }else{
                            alert(result.msg);
                        }
                    }
                });
            });
        },
        getInfo: function(id){
            $.get(baseURL + "sys/color/info/"+id, function(result){
                vm.Color = result.Color;
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                page:page,
                postData: {'color':vm.q.color}
            }).trigger("reloadGrid");
        },
        validator: function () {
            if (isBlank(vm.Color.color)) {
                alert("颜色不能为空");
                return true;
            }
        }

    }
});