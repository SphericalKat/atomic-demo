package jp.co.goalist.registry.discoverers

import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class LocalDiscoverer(override val coroutineContext: CoroutineContext = Job()) : BaseDiscoverer() {

}