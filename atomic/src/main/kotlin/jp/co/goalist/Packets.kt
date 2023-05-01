package jp.co.goalist

enum class Packets(val string: String) {
    PACKET_UNKNOWN("???"),
    PACKET_EVENT("EVENT"),
    PACKET_REQUEST("REQ"),
    PACKET_RESPONSE("RES"),
    PACKET_DISCOVER("DISCOVER"),
    PACKET_INFO("INFO"),
    PACKET_DISCONNECT("DISCONNECT"),
    PACKET_HEARTBEAT("HEARTBEAT"),
    PACKET_PING("PING"),
    PACKET_PONG("PONG"),

    PACKET_GOSSIP_REQ("GOSSIP_REQ"),
    PACKET_GOSSIP_RES("GOSSIP_RES"),
    PACKET_GOSSIP_HELLO("GOSSIP_HELLO"),
}

data class Packet(
    val type: Packets = Packets.PACKET_UNKNOWN,
    val target: String? = null,
    val payload: MutableMap<String, Any>?
)
