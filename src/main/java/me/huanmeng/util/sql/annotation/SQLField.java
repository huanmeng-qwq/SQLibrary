package me.huanmeng.util.sql.annotation;

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
     * 自增
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

    public static enum Order {
        NONE,
        ASC,
        DESC
    }
}
