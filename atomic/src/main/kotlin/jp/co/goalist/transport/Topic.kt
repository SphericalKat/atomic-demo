package jp.co.goalist.transport

import jp.co.goalist.PacketType

data class Topic(
    val cmd: PacketType,
    val nodeID: String? = null
)
