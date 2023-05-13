package com.shendun.architecture.net;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author:daiminzhi
 * Time: 7:11 PM
 */
public class RepositoryManager {

    private Retrofit retrofit;

    private OkHttpClient.Builder okhttpBuild;

    private Map<String, Object> mRetrofitServiceCache;

    private RepositoryManager() {
    }

    private static class SingleTonHoler {
        private static RepositoryManager INSTANCE = new RepositoryManager();
    }

    public static RepositoryManager getInstance() {
        return SingleTonHoler.INSTANCE;
    }

    /**
     * 初始化必要对象和参数
     */
    public void init(String baseUrl, Map<String, String> urlsMap,
                     List<Interceptor> interceptorList) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okhttpBuild = new OkHttpClient.Builder().addInterceptor(logging);
        if (interceptorList != null && !interceptorList.isEmpty()) {
            for (Interceptor interceptor : interceptorList) {
                okhttpBuild.addInterceptor(interceptor);
            }
        }
        if (null != urlsMap) {
            for (Map.Entry<String, String> entry : urlsMap.entrySet()) {
                RetrofitUrlManager.getInstance().putDomain(entry.getKey(), entry.getValue());
            }
        }
        OkHttpClient client = RetrofitUrlManager.getInstance().with(okhttpBuild)
                .connectTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build();
        // 初始化Retrofit
        retrofit = new Retrofit.Builder().client(client)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public OkHttpClient.Builder getOkhttpBuild() {
        return okhttpBuild;
    }

    public <T> T obtainRetrofitService(final Class<T> service) {
        if (mRetrofitServiceCache == null) {
            mRetrofitServiceCache = new HashMap<>();
        }
        T retrofitService = (T) mRetrofitServiceCache.get(service.getCanonicalName());
        if (retrofitService == null) {
            retrofitService = retrofit.create(service);
            mRetrofitServiceCache.put(service.getCanonicalName(), retrofitService);
        }
        return retrofitService;
    }
}
