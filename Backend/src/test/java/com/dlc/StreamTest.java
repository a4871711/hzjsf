package com.dlc;

import com.alibaba.fastjson.JSONObject;
import com.dlc.common.utils.Message;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.stream.Stream;

public class StreamTest {
    @Test
    public void stream1() {
        double tempNaN = 0.0 / 0.0;

        System.out.println(Double.POSITIVE_INFINITY==Double.NEGATIVE_INFINITY);
        System.out.println(Double.NaN==Double.NaN);
        System.out.println(Double.isNaN(tempNaN));
   /*     Stream.iterate(1, i -> i + 1).limit(10).forEach(System.out::println);
        System.out.println(super.hashCode());
        Class clazz = this.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method m :
                methods) {
            System.out.println(m.getName().startsWith("stream1"));
        }*/
    }
    @Test
    public void test(){
        /*Date date=new Date();
        Double temp=date.getHours()*60.0*60.0;
        System.out.println(temp);*/
        BigDecimal a = new BigDecimal("0.00");
        if(a.compareTo(new BigDecimal("0")) ==1){
            System.out.println("is 0  come in");
        }

        Message temp  = new Message();
        temp.setUserId(1l);
        temp.setMoney(new BigDecimal("10"));
        temp.setLeastMoney(new BigDecimal("12"));
        String str  = JSONObject.toJSONString(temp);
        JSONObject json = JSONObject.parseObject(str);
        System.out.println("str====="+str);
        temp = JSONObject.toJavaObject(json,Message.class);
        System.out.println("temp----"+temp);
    }
}