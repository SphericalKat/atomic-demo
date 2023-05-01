package jp.co.goalist.atomicdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AtomicDemoApplication

fun main(args: Array<String>) {
	runApplication<AtomicDemoApplication>(*args)
}
