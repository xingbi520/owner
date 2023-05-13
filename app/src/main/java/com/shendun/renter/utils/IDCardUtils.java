package com.shendun.renter.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 身份证工具类
 */
public class IDCardUtils {

   /**
    * 通过身份证号码获取出生日期、性别、年龄
    * @param certificateNo
    * @return 返回的出生日期格式：1990-01-01   性别格式：F-女，M-男
    */
   public static Map<String, String> getBirAgeSex(String certificateNo) {
      String birthday = "";
      String age = "";
      String sexCode = "";

      int year = Calendar.getInstance().get(Calendar.YEAR);
      char[] number = certificateNo.toCharArray();
      boolean flag = true;

      if (number.length == 15) {
         for (int x = 0; x < number.length; x++) {
            if (!flag) {
               return new HashMap<String, String>();
            }
            flag = Character.isDigit(number[x]);
         }
      } else if (number.length == 18) {
         for (int x = 0; x < number.length - 1; x++) {
            if (!flag) {
               return new HashMap<String, String>();
            }
            flag = Character.isDigit(number[x]);
         }
      }

      if (flag && certificateNo.length() == 15) {
         birthday = "19" + certificateNo.substring(6, 8) + "-"
                 + certificateNo.substring(8, 10) + "-"
                 + certificateNo.substring(10, 12);
         sexCode = Integer.parseInt(certificateNo.substring(certificateNo.length() - 3, certificateNo.length())) % 2 == 0 ? "F" : "M";
         age = (year - Integer.parseInt("19" + certificateNo.substring(6, 8))) + "";
      } else if (flag && certificateNo.length() == 18) {
         birthday = certificateNo.substring(6, 10) + "-"
                 + certificateNo.substring(10, 12) + "-"
                 + certificateNo.substring(12, 14);
         sexCode = Integer.parseInt(certificateNo.substring(certificateNo.length() - 4, certificateNo.length() - 1)) % 2 == 0 ? "F" : "M";
         age = (year - Integer.parseInt(certificateNo.substring(6, 10))) + "";
      }

      Map<String, String> map = new HashMap<String, String>();
      map.put("birthday", birthday);
      map.put("age", age);
      map.put("sexCode", sexCode);
      return map;
   }

   /**
    * 获得所在地
    * @param context 上下文
    * @param idCard 身份证号码
    * @return 整型，例如：18
    */
   public static String getAddressByCardId(Context context, String idCard){
      if(null == context || idCard.equals("") || idCard.length() < 7){
         return "";
      }

      String sAddress = "";
      try {
         String sCardNum = idCard.substring(0,6);
         List<String> strList = getAssetsString(context, "idCardAddressCodeComparison.txt");
         for(String element : strList){
            if (!element.equals("")) {
               String[] values = element.split(" ");
               if(values.length == 2 && values[0].equals(sCardNum)){
                  sAddress = values[1];
                  break;
               }
            }
         }
      }catch (Exception e){
         e.printStackTrace();
      }

      return sAddress;
   }

   /**
    * 获取assets文件的数据
    * @param context, fileName
    * @return
    */
   private static List<String> getAssetsString(Context context, String fileName) {
      List<String> stringList = new ArrayList<>();
      try {
         //获取assets资源管理器
         AssetManager assetManager = context.getAssets();
         //通过管理器打开文件并读取
         BufferedReader bf =
                 new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
         String line;
         while ((line = bf.readLine()) != null) {
            stringList.add(line);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      return stringList;
   }
}