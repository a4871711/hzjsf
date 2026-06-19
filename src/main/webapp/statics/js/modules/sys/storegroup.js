$(function () {
    var storeAddrId = sessionStorage["storeAddrId"];
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/storeGroup/list?storeAddrId='+storeAddrId,
        datatype: "json",
        colModel: [
            { label: '编号', name: 'storeGroupId', index: 'storeGroupId', width: 50, key: true },
            { label: '门店名称', name: 'storeName', index: 'storeName', width: 80 },
            { label: '门店电话', name: 'storePhone', index: 'storePhone', width: 80 },
            { label: '社群名称', name: 'groupName', index: 'groupName', width: 80 },
            { label: '社群详情图', name: 'groupImg', index: 'groupImg', width: 80,
                formatter:function (value) {
                    return "<img src='"+ value+"' weight='80pk' height='80pk' />"
                }
            },
            { label: '社群位置', name: 'groupAddr', index: 'groupAddr', width: 80 },
            { label: '修改时间', name: 'createdDate', index: 'createdDate', width: 80,
                formatter: function (value) {
                    return value == "" || value == null ? "" : formatDate(new Date(value));
                }
            }
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

    $.each($(".upload"), function () {
        var _this = $(this);
        new AjaxUpload($(this), {
            action: baseURL + "sys/upload",
            name: 'files',
            autoSubmit: true,
            responseType: "json",
            onSubmit: function (file, extension) {
                if (!(extension && /^(jpg|jpeg|png|gif)$/.test(extension.toLowerCase()))) {
                    alert('只支持jpg、png、gif格式的图片！');
                    return false;
                }
            },
            onComplete: function (file, r) {
                if (r.code == 1) {
                    var urls = r.data.imgPath;
                    urls.forEach(function (url) {
                        _this.prev().attr("src", url);
                        _this.prev().attr("layer-src", url);
                        vm.groupImg = url;
                        vm.storeGroup.groupImg = url;
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });

});

var vm = new Vue({
    el:'#rrapp',
    data:{
        showList: true,
        title: null,
        storeGroup: {},
        q:{
            groupName:null
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function(){
            vm.showList = false;
            vm.title = "新增";
            vm.storeGroup = {};
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
        },
        update: function (event) {
            var id = getSelectedRow();
            if(id == null){
                return ;
            }
            vm.showList = false;
            vm.title = "修改";
            vm.storeGroup.groupImg = vm.groupImg;
            vm.getInfo(id);
        },
        saveOrUpdate: function (event) {
            if(vm.validator()){
                return;
            }
            vm.storeGroup.groupImg = vm.groupImg;
            var url = "sys/storeGroup/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.storeGroup),
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

            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/storeGroup/delete",
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
            $.get(baseURL + "sys/storeGroup/info/"+id, function(result){
                vm.storeGroup = result.storeGroup;
                var url = vm.storeGroup.groupImg;
                vm.groupImg = url;
                $(".logo img").attr("src", url);
                $(".logo img").attr("layer-src", url);
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                page:page,
                postData: {'groupName':vm.q.groupName}
            }).trigger("reloadGrid");
        },
        /*预览大图*/
        preview: function () {
            $(document).on("click", ".layui-layer-shade", function () {
                $(".layui-layer-shade").remove();
                $(".layui-layer").remove();
            });
            layer.photos({
                photos: "#image" //格式见API文档手册页
                , anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机
            });
        },
        validator: function () {
            if (isBlank(vm.storeGroup.groupName)) {
                alert("社群名称不能为空");
                return true;
            }
            if (vm.storeGroup.groupImg == null) {
                alert("请选择社群详情图");
                return true;
            }
        }

    }
});