package jp.co.goalist.utils

import java.net.InetAddress

fun getSystemName(): String? {
    return try {
        InetAddress.getLocalHost().hostName
    } catch (exception: Exception) {
        System.err.println("System Name Exp : " + exception.message)
        null
    }
}