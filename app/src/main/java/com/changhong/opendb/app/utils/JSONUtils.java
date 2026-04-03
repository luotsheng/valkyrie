package com.changhong.opendb.app.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.util.List;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class JSONUtils
{
        private static final ObjectMapper objectMapper = new ObjectMapper();

        /**
         * 转 JSON 字符串
         */
        public static String toJSONString(Object object, SerializationFeature...features)
        {
                try {
                        for (SerializationFeature feature : features)
                                objectMapper.enable(feature);

                        String ret = objectMapper.writeValueAsString(object);

                        for (SerializationFeature feature : features)
                                objectMapper.disable(feature);

                        return ret;
                } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                }
        }

        public static <T> T toJavaObject(String json, Class<T> aClass)
        {
                try {
                        return objectMapper.readValue(json, aClass);
                } catch (JsonProcessingException e) {
                        Catcher.ithrow(e);
                        return null;
                }
        }

        public static <T> List<T> toJavaList(String jsonArray, Class<T> aClass)
        {
                try {
                        CollectionType collectionType = objectMapper.getTypeFactory()
                                .constructCollectionType(List.class, aClass);

                        return objectMapper.readValue(jsonArray, collectionType);
                } catch (JsonProcessingException e) {
                        Catcher.ithrow(e);
                        return null;
                }
        }

        @SuppressWarnings("unchecked")
        public static <T> T deepCopy(T src)
        {
                return (T) toJavaObject(toJSONString(src), src.getClass());
        }
}
