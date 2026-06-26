$(function () {
    vm.changeSize();
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/advertising/list',
        datatype: "json",
        colModel: [
            {label: '编号', name: 'advId', index: 'advId', width: 50, key: true},
            {
                label: '广告类型', name: 'advType', index: 'advType', width: 80,
                formatter: function (value) {
                    if (value == 1) {
                        return '首页广告';
                    } else if (value == 2) {
                        return '商城广告';
                    } else {
                        return '定制广告';
                    }
                }
            },
            {label: '广告标题', name: 'advTitle', index: 'advTitle', width: 80},
            {
                label: '广告主图', name: 'advMainImg', index: 'advMainImg', width: 80,
                formatter: function (value) {
                    return "<img src='" + value + "' weight='80pk' height='80pk'/>"
                }
            },
           /* {
                label: '广告内容', name: 'advContent', index: 'advContent', width: 80,
                formatter: function (value) {
                  return "<div style='height: 150px;'>"+value+"</div>"
                }
            },*/
            /*{
                label: '广告插图', name: 'figureImg', index: 'figureImg', width: 80,
                formatter: function (value) {
                    return "<img src='" + value + "' weight='80pk' height='80pk'/>"
                }
            },*/
            {
                label: '创建时间', name: 'createdDate', index: 'createdDate', width: 80,
                formatter: function (value) {
                    return value == "" || value == null ? "" : formatDate(new Date(value))
                }
            }
        ],
        viewrecords: true,
        height: 'auto',
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

    $.each($(".upload"), function () {
        var _this = $(this);
        new AjaxUpload($(this), {
            action: baseURL + "sys/upload?width=660&height=280",
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
                        vm.advertising.advMainImg = url;
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });

    $.each($(".upload2"), function () {
        var _this = $(this);
        new AjaxUpload($(this), {
            action: baseURL + "sys/upload?width=690&height=198",
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
                        vm.advertising.advMainImg = url;
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
        q: {},
        advertising: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        changeSize: function () {
            if(vm.advertising.advType == 3){
               $('.upload2').show();
               $('.upload').hide();
            }else{
                $('.upload2').hide();
                $('.upload').show();
            }
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.advertising = {};
            vm.advertising.advType = 2;
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
            ue.setContent("");
        },
        update: function (event) {
            var advid = getSelectedRow();
            if (advid == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(advid)
        },
        saveOrUpdate: function (event) {
            vm.advertising.advContent = ue.getContent();
            //vm.advertising.advContent = ue.getAllHtml();
            //console.log(vm.advertising.advContent);
            if (vm.validator()) {
                return;
            }
            var url = vm.advertising.advId == null ? "sys/advertising/save" : "sys/advertising/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.advertising),
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
            var advids = getSelectedRows();
            if (advids == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/advertising/delete",
                    contentType: "application/json",
                    data: JSON.stringify(advids),
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
        getInfo: function (advid) {
            $.get(baseURL + "sys/advertising/info/" + advid, function (r) {
                $(".logo img").attr("src", r.advertising.advMainImg);
                $(".logo img").attr("layer-src", r.advertising.advMainImg);
                vm.advertising = r.advertising;
                ue.setContent(r.advertising.advContent);

            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page
            }).trigger("reloadGrid");
        },
        reload2: function (event) {
            var page = $("#jqGridAddMsg").jqGrid('getGridParam', 'goodsPage');
            $("#jqGridAddMsg").jqGrid('setGridParam', {
                page: page,
                postData: vm.mq
            }).trigger("reloadGrid");

        },

        /*预览大图*/
        preview: function () {
            $(document).on("click", ".layui-layer-shade", function () {
                $(".layui-layer-shade").remove();
                $(".layui-layer").remove();
            });
            layer.photos({
                photos: "#images" //格式见API文档手册页
                , anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机
            });
        },
        validator: function () {
            var phoneReg = /(^1[3|4|5|7|8]\d{9}$)|(^09\d{8}$)/;
            var passwordReg = /^[0-9a-zA-Z]+$/;
            if (vm.advertising.advTitle == "" || vm.advertising.advTitle == null) {
                alert("请输入标题");
                return true;
            }
            if (vm.advertising.advTitle.length > 32) {
                alert("标题超长");
                return true;
            }
            if (vm.advertising.advMainImg == null) {
                alert("请上传主图");
                return true;
            }
            if(vm.advertising.advType == 3){
                if (vm.advertising.advContent == "" || vm.advertising.advContent == null) {
                    alert("请填写广告内容");
                    return true;
                }
            }

        },
        selectGoods: function () {
            var a = layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-molv',
                title: "请选择商品",
                area: ['750px', '500px'],
                shadeClose: false,
                content: $("#selectGoods")

            });
        },
        queryGoods: function () {
            vm.reload2();
        },
        /**选定商品*/
        selectedGoods: function (event) {
            var goodsId = getSelectedWindowRow2();
            if(goodsId == null){
                return;
            }
            var rowData = $("#jqGridAddMsg").jqGrid('getRowData',goodsId);
            console.log(goodsId+ "商品id:" + rowData.name);
            $("#goodsName").val(rowData.name);
            vm.advertising.goodsName = rowData.name;
            vm.advertising.goodsId = goodsId;
            vm.reload2();
            layer.closeAll();
        },
        initMsgJQqrid: function () {
            $("#jqGridAddMsg").jqGrid({
                url: baseURL + 'sys/advertising/goodsSelectList',
                datatype: "json",
                colModel: [
                    {label: '编号', name: 'goodsId', index: 'goodsId', width: 10, key: true},
                    {label: '商品名称', name: 'name', index: 'name', width: 60},
                    {label: '价格', name: 'price', index: '价格', width: 30},

                ],

                viewrecords: true,
                height: 'auto',
                rowNum: 8,
                rownumbers: true,
                rownumWidth: 25,
                autowidth: true,
                multiselect: true,
                pager: "#jqGridPagerAddMsg",
                jsonReader: {
                    root: "goodsPage.list",
                    page: "goodsPage.currPage",
                    total: "goodsPage.totalPage",
                    records: "goodsPage.totalCount"
                },

                prmNames : {
                    page:"page",
                    rows:"limit",
                    order: "order"
                },
                gridComplete: function () {
                    //隐藏grid底部滚动条
                    $("#jqGridAddMsg").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
                    $("#jqGridAddMsg").setGridWidth($("#selectGoods").width());
                }

            });
            for (var i = 0, len = 10; i < len; i++) {
                $("#jqGridAddMsg").jqGrid('addRowData', i + 1, i + 1);
            }
        }
    }
});

vm.initMsgJQqrid();
$("#selectGoods").hide();