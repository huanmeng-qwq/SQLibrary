package me.huanmeng.util.sql.util;

import cn.hutool.core.util.NumberUtil;
import lombok.SneakyThrows;

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
public class VersionUtils {

    @SneakyThrows
    public static String getMavenVersion(Class<?> clazz, String id) {
        String result = "Unknown-Version";
        InputStream stream = clazz.getClassLoader().getResourceAsStream("META-INF/maven/" + id + "/pom.properties");
        Properties properties = new Properties();

        if (stream != null) {
            try {
                properties.load(stream);

                result = properties.getProperty("version");
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                stream.close();
            }
        }
        return result;
    }

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
            if (NumberUtil.isInteger(s)) {
                list.add(NumberUtil.parseInt(s));
            } else {
                for (char c : s.toCharArray()) {
                    if (NumberUtil.isInteger(String.valueOf(c))) {
                        list.add(NumberUtil.parseInt(String.valueOf(c)));
                    }
                }
            }
        }
        return list.stream().mapToInt(Integer::intValue).toArray();
    }
}
