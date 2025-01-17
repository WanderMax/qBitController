package dev.bartuzen.qbitcontroller.model.deserializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import dev.bartuzen.qbitcontroller.model.TorrentFilePriority

class TorrentFilePriorityDeserializer : JsonDeserializer<TorrentFilePriority>() {
    override fun deserialize(parser: JsonParser?, context: DeserializationContext?): TorrentFilePriority {
        val priorityId = parser?.valueAsInt
        // Seems like older versions of qBittorrent use 4 as normal priority
        if (priorityId == 4) {
            return TorrentFilePriority.NORMAL
        }
        return TorrentFilePriority.values().find { it.id == priorityId }
            ?: throw IllegalArgumentException("Unknown priority: $priorityId")
    }
}
