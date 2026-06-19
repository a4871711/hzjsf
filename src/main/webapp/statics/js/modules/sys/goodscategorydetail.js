$(function () {
    vm.getGoodsCategoryDetail();
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/goodsCategoryDetail/list',
        datatype: "json",
        colModel: [
            { label: '编号', name: 'id', index: 'id', width: 50, key: true },
            { label: '一级分类名称', name: 'categoryName', index: 'categoryName', width: 80 },
            /*{ label: '二级分类主图', name: 'nextCategoryImg', index: 'nextCategoryImg', width: 80,
                formatter:function (value) {
                    return "<img src='"+ value+"' weight='80pk' height='80pk' />"
                }
            },*/
            { label: '二级分类名称', name: 'nextCategoryName', index: 'nextCategoryName', width: 80 },
            { label: '创建时间', name: 'createdDate', index: 'createdDate', width: 80,
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

/*    $.each($(".upload"), function () {
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
                        vm.nextCategoryImg = url;
                        vm.goodsCategoryDetail.nextCategoryImg = url;
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });*/

});

var vm = new Vue({
    el:'#rrapp',
    data:{
        showList: true,
        title: null,
        goodsCategoryDetail: {},
        q:{
            nextCategoryName:null
        },
        resultGoodsCategoryType:{}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function(){
            vm.showList = false;
            vm.title = "新增";
            vm.goodsCategoryDetail = {};
            /*$(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");*/
            vm.getGoodsCategoryDetail();
        },
        update: function (event) {
            var id = getSelectedRow();
            if(id == null){
                return ;
            }
            vm.showList = false;
            vm.title = "修改";
            /*vm.goodsCategoryDetail.nextCategoryImg = vm.nextCategoryImg;*/
            vm.getInfo(id);
            vm.getGoodsCategoryDetail();
        },
        saveOrUpdate: function (event) {
            if(vm.validator()){
                return;
            }
            /*vm.goodsCategoryDetail.nextCategoryImg = vm.nextCategoryImg;*/
            var url = vm.goodsCategoryDetail.id == null ? "sys/goodsCategoryDetail/save" : "sys/goodsCategoryDetail/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.goodsCategoryDetail),
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
                    url: baseURL + "sys/goodsCategoryDetail/delete",
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
            $.get(baseURL + "sys/goodsCategoryDetail/info/"+id, function(result){
                vm.goodsCategoryDetail = result.goodsCategoryDetail;
                /*var url = vm.goodsCategoryDetail.nextCategoryImg;
                vm.nextCategoryImg = url;
                $(".logo img").attr("src", url);
                $(".logo img").attr("layer-src", url);*/
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                page:page,
                postData: {'nextCategoryName':vm.q.nextCategoryName}
            }).trigger("reloadGrid");
        },
        getGoodsCategoryDetail:function(){
            $.get(baseURL + "sys/goodsCategory/selectGoodsCategory", function(r){
                vm.resultGoodsCategoryType = r.goodsCategory;
            });
        },
        /*预览大图*/
        /*preview: function () {
            $(document).on("click", ".layui-layer-shade", function () {
                $(".layui-layer-shade").remove();
                $(".layui-layer").remove();
            });
            layer.photos({
                photos: "#image" //格式见API文档手册页
                , anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机
            });
        },*/
        validator: function () {
            if (isBlank(vm.goodsCategoryDetail.goodsCategoryId)) {
                alert("请选择一级分类名称");
                return true;
            }
            if (isBlank(vm.goodsCategoryDetail.nextCategoryName)) {
                alert("请输入二级分类名称");
                return true;
            }

        }

    }
});