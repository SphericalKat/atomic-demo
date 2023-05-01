package jp.co.goalist

import jp.co.goalist.transport.AMQPTransport
import jp.co.goalist.transport.Transport
import jp.co.goalist.utils.getSystemName
import java.util.UUID

class AtomicBroker(
    nodeID: String? = null,
    transporter: String,
) {
    var nodeID: String
        private set

    var transporter: String = transporter
        private set

    private var transport: Transport

    var started: Boolean

    var stopping: Boolean


    init {
        this.nodeID = nodeID ?: "${getSystemName()}-${UUID.randomUUID()}"
        this.transport = resolveTransport()

        this.started = false
        this.stopping = false
    }

    private fun resolveTransport(): Transport {
        if (this.transporter.startsWith("amqp://") || this.transporter.startsWith("amqps://")) {
            return AMQPTransport(this)
        }

        throw Errors.BrokerOptionsException("Invalid transporter type $transporter.")
    }

    companion object {
        const val projectName = "ATOMIC"
    }
}