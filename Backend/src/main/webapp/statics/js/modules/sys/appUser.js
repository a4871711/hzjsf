$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/user/appList',
        datatype: "json",
        colModel: [			
			{ label: '编号', name: 'userId', index: 'userId', width: 20, key: true },
            { label: '姓名', name: 'nickname', index: 'nickname', width: 40 },
			{ label: '手机号码', name: 'phone', index: 'phone', width: 40},
			{
                label: '头像', name: 'storeImgUrl', index: 'headImgUrl', width: 50,
                formatter: function (value) {
                    if(!value)return '';
                    var urlArray = value.split(",");
                    return "<img src='" + urlArray[0] + "' weight='30' height='30pk' />"
                }
            },
            /*{ label: '性别', name: 'sex', index: 'sex', width: 20,
                formatter:function (value) {
                    if(value == 1){
                        return '男';
                    }else if(value == 2){
                        return '女';
                    }else{
                        return '未知';
                    }
                }
            },
            { label: '身高', name: 'height', index: 'height', width: 20,
                formatter: function (value) {
                    return value == null || value==""? "--" : value+"cm"
                }
            },
            { label: '体重', name: 'weight', index: 'weight', width: 20,
                formatter: function (value) {
                    return value == null || value==""? "--" : value+"kg"
                }
            },
            {label: '备注', name: 'remark', index: 'remark', width: 40},
            { label: '手机设备token', name: 'deviceToken', index: 'deviceToken', width: 30,
                formatter: function (value) {
                    return value.length > 43? "正常" : "异常";
                }
            },*/
            {label: 'OpenId', name: 'openId', index: 'openId', width: 0},
            {label: '签约状态', name: 'wtState', index: 'wtState', width: 40,
                formatter:function (value) {
                    switch (value){
                        case 0: return '<span class="label label-info">未签约</span>';
                        case 1: return '<span class="label label-success">已签约</span>';
                        case 2: return '<span class="label label-danger">已解约</span>';
                    }
                }
            },
            {label: '是否会员', name: 'faceStatus', index: 'faceStatus', width: 40,
                formatter:function (value) {
                    switch (value){
                        case 0: return '<span class="label label-info">否</span>';
                        //case 1: return '<span class="label label-success">已签约</span>';
                        case 2: return '<span class="label label-danger">是</span>';
                    }
                }
            },
            //{label: '生日', name: 'birthday2', index: 'birthday2', width: 0, hidden:true},
            //{label: '备注', name: 'remark', index: 'remark', width: 0, hidden:true},
            { label: '注册时间', name: 'createdDate', index: "createdDate", width: 50,formatter:function (value) {
                return value == "" || value == null ? "" : formatDate(new Date(value))
            }},
            {label: '状态', name: 'auditStatus', index: 'auditStatus', width: 40,
                formatter:function (value) {
                    switch (value){
                        case 1: return '<span class="label label-info">正常</span>';
                        case 2: return '<span class="label label-danger">拉黑</span>';
                    }
                }
            },
            {
                label: '相关操作', name: 'operate', index: 'operate', width: 0, align: "center",
                formatter: function (value, option, obj) {
                    var html = []
                    if(obj.auditStatus == 2)html.push('<a href="javascript:void(0)" onclick="vm.updateAuditStatus(' + obj.userId + ', 1)">恢复状态</a>')
                    if(obj.auditStatus == 1)html.push('<a href="javascript:void(0)" onclick="vm.updateAuditStatus(' + obj.userId + ', 2)">拉黑账号</a>')
                    
                    if(obj.phone)html.push('<a href="javascript:void(0)" onclick="vm.updateEmptyPhone(' + obj.userId + ', )">微信解绑</a>')
                    
                    return html.join('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;');
                }
            }
        ],
		viewrecords: true,
        height: 'auto',
        rowNum: 10,
		rowList : [10,30,50,200,500,2000,99999999],
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
		title: null,
		list: {},
        messageTemplate: {},
        memberInfo: {},
        mq: {},
        status:null,
        q:{
            phone:null,
            nickname:null,
            wtState:"",
            sex: "",
            remark: "",
            auditStatus:'',
        }
},
	methods: {
		query: function () {
			vm.reload();
		},
        queryMsg: function () {
            vm.reload3();
        },
		getInfo: function(id){
            $.ajaxSettings.async = false;
			$.get(baseURL + "sys/walletdetail/info/"+id, function(result){
                vm.walletDetail = result.walletDetail;
                vm.status = result.walletDetail.status;

                console.log(result.walletDetail);
                console.log(vm.walletDetail);
               // vm.walletDetail.status = 2;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			var postData = {}
            for(var i in vm.q){
                if(vm.q[i] || vm.q[i] === 0){
                    postData[i] = vm.q[i]
                }
            }
            $("#jqGrid").jqGrid('setGridParam',{
                page:page,
                postData:$.extend(postData, {
                    "startTime": $("#sdate").val(),
                    "endTime": $("#edate").val()})
            }).trigger("reloadGrid");
		},
        reload3: function (event) {
            var page = $("#jqGridAddMsg").jqGrid('getGridParam', 'msgPage');
            $("#jqGridAddMsg").jqGrid('setGridParam', {
                page: page,
                postData: vm.mq
            }).trigger("reloadGrid");

        },
        //消息推送
        pushMessage: function () {
            //推送对象
            var userId = getSelectedRows();
            if(userId == null){return;}
            var a = layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-lan',
                title: "消息模板",
                area: ['750px', '500px'],
                shadeClose: false,
                content: $("#messageTmp")
            });
        },
        //确认推送
        pushMsg: function (event) {
            //推送对象
            var userIds = getSelectedRows();
            var mtId = getSelectedWindowRow2();
            if(userIds == null || mtId == null){return;}
            confirm('确定将模板消息推送给当前页选中的'+userIds.length+'位用户吗？若想要一次性推送给更多的用户，可通过调节页面下方页容量实现', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/user/pushMsgToUserByUid?mtId="+mtId,
                    contentType: "application/json",
                    data: JSON.stringify(userIds),
                    success: function(result){
                        if(result.code === 0){
                            alert('操作成功', function(index){
                                vm.reload();
                                vm.reload3();
                                layer.closeAll();
                            });
                        }else{
                            alert(result.msg);
                        }
                    }
                });
            });
        },
        //编辑模板
        editMsgTemp: function (value) {
            if(!isBlank(value)){   //修改
                vm.messageTemplate = $("#jqGridAddMsg").jqGrid('getRowData',value);
                vm.messageTemplate.mtId = value;   //从新给主键赋值
            }
            mtlay = layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-lan',
                title: "模板编辑",
                area: ['500px', '300px'],
                shade: 0,
                shadeClose: false,
                content: $("#anewFileWindow")
            });
        },
        //新增消息模板
        addMsgTemp: function () {
            vm.messageTemplate = {};   //初始化
            mtlay = layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-lan',
                title: "模板编辑",
                area: ['500px', '300px'],
                shade: 0,
                shadeClose: false,
                content: $("#anewFileWindow")
            });
        },
        validMsgTemp: function () {
            if (isBlank(vm.messageTemplate.title)) {
                layer.msg("请输入标题");
                return true;
            }else{
                if(vm.messageTemplate.title.length > 15){
                    layer.msg("标题不能超出15字");
                    return true;
                }
            }
            if(isBlank(vm.messageTemplate.message)){
                layer.msg("请输入消息内容");
                return true;
            }else{
                if(vm.messageTemplate.message > 100){
                    layer.msg("消息内容不超出100字");
                    return true;
                }
            }
        },
        //编辑
        saveOrUpdateMsgTmpType: function (event) {
            if (vm.validMsgTemp()) {
                return;
            }
            var url = vm.messageTemplate.mtId == null ? "sys/user/saveMsgTemp" : "sys/user/updateMsgTemp";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.messageTemplate),
                success: function (r) {
                    if (r.code === 0) {
                        alert('操作成功', function (index) {
                            vm.reload();
                            vm.reload3();
                            layer.close(mtlay);
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        deleteMsgTemp: function (value) {
            console.log("删除方法："+value)
            if(value == null){
                layer.msg("未知行");
                return;
            }
            var id = value;
            $.ajax({
                type: "POST",
                url: baseURL + "sys/user/deleteMsgTemp",
                contentType: "application/json",
                data: JSON.stringify(id),
                success: function (r) {
                    if (r.code == 0) {
                        alert('操作成功', function (index) {
                            vm.reload3();
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });

        },
        initMsgJQqrid: function () {
            $("#jqGridAddMsg").jqGrid({
                url: baseURL + 'sys/user/getMessageTmpList',
                datatype: "json",
                colModel: [
                    {label: '编号', name: 'mtId', index: 'mtId', width: 10, key: true, hidden: true},
                    {label: '标题', name: 'title', index: 'title', width: 30},
                    {label: '消息', name: 'message', index: 'message', width: 30},
                    {label:'相关操作',name:'mtId',index:'ctId',width:50,
                        formatter:function (value,option,rowObject) {
                            return '<a href="javascript:void(0)" onclick="vm.editMsgTemp(' + value + ')">编辑</a>&nbsp;&nbsp;<a href="javascript:void(0)" onclick="vm.deleteMsgTemp('+ rowObject.mtId +')">删除</a>'
                        }
                    }
                ],

                viewrecords: true,
                height: 'auto',
                rowNum: 8,
                rownumbers: true,
                rownumWidth: 25,
                autowidth: true,
                multiselect: true,
                pager: "#jqGridPagerAddMsg",
                jsonReader: {
                    root: "msgPage.list",
                    page: "msgPage.currPage",
                    total: "msgPage.totalPage",
                    records: "msgPage.totalCount"
                },

                prmNames : {
                    page:"page",
                    rows:"limit",
                    order: "order"
                },
                gridComplete: function () {
                    //隐藏grid底部滚动条
                    $("#jqGridAddMsg").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
                    $("#jqGridAddMsg").setGridWidth($("#messageTmp").width());
                }

            });
            for (var i = 0, len = 10; i < len; i++) {
                $("#jqGridAddMsg").jqGrid('addRowData', i + 1, i + 1);
            }
        },
        
        updateAuditStatus: function(userId, auditStatus){
            vm.memberInfo = {};   
            //赋值更新对象
            vm.memberInfo.userId = userId;
            vm.memberInfo.auditStatus = auditStatus;
            confirm('确定更新会员信息？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/user/updateUserInfo",
                    contentType: "application/json",
                    data: JSON.stringify(vm.memberInfo),
                    success: function(result){
                        if(result.code === 0){
                            alert('操作成功', function(index){
                                vm.reload();
                                layer.closeAll();
                            });
                        }else{
                            alert(result.msg);
                        }
                    }
                });
            })
        },
        
        updateEmptyPhone: function(userId){
            vm.memberInfo = {};   
            //赋值更新对象
            vm.memberInfo.userId = userId;
            vm.memberInfo.phone = 'NULL';
            confirm('确定更新会员信息？', function(){
                $.ajax({
                        type: "POST",
                        url: baseURL + "sys/user/updateUserInfo",
                        contentType: "application/json",
                        data: JSON.stringify(vm.memberInfo),
                        success: function(result){
                            if(result.code === 0){
                                alert('操作成功', function(index){
                                    vm.reload();
                                    layer.closeAll();
                                });
                            }else{
                                alert(result.msg);
                            }
                        }
                    });
            })
        },

        //更新会员信息
        updateMemberInfo: function (event) {
            var id = getSelectedRow();
            console.log('选择了', id);
            if(id == null){return;}
            vm.memberInfo = {};   //清空上次记录
            var rowData = $("#jqGrid").jqGrid('getRowData', id);
            if(rowData == null){
                layer.msg("请重新选择");
                return;
            }
            //赋值更新对象
            vm.memberInfo.userId = rowData.userId;
            vm.memberInfo.nickname = rowData.nickname;
            vm.memberInfo.phone = rowData.phone;
            vm.memberInfo.sex = rowData.sex == '男' ? 1 : 0;
            vm.memberInfo.height = rowData.height.replace('cm', '');
            vm.memberInfo.weight = rowData.weight.replace('kg', '');
            vm.memberInfo.birthday = rowData.birthday2;
            vm.memberInfo.remark = rowData.remark;
            layer.open({
                type: 1,//弹出一个也m页面层
                offset: '100px',
                skin: 'layui-layer-molv',
                title: "更新会员信息",
                area: ['380px', '460px'],
                shadeClose: false,
                content: $("#updateUserinfoWindow"),
            });
        },

        //确认变更会员信息
        doUpdateUserinfo: function (event) {
            vm.memberInfo.birthday = $("#birthday").val();
            console.log('变更会员信息', vm.memberInfo)

            confirm('确定更新会员信息？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/user/updateUserInfo",
                    contentType: "application/json",
                    data: JSON.stringify(vm.memberInfo),
                    success: function(result){
                        if(result.code === 0){
                            alert('操作成功', function(index){
                                vm.reload();
                                layer.closeAll();
                            });
                        }else{
                            alert(result.msg);
                        }
                    }
                });
            });
        },

	}
});
vm.initMsgJQqrid();
$("#messageTmp").hide();
