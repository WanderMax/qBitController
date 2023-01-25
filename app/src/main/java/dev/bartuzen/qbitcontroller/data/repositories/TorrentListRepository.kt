package dev.bartuzen.qbitcontroller.data.repositories

import dev.bartuzen.qbitcontroller.network.RequestManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TorrentListRepository @Inject constructor(
    private val requestManager: RequestManager
) {
    suspend fun getTorrentList(serverId: Int) = requestManager.request(serverId) { service ->
        service.getTorrentList()
    }

    suspend fun deleteTorrents(serverId: Int, hashes: List<String>, deleteFiles: Boolean) =
        requestManager.request(serverId) { service ->
            service.deleteTorrents(hashes.joinToString("|"), deleteFiles)
        }

    suspend fun pauseTorrents(serverId: Int, hashes: List<String>) = requestManager.request(serverId) { service ->
        service.pauseTorrents(hashes.joinToString("|"))
    }

    suspend fun resumeTorrents(serverId: Int, hashes: List<String>) = requestManager.request(serverId) { service ->
        service.resumeTorrents(hashes.joinToString("|"))
    }

    suspend fun getCategories(serverId: Int) = requestManager.request(serverId) { service ->
        service.getCategories()
    }

    suspend fun getTags(serverId: Int) = requestManager.request(serverId) { service ->
        service.getTags()
    }

    suspend fun deleteCategory(serverId: Int, category: String) = requestManager.request(serverId) { service ->
        service.deleteCategories(category)
    }

    suspend fun deleteTag(serverId: Int, tag: String) = requestManager.request(serverId) { service ->
        service.deleteTags(tag)
    }

    suspend fun increaseTorrentPriority(serverId: Int, hashes: List<String>) = requestManager.request(serverId) { service ->
        service.increaseTorrentPriority(hashes.joinToString("|"))
    }

    suspend fun decreaseTorrentPriority(serverId: Int, hashes: List<String>) = requestManager.request(serverId) { service ->
        service.decreaseTorrentPriority(hashes.joinToString("|"))
    }

    suspend fun maximizeTorrentPriority(serverId: Int, hashes: List<String>) = requestManager.request(serverId) { service ->
        service.maximizeTorrentPriority(hashes.joinToString("|"))
    }

    suspend fun minimizeTorrentPriority(serverId: Int, hashes: List<String>) = requestManager.request(serverId) { service ->
        service.minimizeTorrentPriority(hashes.joinToString("|"))
    }

    suspend fun createCategory(serverId: Int, name: String, savePath: String) = requestManager.request(serverId) { service ->
        service.createCategory(name, savePath)
    }

    suspend fun setLocation(serverId: Int, hashes: List<String>, location: String) =
        requestManager.request(serverId) { service ->
            service.setLocation(hashes.joinToString("|"), location)
        }

    suspend fun editCategory(serverId: Int, name: String, savePath: String) = requestManager.request(serverId) { service ->
        service.editCategory(name, savePath)
    }

    suspend fun createTags(serverId: Int, names: List<String>) = requestManager.request(serverId) { service ->
        service.createTags(names.joinToString(","))
    }
}
