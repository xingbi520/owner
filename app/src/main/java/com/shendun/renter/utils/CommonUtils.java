package com.shendun.renter.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.util.Base64;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class CommonUtils {

    private static long lastClickTime;

    /**
     * 判断手机号
     *
     * @param mobiles
     * @return
     */
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
        String telRegex = "[1][3456789]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    /**
     * 过滤空格
     *
     * @param message
     * @return
     */
    public static String filterBlankSpace(String message) {
        String result = "";
        result = message.replaceAll(" ", "").trim();
        return result;
    }

    /**
     * 判断是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判空"",null,0
     */
    public static boolean isEmpty(String string) {
        return !(string != "" && !TextUtils.isEmpty(string));
    }

    /**
     * 小数点
     *
     * @param v
     * @param scale
     * @return
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        } else {
            BigDecimal bgNum1 = new BigDecimal(Double.toString(v));
            BigDecimal bgNum2 = new BigDecimal("1");
            return bgNum1.divide(bgNum2, scale, 4).doubleValue();
        }
    }

    /**
     * 防止二次点击
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 防止二次点击
     *
     * @return
     */
    public static boolean isFastDoubleClick(long interval) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < interval) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 获取屏幕宽度，高度
     *
     * @param context
     * @return
     */
    public static int[] getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
    }

    /**
     * 要判断是否包含特殊字符的目标字符串
     *
     * @param context
     * @param str
     * @return
     */
    public static boolean compileExCharWifiname(Context context, String str) {
        String limitEx = "[`~!@#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern pattern = Pattern.compile(limitEx);
        Matcher m = pattern.matcher(str);
        return m.find();
    }

    /**
     * 判断是否满足条件：至少是要求的数字、字母、符号的其中2种
     * @return
     */
    public static boolean matchNumberAndLetterAndCharacterOf2(Context context, String str){
        //        String limitEx="/(?!^(\\d+|[a-zA-Z]+|[~!@#$%^&*?]+)$)^[\\w~!@#$%^&*?]{6,20}$/";
        String limitEx = "^(?![0-9]+$)(?![a-zA-Z]+$)(?![`~!@#$%^&*()+=|{}':;',\\[\\]<>?~！@#￥%……&*（）——+|{}【】‘；：’。，、？]+$)[a-zA-Z0-9`~!@#$%^&*()+=|{}':;',\\[\\]<>?~！@#￥%……&*（）——+|{}【】‘；：’。，、？]{6,20}";
        Pattern pattern = Pattern.compile(limitEx);
        Matcher m = pattern.matcher(str);
        return m.find();
    }

    /**
     * 禁止EditText输入空格
     *
     * @param editText
     * @param isDigits 是否有限制输入
     */
    public static void setEditTextInhibitInputSpace(EditText editText, boolean isDigits) {
        setEditTextInhibitInputSpace(editText, -1, isDigits);
    }

    /**
     * 允许EditText输入空格
     *
     * @param editText
     * @param isDigits 是否有限制输入
     */
    public static void setEditTextHaveInputSpace(EditText editText, boolean isDigits) {
        setEditTextInhibitInputSpace(editText, -2, isDigits);
    }

    /**
     * @param editText
     * @param maxlength >0 限制字符长度;-1 禁止空格; -2;允许空格
     * @param isDigits
     */

    public static void setEditTextInhibitInputSpace(EditText editText, int maxlength, boolean isDigits) {
        //中文英文特殊符号数字(除去表情)
        InputFilter inputFilter = new InputFilter() {

            Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\u4E00-\\u9FA5`~!@#$%^&*()+= |{}':;',\\[\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、_.\\-？\\\\]");

            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                Matcher matcher = pattern.matcher(charSequence);
                if (!matcher.find()) {
                    return null;
                } else {
                    return "";
                }

            }
        };

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ")) {
                    return "";
                } else {
                    return null;
                }
            }
        };
        if (maxlength > 0) {

            if (isDigits) {
                editText.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(maxlength)});
            } else {
                editText.setFilters(new InputFilter[]{inputFilter, filter, new InputFilter.LengthFilter(maxlength)});
            }

        } else {
            if (maxlength == -1) {

                if (isDigits) {
                    editText.setFilters(new InputFilter[]{filter});
                } else {
                    editText.setFilters(new InputFilter[]{inputFilter, filter});
                }

            } else if (maxlength == -2) {
                if (!isDigits) editText.setFilters(new InputFilter[]{inputFilter});
            }
        }
    }

    /**
     * 禁止EditText输入特殊字符
     *
     * @param editText
     */
    public static void setEditTextInhibitInputSpeChat(EditText editText, int limitCount) {

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String speChat = "[:/]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());
                if (matcher.find()) return "";
                else return null;
            }
        };

        InputFilter filter1 = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ")) {
                    return "";
                } else {
                    return null;
                }
            }
        };

        InputFilter filter2 = new InputFilter.LengthFilter(limitCount);

        editText.setFilters(new InputFilter[]{filter, filter1, filter2});
    }

    /**
     * 禁止EditText输入中文
     *
     * @param editText
     */
    public static void setEditTextInhibitInputSpeUtf8(EditText editText) {

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (isUtf8String(source.toString())) return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    //    通过正则表达式来判断。下面的例子只允许显示字母、数字和汉字。
    public static void setEditTextStringFilter(EditText editText) throws PatternSyntaxException {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                // 只允许字母、数字和汉字
                String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(source.toString());
                if (m.find()) return "";
                else return null;
            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    //utf-8 字符判断
    public static boolean isUtf8String(String str) {
        if (str == null) {
            return false;
        }
        byte[] array = null;
        try {
            array = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return !((array != null) && (array.length == str.length()));
    }

    /**
     * get App versionCode
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int versionCode = 1;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo;
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    ////获取大版本号
    //public static String getBigVersionName() {
    //    Context context = BaseApplication.getContext();
    //    PackageManager packageManager = context.getPackageManager();
    //    PackageInfo packageInfo;
    //    String versionName = "";
    //    try {
    //        packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
    //        versionName = packageInfo.versionName;
    //    } catch (PackageManager.NameNotFoundException e) {
    //        e.printStackTrace();
    //    }
    //
    //    //只显示前两位
    //    if (!TextUtils.isEmpty(versionName)) {
    //        versionName = versionName.substring(0, versionName.indexOf(".", versionName.indexOf(".") + 1));
    //    }
    //
    //    return versionName;
    //}

    //获取详细版本号
    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取应用程序版本名称信息
     *
     * @return 当前应用的版本名称
     */
    public static String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo =
                packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void limitInputDigist(EditText editText, final int inputType, final String limitDigits) {
        editText.setKeyListener(new DigitsKeyListener() {
            @Override
            public int getInputType() {
                return inputType;
            }

            @Override
            protected char[] getAcceptedChars() {
                return limitDigits.toCharArray();
            }
        });
    }

    /**
     * 通过反射{@link TabLayout}设置下划线(Indicator)宽度，字多宽线就多宽，参阅 https://blog.csdn.net/waplyj/article/details/81068127
     */
    public static void setTabLayoutIndicator(final TabLayout tabLayout) {
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Field field = tabLayout.getClass().getDeclaredField("mTabStrip");
                    field.setAccessible(true);
                    //拿到tabLayout的mTabStrip属性
                    LinearLayout tabStrip = (LinearLayout) field.get(tabLayout);
                    for (int i = 0, count = tabStrip.getChildCount(); i < count; i++) {
                        View tabView = tabStrip.getChildAt(i);
                        //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);
                        TextView textView = (TextView) mTextViewField.get(tabView);
                        tabView.setPadding(0, 0, 0, 0);
                        //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                        int textWidth = 0;
                        textWidth = textView.getWidth();
                        if (textWidth == 0) {
                            textView.measure(0, 0);
                            textWidth = textView.getMeasuredWidth();
                        }
                        int tabWidth = 0;
                        tabWidth = tabView.getWidth();
                        if (tabWidth == 0) {
                            tabView.measure(0, 0);
                            tabWidth = tabView.getMeasuredWidth();
                        }
                        LinearLayout.LayoutParams tabViewParams = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        int margin = (tabWidth - textWidth) / 2;
                        //Log2Utils.d("textWidth=" + textWidth + ", tabWidth=" + tabWidth + ", margin=" + margin);
                        tabViewParams.leftMargin = margin;
                        tabViewParams.rightMargin = margin;
                        tabView.setLayoutParams(tabViewParams);
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static String timePalse(String time) {
        if (time.length() != 14) return time;
        StringBuilder palseTime = new StringBuilder();
        palseTime.append(time.substring(0, 4));
        palseTime.append("年");
        palseTime.append(time.substring(4, 6));
        palseTime.append("月");
        palseTime.append(time.substring(6, 8));
        palseTime.append("日 ");
        palseTime.append(time.substring(8, 10));
        palseTime.append(":");
        palseTime.append(time.substring(10, 12));
        palseTime.append(":");
        palseTime.append(time.substring(12));
        return palseTime.toString();
    }

    /**
     * 判断当前软键盘是否打开
     */
    public static boolean isSoftInputShow(Activity activity) {
        //虚拟键盘隐藏 判断view是否为空
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            //隐藏虚拟键盘
            InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
            return inputmanger.isActive() && activity.getWindow().getCurrentFocus() != null;
        }
        return false;
    }

    /***
     * 获取url 指定name的value;
     * @param url
     * @param name
     * @return
     */
    public static String getValueByName(String url, String name) {
        String result = "";
        int index = url.indexOf("?");
        String temp = url.substring(index + 1);
        String[] keyValue = temp.split("&");
        for (String str : keyValue) {
            if (str.contains(name)) {
                result = str.replace(name + "=", "");
                break;
            }
        }
        return result;
    }
    /**
     * 全屏显示
     */
    public static void setFullScreen(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(params);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); // Activity全屏显示，且状态栏被覆盖掉
    }

    /**
     * 退出全屏
     */
    public static void exitFullScreen(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(params);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN); // Activity全屏显示，但是状态栏不会被覆盖掉，而是正常显示，只是Activity顶端布局会被覆盖住
    }

    /**
     * 图片转base64编码
     *
     * @param path 图片的地址
     * @return
     */
    public static String fileToBase64(String path) {
        String result = null;
        File file = new File(path);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputStream.read(buffer);
            inputStream.close();
            result = Base64.encodeToString(buffer, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 小于10的整数，前面补齐0. 比如1变成"01"
     *
     * @param digit
     * @return
     */
    public static String prefixZero(int digit) {
        if(digit < 1){
            return "00";
        } else if(digit < 10){
            return "0" + String.valueOf(digit);
        } else {
            return String.valueOf(digit);
        }
    }

    /**
     * 设置EditText是否可编辑
     * @param editText : 设置的EditText的对象
     * @param mode : true:可编辑 false:不可编辑
     * @return
     */
    public static void setEditTextEnable(EditText editText, boolean mode){
        if(null == editText) return;

        editText.setFocusable(mode);
        editText.setFocusableInTouchMode(mode);
        editText.setLongClickable(mode);//设置是否能够长按
        editText.setInputType(mode ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_NULL);
    }
}
