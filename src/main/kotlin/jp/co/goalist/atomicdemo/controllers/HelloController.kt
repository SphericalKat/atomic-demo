package jp.co.goalist.atomicdemo.controllers

import jp.co.goalist.AtomicBroker
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class HelloResponse(val name: String, val nodeID: String)

@RestController
class HelloController {

    @GetMapping("/hello")
    fun Hello(@RequestParam(value = "name", defaultValue = AtomicBroker.projectName) name: String): HelloResponse {
        val atomic = AtomicBroker(
            transporter = "amqp://guest:guest@localhost:5672"
        )

        return HelloResponse(name = name, nodeID = atomic.nodeID)
    }
}