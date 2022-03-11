package me.huanmeng.util.sql.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 2022/2/4<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
@AllArgsConstructor
@Data
public class SQLOrderData {
    private String name;
    private boolean asc;
}
