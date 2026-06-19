 $(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/activ/list',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'activityTrainId', index: 'activityTrainId', width: 50, key: true},
            {label: '一级分类', name: 'atName', index: 'atName', width: 40},
            {
                label: '视频链接图', name: 'videoImgUrl', index: 'videoImgUrl', width: 80,
                formatter: function (value,o,r) {

                    return "<img src='" + value + "' weight='80pk' height='80pk' />"
                }
            },
            {
                label: '视频链接', name: 'videoUrl', index: 'videoUrl', width: 80,
                formatter: function (value) {
                    return "<video width=\"240\" height=\"240\" controls autoplay>\n" +
                            "\n" +
                            "  <source src='" + value + "' type=\"video/mp4\">\n" +
                            "\n" +
                            "\n" +
                            "</video>"
                }
            },
            {label: '动作名称', name: 'trainName', index: 'trainName', width: 80},
            // {
            //     label: '动作图片', name: 'trainImgUrl', index: 'trainImgUrl', width: 80,
            //     formatter: function (value) {
            //         return "<img src='" + value + "' weight='80pk' height='80pk' />"
            //     }
            // },
            {label: '动作介绍', name: 'introduce', index: 'introduce', width: 50},
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
        },
        loadComplete: function (r) {
            vm.activTypeList = r.activTypeList;
        }
    });
    $.each($(".upload"), function () {
        var _this = $(this);
        new AjaxUpload($(this), {
            action: baseURL + "sys/upload?width=750&height=376",
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
                        vm.list.videoImgUrl = url;
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });
    $.each($(".uploads"), function () {
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
                        vm.list.trainImgUrl = url;
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });
    $.each($(".uploadss"), function () {
        var _this = $(this);
        new AjaxUpload($(this), {
            action: baseURL + "sys/upload",
            name: 'files',
            autoSubmit: true,
            responseType: "json",
            onSubmit: function (file, extension) {
                if (!(extension && /^(mp4)$/.test(extension.toLowerCase()))) {
                    alert('只支持mp4格式的视频！');
                    return false;
                }
//                var size = $("[type='file']")[0].files[0].size;
////                console.log($("[type='file']")[0].files[0].size);
//                if (size > 50 * 1024 * 1024) {
//                    alert(file + ' 超出文件上传的大小限制');
//                    return false;
//                }

            },
            onComplete: function (file, r) {
                if (r.code == 1) {
                    var urls = r.data.imgPath;
                    urls.forEach(function (url) {
                        _this.prev().attr("src", url);
                        _this.prev().attr("layer-src", url);
                        //vm.imgUrl = url;

                        vm.imgUrl = url;
                        vm.list.videoUrl = url;
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });
});
var ayLay;
var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        qq: {},
        q: {},
        activType: {},
        activTypeList: [],
        list: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.list = {};
            $(".logoImg img").attr("src", "/statics/img/addPhoto.svg");
            $(".logoImg img").attr("layer-src", "/statics/img/addPhoto.svg");
            $(".logo video").attr("src", "/statics/img/addPhoto.svg");
            $(".logo video").attr("layer-src", "/statics/img/addPhoto.svg");

        },
        update: function (event) {
            var activityTrainId = getSelectedRow();
            if (activityTrainId == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";
            vm.getInfo(activityTrainId)
        },
        saveOrUpdate: function (event) {
            vm.list.introduce = ue.getContent();
            if (vm.validator()) {
                return;
            }
            var url = vm.list.activityTrainId == null ? "sys/activ/save" : "sys/activ/update";

            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.list),
                success: function (r) {
                    if (r.code === 0) {
                        alert('操作成功', function (index) {
                            $("#jqGrid").trigger("reloadGrid");
                            vm.reload();
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        del: function (event) {
            var activityTrainId = getSelectedRows();
            if (activityTrainId == null) {
                return;
            }
            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/activ/delete",
                    contentType: "application/json",
                    data: JSON.stringify(activityTrainId),
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
        getInfo: function (activityTrainId) {
            $.get(baseURL + "sys/activ/info/" + activityTrainId, function (r) {
                vm.list = r.activityTrainEntity
                ue.setContent(r.activityTrainEntity.introduce);
                var urls = vm.list.videoImgUrl;
                $(".logoImg img").attr("src", urls);
                $(".logoImg img").attr("layer-src", urls);
                var url = vm.list.trainImgUrl;
                $(".logoImgs img").attr("src", url);
                $(".logoImgs img").attr("layer-src", url);
                $(".logo video").attr("src", vm.list.videoUrl);
                $(".logo video").attr("layer-src", vm.list.videoUrl);

            });
        },
        reload: function (event) {
            UE.getEditor('editor').execCommand('cleardoc');
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'pages');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page,
                postData: {'name': vm.q.name}
            }).trigger("reloadGrid");

        },
        preview: function () {
            $(document).on("click", ".layui-layer-shade", function () {
                $(".layui-layer-shade").remove();
                $(".layui-layer").remove();
            });
            layer.photos({
                photos: "#img" //格式见API文档手册页
                , anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机
            });
            layer.photos({
                photos: "#img" //格式见API文档手册页
                , anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机
            });
        },
        validator: function () {
            if (vm.list.atId == null) {
                alert("请选择一级分类");
                return true;
            }
            if (isBlank(vm.list.trainName)||vm.list.trainName.length > 20) {
                alert("动作名称请输入20个字符串内");
                return true;
            }
            if (isBlank(vm.list.videoImgUrl)) {
                alert("请选择视频图");
                return true;
            }
            if (isBlank(vm.list.videoUrl)) {
                alert("请选择视频链接");
                return true;
            }
            
        },
        queryActivType: function () {
            vm.reload2();
        },
        //自定义分类
        atDefinedType: function () {
            var a = layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-lan',
                title: "自定分类",
                area: ['750px', '500px'],
                shadeClose: false,
                content: $("#atDefined"),
            });
        },
        editactivType: function (value) {
            if(!isBlank(value)){   //修改
                vm.activType = $("#jqGridAdd").jqGrid('getRowData',value);
                vm.activType.atId = value;   //从新给主键赋值
            }
            ayLay = layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-lan',
                title: "编辑",
                area: ['300px', '150px'],
                shade: 0,
                shadeClose: false,
                resize:false,
                content: $("#anewFileWindow")
            });
        },
        addActivType: function () {
            vm.activType = {};   //初始化
            ayLay = layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-lan',
                title: "新增",
                area: ['300px', '150px'],
                shade: 0,
                shadeClose: false,
                resize:false,
                content: $("#anewFileWindow")
            });
        },
        deleteactivType: function (value) {
            var atId = value;
            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/activ/deleteAt",
                    contentType: "application/json",
                    data: JSON.stringify(atId),
                    success: function (r) {
                        if (r.code == 0) {
                            alert('删除成功', function (index) {
                                vm.reload2();
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        saveOrUpdateActivType: function () {
            if (vm.validatorAt()) {
                return;
            }
            var url = vm.activType.atId == null ? "sys/activ/saveActivType" : "sys/activ/updateActivType";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.activType),
                success: function (r) {
                    if (r.code === 0) {
                        alert('操作成功', function (index) {
                            vm.reload2();
                            layer.close(ayLay);
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        reload2: function (event) {
            var page = $("#jqGridAdd").jqGrid('getGridParam', 'activPage');
            $("#jqGridAdd").jqGrid('setGridParam', {
                page: page,
                postData: vm.qq
            }).trigger("reloadGrid");

        },
        validatorAt: function () {
            if (isBlank(vm.activType.atName)){
                alert("请输入名称");
                return true;
            }
        },
        initJQqrid: function () {
            $("#jqGridAdd").jqGrid({
                url: baseURL + 'sys/activ/getActivTypeList',
                datatype: "json",
                colModel: [
                    {label: '编号', name: 'atId', index: 'atId', width: 10, key: true, hidden: true},
                    {label: '分类名称', name: 'atName', index: 'atName', width: 60},
                    {label:'相关操作',name:'atId',index:'atId',width:50,
                        formatter:function (value,option,rowObject) {
                            return '<a href="javascript:void(0)" onclick="vm.editactivType(' + value + ')">编辑</a>&nbsp;&nbsp;' +
                                '<a href="javascript:void(0)" onclick="vm.deleteactivType(' + value + ')">删除</a>'
                        }
                    }
                ],

                viewrecords: true,
                height: 385,
                rowNum: 10,
                rowList: [10, 30, 50],
                rownumbers: true,
                rownumWidth: 25,
                autowidth: true,
                multiselect: true,
                pager: "#jqGridPagerAdd",
                jsonReader: {
                    root: "activPage.list",
                    page: "activPage.currPage",
                    total: "activPage.totalPage",
                    records: "activPage.totalCount"
                },
                prmNames : {
                    page:"page",
                    rows:"limit",
                    order: "order"
                },
                gridComplete: function () {
                    //隐藏grid底部滚动条
                    $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
                },
                loadComplete: function (r) {
                    vm.activTypeList = r.activTypeList;
                },
                ondblClickRow: function () {
                    var row_id = $("#jqGridAdd").getGridParam('selrow');
                    jQuery('＃jqGridAdd').editRow(row_id, true);
                }

            });
            for (var i = 0, len = 10; i < len; i++) {
                $("#jqGridAdd").jqGrid('addRowData', i + 1, i + 1);
            }
        }
    }
});

 vm.initJQqrid();
 $("#atDefined").hide();