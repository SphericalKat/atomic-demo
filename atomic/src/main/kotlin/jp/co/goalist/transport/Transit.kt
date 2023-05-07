package jp.co.goalist.transport

import com.squareup.wire.AnyMessage
import jp.co.goalist.*
import jp.co.goalist.registry.Node
import jp.co.goalist.utils.executeWithRetry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

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
        this.tx.messageHandler = { cmd, msg ->
            this.messageHandler(cmd, msg)
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
        this.tx.makeSubscriptions(
            listOf(
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
            )
        )
    }

    private fun messageHandler(cmd: PacketType, msg: PacketMessage): Boolean {
        try {
            // TODO: check protocol version here

            // packet came from our own node
            if (msg.sender == this.nodeID) {
                // detect nodeID conflicts
                if (cmd == PacketType.PACKET_INFO && msg.packets?.unpackOrNull(PacketInfo.ADAPTER)?.instanceID == this.instanceID) {
                    this.broker.fatal("ServiceBroker has detected a nodeID conflict. Use unique nodeIDs. ServiceBroker stopped.")
                    return false
                }

                // skip our own packets
                if (cmd != PacketType.PACKET_EVENT && cmd != PacketType.PACKET_REQUEST && cmd != PacketType.PACKET_RESPONSE) {
                    return false
                }
            }

            when (cmd) {
                PacketType.PACKET_REQUEST -> TODO()
                PacketType.PACKET_RESPONSE -> TODO()
                PacketType.PACKET_EVENT -> TODO()
                PacketType.PACKET_DISCOVER -> TODO()
                PacketType.PACKET_INFO -> TODO()
                PacketType.PACKET_DISCONNECT -> TODO()
                PacketType.PACKET_HEARTBEAT -> TODO()
                PacketType.PACKET_PING -> TODO()
                PacketType.PACKET_PONG -> processPong(msg.packets!!.unpack(PacketPong.ADAPTER))
                else -> return true
            }

            return true
        } catch (e: Exception) {
            logger.error("cmd: $cmd", e)
            return false
        }
    }

    fun sendPing(nodeID: String, id: String) {
        this.publish(
            PacketMessage(
                packetType = PacketType.PACKET_PING,
                packets = AnyMessage.pack(
                    PacketPing(
                        time = java.util.Date().time,
                        id = id,
                        sender = nodeID
                    )
                )
            )
        )
    }

    fun sendPong(payload: PacketPing) {
        val packet = PacketMessage(
            packets = AnyMessage.pack(
                PacketPong(
                    id = payload.id,
                    time = payload.time,
                )
            ),
            packetType = PacketType.PACKET_PONG,
            target = payload.sender
        )

        return this.tx.publish(packet)
    }

    fun processPong(payload: PacketPong) {
        TODO()
    }

    fun sendHeartBeat(localNode: Node) {
        this.publish(
            PacketMessage(
                packetType = PacketType.PACKET_HEARTBEAT,
                packets = AnyMessage.pack(
                    PacketHeartbeat(
                        cpu = 10.1 // TODO: get from localnode
                    )
                )
            )
        )
    }

    fun discoverNodes() {
        this.publish(
            PacketMessage(
                packetType = PacketType.PACKET_DISCOVER,
                packets = AnyMessage.pack(PacketDiscover())
            )
        )
    }

    fun publish(packet: PacketMessage) {
        return this.tx.prepublish(packet)
    }


    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Transit::class.java)
    }
}