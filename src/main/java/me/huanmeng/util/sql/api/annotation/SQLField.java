package me.huanmeng.util.sql.api.annotation;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import me.huanmeng.util.sql.api.SQLTypeParser;
import me.huanmeng.util.sql.api.SQLibrary;
import me.huanmeng.util.sql.impl.SQLEntityFieldMetaData;
import me.huanmeng.util.sql.serialize.ValueSerialize;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 2022/1/28<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SQLField {
    /**
     * 字段名 (Filed Name)
     */
    String value() default "";

    /**
     * 索引(联合索引)
     */
    boolean id() default false;

    /**
     * 自增id
     */
    boolean isAutoIncrement() default false;

    boolean notNull() default false;

    /**
     * {@link IndexType}
     */
    IndexType index() default IndexType.NONE;

    /**
     * 数据类型<br>
     * VARCHAR,255<br>
     * MEDIUMINT<br>
     */
    String sqlType() default "";

    Class<? extends SQLTypeParser> parser() default SQLTypeParser.class;

    String remapName() default "";

    /**
     * 默认查询排序
     */
    Order orderBy() default Order.NONE;

    /**
     * @return 序列化类型
     */
    Serialize serialize() default Serialize.NONE;

    /**
     * @return 序列化类型
     * @apiNote {@link SQLibrary#addSerialize(String, ValueSerialize)}
     */
    String serializeName() default "";

    /**
     * 排序
     */
    enum Order {
        NONE,
        ASC,
        DESC
    }

    enum IndexType {
        NONE,
        INDEX,
        UNIQUE_KEY,
        PRIMARY_KEY,
        FULLTEXT_INDEX;

        public cc.carm.lib.easysql.api.enums.IndexType get() {
            switch (this) {
                case INDEX: {
                    return cc.carm.lib.easysql.api.enums.IndexType.INDEX;
                }
                default: {
                    return null;
                }
                case UNIQUE_KEY: {
                    return cc.carm.lib.easysql.api.enums.IndexType.UNIQUE_KEY;
                }
                case PRIMARY_KEY: {
                    return cc.carm.lib.easysql.api.enums.IndexType.PRIMARY_KEY;
                }
                case FULLTEXT_INDEX: {
                    return cc.carm.lib.easysql.api.enums.IndexType.FULLTEXT_INDEX;
                }
            }
        }
    }

    /**
     * 序列化
     */
    enum Serialize {
        /**
         * 直接返回
         */
        NONE,
        /**
         * @see Object#toString()
         */
        TO_STRING() {
            @Override
            public <T, I> Object serialize(SQLEntityFieldMetaData<T, I> fieldMetaData, Object o) {
                if (o == null) {
                    return null;
                }
                return o.toString();
            }
        },
        /**
         * @see Gson#toJson(Object)
         */
        JSON() {
            @Override
            public <T, I> Object serialize(SQLEntityFieldMetaData<T, I> fieldMetaData, Object o) {
                Gson gson = fieldMetaData.sqlibrary().gson();
                if (fieldMetaData.jsonSerializer() != null) {
                    //noinspection unchecked,ConstantConditions
                    JsonElement serialize = fieldMetaData.jsonSerializer()
                            .serialize(o, TypeToken.get(o.getClass()).getType(),
                                    fieldMetaData.sqlibrary().fieldJsonSerializationContext());
                    return serialize.toString();
                }
                return gson.toJson(o);
            }

            @Override
            public <I, T> void deserialize(SQLEntityFieldMetaData<I, T> fieldMetaData, ResultSet resultSet, I instance) throws SQLException {
                String data = resultSet.getString(fieldMetaData.fieldName());
                SQLibrary sqlibrary = fieldMetaData.sqlibrary();
                if (fieldMetaData.jsonSerializer() instanceof JsonDeserializer) {
                    JsonElement jsonElement = JsonParser.parseString(data);
                    //noinspection unchecked,ConstantConditions
                    T deserialize = ((JsonDeserializer<T>) fieldMetaData.jsonSerializer()).deserialize(jsonElement, TypeToken.get(instance.getClass()).getType(), sqlibrary.fieldJsonDeserializationContext());
                    fieldMetaData.setValue(instance, deserialize);
                    return;
                }
                fieldMetaData.setValue(instance, sqlibrary.gson().fromJson(data, fieldMetaData.type()));
            }
        },
        ;

        @Nullable
        public <T, I> Object serialize(SQLEntityFieldMetaData<T, I> fieldMetaData, Object o) {
            return o;
        }

        public <I, T> void deserialize(SQLEntityFieldMetaData<I, T> fieldMetaData, ResultSet resultSet, I instance) throws SQLException {
            fieldMetaData.sqlType().transform(resultSet, fieldMetaData, instance);
        }
    }
}
