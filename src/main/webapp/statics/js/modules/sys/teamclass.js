$(function () {
    vm.getStoreNameDetail();
    vm.teamClassValue();
    // vm.getStoreCoach();
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/teamClass/list',
        datatype: "json",
        colModel: [
            {label: '编号', name: 'teamClassId', index: 'teamClassId', width: 20, key: true},
            {label: '课程门店', name: 'storeName', index: 'storeName', width: 50},
            {label: '团体课名称', name: 'teamClassName', index: 'teamClassName', width: 50},
            {
                label: '团体课类型', name: 'teamClassType', index: 'teamClassType', width: 30,
                formatter: function (value) {
                    return value === 0 ?
                        '<span class="label label-info">免费</span>' :
                        '<span class="label label-success">精品</span>';
                }
            },
            {label: '团体课价格', name: 'classPrice', index: 'classPrice', width: 30,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            {label: '总名额', name: 'totalNum', index: 'totalNum', width: 30},
            {label: '教练名称', name: 'coachName', index: 'coachName', width: 30},
            {label: '能量（千卡）', name: 'energy', index: 'energy', width: 30,
                formatter: function (value) {
                    return value == null? "--" : value+"kCal"
                }
            },
            {label: '场所', name: 'room', index: 'room', width: 60},
            {label: '地址', name: 'place', index: 'place', width: 60},
            {label: '上课时间', name: 'classTime',width:60},
            {
                label: '创建时间', name: 'createdDate', index: 'createdDate', width: 60,
                formatter: function (value) {
                    return value == "" || value == null ? "" : formatDate(new Date(value))
                }
            }
        ],
        viewrecords: true,
        rowNum: 10,
        rowList: [10, 30, 50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth: true,
        height: 'auto',
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
            action: baseURL + "sys/upload?width=690&height=250",
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
                        vm.firstImgUrl = url;
                        vm.teamClass.firstImgUrl = url;
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
        teamClass: {},
        firstImgUrl: "",
        Comm: "",
        resultStoreName: "",
        q: {
            teamClassName: null,
            storeName: null
        },
        ue: {},
        resultStoreCoach:{}

    },
    methods: {
        query: function () {
            vm.reload();
        },
        teamClassValue: function () {
            var comm = $("#teamClassType").val();
            vm.Comm = comm;
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.teamClass = {};
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
            ue.setContent("");
            vm.teamClassValue();
            document.getElementById("sdate").value = "";
            document.getElementById("edate").value = "";
        },
        update: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";
            vm.teamClass.firstImgUrl = vm.firstImgUrl;
            vm.getInfo(id);
            vm.teamClassValue();
        },
        saveOrUpdate: function (event) {
            vm.teamClass.classDetail = ue.getContent();
            if (vm.validator()) {
                return;
            }
            vm.teamClass.firstImgUrl = vm.firstImgUrl;
            var p = $("#pdate").val();
            var s = $("#sdate").val();
            var e = $("#edate").val();
            var str =p+" "+s + "~" + e;
            vm.teamClass.classTime = str;
            var url = vm.teamClass.teamClassId == null ? "sys/teamClass/save" : "sys/teamClass/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.teamClass),
                success: function (result) {
                    if (result.code === 0) {
                        alert('操作成功', function (index) {
                            vm.reload();
                        });
                    } else {
                        alert(result.msg);
                    }
                }
            });
        },
        cancel: function (event) {
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
                    url: baseURL + "sys/teamClass/delete",
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
            $.get(baseURL + "sys/teamClass/info/" + id, function (result) {
                vm.teamClass = result.teamClass;
                ue.setContent(result.teamClass.classDetail);
                var urls = vm.teamClass.firstImgUrl;
                vm.firstImgUrl = urls;
                $(".logo img").attr("src", urls);
                $(".logo img").attr("layer-src", urls);
                vm.Comm = vm.teamClass.teamClassType;
                var temp = vm.teamClass.classTime;
                var p = temp.substring(0, temp.indexOf(' '));
                vm.teamClass.classTime = p;
                var s = temp.substring(temp.indexOf(' '), temp.indexOf('~'));
                document.getElementById("sdate").value = s.trim();
                var index = temp.lastIndexOf("~");
                var e = temp.substring(index + 1, temp.length);
                document.getElementById("edate").value = e;
            });
        },
        reload: function (event) {
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page,
                postData: {'teamClassName': vm.q.teamClassName,'storeName': vm.q.storeName}
            }).trigger("reloadGrid");
        },
        getStoreNameDetail: function () {
            $.get(baseURL + "sys/teamClass/selectStoreName", function (r) {
                vm.resultStoreName = r.storeNameDetail;
            });
        },
        /*预览大图*/
        preview: function () {
            $(document).on("click", ".layui-layer-shade", function () {
                $(".layui-layer-shade").remove();
                $(".layui-layer").remove();
            });
            layer.photos({
                photos: "#image"
                , anim: 5
            });
        },

        validator: function () {
            var p = $("#pdate").val();
            var s = $("#sdate").val();
            var e = $("#edate").val();
            if(isBlank(p) || isBlank(s) || isBlank(e)){
                layer.msg("时间选项不能为空");
                return true;
            }
            var t11=s.split(":");
            var t21=e.split(":");
            var sj1 = parseInt(t11[0])*12 + t11[1];
            var sj2 = parseInt(t21[0])*12 + t21[1]
            if (sj1 > sj2){
                layer.msg("开始时间不能大于结束时间");
                return true;
            }

            if (isBlank(vm.teamClass.storeId)) {
                alert("请选择门店");
                return true;
            }
            if (isBlank(vm.teamClass.teamClassName)) {
                alert("团体课名称不能为空");
                return true;
            }
            if (vm.teamClass.teamClassType == null) {
                alert("请选择团体课类型");
                return true;
            }
            if (vm.Comm == 1) {
                if (isBlank(vm.teamClass.classPrice)) {
                    alert("团体课价格不能为空");
                    return true;
                }
            }
            if (isBlank(vm.teamClass.totalNum)) {
                alert("总名额不能为空");
                return true;
            }
            /*if(vm.teamClass.totalNum < 0 || vm.teamClass.totalNum>200){
                alert("总名额不能小于0并且不能大于0");
                return true;
            }*/
            if (vm.teamClass.teamClassType == 1) {//精品课程
                var retype= /^(-?\d+)(\.\d{1,2})?$/;
                if(isBlank(vm.teamClass.classPrice)){
                    alert("团体课价格不能为空");
                    return;
                }
                if(vm.teamClass.classPrice !=0){
                    if(!retype.test(vm.teamClass.classPrice) || vm.teamClass.classPrice < 0){
                        alert("请输入正确金额数值,若输入金额包含小数位，最多不超过2位小数");
                        return true;
                    }
                }
            }
            if (isBlank(vm.teamClass.coachName)) {
                alert("教练名称不能为空");
                return true;
            }
            if (isBlank(vm.teamClass.energy)) {
                alert("能量不能为空");
                return true;
            }
            if (isBlank(vm.teamClass.room)) {
                alert("场所不能为空");
                return true;
            }
            if (isBlank(p)) {
                alert("请选择日期");
                return true;
            }
            if (isBlank(s)) {
                alert("请选择开始时间");
                return true;
            }
            if (isBlank(e)) {
                alert("请选择结束时间");
                return true;
            }
            if (isBlank(vm.teamClass.place)) {
                alert("地址不能为空");
                return true;
            }
            if (isBlank(vm.teamClass.introduce)) {
                alert("课程介绍不能为空");
                return true;
            }
            if (isBlank(vm.teamClass.classLabel)) {
                alert("课程标签不能为空");
                return true;
            }
            if (isBlank(vm.teamClass.classDetail)) {
                alert("课程详情不能为空");
                return true;
            }
            /*if (isBlank(vm.teamClass.classImgUrl)) {
                alert("图片链接不能为空");
                return true;
            }*/
        }
    }
});