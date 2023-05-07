package jp.co.goalist.registry

import jp.co.goalist.PacketInfo
import jp.co.goalist.PacketMessage
import java.lang.management.ManagementFactory

class Node(
    val id: String,
    var instanceID: String? = null,
    var available: Boolean = true,
    var local: Boolean = false,
    var lastHeartbeatTime: Long = ManagementFactory.getRuntimeMXBean().uptime,
    var offlineSince: Long? = null
) {
    var rawInfo: PacketInfo? = null
    var ipList: List<String>? = null
    var hostname: String? = null
    var client: PacketInfo.Client? = null
    var seq: Int = 0

    fun update(payload: PacketMessage, isReconnected: Boolean): Boolean {
        val packet = payload.packets!!.unpack(PacketInfo.ADAPTER)
        this.ipList = packet.ipList
        this.hostname = packet.hostname
        this.client = packet.client

        this.rawInfo = packet
        val newSeq = payload

        return false
    }
}