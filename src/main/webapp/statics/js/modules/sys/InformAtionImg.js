$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/Inform/imgList',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'informationId', index: 'informationId', width: 50, key: true},
            {label: '资讯类型', name: 'imgType', index: 'imgType', width: 80,
                formatter: function (value) {
                    if (value==0){
                        return "星教练"
                    }
                    if (value==1){
                        return "会员说"
                    }
                    if (value==2){
                        return "HOT资讯"
                    }
                    if (value==3){
                        return "燃干货"
                    }
                }

            },

            { label: '列表显示图', name: 'infImgUrl', index: 'infImgUrl', width: 80,
                formatter: function (value) {
                    return "<img src='" + value + "' weight='80pk' height='80pk' />"
                }
            },

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
        }
    });


    $.each($(".upload"), function () {
        var _this = $(this);
        new AjaxUpload($(this), {
            action: baseURL + "sys/upload?width=750&height=375",
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
                        vm.list.infImgUrl = url;
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
            $("#validity").val("");
            $(".logoImg img").attr("src", "/statics/img/addPhoto.svg");
            $(".logoImg img").attr("layer-src", "/statics/img/addPhoto.svg");
        },
        update: function (event) {
            var informationId = getSelectedRow();
            if (informationId == null) {
                return;
            }

            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(informationId)
        },
        saveOrUpdate: function (event) {
            if (vm.validator()) {
                return;
            }
            var url = vm.list.informationId == null ? "sys/Inform/save" : "sys/Inform/update";

            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.list),
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
            var informationId = getSelectedRows();
            if (informationId == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/Inform/delete",
                    contentType: "application/json",
                    data: JSON.stringify(informationId),
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
        getInfo: function (informationId) {
            $.get(baseURL + "sys/Inform/info/" + informationId, function (r) {
                vm.list = r.sysInformAtionEntity
                $("#validity").val(formatDate(new Date(r.sysInformAtionEntity.publishTime)));
                var urls = vm.list.infImgUrl;
                vm.imgUrl = urls;
                $(".logoImg img").attr("src", urls);
                $(".logoImg img").attr("layer-src", urls);

            });
        },
        reload: function (event) {
            vm.list={};
            vm.showList = true;
            var pages = $("#jqGrid").jqGrid('getGridParam', 'pages');
            $("#jqGrid").jqGrid('setGridParam', {
                pages: pages,
                postData: {'fitCard': vm.list, "validity": $("#validity").val()}
            }).trigger("reloadGrid");
        },
        changeTime: function () {
            var new_str = $("#validity").val().replace(/:/g, "-");
            new_str = new_str.replace(/ /g, "-");
            var arr = new_str.split("-");
            var datum = new Date(Date.UTC(arr[0], arr[1] - 1, arr[2], arr[3] - 8, arr[4], arr[5]));
            var strtotime = datum.getTime();
            vm.list.publishTime = strtotime;
        },
        /*预览大图*/
        preview: function () {
            $(document).on("click", ".layui-layer-shade", function () {
                $(".layui-layer-shade").remove();
                $(".layui-layer").remove();
            });
            layer.photos({
                photos: "#img" //格式见API文档手册页
                , anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机
            });
        },
        onChange: function (event) {
            /*var file = event.target.files[0]; // (利用console.log输出看结构就知道如何处理档案资料)
            var url = null;
            if (window.createObjcectURL != undefined) {
                url = window.createOjcectURL(file);
            } else if (window.URL != undefined) {
                url = window.URL.createObjectURL(file);
            } else if (window.webkitURL != undefined) {
                url = window.webkitURL.createObjectURL(file);
            }
            vm.list.trainImgUrl=url;*/
            // do something...
            $.ajaxFileUpload({
                url: baseURL + 'sys/Inform/upload',  //这里是服务器处理的代码
                type: 'post',
                secureuri: false, //一般设置为false
                fileElementId: 'fileUp', // 上传文件的id、name属性名
                dataType: 'json', //返回值类型，一般设置为json、application/json
                success: function (data, status) {
                    $("#image").attr("src", data.msg);
                    vm.list.infImgUrl = data.msg;
                    $("#fileUp").change(vm.onChange);
                },
                error: function (data, status, e) {
                    alert("错误：上传组件错误，请检察网络!");
                }
            });
        },
        validator: function () {
            if (vm.list.imgType==null){
                alert("请选择资讯类型");
                return true;
            }
            if (vm.list.infImgUrl==null || vm.list.infImgUrl==""){
                alert("请选择图片");
                return true;
            }
        }


    }
});