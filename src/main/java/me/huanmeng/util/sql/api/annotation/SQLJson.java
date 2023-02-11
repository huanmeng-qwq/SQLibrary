package me.huanmeng.util.sql.api.annotation;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import java.lang.annotation.*;

/**
 * 2022/10/14<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SQLJson {
    /**
     * @return {@link JsonSerializer} {@link JsonDeserializer}
     * @see SQLField.Serialize#JSON
     * @see SQLField.Serialize#serialize()
     */
    Class<? extends JsonSerializer<?>> targetClass();
}
