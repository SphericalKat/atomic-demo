package jp.co.goalist.transport

import jp.co.goalist.*
import jp.co.goalist.utils.executeWithRetry
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Transit(
    private val broker: AtomicBroker,
    private val nodeID: String,
    private val instanceID: String,
    private val tx: Transport,
) {
    private var connected: Boolean = false
    private var disconnecting: Boolean = false
    private var isReady: Boolean = false

    init {
        this.tx.transit = this
        this.tx.afterConnect = {
            this.afterConnect(it)
        }
    }

    fun connect() {
        logger.info("Connecting to the transporter...")

        executeWithRetry {
            this.tx.connect()
        }
    }

    private fun afterConnect(wasReconnect: Boolean): Unit {
        if (wasReconnect) {
            // TODO: send local node info
        } else {
            this.makeSubscriptions()
        }

        // TODO: discover all nodes
        this.connected = true
    }

    fun makeSubscriptions() {
        this.tx.makeSubscriptions(listOf(
            // Subscribe to broadcast events
            Topic(cmd = PacketType.PACKET_EVENT, nodeID = this.nodeID),

            // Subscribe to requests
            Topic(cmd = PacketType.PACKET_REQUEST, nodeID = this.nodeID),

            // Subscribe to node responses of requests
            Topic(cmd = PacketType.PACKET_RESPONSE, nodeID = this.nodeID),

            // Discovery handler
            Topic(cmd = PacketType.PACKET_DISCOVER),
            Topic(cmd = PacketType.PACKET_DISCOVER, nodeID = this.nodeID),

            // NodeInfo handler
            Topic(cmd = PacketType.PACKET_INFO),
            Topic(cmd = PacketType.PACKET_INFO, nodeID = this.nodeID),

            // Disconnect handler
            Topic(cmd = PacketType.PACKET_DISCONNECT),

            // Heartbeat handler
            Topic(cmd = PacketType.PACKET_HEARTBEAT),

            // Ping handler
            Topic(cmd = PacketType.PACKET_PING), // broadcast
            Topic(cmd = PacketType.PACKET_PING, nodeID = this.nodeID), // targeted

            // Pong handler
            Topic(cmd = PacketType.PACKET_PONG, nodeID = this.nodeID),
        ))
    }

    fun messageHandler(cmd: Packets, packet: Packet): Boolean {
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


    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Transit::class.java)
    }
}