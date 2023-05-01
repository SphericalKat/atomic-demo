package jp.co.goalist.transport

abstract class Transport {
    private var prefix: String = "ATOM"

    abstract fun connect()
}