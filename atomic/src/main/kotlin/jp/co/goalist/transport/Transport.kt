package jp.co.goalist.transport

import jp.co.goalist.AtomicBroker
import jp.co.goalist.Errors
import jp.co.goalist.Packet
import jp.co.goalist.Packets
import org.slf4j.LoggerFactory

abstract class Transport(protected val broker: AtomicBroker) {
    protected var prefix: String = "ATOM"
    protected var nodeID: String
    var connected: Boolean

    init {
        this.connected = false
        this.nodeID = this.broker.nodeID
    }

    abstract fun connect()

    abstract fun subscribe(cmd: Packets, nodeID: String?)

    fun makeSubscriptions(topics: ArrayList<Topic>) {
        topics.map { t -> this.subscribe(t.cmd, t.nodeID) }
    }

    protected fun getTopicName(cmd: String, nodeID: String?): String {
        return "${this.prefix}.${cmd}${nodeID?.let { ".$it" } ?: ""}"
    }

    protected fun receive(cmd: Packets, msg: ByteArray) {
        try {
            // deserialize here
        } catch (e: Exception) {
            logger.warn("Invalid incoming packet. Type: ${cmd.string}", e)
            logger.debug("Content: {}", msg)
        }
    }

    protected fun deserialize(type: Packets, buf: ByteArray): Packet? {
        if (buf.isEmpty()) return null

        val msg = this.broker.serializer.deserialize(buf, type)
        return Packet(type, null, msg)
    }

    protected fun serialize(packet: Packet): ByteArray {
        packet.payload["ver"] = AtomicBroker.PROTOCOL_VER
        packet.payload["sender"] = this.nodeID
        return this.broker.serializer.serialize(packet.payload, packet.type)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Transport::class.java)
        fun resolveTransport(broker: AtomicBroker): Transport {
            if (broker.transporter.startsWith("amqp://") || broker.transporter.startsWith("amqps://")) {
                logger.info("Detected AMQP transporter. Initializing...")
                return AMQPTransport(broker)
            }

            throw Errors.BrokerOptionsException("Invalid transporter type ${broker.transporter}.")
        }
    }
}