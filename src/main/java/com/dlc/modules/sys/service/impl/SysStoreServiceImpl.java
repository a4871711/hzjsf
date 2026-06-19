package com.dlc.modules.sys.service.impl;

import com.dlc.common.utils.*;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.CoachTradeDetailEntity;
import com.dlc.modules.api.entity.PointsExchange;
import com.dlc.modules.api.entity.SystemMsgEntity;
import com.dlc.modules.sys.dao.SysAgentDao;
import com.dlc.modules.sys.dao.SysCoachDao;
import com.dlc.modules.sys.dao.SysDeviceDao;
import com.dlc.modules.sys.dao.SysIncomePayDetailDao;
import com.dlc.modules.sys.dao.SysStoreDao;
import com.dlc.modules.sys.dao.SysTeamClassDao;
import com.dlc.modules.sys.dao.SysUserDao;
import com.dlc.modules.sys.entity.SysAgentEntity;
import com.dlc.modules.sys.entity.SysStoreEntity;
import com.dlc.modules.sys.entity.SysUserEntity;
import com.dlc.modules.sys.service.SysStoreService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import com.dlc.modules.sys.vo.OpenRecordVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.*;

/**
 * lingkangming
 */

@Service("sysStoreService")
public class SysStoreServiceImpl implements SysStoreService {
	@Autowired
	private SysStoreDao sysStoreDao;
	@Autowired
	private SysTeamClassDao sysTeamClassDao;
	@Autowired
	private ArrangeClassMapper arrangeClassMapper;
	@Autowired
    private CoachWalletMapper coachWalletMapper;
	@Autowired
    private CoachTradeDetailDao coachTradeDetailDao;
    @Autowired
    private SysCoachDao sysCoachDao;
	@Autowired
    private SystemMsgDao systemMsgDao;
    @Autowired
    private SportRecordMapper sportRecordMapper;
    @Autowired
    private UserPointsMapper userPointsMapper;
    @Autowired
    private PointsExchangeMapper pointsExchangeMapper;
    @Autowired
    private FaceIdentyRecordMapper faceIdentyRecordMapper;
    @Autowired
    private OpenDoorRecordMapper openDoorRecordMapper;
    @Autowired
    private SysAgentDao sysAgentDao;
    @Autowired
    private SysIncomePayDetailDao incomePayDetailDao;
    @Autowired
    private SysDeviceDao sysDeviceDao;
    @Autowired
    private SysUserDao sysUserDao;


	@Override
	public SysStoreEntity queryObject(Long id) {
		return sysStoreDao.queryObject(id);
	}

	@Override
	public List<SysStoreEntity> queryList(Map<String, Object> map) {
        //Long sId = ShiroUtils.getUserEntity().getStoreId();
        map.put("storeIds",ShiroUtils.getUserEntity().getStoreIds());
        List<SysStoreEntity> list = sysStoreDao.queryList(map);
        if(list != null && !list.isEmpty()) {
        	for(SysStoreEntity item: list) {
        		/*if(StringUtils.isNotBlank(item.getAgent5Ids())) {
	        		SysAgentEntity params = new SysAgentEntity();
	        		params.setDeleteStatus(0L);
	        		params.setType(5L);
	        		params.setIds(item.getAgent5Ids());
	        		List<SysAgentEntity> agent5 = sysAgentDao.selectAgentList(params);
	        		item.setAgent5(agent5);
        		}
        		if(StringUtils.isNotBlank(item.getAgent6Ids())) {
	        		SysAgentEntity params = new SysAgentEntity();
	        		params.setDeleteStatus(0L);
	        		params.setType(6L);
	        		params.setIds(item.getAgent6Ids());
	        		List<SysAgentEntity> agent6 = sysAgentDao.selectAgentList(params);
	        		item.setAgent6(agent6);
        		}*/
        		Map<String, Object> map1 = new HashMap<>();
        		map1.put("type", 2);
        		map1.put("storeIds", item.getStoreId());
        		List<SysUserEntity> sysUser5 = sysUserDao.queryList(map1);
        		item.setSysUser5(sysUser5);
        		
        		map1.put("type", 0);
        		List<SysUserEntity> sysUser6 = sysUserDao.queryList(map1);
        		item.setSysUser6(sysUser6);
        		
        		//显示统计
        		if(map.get("stats") != null) {
        			Map<String, Object> params = new HashMap<>();
        			params.put("storeAddressId", item.getStoreAddrId());
        			Map<String, Object> stats = incomePayDetailDao.selectStoreStats(params);  
        			if(stats == null)stats = new HashMap<>();
        			
        			Map<String, Object> params1 = new HashMap<>();
        			params1.put("storeAddrIds", item.getStoreAddrId());
        			Map<String, Object> stats2 = sysDeviceDao.selectStoreStats(params);    
        			if(stats2 == null)stats2 = new HashMap<>();
        			
        			stats.putAll(stats2);
        			item.setStats(stats);
        		}
        	}
        }
		return list;
	}

	@Override
	public int queryTotal(Map<String, Object> map) {
		return sysStoreDao.queryTotal(map);
	}

	@Override
	public int save(SysStoreEntity store) {
		return sysStoreDao.save(store);
	}

	@Override
	public void update(SysStoreEntity store) {
		sysStoreDao.update(store);
	}

	@Override
	public int delete(Long id) {
        int res = 0;
        //教练是否选择门店上课
        sysStoreDao.deleteCoachPlace(id);
        //会员是否已购买门店健身卡订单，设备
        sysStoreDao.deleteCardOrder(id);
        sysStoreDao.updateUserDevice(id);
        sysStoreDao.deleteStoreDevice(id);

        //会员是否购买门店团体课,课程安排，团课订单
        //sysStoreDao.deleteTeamClassOrder(id);
        sysStoreDao.deleteTeamClaasArr(id);
        sysStoreDao.deleteTeamClass(id);
        //删除私教课预约信息
        sysStoreDao.deleteStorePrivateArr(id);
        //店社群有没有被关注,以及门店社群关联的动态,动态评论,门店社群
        sysStoreDao.deleteGroupAttion(id);
        sysStoreDao.deleteDyPl(id);
        sysStoreDao.deleteGroupDy(id);
        sysStoreDao.deleteStoreGroup(id);
        //门店下的门店地址
        sysStoreDao.deleteStoreAddr(id);
        //删除门店
        sysStoreDao.delete(id);
        //删除门店登录人员
        //res = sysStoreDao.deleteSysUser(id);
        return res;

	}

	@Override
	public void deleteBatch(Long[] ids) {
		sysStoreDao.deleteBatch(ids);
	}

	@Override
	public List<Map<String, Object>> queryStoreByCondition(Map<String, Object> map) {
		return sysStoreDao.queryStoreByCondition(map);
	}

	@Override
	public void updateStoreAddId(Long storeId) {
		sysStoreDao.updateStoreAddId(storeId);
	}

	@Override
	public PageUtils queryClassList(Query query) {
		List<Map<String, Object>> list = sysStoreDao.queryClassList(query);
        int total = sysStoreDao.queryClassTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
		return pageUtil;
	}

	@Override
	public R confirmClass(Long arrangeClassId) {
	    int res = 0;
        try {
            //先将状态修改为已完成(为了防止页面处理时间过长，而定时任务已经更新状态并退款，产生重复，这里重新查询一次)
            Map<String, Object> arrMap = sysStoreDao.queryArrClassByAcIdStatus(arrangeClassId);
            if(null == arrMap){
                return R.error("失败");
            }
            //先查询该教练等级是否存在
            Integer grade = (Integer) arrMap.get("grade");    //当前教练等级
            int haveGrade = sysStoreDao.queryCoachGradeHas(grade);
            if(haveGrade <= 0){return R.error("教练等级为："+grade+",配置信息已不存在，请在管理员设置中配置此教练等级");}
            Long arrClassId = (Long) arrMap.get("arrangeClassId");
            //1.先将约课状态更新成已完成
            res = arrangeClassMapper.updateClassStatus(arrClassId, 1);
            if(res <= 0){return R.error("失败");}  //未更新成功跳过
            //2.给教练回款，至教练钱包表
            Long coachId = (Long) arrMap.get("coachId");
            BigDecimal money = (BigDecimal) arrMap.get("price");
            Integer classType = (Integer) arrMap.get("orderType");   //当前课程订单类型（私教课程类型）
            //原金额（未折扣前）
            BigDecimal origMoney = money;
            //折扣比例
            Double percent = 100d;
            //计算比例金额(按教练等级比例提成计算)
            if(classType != 1){     //体验课除外
                percent = sysStoreDao.getCoachPercent(grade);
                money = sysStoreDao.getPercentMoney(money, grade);   //比例金额
            }

            String OrderNo = (String) arrMap.get("orderNo");
            int result = 0;
            try {
                result = coachWalletMapper.updateCoachMoney(coachId, money);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //更新个人私教课能量值//上课次数
            try {
                sportRecordMapper.updatePrivateClassInfo(arrMap);
                //update point
                Long userId = (Long) arrMap.get("studentId");
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("userId", userId);
                params.put("energy", arrMap.get("energy"));
                //新增运动时间2019-03-25 20:00~21:00
                if(arrMap.get("duration") != null){
                    BigDecimal dur = (BigDecimal) arrMap.get("duration");
                    int duration = dur.intValue();
                    String hms = duration / 60 + ":" + duration % 60;
                    params.put("sportTime", hms);
                }
                //记录当天团体课能量运动数据
                params.put("sportType", 6);//私教课类型
                sportRecordMapper.saveSportData(params);
                //可兑换积分
                Integer pointEx = userPointsMapper.getPointByCal(params);
                //查询当天积分获取情况和是否为卡类用户以及对应积分上限
                Integer dayPoint = pointsExchangeMapper.querySumDayPoint(userId);  //当天积分兑换总和
                Integer rulePoint = pointsExchangeMapper.queryRulePoints(userId);   //规则积分上限值
                if(dayPoint == null){dayPoint = 0;}
                if(rulePoint == null){rulePoint = 0;}
                if(dayPoint < rulePoint) {
                    //判断兑换后积分是否超出用户一天兑换上限,如果超出
                    if ((dayPoint + pointEx) > rulePoint) {
                        pointEx = rulePoint - dayPoint;
                    }
                    if (pointEx > 0) {
                        params.put("point", pointEx);
                        int rr = userPointsMapper.updatePointByCal(params);
                        if (rr > 0) {
                            //积分兑换记录
                            PointsExchange pointsExchange = new PointsExchange();
                            pointsExchange.setUserId(userId);
                            pointsExchange.setPointsCount(pointEx);
                            pointsExchange.setDetailType((byte) 1);  //运动积分+
                            pointsExchange.setExchangeDate(new Date());
                            pointsExchangeMapper.insertSelective(pointsExchange);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            //3.插入教练交易记录信息//教练上课次数+1
            CoachTradeDetailEntity coachTrade = new CoachTradeDetailEntity();
            coachTrade.setCoachId(coachId);
            coachTrade.setOrderNo(OrderNo);
            coachTrade.setMoney(money);
            coachTrade.setOrigMoney(origMoney);
            coachTrade.setPercent(percent);
            coachTrade.setTradeType(1);    //课程回款
            coachTrade.setStatus(result > 0? 1:2);       //1成功2失败
            coachTrade.setReason(result > 0? "":"回款失败");
            //流水号生成
            String transactionNumber = OrderNoGenerator.getOrderIdByTime() + coachId;
            coachTrade.setTransactionNumber(transactionNumber);
            Date date = new Date();
            coachTrade.setCreateTime(date);
            coachTrade.setTransactionTime(date);
            coachTradeDetailDao.save(coachTrade);
            //系统消息
            if(result > 0){
                SystemMsgEntity systemMsgEntity = new SystemMsgEntity();
                Long userId = (Long) arrMap.get("userId");
                String record = "回款通知：订单号为 "+OrderNo
                        +",课程名为 " +arrMap.get("className")
                        +", 共：1节"+", 已回款。"
                        + (classType==1?"":"原价:"+origMoney+", 提成比例:"+percent+"%")
                        +", 实际到账金额:"+money+", 请关注教练收益。";
                systemMsgEntity.setUserId(userId);  //userId 教练的userId
                systemMsgEntity.setRecord(record);
                systemMsgEntity.setMsgType(3);   //教练回款消息
                systemMsgEntity.setSendTime(new Date());
                systemMsgDao.save(systemMsgEntity);
            }
            //更新教练上课次数+1
            sysCoachDao.updateClassCount(coachId);
        } catch (Exception e) {
            e.printStackTrace();
        }

		return R.reOk();
	}

	@Override
	public PageUtils queryCoachList(Query query) {
        List<Map<String, Object>> list = sysStoreDao.queryCoachList(query);
        int total = sysStoreDao.queryCoachTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
		return pageUtil;
	}

    @Override
    public R updateGrade(Map<String, Object> params) {
	    int res = 0;
	    /*Map<String, Object> updateMap = new HashMap<String, Object>();
        updateMap.put("cpsId", params);
        updateMap.put("grade", 0);*/
        try {
            res = sysStoreDao.updateGrade(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(res <= 0){
            R.reError("更新失败");
        }
        return null;
    }

    @Override
    public Long queryStoreAddrId(Long storeId) {
	    Long storeAddrId = null;
        try {
            storeAddrId = sysStoreDao.queryStoreAddrId(storeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return storeAddrId;
    }

    @Override
    public PageUtils queryTeamClassList(Query query) {
	    //查团体课列表
        List<Map<String, Object>> list = sysTeamClassDao.queryTeamClass(query);
        int total = sysTeamClassDao.queryTeamClassTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return pageUtil;
    }

    @Override
    public R updateClass(Long[] teamClassIds) {
	    List<Long> teamClassIdList = new ArrayList<Long>();
	    for(Long teamClassId : teamClassIds){
            teamClassIdList.add(teamClassId);
        }
        //查询是否有未到上课时间的
        int overFlag = sysTeamClassDao.queryOverTimeClass(teamClassIdList);
        if(overFlag > 0){return R.error("未到上课时间的课程不能提前结束");}
	    int res = 0;
        try {
            //已完成
            //将课程状态更新为已结束
            res = sysTeamClassDao.batchUpdateStatus(teamClassIdList);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res > 0? R.ok() : R.error("更新失败");
    }

    @Override
    public int updateStoreAddIdByMap(Map<String, Object> upMap) {
        return sysStoreDao.updateStoreAddIdByMap(upMap);
    }
    //查团体课学员列表
    @Override
    public PageUtils queryStuInfo(Map<String, Object> params) {
        Query query = new Query(params);
        List<Map<String, Object>> stuList = sysStoreDao.queryTeamClassStuList(query);
        int total = sysStoreDao.queryTeamClassStuTotal(query);
        PageUtils pageUtil = new PageUtils(stuList, total, query.getLimit(), query.getPage());
        return pageUtil;
    }

    @Override
    public int insertStore(Map<String, Object> map) {
        return sysStoreDao.insertStore(map);
    }

    @Override
    public int updateStoretatus(Long id, int status) {
	    //将门店,门店地址，门店社群状态更新
        int res = 0;
        Map<String, Object> upMap = new HashMap<>();
        upMap.put("storeId",id);
        upMap.put("status",status);
        sysStoreDao.updateStoreStatus(upMap);
        sysStoreDao.updateStoreAddr(upMap);
        res = sysStoreDao.updateStoreGroup(upMap);
        return res;
    }

    @Override
    public R signInTeamClass(Long[] stuTeamClassIds) {
        //更新学员课程关系表状态已完成
        int res = 0;

        try {
            res = sysStoreDao.batchUpdateStatusO(stuTeamClassIds);
            //更新sportRecord个人团体课数据(能量/积分)
            try {
                for(Long t : stuTeamClassIds){
                    //根据stuTeamClassId 查询学员id和该课程能量值
                    Map<String, Object> updateMap = sysStoreDao.queryStuTeamClassShip(t);
                    if(updateMap == null){break;}     //说明没有学员报名
                    sportRecordMapper.updateTeamClassInfo(updateMap);     //更新团课记录

                    /**update point 处理积分和能量*/
                    Long userId = (Long) updateMap.get("studentId");
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("userId", userId);
                    params.put("energy", updateMap.get("energy"));
                    //处理上课时间
                    String classTime = (String) updateMap.get("classTime");
                    if( StringUtils.isNotBlank(classTime) ){
                        //新增运动时间2019-03-25 20:00~21:00
                        params.put("sportTime", DateUtils.getTimeDiff(classTime));
                    }
                    //记录当天团体课能量运动数据
                    params.put("sportType", 5);//团体课类型
                    sportRecordMapper.saveSportData(params);
                    //可兑换积分
                    Integer pointEx = userPointsMapper.getPointByCal(params);
                    //查询当天积分获取情况和是否为卡类用户以及对应积分上限
                    Integer dayPoint = pointsExchangeMapper.querySumDayPoint(userId);  //当天积分兑换总和
                    Integer rulePoint = pointsExchangeMapper.queryRulePoints(userId);   //规则积分上限值
                    if(dayPoint == null){dayPoint = 0;}
                    if(rulePoint == null){rulePoint = 0;}
                    if(dayPoint < rulePoint){
                        /**判断兑换后积分是否超出用户一天兑换上限,如果超出*/
                        if((dayPoint+pointEx)>rulePoint){
                            pointEx = rulePoint-dayPoint;
                        }
                        if(pointEx > 0){
                            params.put("point", pointEx);
                            int rr = userPointsMapper.updatePointByCal(params);
                            if(rr > 0){
                                //积分兑换记录
                                PointsExchange pointsExchange = new PointsExchange();
                                pointsExchange.setUserId(userId);
                                pointsExchange.setPointsCount(pointEx);
                                pointsExchange.setDetailType((byte) 1);  //运动积分+
                                pointsExchange.setExchangeDate(new Date());
                                pointsExchangeMapper.insertSelective(pointsExchange);
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res > 0? R.ok() : R.error("更新失败");
    }

    @Override
    public List<Map<String,Object>> queryfaceRecordList(Query query) {
        return faceIdentyRecordMapper.selectfaceRecordList(query);
    }

    @Override
    public int queryfaceRecordTotal(Query query) {
        return faceIdentyRecordMapper.selectfaceRecordTotal(query);
    }

    @Override
    public List<OpenRecordVo> queryOpenDoorRecordList(Query query) {
        return openDoorRecordMapper.selectOpenDoorRecordList(query);
    }

    @Override
    public int queryOpenDoorRecordTotal(Query query) {
        return openDoorRecordMapper.selectOpenDoorRecordTotal(query);
    }

    @Override
    public int getStorePeopleTotal(String storeIds){
        return openDoorRecordMapper.getStorePeopleTotal(storeIds);
    }

    @Override
    public Map getNearOpenLog(Long userId, String startTime){
	    return openDoorRecordMapper.getNearOpenLog(userId, startTime);
    }

    @Override
    public Map getLastOpenLog(Long userId, String id){
        return openDoorRecordMapper.getLastOpenLog(userId, id);
    }

}
