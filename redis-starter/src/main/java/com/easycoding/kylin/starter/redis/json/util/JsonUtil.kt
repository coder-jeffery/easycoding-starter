package com.shuyun.kylin.starter.json.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

object JsonUtil {

    val mapper: ObjectMapper = ObjectMapper()

    init {
        //注册未注册的模块
        mapper.findAndRegisterModules()
        //未知属性忽略
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        //允许字段名不带引号 {a:1}
        mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
        //允许用单引号 {'a':'1'}
        mapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
        //允许单个元素匹配到数组上
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        //设置默认的时区
        mapper.setTimeZone(TimeZone.getDefault())
        //设置默认的时间格式为
        //        MAPPER.setDateFormat(DefaultDateFormat.DEFAULT_INSTANCE);
        //序列化时，忽略空值属性
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        //默认根据上下文设置时区
        //        MAPPER.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
    }

    @JvmStatic
    fun toJson(target: Any): String {
        return mapper.writeValueAsString(target)
    }

    @JvmStatic
    fun toPrettyJson(target: Any): String {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(target)
    }

    @JvmStatic
    fun <T> fromJson(jsonContent: String?, classT: Class<T>): T? {

        when (jsonContent) {
            null -> return null
            else -> return when (classT) {
                String::class.java -> jsonContent as T?
                else -> mapper.readValue(jsonContent, classT)
            }
        }

    }

    @JvmStatic
    fun <T> fromJson(jsonContent: String?, ref: TypeReference<T>): T? {
        return when (jsonContent) {
            null -> null
            else -> mapper.readValue(jsonContent, ref)
        }

    }

}
