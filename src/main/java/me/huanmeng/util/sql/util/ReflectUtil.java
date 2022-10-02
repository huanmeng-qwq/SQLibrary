package me.huanmeng.util.sql.util;

import me.huanmeng.util.sql.type.HutoolAdapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 2022/10/2<br>
 * SQLibrary<br>
 *
 * @author huanmeng_qwq
 */
public class ReflectUtil {

    private static final Map<Class<?>, Constructor<?>[]> CONSTRUCTORS_CACHE = new ConcurrentHashMap<>();
    /**
     * 字段缓存
     */
    private static final Map<Class<?>, Field[]> FIELDS_CACHE = new ConcurrentHashMap<>();
    /**
     * 方法缓存
     */
    private static final Map<Class<?>, Method[]> METHODS_CACHE = new ConcurrentHashMap<>();

    private ReflectUtil() {
    }

    // --------------------------------------------------------------------------------------------------------- method

    /**
     * 获得指定类本类及其父类中的Public方法名<br>
     * 去重重载的方法
     *
     * @param clazz 类
     * @return 方法名Set
     */
    public static Set<String> getPublicMethodNames(Class<?> clazz) {
        final HashSet<String> methodSet = new HashSet<>();
        final Method[] methodArray = getPublicMethods(clazz);
        if (ArrayUtil.isNotEmpty(methodArray)) {
            for (Method method : methodArray) {
                methodSet.add(method.getName());
            }
        }
        return methodSet;
    }

    /**
     * 获得本类及其父类所有Public方法
     *
     * @param clazz 查找方法的类
     * @return 过滤后的方法列表
     */
    public static Method[] getPublicMethods(Class<?> clazz) {
        return null == clazz ? null : clazz.getMethods();
    }

    /**
     * 查找指定Public方法 如果找不到对应的方法或方法不为public的则返回{@code null}
     *
     * @param clazz      类
     * @param methodName 方法名
     * @param paramTypes 参数类型
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     */
    public static Method getPublicMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    /**
     * 忽略大小写查找指定方法，如果找不到对应的方法则返回{@code null}
     *
     * <p>
     * 此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。
     * </p>
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param paramTypes 参数类型，指定参数类型如果是方法的子类也算
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     * @since 3.2.0
     */
    public static Method getMethodIgnoreCase(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
        return getMethod(clazz, true, methodName, paramTypes);
    }

    /**
     * 查找指定方法 如果找不到对应的方法则返回{@code null}
     *
     * <p>
     * 此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。
     * </p>
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param paramTypes 参数类型，指定参数类型如果是方法的子类也算
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
        return getMethod(clazz, false, methodName, paramTypes);
    }

    /**
     * 查找指定方法 如果找不到对应的方法则返回{@code null}<br>
     * 此方法为精准获取方法名，即方法名和参数数量和类型必须一致，否则返回{@code null}。<br>
     * 如果查找的方法有多个同参数类型重载，查找第一个找到的方法
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param ignoreCase 是否忽略大小写
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @param paramTypes 参数类型，指定参数类型如果是方法的子类也算
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     * @since 3.2.0
     */
    public static Method getMethod(Class<?> clazz, boolean ignoreCase, String methodName, Class<?>... paramTypes) throws SecurityException {
        if (null == clazz || methodName == null || methodName.trim().isEmpty()) {
            return null;
        }

        final Method[] methods = getMethods(clazz);
        if (ArrayUtil.isNotEmpty(methods)) {
            for (Method method : methods) {
                if (StrUtil.equals(methodName, method.getName(), ignoreCase)
                        && me.huanmeng.util.sql.util.ClassUtil.isAllAssignableFrom(method.getParameterTypes(), paramTypes)
                        //排除桥接方法，pr#1965@Github
                        && !method.isBridge()) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 按照方法名查找指定方法名的方法，只返回匹配到的第一个方法，如果找不到对应的方法则返回{@code null}
     *
     * <p>
     * 此方法只检查方法名是否一致，并不检查参数的一致性。
     * </p>
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     * @since 4.3.2
     */
    public static Method getMethodByName(Class<?> clazz, String methodName) throws SecurityException {
        return getMethodByName(clazz, false, methodName);
    }

    /**
     * 按照方法名查找指定方法名的方法，只返回匹配到的第一个方法，如果找不到对应的方法则返回{@code null}
     *
     * <p>
     * 此方法只检查方法名是否一致（忽略大小写），并不检查参数的一致性。
     * </p>
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     * @since 4.3.2
     */
    public static Method getMethodByNameIgnoreCase(Class<?> clazz, String methodName) throws SecurityException {
        return getMethodByName(clazz, true, methodName);
    }

    /**
     * 按照方法名查找指定方法名的方法，只返回匹配到的第一个方法，如果找不到对应的方法则返回{@code null}
     *
     * <p>
     * 此方法只检查方法名是否一致，并不检查参数的一致性。
     * </p>
     *
     * @param clazz      类，如果为{@code null}返回{@code null}
     * @param ignoreCase 是否忽略大小写
     * @param methodName 方法名，如果为空字符串返回{@code null}
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     * @since 4.3.2
     */
    public static Method getMethodByName(Class<?> clazz, boolean ignoreCase, String methodName) throws SecurityException {
        if (null == clazz || StrUtil.isBlank(methodName)) {
            return null;
        }

        final Method[] methods = getMethods(clazz);
        if (ArrayUtil.isNotEmpty(methods)) {
            for (Method method : methods) {
                if (StrUtil.equals(methodName, method.getName(), ignoreCase)
                        // 排除桥接方法
                        && !method.isBridge()) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 获得指定类中的Public方法名<br>
     * 去重重载的方法
     *
     * @param clazz 类
     * @return 方法名Set
     * @throws SecurityException 安全异常
     */
    public static Set<String> getMethodNames(Class<?> clazz) throws SecurityException {
        final HashSet<String> methodSet = new HashSet<>();
        final Method[] methods = getMethods(clazz);
        for (Method method : methods) {
            methodSet.add(method.getName());
        }
        return methodSet;
    }

    /**
     * 获得一个类中所有方法列表，包括其父类中的方法
     *
     * @param beanClass 类，非{@code null}
     * @return 方法列表
     * @throws SecurityException 安全检查异常
     */
    public static Method[] getMethods(Class<?> beanClass) throws SecurityException {
        if (beanClass == null) {
            throw new NullPointerException("beanClass");
        }
        return METHODS_CACHE.computeIfAbsent(beanClass,
                (e) -> getMethodsDirectly(beanClass, true, true));
    }

    /**
     * 获得一个类中所有方法列表，直接反射获取，无缓存<br>
     * 接口获取方法和默认方法，获取的方法包括：
     * <ul>
     *     <li>本类中的所有方法（包括static方法）</li>
     *     <li>父类中的所有方法（包括static方法）</li>
     *     <li>Object中（包括static方法）</li>
     * </ul>
     *
     * @param beanClass            类或接口
     * @param withSupers           是否包括父类或接口的方法列表
     * @param withMethodFromObject 是否包括Object中的方法
     * @return 方法列表
     * @throws SecurityException 安全检查异常
     */
    public static Method[] getMethodsDirectly(Class<?> beanClass, boolean withSupers, boolean withMethodFromObject) throws SecurityException {
        if (beanClass == null) {
            throw new NullPointerException("beanClass");
        }

        if (beanClass.isInterface()) {
            // 对于接口，直接调用Class.getMethods方法获取所有方法，因为接口都是public方法
            return withSupers ? beanClass.getMethods() : beanClass.getDeclaredMethods();
        }

        final Set<Method> result = new HashSet<>();
        Class<?> searchType = beanClass;
        while (searchType != null) {
            if (!withMethodFromObject && Object.class == searchType) {
                break;
            }
            result.addAll(Arrays.asList(searchType.getDeclaredMethods()));
            result.addAll(getDefaultMethodsFromInterface(searchType));


            searchType = (withSupers && !searchType.isInterface()) ? searchType.getSuperclass() : null;
        }

        return result.toArray(new Method[0]);
    }

    /**
     * 是否为equals方法
     *
     * @param method 方法
     * @return 是否为equals方法
     */
    public static boolean isEqualsMethod(Method method) {
        if (method == null ||
                1 != method.getParameterCount() ||
                !"equals".equals(method.getName())) {
            return false;
        }
        return (method.getParameterTypes()[0] == Object.class);
    }

    /**
     * 是否为hashCode方法
     *
     * @param method 方法
     * @return 是否为hashCode方法
     */
    public static boolean isHashCodeMethod(Method method) {
        return method != null//
                && "hashCode".equals(method.getName())//
                && isEmptyParam(method);
    }

    /**
     * 是否为toString方法
     *
     * @param method 方法
     * @return 是否为toString方法
     */
    public static boolean isToStringMethod(Method method) {
        return method != null//
                && "toString".equals(method.getName())//
                && isEmptyParam(method);
    }

    /**
     * 是否为无参数方法
     *
     * @param method 方法
     * @return 是否为无参数方法
     * @since 5.1.1
     */
    public static boolean isEmptyParam(Method method) {
        return method.getParameterCount() == 0;
    }

    /**
     * 检查给定方法是否为Getter或者Setter方法，规则为：<br>
     * <ul>
     *     <li>方法参数必须为0个或1个</li>
     *     <li>如果是无参方法，则判断是否以“get”或“is”开头</li>
     *     <li>如果方法参数1个，则判断是否以“set”开头</li>
     * </ul>
     *
     * @param method 方法
     * @return 是否为Getter或者Setter方法
     * @since 5.7.20
     */
    public static boolean isGetterOrSetterIgnoreCase(Method method) {
        return isGetterOrSetter(method, true);
    }

    /**
     * 检查给定方法是否为Getter或者Setter方法，规则为：<br>
     * <ul>
     *     <li>方法参数必须为0个或1个</li>
     *     <li>方法名称不能是getClass</li>
     *     <li>如果是无参方法，则判断是否以“get”或“is”开头</li>
     *     <li>如果方法参数1个，则判断是否以“set”开头</li>
     * </ul>
     *
     * @param method     方法
     * @param ignoreCase 是否忽略方法名的大小写
     * @return 是否为Getter或者Setter方法
     * @since 5.7.20
     */
    public static boolean isGetterOrSetter(Method method, boolean ignoreCase) {
        if (null == method) {
            return false;
        }

        // 参数个数必须为0或1
        final int parameterCount = method.getParameterCount();
        if (parameterCount > 1) {
            return false;
        }

        String name = method.getName();
        // 跳过getClass这个特殊方法
        if ("getClass".equals(name)) {
            return false;
        }
        if (ignoreCase) {
            name = name.toLowerCase();
        }
        switch (parameterCount) {
            case 0:
                return name.startsWith("get") || name.startsWith("is");
            case 1:
                return name.startsWith("set");
            default:
                return false;
        }
    }
    // --------------------------------------------------------------------------------------------------------- newInstance

    /**
     * 实例化对象
     *
     * @param <T>   对象类型
     * @param clazz 类名
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String clazz) {
        try {
            return (T) Class.forName(clazz).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Instance class [%s] error!", clazz), e);
        }
    }

    /**
     * 获取类对应接口中的非抽象方法（default方法）
     *
     * @param clazz 类
     * @return 方法列表
     */
    private static List<Method> getDefaultMethodsFromInterface(Class<?> clazz) {
        List<Method> result = new ArrayList<>();
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method m : ifc.getMethods()) {
                if (!ModifierUtil.isAbstract(m)) {
                    result.add(m);
                }
            }
        }
        return result;
    }

    /**
     * 执行方法
     *
     * <p>
     * 对于用户传入参数会做必要检查，包括：
     *
     * <pre>
     *     1、忽略多余的参数
     *     2、参数不够补齐默认值
     *     3、传入参数为null，但是目标参数类型为原始类型，做转换
     * </pre>
     *
     * @param <T>    返回对象类型
     * @param obj    对象，如果执行静态方法，此值为{@code null}
     * @param method 方法（对象方法或static方法都可）
     * @param args   参数对象
     * @return 结果
     */
    public static <T> T invoke(Object obj, Method method, Object... args) {
        try {
            return invokeRaw(obj, method, args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行方法
     *
     * <p>
     * 对于用户传入参数会做必要检查，包括：
     *
     * <pre>
     *     1、忽略多余的参数
     *     2、参数不够补齐默认值
     *     3、传入参数为null，但是目标参数类型为原始类型，做转换
     * </pre>
     *
     * @param <T>    返回对象类型
     * @param obj    对象，如果执行静态方法，此值为{@code null}
     * @param method 方法（对象方法或static方法都可）
     * @param args   参数对象
     * @return 结果
     * @throws InvocationTargetException 目标方法执行异常
     * @throws IllegalAccessException    访问异常
     * @since 5.8.1
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeRaw(Object obj, Method method, Object... args) throws InvocationTargetException, IllegalAccessException {
        method.setAccessible(true);

        // 检查用户传入参数：
        // 1、忽略多余的参数
        // 2、参数不够补齐默认值
        // 3、通过NullWrapperBean传递的参数,会直接赋值null
        // 4、传入参数为null，但是目标参数类型为原始类型，做转换
        // 5、传入参数类型不对应，尝试转换类型
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Object[] actualArgs = new Object[parameterTypes.length];
        if (null != args) {
            for (int i = 0; i < actualArgs.length; i++) {
                if (i >= args.length || null == args[i]) {
                    // 越界或者空值
                    actualArgs[i] = ClassUtil.getDefaultValue(parameterTypes[i]);
                } else if (!parameterTypes[i].isAssignableFrom(args[i].getClass()) && HutoolAdapter.supportHutool()) {
                    //对于类型不同的字段，尝试转换，转换失败则使用原对象类型
                    final Object targetValue = HutoolAdapter.convert(parameterTypes[i], args[i]);
                    if (null != targetValue) {
                        actualArgs[i] = targetValue;
                    }
                } else {
                    actualArgs[i] = args[i];
                }
            }
        }

        if (method.isDefault()) {
            // 当方法是default方法时，尤其对象是代理对象，需使用句柄方式执行
            // 代理对象情况下调用method.invoke会导致循环引用执行，最终栈溢出
            return MethodHandleUtil.invokeSpecial(obj, method, args);
        }

        return (T) method.invoke(ClassUtil.isStatic(method) ? null : obj, actualArgs);
    }
}
