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
@Target(ElementType.TYPE)
public @interface SQLEntity {
    /**
     * 表名 (Table Name)
     */
    String value() default "";
}
