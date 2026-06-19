package com.dlc.modules.sys.controller;

import com.dlc.common.annotation.SysLog;
import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CommonUtil;
import com.dlc.common.utils.ExportExcel;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.service.StoreDeviceV2Service;
import com.dlc.modules.sys.dao.SysCouponDao;
import com.dlc.modules.sys.dao.SysIncomePayDetailDao;
import com.dlc.modules.sys.entity.SysStoreAddressEntity;
import com.dlc.modules.sys.entity.SysStoreEntity;
import com.dlc.modules.sys.entity.SysStoreGroupEntity;
import com.dlc.modules.sys.entity.SysUserEntity;
import com.dlc.modules.sys.service.*;
import com.dlc.modules.sys.shiro.ShiroUtils;
import com.dlc.modules.sys.vo.OpenRecordVo;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author lingkangming
 */
@RestController
@RequestMapping("/sys/store")
public class SysStoreController {
	@Autowired
	private SysStoreService sysStoreService;
	//社群
	@Autowired
	private SysStoreGroupService sysStoreGroupService;
	//门店地址
	@Autowired
	private SysStoreAddressService sysStoreAddressService;
	//门店管理员
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysUserRoleService sysUserRoleService;

	@Autowired
	private StoreDeviceV2Service storeDeviceV2Service;
    @Autowired
    private SysIncomePayDetailDao incomePayDetailDao;
    @Autowired
    private SysCouponDao sysCouponDao;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	@RequiresPermissions("sys:store:list")
	public R list(@RequestParam Map<String, Object> params){
		//查询列表数据
        Query query = new Query(params);
		List<SysStoreEntity> storeList = sysStoreService.queryList(query);
		int total = sysStoreService.queryTotal(query);
		PageUtils pageUtil = new PageUtils(storeList, total, query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}
	
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	@RequiresPermissions("sys:store:info")
	public R info(@PathVariable("id") Long id){
		SysStoreEntity store = sysStoreService.queryObject(id);
		
		return R.ok().put("store", store);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	@RequiresPermissions("sys:store:save")
	public R save(@RequestBody Map<String,Object> params){
		if(StringUtils.isBlank(params.get("storeImgUrl")+"")){
			String tu = params.get("storeImgUrl")+"";
			if(tu == ""){
				return R.error("门店详情图至少选择一张");
			}
		}
		Map<String,Object> map = new HashMap<>();
		String phone = params.get("storePhone")+"";
		if(phone !=null){
			map.put("storePhone",phone);
		}
		if (!sysStoreService.queryStoreByCondition(map).isEmpty() || sysUserService.queryUserByPhone(phone)){
			return R.error("该电话号码的门店已经存在,请更换其它号码");
		}

		params.put("createdDate",new Date());

		int sum = sysStoreService.insertStore(params);
		if(sum <= 0){
			return R.error("操作失败");
		}
		if(sum>0){
			Long storeId = Long.valueOf(params.get("storeId")+"");
			SysStoreEntity storeEntity = sysStoreService.queryObject(storeId);
			sysStoreService.updateStoreAddId(storeEntity.getStoreId());
		}
		//获取门店ID,名称，电话等信息
		Long storeIdShare = 0l;
		String storeNameShare = null;
		String storePhoneShare = null;
		String province = null;
		String city = null;
		String zone = null;
		String storeAddrDetail = null;
		String longitude = null;
		String latitude = null;
		if(StringUtils.isNotBlank(params.get("storeId")+"")){
			storeIdShare = Long.valueOf(params.get("storeId")+"");
		}
		if(StringUtils.isNotBlank(params.get("storeName")+"")){
			storeNameShare = params.get("storeName")+"";
		}
		if(StringUtils.isNotBlank(params.get("storePhone")+"")){
			storePhoneShare = params.get("storePhone")+"";
		}
		if(StringUtils.isNotBlank(params.get("province")+"")){
			province = params.get("province")+"";
		}
		if(StringUtils.isNotBlank(params.get("city")+"")){
			city = params.get("city")+"";
		}
		if(StringUtils.isNotBlank(params.get("zone")+"")){
			zone = params.get("zone")+"";
		}
		if(StringUtils.isNotBlank(params.get("storeAddrDetail")+"")){
			storeAddrDetail = params.get("storeAddrDetail")+"";
		}
		if(StringUtils.isNotBlank(params.get("longitude")+"")){
			longitude = params.get("longitude")+"";
		}
		if(StringUtils.isNotBlank(params.get("latitude")+"")){
			latitude = params.get("latitude")+"";
		}

		//往地址表添加当前门店的信息
		SysStoreAddressEntity storeAddress = new SysStoreAddressEntity();
		storeAddress.setStoreId(storeIdShare);
		storeAddress.setStoreName(storeNameShare);
		storeAddress.setPhone(storePhoneShare);
		storeAddress.setProvince(province);
		storeAddress.setCity(city);
		storeAddress.setZone(zone);
		storeAddress.setStoreAddrDetail(storeAddrDetail);
		storeAddress.setLongitude(longitude);
		storeAddress.setLatitude(latitude);
		storeAddress.setCreatedDate(new Date());
		storeAddress.setGoodsIdStoreId(params.get("goodsIdStoreId") == null || params.get("goodsIdStoreId").toString().isEmpty() ? null : Integer.valueOf(params.get("goodsIdStoreId") + ""));
		storeAddress.setDouyinPoiId(params.get("douyinPoiId") == null || params.get("douyinPoiId").toString().isEmpty() ? null : params.get("douyinPoiId").toString().trim());
		int res = sysStoreAddressService.save(storeAddress);
        Long storeAddressId = storeAddress.getStoreAddrId();
		if(res > 0){
		    Map<String, Object> upMap = new HashMap<String, Object>();
		    upMap.put("storeId", storeIdShare);
		    upMap.put("storeAddrId", storeAddressId);
            sysStoreService.updateStoreAddIdByMap(upMap);
        }
		//往社群表添加当前门店的信息
		String groupName = null;
		String groupImg = null;
		SysStoreGroupEntity storeGroup = new SysStoreGroupEntity();
		//Long storeAddressId = storeAddress.getStoreAddrId();
		storeGroup.setStoreId(storeIdShare);
		storeGroup.setStoreAddrId(storeAddressId);
		storeGroup.setGroupAddr(province+city+zone+storeAddrDetail);
		if(StringUtils.isNotBlank(params.get("groupName")+"")){
			groupName = params.get("groupName").toString();
		}
		if(StringUtils.isNotBlank(params.get("groupImg")+"")){
			groupImg = params.get("groupImg").toString();
		}
		storeGroup.setGroupName(groupName);
		storeGroup.setGroupImg(groupImg);
		storeGroup.setCreatedDate(new Date());
		sysStoreGroupService.save(storeGroup);

		//添加门店管理员
		/*SysUserEntity user = new SysUserEntity();
		user.setType(1);
		user.setUsername(storePhoneShare);
		user.setMobile(storePhoneShare);
		user.setStatus(1);
		user.setCreateTime(new Date());
		user.setEmail("123456@qq.com");
		user.setLoginCount(0);
		user.setDeptId(2l);
		user.setStoreId(storeIdShare);
		user.setSalt("123456");
		user.setPassword("123456");
		List<Long> list = new ArrayList<>();
		list.add(1l);
		user.setRoleIdList(list);
		sysUserService.save(user);*/

		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	@RequiresPermissions("sys:store:update")
	public R update(@RequestBody SysStoreEntity store){
		String tu = store.getStoreImgUrl();
		if(tu == ""){
			return R.error("门店详情图至少选择一张");
		}
		if(!store.getStorePhone().equals(sysStoreService.queryObject(store.getStoreId()).getStorePhone())){
			Map<String,Object> map = new HashMap<>();
			map.put("storePhone",store.getStorePhone());
			if (!sysStoreService.queryStoreByCondition(map).isEmpty()){
				return R.error("该电话号码的门店已经存在,请更换其它号码");
			}
		}

		sysStoreService.update(store);
		SysStoreAddressEntity storeAddressEntity = new SysStoreAddressEntity();
		storeAddressEntity.setStoreId(store.getStoreId());
		storeAddressEntity.setStoreName(store.getStoreName());
		storeAddressEntity.setPhone(store.getStorePhone());
		storeAddressEntity.setProvince(store.getProvince());
		storeAddressEntity.setCity(store.getCity());
		storeAddressEntity.setZone(store.getZone());
		storeAddressEntity.setStoreAddrDetail(store.getStoreAddrDetail());
		storeAddressEntity.setStatus(store.getStatus());
		storeAddressEntity.setLatitude(store.getLatitude());
		storeAddressEntity.setLongitude(store.getLongitude());
		storeAddressEntity.setGoodsIdStoreId(store.getGoodsIdStoreId());
		storeAddressEntity.setDouyinPoiId(store.getDouyinPoiId());
		sysStoreAddressService.updateStoreNameAndPhone(storeAddressEntity);
		return R.ok();
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	@RequiresPermissions("sys:store:delete")
	public R delete(@RequestBody Long ids){
		sysStoreService.delete(ids);

		return R.ok();
	}
	/**
	 * 营业
	 */
	@RequestMapping("/start")
	@RequiresPermissions("sys:store:start")
	public R start(@RequestBody Long id){
		int res = sysStoreService.updateStoretatus(id,1);
		if(res > 0){
            return R.ok();
        }
		return R.error("失败");
	}
	/**
	 * 停业
	 */
	@RequestMapping("/pause")
    @RequiresPermissions("sys:store:pause")
	public R pause(@RequestBody Long id){
		int res = sysStoreService.updateStoretatus(id,0);
		if(res > 0){
            return R.ok();
        }
		return R.error("失败");
	}
	/**门店刷脸记录*/
    @RequestMapping("/faceRecordlist")
    public R faceRecordlist(@RequestParam Map<String, Object> params){
        //查询列表数据
        Query query = new Query(params);
        List<Map<String,Object>> list = sysStoreService.queryfaceRecordList(query);
        int total = sysStoreService.queryfaceRecordTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }
	/**门店刷脸记录根据设备编号*/
	@RequestMapping("/faceRecordByNolist")
	public R faceRecordByNolist(@RequestParam Map<String, Object> params){
	    //if(StringUtils.isBlank(deviceNo)){return null;}
	    //params.put("deviceNo", deviceNo);
		//查询列表数据
		Query query = new Query(params);
		List<Map<String,Object>> list = sysStoreService.queryfaceRecordList(query);
		int total = sysStoreService.queryfaceRecordTotal(query);
		PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
		return R.ok().put("facePage", pageUtil);
	}

	/**门店扫码开门记录*/
	@RequestMapping("/openDoorRecordlist")
	public R openDoorRecordlist(@RequestParam Map<String, Object> params){
		Query query = new Query(params);
		query.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
		List<OpenRecordVo> list = sysStoreService.queryOpenDoorRecordList(query);
		int total = sysStoreService.queryOpenDoorRecordTotal(query);
		PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}

	/** 预计门店在线总人数(2小时内到店人数) */
	@RequestMapping("/getStorePeopleTotal")
	public R getStorePeopleTotal(){
		//Long storeId = 0L;
		//if(ShiroUtils.getUserEntity().getType().equals(1)){
		//	storeId = ShiroUtils.getUserEntity().getStoreId();
		//}
		int total = sysStoreService.getStorePeopleTotal(ShiroUtils.getUserEntity().getStoreIds());
		return R.ok().put("total", total);
	}

	/**门店设备列表*/
	@RequestMapping("/deviceV2")
	public R deviceV2(@RequestParam Map<String, Object> params){
		Query query = new Query(params);
		//if(ShiroUtils.getUserEntity().getType().equals(1)){
		//	query.put("store_id", ShiroUtils.getUserEntity().getStoreId());
		//}
		query.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
		List<Map<String,Object>> list = storeDeviceV2Service.queryDeviceList(query);
		int total = storeDeviceV2Service.queryDeviceTotal(query);
		PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}

	/**
	 * 修改设备
	 */
	@SysLog("修改设备信息")
	@RequestMapping("/updateDevice")
	public R updateUserinfo(@RequestBody Map<String, Object> device){
		if(device.get("id").toString().equals("0")){
			device.put("createTime", new Date());
			device.put("status", 1);
			//if(ShiroUtils.getUserEntity().getType().equals(1)){
				device.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
			//}
			storeDeviceV2Service.addDevice(device);
		}else{
			storeDeviceV2Service.updateDevice(device);
		}
		return R.ok();
	}

	/**风控异常用户账号*/
	@RequestMapping("/memberRisk")
	public R memberRisk(@RequestParam Map<String, Object> params){
		Query query = new Query(params);
		//if(ShiroUtils.getUserEntity().getType().equals(1)){
			query.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
		//}
		List<Map<String,Object>> list = sysUserService.queryRiskList(query);
		for(Map item : list){
			item.put("firstTime", "");
			item.put("lastTime", "");
			//异常登录后的第一次扫码开门时间
			Map logFirst = sysStoreService.getNearOpenLog(Long.parseLong(item.get("userId").toString()), item.get("createTime").toString());
			if(logFirst != null){
				item.put("firstTime", logFirst.get("open_time").toString() );
				//异常登录前的最后一次扫码开门时间
				Map logLast = sysStoreService.getLastOpenLog(Long.parseLong(item.get("userId").toString()), logFirst.get("id").toString() );
				if(logLast != null){
					item.put("lastTime", logLast.get("open_time").toString() );
				}
			}

		}
		int total = sysUserService.queryRiskTotal(query);
		PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}

	/**风控异常记录人工审核*/
	@RequestMapping("/checkRisk")
	public R checkRisk(@RequestParam("id") int id, @RequestParam("check") int check){
		sysUserService.checkRisk(id, check);
		return R.ok();
	}



	/*** 导出门店扫码开门记录 ***********************************************/
	@RequestMapping("/exportOpenDoorRecord")
	public void exportOpenDoorRecord(@RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception{
		params.put("page", 1);
		params.put("limit", 999999);
		Query query = new Query(params);
		query.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
		List<OpenRecordVo> list = sysStoreService.queryOpenDoorRecordList(query);

		String fileName = "扫码开门记录.xlsx";
		String[] titles = {"编号", "用户昵称", "手机号", "门店", "会员所属门店", "距离(米)", "开门结果", "原因", "扫码时间"};
		String[] columns = {"id", "nickname", "phone", "storeName", "deviceStoreName", "distance", "result", "remark", "createTime"};
		exportXlsx(response, list, fileName, titles, columns);

	}

	public static void exportXlsx(HttpServletResponse response, List<?> list, String fileName, String[] title, String[] columns) throws Exception {
		String[][] values = buildExportValues(list, title, columns);
		XSSFWorkbook wb = ExportExcel.getXSSFWorkbook(fileName, title, values);
		writeWorkbook(response, fileName, wb, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	}

	private static String[][] buildExportValues(List<?> list, String[] title, String[] columns) throws Exception {
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
					String getMethod = "get" + column.substring(0, 1).toUpperCase() + column.substring(1);
					Method method = cls.getMethod(getMethod);
					obj = method.invoke(t);
				}
				if (obj instanceof Date) {
					values[i][j] = format.format((Date) obj);
				} else if (obj instanceof BigDecimal) {
					BigDecimal money = (BigDecimal) obj;
					money.setScale(2, BigDecimal.ROUND_HALF_DOWN);
					values[i][j] = String.valueOf(money.doubleValue());
				} else {
					values[i][j] = obj == null ? "" : obj.toString();
				}
				if ("result".equals(column) && values[i][j] != null) {
					values[i][j] = "1".equals(values[i][j]) ? "成功" : "失败";
				}
			}
		}
		return values;
	}

	private static void writeWorkbook(HttpServletResponse response, String fileName, XSSFWorkbook wb, String contentType) {
		OutputStream os = null;
		try {
			setResponseHeader(response, fileName, contentType);
			os = response.getOutputStream();
			wb.write(os);
			os.flush();
		} catch (Exception e) {
			throw new RRException("写入文件出错");
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					throw new RRException("关闭流文件出错");
				}
			}
		}
	}

	public static void setResponseHeader(HttpServletResponse response, String fileName, String contentType) {
		try {
			String headStr = "attachment; filename=\"" + new String(fileName.getBytes("utf-8"), "ISO8859-1") + "\"";
			response.setContentType(contentType);
			response.setHeader("Content-Disposition", headStr);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 列表
	 */
	@RequestMapping("/cardStats")
	//@RequiresPermissions("sys:store:list")
	public R cardStats(@RequestParam Map<String, Object> params){
		params.put("storeAddressIds", ShiroUtils.getUserEntity().getStoreAddrIds());
		List<Map<String, Object>> statsList = incomePayDetailDao.selectStoreStatsByCard(params);
		BigDecimal total = BigDecimal.ZERO;
		if(statsList != null && !statsList.isEmpty()) {
			for(Map<String, Object> item : statsList) {
				BigDecimal temp = CommonUtil.tryToBigDecimal(String.valueOf(item.get("moneyTotal")));
				total = total.add(temp).setScale(2);
			}
		}
		return R.ok().put("data", statsList).put("total", total);
	}
	
	/**
	 * 列表
	 */
	@RequestMapping("/cardCountStats")
	//@RequiresPermissions("sys:store:list")
	public R cardCountStats(@RequestParam Map<String, Object> params){
		params.put("storeAddressIds", ShiroUtils.getUserEntity().getStoreAddrIds());
		List<Map<String, Object>> statsList = incomePayDetailDao.selectStoreStatsCountByCard(params);
		Map<String, Object> statsCount = sysCouponDao.selectCouponStats(params);		
		return R.ok().put("data", statsList).put("coupon", statsCount);
	}

}
