$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/gymengine/list',
        datatype: "json",
        colModel: [
            {label: '编号', name: 'gymEngineId', index: 'gymEngineId', width: 50, key: true},
            {label: '一级分类', name: 'gtName', index: 'gtName', width: 40},
            {label: '器械名称（类型）', name: 'engineName', index: 'engineName', width: 80},
            {
                label: '器械封面', name: 'videoImgUrl', index: 'videoUrl', width: 80,
                formatter: function (value) {
                    return "<img src='" + value + "' controls='controls' weight='150pk' height='150pk'> </img>"
                }
                /*  formatter: function (value) {
                      return "<img src='" + value + "' weight='80pk' height='80pk' />"
                  }*/
            },
            {
                label: '器械视频', name: 'videoUrl', index: 'videoUrl', width: 80,
                formatter: function (value) {
                    return "<video src='" + value + "' controls='controls' weight='150pk' height='150pk'> </video>"
                }
                /*  formatter: function (value) {
                      return "<img src='" + value + "' weight='80pk' height='80pk' />"
                  }*/
            },
            {label: '器械介绍', name: 'introduce', index: 'introduce', width: 80},
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
            $("#jqGrid").setGridWidth($("#jqGrid").width());
        },
        loadComplete: function (r) {
            vm.gymTypeList = r.gymType;
        }
    });

    $.each($(".uploadImg"), function () {
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
                        //vm.imgUrl = url;
                        vm.imgUrl = url;
                        vm.gymEngine.videoImgUrl = url;
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });

    $.each($(".upload"), function () {
        var _this = $(this);
        new AjaxUpload($(this), {
            action: baseURL + "sys/upload",
            name: 'files',
            autoSubmit: true,
            responseType: "json",
            onSubmit: function (file, extension) {
               /* if (!(extension && /^(mp4|mpeg|mpg|avi|rmvb|rm|ogg)$/.test(extension.toLowerCase()))) {
                    alert('只支持rm, rmvb, wmv, avi, mpg, mpeg, mp4,ogg格式的视频！');
                    return false;
                }*/
                if (!(extension && /^(mp4)$/.test(extension.toLowerCase()))) {
                    alert('只支持mp4格式的视频！');
                    return false;
                }
               /* var size = $("[type='file']")[0].files[1].size;
                console.log($("[type='file']")[0].files[1].size);
                if (size > 50 * 1024 * 1024) {
                    alert(file + ' 超出文件上传的大小限制');
                    return false;
                }*/

            },
            onComplete: function (file, r) {
                if (r.code == 1) {
                    var urls = r.data.imgPath;
                    urls.forEach(function (url) {
                        _this.prev().attr("src", url);
                        _this.prev().attr("layer-src", url);
                        //vm.imgUrl = url;

                        vm.imgUrl = url;
                        vm.gymEngine.videoUrl = url;
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });

});
var gyLay;
var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        qq: {},
        gymEngine: {},
        gymType: {},
        gymTypeList: []
    },
    methods: {
        query: function () {
            vm.reload();
        },
        queryGymType: function () {
            vm.reload2();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.gymEngine = {};
            $(".logo video").attr("src", "/statics/img/addPhoto.svg");
            $(".logo video").attr("layer-src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
        },
        update: function (event) {
            vm.gymEngine.videoImgUrl= $(".logo img").attr("src");
            vm.gymEngine.videoUrl= $(".logo video").attr("src");
            var gymEngineId = getSelectedRow();
            if (gymEngineId == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(gymEngineId)
        },
        saveOrUpdate: function (event) {
            vm.gymEngine.introduce = ue.getContent();
            if (vm.validator()) {
                return;
            }
            var url = vm.gymEngine.gymEngineId == null ? "sys/gymengine/save" : "sys/gymengine/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.gymEngine),
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
            var gymengineids = getSelectedRows();
            if (gymengineids == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/gymengine/delete",
                    contentType: "application/json",
                    data: JSON.stringify(gymengineids),
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
        //自定义分类
        gtDefinedType: function () {
             var a = layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-lan',
                title: "自定分类",
                area: ['750px', '500px'],
                shadeClose: false,
                content: $("#gtDefined"),
            });
        },
        editGymType: function (value) {
            if(!isBlank(value)){   //修改
                vm.gymType = $("#jqGridAdd").jqGrid('getRowData',value);
                vm.gymType.gtId = value;   //从新给主键赋值
            }
             gyLay = layer.open({
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
        addGymType: function () {
            vm.gymType = {};   //初始化
            gyLay = layer.open({
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
        deleteGymType: function (value) {
            var gtId = value;
            console.log(value)
            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/gymengine/deleteGt",
                    contentType: "application/json",
                    data: JSON.stringify(gtId),
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
        saveOrUpdateGymType: function () {
            if (vm.validatorGt()) {
                return;
            }
            var url = vm.gymType.gtId == null ? "sys/gymengine/saveGymType" : "sys/gymengine/updateGymType";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.gymType),
                success: function (r) {
                    if (r.code === 0) {
                        alert('操作成功', function (index) {
                            vm.reload2();
                            layer.close(gyLay);
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        getInfo: function (gymengineid) {
            $.get(baseURL + "sys/gymengine/info/" + gymengineid, function (r) {
                vm.gymEngine = r.gymEngine;
                $(".logo img").attr("src", r.gymEngine.videoImgUrl);
                $(".logo img").attr("layer-src", vm.gymEngine.videoImgUrl);
                $(".logo video").attr("src", r.gymEngine.videoUrl);
                $(".logo video").attr("layer-src", vm.gymEngine.videoUrl);
                ue.setContent(r.gymEngine.introduce);
            });
        },
        reload: function (event) {
            UE.getEditor('editor').execCommand('cleardoc');
            vm.gymEngine = {};
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page
            }).trigger("reloadGrid");
        },
        reload2: function (event) {
            var page = $("#jqGridAdd").jqGrid('getGridParam', 'gymPage');
            $("#jqGridAdd").jqGrid('setGridParam', {
                page: page,
                postData: vm.qq
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
                , anim: 1 //0-6的选择，指定弹出图片动画类型，默认随机
            });
        },
        validator: function () {
            var phoneReg = /(^1[3|4|5|7|8]\d{9}$)|(^09\d{8}$)/;
            var passwordReg = /^[0-9a-zA-Z]+$/;
            if (vm.gymEngine.engineName == "" || vm.gymEngine.engineName == null) {
                alert("请输入器械名称");
                return true;
            }
            if (vm.gymEngine.gtId == null) {
                alert("请选择所属一级分类");
                return true;
            }
            if (vm.gymEngine.engineName.length > 20) {
                alert("器械名称请输入20个字符串内");
                return true;
            }
            if (vm.gymEngine.videoImgUrl == null) {
                alert("请上传图片");
                return true;
            }
            if (vm.gymEngine.videoUrl == null) {
                alert("请上传视频");
                return true;
            }
            if (vm.gymEngine.introduce == null || vm.gymEngine.introduce == "") {
                alert("请填写描述");
                return true;
            }

        },
        validatorGt: function () {
            if (isBlank(vm.gymType.gtName)){
                alert("请输入名称");
                return true;
            }
        },
        initJQqrid: function () {
            $("#jqGridAdd").jqGrid({
                url: baseURL + 'sys/gymengine/getGymTypeList',
                datatype: "json",
                colModel: [
                    {label: '编号', name: 'gtId', index: 'gtId', width: 10, key: true, hidden: true},
                    {label: '分类名称', name: 'gtName', index: 'gtName', width: 60},
                    {label:'相关操作',name:'gtId',index:'gtId',width:50,
                        formatter:function (value,option,rowObject) {
                            return '<a href="javascript:void(0)" onclick="vm.editGymType(' + value + ')">编辑</a>&nbsp;&nbsp;' +
                                '<a href="javascript:void(0)" onclick="vm.deleteGymType(' + value + ')">删除</a>'
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
                    root: "gymPage.list",
                    page: "gymPage.currPage",
                    total: "gymPage.totalPage",
                    records: "gymPage.totalCount"
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
                    vm.gymTypeList = r.gymTypeList;
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
$("#gtDefined").hide();