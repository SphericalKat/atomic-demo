package jp.co.goalist

import jp.co.goalist.serializers.BaseSerializer
import jp.co.goalist.serializers.Serializers
import jp.co.goalist.transport.AMQPTransport
import jp.co.goalist.transport.Transport
import jp.co.goalist.utils.getSystemName
import org.slf4j.LoggerFactory
import java.util.UUID

class AtomicBroker(
    nodeID: String? = null,
    transporter: String,
    serializer: String = "JSON",
) {
    var nodeID: String
        private set

    var transporter: String = transporter
        private set

    private var transport: Transport

    var serializer: BaseSerializer

    var started: Boolean

    var stopping: Boolean


    init {
        this.nodeID = nodeID ?: "${getSystemName()}-${UUID.randomUUID()}"
        this.transport = Transport.resolveTransport(this)
        this.serializer = Serializers.resolve(serializer, this)

        this.started = false
        this.stopping = false
    }

    fun start() {
        this.transport.connect()
        this.started = true

        logger.info("ServiceBroker with this.services.length service(s) started successfully.")
    }

    companion object {
        const val projectName = "ATOMIC"
        val logger = LoggerFactory.getLogger(AtomicBroker::class.java)
        const val PROTOCOL_VER = 1.0
    }
}