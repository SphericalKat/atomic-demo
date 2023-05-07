package jp.co.goalist.registry.discoverers

import jp.co.goalist.AtomicBroker
import jp.co.goalist.transport.Transit
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class BaseDiscoverer(
    private val heartbeatInterval: Int
) {
    private lateinit var broker: AtomicBroker
    private lateinit var transit: Transit

    fun init(broker: AtomicBroker) {
        this.broker = broker
        this.transit = broker.transit
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BaseDiscoverer::class.java)
    }
}