$(function () {
    vm.getStoreNameDetail();
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/storecoach/list',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'scId', index: 'scId', width: 50, key: true},
           {label: '所属门店', name: 'storeName', index: 'storeName', width: 80},
            {label: '教练等级', name: 'grade', index: 'grade', width: 80},
            {
                label: '教练头像', name: 'headImgUrl', index: 'headImgUrl', width: 80,
                formatter: function (v) {
                    if (v != null) {
                        return "<img src='" + v + "' width='80pk' height='80pk'/>"
                    }
                }
            },
            {label: '姓名', name: 'coachName', index: 'coachName', width: 80},
            {label: '手机号', name: 'phone', index: 'phone', width: 80},
            {
                label: '性别', name: 'sex', index: 'sex', width: 80,
                formatter: function (v) {
                    if (v == 1) {
                        return "男"
                    } else if (v == 2) {
                        return "女"
                    } else {
                        return "未知"
                    }
                }
            },
            {label: '省市区', name: 'address', index: 'address', width: 80},
            {label: '身份证号', name: 'identity', index: 'identity', width: 80},
            {label: '从业年限', name: 'employTime', index: 'employTime', width: 80},
            {
                label: '资历证书', name: 'diplomaImgUrl', index: 'diplomaImgUrl', width: 80,
                formatter: function (v) {
                    var returnStr="";
                    if (v!=null){
                        var imgs=v.split(',');
                        for (var i=0;i<imgs.length;i++){
                            returnStr+="<img src='"+imgs[i]+"' width='80pk' height='80pk'/>"
                        }
                    }
                    return returnStr
                }
            },
            {
                label: '身份证反面', name: 'identBackImgUrl', index: 'identBackImgUrl', width: 80,
                formatter:

                    function (v) {
                        if (v != null) {
                            return "<img src='" + v + "' width='80pk' height='80pk'/>"
                        }
                    }
            },
            {
                label: '身份证正面', name:
                'identImgUrl', index:
                'identImgUrl', width:
                80,
                formatter: function (v) {
                    if (v != null) {
                        return "<img src='" + v + "' width='80pk' height='80pk'/>"
                    }
                }
            }
            ,
            {
                label: '薪水(元）', name:
                'salary', index:
                'salary', width:
                80
            }
            ,
            {
                label: '评价星级', name:
                'level', index:
                'level', width:
                80
            }
            ,
            {
                label: '状态', name:
                'status', index:
                'status', width:
                80,
                formatter: function (v) {
                    if (v == 1) {
                        return "上架"
                    } else if (v == 2) {
                        return "下架"
                    }
                }
            }
            ,
            {
                label: '教练简介', name:
                'introduce', index:
                'introduce', width:
                80,
                formatter:function (v) {
                    return "<input type='Button' name='name' value='详情' class='btn btn-info but' data-msg='"+v+"' onclick='showInfo(this)'/>"
                }
            }
            ,
            {
                label: '创建时间', name:
                'createdDate', index:
                'createdDate', width:
                80, formatter: function (v) {
                return v == "" || v == null ? "" : formatDate(new Date(v))
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
        pager: "#jqGridPager",
        jsonReader: {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        }
        ,
        prmNames: {
            page: "page",
            rows:
                "limit",
            order:
                "order"
        },
        gridComplete: function () {
            //隐藏grid底部滚动条
            //    $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    })
    $.each($(".uploadHeader"), function () {
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
                        vm.storeCoach.headImgUrl = url;
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });
    $.each($(".uploadidz"), function () {
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
                        vm.storeCoach.identImgUrl = url;
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });
    $.each($(".diplomaImgUrl"), function () {
        var _this = $(this);
        new AjaxUpload($(this), {
            action: baseURL + "sys/upload?width=702&height=1146",
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
                        _this.prev().prev().attr("src", url);
                        _this.prev().prev().attr("layer-src", url);
                        vm.storeCoach.diplomaImgUrl = url;
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });
    $.each($(".uploadidf"), function () {
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
                        vm.storeCoach.identBackImgUrl = url;
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });
    ;
});


var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        showEdit: false,
        showEvaluate: false,
        title: null,
        storeCoach: {},
        infoMsg:"",
        resultStoreName:[],
        q:{},
        ad1ImgsList:[]
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.showEdit = true;
            vm.title = "新增";
            vm.storeCoach = {};
            $(".uploadidf").prev().attr("src", " ../../statics/img/addPhoto.svg");
            $(".uploadidf").prev().attr("layer-src", "../../statics/img/addPhoto.svg");
            $(".uploadidz").prev().attr("src", "../../statics/img/addPhoto.svg");
            $(".uploadidz").prev().attr("layer-src", "../../statics/img/addPhoto.svg");
            $(".diplomaImgUrl").prev().attr("src", "../../statics/img/addPhoto.svg");
            $(".diplomaImgUrl").prev().attr("layer-src", "../../statics/img/addPhoto.svg");
            $(".uploadHeader").prev().attr("src", "../../statics/img/addPhoto.svg");
            $(".uploadHeader").prev().attr("layer-src", "../../statics/img/addPhoto.svg");
        },
        uploadT: function () {
            if (vm.ad1ImgsList.length<5){
                vm.ad1ImgsList.push("");
            }else{
                alert("上传图片最多五张！");
            }
        },
        //删除广告屏幕上传
        removeT: function (e) {
            var index = $(e.target).parent().index();
            vm.ad1ImgsList.splice(index, 1);
        },
        uploadImage1: function (e) {
            var filetype = e.target.value.substring(e.target.value.lastIndexOf(".") + 1, e.target.value.length);
            if (!/^(jpg|jpeg|png|gif)$/.test(filetype.toLowerCase())) {
                alert('只支持jpg、jpeg、png、gif格式的图片！');
                return false;
            }
            var index = $(e.target).parent().index();
            $(e.target).attr("id", "imageUpload1" + index);
            ajaxFileUpload(e.target.id, function (e, url) {
               /* vm.ad1ImgsList.splice(index, 1, url);
                $("#" + e).change(vm.uploadImage1);
                $("#" + e).attr("id", "");*/
                vm.ad1ImgsList.splice(index, 1, url);
                $("#" + e).change(vm.uploadImage1);
                $("#" + e).attr("id", "");

                var ind = e.charAt(e.length - 1);
               /* if (url == null) {
                    vm.ad1ImgsList.splice(parseInt(ind), 1);
                    vm.uploadT();
                }*/
            });
        },
        update: function (event) {
            var scId = getSelectedRow();
            if (scId == null) {
                return;
            }
            vm.showList = false;
            vm.showEdit = true;
            vm.title = "修改";

            vm.getInfo(scId)
        }
        ,
        saveOrUpdate: function (event) {

            vm.storeCoach.diplomaImgUrl="";
            if (vm.ad1ImgsList != null && vm.ad1ImgsList != '') {
                for(var i=0;i<vm.ad1ImgsList.length;i++){
                    if (!isBlank(vm.ad1ImgsList[i])){
                    vm.storeCoach.diplomaImgUrl +=vm.ad1ImgsList[i]+",";
                  }
                }
                vm.storeCoach.diplomaImgUrl=   vm.storeCoach.diplomaImgUrl.substr(0,vm.storeCoach.diplomaImgUrl.length-1);
            }
            if (!vm.validator()) {
                return;
            }
            var url = vm.storeCoach.scId == null ? "sys/storecoach/save" : "sys/storecoach/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.storeCoach),
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
        getStoreNameDetail: function () {
            $.get(baseURL + "sys/teamClass/selectStoreName", function (r) {
                vm.resultStoreName = r.storeNameDetail;
            });
        },
        del: function (event) {
            var scIds = getSelectedRows();
            if (scIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/storecoach/delete",
                    contentType: "application/json",
                    data: JSON.stringify(scIds),
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
        }
        ,
        getInfo: function (scId) {
            $.get(baseURL + "sys/storecoach/info/" + scId, function (r) {
                vm.storeCoach = r.storeCoach;
                $(".uploadidf").prev().attr("src", r.storeCoach.identBackImgUrl);
                $(".uploadidf").prev().attr("layer-src", r.storeCoach.identBackImgUrl);
                $(".uploadidz").prev().attr("src", r.storeCoach.identImgUrl);
                $(".uploadidz").prev().attr("layer-src", r.storeCoach.identImgUrl);
             //   $(".diplomaImgUrl").prev().attr("src", r.storeCoach.diplomaImgUrl);
               // $(".diplomaImgUrl").prev().attr("layer-src", r.storeCoach.diplomaImgUrl);
                $(".uploadHeader").prev().attr("src", r.storeCoach.headImgUrl);
                $(".uploadHeader").prev().attr("layer-src", r.storeCoach.headImgUrl);
                vm.ad1ImgsList = r.storeCoach.diplomaImgUrl == null || r.storeCoach.diplomaImgUrl == "" ? [] :  r.storeCoach.diplomaImgUrl.split(",");

            });
        }, preview: function () {
            $(document).on("click", ".layui-layer-shade", function () {
                $(".layui-layer-shade").remove();
                $(".layui-layer").remove();
            });
            layer.photos({
                photos: "#form" //格式见API文档手册页
                , anim: 1 //0-6的选择，指定弹出图片动画类型，默认随机
            });
        },
        reload: function (event) {
            vm.showList = true;
            vm.showEdit = false;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page,
                postData:vm.q
            }).trigger("reloadGrid");
        }
        ,
        validator: function () {
            var myreg = /^[1][3,4,5,6,7,8,9][0-9]{9}$/;
            /*   if (isBlank(vm.storeCoach.storeId)) {
                   layer.msg("所属门店不能为空");
                   return false;
               }*/
            if (isBlank(vm.storeCoach.grade)) {
                layer.msg("教练等级不能为空");
                return false;
            }
            if (isNumber(vm.storeCoach.grade)) {
                layer.msg("教练等级只能为数字");
                return false;
            }
            if (isBlank(vm.storeCoach.headImgUrl)) {
                layer.msg("请上传教练头像");
                return false;
            }
            if (isBlank(vm.storeCoach.coachName)) {
                layer.msg("姓名不能为空");
                return false;
            }

            if (isBlank(vm.storeCoach.phone)) {
                layer.msg("手机号不能为空");
                return false;
            }
            if (isNumber(vm.storeCoach.phone)) {
                layer.msg("请输入正确的手机号码");
                return false;
            }
            if (vm.storeCoach.phone.length!=11) {
                layer.msg("请输入正确的手机号码");
                return false;
            }
            if (isBlank(vm.storeCoach.sex)) {
                layer.msg("请选择性别");
                return false;
            }
            if (isBlank(vm.storeCoach.address)) {
                layer.msg("省市区不能为空");
                return false;
            }
            if (isBlank(vm.storeCoach.identity)) {
                layer.msg("身份证号不能为空");
                return false;
            }
            if (isBlank(vm.storeCoach.employTime)) {
                layer.msg("从业年限不能为空");
                return false;
            }
            if (isNumber(vm.storeCoach.employTime)) {
                layer.msg("从业年限只能为数字");
                return false;
            }
            /*if (isBlank(vm.storeCoach.diplomaImgUrl)) {
                layer.msg("资历证书不能为空");
                return false;
            }*/
            /*if (isBlank(vm.storeCoach.identBackImgUrl)) {
                layer.msg("身份证反面不能为空");
                return false;
            }
            if (isBlank(vm.storeCoach.identImgUrl)) {
                layer.msg("身份证正面不能为空");
                return false;
            }
            if (isBlank(vm.storeCoach.salary)) {
                layer.msg("薪水(元）不能为空");
                return false;
            }
            if (isBlank(vm.storeCoach.level)) {
                layer.msg("评价星级*（*数，创建时默认0）不能为空");
                return false;
            }*/
            if (isBlank(vm.storeCoach.status)) {
                layer.msg("请选择状态");
                return false;
            }
            /*  if (isBlank(vm.storeCoach.introduce)) {
                  layer.msg("教练简介不能为空");
                  return false;
              }*/
            return true;
        },
        evaluate: function () {
            var scId = getSelectedRow();
            if (scId == null) {
                return;
            }
            vm.showList = false;
            vm.showEdit = false;
            vm.showEvaluate = true;
            var page = $("#jqGrid2").jqGrid('getGridParam', 'page');
            $("#jqGrid2").jqGrid('setGridParam', {
                page: page,
                postData: {"scId": scId}
            }).trigger("reloadGrid");
            $("#jqGrid2").setGridWidth($("#jqGrid").width()*0.99);
        },
        goBack: function () {
            vm.showList = true;
            vm.showEdit = false;
            vm.showEvaluate = false;
        }
    }
});

$(function () {
    $("#jqGrid2").jqGrid({
        url: baseURL + 'sys/storecoachevaluate/list',
        datatype: "json",
        colModel: [
            {label: '序号', name: 'evaluateId', index: 'evaluateId', width: 50, key: true},
            {label: '教练', name: 'coachName', index: 'coachName', width: 80},
            {label: '评论用户', name: 'userName', index: 'userName', width: 80},
            {
                label: '头像', name: 'headImgUrl', index: 'headImgUrl', width: 80,
                formatter: function (v) {
                    if (v != null) {
                        return "<img src='" + v + "' width='80pk' height='80pk'/>"
                    }
                }
            },
            {label: '昵称', name: 'nickname', index: 'nickname', width: 80},
            {label: '评价等级(*级)', name: 'evLevel', index: 'evLevel', width: 80},
            {label: '评价内容', name: 'evContent', index: 'evContent', width: 80,
            formatter:function (v) {

                return "<input type='Button' name='name' value='详情' class='btn btn-info but' data-msg='"+v+"' onclick='showInfo(this)'/>"
            }},
            {
                label: '评论图', name: 'evaluatImgUrl', index: 'evaluatImgUrl', width: 80,
                formatter: function (v) {
                    var returnStr="";
                    if (v!=null){
                        var imgs=v.split(',');
                        for (var i=0;i<imgs.length;i++){
                            returnStr+="<img src='"+imgs[i]+"' width='80pk' height='80pk'/>"
                        }
                    }
                    return returnStr
                }
            },
            {label: '评论时间', name: 'evaluatDate', index: 'evaluatDate', width: 80,
            formatter:function (v) {
                return v=="" || v==null?"":formatDate(new Date(v))
            }}
        ],
        viewrecords: true,
        height: 385,
        rowNum: 10,
        rowList: [10, 30, 50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth: true,
        multiselect: true,
        pager: "#jqGridPager2",
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
            $("#jqGrid2").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });
});

function  showInfo(e) {
    alert(e.getAttribute("data-msg"))

}

function ajaxFileUpload(id, e) {
    $.ajaxFileUpload(
        {
            url: baseURL + "sys/uploads", //用于文件上传的服务器端请求地址
            secureuri: false,
            fileElementId: id, //文件上传域的ID
            dataType: 'json',
            success: function (data, status) {
                if (data.code == 0) {
                    if (e)
                        e(id, data.path[0]);
                }
                else {
                    alert(data.msg);
                    e(id, null);
                }
            },
            error: function (data, status, e)//服务器响应失败处理函数
            {
            //    alert(e);
                if (data.code == 0) {
                    if (e)
                        e(id, data.path[0]);
                }
            }
        }
    )
}
