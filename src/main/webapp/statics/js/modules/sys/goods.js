$(function () {
    vm.getGoodsCategoryDetail();
    /*vm.getColorDetail();
    vm.getSizeDetail();*/
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/goods/list',
        datatype: "json",
        colModel: [
            { label: '编号', name: 'id', index: 'id', width: 30, key: true },
            {label: '商品类型', name: 'nextCategoryName', index: 'nextCategoryName', width: 50},
            {label: '商品名称', name: 'name', index: 'name', width: 50},
            {label: '商品价格', name: 'price', index: 'price', width: 30,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            {label: '商品型号', name: 'style', index: 'style', width: 50,hidden: true},
            {label: '商品颜色', name: 'color', index: 'color', width: 40,hidden: true},
            {label: '商品尺寸', name: 'size', index: 'size', width: 40,hidden: true},
            { label: '商品状态', name: 'status', width: 30,
                formatter: function(value){
                    return value === 0 ?
                    '<span class="label label-danger">下架</span>' :
                    '<span class="label label-success">上架</span>';
            }},
            {label: '运费', name: 'freight', index: 'freight', width: 30,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            {
                label: '创建时间', name: 'createdDate', index: 'createdDate', width: 60,
                formatter: function (value) {
                    return value == "" || value == null ? "" : formatDate(new Date(value))
                }
            },
            {label:'相关操作',name:'id',index:'id',width:50,
                formatter:function (value,option,rowObject) {
                    return '<a href="javascript:void(0)" onclick="vm.insertGoods(' + value + ')">添加规格</a>&nbsp;&nbsp;<a href="javascript:void(0)" onclick="vm.insertGoodsDetail('+ rowObject.goodsId +')">规格列表</a>'
                }
            }
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

    $.each($(".upload"), function () {
        var _this = $(this);
        new AjaxUpload($(this), {
            action: baseURL + "sys/upload?width=750&height=445",
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
                        vm.carouselImgUrl += url + ",";
                        vm.Goods.carouselImgUrl = vm.carouselImgUrl.substring(0, vm.carouselImgUrl.length - 1);
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });

    $.each($(".uploadImg"), function () {
        var _this = $(this);
        new AjaxUpload($(this), {
            action: baseURL + "sys/upload?width=330&height=330",
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
                        vm.imgUrl = url;
                        vm.Goods.imgUrl = url;
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });

});

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        Goods: {},
        imgUrl: "",
        tempArray: [],
        tempRemarkArray: [],
        q:{
            name:null
        },
        ue: {},
        resultGoodsCategoryDetailType:{},
        resultColor:{},
        resultSize:{},
        colorType: [],
        sizeType: []

    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.Goods = {};
            vm.colorType = [];
            vm.sizeType= [];
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
            ue.setContent("");
        },
        update: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";
            vm.Goods.imgUrl = vm.imgUrl;
            vm.getInfo(id);
        },
        saveOrUpdate: function (event) {
            /*vm.Goods.color = vm.colorType.toString();
            vm.Goods.size = vm.sizeType.toString();*/
            vm.Goods.remark = ue.getContent();
            if(vm.validator()){
                return;
            }
            vm.Goods.imgUrl = vm.imgUrl;
            var img = "";
            /*var imgRemark = "";*/
            $.each($("#image img"), function (index, value) {
                var url = $(this).attr("src");
                if(url.indexOf("addPhoto.svg") ==-1){
                    img += url + ",";
                }
            });
            vm.Goods.carouselImgUrl =img.substring(0, img.length - 1);
            /*$.each($("#images img"), function (index, value) {
                var url = $(this).attr("src");
                if(url.indexOf("addPhoto.svg") ==-1){
                    imgRemark += url + ",";
                }
            });
            vm.Goods.remarkImgUrl =imgRemark.substring(0, imgRemark.length - 1);*/
            var url = vm.Goods.goodsId == null ? "sys/goods/save" : "sys/goods/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.Goods),
                success: function (result) {
                    if (result.code === 0) {
                        alert('操作成功', function (index) {
                            vm.reload();
                            location.reload();
                        });
                    } else {
                        alert(result.msg);
                    }
                }
            });
        },
        cancel: function(event){
            vm.showList = true;
            ue.setContent("");
        },
        del: function (event) {
            var ids = getSelectedRows();
            if (ids == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/goods/delete",
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
        getInfo: function (id) {
            $.get(baseURL + "sys/goods/info/" + id, function (result) {
                vm.Goods = result.Goods;
                ue.setContent(result.Goods.remark);
                vm.colorType = vm.Goods.color.split(",");
                vm.sizeType = vm.Goods.size.split(",");
                var urls = vm.Goods.imgUrl;
                vm.imgUrl = urls;
                $(".logoImg img").attr("src", urls);
                $(".logoImg img").attr("layer-src", urls);

                vm.tempArray = vm.Goods.carouselImgUrl.split(",");
                vm.tempArray.forEach(function (url, index) {
                    $($(".logo img")[index]).attr("src", url);
                    $($(".logo img")[index]).attr("layer-src", url);
                });


                /*vm.tempRemarkArray = vm.Goods.remarkImgUrl.split(",");
                vm.tempRemarkArray.forEach(function (urlRemark, index) {
                    $($(".logoRemark img")[index]).attr("src", urlRemark);
                    $($(".logoRemark img")[index]).attr("layer-src", urlRemark);
                });*/
            });
        },
        saveGoods: function (event) {
            if(vm.validata()){
                return;
            }
            vm.Goods.imgUrl = vm.imgUrl;
            var url = "sys/goods/insertGoods";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.Goods),
                success: function (result) {
                    if (result.code === 0) {
                        alert('操作成功', function (index) {
                            vm.reload();
                            vm.closeAllLayer();
                            //location.reload();
                        });
                    } else {
                        alert(result.msg);
                    }
                }
            });
        },
        reload: function (event) {
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page,
                postData: {'name':vm.q.name}
            }).trigger("reloadGrid");
        },
        getGoodsCategoryDetail:function(){
            $.get(baseURL + "sys/goodsCategoryDetail/selectGoodsCategoryDetail", function(r){
                vm.resultGoodsCategoryDetailType = r.goodsCategoryDetail;
            });
        },
        /*getColorDetail:function(){
            $.get(baseURL + "sys/color/selectColorDetail", function(r){
                vm.resultColor = r.colorDetail;
            });
        },*/
        /*getSizeDetail:function(){
            $.get(baseURL + "sys/size/selectSizeDetail", function(r){
                vm.resultSize = r.sizeDetail;
            });
        },*/
        //删除轮播图
        removeT: function (e) {
            var index = $(e.target).parent().index();
            console.log("删除图片下标removeT:"+index);
            $($(".logo img")[index-1]).attr("src", "/statics/img/addPhoto.svg");
            $($(".logo img")[index-1]).attr("layer-src", "/statics/img/addPhoto.svg");
        },

        /*预览大图*/
        preview: function () {
            $(document).on("click", ".layui-layer-shade", function () {
                $(".layui-layer-shade").remove();
                $(".layui-layer").remove();
            });
            layer.photos({
                photos: "#img"
                , anim: 5
            });
            layer.photos({
                photos: "#image"
                , anim: 5
            });
            layer.photos({
                photos: "#images"
                , anim: 5
            });
        },
        insertGoods:function(id){
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
            $.get(baseURL + "sys/goods/info/" + id, function (result) {
                vm.Goods = result.Goods;
                vm.Goods.color = "";
                vm.Goods.size = "";
                vm.Goods.total = "";
            });
            layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-molv',
                title: "添加规格",
                area: ['700px', '320px'],
                shade: 0,
                shadeClose: false,
                resize:false,
                content: $("#anewFileWindow")
            });
        },
        closeAllLayer:function () {
            layer.closeAll();
        },
        insertGoodsDetail: function (event) {
            var id = event;

            sessionStorage["goodsId"] = id;
            location.href = "goodsdetail.html?backurl=" + window.location.href;
        },
        validator: function () {
            var re = /^[1-9]\d*$/;
            var chinese = /^[\u3220-\uFA29]+$/;
            if (vm.Goods.categoryId == null) {
                alert("请选择商品类型");
                return true;
            }
            if (isBlank(vm.Goods.name)) {
                alert("商品名称不能为空");
                return true;
            }
            if(isBlank(vm.Goods.price)){
                alert("金额数值不能为空");
                return true;
            }
            var retype= /^(-?\d+)(\.\d{1,2})?$/;
            if(vm.Goods.price !=0){
                if(!retype.test(vm.Goods.price) || vm.Goods.price < 0){
                    alert("请输入正确金额数值,若输入金额包含小数位，最多不超过2位小数");
                    return true;
                }
            }

            if(vm.Goods.freight !=0){
                if(isBlank(vm.Goods.freight) || !retype.test(vm.Goods.freight) || vm.Goods.freight < 0){
                    alert("请输入正确金额数值,若输入运费金额包含小数位，最多不超过2位小数");
                    return true;
                }
            }

            if(chinese.test(vm.Goods.style)){
                alert("商品型号不能包含中文");
                return true;
            }
            if (isBlank(vm.Goods.color)) {
                alert("商品颜色不能为空");
                return true;
            }
            if (isBlank(vm.Goods.size)) {
                alert("商品尺寸不能为空");
                return true;
            }

            if (vm.Goods.status == null) {
                alert("请选择商品状态");
                return true;
            }

            if (isBlank(vm.Goods.remark)) {
                alert("商品描述不能为空");
                return true;
            }
        },
        validata: function () {
            var re = /^[0-9]\d*$/;
            if (isBlank(vm.Goods.color)) {
                alert("商品颜色不能为空");
                return true;
            }
            if (isBlank(vm.Goods.size)) {
                alert("商品尺寸不能为空");
                return true;
            }
        }
    }
});