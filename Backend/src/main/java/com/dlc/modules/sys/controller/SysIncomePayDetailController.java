package com.dlc.modules.sys.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.*;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.modules.api.service.CardOrderService;
import com.dlc.modules.sys.entity.SysIncomePayDetailEntity;
import com.dlc.modules.sys.service.SysIncomePayDetailService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


/**
 * 收支明细表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-11 15:33:19
 */
@RestController
@RequestMapping("sys/incomepaydetail")
public class SysIncomePayDetailController {
    @Autowired
    private SysIncomePayDetailService sysIncomePayDetailService;

    @Autowired
    private CardOrderService cardOrderService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:incomepaydetail:list")
    public R list(@RequestParam Map<String, Object> params) {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<SysIncomePayDetailEntity> sysIncomePayDetailList = sysIncomePayDetailService.queryList(query);
        int total = sysIncomePayDetailService.queryTotal(query);
        PageUtils page = new PageUtils(sysIncomePayDetailList, total, query.getLimit(), query.getPage());
        BigDecimal bigDecimal = new BigDecimal("0.00");
        for (SysIncomePayDetailEntity e : sysIncomePayDetailList) {
            //payType（1,提现  8,积分兑换  9商城退款  11提现失败退款） tradeStatus(3，已完成)
            if ((e.getPayType() == 1 || e.getPayType() == 8 || e.getPayType() == 9) && e.getTradeStatus() == 3)
                ;
                //     bigDecimal = bigDecimal.subtract(new BigDecimal(e.getMoney().toString()));
            else if (e.getPayType() == 1 || e.getPayType() == 8 || e.getPayType() == 9 || (e.getPayType() == 11 && e.getTradeStatus() != 3))
                ;
            else if (e.getTradeType() != 1 && e.getTradeStatus() == 3 && e.getPayType() != 1 && e.getPayType() != 7 && e.getPayType() != 8 && e.getPayType() != 9 && e.getPayType() != 11)
                bigDecimal = bigDecimal.add(new BigDecimal(e.getMoney().toString()));
        }
        return R.ok().put("page", page).put("pageCountMoney", bigDecimal);
    }

    /**
     * 转卡记录
     * @param params
     * @return
     */
    @RequestMapping("/transformedList")
    public R transformedList(@RequestParam Map<String, Object> params) {
    	params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<Map<String,Object>> sysTransformedList = sysIncomePayDetailService.queryTransList(query);
        int total = sysIncomePayDetailService.queryTransTotal(query);
        PageUtils page = new PageUtils(sysTransformedList, total, query.getLimit(), query.getPage());
        return R.ok().put("page", page);
    }

    /**
     * 商家收支列表
     */
    @RequestMapping("/storedList")
    @RequiresPermissions("sys:incomepaydetail:list")
    public R storedlist(@RequestParam Map<String, Object> params) {
    	params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<SysIncomePayDetailEntity> sysIncomePayDetailList = sysIncomePayDetailService.queryStoredList(query);
        int total = sysIncomePayDetailService.queryStoreTotal(query);
        PageUtils page = new PageUtils(sysIncomePayDetailList, total, query.getLimit(), query.getPage());
        BigDecimal bigDecimal = new BigDecimal("0.00");
        for (SysIncomePayDetailEntity e : sysIncomePayDetailList) {
            //payType（1,提现  8,积分兑换  9商城退款  11提现失败退款） tradeStatus(3，已完成)

            bigDecimal = bigDecimal.add(new BigDecimal(e.getMoney().toString()));
        }
        return R.ok().put("page", page).put("pageCountMoney", bigDecimal);
    }
    /**
     * 所有商家水吧台收支列表
     */
    @RequestMapping("/allstoredList")
    public R allstoredList(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<SysIncomePayDetailEntity> sysIncomePayDetailList = sysIncomePayDetailService.queryAllStoredList(query);
        int total = sysIncomePayDetailService.queryAllStoreTotal(query);
        PageUtils page = new PageUtils(sysIncomePayDetailList, total, query.getLimit(), query.getPage());
        BigDecimal bigDecimal = new BigDecimal("0.00");
        for (SysIncomePayDetailEntity e : sysIncomePayDetailList) {
            //payType（1,提现  8,积分兑换  9商城退款  11提现失败退款） tradeStatus(3，已完成)

            bigDecimal = bigDecimal.add(new BigDecimal(e.getMoney().toString()));
        }
        return R.ok().put("page", page).put("pageCountMoney", bigDecimal);
    }

    @RequestMapping("/countMoney")
    @RequiresPermissions("sys:incomepaydetail:list")
    public R countMoney(@RequestParam Map<String, Object> params) {
        return R.ok().put("countMoney", sysIncomePayDetailService.countMoney(params));
    }

    @RequestMapping("/orderInfo/{id}")
    public R orderInfo(@PathVariable("id") Long id) {
        return R.ok().put("orderDetail", sysIncomePayDetailService.orderInfo(id));
    }

    @RequestMapping("/storedCountMoney")
    @RequiresPermissions("sys:incomepaydetail:list")
    public R storedCountMoney(@RequestParam Map<String, Object> params) {
    	params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        String storedCountMoney=sysIncomePayDetailService.storedCountMoney(params);
        return R.ok().put("countMoney", storedCountMoney==null?0:storedCountMoney);
    }

    /**
     * 更新记录交易状态
     * @return
     */
    @RequestMapping("/updateTradeStatus")
    public R updateTradeStatus(@RequestBody Map income) {
        Long incomePayDetailId = Long.valueOf(income.get("incomePayDetailId").toString());
        Integer tradeStatus = Integer.valueOf(income.get("tradeStatus").toString());
        String orderNo = income.get("orderNo").toString();
        int status = 0;
        switch (tradeStatus){
            case 3: status = 4; break;
            case 7: status = 6; break;
        }
        int result = sysIncomePayDetailService.updateTradeStatus(incomePayDetailId, tradeStatus);
        if(result > 0 && StringUtils.isNotBlank(orderNo) && status > 0){
            cardOrderService.updateOrderStatus(orderNo, status);
        }
        return R.ok();
    }

    /**
     * 导出
     */
    @RequestMapping("/export")
    @RequiresPermissions("sys:incomepaydetail:export")
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<SysIncomePayDetailEntity> sysIncomePayDetailList = sysIncomePayDetailService.queryList(query);
        for(SysIncomePayDetailEntity item: sysIncomePayDetailList) {
        	if (item.getRenewSourceDesc() == null || item.getRenewSourceDesc().trim().isEmpty()) {
        		item.setRenewSourceDesc(resolveRenewSourceDesc(item));
        	}
        	item.setPayTypeDesc(resolvePayTypeDesc(item));
        	switch (item.getCardType()) {
	            case 0:
	                item.setCtName("月卡");
	                break;
	            case 1:
	            	item.setCtName("季卡");
	                break;
	            case 2:
	            	item.setCtName("半年卡");
	                break;
	            case 3:
	            	item.setCtName("年卡");
	                break;
	            default:
	            	item.setCtName("次卡");
	                break;
	        }
        	item.setCouponMoney(item.getPaySum() != null ? (item.getPaySum().subtract(item.getMoney()).setScale(2)) : BigDecimal.ZERO);
        }

        String fileName = "会员购卡记录.xls";
        String[] titles = {"姓名", "手机号码", "所属门店", "会员类型", "卡片分类", "是否自动续费", "支付方式", "订单金额", "卡券金额", "支付金额", "有效期(旧)", "有效期(新)", "购卡时间"};
        String[] columns = {"userName", "phone", "storeName", "ctName", "cardNature", "renewSourceDesc", "payTypeDesc", "paySum", "couponMoney", "money", "oldValidityDate", "validityDate", "transactionTime"};
        export(response, sysIncomePayDetailList, fileName, titles, columns);

    }

    private static String resolveRenewSourceDesc(SysIncomePayDetailEntity item) {
        Integer payType = item.getPayType();
        Integer tradeType = item.getTradeType();
        if (Integer.valueOf(13).equals(payType) || Integer.valueOf(13).equals(tradeType)) {
            return "自动";
        }
        if (tradeType != null && (tradeType == 1 || tradeType == 2 || tradeType == 3)) {
            return "用户";
        }
        if (Integer.valueOf(2).equals(payType)) {
            return "用户";
        }
        return "后台";
    }

    private static String resolvePayTypeDesc(SysIncomePayDetailEntity item) {
        String source = item.getRenewSourceDesc();
        if (source == null || source.trim().isEmpty()) {
            source = resolveRenewSourceDesc(item);
        }
        String label = "后台续费";
        if ("自动".equals(source)) {
            label = "自动续费";
        } else if ("用户".equals(source)) {
            Integer tradeType = item.getTradeType();
            if (Integer.valueOf(3).equals(tradeType)) {
                label = "支付宝";
            } else if (Integer.valueOf(1).equals(tradeType)) {
                label = "钱包支付";
            } else {
                label = "微信支付";
            }
        }
        return label + " 第" + item.getBuyCount() + "次";
    }

    public static void export(HttpServletResponse response, List<?> list, String fileName, String[] title, String[] columns) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[][] values = new String[list.size()][];

        for (int i = 0; i < list.size(); i++) {
            values[i] = new String[title.length];
            for (int j = 0; j < columns.length; j++) {
                Object t = list.get(i);
                Class cls = t.getClass();
                String column = columns[j];

                if (column.length() == 0) {
                    throw new Exception("字段不能为空");
                }
                String getMethod = "get" + column.substring(0, 1).toUpperCase() + column.substring(1, column.length());
                Method method = cls.getMethod(getMethod);
                Object obj = method.invoke(t);
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
                if ("cardNature".equals(column)) {
                    values[i][j] = "1".equals(values[i][j]) ? "权益卡" : "普通卡";
                }
                if ("tradeType".equals(column)) {
                    if (values[i][j] != null) {
                        switch (values[i][j]) {
                            case "1":
                                values[i][j] = "钱包";
                                break;
                            case "2":
                                values[i][j] = "微信";
                                break;
                            case "3":
                                values[i][j] = "支付宝";
                                break;
                            default:
                                values[i][j] = "提现";
                                break;
                        }
                    }
                }
                if ("tradeStatus".equals(column)) {
                    if (values[i][j] != null) {
                        switch (values[i][j]) {
                            case "1":
                                values[i][j] = "审核中";
                                break;
                            case "2":
                                values[i][j] = "审核失败";
                                break;
                            case "3":
                                values[i][j] = "成功";
                                break;
                            case "6":
                                values[i][j] = "充值失败";
                                break;
                        }
                    }
                }
                if ("payType".equals(column)) {
                    if (values[i][j] != null) {
                        switch (values[i][j]) {
                            case "1":
                                values[i][j] = "提现";
                                break;
                            case "2":
                                values[i][j] = "会员卡购买";
                                break;
                            case "3":
                                values[i][j] = "商城购买";
                                break;
                            case "4":
                                values[i][j] = "私教课购买";
                                break;
                            case "5":
                                values[i][j] = "团体课购买";
                                break;
                            case "6":
                                values[i][j] = "充值";
                                break;
                            case "7":
                                values[i][j] = "广告消费";
                                break;
                            case "8":
                                values[i][j] = "积分兑换";
                                break;
                            case "9":
                                values[i][j] = "商城退款";
                                break;
                            case "10":
                                values[i][j] = "其他";
                                break;
                            case "11":
                                values[i][j] = "提现失败退款";
                                break;
                            case "13":
                                values[i][j] = "会员卡自动续费";
                                break;
                        }
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

}
