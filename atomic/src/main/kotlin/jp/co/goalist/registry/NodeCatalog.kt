package jp.co.goalist.registry

import jp.co.goalist.AtomicBroker
import jp.co.goalist.PacketInfo
import jp.co.goalist.PacketMessage
import jp.co.goalist.utils.Event
import jp.co.goalist.utils.getIpAddresses
import jp.co.goalist.utils.getSystemName
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.management.ManagementFactory

class NodeCatalog(val registry: Registry, val broker: AtomicBroker) {
    private val nodes: MutableMap<String, Node> = mutableMapOf()
    var localNode: Node? = null

    init {
        this.createLocalNode()
    }

    fun createLocalNode() {
        val node = Node(this.broker.nodeID)
        node.local = true
        node.ipList = getIpAddresses()
        node.instanceID = this.broker.instanceID
        node.hostname = getSystemName()
        node.client = PacketInfo.Client(
            type = "JVM",
            version = AtomicBroker.PROTOCOL_VER.toString(),
            langVersion = "Kotlin 1.8.20"
        )
        node.seq = 1

        this.add(node.id, node)

        this.localNode = node
    }

    /**
     * Add a [Node] to the catalog
     */
    fun add(id: String, node: Node) {
        this.nodes[id] = node
    }

    /**
     * Get a [Node] with nodeID equal to [id]
     */
    fun get(id: String): Node? = this.nodes[id]

    /**
     * Delete a [Node] by with nodeID equal to [id]
     */
    fun delete(id: String): Boolean = this.nodes.remove(id) == null

    /**
     * Get a count of all registered [Node]s
     */
    fun count() = this.nodes.size

    /**
     * Get a count of all online [Node]s
     */
    fun onlineCount(): Int = this.nodes.values.count { it.available }

    fun processNodeInfo(payload: PacketMessage): Node {
        val nodeID = payload.sender
        var node = this.get(nodeID)
        var isNew = false
        var isReconnected = false

        // if a node with given nodeID does not exist, add it to our catalog
        if (node == null) {
            isNew = true
            node = Node(nodeID)
            this.add(nodeID, node)
        } else if (!node.available) {
            // if this node has been unavailable, mark it as available again
            isReconnected = true
            node.lastHeartbeatTime = ManagementFactory.getRuntimeMXBean().uptime
            node.available = true
            node.offlineSince = null
        }

        val needRegister = node.update(payload, isReconnected)

        if (isNew) {
            this.broker.broadcastLocal("#node.connected", Event(mapOf(
                "node" to node,
                "reconnected" to false,
            )))
            logger.info("Node $nodeID connected.")
        } else if (isReconnected) {
            this.broker.broadcastLocal("#node.connected", Event(mapOf(
                "node" to node,
                "reconnected" to true,
            )))
            logger.info("Node $nodeID reconnected.")
        } else {
            this.broker.broadcastLocal("#node.updated", Event(mapOf(
                "node" to node
            )))
            logger.info("Node $nodeID updated.")
        }

        return node
    }

    fun disconnected(id: String, isUnexpected: Boolean = false) {
        val node = this.get(id)
        if (node != null && node.available) {
            // TODO: node.disconnected()

            if (isUnexpected) logger.warn("Node ${node.id} disconnected unexpectedly.")
            else logger.info("Node ${node.id} disconnected.")

            // TODO: if (this.broker.transit) this.broker.transit.removePendingRequestByNodeID(nodeID);
        }
    }

    fun list(onlyAvailable: Boolean = false, withServices: Boolean = false): List<Node> {
        val res = listOf<Node>()
        this.nodes.values.forEach {
            if (onlyAvailable && !it.available) return@forEach
        }
        return res
    }

    fun toList() = this.nodes.values.toList()

    companion object {
        val logger: Logger = LoggerFactory.getLogger(NodeCatalog::class.java)
    }
}