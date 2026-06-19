$(function () {
    loadAllMap();
    showOrHide();
    var storeAddrId = sessionStorage["storeAddrId"];
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/storeAddress/list',
        datatype: "json",
        colModel: [
            { label: '编号', name: 'storeAddrId', index: 'storeAddrId', width: 50, key: true },
            { label: '门店名称', name: 'storeName', index: 'storeName', width: 80 },
            { label: '门店电话', name: 'storePhone', index: 'storePhone', width: 80 },
            { label: '省', name: 'province', index: 'province', width: 80 },
            { label: '市', name: 'city', index: 'city', width: 80 },
            { label: '区', name: 'zone', index: 'zone', width: 80 },
            { label: '美团门店ID', name: 'goodsIdStoreId', index: 'goodsIdStoreId', width: 80 },
            { label: '抖音门店ID', name: 'douyinPoiId', index: 'douyinPoiId', width: 100 },
            { label: '街道详细地址', name: 'storeAddrDetail', index: 'storeAddrDetail', width: 80 },
            { label: '经度', name: 'longitude', index: 'longitude', width: 80 },
            { label: '纬度', name: 'latitude', index: 'latitude', width: 80 },
            { label: '修改时间', name: 'createdDate', index: 'createdDate', width: 80,
                formatter: function (value) {
                    return value == "" || value == null ? "" : formatDate(new Date(value));
                }
            }
        ],
        viewrecords: true,
        height: 'auto',
        rowNum: 10,
        rowList : [10,30,50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        postData:{"storeAddrId":storeAddrId},
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page",
            rows:"limit",
            order: "order"
        },
        gridComplete:function(){
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });
        }
    });

});

var vm = new Vue({
    el:'#rrapp',
    data:{
        showList: true,
        title: null,
        storeAddress: {},
        q:{
            status:null
        },
        addressData: null
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function(){
            vm.showList = false;
            vm.title = "新增";
            vm.storeAddress = {};
        },
        update: function (event) {
            var id = getSelectedRow();
            if(id == null){
                return ;
            }
            vm.showList = false;
            vm.title = "修改";
            vm.getInfo(id);
        },
        saveOrUpdate: function (event) {
            if(vm.validator()){
                return;
            }
            vm.storeAddress.longitude = $("#jing_du").val();
            vm.storeAddress.latitude = $("#wei_du").val();
            var url = "sys/storeAddress/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.storeAddress),
                success: function(result){
                    if(result.code === 0){
                        alert('操作成功', function(index){
                            vm.reload();
                        });
                    }else{
                        alert(result.msg);
                    }
                }
            });
        },
        del: function (event) {
            var ids = getSelectedRows();
            if(ids == null){
                return ;
            }

            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/storeAddress/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function(result){
                        if(result.code == 0){
                            alert('操作成功', function(index){
                                $("#jqGrid").trigger("reloadGrid");
                            });
                        }else{
                            alert(result.msg);
                        }
                    }
                });
            });
        },
        getInfo: function(id){
            $.get(baseURL + "sys/storeAddress/info/"+id, function(result){
                vm.storeAddress = result.storeAddress;
                loadAllMap();
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
            vm.storeAddress.address = "";
            var index = $(e.target).attr("data-index");
            var selectData = vm.addressData[index];
            var name = selectData.name;
            /*vm.storeAddress.address = vm.storeAddress.address + name;*/
            var childs = selectData.type;
            if(childs == 1){
                vm.addressData = vm.addressData[index].city;
                vm.storeAddress.province = name;
            }else if(childs == 2){
                vm.addressData = vm.addressData[index].area;
                vm.storeAddress.city = name;
            }else{
                vm.storeAddress.zone = name;
                if(vm.storeAddress.storeAddrId == null){
                    document.getElementById("area").value = name;
                }else{
                    vm.storeAddress.zone = name;
                }
                layer.closeAll();
            }

        },
        reload: function (event) {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');

            $("#jqGrid").jqGrid('setGridParam',{
                page:page
            }).trigger("reloadGrid");
           /* $("#jqGrid").jqGrid('setGridParam',{
                page:page,
                postData: {'status':vm.q.status}
            }).trigger("reloadGrid");*/
        },
        validator: function () {
            if (isBlank(vm.storeAddress.province)) {
                alert("省不能为空");
                return true;
            }
            if (isBlank(vm.storeAddress.city)) {
                alert("市不能为空");
                return true;
            }
            if (isBlank(vm.storeAddress.zone)) {
                alert("区不能为空");
                return true;
            }
            if (isBlank(vm.storeAddress.storeAddrDetail)) {
                alert("街道详细地址不能为空");
                return true;
            }
            if (isBlank(vm.storeAddress.longitude)) {
                alert("请输入经度或者纬度");
                return true;
            }
            if (isBlank(vm.storeAddress.latitude)) {
                alert("请输入经度或者纬度");
                return true;
            }
        }
    }
});

function loadAddressData() {
    $.getJSON(baseURL + "/statics/plugins/address.json", function (data) {
        console.info(data);
        vm.addressData = data;
        vm.storeAddress.address = "";
    });
}
function showOrHide(){
    var obj=document.getElementById('test')
    if(obj.style.display=="none"){
        obj.style.display="";
    }else{
        obj.style.display="none";
    }
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
    /*vm.storeAddress.longitude = 113.728235;
    vm.storeAddress.latitude = 22.986059;*/
// 百度地图API功能
    
    var map = new AMap.Map("allmap");
    if (isBlank(vm.storeAddress.longitude) || isBlank(vm.storeAddress.latitude)) {
		vm.storeAddress.longitude = 113.728235;
		vm.storeAddress.latitude = 22.986059;
		
		/*map.plugin('AMap.Geolocation', function () {
            var geolocation = new AMap.Geolocation();
            geolocation.getCurrentPosition(function (r) {
                console.log(r)
                if (this.getStatus() == BMAP_STATUS_SUCCESS) {
                    var mk = new BMap.Marker(r.point);
                    map.add(mk);
                    map.setCenter(r.point);
                    vm.storeAddress.longitude = r.point.lng;
                    vm.storeAddress.latitude = r.point.lat;
                    console.info(r.point.lng);
                    console.info(r.point.lat);
                } else {
                    vm.storeAddress.longitude = 113.728235;
                    vm.storeAddress.latitude = 22.986059;
                }
            });
		});*/
    }
    var point = new AMap.LngLat(vm.storeAddress.longitude, vm.storeAddress.latitude);
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

        vm.storeAddress.longitude = e.lnglat.lng;
        vm.storeAddress.latitude = e.lnglat.lat;
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
    
            vm.storeAddress.longitude = e.poi.location.lng;
            vm.storeAddress.latitude = e.poi.location.lat;
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