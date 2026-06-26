$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/user/userCouponList',
        datatype: "json",
        colModel: [			
			{ label: '用户编号', name: 'userId', index: 'userId', width: 50, key: true },
            { label: '昵称', name: 'nickname', index: 'nickname', width: 80 },
			{ label: '手机号', name: 'phone', index: 'phone', width: 80},
			{ label: '可用优惠券数量', name: 'couponNum', index: 'couponNum', width: 50},
            { label: '用户注册时间', name: 'createdDate', index: "createdDate", width: 50,formatter:function (value) {
                return value == "" || value == null ? "" : formatDate(new Date(value))
            }}
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
        showDetail: true,
		title: null,
        couponDetail:[],
		list: {},
        coupon:{
            couponMoney:0,
            validDay:0,
            limitPrice: 0,
            couponTitle: '',
            storeAddrIds: ''
        },
        status:null,
        q:{
            phone:null
        }
},
	methods: {
		query: function () {
			vm.reload();
		},
        lookDetail:function () {
            var id = getSelectedRow();
            if(id == null){
                return ;
            }
            vm.showList = false;
            vm.showDetail = false;
            vm.title = "个人优惠券详情";
            vm.getInfo(id);

        },
		getInfo: function(value){
            $.ajaxSettings.async = false;
			$.get(baseURL + "sys/user/couponInfo/"+value, function(result){
                vm.couponDetail = result.couponDetail;
                console.log(result.couponDetail);
                console.log(vm.couponDetail);
               // vm.walletDetail.status = 2;
            });
		},
        closeAllLayer:function () {
            layer.closeAll();
        },
        sendCoupon: function(){

            layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-molv',
                title: "添加",
                area: ['700px', '320px'],
                shade: 0,
                shadeClose: false,
                content: $("#anewFileWindow"),
            });
		},
        saveCoupon: function () {
            var ids = getSelectedRows();
            if(ids == null){
                return ;
            }
            var couponMoney = vm.coupon.couponMoney;
            var valday = vm.coupon.validDay;
            var retype = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/;
            var vlitype = /^[0-9]*[1-9][0-9]*$/;
            if (couponMoney<0 || !retype.test(couponMoney)) {
                alert("请输入正确金额数值,若输入金额包含小数位，最多不超过2位小数");
                return true;
            }
            if(!vlitype.test(valday)){
                alert("请输入整数天数");
                return true;
            }
            confirm('确定发放优惠券吗？确认后将向发放会员推送消息通知', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/user/sendCoupon?userIds="
                    	+ids+"&couponMoney="+couponMoney+"&validDay="+valday
                    	+"&limitPrice="+vm.coupon.limitPrice+"&couponTitle="+vm.coupon.couponTitle+"&storeAddrIds="+vm.coupon.storeAddrIds,
                    contentType:"application/json",
                    dataType: "json",
                    success: function(result){
                        if(result.code == 0){
                            alert('操作成功', function(index){
                                $("#jqGrid").trigger("reloadGrid");
                                vm.closeAllLayer();
                            });
                        }else{
                            alert(result.msg);
                        }
                        vm.reload();
                    }
                });

            });
        },

		reload: function (event) {
			vm.showList = true;
            vm.showDetail = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page,
                postData:{'phone':vm.q.phone,"startTime": $("#sdate").val(),
                    "endTime": $("#edate").val()}
            }).trigger("reloadGrid");
		}
	}
});