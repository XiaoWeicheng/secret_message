package org.example.secret_message.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author weicheng.zhao
 * @date 2020/12/23
 */
@Slf4j
public final class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final byte[] DEFAULT_RESULT = new byte[0];

    private JsonUtils() {
    }

    public static <T> T parseJson(String json, Class<T> tClass) {
        try {
            return OBJECT_MAPPER.readValue(json, tClass);
        } catch (Exception e) {
            log.error("反序列化Json异常 {} {}", json, tClass, e);
            return null;
        }
    }

    public static <T> T parseJson(byte[] json, Class<T> tClass) {
        try {
            return OBJECT_MAPPER.readValue(json, tClass);
        } catch (Exception e) {
            log.error("反序列化Json异常 {} {}", json, tClass, e);
            return null;
        }
    }

    public static <T> String toJsonString(T object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            log.error("序列化Json异常 {}", object, e);
            return null;
        }
    }

    public static <T> byte[] toJsonBytes(T object) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(object);
        } catch (Exception e) {
            log.error("序列化Json异常 {}", object, e);
            return DEFAULT_RESULT;
        }
    }
}
