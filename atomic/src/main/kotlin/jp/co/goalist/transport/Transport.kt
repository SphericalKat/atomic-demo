package jp.co.goalist.transport

import jp.co.goalist.AtomicBroker
import jp.co.goalist.Errors
import jp.co.goalist.Packet
import jp.co.goalist.Packets
import org.slf4j.LoggerFactory

abstract class Transport(protected val broker: AtomicBroker) {
    protected var prefix: String = "ATOM"
    protected var nodeID: String
    protected val instanceID: String
    var connected: Boolean

    init {
        this.connected = false
        this.nodeID = this.broker.nodeID
        this.instanceID = this.broker.instanceID
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
            val packet = this.deserialize(cmd, msg)
        } catch (e: Exception) {
            logger.warn("Invalid incoming packet. Type: ${cmd.string}", e)
            logger.debug("Content: {}", msg)
        }
    }

    protected fun messageHandler(cmd: Packets, packet: Packet): Boolean {
        try {
            val payload = packet.payload ?: throw Errors.AtomicServerError("Missing response payload.")

            // TODO: check protocol version here

            // packet came from our own node
            if (payload["sender"] as String == this.nodeID) {
                // detect nodeID conflicts
                if (cmd == Packets.PACKET_INFO && payload["instanceID"] as String == this.instanceID) {
                    this.broker.fatal("ServiceBroker has detected a nodeID conflict. Use unique nodeIDs. ServiceBroker stopped.")
                    return false
                }

                // skip our own packets
                if (cmd != Packets.PACKET_EVENT && cmd != Packets.PACKET_REQUEST && cmd != Packets.PACKET_RESPONSE) {
                    return false
                }
            }

            // request
            if (cmd == Packets.PACKET_REQUEST) {

            }

            return true
        } catch (e: Exception) {
            logger.error("cmd: $cmd packet: $packet", e)
            return false
        }
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