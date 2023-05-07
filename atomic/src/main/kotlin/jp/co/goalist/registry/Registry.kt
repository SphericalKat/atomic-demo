package jp.co.goalist.registry

import jp.co.goalist.AtomicBroker
import jp.co.goalist.PacketMessage
import jp.co.goalist.PacketInfo
import jp.co.goalist.registry.discoverers.BaseDiscoverer
import jp.co.goalist.registry.discoverers.LocalDiscoverer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Registry::class.java)
    }
}