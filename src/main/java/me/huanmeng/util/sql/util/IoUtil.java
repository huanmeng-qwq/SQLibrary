package me.huanmeng.util.sql.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 2022/10/2<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class IoUtil {
    private IoUtil() {
    }

    /**
     * 从流中读取bytes，读取完毕后关闭流
     *
     * @param inputStream {@link InputStream}
     * @return bytes
     */
    public static byte[] readBytes(InputStream inputStream) {
        return readBytes(inputStream, true);
    }

    /**
     * 从流中读取bytes，读取完毕后关闭流
     *
     * @param inputStream {@link InputStream}
     * @param close       是否关闭流
     * @return bytes
     */
    public static byte[] readBytes(InputStream inputStream, boolean close) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
            int read;
            while ((read = inputStream.read()) != -1) {
                outputStream.write(read);
            }
            if (close) {
                inputStream.close();
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
