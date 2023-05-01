package jp.co.goalist.transport

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import jp.co.goalist.AtomicBroker

class AMQPTransport(private val broker: AtomicBroker): Transport() {
    var connectAttempt = 0
    var connected = false
    private lateinit var connection: Connection
    private var channel: Channel? = null

    override fun connect() {
        val factory = ConnectionFactory()
        this.connection = factory.newConnection(broker.transporter)

        connection.createChannel().let {
            this.channel = it


        }
    }
}