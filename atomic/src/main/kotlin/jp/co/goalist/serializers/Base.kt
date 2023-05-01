package jp.co.goalist.serializers

import jp.co.goalist.AtomicBroker
import jp.co.goalist.Packets

abstract class BaseSerializer(val broker: AtomicBroker) {
    abstract fun deserialize(buf: ByteArray, type: Packets): MutableMap<String, Any>
    abstract fun serialize(data: Any, type: Packets): ByteArray
}