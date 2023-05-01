package jp.co.goalist.transport

import jp.co.goalist.Packets

data class Topic(
    val cmd: Packets,
    val nodeID: String? = null
)
