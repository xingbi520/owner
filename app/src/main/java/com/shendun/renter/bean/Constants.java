package com.shendun.renter.bean;

/**
 * 部分常量类
 */
public class Constants {
    public static final String INTENT_ACTION_USER_CHANGE = "USER_CHANGE";
    public static final String INTENT_ACTION_LOGOUT = "LOGOUT";
    public static final String WEICHAT_APPID = "wxa8213dc827399101";
    public static final String WEICHAT_SECRET = "5c716417ce72ff69d8cf0c43572c9284";

    public static final String RESPONSE_SUCCEED = "0";

    public static final String CACHE_NAME_USER_INFO = "UserInfo";

    //订单状态。0空闲，1待入住，2已入住，3已退房，4取消，9全部
    public static final String ROOM_ORDER_FREE= "0";
    public static final String ROOM_ORDER_READY= "1";
    public static final String ROOM_ORDER_CHECK_IN= "2";
    public static final String ROOM_ORDER_CHECK_OUT= "3";
    public static final String ROOM_ORDER_CANCEL= "4";
    public static final String ROOM_ORDER_ALL= "9";
    public static final String ROOM_ORDER_DOMAIN = "9";

    public static final int ROOM_ORDER_PAGE_SIZE= 15;

    public static final String ACTIVITY_OBJECT_ROOM = "room";
    public static final String ACTIVITY_OBJECT_ROOM_ORDER = "room_order";
    public static final String FRAGMENT_ARGUMENTS = "fragment_arguments";

    public static final String ORDER_STATUS_CHECKOUT = "3";
    public static final String ORDER_STATUS_CANCEL = "4";

    public static final String ORDER_ACTION_CHECKOUT = "3"; //3退房
    public static final String ORDER_ACTION_CANCEL = "4";   //4取消

    public static final String LOCK_REGISTER_CANCEL = "1"; //表示门锁的登记和退房
    public static final String LOCK_GET_PASSWORD = "2";    //获取门锁密码
}
