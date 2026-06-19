$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/about/list',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'id', index: 'id', width: 20,key: true },
            {
                label: 'app图标', name: 'appImgUrl', index: 'appImgUrl', width: 50,
                formatter: function (value) {
                    return "<img src='" + value + "' weight='80pk' height='80pk' />"
                }
            },
            {label: 'app名称', name: 'appName', index: 'appName', width: 50},
            {label: '客服电话', name: 'serviceTel', index: 'serviceTel', width: 50},
            {
                label: '创建时间', name: 'createTime', index: 'createTime', width: 50,
                formatter: function (value) {
                    return value == "" || value == null ? "" : formatDate(new Date(value))
                }
            },
            { label: '版本号', name: 'version', index: 'version', width: 30},
            { label: 'app下载链接(Andorid)', name: 'appUrl', index: 'appUrl', width: 80},
            { label: 'app下载链接(IOS)', name: 'appUrlIos', index: 'appUrlIos', width: 80},
            { label: '中文公司名', name: 'companyNameCHN', index: 'companyNameCHN', width: 50},
            { label: '英文公司名', name: 'companyNameENG', index: 'companyNameENG', width: 50},
            { label: '开门限制距离', name: 'openDoor', index: 'openDoor', width: 50},
            { label: '开门二维码有效时间', name: 'qrcodeValid', index: 'qrcodeValid', width: 50}
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
            /*vm.priceTotal = 0;
            var arr = $("#jqGrid").jqGrid('getRowData');
            var arrid = $("#jqGrid").jqGrid('getDataIDs');
            if(arrid.length == 0)return;
            arr.push($("#jqGrid").jqGrid('getRowData',arrid[arrid.length-1]));
            for(var i=0;i<arr.length;i++){
                vm.priceTotal += parseFloat(arr[i].count);
                vm.num = Math.round(vm.priceTotal * 100)/100;
            }*/

            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });

    //设置日期时间控件
    /*function Datetime() {
        $('#datetimepicker1').datetimepicker({
            format: 'YYYY-MM-DD',
            locale: moment.locale('zh-cn')
        });
    }*/

    /**
     * 图片上传
     */
    $.each($(".upload"), function () {
        var _this = $(this);
        new AjaxUpload($(this), {
            action: baseURL + "sys/upload?width=184&height=184",
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
                        vm.appImgUrl += url + ",";
                        vm.list.appImgUrl = vm.appImgUrl.substring(0, vm.appImgUrl.length - 1);
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
        list: {},
        imgUrl: "",
        tempArray: [],
        tempRemarkArray: [],
        q:{
            name:null
        },
        resultGoodsCategoryDetailType:{}

    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.list = {};
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
        },
        update: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            vm.list.appImgUrl = vm.appImgUrl;
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(id)
        },
        saveOrUpdate: function (event) {
            vm.list.appImgUrl = $("#img img").attr("src");
            if (vm.validator()) {
                return;
            }
            var url = vm.list.id == null ? "sys/about/save" : "sys/about/update";
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
            var id = getSelectedRows();
            if (id == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/about/delete",
                    contentType: "application/json",
                    data: JSON.stringify(id),
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
            $.get(baseURL + "sys/about/info/" + id, function (r) {
                vm.list = r.entity;

                $(".logoImg img").attr("src", vm.list.appImgUrl);
                $(".logoImg img").attr("layer-src", vm.list.appImgUrl);
            });
        },
        reload: function (event) {
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
            vm.showList = true;
            var pages = $("#jqGrid").jqGrid('getGridParam', 'pages');
            $("#jqGrid").jqGrid('setGridParam', {
                pages: pages,
            }).trigger("reloadGrid");
        },
        changeTime: function () {
            var new_str = $("#validity").val().replace(/:/g, "-");
            new_str = new_str.replace(/ /g, "-");
            var arr = new_str.split("-");
            var datum = new Date(Date.UTC(arr[0], arr[1] - 1, arr[2], arr[3] - 8, arr[4], arr[5]));
            var strtotime = datum.getTime();
            vm.fitCard.validity = strtotime;
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
            var myreg = /^[1][3,4,5,7,8][0-9]{9}$/;
            if (vm.list.appName.length > 20) {
                alert("app名称请输入20个字符串内");
                return true;
            }

            if (isBlank(vm.list.serviceTel)) {
                alert("客服电话不能为空");
                return true;
            }
            var reg = new RegExp("[\\u4E00-\\u9FFF]+", "g");
            if (reg.test(vm.list.serviceTel)) {
                alert("客服电话不能包含汉字！");
                return true;
            }
            if (vm.list.serviceTel.length > 20) {
                alert("客服电话过长");
                return true;
            }

        }
    }
});