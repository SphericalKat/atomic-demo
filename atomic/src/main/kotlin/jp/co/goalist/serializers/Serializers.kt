package jp.co.goalist.serializers

import jp.co.goalist.AtomicBroker
import jp.co.goalist.Errors
import java.util.*

class Serializers {
    companion object {
        fun resolve(opt: String, broker: AtomicBroker): BaseSerializer {
            if (opt.lowercase(Locale.getDefault()) == "json") {
                return JSONSerializer(broker)
            }

            throw Errors.BrokerOptionsException("Invalid serializer type $opt")
        }
    }
}