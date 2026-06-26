$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/coach/list',
        datatype: "json",
        colModel: [
            {label: '编号', name: 'coachId', index: 'coachId', width: 20, key: true},
            {label: '姓名', name: 'coachName', index: 'coachName', width: 35},
            {label: '手机号码', name: 'phone', index: 'phone', width: 50},
            {
                label: '性别', name: 'sex', index: 'sex  ', width: 20,
                formatter: function (value) {
                    if (value == 1) {
                        return '男';
                    } else if (value == 2) {
                        return '女';
                    } else {
                        return '未知';
                    }
                }
            },
            {label: '地址', name: 'province', index: 'province', width: 110},
            {label: '身份证号', name: 'identity', index: 'identity', width: 80},
            {label: '从业年限', name: 'employTime', index: 'employTime', width: 35,
                formatter: function (value) {
                    return value == null? "--" : value+"年"
                }
            },
            {
                label: '身份证正面图', name: 'identImgUrl', index: 'identImgUrl', width: 50,
                formatter: function (value) {
                    return "<img src='" + value + "' weight='30pk' height='30pk' />"
                }
            },
            {
                label: '身份证反面图', name: 'identBackImgUrl', index: 'identBackImgUrl', width: 50,
                formatter: function (value) {
                    return "<img src='" + value + "' weight='30pk' height='30pk' />"
                }
            },
            {
                label: '资历证书', name: 'diplomaImgUrl', index: 'diplomaImgUrl', width: 40,
                formatter: function (value) {
                    return "<img src='" + value + "' weight='30pk' height='30pk' />"
                }
            },
            {label: '教练等级', name: 'grade', index: 'grade', width: 40},
            {
                label: '审核状态', name: 'approveStatus', index: 'approveStatus', width: 35,
                formatter: function (value) {
                    if (value == 0) {
                        return '<span class="label label-info">待审核</span>';
                    } else if (value == 1) {
                        return '<span class="label label-success">审核成功</span>';
                    } else {
                        return '<span class="label label-danger">审核失败</span>';
                    }
                }
            },
            {label: '最低 元/节起', name: 'minClassMoney', index: 'minClassMoney', width: 45,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            {
                label: '创建时间', name: 'createdDate', index: 'createdDate', width: 60,
                formatter: function (value) {
                    return value == "" || value == null ? "" : formatDate(new Date(value))
                }
            },
            {
                label: '审核时间', name: 'approveTime', index: 'approveTime', width: 60,
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
        loadComplete: function (data) {
            vm.coachGradeList = data.coachGrade;
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            // $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });

});

var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        showDetail: true,
        title: null,
        coach: {},
        imgUrl: "",
        tempArray: [],
        coachGradeList: [],
        q: {
            coachName: "",
            approveStatus: "",
            phone: ""
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        details: function (event) {
            /*if(vm.coach.approveStatus != 0){
                alert("请选择待审核的数据操作");
            }*/
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "详情";
            vm.coach.identImgUrl = vm.identImgUrl;
            vm.getInfo(id);
        },
        updateSuccess: function (event) {
            vm.coach.identImgUrl = vm.identImgUrl;
            var retype = /^[1-9]\d*$/;
            if (vm.coach.grade > 10 || vm.coach.grade < 1 || !retype.test(vm.coach.grade)) {
                alert("请输入教练等级1-10(整数)");
                return true;
            }
            var img = "";
            $.each($("#image img"), function (index, value) {
                var url = $(this).attr("src");
                if (url.indexOf("addPhoto.svg") == -1) {
                    img += url + ",";
                }
            });
            vm.coach.identImgUrl = img.substring(0, img.length - 1);
            var url = "sys/coach/updateSuccess";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.coach),
                success: function (result) {
                    if (result.code === 0) {
                        alert('审核成功', function (index) {
                            $.getJSON(baseURL + "sys/message/list", function (r) {
                                console.log(r.page.length);
                                console.log(window.parent.document.getElementById("newMsg"))
                                window.parent.document.getElementById("newMsg").innerHTML = "您有" + r.page.length + "条待审核消息";

                            });
                            vm.reload();
                        });
                    } else {
                        alert(result.msg);
                    }
                }
            });
        },
        updateGrade: function (event) {
            //编辑教练等级和教练从业年限
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            vm.title = "编辑";
            vm.getInfo(id);
            layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-molv',
                title: "编辑",
                area: ['550px', '250px'],
                shade: 0,
                shadeClose: false,
                resize: false,
                content: $("#anewFileWindow"),
            });
        },
        closeAllLayer: function () {
            layer.closeAll();
        },
        updateFailure: function (event) {
            if (vm.validator()) {
                return;
            }

            vm.coach.identImgUrl = vm.identImgUrl;
            var img = "";
            $.each($("#image img"), function (index, value) {
                var url = $(this).attr("src");
                if (url.indexOf("addPhoto.svg") == -1) {
                    img += url + ",";
                }
            });
            vm.coach.identImgUrl = img.substring(0, img.length - 1);
            var url = "sys/coach/updateFailure";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.coach),
                success: function (result) {
                    if (result.code === 0) {
                        alert('操作成功', function (index) {
                            $.getJSON(baseURL + "sys/message/list", function (r) {
                                console.log(r.page.length);
                                console.log(window.parent.document.getElementById("newMsg"))
                                window.parent.document.getElementById("newMsg").innerHTML = "您有" + r.page.length + "条待审核消息";
                            });
                            vm.reload();
                        });
                    } else {
                        alert(result.msg);
                    }
                }
            });
        },
        coachUpdate: function (event) {
            if (vm.validatorUp()) {
                return;
            }

            confirm('是否确定更新？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/coach/coachUpdate",
                    contentType: "application/json",
                    data: JSON.stringify(vm.coach),
                    success: function (result) {
                        if (result.code == 0) {
                            alert('操作成功', function (index) {
                                $("#jqGrid").trigger("reloadGrid");
                                vm.closeAllLayer();
                            });
                        } else {
                            alert(result.msg);
                        }
                    }
                });
            });
        },
        getInfo: function (id) {
            $.get(baseURL + "sys/coach/info/" + id, function (result) {
                vm.coach = result.coach;
                var urls = vm.coach.diplomaImgUrl;
                vm.diplomaImgUrl = urls;
                $(".logoImg img").attr("src", urls);
                $(".logoImg img").attr("layer-src", urls);

                var urls = vm.coach.identImgUrl;
                vm.identImgUrl = urls;
                $(".logo_ima img").attr("src", urls);
                $(".logo_ima img").attr("layer-src", urls);

                var urls = vm.coach.identBackImgUrl;
                vm.identBackImgUrl = urls;
                $(".logo img").attr("src", urls);
                $(".logo img").attr("layer-src", urls);

            });
        },
        reload: function (event) {
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
            vm.showList = true;
            vm.showDetail = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page,
                postData: {'coachName': vm.q.coachName, 'approveStatus': vm.q.approveStatus, "phone": vm.q.phone}
            }).trigger("reloadGrid");

        },
        /*getGoodsCategoryDetail:function(){
            $.get(baseURL + "sys/goodsCategoryDetail/selectGoodsCategoryDetail", function(r){
                vm.resultGoodsCategoryDetailType = r.goodsCategoryDetail;
            });
        },*/
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
                photos: "#ima"
                , anim: 5
            });
        },
        validator: function () {
            if (isBlank(vm.coach.approveResult)) {
                alert("审核不通过，必须填写失败内容");
                return true;
            }
        },
        validatorUp: function () {
            if (isNumber(vm.coach.employTime)) {
                alert("请输入整数");
                return true;
            }
        }
    }
});