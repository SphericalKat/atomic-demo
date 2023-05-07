package jp.co.goalist.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext


data class Event(
    val data: Any
)

class LocalBus(override val coroutineContext: CoroutineContext = Job()) : CoroutineScope {
    private val flows = mutableMapOf<String, MutableSharedFlow<Event>>()

    fun on(eventName: String, callBack: (data: Event) -> Unit) {
        val existingFlow = flows[eventName]
        if (existingFlow == null) {
            val flow = MutableSharedFlow<Event>(replay = 0)
            flow.onEach { callBack(it) }.launchIn(this)

            flows[eventName] = flow
            logger.info("Registered new event $eventName")
        } else {
            existingFlow.onEach { callBack(it) }.launchIn(this)
            logger.info("Registered new event handler on existing event $eventName")
        }
    }

    fun emit(eventName: String, data: Any) {
        val flow = flows[eventName]
        runBlocking { flow?.emit(Event(data)) }
        logger.info("Emitted event $eventName")
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(LocalBus::class.java)
    }
}
