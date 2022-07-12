package com.shuyun.kylin.starter.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.shuyun.kylin.starter.json.util.JsonUtil

class JsonToMapSerializer : JsonSerializer<String>() {
    override fun serialize(value: String?, gen: JsonGenerator, serializers: SerializerProvider?) {
        gen.writeObject(JsonUtil.mapper.readValue<Map<*, *>>(value, Map::class.java))
    }
}