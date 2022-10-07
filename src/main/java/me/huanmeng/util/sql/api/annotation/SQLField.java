package me.huanmeng.util.sql.api.annotation;

import com.google.gson.Gson;
import me.huanmeng.util.sql.impl.SQLEntityFieldMetaData;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.*;

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

    /**
     * 数据类型<br>
     * VARCHAR,255<br>
     * MEDIUMINT<br>
     */
    String sqlType() default "";

    /**
     * 默认查询排序
     */
    Order orderBy() default Order.NONE;

    /**
     * @return 序列化类型
     */
    Serialize serialize() default Serialize.NONE;

    /**
     * 排序
     */
    enum Order {
        NONE,
        ASC,
        DESC
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
            public Object transform(SQLEntityFieldMetaData<?> fieldMetaData, Object o) {
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
            public Object transform(SQLEntityFieldMetaData<?> fieldMetaData, Object o) {
                return fieldMetaData.sqlibrary().gson().toJson(o);
            }
        },
        ;

        @Nullable
        public Object transform(SQLEntityFieldMetaData<?> fieldMetaData, Object o) {
            return o;
        }
    }
}
