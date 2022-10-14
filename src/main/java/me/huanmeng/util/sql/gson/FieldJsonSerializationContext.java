package me.huanmeng.util.sql.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;

/**
 * 2022/10/14<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class FieldJsonSerializationContext implements JsonSerializationContext {
    protected final Gson gson;

    public FieldJsonSerializationContext(Gson gson) {
        this.gson = gson;
    }

    @Override
    public JsonElement serialize(Object src) {
        return gson.toJsonTree(src);
    }

    @Override
    public JsonElement serialize(Object src, Type typeOfSrc) {
        return gson.toJsonTree(src, typeOfSrc);
    }
}
