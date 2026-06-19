package com.dlc.common.utils;

import java.sql.Time;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * 日期处理
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年12月21日 下午12:53:33
 */
public class DateUtils {
	/** 时间格式(yyyy-MM-dd) */
	public final static String DATE_PATTERN = "yyyy-MM-dd";
	public final static String DATE_PATTERN2 = "yyyyMMdd";
	/** 时间格式(yyyy-MM-dd HH:mm:ss) */
	public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public final static String DATE_TIME_CST = "EEE MMM dd HH:mm:ss zzz yyyy";

    public static boolean sameDate(Date d1, Date d2) {
        LocalDate localDate1 = ZonedDateTime.ofInstant(d1.toInstant(), ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = ZonedDateTime.ofInstant(d2.toInstant(), ZoneId.systemDefault()).toLocalDate();
        return localDate1.isEqual(localDate2);
    }

    public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }

    public static String formatYYYMMDD(Date date) {
        return format(date, DATE_PATTERN2);
    }

    public static String formatFull(Date date) {
        return format(date, DATE_TIME_PATTERN);
    }

    public static String format(Date date, String pattern) {
        if(date != null){
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }

    /***
     * 返回时间格式yyyy-MM-dd
     * @return
     */
    public static Date getDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(8);
        Date currentTime_2 = formatter.parse(dateString, pos);
        return currentTime_2;
    }

    /***
     * 返回时间格式yyyy-MM-dd
     * @return
     */
    public static Date getNowDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(8);
        Date currentTime_2 = formatter.parse(dateString, pos);
        return currentTime_2;
    }

    public static String format(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
        try {
            return format(formatter.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取某天的零时零分零秒
     * @return
     */
    public static Date getCurrentDate(Integer currentDaytInt){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, currentDaytInt);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取时间差(天数差，小时差，分钟差)
     */
    public static int getHours(Date fromDate,Date toDate){
        long from = fromDate.getTime();
        long to = toDate.getTime();
        int hours = (int) ((to - from)/(1000 * 60 * 60));
        return hours;
    }

    /**
     * 获取时间差(天数差，小时差，分钟差)
     */
    public static int getHours(Long from,Long to){
        /*long from = fromDate.getTime();
        long to = toDate.getTime();*/
        int hours = (int) ((to - from)/(1000 * 60 * 60));
        return hours;
    }


    /**
     * 获取时间差(天数差，小时差，分钟差)
     */
    public static int getMin(Date fromDate,Date toDate){
        long from = fromDate.getTime();
        long to = toDate.getTime();
        int min = (int) ((to - from)/(1000 * 60));
        return min;
    }


    /**
     * 获取时间差(天数差，小时差，分钟差)
     */
    public static int getMin(Long from,Long to){
        /*long from = fromDate.getTime();
        long to = toDate.getTime();*/
        int min = (int) ((to - from)/(1000 * 60));
        return min;
    }

    public static int getYear(Date fromDate,Date toDate){
        Calendar  from  =  Calendar.getInstance();
        from.setTime(fromDate);
        Calendar  to  =  Calendar.getInstance();
        to.setTime(toDate);

        int fromYear = from.get(Calendar.YEAR);
        int fromMonth = from.get(Calendar.MONTH);
        int fromDay = from.get(Calendar.DAY_OF_MONTH);

        int toYear = to.get(Calendar.YEAR);
        int toMonth = to.get(Calendar.MONTH);
        int toDay = to.get(Calendar.DAY_OF_MONTH);
        int year = toYear  -  fromYear;
        int month = toMonth  - fromMonth;
        int day = toDay  - fromDay;
        return year;
    }

    /**
     * 增加日期天数()
     */
    public static Date addDate(Date startTime,int count){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startTime);
        calendar.add(calendar.DATE,count); //把日期往后增加一天,整数  往后推,负数往前移动
        Date toTime =calendar.getTime(); //这个时间就是日期往后推一天的结果
        return toTime;
    }
    /**
     * 增加日期分钟()
     */
    public static Date addMin(Date startTime, int count){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startTime);
        calendar.add(calendar.MINUTE,count); //把日期往后增加一天,整数  往后推,负数往前移动
        Date toTime =calendar.getTime(); //这个时间就是日期往后推一天的结果
        return toTime;
    }

    //String类型的时间转为Date
    static public  Date toDate(String time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date= null;
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    //String类型的时间转为Date
    static public  Date toDateFull(String time){
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_PATTERN);
        Date date= null;
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    //String类型的时间转为Date
    static public  Date toDateCST(String time){
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_CST);
        formatter.setTimeZone(TimeZone.getTimeZone("CST"));
        Date date= null;
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    
    //计算天数差
    public static long getDayDiff(String smdate,String bdate) {
        long between_days = 0L;
        try {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(smdate));
            long time1 = cal.getTimeInMillis();
            cal.setTime(sdf.parse(bdate));
            long time2 = cal.getTimeInMillis();
            between_days=(time2-time1)/(1000*3600*24);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return between_days;
    }
    //计算时分秒差   2019-03-25 20:00~21:00
    public static String getTimeDiff(String dateStr) {
        int len = dateStr.length();
        if(len < 22){return "00:00";}
        String hms = "00:00";
        try {
            String[] spDate1 = dateStr.split(" ");
            String[] spDate2 = spDate1[1].split("~");
            String sdStr = spDate1[0] + " " + spDate2[0];
            String edStr = spDate1[0] + " " + spDate2[1];
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date sd = sdf.parse(sdStr);
            Date ed = sdf.parse(edStr);
            int min = Math.abs(getMin(sd, ed));
            //转换成HH:mm:ss
            hms = min / 60 + ":" + min % 60;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return hms;
    }

}
