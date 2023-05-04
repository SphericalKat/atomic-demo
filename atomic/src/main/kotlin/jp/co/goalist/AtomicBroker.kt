package jp.co.goalist

import jp.co.goalist.serializers.BaseSerializer
import jp.co.goalist.serializers.Serializers
import jp.co.goalist.transport.AMQPTransport
import jp.co.goalist.transport.Transit
import jp.co.goalist.transport.Transport
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

    private var transit: Transit

    var serializer: BaseSerializer

    var started: Boolean

    var stopping: Boolean


    init {
        this.nodeID = nodeID ?: "${getSystemName()}-${UUID.randomUUID()}"
        this.instanceID = UUID.randomUUID().toString()
        val tx = Transport.resolveTransport(this)
        this.transit = Transit(this, this.nodeID, this.instanceID, tx)

        logger.info("Transporter: ${tx.type}")

        this.serializer = Serializers.resolve(serializer, this)

        this.started = false
        this.stopping = false
    }

    fun start() {
        this.transport.connect()
        this.started = true

        logger.info("ServiceBroker with this.services.length service(s) started successfully.")
    }

    fun fatal(message: String, e: Exception? = null, needExit: Boolean = true) {
        logger.error("Fatal error: $message $e")

        if (needExit) exitProcess(1)
    }

    companion object {
        const val projectName = "ATOMIC"
        val logger: Logger = LoggerFactory.getLogger(AtomicBroker::class.java)
        const val PROTOCOL_VER = 1.0
    }
}