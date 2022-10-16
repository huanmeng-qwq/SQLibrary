package me.huanmeng.util.sql;

import com.google.gson.*;
import me.huanmeng.util.sql.api.annotation.SQLEntity;
import me.huanmeng.util.sql.api.annotation.SQLField;
import me.huanmeng.util.sql.api.annotation.SQLJson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 2022/10/2<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
@SQLEntity("users")
public class UserData {
    @SQLField(id = true, isAutoIncrement = true)
    private Long dbId;
    @SQLField
    private String username;
    @SQLField
    private Integer age;

    public UserData(Long dbId, String username, Integer age) {
        this.dbId = dbId;
        this.username = username;
        this.age = age;
    }

    public Long getDbId() {
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserData)) return false;
        UserData userData = (UserData) o;
        return Objects.equals(dbId, userData.dbId) && Objects.equals(username, userData.username) && Objects.equals(age, userData.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbId, username, age);
    }

    @Override
    public String toString() {
        return "UserData{" +
                "dbId=" + dbId +
                ", username='" + username + '\'' +
                ", age=" + age +
                '}';
    }

    private static class UserDataSerializer implements JsonSerializer<List<String>>, JsonDeserializer<List<String>> {

        @Override
        public List<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            ArrayList<String> list = new ArrayList<>();
            JsonObject jsonObject = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                list.add(entry.getValue().getAsString());
            }
            return list;
        }

        @Override
        public JsonElement serialize(List<String> src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            for (int i = 0; i < src.size(); i++) {
                jsonObject.addProperty(String.valueOf(i), src.get(i));
            }
            return jsonObject;
        }
    }
}
