package jp.co.goalist.utils

import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface

fun getSystemName(): String? {
    return try {
        InetAddress.getLocalHost().hostName
    } catch (exception: Exception) {
        System.err.println("System Name Exp : " + exception.message)
        null
    }
}

inline fun <T> executeWithRetry(
    predicate: (cause: Throwable) -> Boolean = { true },
    retries: Int = 1,
    call: () -> T
): T? {
    for (i in 0..retries) {
        return try {
            call()
        } catch (e: Exception) {
            if (predicate(e) && i < retries) {
                continue
            } else throw e
        }
    }
    return null
}

fun getIpAddresses(): List<String> {
    val interfaces = NetworkInterface.getNetworkInterfaces().iterator()
    val list = mutableListOf<String>()
    val internalList = mutableListOf<String>()
    interfaces.forEach { netInt ->
        netInt.inetAddresses
            .toList()
            .filterIsInstance<Inet4Address>()
            .forEach { addr ->
                if (addr.isLoopbackAddress) {
                    internalList.add(addr.hostAddress)
                } else {
                    list.add(addr.hostAddress)
                }
            }
    }

    return list.ifEmpty { internalList }
}