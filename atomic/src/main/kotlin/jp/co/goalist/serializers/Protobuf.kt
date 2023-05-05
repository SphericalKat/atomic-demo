package jp.co.goalist.serializers

import jp.co.goalist.AtomicBroker
import jp.co.goalist.PacketMessage
import jp.co.goalist.PacketType

class ProtobufSerializer(broker: AtomicBroker) : BaseSerializer(broker) {
    override fun deserialize(buf: ByteArray, type: PacketType): PacketMessage {
        return PacketMessage.ADAPTER.decode(buf)
    }

    override fun serialize(data: PacketMessage, type: PacketType): ByteArray {
        return PacketMessage.ADAPTER.encode(data)
    }
}