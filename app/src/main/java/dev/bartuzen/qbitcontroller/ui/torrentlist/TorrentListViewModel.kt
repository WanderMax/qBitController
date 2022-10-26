package dev.bartuzen.qbitcontroller.ui.torrentlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bartuzen.qbitcontroller.data.SettingsManager
import dev.bartuzen.qbitcontroller.data.TorrentSort
import dev.bartuzen.qbitcontroller.data.repositories.TorrentListRepository
import dev.bartuzen.qbitcontroller.model.ServerConfig
import dev.bartuzen.qbitcontroller.model.Torrent
import dev.bartuzen.qbitcontroller.network.RequestError
import dev.bartuzen.qbitcontroller.network.RequestResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TorrentListViewModel @Inject constructor(
    private val repository: TorrentListRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {
    val torrentList = MutableStateFlow<List<Torrent>?>(null)

    val torrentSort = settingsManager.sortFlow

    private val eventChannel = Channel<Event>()
    val eventFlow = eventChannel.receiveAsFlow()

    val isLoading = MutableStateFlow(true)
    val isRefreshing = MutableStateFlow(false)
    var isInitialLoadStarted = false

    fun updateTorrentList(serverConfig: ServerConfig, torrentSort: TorrentSort? = null) =
        viewModelScope.launch {
            when (
                val result = repository.getTorrentList(
                    serverConfig, torrentSort ?: this@TorrentListViewModel.torrentSort.first()
                )
            ) {
                is RequestResult.Success -> {
                    torrentList.value = result.data
                }
                is RequestResult.Error -> {
                    eventChannel.send(Event.Error(result.error))
                }
            }
        }

    fun deleteTorrents(
        serverConfig: ServerConfig,
        torrentHashes: List<String>,
        deleteFiles: Boolean
    ) = viewModelScope.launch {
        when (
            val result = repository.deleteTorrents(
                serverConfig, torrentHashes.joinToString("|"), deleteFiles
            )
        ) {
            is RequestResult.Success -> {
                eventChannel.send(Event.TorrentsDeleted(torrentHashes.size))
            }
            is RequestResult.Error -> {
                eventChannel.send(Event.Error(result.error))
            }
        }
    }

    fun pauseTorrents(serverConfig: ServerConfig, torrentHashes: List<String>) =
        viewModelScope.launch {
            when (
                val result = repository.pauseTorrents(
                    serverConfig, torrentHashes.joinToString("|")
                )
            ) {
                is RequestResult.Success -> {
                    eventChannel.send(Event.TorrentsPaused(torrentHashes.size))
                }
                is RequestResult.Error -> {
                    eventChannel.send(Event.Error(result.error))
                }
            }
        }

    fun resumeTorrents(serverConfig: ServerConfig, torrentHashes: List<String>) =
        viewModelScope.launch {
            when (
                val result = repository.resumeTorrents(
                    serverConfig, torrentHashes.joinToString("|")
                )
            ) {
                is RequestResult.Success -> {
                    eventChannel.send(Event.TorrentsResumed(torrentHashes.size))
                }
                is RequestResult.Error -> {
                    eventChannel.send(Event.Error(result.error))
                }
            }
        }

    fun setTorrentSort(torrentSort: TorrentSort) = viewModelScope.launch {
        settingsManager.setTorrentSort(torrentSort)
    }

    sealed class Event {
        data class Error(val result: RequestError) : Event()
        data class TorrentsDeleted(val count: Int) : Event()
        data class TorrentsPaused(val count: Int) : Event()
        data class TorrentsResumed(val count: Int) : Event()
    }
}