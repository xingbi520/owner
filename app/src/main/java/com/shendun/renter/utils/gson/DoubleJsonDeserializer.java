package com.shendun.renter.utils.gson;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by xw
 * on 2016/11/22.
 */
public class DoubleJsonDeserializer implements JsonDeserializer<Double> {
    @Override
    public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return json.getAsDouble();
        } catch (Exception e) {
            LogUtils.d("DoubleJsonDeserializer-deserialize-error:" + (json != null ? json.toString() : ""));
            return 0D;
        }
    }
}
