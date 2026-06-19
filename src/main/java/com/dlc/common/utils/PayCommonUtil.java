package com.dlc.common.utils;

import java.text.SimpleDateFormat;
import java.util.*;

public class PayCommonUtil {

    /**
     * 是否签名正确,规则是:按参数名称a-z排序,遇到空值的参数不参加签名
     * @param characterEncoding
     * @param packageParams
     * @param API_KEY
     * @return
     */
    public static boolean isTenpaySign(String characterEncoding, SortedMap<Object, Object> packageParams, String API_KEY) {
        StringBuffer sb = new StringBuffer();  
        Set es = packageParams.entrySet();  
        Iterator it = es.iterator();  
        while(it.hasNext()) {  
            Map.Entry entry = (Map.Entry)it.next();  
            String k = (String)entry.getKey();  
            String v = (String)entry.getValue();  
            if(!"sign".equals(k) && null != v && !"".equals(v)) {  
                sb.append(k + "=" + v + "&");  
            }  
        }  
        sb.append("key=" + API_KEY);  
        //算出摘要  
        String mySign = MD5Utils.MD5Encode(sb.toString(), characterEncoding, false);
        String tenPaySign = ((String)packageParams.get("sign")).toLowerCase();
        return tenPaySign.equals(mySign);
    }

    /**
     * sign签名
     * @param characterEncoding
     * @param packageParams
     * @param API_KEY
     * @return
     */
	public static String createSign(String characterEncoding, SortedMap<Object, Object> packageParams, String API_KEY) {
        StringBuffer sb = new StringBuffer();  
        Set es = packageParams.entrySet();  
        Iterator it = es.iterator();  
        while (it.hasNext()) {  
            Map.Entry entry = (Map.Entry) it.next();  
            String k = (String) entry.getKey();  
            String v = (String) entry.getValue();  
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {  
                sb.append(k + "=" + v + "&");  
            }  
        }  
        sb.append("key=" + API_KEY);
        String sign = MD5Utils.MD5Encode(sb.toString(), characterEncoding, true);
        return sign;  
    }

    /**
     * sign签名 -字符类型的
     * @param characterEncoding
     * @param parameters
     * @param key
     * @return
     * @throws Exception
     */
    public static String createSign2(String characterEncoding, SortedMap<String,String> parameters, String key) throws Exception {

        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"paySign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" +key);
        String sign = MD5Utils.MD5Encode(sb.toString(), characterEncoding, true);
        return sign;
    }

    /**
     * 将请求参数转换为xml格式的string
     * @param parameters
     * @return
     */
    public static String getRequestXml(SortedMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();  
        sb.append("<xml>");  
        Set es = parameters.entrySet();  
        Iterator it = es.iterator();  
        while (it.hasNext()) {  
            Map.Entry entry = (Map.Entry) it.next();  
            String k = (String) entry.getKey();  
            String v = (String) entry.getValue();  
            if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)) {  
                sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");  
            } else {  
                sb.append("<" + k + ">" + v + "</" + k + ">");  
            }  
        }  
        sb.append("</xml>");  
        return sb.toString();  
    }

    /**
     * 取出一个指定长度大小的随机正整数.
     * @param length
     * @return
     */
    public static int buildRandom(int length) {  
        int num = 1;  
        double random = Math.random();  
        if (random < 0.1) {  
            random = random + 0.1;  
        }  
        for (int i = 0; i < length; i++) {  
            num = num * 10;  
        }  
        return (int) ((random * num));  
    }  
  
    /** 
     * 获取当前时间
     * @return String
     */  
    public static String getCurrTime() {  
        Date now = new Date();  
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");  
        String s = outFormat.format(now);  
        return s;  
    }

    public static String getTimestamp() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}
}
