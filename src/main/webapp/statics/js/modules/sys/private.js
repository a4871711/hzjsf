$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/private/list',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'privateClassId', index: 'privateClassId', width: 50, key: true},
            {label: '课程名称', name: 'className', index: 'className', width: 80},
            {
                label: '封面图', name: 'firstImgUrl', index: 'firstImgUrl', width: 80,
                formatter: function (value) {
                    return "<img src='" + value + "' weight='80pk' height='80pk' />"
                }
            },
            {label: '私教课图片', name: 'imgUrl', index: 'imgUrl', width: 60,
                formatter:function (value) {
                    var urlArray = value.split(",");
                    return "<img src='"+  urlArray[0]+"' weight='30' height='30pk' />"
                }
            },
            {label: '课时长(分钟)', name: 'classTime', index: 'classTime', width: 80,
                formatter: function (value) {
                    return value == null? "--" : value+"分钟"
                }
            },
            {label: '类型', name: 'classType', index: 'classType', width: 80,hidden:true},
            {label: '课程类型', name: 'classTypeName', index: 'classTypeName', width: 80},
            {label: '教练等级', name: 'grade', index: 'grade', width: 80},
            {label: '课程单价', name: 'classPrice', index: 'classPrice', width: 80,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            {label: '能量消耗（千卡）', name: 'energy', index: 'energy', width: 80,
                formatter: function (value) {
                    return value == null? "--" : value+"kCal"
                }
            },
            {label: '课程详细', name: 'classDetail', index: 'classDetail', width: 80,
                formatter: function (values) {
                    var values = '"' + values + '"';
                    return "<input type='Button' name='name' value='详情' class='btn btn-info but' onclick='but("+values+")'/>"
                }
            },
            {label: '课程有效时间', name: 'validityDay', index: 'validityDay', width: 50},
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
        loadComplete: function (data) {
            vm.classTypeList=data.classType;
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });
    $.each($(".uploads"), function () {
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
                        vm.carouselImgUrl += url + ",";
                        vm.list.firstImgUrl = vm.firstImgUrl.substring(0, vm.firstImgUrl.length - 1);
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
                        vm.list.firstImgUrl = vm.firstImgUrl.substring(0, vm.firstImgUrl.length - 1);
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });
    $.each($(".uploadRemark"), function () {
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
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });

});
function but(values){
    alert(values);
}
var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        list: {},
        imgUrl: "",
        tempArray: [],
        classTypeList: [],
        tempRemarkArray: [],
        q:{
            name:null,
            className:""
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
            $("#selectT").attr("disabled",false)
            $(".logoImg img").attr("src", "/statics/img/addPhoto.svg");
            $(".logoImg img").attr("layer-src", "/statics/img/addPhoto.svg");
        },
        a: function () {
            $("#message").css('display', 'block');
            $("#dengji").val($("#select option:selected").text());
        },
        fillGrade: function (value) {
            $.ajax({
                type: "POST",
                url: baseURL + "sys/private/gradeRule?"+"money="+value,
                contentType: "application/json",
                success: function (r) {
                    if (r.code === 0) {
                        $("#dengji").val(r.gradeRu);
                    } else {
                        $("#dengji").val(4);
                    }
                }
            });
        },
        update: function (event) {
            var privateClassId = getSelectedRow();
            if (privateClassId == null) {
                return;
            }

            vm.showList = false;
            vm.title = "修改";
            vm.list.imgUrl = vm.imgUrl;
            vm.getInfo(privateClassId)
            $("#selectT").attr("disabled",true)
        },
        saveOrUpdate: function (event) {
            var dengji=$("#dengji").val();
            var price=$("#price").val();
            if(vm.validator()){
                return;
            }
            vm.list.firstImgUrl = $("#img img").attr("src");
            var img = "";
            var imgRemark = "";

            $.each($("#images img"), function (index, value) {
                var url = $(this).attr("src");
                if(url.indexOf("addPhoto.svg") ==-1){
                    imgRemark += url + ",";
                }
            });
            vm.list.imgUrl =imgRemark.substring(0, imgRemark.length - 1);
            var url = vm.list.privateClassId == null ? "sys/private/save?dengji="+dengji+"&price="+price : "sys/private/update?dengji="+dengji+"&price="+price;

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
            var privateClassIds = getSelectedRows();
            if (privateClassIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？删除后与私教课相关的部分数据将连带清除，请谨慎操作', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/private/delete",
                    contentType: "application/json",
                    data: JSON.stringify(privateClassIds),
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
        getInfo: function (privateClassId) {
            $.get(baseURL + "sys/private/info/" + privateClassId, function (r) {
                vm.list = r.sysPrivateClassEntity
                var urls = vm.list.firstImgUrl;
                vm.imgUrl = urls;
                $(".logoImg img").attr("src", urls);
                $(".logoImg img").attr("layer-src", urls);
                vm.tempArray = vm.list.imgUrl.split(",");
                vm.tempArray.forEach(function (url, index) {
                    console.log(url);
                    $($(".logo img")[index]).attr("src", url);
                    $($(".logo img")[index]).attr("layer-src", url);
                });

            });
        },
        reload: function (event) {
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'pages');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page,
                postData: {'name':vm.q.name,"className":vm.q.className}
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
        },
        validator: function () {
            if(isBlank(vm.list.className)){
                alert("课程名称不能为空");
                return true;
            }
            if(vm.list.classType == null){
                alert("课程类型不能为空");
                return true;
            }
            if (vm.list.className.lenth>20) {
                alert("课程名称不能超过20字符串");
                return true;
            }
            var retype = /^[1-9]\d*$/;
            if (vm.list.classTime==null || vm.list.classTime<1 || !retype.test(vm.list.classTime)) {
                alert("请输入时长(整数)");
                return true;
            }
            // if (vm.list.leastBuyNum==null || vm.list.leastBuyNum<1) {
            //     alert("请输入最低购买(整数)");
            //     return true;
            // }
            if (vm.list.energy==null || vm.list.energy<1) {
                alert("请输入能量消耗)");
                return true;
            }
            if (vm.list.validityDay==null || vm.list.validityDay<1 || !retype.test(vm.list.validityDay)) {
                alert("请输入有效时间(整数)");
                return true;
            }
            var dengji=$("#dengji").val();
            var price=$("#price").val();
            //var retype = /^[1-9]\d*$/;
            if (dengji>10 ||dengji<1 || !retype.test(dengji)) {
                alert("请输入教练等级1-10(整数)");
                return true;
            }
            var retype= /^(-?\d+)(\.\d{1,2})?$/;
            if (!retype.test(price) || price<0) {
                alert("请输入正确金额数值,若输入金额包含小数位，最多不超过2位小数");
                return true;
            }
        }
    }
});