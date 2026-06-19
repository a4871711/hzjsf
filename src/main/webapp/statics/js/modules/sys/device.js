$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/device/list',
        datatype: "json",
        colModel: [
            {label: '编号', name: 'deviceId', index: 'deviceId', width: 50, key: true},
            {label: '设备编号', name: 'deviceNo', index: 'deviceNo', width: 80},
            {label: '设备名', name: 'deviceName', index: 'deviceName', width: 80},
            /*{
                label: '设备图', name: 'deviceImgUrl', index: 'deviceImgUrl', width: 80,
                formatter: function (value) {
                    return "<img src='" + value + "' weight='80pk' height='80pk'/>"
                }
            },*/
            {label: '用户编号', name: 'proxyId', index: 'proxyId', width: 80},
            // {label: 'storeId', name: 'storeId', index: 'storeId', width: 80},
            {label: '用户姓名', name: 'proxyName', index: 'proxyName', width: 80},
            {label: '联系电话', name: 'phone', index: 'phone', width: 80},
            //{label: '设备价格', name: 'devicePrice', index: 'devicePrice', width: 80},
            //{label: '设备地址详情', name: 'addressDetail', index: 'addressDetail', width: 80},
            {label: '设备状态', name: 'status', index: 'status', width: 80,
            formatter:function (value) {
              switch (value){
                  case 0: return '<span class="label label-info">待确认</span>';
                  case 1: return '<span class="label label-success">已确认</span>';
                  case 2: return '<span class="label label-danger">已禁用</span>';
                  case 3: return '<span class="label label-warning">已转卡</span>';
                  case 4: return '<span class="label label-default">已过期</span>';
                  case 5: return '<span class="label label-primary">已注销</span>';
              }
            }
            },
            {label: '设备状态', name: 'status', index: 'status', width: 0, hidden:true},
            {label: '设备数量（清单）', name: 'inventory', index: 'inventory', width: 80},
            {
                label: '创建时间', name: 'createTime', index: 'createTime', width: 80,
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
            $("#jqGrid").setGridWidth($("#jqGrid").width());
        }
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
                        vm.device.deviceImgUrl = url;
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
        q: {
            deviceNo: null,
            phone: null,
            status:''
        },
        device: {}
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.showList = false;
            vm.title = "新增";
            vm.device = {};
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
            vm.device.status = 0;
        },
        update: function (event) {
            var deviceId = getSelectedRow();
            if (deviceId == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";

            vm.getInfo(deviceId)
        },

        forbidden: function (event) {
            var deviceIds = getSelectedRows();
            if (deviceIds == null) {
                return;
            }
            for(var i=0; i<deviceIds.length; i++){
                var rowData = $("#jqGrid").jqGrid('getRowData',deviceIds[i]);
                if(rowData.status != 1){
                    layer.msg("请选择已确认的数据禁用");
                    return;
                }
            }
            confirm("禁用后，该会员将无法进行正常刷脸认证、刷手环等相关操作", function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/device/forbidden",
                    contentType: "application/json",
                    data: JSON.stringify(deviceIds),
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
            });

        },
        unForbidden: function (event) {
            var deviceIds = getSelectedRows();
            if (deviceIds == null) {
                return;
            }
            for(var i=0; i<deviceIds.length; i++){
                var rowData = $("#jqGrid").jqGrid('getRowData',deviceIds[i]);
                if(rowData.status != 2){
                    layer.msg("请选择已禁用的数据解除");
                    return;
                }
            }
            $.ajax({
                type: "POST",
                url: baseURL + "sys/device/unForbidden",
                contentType: "application/json",
                data: JSON.stringify(deviceIds),
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

        saveOrUpdate: function (event) {
            if (vm.validator()) {
                return;
            }
            if(vm.device.status == 3){
                layer.msg("'已转卡' 会员不可绑定设备");
                return;
            }
            if(vm.device.status == 4){
                layer.msg("'已过期' 会员不可绑定设备");
                return;
            }
            //if(vm.device.status == 1){alert("请选择待确认的记录操作");return;}
            var url = vm.device.deviceId == null ? "sys/device/save" : "sys/device/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.device),
                success: function (r) {
                    if (r.code === 0) {
                        alert(r.msg, function (index) {
                            vm.reload();
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        del: function (event) {
            var deviceIds = getSelectedRows();
            if (deviceIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/device/delete",
                    contentType: "application/json",
                    data: JSON.stringify(deviceIds),
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
        getInfo: function (deviceId) {
            $.get(baseURL + "sys/device/info/" + deviceId, function (r) {
                vm.device = r.device;
                $(".logo img").attr("src", r.device.deviceImgUrl);
                $(".logo img").attr("layer-src", r.device.deviceImgUrl);

            });
        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page,
                postData: {'deviceNo':vm.q.deviceNo,'phone':vm.q.phone,"status":vm.q.status}
            }).trigger("reloadGrid");
        }, /*预览大图*/
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
            var phoneReg = /^[1][2,3,4,5,6,7,8,9][0-9]{9}$/;
            var passwordReg = /^[0-9a-zA-Z]+$/;
            if (vm.device.deviceNo == "" || vm.device.deviceNo == null) {
                alert("请输入设备编号");
                return true;
            }
            if (vm.device.deviceNo.length > 32) {
                alert("设备编号超长");
                return true;
            }
            if (vm.device.deviceName == "" || vm.device.deviceName == null) {
                alert("请输入设备名");
                return true;
            }
            if (vm.device.deviceName.length > 32) {
                alert("设备名超长");
                return true;
            }
            /*if (isNumber(vm.device.proxyId)) {
                alert("代理人ID必须为数字");
                return true;
            }
            if (vm.device.proxyId.length > 20) {
                alert("代理人ID超长");
                return true;
            }
            if (vm.device.deviceImgUrl == null) {
                alert("请上传设备图片");
                return true;
            }*/
           /* if (isNumber(vm.device.storeId)) {
                alert("storeId必须为数字");
                return true;
            }
            if (vm.device.storeId.length > 20) {
                alert("storeId超长");
                return true;
            }*/
            if (vm.device.proxyName == "" || vm.device.proxyName == null) {
                alert("请输入代理人姓名");
                return true;
            }
            if (vm.device.proxyName.length > 32) {
                alert("代理人姓名超长");
                return true;
            }
            if (!phoneReg.test(vm.device.phone)) {
                alert("请输入正确的手机号");
                return true;
            }
            /* if (vm.device.addressDetail == "" || vm.device.addressDetail == null) {
                alert("请输入设备地址详情");
                return true;
            }
           if (vm.device.addressDetail.length > 100) {
                alert("设备地址详情超长");
                return true;
            }*/
        }
    }
});