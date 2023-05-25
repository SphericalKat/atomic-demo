package jp.co.goalist

import jp.co.goalist.registry.Registry
import jp.co.goalist.serializers.BaseSerializer
import jp.co.goalist.serializers.Serializers
import jp.co.goalist.transport.AMQPTransport
import jp.co.goalist.transport.Transit
import jp.co.goalist.transport.Transport
import jp.co.goalist.utils.LocalBus
import jp.co.goalist.utils.getSystemName
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlin.system.exitProcess

class AtomicBroker(
    nodeID: String? = null,
    transporter: String,
    serializer: String = "JSON",
) {
    var nodeID: String
        private set

    val instanceID: String

    var transporter: String = transporter
        private set

    var transit: Transit

    var serializer: BaseSerializer

    var started: Boolean

    var stopping: Boolean

    var localBus = LocalBus()

    var registry: Registry

    init {
        this.nodeID = nodeID ?: "${getSystemName()}-${UUID.randomUUID()}"
        logger.info("NodeID: ${this.nodeID}")
        this.instanceID = UUID.randomUUID().toString()
        val tx = Transport.resolveTransport(this)
        this.registry = Registry(this)
        this.transit = Transit(this, this.nodeID, this.instanceID, tx)
        logger.info("Transporter: ${tx.type}")

        this.registry.init()

        this.serializer = Serializers.resolve(serializer, this)

        this.started = false
        this.stopping = false

        Runtime.getRuntime().addShutdownHook(Thread {
            logger.info("Runtime shutdown hook triggered.")
            this.stop()
        })
    }

    fun start() {
        val startTime = java.util.Date().time

        this.transit.connect()
        this.started = true

        logger.info("ServiceBroker with this.services.length service(s) started successfully in ${java.util.Date().time - startTime}ms.")
    }

    fun stop() {
        this.started = false
        this.transit.disconnect()
        this.registry.stop()
    }

    fun fatal(message: String, e: Exception? = null, needExit: Boolean = true) {
        logger.error("Fatal error: $message $e")

        if (needExit) exitProcess(1)
    }

    fun emit(eventName: String, payload: Any) {
        if (eventName.startsWith('#')) this.localBus.emit(eventName, payload)
    }

    fun broadcastLocal(eventName: String, payload: Any) {
        logger.debug("Broadcast $eventName local event.")

        if (eventName.startsWith('#')) this.localBus.emit(eventName, payload)

        // TODO: local services emit etc
    }

    fun getLocalNodeInfo() = this.registry.getLocalNodeInfo()

    companion object {
        const val projectName = "ATOMIC"
        val logger: Logger = LoggerFactory.getLogger(AtomicBroker::class.java)
        const val PROTOCOL_VER = 1.0
    }
}