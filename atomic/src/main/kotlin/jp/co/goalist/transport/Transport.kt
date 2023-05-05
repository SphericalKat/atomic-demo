package jp.co.goalist.transport

import jp.co.goalist.*
import org.slf4j.LoggerFactory

abstract class Transport(protected val broker: AtomicBroker) {
    protected var prefix: String = "ATOM"
    protected var nodeID: String
    protected val instanceID: String
    var afterConnect: ((Boolean) -> Unit)? = null
    lateinit var transit: Transit

    abstract val type: String
    var connected: Boolean

    init {
        this.connected = false
        this.nodeID = this.broker.nodeID
        this.instanceID = this.broker.instanceID
    }

    abstract fun connect()

    abstract fun subscribe(cmd: PacketType, nodeID: String?)

    fun makeSubscriptions(topics: List<Topic>) {
        topics.map { t -> this.subscribe(t.cmd, t.nodeID) }
    }

    protected fun getTopicName(cmd: PacketType, nodeID: String?): String {
        return "${this.prefix}.${cmd}${nodeID?.let { ".$it" } ?: ""}"
    }

    protected fun receive(packetType: PacketType, msg: ByteArray) {
        try {
            val packet = this.deserialize(packetType, msg)
        } catch (e: Exception) {
            logger.warn("Invalid incoming packet. Type: $packetType", e)
            logger.debug("Content: {}", msg)
        }
    }

    data class SendMeta(
        val balanced: Boolean = false,
        val packet: PacketMessage? = null
    )

    abstract fun send(topic: String, data: ByteArray, meta: SendMeta)

    fun publish(packet: PacketMessage) {
        val topic = getTopicName(packet.packetType, packet.target)
        val data = this.serialize(packet)

        return this.send(topic, data, SendMeta(packet = packet))
    }

    private fun requestHandler(payload: MutableMap<String, Any>) {
        val requestID = payload["requestID"]?.let { "with requestID '$it'" } ?: ""
        logger.debug("<= Request '{}' {} received from '{}' node.", payload["action"], requestID, payload["sender"])

        try {
            if (this.broker.stopping) {
                logger.warn(
                    "Incoming '{}' {} received from '{}' node is dropped because broker is stopped.",
                    payload["action"],
                    requestID,
                    payload["sender"]
                )

                // throw new error here
                throw Errors.AtomicServerError("${payload["action"]} ${this.nodeID}")
            }
        } catch (e: Exception) {
            // TODO: deal with exception
        }
    }

    protected fun deserialize(type: PacketType, buf: ByteArray): PacketMessage? {
        if (buf.isEmpty()) return null

        return broker.serializer.deserialize(buf, type)
    }

    protected fun serialize(packet: PacketMessage): ByteArray {
        val newPacket = packet.copy(
            ver = AtomicBroker.PROTOCOL_VER.toString(),
            sender = this.nodeID,
        )
        return this.broker.serializer.serialize(newPacket, newPacket.packetType)
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