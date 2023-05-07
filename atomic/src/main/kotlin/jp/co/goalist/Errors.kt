package jp.co.goalist

class Errors {
    class BrokerOptionsException(message: String): Exception(message)

    class AtomicServerError(message: String): Exception(message)

    class BrokerDisconnectedError(): Exception("Broker is disconnected")
}
