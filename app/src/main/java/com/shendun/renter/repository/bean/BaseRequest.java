package com.shendun.renter.repository.bean;

import java.io.Serializable;
import java.util.Random;
import com.shendun.architecture.utils.MD5Util;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseRequest implements Serializable {

    //服务器端开发要求写1
    private static final String CHANNEL = "1";
    //服务器端开发要求写 ''空字符串
    private static final String VERSION = "";

    protected abstract String key();

    protected abstract JSONObject bodyObject() throws JSONException;

    protected String getJsonStr() {
        JSONObject headerObject = new JSONObject();
        JSONObject json = new JSONObject();
        try {
            String key = key();
            JSONObject bodyObject = bodyObject();
            String channel = CHANNEL;
            String resTime = String.valueOf(System.currentTimeMillis());
            Random rnd = new Random();
            int num = rnd.nextInt(89999) + 10000;
            String reqSeq = String.valueOf(num);
            String version = VERSION;
            headerObject.put("key", key);
            headerObject.put("channel", channel);
            headerObject.put("reqSeq", reqSeq);
            headerObject.put("resTime", resTime);
            headerObject.put("version", version);
            headerObject.put("sign", MD5Util.toMD5(key + resTime + reqSeq + channel + version));
            json.put("header", headerObject);
            json.put("body", bodyObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public RequestBody getRequestBody() {
        return    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), getJsonStr());
    }
}
