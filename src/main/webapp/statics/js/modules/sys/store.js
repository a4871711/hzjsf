$(function () {
    loadAllMap();
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/store/list',
        datatype: "json",
        colModel: [
            {label: '编号', name: 'storeId', index: 'storeId', width: 30, key: true},
            {
                label: '门店详情图', name: 'storeImgUrl', index: 'storeImgUrl', width: 50,
                formatter: function (value) {
                    var urlArray = value.split(",");
                    return "<img src='" + urlArray[0] + "' weight='30' height='30pk' />"
                }
            },
            {label: '门店名称', name: 'storeName', index: 'storeName', width: 60},
            {label: '门店地址ID', name: 'storeAddrId', index: 'storeAddrId', width: 60, hidden: true},
            {label: '门店地址', name: 'storeAddr', index: 'storeAddr', width: 60},
            {label: '门店电话', name: 'storePhone', index: 'storePhone', width: 50},
            {label: '场内实时人数', name: 'currentNum', index: 'currentNum', width: 50},
            {label: '门店大小(平方米)', name: 'storeArea', index: 'storeArea', width: 50},
            {label: '美团门店ID', name: 'goodsIdStoreId', index: 'goodsIdStoreId', width: 80},
            {label: '抖音门店ID', name: 'douyinPoiId', index: 'douyinPoiId', width: 120},
            {
                label: '营业状况', name: 'status', index: 'status', width: 50,
                formatter: function (value) {
                    if (value == 1) {
                        return '<span class="label label-success">正常</span>';
                    } else {
                        return '<span class="label label-warning">停用</span>';
                    }
                }
            },
            {label: '营业状况', name: 'status', index: 'status', width: 50, hidden: true},
            {
                label: '创建时间', name: 'createdDate', index: 'createdDate', width: 80,
                formatter: function (value) {
                    return value == "" || value == null ? "" : formatDate(new Date(value));
                }
            },
            {
                label: '相关操作', name: 'operate', index: 'operate', width: 70, align: "center",
                formatter: function (value, option, obj) {
                    return '<a href="javascript:void(0)" onclick="vm.updateStoreAdd(' + obj.storeAddrId + ')">修改地址</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:void(0)" onclick="vm.updateStoreGroup(' + obj.storeAddrId + ')">修改社群</a>'
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
        },
        loadComplete: function () {
            $.get(baseURL + "sys/user/info", function (r) {
                if (!isBlank(r.user.storeId)) {
                    $("#jqGrid").setGridParam().hideCol('operate');
                    $("#jqGrid").resize();
                }
            });
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
                        vm.storeImgUrl += url + ",";
                        vm.store.storeImgUrl = vm.storeImgUrl.substring(0, vm.storeImgUrl.length - 1);
                    })
                } else {
                    alert(r.msg);
                }
            }
        });
    });

    $.each($(".uploadGroup"), function () {
        var _this = $(this);
        new AjaxUpload($(this), {
            action: baseURL + "sys/upload?width=160&height=160",
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
                        vm.groupImg = url;
                        vm.store.groupImg = url;
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
        store: {},
        q: {
            storeName: null
        },
        ad1ImgList: [],
        addressData: null,
        groupImg: "",
        ad1ImgsList: []
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function () {
            vm.ad1ImgsList = [];
            vm.showList = false;
            vm.title = "新增";
            vm.store = {};
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
            $(".logo_group img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo_group img").attr("layer-src", "/statics/img/addPhoto.svg");
        },
        update: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            vm.showList = false;
            vm.title = "修改";
            /*vm.store.groupImg = vm.groupImg;*/
            vm.getInfo(id);
        },
        saveOrUpdate: function (event) {

            var img = "";
            $.each($("#image img"), function (index, value) {
                var url = $(this).attr("src");
                if (url.indexOf("addPhoto.svg") == -1) {
                    img += url + ",";
                }
            });
            vm.store.groupImg = vm.groupImg;
            vm.store.longitude = $("#jing_du").val();
            vm.store.latitude = $("#wei_du").val();
            /*vm.store.storeImgUrl = img.substring(0, img.length - 1);*/
            vm.store.storeImgUrl="";
            if (vm.ad1ImgsList != null && vm.ad1ImgsList != '') {
                for(var i=0;i<vm.ad1ImgsList.length;i++){
                    if (!isBlank(vm.ad1ImgsList[i])){
                        vm.store.storeImgUrl +=vm.ad1ImgsList[i]+",";
                    }
                }
                vm.store.storeImgUrl=vm.store.storeImgUrl.substr(0,vm.store.storeImgUrl.length - 1);
            }
            if (vm.validator()) {
                return;
            }
            var url = vm.store.storeId == null ? "sys/store/save" : "sys/store/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.store),
                success: function (result) {
                    if (result.code === 0) {
                        alert('操作成功,请确保门店社群和门店地址信息完整', function (index) {
                            vm.reload();
                        });
                    } else {
                        alert(result.msg);
                    }
                }
            });
        },
        start: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            var rowData = $("#jqGrid").jqGrid('getRowData', id);
            if (rowData.status != 0) {
                layer.msg("请选择状态为'停用'的数据操作");
                return;
            }
            $.ajax({
                type: "POST",
                url: baseURL + "sys/store/start",
                contentType: "application/json",
                data: JSON.stringify(id),
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
        pause: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }
            var rowData = $("#jqGrid").jqGrid('getRowData', id);
            if (rowData.status != 1) {
                layer.msg("请选择状态为'正常'的数据操作");
                return;
            }
            $.ajax({
                type: "POST",
                url: baseURL + "sys/store/pause",
                contentType: "application/json",
                data: JSON.stringify(id),
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
        del: function (event) {
            var id = getSelectedRow();
            if (id == null) {
                return;
            }

            confirm('确定要删除选中的记录？删除后与该门店相关的数据将被清空，请谨慎删除', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/store/delete",
                    contentType: "application/json",
                    data: JSON.stringify(id),
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
            $.get(baseURL + "sys/store/info/" + id, function (result) {
                vm.store = result.store;
                vm.ad1ImgsList = result.store.storeImgUrl == null || result.store.storeImgUrl == "" ? [] : result.store.storeImgUrl.split(",");

            });
        },
        selectAddress: function () {
            loadAddressData();          //渲染 省市区 数据
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-rim',
                title: "选择省份",
                area: ['300px', '450px'],
                shadeClose: true,
                content: $("#addressLayer")
            });
        },
        liClick: function (e) {
            vm.store.address = "";
            var index = $(e.target).attr("data-index");
            var selectData = vm.addressData[index];
            var name = selectData.name;
            /*vm.storeAddress.address = vm.storeAddress.address + name;*/
            var childs = selectData.type;
            if (childs == 1) {
                vm.addressData = vm.addressData[index].city;
                vm.store.province = name;
            } else if (childs == 2) {
                vm.addressData = vm.addressData[index].area;
                vm.store.city = name;
            } else {
                vm.store.zone = name;
                if (vm.store.storeId == null) {
                    document.getElementById("area").value = name;
                } else {
                    vm.store.zone = name;
                }
                layer.closeAll();
            }

        },
        updateStoreAdd: function (event) {
            var id = event;

            sessionStorage["storeAddrId"] = id;
            location.href = "storeaddress.html?backurl=" + window.location.href;
        },
        updateStoreGroup: function (event) {
            var id = event;

            sessionStorage["storeAddrId"] = id;
            location.href = "storegroup.html?backurl=" + window.location.href;
        },
        reload: function (event) {
            $(".logo img").attr("src", "/statics/img/addPhoto.svg");
            $(".logo img").attr("layer-src", "/statics/img/addPhoto.svg");
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                page: page,
                postData: {'storeName': vm.q.storeName}
            }).trigger("reloadGrid");
        },
        /*预览大图*/
        preview: function () {
            $(document).on("click", ".layui-layer-shade", function () {
                $(".layui-layer-shade").remove();
                $(".layui-layer").remove();
            });
            layer.photos({
                photos: "#image" //格式见API文档手册页
                , anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机
            });
            layer.photos({
                photos: "#imageGroup" //格式见API文档手册页
                , anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机
            });
        },
        uploadT: function () {
            if (vm.ad1ImgsList.length < 5) {
                vm.ad1ImgsList.push("");
            } else {
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
            $(e.target).attr("id", "imageUpload_" + index);
            ajaxFileUpload(e.target.id, function (e, url) {

                vm.ad1ImgsList.splice(index, 1, url);
                $("#" + e).change(vm.uploadImage1);
                $("#" + e).attr("id", "");

                var ind = e.charAt(e.length - 1);
       /*      if (url == null) {
                    vm.ad1ImgsList.splice(parseInt(ind), 1);
                    vm.uploadT();
                }
                console.log(vm.ad1ImgsList)
                console.log(e)*/

            });
        },
        validator: function () {
            if (isBlank(vm.store.storeName)) {
                alert("门店名称不能为空");
                return true;
            }
            if (isBlank(vm.store.storePhone)) {
                alert("门店电话不能为空");
                return true;
            }
            var reg = new RegExp("[\\u4E00-\\u9FFF]+", "g");
            if (reg.test(vm.store.storePhone)) {
                alert("门店电话不能包含汉字！");
                return true;
            }
            if (vm.store.storePhone.length > 20) {
                alert("门店电话过长");
                return true;
            }
            if (isBlank(vm.store.storeArea)) {
                alert("门店大小不能为空");
                return true;
            }
            if (vm.store.storeId == null) {
                if (isBlank(vm.store.province)) {
                    alert("省不能为空");
                    return true;
                }
                if (isBlank(vm.store.city)) {
                    alert("市不能为空");
                    return true;
                }
                if (isBlank(vm.store.zone)) {
                    alert("区不能为空");
                    return true;
                }
                if (isBlank(vm.store.storeAddrDetail)) {
                    alert("街道详细地址不能为空");
                    return true;
                }
                vm.store.longitude = $("#jing_du").val();
                vm.store.latitude = $("#wei_du").val();
                if (isBlank(vm.store.longitude)) {
                    alert("请输入经度或者纬度");
                    return true;
                }
                if (isBlank(vm.store.latitude)) {
                    alert("请输入经度或者纬度");
                    return true;
                }
                if (vm.store.groupImg == null) {
                    alert("请选择社群详情图");
                    return true;
                }
                if (isBlank(vm.store.groupName)) {
                    alert("社群名称不能为空");
                    return true;
                }

            }

        }
    }
});

function loadAddressData() {
    $.getJSON(baseURL + "/statics/plugins/address.json", function (data) {
        console.info(data);
        vm.addressData = data;
        vm.store.address = "";
    });
}

function ajaxFileUpload(id, e) {
    $.ajaxFileUpload(
        {
            url: baseURL + "sys/uploads?width=750&height=413", //用于文件上传的服务器端请求地址
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
                alert(data.msg);
            }
        }
    )
}

function loadAllMap() {
    function G(id) {
        return document.getElementById(id);
    }

    /*function displayHideUI()
    {
        var ui = document.getElementById("allmap");
        ui.style.display="block";
    }*/
    /*vm.store.longitude = 113.728235;
    vm.store.latitude = 22.986059;*/
// 百度地图API功能
    
    var map = new AMap.Map("allmap");
    if (isBlank(vm.store.longitude) || isBlank(vm.store.latitude)) {
		vm.store.longitude = 113.728235;
		vm.store.latitude = 22.986059;
		
		/*map.plugin('AMap.Geolocation', function () {
            var geolocation = new AMap.Geolocation();
            geolocation.getCurrentPosition(function (r) {
                console.log(r)
                if (this.getStatus() == BMAP_STATUS_SUCCESS) {
                    var mk = new BMap.Marker(r.point);
                    map.add(mk);
                    map.setCenter(r.point);
                    vm.store.longitude = r.point.lng;
                    vm.store.latitude = r.point.lat;
                    console.info(r.point.lng);
                    console.info(r.point.lat);
                } else {
                    vm.store.longitude = 113.728235;
                    vm.store.latitude = 22.986059;
                }
            });
		});*/
    }
    var point = new AMap.LngLat(vm.store.longitude, vm.store.latitude);
    map.setZoomAndCenter(15, point);
    var marker = new AMap.Marker(point); // 创建标注
    map.add(marker); // 将标注添加到地图中
    //marker.setAnimation(AMAP_ANIMATION_BOUNCE); //跳动的动画

//单击获取点击的经纬度
    map.on("click", function (e) {
        console.log(e)
        var jing_du_value = e.lnglat.lng;
        var wei_du_value = e.lnglat.lat;
//alert(e.lnglat.lng + "," + e.lnglat.lat);
        var jing_du = document.getElementById("jing_du");
        var wei_du = document.getElementById("wei_du");
        jing_du.value = jing_du_value;
        wei_du.value = wei_du_value;

        vm.store.longitude = e.lnglat.lng;
        vm.store.latitude = e.lnglat.lat;
        console.info(e.lnglat.lng);
        console.info(e.lnglat.lat);
        loadAllMap();
    });
    
    AMap.plugin('AMap.Autocomplete',function(){//回调函数
        var autoOptions = {
            city: "", //城市，默认全国
            input:"suggestId"//使用联想输入的input的id
        };
        var autocomplete= new AMap.Autocomplete(autoOptions);
    
        AMap.event.addListener(autocomplete, "select", function(e){
            //TODO 选择后的处理程序，data的格式见 录
            console.log(e)
            var jing_du_value = e.poi.location.lng;
            var wei_du_value = e.poi.location.lat;
    //alert(e.poi.location.lng + "," + e.poi.location.lat);
            var jing_du = document.getElementById("jing_du");
            var wei_du = document.getElementById("wei_du");
            jing_du.value = jing_du_value;
            wei_du.value = wei_du_value;
    
            vm.store.longitude = e.poi.location.lng;
            vm.store.latitude = e.poi.location.lat;
            console.info(e.poi.location.lng);
            console.info(e.poi.location.lat);
            loadAllMap();
        }); 
    });

    /*
//提升框
    var opts = {
        width: 200, // 信息窗口宽度
        height: 100, // 信息窗口高度
        title: "经纬度", // 信息窗口标题
        enableMessage: true,//设置允许信息窗发送短息
        message: "经纬度"
    }
    var infoWindow = new AMap.InfoWindow("地址：东莞", opts); // 创建信息窗口对象
    marker.on("click", function () {
        map.openInfoWindow(infoWindow, point); //开启信息窗口
    });
// 百度地图API功能
    map.setZoomAndCenter(8, point);
    setTimeout(function () {
        map.setZoom(14);
    }, 0); //2秒后放大到14级
    //map.enableScrollWheelZoom(true);

    var ac = new AMap.Autocomplete(    //建立一个自动完成的对象
        {
            "input": "suggestId"
            , "location": map
        });

    ac.on("onhighlight", function (e) {  //鼠标放在下拉列表上的事件
        var str = "";
        var _value = e.fromitem.value;
        var value = "";
        if (e.fromitem.index > -1) {
            value = _value.province + _value.city + _value.district + _value.street + _value.business;
        }
        str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;

        value = "";
        if (e.toitem.index > -1) {
            _value = e.toitem.value;
            value = _value.province + _value.city + _value.district + _value.street + _value.business;
        }
        str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
        G("searchResultPanel").innerHTML = str;
    });

    var myValue;
    ac.on("onconfirm", function (e) {    //鼠标点击下拉列表后的事件
        var _value = e.item.value;
        myValue = _value.province + _value.city + _value.district + _value.street + _value.business;
        G("searchResultPanel").innerHTML = "onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;

        setPlace();
    });

    function setPlace() {
        map.clearOverlays();    //清除地图上所有覆盖物
        function myFun() {
            var pp = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
            map.centerAndZoom(pp, 18);
            map.addOverlay(new AMap.Marker(pp));    //添加标注
        }

        var local = new AMap.LocalSearch(map, { //智能搜索
            onSearchComplete: myFun
        });
        local.search(myValue);
    }*/
}