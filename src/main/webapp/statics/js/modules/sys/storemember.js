$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/user/storeMemberList',
        datatype: "json",
        colModel: [			
			{ label: 'ID', name: 'deviceId', index: 'deviceId', width: 20, key: true, hidden: true},
			{ label: '编号', name: 'userId', index: 'userId', width: 40 },
            { label: '姓名', name: 'nickname', index: 'nickname', width: 60 },
			{ label: '手机号码', name: 'phone', index: 'phone', width: 60},
			{
                label: '头像', name: 'storeImgUrl', index: 'headImgUrl', width: 50,
                formatter: function (value) {
                    if(!value)return '';
                    var urlArray = value.split(",");
                    return "<img src='" + urlArray[0] + "' weight='30' height='30pk' />"
                }
            },
            /*{ label: '性别', name: 'sex', index: 'sex', width: 30,
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
            { label: '出生日期', name: 'birthday', index: 'birthday', width: 50},
            { label: '身高', name: 'height', index: 'height', width: 40,
                formatter: function (value) {
                    return value == null || value==""? "--" : value+"cm"
                }
            },
            { label: '体重', name: 'weight', index: 'weight', width: 40,
                formatter: function (value) {
                    return value == null || value==""? "--" : value+"kg"
                }
            },
            {label: '免费赠送', name: 'selecteFree', index: 'selecteFree', width: 45,
                formatter:function (value) {
                    switch (value){
                        case 0: return '<span class="label label-info">未选</span>';
                        case 1: return '<span class="label label-success">已选</span>';
                    }
                }
            },
            { label: '人脸识别状态', name: 'faceStatus', index: 'faceStatus', width: 50,
                formatter: function (value) {
                    if (value == 0) {
                        return '未购卡,未认证人脸';
                    }
                    else if (value == 1) {
                        return '已购卡,已认证人脸';
                    } else if (value == 2) {
                        return '已购卡,未认证人脸';
                    } else {
                        return '';
                    }

                }
            },
            { label: '刷脸次数', name: 'inOutNum', index: 'inOutNum', width: 30,
                formatter: function (value){
                    return isBlank(value) ? 0 : value;
                }
            },*/
			{ label: '开通门店', name: 'storeName', index: 'storeName', width: 50},
            { label: '会员类型', name: 'ctName', index: "ctName", width: 50,
                formatter: function (value){
                    return value + '-用户'
                }
            },
            {label: '会员状态', name: 'status', index: 'status', width: 45,
                formatter:function (value) {
                    switch (value){
                        case 0: return '<span class="label label-info">待确认</span>';
                        case 1: return '<span class="label label-success">已确认</span>';
                        case 2: return '<span class="label label-danger">已停用</span>';
                        case 3: return '<span class="label label-warning">已转卡</span>';
                        case 4: return '<span class="label label-default">已过期</span>';
                        case 5: return '<span class="label label-primary">已注销</span>';
                    }
                }
            },
            {label: '会员状态', name: 'status', index: 'status', width: 0, hidden:true},
            {label: '是否自动续费', name: 'autoPay', index: 'autoPay', width: 45,
                formatter:function (value) {
                    switch (value){
                        case 1: return '<span class="label label-success">是</span>';
                        case 2: return '<span class="label label-info">否</span>';
                        default: return '<span class="label label-info">否</span>';
                    }
                }
            },
            {label: '是否自动续费', name: 'autoPay', index: 'autoPay', width: 0, hidden:true},
            {label: '签约状态', name: 'wtState', index: 'wtState', width: 40,
                formatter:function (value) {
                    switch (value){
                        case 0: return '<span class="label label-info">未签约</span>';
                        case 1: return '<span class="label label-success">已签约</span>';
                        case 2: return '<span class="label label-danger">已解约</span>';
                    }
                }
            },
            { label: '到期时间', name: 'validityDate', index: "validityDate", width: 60},
            { label: '购卡时间', name: 'createdDate', index: "createdDate", width: 70,formatter:function (value) {
                return value == "" || value == null ? "" : formatDate(new Date(value))
            }},
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
            {label: '备注', name: 'remark', index: 'remark', width: 40},
            {
                label: '相关操作', name: 'operate', index: 'operate', width: 0, align: "center",
                formatter: function (value, option, obj) {
                    var html = []
                    if(obj.auditStatus == 2)html.push('<a href="javascript:void(0)" onclick="vm.updateAuditStatus(' + obj.userId + ', 1)">恢复状态</a>')
                    if(obj.auditStatus == 1)html.push('<a href="javascript:void(0)" onclick="vm.updateAuditStatus(' + obj.userId + ', 2)">拉黑账号</a>')
                    
                    //if(obj.phone)html.push('<a href="javascript:void(0)" onclick="vm.updateEmptyPhone(' + obj.userId + ', )">微信解绑</a>')
                    
                    html.push('<a href="javascript:void(0)" onclick="vm.userStoreMemberLog(' + obj.userId + ')">会员开通记录</a>')
                    html.push('<a href="javascript:void(0)" onclick="vm.userCouponList(' + obj.userId + ')">卡券管理</a>')
                    
                    
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
            $("#jqGrid").setGridWidth($("#jqGrid").width());
        }
    });
});
var mtlay;
var vm = new Vue({
	el:'#rrapp',
	data:{
		showList: true,
		title: null,
		list: {},
        messageTemplate: {},
        memberInfo: {},
        qq: {},
        mq: {},
        q:{
            nickname:'',
            phone:"",
            storeName: "",
            autoPay: "",
            selecteFree: "",
            sex: "",
            remark: "",
            wtState: '',
            auditStatus:'',
            status:''
        }
},
methods: {
    query: function () {
        vm.reload();
    },
    queryM: function () {
        vm.reload2();
    },
    queryMsg: function () {
        vm.reload3();
    },
    exportAll: function (event) {
        var postData = []
        for(var i in vm.q){
            if(vm.q[i] || vm.q[i] === 0){
                postData[i] = i + '=' + vm.q[i]
            }
        }
        var query = postData.join('&')
        if(query)query += '&'
        var url = baseURL + "sys/user/exportData?" + query + "startTime=" + $("#sdate").val() +
            "&endTime=" + $("#edate").val()
        window.location.href = encodeURI(url);
    },
    //人脸重新认证
    updateFace: function (event) {
        var id = getSelectedRow();
        if(id == null){
            return ;
        }
        confirm('确认后，请在APP端重新认证人脸', function(){
            $.ajax({
                type: "POST",
                url: baseURL + "sys/user/updateFaceStatus",
                contentType: "application/json",
                data: JSON.stringify(id),
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
        });
    },
    //解除自动续费
    delAutoPay: function (event) {
        var id = getSelectedRow();
        if(id == null){
            return ;
        }
        var rowData = $("#jqGrid").jqGrid('getRowData',id);
        if(rowData.autoPay <= 0){
            layer.msg("请选择'自动续费'的会员执行解除操作");
            return;
        }

        confirm('解除后，该会员将无法周期扣款自动续费', function(){
            $.ajax({
                type: "POST",
                url: baseURL + "sys/user/delAutoPay",
                contentType: "application/json",
                data: JSON.stringify(id),
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
        });
    },

    //会员停用
    updateMemberStatus: function (event) {
        var deviceIds = getSelectedRows();
        if(deviceIds == null){
            return ;
        }
        for(var i=0; i<deviceIds.length; i++){
            var rowData = $("#jqGrid").jqGrid('getRowData',deviceIds[i]);
            if(!(rowData.status == 0 || rowData.status == 1)){
                layer.msg("请选择'已确认'或 '待确认'的会员执行停用操作");
                return;
            }
        }

        confirm('停用后，该会员将无法进行正常刷脸认证、刷手环等相关操作', function(){
            $.ajax({
                type: "POST",
                url: baseURL + "sys/user/updateMemberStatus",
                contentType: "application/json",
                data: JSON.stringify(deviceIds),
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
        });
    },
    //会员启用
    updateMemberStart: function (event) {
        var deviceIds = getSelectedRows();
        if(deviceIds == null){
            return ;
        }
        for(var i=0; i<deviceIds.length; i++){
            var rowData = $("#jqGrid").jqGrid('getRowData',deviceIds[i]);
            if(rowData.status != 2){
                layer.msg("请选择'已停用'的会员执行'启用'操作");
                return;
            }
        }

        confirm('确定启用选中会员？', function(){
            $.ajax({
                type: "POST",
                url: baseURL + "sys/user/updateMemberStart",
                contentType: "application/json",
                data: JSON.stringify(deviceIds),
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
        });
    },
    //注销会员卡
    cancelMemberCard: function () {
        var deviceIds = getSelectedRows();
        if(deviceIds == null){
            return ;
        }
        for(var i=0; i<deviceIds.length; i++){
            var rowData = $("#jqGrid").jqGrid('getRowData',deviceIds[i]);
            if(!(rowData.status == 0 || rowData.status == 1)){
                layer.msg("请选择'已确认'或 '待确认'的会员执行注销操作");
                return;
            }
        }

        confirm('注销后，该会员将无法进行正常刷脸认证、刷手环等相关操作', function(){
            $.ajax({
                type: "POST",
                url: baseURL + "sys/user/cancelMemberCard",
                contentType: "application/json",
                data: JSON.stringify(deviceIds),
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
        });
    },
    //变更有效期
    updateValidity: function (event) {
        var deviceId = getSelectedRow();
        if(deviceId == null){return;}
        vm.memberInfo = {};   //清空上次记录
        var rowData = $("#jqGrid").jqGrid('getRowData',deviceId);
        if(rowData == null){
            layer.msg("请重新选择");
            return;
        }
        if(rowData.status == 3){
            layer.msg("'已转卡'会员不可修改有效期");
            return;
        }
        //赋值更新对象
        vm.memberInfo.deviceId = rowData.deviceId;
        vm.memberInfo.userId = rowData.userId;
        vm.memberInfo.validityDate = rowData.validityDate;
        vm.memberInfo.newValidityDate = rowData.validityDate;
        layer.open({
            type: 1,//弹出一个也m页面层
            offset: '100px',
            skin: 'layui-layer-molv',
            title: "更新会员到期时间",
            area: ['380px', '150px'],
            shadeClose: false,
            content: $("#updateValiWindow"),
        });
    },

    //确认变更有效期
    updateNewValidity: function (event) {
        vm.memberInfo.newValidityDate = $("#vdate").val();
        var oldDate = new Date(vm.memberInfo.validityDate);
        var newDate = new Date(vm.memberInfo.newValidityDate);
        if( newDate.getTime() < new Date().getTime() ){
            layer.msg("更新日期不能小于当前时间");
            return;
        }

        confirm('确定更改到期日至：'+vm.memberInfo.newValidityDate+'？', function(){
            $.ajax({
                type: "POST",
                url: baseURL + "sys/user/updateMemberValidity",
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
        //vm.memberInfo.nickname = rowData.nickname;
        //vm.memberInfo.phone = rowData.phone;
        //vm.memberInfo.sex = rowData.sex == '男' ? 1 : 0;
        //vm.memberInfo.height = rowData.height.replace('cm', '');
        //vm.memberInfo.weight = rowData.weight.replace('kg', '');
        //vm.memberInfo.birthday = rowData.birthday;
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

    reload2: function (event) {
        var page = $("#jqGridAdd").jqGrid('getGridParam', 'memberPage');
        $("#jqGridAdd").jqGrid('setGridParam', {
            page: page,
            postData: vm.qq
        }).trigger("reloadGrid");

    },

    reload3: function (event) {
        var page = $("#jqGridAddMsg").jqGrid('getGridParam', 'msgPage');
        $("#jqGridAddMsg").jqGrid('setGridParam', {
            page: page,
            postData: vm.mq
        }).trigger("reloadGrid");

    },

    //转卡
    transferCard: function (event) {
        //转卡人
        var trDeviceId = getSelectedRow();
        if(trDeviceId == null){return;}
        //已转卡的会员不可以再次转卡
        var rowData = $("#jqGrid").jqGrid('getRowData',trDeviceId);
        if(rowData.status == 3){
            layer.msg(" '已转卡' 会员不可以执行此操作");
            return;
        }
        if(rowData.status == 4){
            layer.msg(" '已过期' 会员不可以执行此操作");
            return;
        }
        var a = layer.open({
            type: 1,//弹出一个也m页面层
            offset: '100px',
            skin: 'layui-layer-molv',
            title: "接转方-会员列表",
            area: ['750px', '500px'],
            shadeClose: false,
            content: $("#transferCard")
        });

    },
    //确认转卡
    chargeCard: function (event) {
        //接转人
        var taUserId = getSelectedWindowRow();
        //转卡人
        var trDeviceId = getSelectedRow();
        var rowData = $("#jqGrid").jqGrid('getRowData',trDeviceId);
        var trUserId = rowData.userId;
        if(taUserId == null || trUserId == null){
            return ;
        }
        if(taUserId == trUserId){
            layer.msg('相同会员间 不可以 执行转卡操作');
            return ;
        }
        confirm('确定, 将编号为: '+trUserId+' 的会员所持有的卡, 转给编号为: '+taUserId+'的会员吗？', function(){
            $.ajax({
                type: "POST",
                url: baseURL + "sys/user/transferCard",
                dataType: "json",
                data: {"taUserId": taUserId, "trUserId": trUserId},
                success: function(result){
                    if(result.code === 0){
                        alert('操作成功', function(index){
                            vm.reload();
                            vm.reload2()
                            layer.closeAll();
                        });
                    }else{
                        alert(result.msg);
                    }
                }
            });
        });
    },
    initJQqrid: function () {
        $("#jqGridAdd").jqGrid({
            url: baseURL + 'sys/user/getTranMemberList',
            datatype: "json",
            colModel: [
                {label: '编号', name: 'userId', index: 'userId', width: 10, key: true},
                {label: '昵称', name: 'nickname', index: 'nickname', width: 30},
                {label: '手机号', name: 'phone', index: 'phone', width: 30},
                {label: '是否有卡', name: 'cardFlag', index: 'cardFlag', width: 30,
                    formatter:function (value) {
                        switch (value){
                            case 0: return '<span class="label label-info">无卡用户</span>';
                            case 1: return '<span class="label label-success">有卡用户</span>';
                        }
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
            pager: "#jqGridPagerAdd",
            jsonReader: {
                root: "memberPage.list",
                page: "memberPage.currPage",
                total: "memberPage.totalPage",
                records: "memberPage.totalCount"
            },

            prmNames : {
                page:"page",
                rows:"limit",
                order: "order"
            },
            gridComplete: function () {
                //隐藏grid底部滚动条
                $("#jqGridAdd").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
                $("#jqGridAdd").setGridWidth($("#transferCard").width());
            }

        });
        for (var i = 0, len = 10; i < len; i++) {
            $("#jqGridAdd").jqGrid('addRowData', i + 1, i + 1);
        }
    },
    //消息推送
    pushMessage: function () {
        //推送对象
        var deviceId = getSelectedRows();
        if(deviceId == null){return;}
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
        var deviceIds = getSelectedRows();
        var mtId = getSelectedWindowRow2();
        if(deviceIds == null || mtId == null){return;}
        confirm('确定将模板消息推送给当前页选中的'+deviceIds.length+'位用户吗？若想要一次性推送给更多的用户，可通过调节页面下方页容量实现', function(){
            $.ajax({
                type: "POST",
                url: baseURL + "sys/user/pushMsgToUser?mtId="+mtId,
                contentType: "application/json",
                data: JSON.stringify(deviceIds),
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
            if(vm.messageTemplate.message.length > 100){
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
    }
}
});
vm.initJQqrid();
$("#transferCard").hide();

vm.initMsgJQqrid();
$("#messageTmp").hide();