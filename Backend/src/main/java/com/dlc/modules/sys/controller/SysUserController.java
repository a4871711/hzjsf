package com.dlc.modules.sys.controller;


import com.dlc.common.annotation.SysLog;
import com.dlc.common.exception.RRException;
import com.dlc.common.utils.*;
import com.dlc.common.validator.Assert;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.common.validator.group.AddGroup;
import com.dlc.common.validator.group.UpdateGroup;
import com.dlc.modules.api.dao.DeviceMapper;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.entity.Device;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.service.UserInfoService;
import com.dlc.modules.api.service.WxPayService;
import com.dlc.modules.api.vo.UserInfoVo;
import com.dlc.modules.qd.utils.PhoneCodeVer;
import com.dlc.modules.sys.dao.SysDeviceChangeDao;
import com.dlc.modules.sys.dao.SysDeviceDao;
import com.dlc.modules.sys.entity.*;
import com.dlc.modules.sys.service.SysUserRoleService;
import com.dlc.modules.sys.service.SysUserService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统用户
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年10月31日 上午10:40:10
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends AbstractController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private SysDeviceDao deviceDao;
    @Autowired
    private SysDeviceChangeDao deviceChangeDao;
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 所有用户列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:user:list")
    public R list(@RequestParam Map<String, Object> params){
        Long userId = ShiroUtils.getUserId();
        if(!userId.equals(1)){  //排除管理员
            params.put("user_id", userId);
        }
        //查询列表数据
        Query query = new Query(params);
        List<SysUserEntity> userList = sysUserService.queryList(query);
        int total = sysUserService.queryTotal(query);

        PageUtils pageUtil = new PageUtils(userList, total, query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil);
    }

    /**
     * 门店用户列表
     */
    @RequestMapping("/storeMemberList")
    public R storeMemberList(@RequestParam Map<String, Object> params){
        String storeIds = ShiroUtils.getUserEntity().getStoreIds();
        //if(storeId == null){return R.error("暂无数据");}
        //查询列表数据
        params.put("storeIds",storeIds);
        PageUtils pageUtil = sysUserService.queryStoreMemberList(params);
        return R.ok().put("page", pageUtil);
    }

    /**
     * 导出门店用户列表
     */
    @RequestMapping("/exportData")
    public R exportData(@RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        //Long storeId = ShiroUtils.getUserEntity().getStoreId();
        //if(storeId == null){return R.error("暂无数据");}
        //查询列表数据
        params.put("storeIds",ShiroUtils.getUserEntity().getStoreIds());
        List<SysStoreMemberExcel> list = sysUserService.queryExportStoreMemberList(params);
        String fileName = "门店会员列表.xls";
        String[] titles = {"编号", "昵称", "手机号", "所属门店", "性别", "出生日期", "身高cm", "体重kg", "备注", "人脸识别状态", "刷脸次数","会员类型",
        "卡名称","会员状态","到期时间","购卡时间"};
        String[] columns = {"userId", "nickname", "phone", "storeName", "sex", "birthday", "height", "weight", "remark", "faceStatus", "inOutNum","ctName",
        "cardName","status","validityDate","createdDate"};
        export(response, list, fileName, titles, columns);
        return R.ok("正在导出数据...");
    }

    public static void export(HttpServletResponse response, List<?> list, String fileName, String[] title, String[] columns) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[][] values = new String[list.size()][];

        for (int i = 0; i < list.size(); i++) {
            values[i] = new String[title.length];
            for (int j = 0; j < columns.length; j++) {
                Object t = list.get(i);
                String column = columns[j];

                if (column.length() == 0) {
                    throw new Exception("字段不能为空");
                }
                Object obj;
                if (t instanceof Map) {
                    obj = ((Map<?, ?>) t).get(column);
                } else {
                    Class cls = t.getClass();
                    String getMethod = "get" + column.substring(0, 1).toUpperCase() + column.substring(1, column.length());
                    Method method = cls.getMethod(getMethod);
                    obj = method.invoke(t);
                }
                if (obj instanceof Date) {
                    Date date = (Date) obj;
                    values[i][j] = format.format(date);
                } else if (obj instanceof BigDecimal) {
                    BigDecimal money = (BigDecimal) obj;
                    money.setScale(2, BigDecimal.ROUND_HALF_DOWN);
                    values[i][j] = String.valueOf(money.doubleValue());
                } else {
                    values[i][j] = obj == null ? "" : obj.toString();
                }
                if ("sex".equals(column)) {
                    if (values[i][j] != null) {
                        switch (values[i][j]) {
                            case "1":
                                values[i][j] = "男";
                                break;
                            case "2":
                                values[i][j] = "女";
                                break;
                            default:
                                values[i][j] = "未知";
                                break;
                        }
                    }
                }

                if ("faceStatus".equals(column)) {
                    if (values[i][j] != null) {
                        switch (values[i][j]) {
                            case "0":
                                values[i][j] = "未购卡,未认证人脸";
                                break;
                            case "1":
                                values[i][j] = "已购卡,已认证人脸";
                                break;
                            case "2":
                                values[i][j] = "已购卡,未认证人脸";
                                break;
                            default:
                                values[i][j] = "--";
                                break;
                        }
                    }
                }

                if ("status".equals(column)) {
                    if (values[i][j] != null) {
                        switch (values[i][j]) {
                            case "0":
                                values[i][j] = "待确认";
                                break;
                            case "1":
                                values[i][j] = "已确认";
                                break;
                            case "2":
                                values[i][j] = "已停用";
                                break;
                            case "3":
                                values[i][j] = "已转卡";
                                break;
                            case "4":
                                values[i][j] = "已过期";
                                break;
                            case "5":
                                values[i][j] = "已注销";
                                break;
                            default:
                                values[i][j] = "--";
                                break;
                        }
                    }
                }

                if ("wtState".equals(column)) {
                    if (values[i][j] != null) {
                        switch (values[i][j]) {
                            case "0":
                                values[i][j] = "未签约";
                                break;
                            case "1":
                                values[i][j] = "已签约";
                                break;
                            case "2":
                                values[i][j] = "已解约";
                                break;
                            default:
                                break;
                        }
                    }
                }

                if ("auditStatus".equals(column)) {
                    if (values[i][j] != null) {
                        switch (values[i][j]) {
                            case "1":
                                values[i][j] = "正常";
                                break;
                            case "2":
                                values[i][j] = "黑名单";
                                break;
                            default:
                                break;
                        }
                    }
                }

                if ("couponStatus".equals(column)) {
                    if (values[i][j] != null) {
                        switch (values[i][j]) {
                            case "0":
                                values[i][j] = "未使用";
                                break;
                            case "1":
                                values[i][j] = "已使用";
                                break;
                            case "2":
                                values[i][j] = "已过期";
                                break;
                            default:
                                break;
                        }
                    }
                }

                if ("couponType".equals(column)) {
                    if (values[i][j] != null) {
                        switch (values[i][j]) {
                            case "0":
                                values[i][j] = "普通";
                                break;
                            case "1":
                                values[i][j] = "抖音";
                                break;
                            case "2":
                                values[i][j] = "美团";
                                break;
                            default:
                                break;
                        }
                    }
                }

                if ("couponNew".equals(column)) {
                    if (values[i][j] != null) {
                        values[i][j] = "1".equals(values[i][j]) ? "是" : "否";
                    }
                }

            }
        }
        HSSFWorkbook wb = ExportExcel.getHSSFWorkbook(fileName, title, values, null);

        OutputStream os = null;
        // 将文件存到指定位置
        try {
            setResponseHeader(response, fileName);
            os = response.getOutputStream();
            // os = new FileOutputStream("D:/测试Excel.xls");
            //写入excel文件
            wb.write(os);
            os.flush();
        } catch (Exception e) {
            throw new RRException("写入文件出错");
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                throw new RRException("关闭流文件出错");
            }
        }
    }
    public static void setResponseHeader(HttpServletResponse response, String fileName) {
        try {

            String headStr = "attachment; filename=\"" + new String(fileName.getBytes("utf-8"), "ISO8859-1") + "\"";
            response.setContentType("octets/stream");
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.setHeader("Content-Disposition", headStr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * 查询转卡用户列表
     */
    @RequestMapping("/getTranMemberList")
    public R getTranMemberList(@RequestParam Map<String, Object> params){
        //Long storeId = ShiroUtils.getUserEntity().getStoreId();
        //查询列表数据
        params.put("storeIds",ShiroUtils.getUserEntity().getStoreIds());
        PageUtils pageUtil = sysUserService.getTranMemberList(params);
        return R.ok().put("memberPage", pageUtil);
    }
    /**
     * 查询模板列表
     */
    @RequestMapping("/getMessageTmpList")
    public R getMessageTmpList(@RequestParam Map<String, Object> params){
        //Long storeId = ShiroUtils.getUserEntity().getStoreId();
        //查询列表数据
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        PageUtils pageUtil = sysUserService.getMessageTmpList(params);
        return R.ok().put("msgPage", pageUtil);
    }
    /**
     * 门店用户人脸认证重置
     */
    @RequestMapping("/updateFaceStatus")
    @RequiresPermissions("sys:user:updateFaceStatus")
    public R updateFaceStatus(@RequestBody Long id){
        int res = sysUserService.updateFaceStatusR(id);
        if(res > 0){return R.ok();}
        return R.error("失败");
    }
    /**
     * 转卡
     */
    @RequestMapping("/transferCard")
    public R transferCard(@RequestParam Long taUserId, Long trUserId){
        if(taUserId == null || trUserId == null){return R.error("转卡人和接转人编号不能为空");}
        if(trUserId.equals(taUserId)){
            return R.error("转卡人和接转人不能为相同用户");
        }
        Long storeId = ShiroUtils.getUserEntity().getStoreId();
        //转卡人有效期时间是否已过期
        SysDeviceEntity sysDeviceEntity = sysUserService.queryStoreMemberById(trUserId, storeId);
        if(sysDeviceEntity == null){
            return R.error("当前用户不能执行 '转卡' 操作");
        }
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        Date validate = sysDeviceEntity.getValidityDate();
        long dayGap = DateUtils.getDayDiff(df.format(new Date()), df.format(validate));
        if(dayGap <= 0){
            return R.error("转卡人健身卡有效期至:"+df.format(validate)+ ", 超出有效期(含)期限, 将不可执行 '转卡' 操作");
        }
        //接转人在其他门店是否已购卡
        int ifCard = sysUserService.queryOtherCard(taUserId, storeId);
        if( ifCard > 0 ){
            return R.error("接转用户在其他门店已有卡，不能执行 '转卡' 操作");
        }
        int r = sysUserService.transferCard(taUserId, trUserId, storeId);
        if(r > 0){
            return R.ok();
        }
        return R.error("失败");
    }

    /**
     * 解除自动续费
     */
    @RequestMapping("/delAutoPay")
    public R delAutoPay(@RequestBody Long id){
        //更新设备表会员续费状态为2 非自动续费

        return sysUserService.delAutoPayByDeviceId(id);
    }

    /**
     * 门店会员停用
     */
    @RequestMapping("/updateMemberStatus")
    @RequiresPermissions("sys:user:updateMemberStatus")
    public R updateMemberStatus(@RequestBody Long[] deviceIds){
        //更新设备表会员使用状态为2 停用
        int res = sysUserService.updateMemberStatus(deviceIds, 2);
        if(res > 0){
            return R.ok();
        }
        return R.error("失败");
    }
    /**
     * 门店会员启用
     */
    @RequestMapping("/updateMemberStart")
    @RequiresPermissions("sys:user:updateMemberStatus")
    public R updateMemberStart(@RequestBody Long[] deviceIds){
        //更新设备表会员使用状态为1 启用
        int res = sysUserService.updateMemberStatusStart(deviceIds);
        if(res > 0){
            return R.ok();
        }
        return R.error("失败");
    }
    /**
     * 注销门店会员卡
     */
    @RequestMapping("/cancelMemberCard")
    public R cancelMemberCard(@RequestBody Long[] deviceIds){
        //更新设备表会员使用状态为5 注销
        int res = sysUserService.updateMemberStatus(deviceIds, 5);
        if(res > 0){
            List userIds = sysUserService.getUserIdsByDeviceId(deviceIds);
            System.out.println("注销用户IDS" + userIds );
            sysUserService.batchDeleteUsers(userIds, ShiroUtils.getUserId(), new Date());
            return R.ok();
        }
        return R.error("失败");
    }
    /**
     * app端所有用户列表
     */
    @RequestMapping("/appList")
    @RequiresPermissions("sys:user:appList")
    public R appList(@RequestParam Map<String, Object> params){
        //查询列表数据
        PageUtils pageUtil = sysUserService.queryAppList(params);
        return R.ok().put("page", pageUtil);
    }

    /**
     * 清除APP用户关联数据（需验证当前登录管理员密码）
     */
    @SysLog("清除APP用户数据")
    @RequestMapping("/clearAppUserData")
    @RequiresPermissions("sys:appuser:clearData")
    public R clearAppUserData(@RequestBody Map<String, Object> params) {
        Object userIdObj = params.get("userId");
        if (userIdObj == null) {
            return R.error("用户参数错误");
        }
        Long userId = Long.valueOf(userIdObj.toString());
        String password = params.get("password") == null ? null : params.get("password").toString();
        List<String> clearTypes = null;
        Object clearTypesObj = params.get("clearTypes");
        if (clearTypesObj instanceof List) {
            clearTypes = (List<String>) clearTypesObj;
        }
        return sysUserService.clearAppUserData(userId, password, clearTypes);
    }

    /**
     * del所有用户列表
     */
    @RequestMapping("/delList")
    @RequiresPermissions("sys:user:appList")
    public R delList(@RequestParam Map<String, Object> params){
        //查询列表数据
    	params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        PageUtils pageUtil = sysUserService.queryDelList(params);
        return R.ok().put("page", pageUtil);
    }

    /**
     * 导出会员注销记录
     */
    @RequestMapping("/exportDelList")
    public void exportDelList(@RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        params.put("page", 1);
        params.put("limit", 999999);
        PageUtils pageUtil = sysUserService.queryDelList(params);
        List<Map<String, Object>> list = (List<Map<String, Object>>) pageUtil.getList();
        String fileName = "会员注销记录.xls";
        String[] titles = {"姓名", "手机号", "所属门店", "操作人", "注销时间"};
        String[] columns = {"nickname", "phone", "storeName", "username", "delTime"};
        export(response, list, fileName, titles, columns);
    }
    /**
     * app端所有用户优惠券列表
     */
    @RequestMapping("/userCouponList")
    @RequiresPermissions("sys:user:userCouponList")
    public R userCouponList(@RequestParam Map<String, Object> params){
        //查询列表数据
        PageUtils pageUtil = sysUserService.queryUserCouponList(params);
        return R.ok().put("page", pageUtil);
    }
    /**
     * app端所有用户优惠券列表
     */
    @RequestMapping("/sendCoupon")
    @RequiresPermissions("sys:user:sendCoupon")
    public R sendCoupon(Long[] userIds, Double couponMoney, Integer validDay, BigDecimal limitPrice, String couponTitle, String storeAddrIds){
        //查询列表数据
        return sysUserService.updateUserCoupon(userIds, couponMoney, validDay, limitPrice, couponTitle, storeAddrIds);
    }

    /**
     * app端所有用户优惠券列表
     */
    @RequestMapping("/sendSysCoupon")
    @RequiresPermissions("sys:user:sendCoupon")
    public R sendCoupon(Long[] userIds, Long sysCouponId){
        //查询列表数据
        return sysUserService.updateUserCoupon(userIds, sysCouponId);
    }
    
    /**
     * app端用户优惠券详情列表
     */
    @RequestMapping("/couponInfo/{id}")
    @RequiresPermissions("sys:user:couponInfo")
    public R couponInfo(@PathVariable("id") Long userId){
        //查询列表数据
        logger.info("++++++couponInfo*****"+userId);
        List<Map<String, Object>> pageUtil = sysUserService.queryUserCouponInfo(userId);
        return R.ok().put("couponDetail", pageUtil);
    }

    /**
     * 获取登录的用户信息
     */
    @RequestMapping("/info")
    public R info(){
        return R.ok().put("user", getUser());
    }

    /**
     * 修改登录用户密码
     */
    @SysLog("修改密码")
    @RequestMapping("/password")
    public R password(String password, String newPassword,String code){
        Assert.isBlank(newPassword, "新密码不能为空");
        String phone = ShiroUtils.getUserEntity().getMobile();
        //原密码
        password = ShiroUtils.sha256(password, getUser().getSalt());
        //新密码
        newPassword = ShiroUtils.sha256(newPassword, getUser().getSalt());

        /*if(StringUtils.isEmpty(code) || !code.equals(redisUtils.get(ConfigConstant.PHONE+phone))){
            return R.reError("验证码有误");
        }*/

        //更新密码aa
        int count = sysUserService.updatePassword(getUserId(), password, newPassword);
        if(count == 0){
            return R.error("原密码不正确");
        }
        return R.ok();
    }

    /**
     * 获取验证码
     * @return
     */
    @RequestMapping(value = "/newSetCode",method = RequestMethod.GET)
    public R newSetCode(){
        String phone = ShiroUtils.getUserEntity().getMobile();
        if(StringUtils.isEmpty(phone) || !PhoneCodeVer.isPhoneNum(phone)){
            return R.reError("手机号码有误");
        }
        PhoneCodeVer.sendCode(phone);
        return R.reOk();
    }

    /**
     * 用户信息
     */
    @RequestMapping("/info/{userId}")
    @RequiresPermissions("sys:user:info")
    public R info(@PathVariable("userId") Long userId){
        SysUserEntity user = sysUserService.queryObject(userId);

        //获取用户所属的角色列表aa
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userId);
        user.setRoleIdList(roleIdList);
        return R.ok().put("user", user);
    }

    /**
     * 保存用户
     */
    @SysLog("保存用户")
    @RequestMapping("/save")
    @RequiresPermissions("sys:user:save")
    public R save(@RequestBody SysUserEntity user){
        ValidatorUtils.validateEntity(user, AddGroup.class);

        sysUserService.save(user);

        return R.ok();
    }

    /**
     * 修改用户
     */
    @SysLog("修改用户")
    @RequestMapping("/update")
    @RequiresPermissions("sys:user:update")
    public R update(@RequestBody SysUserEntity user){
        ValidatorUtils.validateEntity(user, UpdateGroup.class);

        sysUserService.update(user);

        return R.ok();
    }

    /**
     * 删除用户
     */
    @SysLog("删除用户")
    @RequestMapping("/delete")
    @RequiresPermissions("sys:user:delete")
    public R delete(@RequestBody Long[] userIds){
        if(ArrayUtils.contains(userIds, 1L)){
            return R.error("系统管理员不能删除");
        }

        if(ArrayUtils.contains(userIds, getUserId())){
            return R.error("当前用户不能删除");
        }

        sysUserService.deleteBatch(userIds);

        return R.ok();
    }
    /**
     * 优惠券列表
     */
    @RequestMapping("/couponList")
    public R couponList(@RequestParam Map<String, Object> params){
        //查询列表数据
        //if(ShiroUtils.getUserEntity().getType().equals(1)){
            params.put("storeAddrIds", ShiroUtils.getUserEntity().getStoreAddrIds());
        //}
        PageUtils pageUtil = sysUserService.queryCouponList(params);
        return R.ok().put("page", pageUtil);
    }

    /**
     * 导出APP用户列表
     */
    @RequestMapping("/exportAppList")
    public void exportAppList(@RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        params.put("page", 1);
        params.put("limit", 999999);
        PageUtils pageUtil = sysUserService.queryAppList(params);
        List<Map<String, Object>> list = (List<Map<String, Object>>) pageUtil.getList();
        for (Map<String, Object> item : list) {
            Object faceStatus = item.get("faceStatus");
            int fs = faceStatus == null ? 0 : Integer.parseInt(faceStatus.toString());
            item.put("faceStatus", fs == 0 ? "否" : "是");
        }
        String fileName = "APP用户列表.xls";
        String[] titles = {"编号", "姓名", "手机号", "OpenId", "签约状态", "是否会员", "状态", "注册时间"};
        String[] columns = {"userId", "nickname", "phone", "openId", "wtState", "faceStatus", "auditStatus", "createdDate"};
        export(response, list, fileName, titles, columns);
    }

    /**
     * 导出优惠券列表（发放记录 / 使用记录）
     */
    @RequestMapping("/exportCouponList")
    public void exportCouponList(@RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        params.put("storeAddrIds", ShiroUtils.getUserEntity().getStoreAddrIds());
        params.put("page", 1);
        params.put("limit", 999999);
        PageUtils pageUtil = sysUserService.queryCouponList(params);
        List<UserCouponEntity> list = (List<UserCouponEntity>) pageUtil.getList();
        String fileName;
        String[] titles;
        String[] columns;
        if (params.get("used") != null) {
            fileName = "优惠券使用记录.xls";
            titles = new String[]{"编号", "手机号", "类型", "兑券码", "是否新人券", "优惠金额", "使用门槛", "使用门店", "使用时间"};
            columns = new String[]{"couponId", "phone", "couponType", "goodsId", "couponNew", "couponPrice", "limitPrice", "storeName", "payTime"};
        } else {
            fileName = "优惠券发放记录.xls";
            titles = new String[]{"编号", "手机号", "优惠金额", "使用门槛", "操作人", "状态", "类型", "兑券码", "是否新人券", "到期时间", "发放时间"};
            columns = new String[]{"couponId", "phone", "couponPrice", "limitPrice", "sysUserName", "couponStatus", "couponType", "goodsId", "couponNew", "validityTime", "createdDate"};
        }
        export(response, list, fileName, titles, columns);
    }
    /**
     * 保存模板信息
     */
    @RequestMapping("/saveMsgTemp")
    public R saveMsgTemp(@RequestBody MessageTemplate messageTemplate){
        if(messageTemplate == null){
            return R.error("请求有误");
        }
        Long storeId = ShiroUtils.getUserEntity().getStoreId();
        messageTemplate.setStoreId(storeId == null?0 : storeId);
        sysUserService.saveMsgTemp(messageTemplate);
        return R.ok();
    }
    /**
     * 更新模板信息
     */
    @RequestMapping("/updateMsgTemp")
    public R updateMsgTemp(@RequestBody MessageTemplate messageTemplate){
        if(messageTemplate == null){
            return R.error("请求有误");
        }
        sysUserService.updateMsgTemp(messageTemplate);
        return R.ok();
    }
    /**
     * 删除模板信息
     */
    @RequestMapping("/deleteMsgTemp")
    public R deleteMsgTemp(@RequestBody Long id){
        if(id == null){
            return R.error("请求有误");
        }
        sysUserService.deleteMsgTemp(id);
        return R.ok();
    }
    /**
     * 推送消息
     */
    @RequestMapping("/pushMsgToUser")
    public R pushMsgToUser(@RequestBody Long[] deviceIds, @RequestParam Long mtId){
        if(deviceIds == null || mtId == null){return R.error("未选中");}
        sysUserService.pushMsgToUser(deviceIds, mtId);
        return R.ok();
    }
    /**
     * 推送消息
     */
    @RequestMapping("/pushMsgToUserByUid")
    public R pushMsgToUserByUid(@RequestBody Long[] userIds, @RequestParam Long mtId){
        if(userIds == null || mtId == null){return R.error("未选中");}
        sysUserService.pushMsgToUserByUId(userIds, mtId);
        return R.ok();
    }
    /**
     * 更新到期日
     */
    @RequestMapping("/updateMemberValidity")
    public R updateMemberValidity(@RequestBody NewMember newMember){
        if( newMember == null || newMember.getDeviceId() == null ||
                newMember.getUserId() == null || newMember.getNewValidityDate() == null ){
            return R.error("请求参数缺失");
        }
        //再查一次会员是不是符合更新条件
        SysDeviceEntity sysDeviceEntity = sysUserService.canUpdateValidity(newMember.getDeviceId());
        if( sysDeviceEntity == null ){ return R.error("您更新的记录已不存在！"); }
        if( sysDeviceEntity.getStatus().equals(3)){
            return R.error("已转卡，不可更新");
        }
        newMember.setStoreId(sysDeviceEntity.getStoreId());
        //已过期的
        if(sysDeviceEntity.getStatus().equals(4)){
            //是否已经在其他门店办了卡
            int ifCard = sysUserService.queryOtherCard(newMember.getUserId(), sysDeviceEntity.getStoreId());
            if( ifCard > 0 ){
                return R.error("该用户在其他门店已有卡，不可更新有效期");
            }
            //过期会员更新有效期
            sysUserService.updateOverMemberValiditys(newMember, sysDeviceEntity.getDeviceNo());
            return R.ok();
        }
        //更新
        int res = sysUserService.updateMemberValiditys(newMember);
        return R.ok();
    }

    /**
     * 更换门店
     */
    @RequestMapping("/updateMemberChange")
    public R updateMemberChange(@RequestBody NewMember newMember){
        if( newMember == null || newMember.getDeviceId() == null ||
                newMember.getUserId() == null || newMember.getNewStoreAddrId() == null ){
            return R.error("请求参数缺失");
        }
        SysDeviceEntity device = deviceDao.queryObject(newMember.getDeviceId());
        
        ModelMapper modelMapper = new ModelMapper();
        SysDeviceChangeEntity change = modelMapper.map(device, SysDeviceChangeEntity.class);
        change.setNewStoreAddressId(newMember.getNewStoreAddrId());
        change.setSysUserId(ShiroUtils.getUserId());
        
        SysDeviceEntity update = new SysDeviceEntity();
        update.setDeviceId(device.getDeviceId());       
        update.setStoreId(newMember.getStoreId());
        update.setStoreAddressId(newMember.getNewStoreAddrId());
        
        if(StringUtils.isNotBlank(device.getStoreAddrIds())) {
        	String[] temp = device.getStoreAddrIds().split(",");
        	boolean has = false;
        	for (String s : temp) {
                if (s.equals(newMember.getNewStoreAddrId() + "")) {
                	has = true;
                }
            }
            if(!has)update.setStoreAddrIds(device.getStoreAddrIds() + "," + newMember.getNewStoreAddrId());
        }
        deviceDao.updateDeviceSelective(update);
        deviceChangeDao.insertDeviceSelective(change);
        
        Map<String, Object> userUp = new HashMap<>();
        userUp.put("userId", device.getProxyId());
        userUp.put("nowStoreId", newMember.getNewStoreAddrId());
        sysUserService.updateUserInfo(userUp);
        
        return R.ok();
    }
    /**
     * 有效期更新记录
     */
    @RequestMapping("/validityRecordList")
    public R validityRecordList(@RequestParam Map<String, Object> params){
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        //查询列表数据
        PageUtils pageUtil = sysUserService.getValidityRecordList(params);
        return R.ok().put("page", pageUtil);
    }
    /**
     * 门店更新记录
     */
    @RequestMapping("/changeRecordList")
    public R changeRecordList(@RequestParam Map<String, Object> params){
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<SysDeviceChangeEntity> list = deviceChangeDao.queryList(query);
        int total = deviceChangeDao.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    /**
     * 修改用户
     */
    @SysLog("修改用户信息")
    @RequestMapping("/updateUserInfo")
    public R updateUserinfo(@RequestBody Map user){
        if(StringUtils.isBlank( user.get("userId").toString() )){
            return R.error("用户ID缺失");
        }
        sysUserService.updateUserInfo(user);
        return R.ok();
    }

    /**
     * 解除续费
     */
    @SysLog("解除续费")
    @RequestMapping("/deleteContract")
    public R deleteContract(@RequestBody Map user){
        if(StringUtils.isBlank( user.get("userId").toString() )){
            return R.error("用户ID缺失");
        }
        if(StringUtils.isBlank( user.get("contractId").toString() )){
            return R.error("用户未签约");
        }
        long userId = Long.parseLong(user.get("userId").toString());
        Device myDevice = deviceMapper.selectUserValidity(userId);
        if(myDevice != null) {
        	Device up = new Device();
        	up.setDeviceId(myDevice.getDeviceId());
        	up.setAutoPay(0);
        	deviceMapper.updateByPrimaryKeySelective(up);
        }
        Map<String, Object> userUp = new HashMap<>();
        userUp.put("userId", userId);
        userUp.put("wtState", 2);
        userUp.put("contractId", "");
        userUp.put("contractTime", DateUtils.formatFull(new Date()));
        sysUserService.updateUserInfo(userUp);
        wxPayService.deleteContract(user.get("contractId").toString());
        return R.ok();
    }
    
    /**
	 * 列表
	 */
	@RequestMapping("/userStats")
	public R userStats(@RequestParam Map<String, Object> params){
		params.put("storeAddressIds", ShiroUtils.getUserEntity().getStoreAddrIds());
		Map<String, Object> stats = userInfoMapper.userStats(params);		
		return R.ok().put("data", stats);
	}

}
