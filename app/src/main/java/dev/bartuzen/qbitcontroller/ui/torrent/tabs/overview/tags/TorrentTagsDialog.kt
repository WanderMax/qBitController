package dev.bartuzen.qbitcontroller.ui.torrent.tabs.overview.tags

import android.os.Bundle
import android.text.TextUtils
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.bartuzen.qbitcontroller.R
import dev.bartuzen.qbitcontroller.databinding.DialogTorrentTagsBinding
import dev.bartuzen.qbitcontroller.model.ServerConfig
import dev.bartuzen.qbitcontroller.ui.torrent.tabs.overview.TorrentOverviewFragment
import dev.bartuzen.qbitcontroller.utils.getParcelableCompat
import dev.bartuzen.qbitcontroller.utils.launchAndCollectLatestIn
import dev.bartuzen.qbitcontroller.utils.setNegativeButton
import dev.bartuzen.qbitcontroller.utils.setPositiveButton
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filterNotNull

class TorrentTagsDialog() : DialogFragment(R.layout.dialog_torrent_tags) {
    private val viewModel: TorrentTagsViewModel by viewModels(ownerProducer = { requireParentFragment() })

    private val serverConfig get() = arguments?.getParcelableCompat<ServerConfig>("serverConfig")!!
    private val currentTags get() = arguments?.getStringArrayList("currentTags")!!

    private val parentFragment get() = (requireParentFragment() as TorrentOverviewFragment)

    constructor(serverConfig: ServerConfig, currentTags: List<String>) : this() {
        arguments = bundleOf(
            "serverConfig" to serverConfig,
            "currentTags" to currentTags
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = MaterialAlertDialogBuilder(requireContext()).apply {
        val binding = DialogTorrentTagsBinding.inflate(layoutInflater)
        setView(binding.root)
        setTitle(R.string.torrent_tags_dialog_title)
        setPositiveButton { _, _ ->
            val selectedTags = binding.chipGroupTags.checkedChipIds.map { id ->
                binding.chipGroupTags.findViewById<Chip>(id).text.toString()
            }
            parentFragment.onTagsDialogResult(selectedTags)
        }
        setNegativeButton()

        viewModel.updateTags(serverConfig)

        viewModel.tags.filterNotNull().launchAndCollectLatestIn(this@TorrentTagsDialog) { tags ->
            tags.forEach { tag ->
                val chip = Chip(requireContext())
                chip.text = tag
                chip.setEnsureMinTouchTargetSize(false)
                chip.setChipBackgroundColorResource(R.color.torrent_tag)
                chip.ellipsize = TextUtils.TruncateAt.END
                chip.isCheckable = true

                if (tag in currentTags) {
                    chip.isChecked = true
                }

                binding.chipGroupTags.addView(chip)
            }

            cancel()
        }

        viewModel.eventFlow.launchAndCollectLatestIn(this@TorrentTagsDialog) { event ->
            when (event) {
                is TorrentTagsViewModel.Event.Error -> {
                    parentFragment.onTagsDialogError(event.result)
                    dismiss()
                }
            }
        }
    }.create()
}
