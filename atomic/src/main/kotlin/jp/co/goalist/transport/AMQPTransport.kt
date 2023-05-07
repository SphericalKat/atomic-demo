package jp.co.goalist.transport

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Delivery
import jp.co.goalist.AtomicBroker
import jp.co.goalist.PacketMessage
import jp.co.goalist.PacketType
import jp.co.goalist.Packets
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import kotlin.math.log

data class QueueOptions(
    var autoDelete: Boolean = false,
    var options: Map<String, Any>? = null
)

class AMQPTransport(broker: AtomicBroker) : Transport(broker) {
    var connectAttempt = 0

    private lateinit var connection: Connection
    private var channel: Channel? = null
    private var connectionCount = 0
    override val type: String
        get() = "AMQP"


    override fun connect() {
        logger.info("Connecting to the transporter...")
        val factory = ConnectionFactory()
        this.connection = factory.newConnection(broker.transporter)

        try {
            connection.createChannel().let {
                this.channel = it
                this.connectionCount += 1
                val isReconnect = this.connectionCount > 1

                if (isReconnect) {
                    this.transit.makeSubscriptions()
                }

                this.onConnected(isReconnect)
            }
        } catch (e: IOException) {
            logger.warn("Connection failed.", e)
        }
    }

    private fun onConnected(wasReconnect: Boolean) {
        this.connected = true
        this.afterConnect?.invoke(wasReconnect)
    }

    override fun subscribe(cmd: PacketType, nodeID: String?) {
        if (this.channel == null) {
            return
        }

        val channel = this.channel!! // just to remove null checks everywhere

        val topic = getTopicName(cmd, nodeID)

        // some topics are specific to this node already, in this case we don't need an exchange
        if (nodeID != null) {
            val needAck = cmd == PacketType.PACKET_REQUEST
            val queueOptions = getQueueOptions(cmd)
            channel.queueDeclare(topic, true, false, queueOptions.autoDelete, queueOptions.options)
            channel.basicConsume(topic, consumeCallback(cmd, needAck)) { _ -> }
        } else {
            val queueName = "${this.prefix}.${cmd}.${this.nodeID}"

            channel.exchangeDeclare(topic, BuiltinExchangeType.FANOUT, true)

            val queueOptions = getQueueOptions(cmd)
            channel.queueDeclare(queueName, true, false, queueOptions.autoDelete, queueOptions.options)

            channel.queueBind(queueName, topic, "")
            channel.basicConsume(queueName, consumeCallback(cmd)) { _ -> }
        }
    }

    override fun send(topic: String, data: ByteArray, meta: SendMeta) {
        if (this.channel == null) return

        if (meta.packet?.target != null || meta.balanced) {
            // there is a target, or this is a balanced send. either way, we publish directly to the queue
            // setting exchange as "" and routing key to topic directly publishes to the target queue
            this.channel!!.basicPublish("", topic, null, data)
        } else {
            // more traditional publish to the exchange
            this.channel!!.basicPublish(topic, "", null, data)
        }
    }

    private fun getQueueOptions(packetType: PacketType, balancedQueue: Boolean = false): QueueOptions {
        val queueOptions = QueueOptions()

        when (packetType) {
            PacketType.PACKET_HEARTBEAT -> {
                queueOptions.autoDelete = true
            }

            PacketType.PACKET_DISCOVER,
            PacketType.PACKET_DISCONNECT,
            PacketType.PACKET_INFO,
            PacketType.PACKET_PING,
            PacketType.PACKET_PONG -> {
                queueOptions.autoDelete = true
            }

            else -> {}
        }

        return queueOptions
    }

    private fun consumeCallback(cmd: PacketType, needAck: Boolean = false): (String, Delivery) -> Unit {
        return { _: String, msg: Delivery ->
            this.receive(cmd, msg.body)

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