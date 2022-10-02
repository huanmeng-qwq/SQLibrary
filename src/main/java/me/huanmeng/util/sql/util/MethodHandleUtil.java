package me.huanmeng.util.sql.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 2022/10/2<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class MethodHandleUtil {
    private MethodHandleUtil() {
    }

    /**
     * jdk8中如果直接调用{@link MethodHandles#lookup()}获取到的{@link MethodHandles.Lookup}在调用findSpecial和unreflectSpecial
     * 时会出现权限不够问题，抛出"no private access for invokespecial"异常，因此针对JDK8及JDK9+分别封装lookup方法。
     *
     * @param callerClass 被调用的类或接口
     * @return {@link MethodHandles.Lookup}
     */
    public static MethodHandles.Lookup lookup(Class<?> callerClass) {
        try {
            return LookupFactory.lookup(callerClass);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行接口或对象中的特殊方法（private、static等）<br>
     *
     * <pre class="code">
     *     interface Duck {
     *         default String quack() {
     *             return "Quack";
     *         }
     *     }
     *
     *     Duck duck = (Duck) Proxy.newProxyInstance(
     *         ClassLoaderUtil.getClassLoader(),
     *         new Class[] { Duck.class },
     *         MethodHandleUtil::invoke);
     * </pre>
     *
     * @param <T>    返回结果类型
     * @param obj    接口的子对象或代理对象
     * @param method 方法
     * @param args   参数
     * @return 结果
     */
    public static <T> T invokeSpecial(Object obj, Method method, Object... args) {
        return invoke(true, obj, method, args);
    }

    /**
     * 执行接口或对象中的方法<br>
     *
     * <pre class="code">
     *     interface Duck {
     *         default String quack() {
     *             return "Quack";
     *         }
     *     }
     *
     *     Duck duck = (Duck) Proxy.newProxyInstance(
     *         ClassLoaderUtil.getClassLoader(),
     *         new Class[] { Duck.class },
     *         MethodHandleUtil::invoke);
     * </pre>
     *
     * @param <T>       返回结果类型
     * @param isSpecial 是否为特殊方法（private、static等）
     * @param obj       接口的子对象或代理对象
     * @param method    方法
     * @param args      参数
     * @return 结果
     */
    @SuppressWarnings("unchecked")
    public static <T> T invoke(boolean isSpecial, Object obj, Method method, Object... args) {
        if (method == null) {
            throw new NullPointerException("Method must be not null!");
        }
        final Class<?> declaringClass = method.getDeclaringClass();
        final MethodHandles.Lookup lookup = lookup(declaringClass);
        try {
            MethodHandle handle = isSpecial ? lookup.unreflectSpecial(method, declaringClass)
                    : lookup.unreflect(method);
            if (null != obj) {
                handle = handle.bindTo(obj);
            }
            return (T) handle.invokeWithArguments(args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
