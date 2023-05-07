package jp.co.goalist.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext


data class Event(
    val name: String,
    val data: Any
)

class LocalBus(override val coroutineContext: CoroutineContext = Job()) : CoroutineScope {
    private val flows = mutableMapOf<String, MutableSharedFlow<Event>>()

    fun on(eventName: String, callBack: (data: Event) -> Unit) {
        val flow = MutableSharedFlow<Event>(replay = 0)
        flow.onEach { callBack(it) }

        flows[eventName] = flow
    }

    fun emit(eventName: String, data: Any) {
        val flow = flows[eventName]
        runBlocking { flow?.emit(Event(eventName, data)) }
    }
}
