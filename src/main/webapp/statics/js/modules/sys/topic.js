$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/topic/list',
        datatype: "json",
        colModel: [
            {label: '话题编号', name: 'topicId', index: 'topicId', width: 50, key: true},
            {label: '话题名称', name: 'topicName', index: 'topicName', width: 80},
            {
                label: '封面图', name: 'firstImgUrl', index: 'firstImgUrl', width: 80,
                formatter: function (value) {
                    return "<img src='" + value + "' weight='80pk' height='80pk'/>"
                }
            },
        /*    {label: '动态数量', name: 'dyNum', index: 'dyNum', width: 80},
            {label: '关注数量', name: 'attentionNum', index: 'attentionNum', width: 80},*/
            {
                label: '话题介绍', name: 'topicIntroduce', index: 'topicIntroduce', width: 80,
                formatter: function (value) {
                    return '<button class="btn btn-info" name="' + value + '" onclick="but(this)">详情</button>'

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
            action: baseURL + "sys/upload?width=160&height=113",
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
                        vm.topic.firstImgUrl = url;
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });


});

function but(value) {
    alert(value.name);
}

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        topic: {},
        numShow: true
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.topic = {};
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
            vm.numShow = false;
        },
        update: function (event) {
            var topicId = getSelectedRow();
            if (topicId == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";
            vm.numShow = true;
            vm.getInfo(topicId)
        },
        saveOrUpdate: function (event) {
            if (vm.validator()) {
                return;
            }
            var url = vm.topic.topicId == null ? "sys/topic/save" : "sys/topic/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.topic),
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
            var topicIds = getSelectedRows();
            if (topicIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/topic/delete",
                    contentType: "application/json",
                    data: JSON.stringify(topicIds),
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
        getInfo: function (topicid) {
            $.get(baseURL + "sys/topic/info/" + topicid, function (r) {
                vm.topic = r.topic;
                $(".logo img").attr("src", r.topic.firstImgUrl);
                $(".logo img").attr("layer-src", vm.topic.firstImgUrl);
            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page
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
            if (vm.topic.topicName == "" || vm.topic.topicName == null) {
                alert("话题不能为空");
                return true;
            }
            if (vm.topic.topicName.length > 32) {
                alert("话题超长");
                return true;
            }
            if (vm.topic.firstImgUrl == null) {
                alert("请上传图片");
                return true;
            }
          /*  if (isNumber(vm.topic.dyNum)) {
                alert("动态数量请输入数字");
                return true;
            }
            if (vm.topic.dyNum.length > 10) {
                alert("动态数量太长");
                return true;
            }
            if (isNumber(vm.topic.attentionNum)) {
                alert("关注数量请输入数字");
                return true;
            }
            if (vm.topic.attentionNum.length > 10) {
                alert("关注数量超长");
                return true;
            }*/
            if (vm.topic.topicIntroduce == "" || vm.topic.topicIntroduce == null) {
                alert("请输入话题介绍");
                return true;
            }
            if (vm.topic.topicIntroduce.length > 128) {
                alert("话题介绍超长");
                return true;
            }
        }
    }
});