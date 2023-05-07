package jp.co.goalist.registry

import jp.co.goalist.AtomicBroker
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Registry(val broker: AtomicBroker) {
    init {
        
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(Registry::class.java)
    }
}