package me.huanmeng.util.sql.test;

import lombok.*;
import me.huanmeng.util.sql.annotation.SQLField;

import java.util.Objects;
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
    @SQLField(id = true)
    private String type;
    private int coin;
    private boolean a;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserData userData = (UserData) o;
        return coin == userData.coin && a == userData.a && uuid.equals(userData.uuid) && type.equals(userData.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, type, coin, a);
    }
}
