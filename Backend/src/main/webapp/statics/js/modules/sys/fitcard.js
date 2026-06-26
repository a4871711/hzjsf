$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/fitcard/list',
        datatype: "json",
        colModel: [
            {label: '编号', name: 'fitCardId', index: 'fitCardId', width: 50, key: true},
            {label: '卡片', name: 'cardType', index: 'cardType', width: 80, hidden:true},
            {label: '卡片类型', name: 'ctName', index: 'ctName', width: 80 },
            {label: '单价', name: 'cardPrice', index: 'cardPrice', width: 80,
                formatter: function (value,option,rowObject) {
                    return rowObject.cardPrice == null? "--" : rowObject.cardPrice+"元"
                    + (rowObject.nextPrice>0?(" (次月起"+ rowObject.nextPrice+"元)"):"")

                }
            },
            {label: '新人价', name: 'newUserPrice', index: 'newUserPrice', width: 60,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            {label: '原价', name: 'costPrice', index: 'costPrice', width: 40,
                formatter: function (value) {
                    return value == null? "--" : value+"元"
                }
            },
            {
                label: '有效期/天', name: 'validity', index: 'validity', width: 80,
                formatter: function (value) {
                    return value == null? "--" : value+"天"
                }
            },
            {
                label: '是否自动续费', name: 'autoPay', index: 'autoPay', width: 80,
                formatter: function (value) {
                    return value > 0? value + "级" : "否"
                }
            },
            {
                label: '卡图', name: 'cardImgUrl', index: 'cardImgUrl', width: 80,
                formatter: function (value) {
                    return "<img src='" + value + "' weight='80pk' height='80pk' />"
                }
            },
            {
                label: '状态', name: 'status', index: 'status', width: 40,
                formatter: function (value) {
                    return value === 1 ?
                        '<span class="label label-success">上架</span>' :
                        '<span class="label label-default">下架</span>';
                }
            },
            {label: '会员规则', name: 'cardRule', index: 'cardRule', width: 80},
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
            $("#jqGrid").setGridWidth($("#jqGrid").width());
        },
        loadComplete: function (r) {
            vm.cardTypeList = r.cardType;
        }
    });
    $.each($(".upload"), function () {
        var _this = $(this);
        new AjaxUpload($(this), {
            action: baseURL + "sys/upload?width=446&height=266",
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
                        vm.fitCard.cardImgUrl = url;
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });

});
var blay;
var vm = new Vue({
    el: '#rrapp',
    data: {
        showList: true,
        title: null,
        imgUrl:"",
        fitCard: {},
        cardType: {},
        cardTypeList: []
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.fitCard = {cardType:0,validity:30, autoPay:2, status:1};
            //var validity = getValidity(vm.fitCard.cardType);
            $("#validity").val();
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
        },
        isAutoPay:function () {
            if(vm.fitCard.autoPay > 0){
                $("#nextPrice").attr("disabled", false);
            }else {
                $("#nextPrice").attr("disabled", true);
            }
        },
        initValidity: function(event){
            var cardType = vm.fitCard.cardType;
            if(cardType == 0){
                vm.fitCard.validity = 30;
            }else if(cardType == 1){
                vm.fitCard.validity = 90;
            }else if(cardType == 2){
                vm.fitCard.validity = 180;
            }else if(cardType == 3){
                vm.fitCard.validity = 365;
            }else{
                vm.fitCard.validity = 0;
            }
        },
        update: function (event) {
            var fitCardId = getSelectedRow();
            if (fitCardId == null) {
                return;
            }

            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(fitCardId)
        },
        saveOrUpdate: function (event) {
            if (vm.validator()) {
                return;
            }
            var url = vm.fitCard.fitCardId == null ? "sys/fitcard/save" : "sys/fitcard/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.fitCard),
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
            var fitCardIds = getSelectedRows();
            if (fitCardIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/fitcard/delete",
                    contentType: "application/json",
                    data: JSON.stringify(fitCardIds),
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
        //上架
        onCard:function () {
            var fitCardIds = getSelectedRows();
            if (fitCardIds == null) {
                return;
            }
            confirm('确定要上架选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/fitcard/onCard",
                    contentType: "application/json",
                    data: JSON.stringify(fitCardIds),
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
        //下架
        offCard:function () {
            var fitCardIds = getSelectedRows();
            if (fitCardIds == null) {
                return;
            }
            confirm('确定要下架选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/fitcard/offCard",
                    contentType: "application/json",
                    data: JSON.stringify(fitCardIds),
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
        getInfo: function (fitCardId) {
            $.get(baseURL + "sys/fitcard/info/" + fitCardId, function (r) {
                vm.fitCard = r.fitCard
                $(".logo img").attr("src", r.fitCard.cardImgUrl);
                $(".logo img").attr("layer-src", vm.fitCard.cardImgUrl);
                if(vm.fitCard.autoPay > 0){
                    $("#nextPrice").attr("disabled", false);
                }else {
                    $("#nextPrice").attr("disabled", true);
                }
            });
        },
        reload: function (event) {
            vm.showList = true;
            var pages = $("#jqGrid").jqGrid('getGridParam', 'pages');
            $("#jqGrid").jqGrid('setGridParam', {
                pages: pages,
                postData: {'fitCard': vm.fitCard, "validity": $("#validity").val()}
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
                photos: "#images" //格式见API文档手册页
                , anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机
            });
        },
        validator: function () {

            var phoneReg = /(^1[3|4|5|7|8]\d{9}$)|(^09\d{8}$)/;
            var passwordReg = /^[0-9a-zA-Z]+$/;
          //  var price=/(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/;
            var retype= /^(-?\d+)(\.\d{1,2})?$/;
            if (vm.fitCard.cardPrice==null||vm.fitCard.cardPrice=="") {
                alert("请输入价格");
                return true;
            }
            if(isBlank(vm.fitCard.cardPrice) || !retype.test(vm.fitCard.cardPrice) || vm.fitCard.cardPrice < 0){
                alert("请输入正确金额数值,若输入金额包含小数位，最多不超过2位小数");
                return true;
            }
            if (isBlank(vm.fitCard.validity)){
                alert("请输入有效期");
                return true;
            }
            if (isNumber(vm.fitCard.validity)){
                alert("有效期请输入整数");
                return true;
            }
            if ((vm.fitCard.validity+"").indexOf(".")!=-1){
                alert("有效期请输入整数");
                return true;
            }
            if (vm.fitCard.cardImgUrl==null){
                alert("请上传图片");
                return true;
            }
            if (vm.fitCard.cardRule==null||vm.fitCard.cardRule==""){
                alert("请输入会员规则");
                return true;
            }
            if (vm.fitCard.cardRule.length>255){
                alert("会员规则过长");
                return true;
            }

        },
        validatorCt: function () {
            if (isBlank(vm.cardType.ctName)){
                alert("请输入卡类型");
                return true;
            }
        },

        //修改，新增操作
        editCardType:function(value){
            if(!isBlank(value)){   //修改
                vm.cardType = $("#jqGridAdd").jqGrid('getRowData',value);
                vm.cardType.ctId = value;   //从新给主键赋值
            }
            blay = layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-molv',
                title: "自定义",
                area: ['300px', '150px'],
                shade: 0,
                shadeClose: false,
                resize:false,
                content: $("#anewFileWindow")
            });
        },
        //删除卡类型
        deleteCardType:function(value){
            var ctId = value;
            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/fitcard/deleteCt",
                    contentType: "application/json",
                    data: JSON.stringify(ctId),
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
        //编辑卡片类型
        saveOrUpdateType: function(){
            if (vm.validatorCt()) {
                return;
            }
            var url = vm.cardType.ctId == null ? "sys/fitcard/saveCardType" : "sys/fitcard/updateCardType";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.cardType),
                success: function (r) {
                    if (r.code === 0) {
                        alert('操作成功', function (index) {
                            vm.reload2();
                            layer.close(blay);
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        //自定义卡类型
        ctDefined: function () {
            var a = layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-molv',
                title: "自定义卡片类型",
                area: ['700px', '500px'],
                shadeClose: false,
                content: $("#ctDefined"),
                btn: ["新增"],
                btn1: function () {
                    vm.cardType = {};
                    vm.editCardType(null);
                },
                cancel:function () {
                    vm.getCardTypeInfo();
                }
            });

        },

        reload2: function (event) {
            var page = $("#jqGridAdd").jqGrid('getGridParam', 'cardType');
            $("#jqGridAdd").jqGrid('setGridParam', {
                page: page
            }).trigger("reloadGrid");

        },
        getCardTypeInfo: function () {
            $.get(baseURL + "sys/fitcard/getCardTypeList", function (r) {
                vm.cardTypeList = r.cardType;
            });
        },
        initJQqrid: function () {
            $("#jqGridAdd").jqGrid({
                url: baseURL + 'sys/fitcard/getCardTypeList',
                datatype: "json",
                colModel: [
                    {label: '编号', name: 'ctId', index: 'ctId', width: 10, key: true, hidden: true},
                    {label: '类型编号', name: 'cardType', index: 'cardType', width: 30},
                    {label: '卡类型名称', name: 'ctName', index: 'ctName', width: 60},
                ],
                cellEdit: true,
                cellsubmit: "clientArray",
                afterSaveCell: function () {

                },
                viewrecords: true,
                height: 385,
                rowNum: 1000,
                rownumbers: true,
                rownumWidth: 25,
                autowidth: true,
                multiselect: true,
                jsonReader: {
                    root: "cardType"
                },
                gridComplete: function () {
                    //隐藏grid底部滚动条
                    $("#jqGridAdd").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
                    //$("#jqGridAdd").setGridWidth($("#ctDefined").width());
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
$("#ctDefined").hide();