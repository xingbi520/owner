package com.shendun.renter.repository;

import io.reactivex.rxjava3.core.Observable;

import com.shendun.renter.repository.bean.AppUpdateResponse;
import com.shendun.renter.repository.bean.DomainResponse;
import com.shendun.renter.repository.bean.FaceAuthResponse;
import com.shendun.renter.repository.bean.GetLockPwdResponse;
import com.shendun.renter.repository.bean.GetPlatFromResponse;
import com.shendun.renter.repository.bean.LoginResponse;
import com.shendun.renter.repository.bean.OrderDetailResponse;
import com.shendun.renter.repository.bean.OrderResponse;
import com.shendun.renter.repository.bean.GetPwdResponse;
import com.shendun.renter.repository.bean.ResponseBean;
import com.shendun.renter.repository.bean.RoomResponse;
import com.shendun.renter.repository.bean.RoomSourceResponse;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Author:daiminzhi
 * Time: 11:47 AM
 */
public interface NetService {
    //获得验证码
    @POST("/API/wyf/{key}")
    Observable<GetPwdResponse> getPwd(@Path("key") String key, @Body RequestBody requestBody);

    //登录接口
    @POST("/API/wyf/{key}")
    Observable<LoginResponse> appLogin(@Path("key") String key, @Body RequestBody requestBody);

    //房间列表接口
    @POST("/API/wyf/{key}")
    Observable<RoomResponse> getRooms(@Path("key") String key, @Body RequestBody requestBody);

    //房源核查
    @POST("/API/wyf/{key}")
    Observable<RoomSourceResponse> getRoomSource(@Path("key") String key, @Body RequestBody requestBody);

    //增加租客接口
    @POST("/API/wyf/{key}")
    Observable<ResponseBean> getAddZks(@Path("key") String key, @Body RequestBody requestBody);

    //增加租客V2接口
    @POST("/API/wyf/v2/{key}")
    Observable<ResponseBean> getAddV2Zks(@Path("key") String key, @Body RequestBody requestBody);

    //辖区列表接口
    @POST("/API/wyf/{key}")
    Observable<DomainResponse> getDomains(@Path("key") String key, @Body RequestBody requestBody);

    //订单列表接口
    @POST("/API/wyf/{key}")
    Observable<OrderResponse> getOrders(@Path("key") String key, @Body RequestBody requestBody);

    //订单列表详情接口
    @POST("/API/wyf/{key}")
    Observable<ResponseBean<OrderDetailResponse>> getOrderDetail(@Path("key") String key, @Body RequestBody requestBody);

    //订单状态修改接口
    @POST("/API/wyf/{key}")
    Observable<ResponseBean> modifyOrder(@Path("key") String key, @Body RequestBody requestBody);

    //实人核验接口
    @POST("/API/wyf/{key}")
    Observable<FaceAuthResponse> faceAuth(@Path("key") String key, @Body RequestBody requestBody);

    //获取网约房平台名单代码
    @POST("/API/wyf/{key}")
    Observable<ResponseBean<GetPlatFromResponse>> getPlatfrom(@Path("key") String key, @Body RequestBody requestBody);

    //通过该接口获取密码（暂时适用神盾门锁）
    @POST("/API/wyf/{key}")
    Observable<ResponseBean<GetLockPwdResponse>> getSdLock(@Path("key") String key, @Body RequestBody requestBody);

    //开门密码获取和修改，用于普通门锁(蓝牙门锁等，区别神盾门锁)更新开锁密码，更新后租户可以显示该密码用于开门；
    @POST("/API/wyf/{key}")
    Observable<ResponseBean<GetLockPwdResponse>> uploadPassword(@Path("key") String key, @Body RequestBody requestBody);

    //app更新
    @POST("/API/wyf/{key}")
    Observable<ResponseBean<AppUpdateResponse>> appUpdate(@Path("key") String key, @Body RequestBody requestBody);
}
