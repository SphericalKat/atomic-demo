package jp.co.goalist.registry.discoverers

import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class LocalDiscoverer(override val coroutineContext: CoroutineContext = Job()) : BaseDiscoverer() {
    fun discoverNode(nodeID: String) {
        this.transit.discoverNode(nodeID)
    }

    override fun discoverAllNodes() = this.transit.discoverNodes()

    override fun sendLocalNodeInfo(nodeID: String?) {
        val info = this.broker.getLocalNodeInfo()

        if (nodeID == null) {
            // TODO: make balanced subscriptions on transporter
        }

        this.transit.sendNodeInfo(info, nodeID)
    }
}