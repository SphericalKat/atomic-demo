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