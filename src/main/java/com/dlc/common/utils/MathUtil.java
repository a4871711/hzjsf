package com.dlc.common.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-11-30 21:05
 */
public class MathUtil {

    private MathUtil() {}

    private static DecimalFormat df = new DecimalFormat("0.00");
    private static DecimalFormat df2 = new DecimalFormat("0.0");


    public static BigDecimal fromIntToBigDecimal(Integer x){
        String format = df.format((float)  x/ 1000);
        double v = Double.parseDouble(format);
        BigDecimal dis = BigDecimal.valueOf(v);
        return dis;
    }

    //保留一位小数点
    public static String  fromStringToOnePoint(String x){
        double d = Double.valueOf(x);
        String format = df2.format(d);
        return format;
    }

    public static BigDecimal  fromStringToTwoPoint(String x){
        double d = Double.valueOf(x);
        String format = df.format(d);
        double v = Double.parseDouble(format);
        BigDecimal dis = BigDecimal.valueOf(v);
        return dis;
    }

    public static BigDecimal objectConvertBigDecimal(Object value) {
        BigDecimal ret = null;
        if (value != null) {
            if (value instanceof BigDecimal) {
                ret = (BigDecimal) value;
            } else if (value instanceof String) {
                ret = new BigDecimal((String) value);
            } else if (value instanceof BigInteger) {
                ret = new BigDecimal((BigInteger) value);
            } else if (value instanceof Number) {
                ret = new BigDecimal(((Number) value).doubleValue());
            } else {
                throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass()
                        + " into a BigDecimal.");
            }
        }
        return ret;

    }
}
