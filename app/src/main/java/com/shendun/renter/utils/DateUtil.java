package com.shendun.renter.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * 根据style转成时间字符串
     */
    public static String DateToTime(long date, String style) {
        SimpleDateFormat formatter = new SimpleDateFormat(style);
        return formatter.format(date);
    }

    /**
     * 判断时间是否小于24小时
     */
    public static boolean judgmentDate(String date1, String date2){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
            Date start = sdf.parse(date1);
            Date end = sdf.parse(date2);
            long cha = end.getTime() - start.getTime();
            if (cha < 0) {
                return false;
            }

            double result = cha * 1.0 / (1000 * 60 * 60);
            if (result <= 24) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }

    /**
     * 日期比较
     *
     * @param dataBefore
     * @param dataAfter
     * @return -1(dataBefore小于dataAfter); -1(dataBefore等于dataAfter); 1(dataBefore大于dataAfter);
     */
    public static int compareDateDay(String dataBefore, String dataAfter) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(dataBefore);
            Date dt2 = df.parse(dataAfter);
            if (dt1.getTime() > dt2.getTime()) {
                //dt1在dt2前
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                //dt1在dt2后
                return -1;
            } else {
                //dt1和dt2相等
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * 日期偏移
     */
    public static Date dateOffset(String dateTime,int Num) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date nowDate = null;
        try {
            nowDate = df.parse(dateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果需要向后计算日期 -改为+
        Date newDate2 = new Date(nowDate.getTime() + (long)Num * 24 * 60 * 60 * 1000);
        return newDate2;
    }

    /**
     * 判断日期与当前日期相差几年
     * @param old_date
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int yearBetween(String old_date) {
        int li_year=0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar bef = Calendar.getInstance();
            bef.setTime(sdf.parse(old_date));
            String curr_date = sdf.format(new Date());
            Period period = Period.between(LocalDate.parse(old_date), LocalDate.parse(curr_date));
            li_year = period.getYears();
            return li_year;
        } catch (ParseException e) {
            return 20;
        }
    }

    /**
     * 判断是否是成年人
     * @param date
     * @return 1:成年人；0：未成年人；-1：解析异常
     */
    public static int checkAdult(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar current = Calendar.getInstance();
            Calendar birthDay = Calendar.getInstance();
            birthDay.setTime(sdf.parse(date));
            Integer year = current.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
            if (year > 18) {
                return 1;
            } else if (year < 18) {
                return 0;
            }
            // 如果年相等，就比较月份
            Integer month = current.get(Calendar.MONTH) - birthDay.get(Calendar.MONTH);
            if (month > 0) {
                return 1;
            } else if (month < 0) {
                return 0;
            }
            // 如果月也相等，就比较天
            Integer day = current.get(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH);
            if(day >= 0){
                return 1;
            } else {
                return 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        } catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }
}
