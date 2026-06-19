$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'sys/systemmsg/list',
        datatype: "json",
        colModel: [			
			{ label: '序号', name: 'sysMsgId', index: 'sysMsgId', width: 50, key: true },
	  		/*{ label: '用户ID', name: 'userId', index: 'userId', width: 80 },*/
            { label: '用户', name: 'userName', index: 'userName', width: 80 },
	  /*	{ label: '消息类型（0:正常消息 1:举报消息 2：退款消息 3：教练回款）', name: 'msgType', index: 'msgType', width: 80 }, 		*/
	  	/*	{ label: '已读标识（0：未读 1：已读）', name: 'readFlag', index: 'readFlag', width: 80 }, 	*/
	  		{ label: '内容', name: 'record', index: 'record', width: 80 },
            { label: '发送时间', name: 'sendTime', index: 'sendTime', width: 80,
                formatter: function (value) {
               //     return value == "" || value == null ? "" : formatDate(new Date(value))
					return value;
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
		systemMsg: {}
	},
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.systemMsg = {};
		},
		update: function (event) {
			var sysMsgId = getSelectedRow();
			if(sysMsgId == null){
				return ;
			}
			vm.showList = false;
            vm.title = "修改";
            
            vm.getInfo(sysMsgId)
		},
		saveOrUpdate: function (event) {
			var url = vm.systemMsg.sysMsgId == null ? "sys/systemmsg/save" : "sys/systemmsg/update";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.systemMsg),
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(index){
							vm.reload();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
		del: function (event) {
			var sysMsgIds = getSelectedRows();
			if(sysMsgIds == null){
				return ;
			}
			
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "sys/systemmsg/delete",
                    contentType: "application/json",
				    data: JSON.stringify(sysMsgIds),
				    success: function(r){
						if(r.code == 0){
							alert('操作成功', function(index){
								$("#jqGrid").trigger("reloadGrid");
							});
						}else{
							alert(r.msg);
						}
					}
				});
			});
		},
		getInfo: function(sysMsgId){
			$.get(baseURL + "sys/systemmsg/info/"+sysMsgId, function(r){
                vm.systemMsg = r.systemMsg;
            });
		},
		reload: function (event) {
			vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                page:page
            }).trigger("reloadGrid");
		},
		validator:function(){
							
											               if(isBlank(vm.systemMsg.userId)){
                   layer.msg("用户ID不能为空");
                   return false;
				}
											               if(isBlank(vm.systemMsg.sendTime)){
                   layer.msg("发送时间不能为空");
                   return false;
				}
											               if(isBlank(vm.systemMsg.msgType)){
                   layer.msg("消息类型（0:正常消息 1:举报消息 2：退款消息 3：教练回款）不能为空");
                   return false;
				}
											               if(isBlank(vm.systemMsg.readFlag)){
                   layer.msg("已读标识（0：未读 1：已读）不能为空");
                   return false;
				}
											               if(isBlank(vm.systemMsg.record)){
                   layer.msg("发送内容不能为空");
                   return false;
				}
										return true;
		}
	}
});