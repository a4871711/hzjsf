$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/leve/list',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'lpsId', index: 'lpsId', width: 50, key: true},
            {label: '课程名称', name: 'privateClassId', index: 'privateClassId', width: 80},
            {
                label: '封面图', name: 'img', index: 'img', width: 80,
                formatter: function (value) {
                    return "<img src='" + value + "' weight='80pk' height='80pk' />"
                }
            },
            {label: '价格', name: 'price', index: 'price', width: 80},

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
        },
        update: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }

            vm.showList = false;
            vm.title = "修改";
            vm.list.imgUrl = vm.imgUrl;
            vm.getInfo(id)
        },
        saveOrUpdate: function (event) {
            if (vm.validator()) {
                return;
            }
            var url =  "sys/leve/update";
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
            var privateClassId = getSelectedRows();
            if (privateClassId == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/leve/delete",
                    contentType: "application/json",
                    data: JSON.stringify(privateClassId),
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
        getInfo: function (id) {
            $.get(baseURL + "sys/leve/info/" + id, function (r) {
                vm.list = r.entity
                var urls = vm.list.img;
                $(".logoImg img").attr("src", urls);
                $(".logoImg img").attr("layer-src", urls);
            });
        },
        reload: function (event) {
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'pages');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page,
                postData: {'name':vm.q.name}
            }).trigger("reloadGrid");

        },
        changeTime: function () {
            var new_str = $("#validity").val().replace(/:/g, "-");
            new_str = new_str.replace(/ /g, "-");
            var arr = new_str.split("-");
            var datum = new Date(Date.UTC(arr[0], arr[1] - 1, arr[2], arr[3] - 8, arr[4], arr[5]));
            var strtotime = datum.getTime();
            vm.list.validity = strtotime;
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
        }
    }
});