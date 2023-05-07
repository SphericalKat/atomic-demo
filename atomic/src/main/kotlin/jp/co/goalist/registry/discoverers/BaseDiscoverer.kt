package jp.co.goalist.registry.discoverers

import jp.co.goalist.AtomicBroker
import jp.co.goalist.registry.Node
import jp.co.goalist.registry.Registry
import jp.co.goalist.transport.Transit
import jp.co.goalist.utils.tickerFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.cancellable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.roundToInt
import kotlin.time.DurationUnit
import kotlin.time.toDuration

abstract class BaseDiscoverer(
    private val heartbeatInterval: Int = 10,
    private val heartbeatTimeout: Int = 30
) : CoroutineScope {
    private lateinit var broker: AtomicBroker
    private lateinit var transit: Transit
    private lateinit var registry: Registry
    private var localNode: Node? = null
    private var heartbeatJob: Job? = null

    fun init(registry: Registry) {
        this.registry = registry
        this.broker = registry.broker
        this.transit = this.broker.transit
        this.localNode = this.registry.nodes.localNode

        this.broker.localBus.on("#transporter.connected") { this.startHeartbeatTimers() }
        this.broker.localBus.on("#transporter.disconnected") { this.stopHeartbeatTimers() }
    }

    fun startHeartbeatTimers() {
        val time = heartbeatInterval * 1000 + ((Math.random() * 1000).roundToInt() - 500)

        this.heartbeatJob = launch(Dispatchers.IO) {
            tickerFlow(time.toDuration(DurationUnit.SECONDS)).cancellable().collect { beat() }
        }
    }

    fun stopHeartbeatTimers() {
        if (this.heartbeatJob?.isActive == true) {
            this.heartbeatJob?.cancel()
            this.heartbeatJob = null
        }
    }


    fun beat() {
        // TODO: update local info with cpu data
        return this.sendHeartBeat()
    }

    fun sendHeartBeat() {
        if (this.localNode == null) return
        return this.transit.sendHeartBeat(this.localNode!!)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BaseDiscoverer::class.java)
    }
}