import $http from '@/utils/httpRequest'
export default {
	login(data) {
		return $http({
			url: $http.adornUrl('/sys/login'),
			method: 'post',
			//contentType: 'json',
			noAuthorization: true,
			data: $http.adornData({
				...data
			}, false, 'form')
		});
	},

	//登录用户信息-获取
	getInfo(data) {
		return $http({
			url: $http.adornUrl('/sys/user/info'),
			method: 'get',
			// contentType:'json',
			// data: $http.adornData({
			//   ...data
			// },false,'json')
		});
	},

	//导航菜单（路由）
	getRouters(data) {
		return $http({
			url: $http.adornUrl('/sys/menu/nav'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},

	//管理员列表
	user_list(data) {
		return $http({
			url: $http.adornUrl('/sys/user/list'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	//添加管理员
	user_save(data) {
		return $http({
			url: $http.adornUrl('/sys/user/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},

	//编辑管理员
	user_update(data) {
		return $http({
			url: $http.adornUrl('/sys/user/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//角色列表
	getRoleList(data) {
		return $http({
			url: $http.adornUrl('/sys/role/list'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	//部站列表
	getDeptList(data) {
		return $http({
			url: $http.adornUrl('/sys/dept/list'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},

	//删除管理员账号
	user_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/user/delete'),
			method: 'post',
			contentType: 'json',
			data: JSON.stringify(data)
		});
	},
	//日志列表查询
	operlog_list(data) {
		return $http({
			url: $http.adornUrl('/sys/log/list'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	//用户列表
	user_list2(data) {
		return $http({
			url: $http.adornUrl('/sys/user/appList'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	// 清除APP用户关联数据
	user_clearAppUserData(data) {
		return $http({
			url: $http.adornUrl('/sys/user/clearAppUserData'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData(data, false, 'json')
		});
	},
	//用户列表
	user_list3(data) {
		return $http({
			url: $http.adornUrl('/sys/user/storeMemberList'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	
	//用户更新
	user_update2(data) {
		return $http({
			url: $http.adornUrl('/sys/user/updateUserInfo'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	
	//解除续费
	user_del_contract(data) {
		return $http({
			url: $http.adornUrl('/sys/user/deleteContract'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	
	//用户停用
	updateMemberStatus(data) {
		return $http({
			url: $http.adornUrl('/sys/user/updateMemberStatus'),
			method: 'post',
			contentType: 'json',
			data: JSON.stringify(data)
		});
	},
	
	//用户启用
	updateMemberStart(data) {
		return $http({
			url: $http.adornUrl('/sys/user/updateMemberStart'),
			method: 'post',
			contentType: 'json',
			data: JSON.stringify(data)
		});
	},
	
	//用户注销卡
	cancelMemberCard(data) {
		return $http({
			url: $http.adornUrl('/sys/user/cancelMemberCard'),
			method: 'post',
			contentType: 'json',
			data: JSON.stringify(data)
		});
	},
	
	//用户转卡
	transferCard(data) {
		return $http({
			url: $http.adornUrl('/sys/user/transferCard'),
			method: 'post',
			contentType: 'form',
			data: $http.adornData({
				...data
			}, false, 'form')
		});
	},
	
	//用户卡有效期
	updateMemberValidity(data) {
		return $http({
			url: $http.adornUrl('/sys/user/updateMemberValidity'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	
	//用户卡门店
	updateMemberChange(data) {
		return $http({
			url: $http.adornUrl('/sys/user/updateMemberChange'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//购卡记录
	incomepaydetail(data){
		return $http({
			url: $http.adornUrl('/sys/incomepaydetail/list'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	//有效期更新记录
	validityRecordList(data){
		return $http({
			url: $http.adornUrl('/sys/user/validityRecordList'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	//门店更换记录
	changeRecordList(data){
		return $http({
			url: $http.adornUrl('/sys/user/changeRecordList'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	//注销会员记录
	delUserRecordList(data){
		return $http({
			url: $http.adornUrl('/sys/user/delList'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	
	
	//门店列表
	store_list(data) {
		return $http({
			url: $http.adornUrl('/sys/store/list'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	//添加门店
	store_save(data) {
		return $http({
			url: $http.adornUrl('/sys/store/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},

	//编辑门店
	store_update(data) {
		return $http({
			url: $http.adornUrl('/sys/store/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//删除门店账号
	store_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/store/delete'),
			method: 'post',
			contentType: 'json',
			data: JSON.stringify(data)
		});
	},
	
	
	//教练列表
	storecoach_list(data) {
		return $http({
			url: $http.adornUrl('/sys/storecoach/list'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	//添加教练
	storecoach_save(data) {
		return $http({
			url: $http.adornUrl('/sys/storecoach/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},

	//编辑教练
	storecoach_update(data) {
		return $http({
			url: $http.adornUrl('/sys/storecoach/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//删除教练账号
	storecoach_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/storecoach/delete'),
			method: 'post',
			contentType: 'json',
			data: JSON.stringify(data)
		});
	},
	
	openDoorRecordlist(data){
		return $http({
			url: $http.adornUrl('/sys/store/openDoorRecordlist'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	
	//会员卡套餐列表
	fitcard_list(data) {
		return $http({
			url: $http.adornUrl('/sys/fitcard/list'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	//添加会员卡套餐
	fitcard_save(data) {
		return $http({
			url: $http.adornUrl('/sys/fitcard/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},

	//编辑会员卡套餐
	fitcard_update(data) {
		return $http({
			url: $http.adornUrl('/sys/fitcard/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//删除会员卡套餐账号
	fitcard_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/fitcard/delete'),
			method: 'post',
			contentType: 'json',
			data: JSON.stringify(data)
		});
	},
	
	//代理列表
	agent_list(data) {
		return $http({
			url: $http.adornUrl('/sys/agent/list'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//添加代理
	agent_save(data) {
		return $http({
			url: $http.adornUrl('/sys/agent/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},

	//编辑代理
	agent_update(data) {
		return $http({
			url: $http.adornUrl('/sys/agent/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//删除代理账号
	agent_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/agent/delete'),
			method: 'post',
			contentType: 'json',
			data: JSON.stringify(data)
		});
	},
	
	//广告列表
	advertising_list(data) {
		return $http({
			url: $http.adornUrl('/sys/advertising/list'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	//添加广告
	advertising_save(data) {
		return $http({
			url: $http.adornUrl('/sys/advertising/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},

	//编辑广告
	advertising_update(data) {
		return $http({
			url: $http.adornUrl('/sys/advertising/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//删除广告
	advertising_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/advertising/delete'),
			method: 'post',
			contentType: 'json',
			data: JSON.stringify(data)
		});
	},
	
	//协议列表
	protocol_list(data) {
		return $http({
			url: $http.adornUrl('/sys/protocol/list'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	//添加协议
	protocol_save(data) {
		return $http({
			url: $http.adornUrl('/sys/protocol/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},

	//编辑协议
	protocol_update(data) {
		return $http({
			url: $http.adornUrl('/sys/protocol/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//删除协议
	protocol_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/protocol/delete'),
			method: 'post',
			contentType: 'json',
			data: JSON.stringify(data)
		});
	},
	
	//优惠券列表
	sysCoupon_list(data) {
		return $http({
			url: $http.adornUrl('/sys/sysCoupon/list'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//添加优惠券
	sysCoupon_save(data) {
		return $http({
			url: $http.adornUrl('/sys/sysCoupon/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},

	//编辑优惠券
	sysCoupon_update(data) {
		return $http({
			url: $http.adornUrl('/sys/sysCoupon/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//删除优惠券
	sysCoupon_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/sysCoupon/delete'),
			method: 'post',
			contentType: 'json',
			data: JSON.stringify(data)
		});
	},
	
	//发放优惠券
	sysCoupon_send(data) {
		return $http({
			url: $http.adornUrl('/sys/user/sendSysCoupon'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	
	//优惠券发送记录列表
	couponList(data) {
		return $http({
			url: $http.adornUrl('/sys/user/couponList'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},

	//VIP转让费用规则-列表
	vipFeeRule_list(data) {
		return $http({
			url: $http.adornUrl('/sys/vipFeeRule/list'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	//VIP转让费用规则-详情
	vipFeeRule_info(id) {
		return $http({
			url: $http.adornUrl(`/sys/vipFeeRule/info/${id}`),
			method: 'get'
		});
	},
	//VIP转让费用规则-新增
	vipFeeRule_save(data) {
		return $http({
			url: $http.adornUrl('/sys/vipFeeRule/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//VIP转让费用规则-编辑
	vipFeeRule_update(data) {
		return $http({
			url: $http.adornUrl('/sys/vipFeeRule/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//VIP转让费用规则-删除
	vipFeeRule_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/vipFeeRule/delete'),
			method: 'post',
			contentType: 'json',
			data: JSON.stringify(data)
		});
	},
}
