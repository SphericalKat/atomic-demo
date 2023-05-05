package jp.co.goalist.atomicdemo

import jp.co.goalist.AtomicBroker
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component


@Component
class AtomicComponent {
	val atomic: AtomicBroker
	init {
	    atomic = AtomicBroker(
			transporter = "amqp://guest:guest@localhost:5672",
			serializer = "protobuf"
		)
		atomic.start()
	}
}

@SpringBootApplication
class AtomicDemoApplication

fun main(args: Array<String>) {
	runApplication<AtomicDemoApplication>(*args)
}
