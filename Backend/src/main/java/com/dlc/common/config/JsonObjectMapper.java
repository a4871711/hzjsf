package com.dlc.common.config;

import com.dlc.common.utils.DateUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/***********************************
 *Class by 王楚荣
 *2018/8/29/029
 * **********************************/
public class JsonObjectMapper extends ObjectMapper{
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DateUtils.DATE_TIME_PATTERN);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DateUtils.DATE_PATTERN);

    public JsonObjectMapper() {
        super();
        //反序列化忽略不存在的对象
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 支持 Java 8 日期时间类型（LocalDateTime/LocalDate 等），统一序列化为字符串而非时间戳数组
        this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER));
        this.registerModule(javaTimeModule);
        // java.util.Date/java.sql.Timestamp 统一格式化为字符串，避免走默认 ISO-8601（带 T 和时区）导致前端 parseTime 解析失败
        this.setDateFormat(new SimpleDateFormat(DateUtils.DATE_TIME_PATTERN));
        // 反序列化兼容纯日期串：admin 表单 type:"date" 固定提交 yyyy-MM-dd（无时间），
        // setDateFormat 的严格格式会直接抛 InvalidFormatException 被兜底成"操作有误"
        SimpleModule dateModule = new SimpleModule();
        dateModule.addDeserializer(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                if (p.currentToken().isNumeric()) {
                    return new Date(p.getLongValue());
                }
                String text = p.getValueAsString();
                if (text == null) {
                    // 数组/对象等结构化 token 取不出字符串，明确报错而不是静默置 null
                    throw new IOException("日期字段类型错误，应为字符串或时间戳数字");
                }
                text = text.trim();
                if (text.isEmpty()) {
                    return null;
                }
                String pattern = text.length() <= 10 ? DateUtils.DATE_PATTERN : DateUtils.DATE_TIME_PATTERN;
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                // 关闭宽松模式：否则 "2026-13-45" 会被静默进位成 2027-02-14 写入数据库
                sdf.setLenient(false);
                try {
                    return sdf.parse(text);
                } catch (ParseException e) {
                    throw new IOException("日期格式应为 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss，实际收到: " + text);
                }
            }
        });
        this.registerModule(dateModule);
        // 空值处理为空串
        this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonProcessingException {
                jg.writeString("");
            }
        });
    }

}
