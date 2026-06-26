$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/share/list',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'shareId', index: 'shareId', width: 50, key: true},
            /*	{ label: '用户ID', name: 'userId', index: 'userId', width: 80 }, 	*/
            {
                label: '分享的类型', name: 'shareType', index: 'shareType', width: 80,
                formatter: function (val) {
                    //（1.门店分享，2.运动分享,3.资讯分享）
                    if (val == 1) {
                        return "<label>门店分享</label>"
                    }else  if (val==2){
                        return "<label>运动分享</label>"
                    } else  if (val==3){
                        return "<label>资讯分享</label>"
                    }else {
                        return ""
                    }
                }
            },
            {label: '分享标题', name: 'title', index: 'title', width: 80},
            {label: '分享内容', name: 'content', index: 'content', width: 80},
            {label: '分享链接', name: 'linkUrl', index: 'linkUrl', width: 80},
            {
                label: '分享图片', name: 'imgUrl', index: 'imgUrl', width: 80,
                formatter: function (val) {
                    return "<img src='" + val + "' width='80pk' height='80pk'/>"
                }
            }
            /*{ label: '分享渠道(1:微信 2：朋友圈 3：QQ 4：QQ空间)', name: 'shareChannel', index: 'shareChannel', width: 80 }, 			*/
            /*{ label: '分享时间', name: 'createdDate', index: 'createdDate', width: 80 }	*/
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
    /**
     * 图片上传
     */
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
                    var appImgUrl="";
                    urls.forEach(function (url) {
                        _this.prev().attr("src", url);
                        _this.prev().attr("layer-src", url);
                         appImgUrl+= url + ",";

                    });
                    vm.share.imgUrl = appImgUrl.substring(0, appImgUrl.length - 1);
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
        share: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.share = {};
        },
        update: function (event) {
            var shareId = getSelectedRow();
            if (shareId == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(shareId)
        },
        saveOrUpdate: function (event) {
            var url = vm.share.shareId == null ? "sys/share/save" : "sys/share/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.share),
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
            var shareIds = getSelectedRows();
            if (shareIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/share/delete",
                    contentType: "application/json",
                    data: JSON.stringify(shareIds),
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
        getInfo: function (shareId) {
            $.get(baseURL + "sys/share/info/" + shareId, function (r) {
                vm.share = r.share;
                $(".logo img").attr("src", vm.share.imgUrl);
                $(".logo img").attr("layer-src", vm.share.imgUrl);
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
                photos: "#img" //格式见API文档手册页
                , anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机
            });
        },
        validator: function () {

            /*if (isBlank(vm.share.userId)) {
                layer.msg("用户ID不能为空");
                return false;
            }
            if (isBlank(vm.share.shareType)) {
                layer.msg("分享的类型（1.门店分享，2.运动分享）不能为空");
                return false;
            }*/
            if (isBlank(vm.share.title)) {
                layer.msg("分享标题不能为空");
                return false;
            }
            if (isBlank(vm.share.content)) {
                layer.msg("分享内容不能为空");
                return false;
            }
            if (isBlank(vm.share.linkUrl)) {
                layer.msg("分享链接不能为空");
                return false;
            }
            if (isBlank(vm.share.imgUrl)) {
                layer.msg("分享图片不能为空");
                return false;
            }
            /*  if (isBlank(vm.share.shareChannel)) {
                  layer.msg("分享渠道(1:微信 2：朋友圈 3：QQ 4：QQ空间)不能为空");
                  return false;
              }
              if (isBlank(vm.share.createdDate)) {
                  layer.msg("分享时间不能为空");
                  return false;
              }*/
            return true;
        }
    }
});