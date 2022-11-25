package me.huanmeng.util.sql.serialize;

import me.huanmeng.util.sql.impl.SQLEntityFieldMetaData;

/**
 * 2022/11/25<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public interface ValueSerialize {
    <T, I> Object serialize(SQLEntityFieldMetaData<T, I> fieldMetaData, Object o);
}
