package me.huanmeng.util.sql.util;

/**
 * 2022/10/2<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */

@FunctionalInterface
public interface BiFunctionThrowable<T, U, R, EX extends Throwable> {
    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     */
    R apply(T t, U u) throws EX;
}
