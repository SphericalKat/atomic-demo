package jp.co.goalist.transport

import jp.co.goalist.AtomicBroker
import jp.co.goalist.Errors
import jp.co.goalist.Packets
import org.slf4j.LoggerFactory

abstract class Transport(protected val broker: AtomicBroker) {
    protected var prefix: String = "ATOM"
    protected var nodeID: String
    var connected: Boolean

    init {
        this.connected = false
        this.nodeID = this.broker.nodeID
    }

    abstract fun connect()

    abstract fun subscribe(cmd: Packets, nodeID: String?)

    fun makeSubscriptions(topics: ArrayList<Topic>) {
        topics.map { t -> this.subscribe(t.cmd, t.nodeID) }
    }

    protected fun getTopicName(cmd: String, nodeID: String?): String {
        return "${this.prefix}.${cmd}${nodeID?.let { ".$it" } ?: ""}"
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Transport::class.java)
        fun resolveTransport(broker: AtomicBroker): Transport {
            if (broker.transporter.startsWith("amqp://") || broker.transporter.startsWith("amqps://")) {
                logger.info("Detected AMQP transporter. Initializing...")
                return AMQPTransport(broker)
            }

            throw Errors.BrokerOptionsException("Invalid transporter type ${broker.transporter}.")
        }
    }
}