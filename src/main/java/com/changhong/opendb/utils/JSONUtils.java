package com.changhong.opendb.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author Luo Tiansheng
 * @since 2026/3/26
 */
public class JSONUtils
{
        private static final ObjectMapper objectMapper = new ObjectMapper();

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
}
