package dev.bartuzen.qbitcontroller.model.deserializers

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.bartuzen.qbitcontroller.model.Category
import dev.bartuzen.qbitcontroller.model.MainData
import dev.bartuzen.qbitcontroller.model.ServerState
import dev.bartuzen.qbitcontroller.model.Torrent

fun parseMainData(mainData: String): MainData {
    val mapper = jacksonObjectMapper()
        .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    val mainDataNode = mapper.readTree(mainData)

    val serverState = mapper.treeToValue(mainDataNode["server_state"], ServerState::class.java)

    val torrents = mutableListOf<Torrent>()
    mainDataNode["torrents"]?.fields()?.forEach { (hash, torrentNode) ->
        (torrentNode as ObjectNode).put("hash", hash)
        torrents.add(mapper.treeToValue(torrentNode, Torrent::class.java))
    }

    val categories = mainDataNode["categories"]?.map { node ->
        Category(
            name = node["name"].asText(),
            savePath = node["savePath"].asText()
        )
    }?.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }) ?: emptyList()

    val tags = mainDataNode["tags"]?.map { node ->
        node.asText()
    }?.sortedWith(String.CASE_INSENSITIVE_ORDER) ?: emptyList()

    return MainData(
        serverState = serverState,
        torrents = torrents,
        categories = categories,
        tags = tags
    )
}
