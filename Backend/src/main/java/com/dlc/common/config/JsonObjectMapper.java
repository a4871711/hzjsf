package com.dlc.common.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

/***********************************
 *Class by 王楚荣
 *2018/8/29/029
 * **********************************/
public class JsonObjectMapper extends ObjectMapper{
    private static final long serialVersionUID = 1L;
    public JsonObjectMapper() {
        super();
        this.registerModule(new JavaTimeModule());
        //反序列化忽略不存在的对象
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 空值处理为空串
        this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
                jg.writeString("");
            }
        });
    }

}
