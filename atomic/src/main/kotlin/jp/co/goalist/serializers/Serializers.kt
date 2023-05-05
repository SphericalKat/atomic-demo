package jp.co.goalist.serializers

import jp.co.goalist.AtomicBroker
import jp.co.goalist.Errors
import java.util.*

class Serializers {
    companion object {
        fun resolve(opt: String, broker: AtomicBroker): BaseSerializer {
            return when (opt.lowercase(Locale.getDefault())) {
                "proto", "protobuf" -> ProtobufSerializer(broker)
                else -> throw Errors.BrokerOptionsException("Invalid serializer type $opt")
            }
        }
    }
}