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

	//停卡规则-列表
	vipPauseRule_list(data) {
		return $http({
			url: $http.adornUrl('/sys/vipPauseRule/list'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	//停卡规则-详情
	vipPauseRule_info(id) {
		return $http({
			url: $http.adornUrl(`/sys/vipPauseRule/info/${id}`),
			method: 'get'
		});
	},
	//停卡规则-新增
	vipPauseRule_save(data) {
		return $http({
			url: $http.adornUrl('/sys/vipPauseRule/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//停卡规则-编辑
	vipPauseRule_update(data) {
		return $http({
			url: $http.adornUrl('/sys/vipPauseRule/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//停卡规则-删除
	vipPauseRule_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/vipPauseRule/delete'),
			method: 'post',
			contentType: 'json',
			data: JSON.stringify(data)
		});
	},

	//VIP权益卡商品-列表
	vipCard_list(data) {
		return $http({
			url: $http.adornUrl('/sys/vipCard/list'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	//VIP权益卡商品-详情
	vipCard_info(id) {
		return $http({
			url: $http.adornUrl(`/sys/vipCard/info/${id}`),
			method: 'get'
		});
	},
	//VIP权益卡商品-新增
	vipCard_save(data) {
		return $http({
			url: $http.adornUrl('/sys/vipCard/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//VIP权益卡商品-编辑
	vipCard_update(data) {
		return $http({
			url: $http.adornUrl('/sys/vipCard/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//VIP权益卡商品-删除
	vipCard_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/vipCard/delete'),
			method: 'post',
			contentType: 'json',
			data: JSON.stringify(data)
		});
	},
	//VIP权益卡商品-上架
	vipCard_onCard(data) {
		return $http({
			url: $http.adornUrl('/sys/vipCard/onCard'),
			method: 'post',
			contentType: 'json',
			data: JSON.stringify(data)
		});
	},
	//VIP权益卡商品-下架
	vipCard_offCard(data) {
		return $http({
			url: $http.adornUrl('/sys/vipCard/offCard'),
			method: 'post',
			contentType: 'json',
			data: JSON.stringify(data)
		});
	},

	//VIP转让审核-列表
	vipTransfer_list(data) {
		return $http({
			url: $http.adornUrl('/sys/vipTransfer/list'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	//VIP转让审核-审核(pass=1通过 0驳回)
	vipTransfer_audit(data) {
		return $http({
			url: $http.adornUrl('/sys/vipTransfer/audit'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},

	//停卡记录-列表(后台只读)
	cardPause_list(data) {
		return $http({
			url: $http.adornUrl('/sys/cardPause/list'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},

	//会员黑名单-列表
	memberBlacklist_list(data) {
		return $http({
			url: $http.adornUrl('/sys/memberBlacklist/list'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},
	//会员黑名单-拉黑(phone+reason)
	memberBlacklist_save(data) {
		return $http({
			url: $http.adornUrl('/sys/memberBlacklist/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},
	//会员黑名单-解除({id})
	memberBlacklist_remove(data) {
		return $http({
			url: $http.adornUrl('/sys/memberBlacklist/remove'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({
				...data
			}, false, 'json')
		});
	},

	//权益卡购买记录-列表(后台只读)
	vipCardOrder_list(data) {
		return $http({
			url: $http.adornUrl('/sys/vipCardOrder/list'),
			method: 'get',
			params: $http.adornParams({
				...data
			})
		});
	},

	// ==================== 私教管理后台（第24步）====================

	// ===== 教练域 =====
	// 私教教练-列表
	ptCoach_list(data) {
		return $http({
			url: $http.adornUrl('/sys/ptCoach/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 私教教练-详情({id})
	ptCoach_info(data) {
		return $http({
			url: $http.adornUrl(`/sys/ptCoach/info/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},
	// 私教教练-新增
	ptCoach_save(data) {
		return $http({
			url: $http.adornUrl('/sys/ptCoach/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 私教教练-修改
	ptCoach_update(data) {
		return $http({
			url: $http.adornUrl('/sys/ptCoach/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 私教教练-删除(ids 数组)
	ptCoach_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/ptCoach/delete'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData(data, false, 'json')
		});
	},
	// 私教教练-启用/停用({id,status,disableReason?})
	ptCoach_changeStatus(data) {
		return $http({
			url: $http.adornUrl('/sys/ptCoach/changeStatus'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 私教教练-最近预约只读({id})
	ptCoach_appointments(data) {
		return $http({
			url: $http.adornUrl(`/sys/ptCoach/appointments/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},

	// 教练等级-列表
	coachLevel_list(data) {
		return $http({
			url: $http.adornUrl('/sys/coachLevel/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 教练等级-详情({id})
	coachLevel_info(data) {
		return $http({
			url: $http.adornUrl(`/sys/coachLevel/info/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},
	// 教练等级-新增
	coachLevel_save(data) {
		return $http({
			url: $http.adornUrl('/sys/coachLevel/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 教练等级-修改
	coachLevel_update(data) {
		return $http({
			url: $http.adornUrl('/sys/coachLevel/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 教练等级-删除(ids 数组)
	coachLevel_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/coachLevel/delete'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData(data, false, 'json')
		});
	},
	// 教练等级-启用/停用({id,status})
	coachLevel_changeStatus(data) {
		return $http({
			url: $http.adornUrl('/sys/coachLevel/changeStatus'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 教练等级-启用下拉项
	coachLevel_options(data) {
		return $http({
			url: $http.adornUrl('/sys/coachLevel/options'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},

	// 教练分成规则-列表
	commission_list(data) {
		return $http({
			url: $http.adornUrl('/sys/commission/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 教练分成规则-详情({id})
	commission_info(data) {
		return $http({
			url: $http.adornUrl(`/sys/commission/info/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},
	// 教练分成规则-新增
	commission_save(data) {
		return $http({
			url: $http.adornUrl('/sys/commission/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 教练分成规则-修改
	commission_update(data) {
		return $http({
			url: $http.adornUrl('/sys/commission/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 教练分成规则-删除(ids 数组)
	commission_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/commission/delete'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData(data, false, 'json')
		});
	},
	// 教练分成规则-启用/停用({id,status})
	commission_changeStatus(data) {
		return $http({
			url: $http.adornUrl('/sys/commission/changeStatus'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},

	// 教练排班-左侧教练列表
	schedule_coachList(data) {
		return $http({
			url: $http.adornUrl('/sys/schedule/coachList'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 教练排班-某教练排班列表
	schedule_list(data) {
		return $http({
			url: $http.adornUrl('/sys/schedule/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 教练排班-新增
	schedule_save(data) {
		return $http({
			url: $http.adornUrl('/sys/schedule/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 教练排班-修改
	schedule_update(data) {
		return $http({
			url: $http.adornUrl('/sys/schedule/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 教练排班-删除(ids 数组)
	schedule_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/schedule/delete'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData(data, false, 'json')
		});
	},
	// 教练排班-启用/停用({id,isEnabled})
	schedule_changeEnabled(data) {
		return $http({
			url: $http.adornUrl('/sys/schedule/changeEnabled'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},

	// ===== 商品域 =====
	// 私教商品类型-列表
	ptProductType_list(data) {
		return $http({
			url: $http.adornUrl('/sys/ptProductType/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 私教商品类型-详情({id})
	ptProductType_info(data) {
		return $http({
			url: $http.adornUrl(`/sys/ptProductType/info/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},
	// 私教商品类型-新增
	ptProductType_save(data) {
		return $http({
			url: $http.adornUrl('/sys/ptProductType/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 私教商品类型-修改
	ptProductType_update(data) {
		return $http({
			url: $http.adornUrl('/sys/ptProductType/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 私教商品类型-删除(ids 数组)
	ptProductType_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/ptProductType/delete'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData(data, false, 'json')
		});
	},
	// 私教商品类型-启用/停用({id,status})
	ptProductType_updateStatus(data) {
		return $http({
			url: $http.adornUrl('/sys/ptProductType/updateStatus'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 私教商品类型-启用下拉项
	ptProductType_options(data) {
		return $http({
			url: $http.adornUrl('/sys/ptProductType/options'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},

	// 私教商品-列表
	ptProduct_list(data) {
		return $http({
			url: $http.adornUrl('/sys/ptProduct/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 私教商品-详情({id})
	ptProduct_info(data) {
		return $http({
			url: $http.adornUrl(`/sys/ptProduct/info/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},
	// 私教商品-新增
	ptProduct_save(data) {
		return $http({
			url: $http.adornUrl('/sys/ptProduct/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 私教商品-修改
	ptProduct_update(data) {
		return $http({
			url: $http.adornUrl('/sys/ptProduct/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 私教商品-删除(ids 数组)
	ptProduct_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/ptProduct/delete'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData(data, false, 'json')
		});
	},
	// 私教商品-上架入卡(ids 数组)
	ptProduct_onCard(data) {
		return $http({
			url: $http.adornUrl('/sys/ptProduct/onCard'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData(data, false, 'json')
		});
	},
	// 私教商品-下架出卡(ids 数组)
	ptProduct_offCard(data) {
		return $http({
			url: $http.adornUrl('/sys/ptProduct/offCard'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData(data, false, 'json')
		});
	},
	// 私教商品-复制({id})
	ptProduct_copy(data) {
		return $http({
			url: $http.adornUrl(`/sys/ptProduct/copy/${data.id}`),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({}, false, 'json')
		});
	},
	// 私教商品-团课商品下拉项
	ptProduct_groupClassOptions(data) {
		return $http({
			url: $http.adornUrl('/sys/ptProduct/groupClassOptions'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},

	// 会员附赠团课权益-列表(只读)
	ptMemberGroupBenefit_list(data) {
		return $http({
			url: $http.adornUrl('/sys/ptMemberGroupBenefit/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 会员附赠团课权益-详情+流水({id})
	ptMemberGroupBenefit_info(data) {
		return $http({
			url: $http.adornUrl(`/sys/ptMemberGroupBenefit/info/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},

	// ===== 交易域 =====
	// 私教购买记录-列表(只读)
	privateOrder_list(data) {
		return $http({
			url: $http.adornUrl('/sys/privateOrder/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 私教购买记录-详情({id})
	privateOrder_info(data) {
		return $http({
			url: $http.adornUrl(`/sys/privateOrder/info/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},
	// 私教购买记录-退款({orderId,refundAmount,refundLessons?,remark?})
	privateOrder_refund(data) {
		return $http({
			url: $http.adornUrl('/sys/privateOrder/refund'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},

	// 会员私教权益-列表+统计(只读)
	memberBenefit_list(data) {
		return $http({
			url: $http.adornUrl('/sys/memberBenefit/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 会员私教权益-详情({id})
	memberBenefit_info(data) {
		return $http({
			url: $http.adornUrl(`/sys/memberBenefit/info/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},

	// 私教预约记录-列表+统计
	privateAppointment_list(data) {
		return $http({
			url: $http.adornUrl('/sys/privateAppointment/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 私教预约记录-详情({id})
	privateAppointment_info(data) {
		return $http({
			url: $http.adornUrl(`/sys/privateAppointment/info/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},
	// 私教预约记录-完成核销({appointmentId})
	privateAppointment_finish(data) {
		return $http({
			url: $http.adornUrl('/sys/privateAppointment/finish'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 私教预约记录-后台取消({appointmentId,cancelReason?})
	privateAppointment_cancel(data) {
		return $http({
			url: $http.adornUrl('/sys/privateAppointment/cancel'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 私教预约记录-教练代约课({coachId,memberId,productId,appointmentDate,startTime,endTime})
	privateAppointment_coachBook(data) {
		return $http({
			url: $http.adornUrl('/sys/privateAppointment/coachBook'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},

	// ===== 营销域 =====
	// 优惠券-列表
	mkCoupon_list(data) {
		return $http({
			url: $http.adornUrl('/sys/mkCoupon/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 优惠券-详情({id})
	mkCoupon_info(data) {
		return $http({
			url: $http.adornUrl(`/sys/mkCoupon/info/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},
	// 优惠券-新增
	mkCoupon_save(data) {
		return $http({
			url: $http.adornUrl('/sys/mkCoupon/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 优惠券-修改
	mkCoupon_update(data) {
		return $http({
			url: $http.adornUrl('/sys/mkCoupon/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 优惠券-删除(ids 数组)
	mkCoupon_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/mkCoupon/delete'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData(data, false, 'json')
		});
	},
	// 优惠券-上下架({id,status})
	mkCoupon_changeStatus(data) {
		return $http({
			url: $http.adornUrl('/sys/mkCoupon/changeStatus'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 优惠券-后台发券({couponId,memberIds[]})
	mkCoupon_grant(data) {
		return $http({
			url: $http.adornUrl('/sys/mkCoupon/grant'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},

	// 拼团活动-列表
	mkGroupBuy_list(data) {
		return $http({
			url: $http.adornUrl('/sys/mkGroupBuy/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 拼团活动-详情({id})
	mkGroupBuy_info(data) {
		return $http({
			url: $http.adornUrl(`/sys/mkGroupBuy/info/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},
	// 拼团活动-新增
	mkGroupBuy_save(data) {
		return $http({
			url: $http.adornUrl('/sys/mkGroupBuy/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 拼团活动-修改
	mkGroupBuy_update(data) {
		return $http({
			url: $http.adornUrl('/sys/mkGroupBuy/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 拼团活动-删除(ids 数组)
	mkGroupBuy_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/mkGroupBuy/delete'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData(data, false, 'json')
		});
	},
	// 拼团活动-上下架({id,status})
	mkGroupBuy_changeStatus(data) {
		return $http({
			url: $http.adornUrl('/sys/mkGroupBuy/changeStatus'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},

	// 限时秒杀活动-列表
	mkFlashSale_list(data) {
		return $http({
			url: $http.adornUrl('/sys/mkFlashSale/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 限时秒杀活动-详情({id})
	mkFlashSale_info(data) {
		return $http({
			url: $http.adornUrl(`/sys/mkFlashSale/info/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},
	// 限时秒杀活动-新增
	mkFlashSale_save(data) {
		return $http({
			url: $http.adornUrl('/sys/mkFlashSale/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 限时秒杀活动-修改
	mkFlashSale_update(data) {
		return $http({
			url: $http.adornUrl('/sys/mkFlashSale/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 限时秒杀活动-删除(ids 数组)
	mkFlashSale_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/mkFlashSale/delete'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData(data, false, 'json')
		});
	},
	// 限时秒杀活动-上下架({id,status})
	mkFlashSale_changeStatus(data) {
		return $http({
			url: $http.adornUrl('/sys/mkFlashSale/changeStatus'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},

	// 会员领券记录-列表(只读)
	mkMemberCoupon_list(data) {
		return $http({
			url: $http.adornUrl('/sys/mkMemberCoupon/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 会员领券记录-详情({id})
	mkMemberCoupon_info(data) {
		return $http({
			url: $http.adornUrl(`/sys/mkMemberCoupon/info/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},

	// 会员卡权益聚合视图-列表(占位,本期空)
	cardBenefit_list(data) {
		return $http({
			url: $http.adornUrl('/sys/cardBenefit/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},

	// ===== 资金域 =====
	// 会员储值账户-列表+统计
	wallet_list(data) {
		return $http({
			url: $http.adornUrl('/sys/wallet/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 会员储值账户-详情({id})
	wallet_info(data) {
		return $http({
			url: $http.adornUrl(`/sys/wallet/info/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},
	// 会员储值账户-后台人工充值({memberId,amount,remark?})
	wallet_recharge(data) {
		return $http({
			url: $http.adornUrl('/sys/wallet/recharge'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 会员储值账户-冻结({memberId})
	wallet_freeze(data) {
		return $http({
			url: $http.adornUrl('/sys/wallet/freeze'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 会员储值账户-解冻({memberId})
	wallet_unfreeze(data) {
		return $http({
			url: $http.adornUrl('/sys/wallet/unfreeze'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 会员储值流水-列表(只读)
	wallet_flowList(data) {
		return $http({
			url: $http.adornUrl('/sys/wallet/flowList'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},

	// 私教分期计划-列表
	installment_list(data) {
		return $http({
			url: $http.adornUrl('/sys/installment/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 私教分期计划-详情+账单期({id})
	installment_info(data) {
		return $http({
			url: $http.adornUrl(`/sys/installment/info/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},
	// 私教分期计划-催缴({id})
	installment_remind(data) {
		return $http({
			url: $http.adornUrl('/sys/installment/remind'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},

	// ===== 运营域 =====
	// 教练评价-列表
	coachComment_list(data) {
		return $http({
			url: $http.adornUrl('/sys/coachComment/list'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 教练评价-详情({id})
	coachComment_info(data) {
		return $http({
			url: $http.adornUrl(`/sys/coachComment/info/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},
	// 教练评价-手动补录
	coachComment_save(data) {
		return $http({
			url: $http.adornUrl('/sys/coachComment/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 教练评价-编辑
	coachComment_update(data) {
		return $http({
			url: $http.adornUrl('/sys/coachComment/update'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 教练评价-删除(ids 数组)
	coachComment_delete(data) {
		return $http({
			url: $http.adornUrl('/sys/coachComment/delete'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData(data, false, 'json')
		});
	},
	// 教练评价-回复({commentId,replyContent})
	coachComment_reply(data) {
		return $http({
			url: $http.adornUrl('/sys/coachComment/reply'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 教练评价-处理({commentId,handleStatus,handleRemark})
	coachComment_handle(data) {
		return $http({
			url: $http.adornUrl('/sys/coachComment/handle'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},

	// 续费预警规则-列表
	renewalWarning_ruleList(data) {
		return $http({
			url: $http.adornUrl('/sys/renewalWarning/ruleList'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 续费预警规则-详情({id})
	renewalWarning_ruleInfo(data) {
		return $http({
			url: $http.adornUrl(`/sys/renewalWarning/ruleInfo/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},
	// 续费预警规则-新增
	renewalWarning_ruleSave(data) {
		return $http({
			url: $http.adornUrl('/sys/renewalWarning/ruleSave'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 续费预警规则-修改
	renewalWarning_ruleUpdate(data) {
		return $http({
			url: $http.adornUrl('/sys/renewalWarning/ruleUpdate'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 续费预警规则-删除(ids 数组)
	renewalWarning_ruleDelete(data) {
		return $http({
			url: $http.adornUrl('/sys/renewalWarning/ruleDelete'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData(data, false, 'json')
		});
	},
	// 续费预警记录-列表
	renewalWarning_recordList(data) {
		return $http({
			url: $http.adornUrl('/sys/renewalWarning/recordList'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 续费预警记录-跟进({recordId,followRemark})
	renewalWarning_follow(data) {
		return $http({
			url: $http.adornUrl('/sys/renewalWarning/follow'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 续费预警记录-标记状态({recordId,followStatus})
	renewalWarning_markStatus(data) {
		return $http({
			url: $http.adornUrl('/sys/renewalWarning/markStatus'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},

	// 异常预警规则-列表
	exceptionWarning_ruleList(data) {
		return $http({
			url: $http.adornUrl('/sys/exceptionWarning/ruleList'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 异常预警规则-详情({id})
	exceptionWarning_ruleInfo(data) {
		return $http({
			url: $http.adornUrl(`/sys/exceptionWarning/ruleInfo/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},
	// 异常预警规则-新增
	exceptionWarning_ruleSave(data) {
		return $http({
			url: $http.adornUrl('/sys/exceptionWarning/ruleSave'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 异常预警规则-修改
	exceptionWarning_ruleUpdate(data) {
		return $http({
			url: $http.adornUrl('/sys/exceptionWarning/ruleUpdate'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 异常预警规则-删除(ids 数组)
	exceptionWarning_ruleDelete(data) {
		return $http({
			url: $http.adornUrl('/sys/exceptionWarning/ruleDelete'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData(data, false, 'json')
		});
	},
	// 异常预警记录-列表(只读)
	exceptionWarning_recordList(data) {
		return $http({
			url: $http.adornUrl('/sys/exceptionWarning/recordList'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 异常预警-顶部统计
	exceptionWarning_stat(data) {
		return $http({
			url: $http.adornUrl('/sys/exceptionWarning/stat'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},

	// 团课转私教规则-列表
	groupToPrivate_ruleList(data) {
		return $http({
			url: $http.adornUrl('/sys/groupToPrivate/ruleList'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 团课转私教规则-详情({id})
	groupToPrivate_ruleInfo(data) {
		return $http({
			url: $http.adornUrl(`/sys/groupToPrivate/ruleInfo/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},
	// 团课转私教规则-新增
	groupToPrivate_ruleSave(data) {
		return $http({
			url: $http.adornUrl('/sys/groupToPrivate/ruleSave'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 团课转私教规则-修改
	groupToPrivate_ruleUpdate(data) {
		return $http({
			url: $http.adornUrl('/sys/groupToPrivate/ruleUpdate'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 团课转私教规则-删除(ids 数组)
	groupToPrivate_ruleDelete(data) {
		return $http({
			url: $http.adornUrl('/sys/groupToPrivate/ruleDelete'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData(data, false, 'json')
		});
	},
	// 团课转私教名单-列表
	groupToPrivate_leadList(data) {
		return $http({
			url: $http.adornUrl('/sys/groupToPrivate/leadList'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 团课转私教名单-详情({id})
	groupToPrivate_leadInfo(data) {
		return $http({
			url: $http.adornUrl(`/sys/groupToPrivate/leadInfo/${data.id}`),
			method: 'get',
			params: $http.adornParams({})
		});
	},
	// 团课转私教名单-发券({leadId,couponId})
	groupToPrivate_sendCoupon(data) {
		return $http({
			url: $http.adornUrl('/sys/groupToPrivate/sendCoupon'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 团课转私教名单-跟进({leadId,followStatus,followRemark})
	groupToPrivate_follow(data) {
		return $http({
			url: $http.adornUrl('/sys/groupToPrivate/follow'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},
	// 团课转私教名单-标记已转化({leadId})
	groupToPrivate_markConverted(data) {
		return $http({
			url: $http.adornUrl('/sys/groupToPrivate/markConverted'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},

	// 跨店结算规则-取当前唯一规则
	crossStoreSettle_info(data) {
		return $http({
			url: $http.adornUrl('/sys/crossStoreSettle/info'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 跨店结算规则-保存(upsert)
	crossStoreSettle_save(data) {
		return $http({
			url: $http.adornUrl('/sys/crossStoreSettle/save'),
			method: 'post',
			contentType: 'json',
			data: $http.adornData({ ...data }, false, 'json')
		});
	},

	// 私教收入报表-顶部汇总(beginDate/endDate 必填)
	privateReport_summary(data) {
		return $http({
			url: $http.adornUrl('/sys/privateReport/summary'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 私教收入报表-门店分组
	privateReport_storeReport(data) {
		return $http({
			url: $http.adornUrl('/sys/privateReport/storeReport'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 私教收入报表-教练分组
	privateReport_coachReport(data) {
		return $http({
			url: $http.adornUrl('/sys/privateReport/coachReport'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 私教收入报表-课程分组
	privateReport_courseReport(data) {
		return $http({
			url: $http.adornUrl('/sys/privateReport/courseReport'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 私教收入报表-明细
	privateReport_detailReport(data) {
		return $http({
			url: $http.adornUrl('/sys/privateReport/detailReport'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
	// 私教收入报表-异常数据列表
	privateReport_abnormalList(data) {
		return $http({
			url: $http.adornUrl('/sys/privateReport/abnormalList'),
			method: 'get',
			params: $http.adornParams({ ...data })
		});
	},
}
