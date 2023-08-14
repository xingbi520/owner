package com.shendun.renter.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.google.gson.Gson;

import com.shendun.architecture.net.RepositoryManager;
import com.shendun.architecture.utils.DeviceUtils;
import com.shendun.renter.bean.StandardAddressInfo;
import com.shendun.renter.config.UrlConfig;
import com.shendun.renter.repository.bean.BaseResponse;
import com.shendun.renter.repository.bean.DownloadResponseBody;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import timber.log.Timber;

public class RenterApplication extends Application {
    private static final String TAG = RenterApplication.class.getSimpleName();

    private static RenterApplication context;
    private RxErrorHandler rxErrorHandler;

    private float screenWidth, screenHeight;

    private final Gson gson = new Gson();

    private StandardAddressInfo standardAddressInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        init(context);
        Timber.plant(new Timber.DebugTree());
        AutoSizeConfig.getInstance()
            .getUnitsManager()
            .setSupportDP(false)
            .setSupportSubunits(Subunits.MM);
    }

    public void init(Application application) {
        initRepository(application.getApplicationContext());
        initActivity(application);
        initRxErrorHandler(application);
        RxJavaPlugins.setErrorHandler(throwable -> {
            throwable.printStackTrace();
            Timber.tag("Rxjava ErrorHandler")
                    .d("MyApplication setRxJavaErrorHandler:%s ", throwable.getMessage());
        });
    }

    /**
     * 初始化网络栈
     */
    private void initRepository(Context context) {
        List<Interceptor> interceptorList = new ArrayList<>();
        interceptorList.add(new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                Timber.e("header intercept");
                Headers.Builder headerBuilder = new Headers.Builder();
                headerBuilder.add("appversion", DeviceUtils.getVersionName(context));
                headerBuilder.add("platform", "1");
                if (screenWidth == 0 || screenHeight == 0) {
                    screenWidth = DeviceUtils.getRealScreenWidth(context);
                    screenHeight = DeviceUtils.getRealScreenHeight(context);
                }
                headerBuilder.add("width", String.valueOf(screenWidth));
                headerBuilder.add("height", String.valueOf(screenHeight));
                Request build = chain.request().newBuilder().headers(headerBuilder.build()).build();

                Response response = chain.proceed(build);
                try {
                    ResponseBody responseBody = response.body();
                    BufferedSource source = responseBody.source();
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                    Buffer buffer = source.getBuffer();
                    Charset charset = StandardCharsets.UTF_8;
                    MediaType contentType = responseBody.contentType();
                    String bodyString = buffer.clone().readString(contentType.charset(charset));
                    BaseResponse baseResponse = gson.fromJson(bodyString, BaseResponse.class);
                    if (BaseResponse.RESULT_RSP_CODE_FAMILY_DISMISS.equals(
                            baseResponse.getRSP().getRSP_CODE())
                            || BaseResponse.RESULT_RSP_CODE_REMOVED_FAMILY.equals(
                            baseResponse.getRSP().getRSP_CODE())) {
                    }
                } catch (Exception e) {
                    return response;
                }

                return response;
            }
        });

        interceptorList.add(new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());

                return response.newBuilder().body(new DownloadResponseBody(response.body())).build();
            }
        });

        if (AppUtils.isAppDebug ()) {
            RepositoryManager.getInstance().init(UrlConfig.DEBUG_BASE_URL, null, interceptorList);
        } else {
            RepositoryManager.getInstance().init(UrlConfig.BASE_URL, null, interceptorList);
        }
    }

    private void initActivity(Application application) {
        application.registerActivityLifecycleCallbacks(
                new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(@NonNull Activity activity,
                                                  @Nullable Bundle savedInstanceState) {

                    }

                    @Override
                    public void onActivityStarted(@NonNull Activity activity) {
//                        if (activity instanceof BaseActivity) {
//                            View topbar = activity.findViewById(R.id.topbar);
//                            if (null != topbar) {
//                                if (topbar instanceof TopBar) {
//                                    ImageButton leftImageButton =
//                                            ((TopBar) topbar).getLeftImageButton();
//                                    if (null != leftImageButton) {
//                                        leftImageButton.setOnClickListener(view -> {
//                                            activity.onBackPressed();
//                                        });
//                                    }
//                                }
//                            }
//                        }
                    }

                    @Override
                    public void onActivityResumed(@NonNull Activity activity) {

                    }

                    @Override
                    public void onActivityPaused(@NonNull Activity activity) {

                    }

                    @Override
                    public void onActivityStopped(@NonNull Activity activity) {

                    }

                    @Override
                    public void onActivitySaveInstanceState(@NonNull Activity activity,
                                                            @NonNull Bundle outState) {

                    }

                    @Override
                    public void onActivityDestroyed(@NonNull Activity activity) {

                    }
                });
    }

    private void initRxErrorHandler(Context context){
        rxErrorHandler = RxErrorHandler
                .builder()
                .with(context)
                .responseErrorListener(new ResponseErrorListener() {
                    @Override
                    public void handleResponseError(Context context, Throwable t) {
                        if (t instanceof UnknownHostException) {
                            //do something ...
                            Timber.e(TAG, t.getMessage());
                        } else if (t instanceof SocketTimeoutException) {
                            //do something ...
                            Timber.e(TAG, t.getMessage());
                        } else {
                            //handle other Exception ...
                            Timber.e(TAG, t.getMessage());
                        }
                    }
                }).build();
    }

    public RxErrorHandler getRxErrorHandler() {
        return rxErrorHandler;
    }

    public static RenterApplication getContext() {
        return context;
    }

    public StandardAddressInfo getStandardAddressInfo() {
        return standardAddressInfo;
    }

    public void setStandardAddressInfo(StandardAddressInfo standardAddressInfo) {
        this.standardAddressInfo = standardAddressInfo;
    }
}