package jp.co.goalist.serializers

import jp.co.goalist.AtomicBroker
import jp.co.goalist.PacketMessage
import jp.co.goalist.PacketType

abstract class BaseSerializer(val broker: AtomicBroker) {
    abstract fun deserialize(buf: ByteArray, type: PacketType): PacketMessage
    abstract fun serialize(data: PacketMessage, type: PacketType): ByteArray
}