package jp.co.goalist.atomicdemo.controllers

import jp.co.goalist.Atomic
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class HelloResponse(val name: String)

@RestController
class HelloController {

    @GetMapping("/hello")
    fun Hello(@RequestParam(value = "name", defaultValue = Atomic.projectName) name: String): HelloResponse {
        return HelloResponse(name = name)
    }
}