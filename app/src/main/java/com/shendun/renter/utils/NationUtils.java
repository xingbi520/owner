package com.shendun.renter.utils;

import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

public class NationUtils {

    private static String[] NATIION_DATAS = new String[]{"汉", "蒙古", "回", "藏", "维吾尔", "苗", "彝", "壮", "布依", "朝鲜", "满", "侗", "瑶", "白", "土家", "哈尼", "哈萨克", "傣", "黎", "傈僳", "佤", "畲", "高山", "拉祜", "水", "东乡", "纳西", "景颇", "柯尔克孜", "土", "达斡尔", "仫佬", "羌", "布朗", "撒拉", "毛南", "仡佬", "锡伯", "阿昌", "普米", "塔吉克", "怒", "乌孜别克", "俄罗斯", "鄂温克", "德昂", "保安", "裕固", "京", "塔塔尔", "独龙", "鄂伦春", "赫哲", "门巴", "珞巴", "基诺"};

    /**
     * 获得民族list
     *
     * @return List<String>
     */
    public static List<String> getNationList() {
        return Arrays.asList(NATIION_DATAS);
    }

    /**
     * 获得民族
     *
     * @param index
     * @return String
     */
    public static String getNationCn(String index) {
        if(null == index || TextUtils.isEmpty(index)){
            return "";
        }

        String nation = "";
        int nIndex = Integer.valueOf(index);
        if(nIndex > 0){
            nation = NATIION_DATAS[nIndex - 1];
        }

        return nation;
    }

    /**
     * 获得民族
     *
     * @param index
     * @return String
     */
    public static int getNationIndex(String index) {
        if(null == index || TextUtils.isEmpty(index)){
            return 0;
        }

        int nation = 0;
        int nIndex = Integer.valueOf(index);
        if(nIndex > 0){
            nation = nIndex - 1;
        }

        return nation;
    }
}
