package me.huanmeng.util.sql.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * 2022/2/28<br>
 * Util<br>
 *
 * @author huanmeng_qwq
 */
public class VersionUtil {

    /**
     * 获取maven项目的版本
     *
     * @param clazz 该项目的类
     * @param id    groupId/artifactId
     * @return 版本
     */
    public static String getMavenVersion(Class<?> clazz, String id) {
        String result;
        try (InputStream stream = clazz.getClassLoader().getResourceAsStream("META-INF/maven/" + id + "/pom.properties")) {
            Properties properties = new Properties();
            properties.load(stream);
            result = properties.getProperty("version");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 判断是否比另一个版本新
     *
     * @param version 当前版本
     * @param check   要判断的版本
     */
    public static boolean isLastOrIs(String version, String check) {
        final int[] l = getIntArrayByString(version);
        final int[] c = getIntArrayByString(check);
        for (int i = 0; i < l.length; i++) {
            if (i >= c.length) {
                break;
            }
            if (c[i] < l[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean isOldOrIs(String version, String check) {
        final int[] l = getIntArrayByString(version);
        final int[] c = getIntArrayByString(check);
        for (int i = 0; i < l.length; i++) {
            if (i >= c.length) {
                break;
            }
            if (c[i] > l[i]) {
                return false;
            }
        }
        return true;
    }

    public static int[] getIntArrayByString(String str) {
        final String[] strings = Arrays.stream(str.split("-"))
                .flatMap(e -> Arrays.stream(e.split("_")))
                .flatMap(e -> Arrays.stream(e.split("\\.")))
                .toArray(String[]::new);
        List<Integer> list = new ArrayList<>();
        for (String s : strings) {
            if (NumberUtil.isInt(s)) {
                list.add(Integer.parseInt(s));
            } else {
                for (char c : s.toCharArray()) {
                    if (NumberUtil.isInt(String.valueOf(c))) {
                        list.add(Integer.parseInt(String.valueOf(c)));
                    }
                }
            }
        }
        return list.stream().mapToInt(Integer::intValue).toArray();
    }
}
