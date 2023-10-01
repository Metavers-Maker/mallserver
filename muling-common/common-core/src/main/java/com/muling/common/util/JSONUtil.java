package com.muling.common.util;

import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import jodd.json.TypeJsonSerializer;
import jodd.json.TypeJsonSerializerMap;
import jodd.util.StringPool;
import jodd.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;

public class JSONUtil {

    private static Logger logger = LoggerFactory.getLogger(JSONUtil.class);

    static {
        /**
         * @see jodd.json.TypeJsonSerializerMap 中使用的 TypeCache<TypeJsonSerializer> map 是IdentifyMap 而非ConcurrentHashMap, 是线程不安全的。
         * 导致多个服务合并在同一个JVM时，在启动的时候会去并发加载该static块，从而使map中的key-value 值被URL.class -> new URLJsonSerializer()的覆盖，
         * 例如java.Lang.Long 被覆盖， 从而在序列化的时候找不到Long对应的序列化器，而使用了默认的
         * @see jodd.json.impl.ObjectJsonSerializer, 结果使序列化值原本应该是 {"longValue": 20} 的变成了 {"longValue": {}}
         * */
        if (notRegisteredURLJsonSerializer()) {
            synchronized (TypeJsonSerializerMap.get()) {
                if (notRegisteredURLJsonSerializer()) {
                    // 自定义URL的JSON序列化，避免默认序列化访问URL类的getContent方法触发远程调用；逆序列化通过jodd-bean的URLConverter自动支持
                    TypeJsonSerializerMap.get().register(URL.class, new URLJsonSerializer());
                }
            }
        }
    }

    /**
     * 服务合并中由于加载的class属于不同的服务路径，因此在服务A put 进去的对象在后续服务B get 出来的对象用instanceof URLJsonSerializer 结果是false
     * 因此不能使用 instanceof URLJsonSerializer判断，用URLJsonSerializer 的类名判断
     */
    private static boolean notRegisteredURLJsonSerializer() {
        TypeJsonSerializer typeJsonSerializer = TypeJsonSerializerMap.get().lookup(URL.class);

        return !(null != typeJsonSerializer && typeJsonSerializer.getClass().getName().equals(URLJsonSerializer.class.getName()));
    }

    public static String serialize(Object value) {

        return serialize(JsonSerializer.create().deep(true), value);
    }

    public static String prettySerialize(Object value) {

        return serialize(JsonSerializer.createPrettyOne().deep(true), value);
    }

    /**
     * useful for log output
     *
     * @param value
     * @return
     */
    public static String serializeSilently(Object value) {

        try {
            return serialize(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return StringPool.EMPTY;
    }

    /**
     * useful for log output
     *
     * @param value
     * @return
     */
    public static String prettySerializeSilently(Object value) {

        try {
            return prettySerialize(value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return StringPool.EMPTY;
    }

    public static String serialize(JsonSerializer serializer, Object value) {

        return serializer.serialize(value);
    }

    public static <T> T deserialize(String value, Class bean) {

        if (value == null) {
            return null;
        }

        // 数组类型处理
        if (StringUtil.trimLeft(value).startsWith("[")) {
            return new JsonParser().looseMode(true).map("values", bean).parse(value);
        }

        // 对象类型处理
        return (T) new JsonParser().looseMode(true).parse(value, bean);
    }

    public static Map deserialize(String value) {

        if (value == null || value.isEmpty()) {
            return null;
        }

        return JsonParser.create().parse(value);
    }
}