package jp.co.goalist.registry

import jp.co.goalist.AtomicBroker
import jp.co.goalist.PacketMessage
import jp.co.goalist.PacketInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Registry(val broker: AtomicBroker) {
    var localNodeInfoInvalidated = true
    val nodes: NodeCatalog = NodeCatalog(this, broker)

    /**
     * Processes in incoming node [PacketInfo] wrapped in a [PacketMessage] [payload]
     */
    fun processNodeInfo(payload: PacketMessage) = this.nodes.processNodeInfo(payload)

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Registry::class.java)
    }
}