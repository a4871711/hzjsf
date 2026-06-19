$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/goodsCategory/list',
        datatype: "json",
        colModel: [
            { label: '编号', name: 'goodsCategoryId', index: 'goodsCategoryId', width: 50, key: true },
            { label: '一级分类主图', name: 'categoryImg', index: 'categoryImg', width: 80,
                formatter:function (value) {
                    return "<img src='"+ value+"' weight='80pk' height='80pk' />"
                }
            },
            { label: '一级分类名称', name: 'categoryName', index: 'categoryName', width: 80 },
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

    $.each($(".upload"), function () {
        var _this = $(this);
        new AjaxUpload($(this), {
            action: baseURL + "sys/upload?width=80&height=80",
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
                        vm.categoryImg = url;
                        vm.goodsCategory.categoryImg = url;
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
        goodsCategory: {},
        q:{
            categoryName:null
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function(){
            vm.showList = false;
            vm.title = "新增";
            vm.goodsCategory = {};
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
            vm.goodsCategory.categoryImg = vm.categoryImg;
            vm.getInfo(id);
        },
        saveOrUpdate: function (event) {
            /*if(vm.validator()){
                return;
            }*/
            vm.goodsCategory.categoryImg = vm.categoryImg;
            var url = vm.goodsCategory.goodsCategoryId == null ? "sys/goodsCategory/save" : "sys/goodsCategory/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.goodsCategory),
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
                    url: baseURL + "sys/goodsCategory/delete",
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
            $.get(baseURL + "sys/goodsCategory/info/"+id, function(result){
                vm.goodsCategory = result.goodsCategory;
                var url = vm.goodsCategory.categoryImg;
                vm.categoryImg = url;
                $(".logo img").attr("src", url);
                $(".logo img").attr("layer-src", url);
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                page:page,
                postData: {'categoryName':vm.q.categoryName}
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
        }/*,
        validator: function () {

            if (isBlank(vm.aboutUs.title)) {
                alert("请输入标题!");
                return true;
            }
            if (isBlank(vm.aboutUs.content)) {
                alert("请输入内容!");
                return true;
            }
            if (isBlank(vm.aboutUs.serviceTel)) {
                alert("请输入客服电话!");
                return true;
            }
            if (isBlank(vm.aboutUs.version)) {
                alert("请输入版本号!");
                return true;
            }
            if (vm.aboutUs.version.length>20){
                alert("版本号过长");
                return true;
            }
            if (isBlank(vm.aboutUs.companyNameCHN)) {
                alert("请输入公司中文名!");
                return true;
            }
            if (isBlank(vm.aboutUs.companyNameENG)) {
                alert("请输入公司英文名!");
                return true;
            }

        }*/

    }
});