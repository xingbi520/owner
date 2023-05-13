package com.shendun.renter.utils;

import android.content.Context;
import android.text.TextUtils;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathUtil {

    //默认除法运算精度
    private static final int DEF_DIV_SCALE = 2;

    public static boolean isMobileNO(String mobiles) {
        /*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
        联通：130、131、132、152、155、156、185、186
        电信：133、153、180、189、（1349卫通）
        总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
        ------------------------------------------------
        13(老)号段：130、131、132、133、134、135、136、137、138、139
        14(新)号段：145、147
        15(新)号段：150、151、152、153、154、155、156、157、158、159
        17(新)号段：170、171、173、175、176、177、178
        18(3G)号段：180、181、182、183、184、185、186、187、188、189
        */
        String telRegex =
            "[1][3456789]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(telRegex);
        }
    }

    /**
     * 判断是否满足条件：至少是要求的数字、字母、符号的其中2种
     */
    public static boolean matchNumberAndLetterAndCharacterOf2(Context context, String str) {
        //        String limitEx="/(?!^(\\d+|[a-zA-Z]+|[~!@#$%^&*?]+)$)^[\\w~!@#$%^&*?]{6,20}$/";
        String limitEx =
            "^(?![0-9]+$)(?![a-zA-Z]+$)(?![`~!@#$%^&*()+=|{}':;',\\[\\]<>?~！@#￥%……&*（）——+|{}【】‘；：’。，、？]+$)[a-zA-Z0-9`~!@#$%^&*()+=|{}':;',\\[\\]<>?~！@#￥%……&*（）——+|{}【】‘；：’。，、？]{6,20}";
        Pattern pattern = Pattern.compile(limitEx);
        Matcher m = pattern.matcher(str);
        return m.find();
    }

    /**
     * 判断是否满足条件：至少是要求的数字、字母、符号的其中3种
     */
    public static boolean matchNumberAndLetterAndCharacterOf3(String str) {
        String limitEx =
            "^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_]+$)(?![a-z0-9]+$)(?![a-z\\W_]+$)(?![0-9\\W_]+$)[a-zA-Z0-9\\W_]{8,20}$";
        Pattern pattern = Pattern.compile(limitEx);
        Matcher m = pattern.matcher(str);
        return m.find();
    }

    /**
     * 提供精确的比较运算。
     *
     * @param v1
     * @param v2
     * @return v1 小于、等于或大于 v2 时，返回 -1、0 或 1
     */
    public static int compareTo(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.compareTo(b2);
    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(double v1, double v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * kb转换成mb, gb, tb。
     *
     * @param kb
     * @return 单位转化结果
     */
    public static String kbConvert(long kb){
        //定义TB的计算常量
        final int TB = 1024 * 1024 * 1024;
        //定义GB的计算常量
        final int GB = 1024 * 1024;
        //定义MB的计算常量
        final int MB = 1024;
        //格式化小数
        DecimalFormat format = new DecimalFormat("#####0.0");
        if (kb / TB >= 1){
            return format.format(kb / (float)TB) + "T";
        } else if (kb / GB >= 1){
            return format.format(kb / (float)GB) + "G";
        } else if (kb / MB >= 1){
            return format.format(kb / (float)MB) + "M";
        } else {
            return kb + "K";
        }
    }
}
