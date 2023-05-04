package jp.co.goalist.transport

import jp.co.goalist.AtomicBroker
import jp.co.goalist.Packets
import jp.co.goalist.utils.executeWithRetry
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Transit(
    private val broker: AtomicBroker,
    val nodeID: String,
    val instanceID: String,
    val tx: Transport,
) {
    private var connected: Boolean = false
    private var disconnecting: Boolean = false
    private var isReady: Boolean = false

    init {

    }

    fun connect() {
        logger.info("Connecting to the transporter...")

        executeWithRetry {
            this.tx.connect()
        }
    }

    fun makeSubscriptions() {
        this.tx.makeSubscriptions(listOf(
            // Subscribe to broadcast events
            Topic(cmd = Packets.PACKET_EVENT, nodeID = this.nodeID),

            // Subscribe to requests
            Topic(cmd = Packets.PACKET_REQUEST, nodeID = this.nodeID),

            // Subscribe to node responses of requests
            Topic(cmd = Packets.PACKET_RESPONSE, nodeID = this.nodeID),

            // Discovery handler
            Topic(cmd = Packets.PACKET_DISCOVER),
            Topic(cmd = Packets.PACKET_DISCOVER, nodeID = this.nodeID),

            // NodeInfo handler
            Topic(cmd = Packets.PACKET_INFO),
            Topic(cmd = Packets.PACKET_INFO, nodeID = this.nodeID),

            // Disconnect handler
            Topic(cmd = Packets.PACKET_DISCONNECT),

            // Heartbeat handler
            Topic(cmd = Packets.PACKET_HEARTBEAT),

            // Ping handler
            Topic(cmd = Packets.PACKET_PING), // broadcast
            Topic(cmd = Packets.PACKET_PING, nodeID = this.nodeID), // targeted

            // Pong handler
            Topic(cmd = Packets.PACKET_PONG, nodeID = this.nodeID),
        ))
    }

    fun messageHandler() {}

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Transit::class.java)
    }
}