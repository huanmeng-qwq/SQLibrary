package me.huanmeng.util.sql.test;

import lombok.*;
import me.huanmeng.util.sql.annotation.SQLField;

import java.util.List;
import java.util.UUID;

/**
 * 2022/1/29<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserData {
    @SQLField(id = true, isAutoIncrement = true)
    private int id;
    @SQLField(id = true)
    private UUID uuid;
    @SQLField
    private Integer[] data;
}
