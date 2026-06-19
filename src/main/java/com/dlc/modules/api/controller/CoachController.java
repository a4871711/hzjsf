package com.dlc.modules.api.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dlc.common.exception.RRException;
import com.dlc.common.utils.*;
import com.dlc.modules.api.entity.Coach;
import com.dlc.modules.api.entity.CoachClassShip;
import com.dlc.modules.api.entity.CoachWallet;
import com.dlc.modules.api.service.*;
import com.dlc.modules.api.vo.UserInfoVo;
import com.dlc.modules.qd.utils.PhoneCodeVer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/15/015
 * 教练
 */
@RestController
@RequestMapping("/api/coach")//coach
public class CoachController extends BaseController {

    @Autowired
    private CoachService coachService;
    
    @Autowired
    private CoachClassShipService coachClassShipService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private StuPrivateclassShipService stuPrivateclassShipService;

    @Autowired
    private CoachAppointmentService coachAppointmentService;

    @Autowired
    private PrivateClassOrderService privateClassOrderService;

    /**
     *  @Auther:YD
     *  @parameters:
     *  教练列表
     */
    @RequestMapping("/list")
    public R queryCoachList(@RequestParam Map<String,Object> params){
        Query query = new Query(params);
        List<Map<String,Object>> list = coachService.queryCoachList(query);
        int total = coachService.queryCoachTotal(query);
        PageUtils page = new PageUtils(list,total,query.getLimit(),query.getPage());
        return R.reOk().put("page",page);
    }
    
    /**
     *  @Auther:YD
     *  @parameters:
     *  教练列表
     */
    @RequestMapping("/storeCoachList")
    public R storeCoachList(@RequestParam Map<String,Object> params){
        Query query = new Query(params);
        List<Map<String,Object>> list = coachService.queryStoreCoachList(query);
        int total = coachService.queryStoreCoachTotal(query);
        PageUtils page = new PageUtils(list,total,query.getLimit(),query.getPage());
        return R.reOk().put("page",page);
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  教练详情
     */
    @RequestMapping("/info")
    public R selectCoachInfo(@RequestParam Map<String,Object> params){
        //coachService.selectCoachInfo(params)
        return R.reOk(coachService.selectStoreCoachInfo(params));
    }

    /**
     *  @Auther:YD
     *  @parameters: storeId 门店id
     *  推荐教练
     */
    @RequestMapping("/recommendCoach")
    public R recommendCoach(Long storeId){
        //List<Map<String,Object>> list = coachService.recommendCoach(storeId);
        //场馆教练
        List<Map<String,Object>> list = coachService.recommendStoreCoach(storeId);
        return R.reOk(list);
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  服务场地
     */
    @RequestMapping("/servePlaceList")
    public R servePlaceList(Long coachId){
        return R.reOk(coachService.servePlaceList(coachId));
    }

    /**
     * 认证教练资料提交
     * @param coach
     * @param request
     * @return
     */
    @RequestMapping("/sumbitCoachInfo")
    public R sumbitCoachInfo(Coach coach, HttpServletRequest request){
        if (coach.getCoachName()==null){
            return R.reError("教练名称不能为空");
        }
        if (coach.getProvince()==null){
            return R.reError("所在城市不能为空");
        }
        if (coach.getIdentity()==null){
            return R.reError("身份证号不能为空");
        }
        if (coach.getSex()==null){
            return R.reError("性别不能为空");
        }
        if (coach.getIdentImgUrl()==null){
            return R.reError("身份证正反面不能为空");
        }
        if (coach.getEmployTime()==null){
            return R.reError("从业年限不能为空");
        }
        if (coach.getEmployTime()==null){
            return R.reError("从业年限不能为空");
        }
        if (coach.getDiplomaImgUrl()==null){
            return R.reError("资历证书图不能为空");
        }
        if (coach.getStoreId()==null){
            return R.reError("服务门店不能为空");
        }
        if (coach.getPhone()==null||!PhoneCodeVer.isPhoneNum(coach.getPhone())){
            return R.reError("手机号有误");
        }
        UserInfoVo user = getUserVo(request);
        coach.setUserId(user.getUserId());
        coach.setHeadImgUrl(user.getHeadImgUrl());
        coach.setCreatedDate(new Date());
        int res = coachService.saveCoachInfo(coach);

        if (res>0){
            return R.reOk();
        }
        return R.reError("网络错误，提交失败!");
    }


    /**
     * 教练课程
     * @param request page limit coachId
     * @return
     *
     */
    @RequestMapping("/queryCoachPrivateClass")
    public R queryCoachPrivateClass(@RequestParam Map<String, Object> params,HttpServletRequest request){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return R.error("分页信息不能为空");
        }

        Query query = new Query(params);

        List<Map<String,Object>> list = coachClassShipService.queryCoachClass(query);
        int total = coachClassShipService.queryCoachClassRecordTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.reOk(pageUtil);
    }

    /**
     * 编辑教练课程
     * @param coachClassShip  coachClassShipId,lowestClass
     * @return
     */
    @RequestMapping("/editCoachClass")
    public R editCoachClass(CoachClassShip coachClassShip){
        int res = coachClassShipService.updateCoachClassShio(coachClassShip);
        if (res>0) {
               return R.reOk();
        }
        return R.reError("网络错误，提交失败!");
    }




    /**
     * 待添加课程列表
     * @return
     */
    @RequestMapping("/queryPrivateClass")
    public R queryPrivateClass(HttpServletRequest request,Long coachId){
        //先查询出教练等级，查询出只要符合该教练等级课程
        Integer grade = coachService.queryCoachGradeByCoachId(coachId);
        List<Map<String,Object>> list =  coachService.queryAddClassByGrade(grade);
        return  R.reOk(list);
    }


    /**
     * 场所列表
     * @return
     */
    @RequestMapping("/queryStoreList")
    public R queryStoreList(){

        return R.reOk();
    }


    /**
     * 首页我是教练
     * @param request
     * @return
     */
    @RequestMapping("/queryCoachStatus")
    public JSONObject queryCoachStatus(HttpServletRequest request) {
        Long userId = getUserId(request);
        Map<String, Object> map = coachService.queryCoachStatus(userId);
        JSONObject jsonObject;
        if (map == null) {
            jsonObject = new JSONObject();
            jsonObject.put("code", 100001);
            jsonObject.put("msg", "未提交资料,跳转教练入驻界面");
            return jsonObject;

        }
        //审核状态（0：待审核 1：审核成功 2：审核失败）
        jsonObject = new JSONObject();
        jsonObject.put("code", 1);
        jsonObject.put("msg", "已提交资料");
        jsonObject.put("status", Integer.parseInt(map.get("approveStatus").toString()));
        jsonObject.put("result", map.get("approveResult").toString());
        jsonObject.put("data",map);
        return jsonObject;
    }


    /**
     * 教练端约课列表
     * @param coachId
     * @return
     */
    @RequestMapping("/queryStuPrivateClass")
    public R queryStuPrivateClass(Long coachId){
        List<Map<String, Object>> stuPrivateClassList = stuPrivateclassShipService.queryStuPrivateClassByCoachId(coachId);
        return R.reOk(stuPrivateClassList);
    }


    /**
     *
     * @param coachId privateClassJsonArray :[{"classId":1,"classFee":50,"coachId":1},{},{}]
     * @param privateClassJsonArray
     * @return
     */
    @RequestMapping("/addCoachClass")
    public R addCoachClass(Long coachId, String privateClassJsonArray) {

        JSONArray jsonArray = JSONArray.parseArray(privateClassJsonArray);

        List<CoachClassShip> list = new ArrayList<>();

        for (Object object : jsonArray) {
            CoachClassShip coachClassShip = new CoachClassShip();
            Map privateClassMap = (Map) object;
            coachClassShip.setClassFee(MathUtil.fromStringToTwoPoint(privateClassMap.get("classFee").toString()));
            coachClassShip.setClassId(Long.parseLong(privateClassMap.get("classId").toString()));
            coachClassShip.setCoachId(coachId);
            //判断是否存在
            Boolean isExist = coachClassShipService.isExistClass(coachClassShip);
            if (!isExist){
                list.add(coachClassShip);
            }
        }
        //JSONObject jsonObject = JSONObject.parseObject(privateClassJsonArray);

        //String[] privateClassArray = privateClassIds.split(",");
        //List<String> privateClassList = Arrays.asList(privateClassArray);
        if (list.isEmpty()){
            return R.reError("课程已经存在!");
        }
        int res = coachClassShipService.addCoachClassShip(list);

        if (res>0){
            //查询该教练所有的课程价格，取最小值设教练的最低价格
            List<CoachClassShip> coachClassShips = coachClassShipService.queryAllCoachClass(coachId);
            CoachClassShip coachClassShip = coachClassShips.get(0);
            Coach coach = new Coach();
            coach.setCoachId(coachId);
            coach.setMinClassMoney(coachClassShip.getClassFee());
            coachService.updateCoachInfo(coach);
            coachService.updateCoachClassInfo(coach);
            return R.reOk();
        }
        return R.reError("网络错误,保存失败");
    }

    @RequestMapping("/coachWalletDetail")
    public R coachWalletDetail(HttpServletRequest request,Long coachId){
        //可提现余额
        Double accountBalance = coachService.queryCoachAccountBalance(coachId);
        //
        Double totalIncome = coachService.queryCoachTotalIncome(coachId);
        Double withdrawCash =  coachService.queryWithdrawCash(coachId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountBalance",accountBalance+"");//可提现余额
        jsonObject.put("totalIncome",totalIncome+"");
        jsonObject.put("withdrawCash",withdrawCash+"");//已提现
        return R.reOk(jsonObject);
    }


    /**
     * 教练提交记录列表
     * @param params
     * @return
     */
    @RequestMapping("/withdrawCashList")
    public R withdrawCashList(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        Long userId = getUserId(request);
        params.put("type", 2);
        params.put("userId", userId);
        Query query = new Query(params);
        List<Map<String, Object>> list = coachService.queryWithdrawCashList(query);
        int total = coachService.queryWithdrawCashListTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.reOk(pageUtil);
    }


    @RequestMapping("/queryCoachAppointment")
    public R queryCoachAppointment(Long coachId, String dataTime) {
        String data = coachAppointmentService.queryCoachAppointment(coachId, dataTime);
        JSONArray jsonArray = JSONArray.parseArray(data);
        if(jsonArray==null){
            JSONArray array = JSONArray.parseArray(ConfigConstant.COACHAPPOINTMENT);
            return R.reOk(array);
        }
        //JSONObject jsonObject = JSONObject.parseObject(data);
        //过期时间改变成不可预约(只针对今天的)
        if (dataTime.equals(DateUtils.format(new Date()))) {
            JSONArray timeDataArray = (JSONArray) ((JSONObject) jsonArray.get(0)).get("timeData");
            DateFormat formatter = new SimpleDateFormat("HH");
            for (Object object : timeDataArray) {
                JSONObject timeDataOB = (JSONObject) object;
                Integer timeName = Integer.parseInt(timeDataOB.get("timeName").toString().substring(0, 2));
                Integer nowTime = Integer.parseInt(formatter.format(new Date()));
                if (timeName <= nowTime) {
                    timeDataOB.put("bookType", 1);
                }
            }
        }

        return R.reOk(jsonArray);
    }


    @RequestMapping("/editCoachAppointment")
    public R editCoachAppointment(Long coachId,String dataTime,String dataJsonArray){

        int res =  coachAppointmentService.updateCoachAppointment(coachId, dataTime,dataJsonArray);
        if (res>0){
            return R.reOk();
        }
        return R.error("网络问题保存失败");
    }


    @RequestMapping("/updateCoachStore")
    public R updateCoachStore(Long coachId,String ids){
        try {
            if(org.apache.commons.lang.StringUtils.isBlank(ids) || coachId == null){return R.reError("请求参数有误");}
            //查询已经添加过且状态不为失败的门店
            String[] stIds = ids.split(",");
            List<Long> sts = new ArrayList<>();
            for(String sId : stIds){
                sts.add(Long.parseLong(sId));
            }
            List<Long> existList = coachService.queryExistStoreId(coachId, sts);
            //去除待审核和已通过的
            sts.removeAll(existList);
            if(sts.size() > 0){     //说明有新的门店需要添加
                int res = 0;
                for(Long id : sts){
                    Map<String,Object> paramMap = new HashMap<>();
                    paramMap.put("coachId",coachId);
                    paramMap.put("storeId",id);
                    //add new 20181029 根据门店id查门店名称不全关系表信息
                    String storeName = storeService.queryStoreName(id);
                    paramMap.put("storeName", storeName);
                    res = coachService.addCoachPlaceShip(paramMap);
                }
                if(res > 0){return R.reOk();}
            }else{
                return R.reError("该场所已经存在或提交审核,系统将不再提交审核申请!");
            }
        } catch (NumberFormatException ne) {
            ne.printStackTrace();
            throw new RRException("请求失败",ne);
        }catch (Exception e) {
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
        return R.reError("提交失败!");
    }


    /**
     * 私教课订单列表
     * coachId
     * @param params coachId page limit
     * @return
     */
    @RequestMapping("/coachOrderList")
    public R coachOrderList(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        List<Map<String,Object>> list = privateClassOrderService.queryOrderList(query);
        int total  = privateClassOrderService.queryOrderListTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.reOk(pageUtil);
    }


    /**
     * 支付宝账查询
     * @param coachId
     * @return
     */
    @RequestMapping("/coachAlipayCount")
    public R coachAlipay(Long coachId) {
        Map<String, Object> map = coachService.queryAlipayCountInfo(coachId);
        if (map==null){
            R r = new R();
            r.put("code", "-2");
            r.put("msg", "您未设置支付宝账号");
            return r;
        }
        int lowPirce = coachService.queryLowPriceFromDataMap();
        map.put("lowPrice", lowPirce+"");
        return R.reOk(map);
    }

    /**
     * 私教课订单详情
     * @param privateClassOrderId
     * @return
     */
    @RequestMapping("coachOrderDetail")
    public R coachOrderDetail(Long privateClassOrderId){
        Map<String, Object> map = privateClassOrderService.queryPrivateClassDetail(privateClassOrderId);
        return R.reOk(map);
    }


    @RequestMapping("editAlipayCount")
    public R editAlipayCount(@RequestParam Map<String, Object> params){
        int res =  coachService.updateCoachChWallet(params);
        if (res>0){
            return R.reOk();
        }
        long coachId = Long.parseLong(params.get("coachId").toString());
        String realName = params.get("realName").toString();
        String alipay = params.get("alipay").toString();
        CoachWallet coachWallet = new CoachWallet();
        coachWallet.setAlipay(alipay);
        coachWallet.setCoachId(coachId);
        coachWallet.setRealName(realName);
        int result = coachService.addCoachWallet(coachWallet);
        return R.reError("网络错误,提交失败！");
    }


    /**
     * 教练钱包提现
     * @return
     */
    @RequestMapping("withdrawCash")
    public R withdrawCash(Long coachId, BigDecimal money,String alipay){

        //最低提现金额
        int lowPirce = coachService.queryLowPriceFromDataMap();
        if (money.compareTo(BigDecimal.valueOf(lowPirce))==-1){
            //提现金额必须大于一百
            return R.reError("提现金额必须大于限定值");
        }
        //增加提现记录
        CoachWallet coachWallet = coachService.queryCoachWalletEntity(coachId);
        //BigDecimal balance = coachService.queryCoachWallet(coachId);
        BigDecimal balance = coachWallet.getMoney();
        //提现金额大于余额
        if (money.compareTo(balance)==1){
            return R.reError("提现金额超出了余额!");
        }
        BigDecimal newBalance = balance.subtract(money);
        int i = coachService.updateCoachWalletMoney(newBalance,coachId);
        int res =  coachService.addWithdrawCashRecord(coachId,money,alipay, coachWallet.getRealName());
        //扣减钱包余额
        //先查询出来原始余额

        if (res>0&&i>0){
            return R.reOk();
        }
        return R.reError("网络错误，提交失败!");
    }


    /**
     * 教练资料详情
     * @param coachId
     * @return
     */
    @RequestMapping("queryCoachInfo")
    public R queryCoachInfo(Long coachId){
        Map<String,Object> coachInfo = coachService.queryCoachInfo(coachId);
        return R.reOk(coachInfo);
    }


    /**
     * 编辑
     * @param coach
     * @return
     */
    @RequestMapping("editCoachInfo")
    public R editCoachInfo(Coach coach){
        int res = coachService.updateCoachInfo(coach);
        if (res>0){
            return R.reOk();
        }
        return R.reError("网络问题,提交失败！");
    }

    /**
     * 查询教练评价
     */
}
