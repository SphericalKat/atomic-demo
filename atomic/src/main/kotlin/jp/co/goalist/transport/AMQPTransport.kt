package jp.co.goalist.transport

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Delivery
import jp.co.goalist.AtomicBroker
import jp.co.goalist.Packets
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

data class QueueOptions(
    var autoDelete: Boolean = false
)

class AMQPTransport(broker: AtomicBroker) : Transport(broker) {
    var connectAttempt = 0

    private lateinit var connection: Connection
    private var channel: Channel? = null
    override val type: String
        get() = "AMQP"


    override fun connect() {
        logger.info("Connecting to the transporter...")
        val factory = ConnectionFactory()
        this.connection = factory.newConnection(broker.transporter)

        try {
            connection.createChannel().let {
                this.channel = it
                this.onConnected(false)

            }
        } catch (e: IOException) {
            logger.warn("Connection failed.", e)
        }
    }

    fun onConnected(wasReconnect: Boolean) {

    }

    override fun subscribe(cmd: Packets, nodeID: String?) {
        if (channel == null) {
            return
        }

        val topic = getTopicName(cmd.string, nodeID)

        // some topics are specific to this node already, in this case we don't need an exchange
        if (nodeID != null) {
            val needAck = listOf(Packets.PACKET_REQUEST).indexOf(cmd) != -1
            val queueOptions = getQueueOptions(cmd)
            channel!!.queueDeclare(topic, false, false, queueOptions.autoDelete, null)
            channel!!.basicConsume(topic, consumeCallback(cmd, needAck)) { _ -> }
        } else {
            val queueName = "${this.prefix}.${cmd}.${this.nodeID}"
            channel!!.exchangeDeclare(topic, BuiltinExchangeType.FANOUT)
            val queueOptions = getQueueOptions(cmd)
            channel!!.queueDeclare(queueName, false, false, queueOptions.autoDelete, null)
        }
    }

    private fun getQueueOptions(packetType: Packets, balancedQueue: Boolean = false): QueueOptions {
        val queueOptions = QueueOptions()

        when (packetType) {
            // requests and responses don't expire
            Packets.PACKET_REQUEST -> {

            }

            Packets.PACKET_EVENT -> {

            }

            Packets.PACKET_RESPONSE -> {

            }

            // Packet types meant for internal use
            Packets.PACKET_HEARTBEAT -> {
                queueOptions.autoDelete = true
            }

            Packets.PACKET_DISCOVER,
            Packets.PACKET_DISCONNECT,
            Packets.PACKET_UNKNOWN,
            Packets.PACKET_INFO,
            Packets.PACKET_PING,
            Packets.PACKET_PONG -> {
                queueOptions.autoDelete = true
            }

            else -> {}
        }
        return queueOptions
    }

    private fun consumeCallback(cmd: Packets, needAck: Boolean = false): (String, Delivery) -> Unit {
        return { _: String, msg: Delivery ->
            val result = this.receive(cmd, msg.body)

            // If a promise is returned, acknowledge the message after it has resolved.
            // This means that if a worker dies after receiving a message but before responding, the
            // message won't be lost, and it can be retried.
            if (needAck) {
                try {
                    this.channel?.basicAck(msg.envelope.deliveryTag, false)
                } catch (e: Exception) {
                    logger.error("Message handling error.", e)
                    this.channel?.basicNack(msg.envelope.deliveryTag, false, true)
                }
            }
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(AMQPTransport::class.java)
    }
}