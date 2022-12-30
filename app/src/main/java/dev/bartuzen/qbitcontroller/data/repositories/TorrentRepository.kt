package dev.bartuzen.qbitcontroller.data.repositories

import dev.bartuzen.qbitcontroller.model.ServerConfig
import dev.bartuzen.qbitcontroller.model.TorrentFilePriority
import dev.bartuzen.qbitcontroller.network.RequestManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TorrentRepository @Inject constructor(
    private val requestManager: RequestManager
) {
    suspend fun getTorrent(serverConfig: ServerConfig, hash: String) = requestManager.request(serverConfig) { service ->
        service.getTorrentList(hash)
    }

    suspend fun getFiles(serverConfig: ServerConfig, hash: String) = requestManager.request(serverConfig) { service ->
        service.getFiles(hash)
    }

    suspend fun getPieces(serverConfig: ServerConfig, hash: String) = requestManager.request(serverConfig) { service ->
        service.getTorrentPieces(hash)
    }

    suspend fun getProperties(serverConfig: ServerConfig, hash: String) = requestManager.request(serverConfig) { service ->
        service.getTorrentProperties(hash)
    }

    suspend fun deleteTorrent(serverConfig: ServerConfig, hash: String, deleteFiles: Boolean) =
        requestManager.request(serverConfig) { service ->
            service.deleteTorrents(hash, deleteFiles)
        }

    suspend fun pauseTorrent(serverConfig: ServerConfig, hash: String) = requestManager.request(serverConfig) { service ->
        service.pauseTorrents(hash)
    }

    suspend fun resumeTorrent(serverConfig: ServerConfig, hash: String) = requestManager.request(serverConfig) { service ->
        service.resumeTorrents(hash)
    }

    suspend fun getTrackers(serverConfig: ServerConfig, hash: String) = requestManager.request(serverConfig) { service ->
        service.getTorrentTrackers(hash)
    }

    suspend fun addTrackers(serverConfig: ServerConfig, hash: String, urls: List<String>) =
        requestManager.request(serverConfig) { service ->
            service.addTorrentTrackers(hash, urls.joinToString("\n"))
        }

    suspend fun deleteTrackers(serverConfig: ServerConfig, hash: String, urls: List<String>) =
        requestManager.request(serverConfig) { service ->
            service.deleteTorrentTrackers(hash, urls.joinToString("|"))
        }

    suspend fun toggleSequentialDownload(serverConfig: ServerConfig, hash: String) =
        requestManager.request(serverConfig) { service ->
            service.toggleSequentialDownload(hash)
        }

    suspend fun togglePrioritizeFirstLastPiecesDownload(serverConfig: ServerConfig, hash: String) =
        requestManager.request(serverConfig) { service ->
            service.togglePrioritizeFirstLastPiecesDownload(hash)
        }

    suspend fun setAutomaticTorrentManagement(serverConfig: ServerConfig, hash: String, enable: Boolean) =
        requestManager.request(serverConfig) { service ->
            service.setAutomaticTorrentManagement(hash, enable)
        }

    suspend fun setDownloadSpeedLimit(serverConfig: ServerConfig, hash: String, limit: Int) =
        requestManager.request(serverConfig) { service ->
            service.setDownloadSpeedLimit(hash, limit)
        }

    suspend fun setUploadSpeedLimit(serverConfig: ServerConfig, hash: String, limit: Int) =
        requestManager.request(serverConfig) { service ->
            service.setUploadSpeedLimit(hash, limit)
        }

    suspend fun setForceStart(serverConfig: ServerConfig, hash: String, value: Boolean) =
        requestManager.request(serverConfig) { service ->
            service.setForceStart(hash, value)
        }

    suspend fun setSuperSeeding(serverConfig: ServerConfig, hash: String, value: Boolean) =
        requestManager.request(serverConfig) { service ->
            service.setSuperSeeding(hash, value)
        }

    suspend fun recheckTorrent(serverConfig: ServerConfig, hash: String) = requestManager.request(serverConfig) { service ->
        service.recheckTorrents(hash)
    }

    suspend fun reannounceTorrent(serverConfig: ServerConfig, hash: String) =
        requestManager.request(serverConfig) { service ->
            service.reannounceTorrents(hash)
        }

    suspend fun renameTorrent(serverConfig: ServerConfig, hash: String, name: String) =
        requestManager.request(serverConfig) { service ->
            service.renameTorrent(hash, name)
        }

    suspend fun setFilePriority(serverConfig: ServerConfig, hash: String, ids: List<Int>, priority: TorrentFilePriority) =
        requestManager.request(serverConfig) { service ->
            service.setFilePriority(hash, ids.joinToString("|"), priority.id)
        }

    suspend fun renameFile(serverConfig: ServerConfig, hash: String, file: String, newName: String) =
        requestManager.request(serverConfig) { service ->
            service.renameFile(hash, file, newName)
        }

    suspend fun renameFolder(serverConfig: ServerConfig, hash: String, folder: String, newName: String) =
        requestManager.request(serverConfig) { service ->
            service.renameFolder(hash, folder, newName)
        }

    suspend fun getCategories(serverConfig: ServerConfig) = requestManager.request(serverConfig) { service ->
        service.getCategories()
    }

    suspend fun setCategory(serverConfig: ServerConfig, hash: String, category: String?) =
        requestManager.request(serverConfig) { service ->
            service.setCategory(hash, category ?: "")
        }

    suspend fun getTags(serverConfig: ServerConfig) = requestManager.request(serverConfig) { service ->
        service.getTags()
    }

    suspend fun addTags(serverConfig: ServerConfig, hash: String, tags: List<String>) =
        requestManager.request(serverConfig) { service ->
            service.addTags(hash, tags.joinToString(","))
        }

    suspend fun removeTags(serverConfig: ServerConfig, hash: String, tags: List<String>) =
        requestManager.request(serverConfig) { service ->
            service.removeTags(hash, tags.joinToString(","))
        }
}
