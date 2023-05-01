package jp.co.goalist.serializers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jp.co.goalist.AtomicBroker
import jp.co.goalist.Packets

class JSONSerializer(broker: AtomicBroker) : BaseSerializer(broker) {
    override fun deserialize(buf: ByteArray, type: Packets): MutableMap<String, Any> {
        return ObjectMapper().readValue<MutableMap<String, Any>>(buf.toString())
    }

    override fun serialize(data: Any, type: Packets): ByteArray = ObjectMapper().writeValueAsBytes(data)
}