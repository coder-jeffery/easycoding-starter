package com.shuyun.kylin.starter.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.shuyun.kylin.starter.json.util.JsonUtil

/**
 * @author raven
 * @date 2019-07-23 15:35
 */
class MapToJsonDeserializer : JsonDeserializer<String>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): String {
        return JsonUtil.mapper.writeValueAsString(p.readValueAs<Map<*, *>>(Map::class.java))
    }
}