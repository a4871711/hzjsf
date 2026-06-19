$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/size/list',
        datatype: "json",
        colModel: [
            { label: '编号', name: 'sizeId', index: 'sizeId', width: 50, key: true },
            { label: '颜色尺寸', name: 'size', index: 'size', width: 80 }
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
        Size: {},
        q:{
            name:null
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function(){
            vm.showList = false;
            vm.title = "新增";
            vm.Size = {};
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
            var url = vm.Size.sizeId == null ? "sys/size/save" : "sys/size/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.Size),
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
                    url: baseURL + "sys/size/delete",
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
            $.get(baseURL + "sys/size/info/"+id, function(result){
                vm.Size = result.Size;
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                page:page,
                postData: {'size':vm.q.size}
            }).trigger("reloadGrid");
        },
        validator: function () {
            if (isBlank(vm.Size.size)) {
                alert("尺寸不能为空");
                return true;
            }

        }

    }
});