package jp.co.goalist.registry

import jp.co.goalist.AtomicBroker
import jp.co.goalist.PacketMessage
import jp.co.goalist.PacketInfo
import jp.co.goalist.registry.discoverers.BaseDiscoverer
import jp.co.goalist.registry.discoverers.LocalDiscoverer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.log

class Registry(val broker: AtomicBroker) {
    var localNodeInfoInvalidated = true
    val nodes: NodeCatalog = NodeCatalog(this, broker)
    val discoverer: BaseDiscoverer = LocalDiscoverer()

    /**
     * Processes in incoming node [PacketInfo] wrapped in a [PacketMessage] [payload]
     */
    fun processNodeInfo(payload: PacketMessage) = this.nodes.processNodeInfo(payload)

    fun init() {
        this.discoverer.init(this)
    }

    fun stop() {
        this.discoverer.stop()
    }

    fun regenerateLocalRawInfo(incSeq: Boolean, isStopping: Boolean = false): PacketInfo {
        val node = this.nodes.localNode!!
        if (incSeq) node.seq++

        val rawInfo = PacketInfo(
            ipList = node.ipList!!,
            hostname = node.hostname!!,
            instanceID = node.instanceID!!,
            client = node.client!!.copy(),
            seq = node.seq
        )

        if (!isStopping && (this.broker.started || incSeq)) {
            rawInfo.services
        }

        return rawInfo
    }

    fun getLocalNodeInfo(force: Boolean = false): PacketInfo {
        if (force || this.nodes.localNode?.rawInfo == null || this.localNodeInfoInvalidated) {
            val res = this.regenerateLocalRawInfo(this.localNodeInfoInvalidated)
            logger.debug("Local Node info regenerated.")
            this.localNodeInfoInvalidated = false
            return res
        }
        return this.nodes.localNode!!.rawInfo!!
    }

    fun getNodeInfo(nodeID: String): PacketInfo? {
        val node = this.nodes.get(nodeID) ?: return null

        if (node.local) return this.getLocalNodeInfo()

        return node.rawInfo
    }

    fun getNodeList(onlyAvailable: Boolean = false, withServices: Boolean = false) =
        this.nodes.list(onlyAvailable, withServices)

    fun getNodeRawList() = this.nodes.toList().map { it.rawInfo }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Registry::class.java)
    }
}