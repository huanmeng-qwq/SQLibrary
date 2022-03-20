package me.huanmeng.util.sql.annotation;

import java.lang.annotation.*;

/**
 * 2022/3/13<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SQLIgnore {
}
