package jp.co.goalist.atomicdemo.controllers

import jp.co.goalist.AtomicBroker
import jp.co.goalist.atomicdemo.AtomicComponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class HelloResponse(val name: String, val nodeID: String)

@RestController
class HelloController @Autowired constructor(val atomicComponent: AtomicComponent) {

    @GetMapping("/hello")
    fun Hello(@RequestParam(value = "name", defaultValue = AtomicBroker.projectName) name: String): HelloResponse {
        return HelloResponse(name = name, nodeID = atomicComponent.atomic.nodeID)
    }
}