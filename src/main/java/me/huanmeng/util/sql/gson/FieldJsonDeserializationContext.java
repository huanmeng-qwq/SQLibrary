package me.huanmeng.util.sql.gson;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * 2022/10/14<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class FieldJsonDeserializationContext implements JsonDeserializationContext {
    protected final Gson gson;

    public FieldJsonDeserializationContext(Gson gson) {
        this.gson = gson;
    }

    @Override
    public <T> T deserialize(JsonElement json, Type typeOfT) throws JsonParseException {
        return gson.fromJson(json, typeOfT);
    }
}
