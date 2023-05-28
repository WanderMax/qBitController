package dev.bartuzen.qbitcontroller.ui.rss.editrule

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.bartuzen.qbitcontroller.R
import dev.bartuzen.qbitcontroller.databinding.FragmentEditRssRuleBinding
import dev.bartuzen.qbitcontroller.model.RssRule
import dev.bartuzen.qbitcontroller.utils.getErrorMessage
import dev.bartuzen.qbitcontroller.utils.launchAndCollectIn
import dev.bartuzen.qbitcontroller.utils.launchAndCollectLatestIn
import dev.bartuzen.qbitcontroller.utils.showSnackbar
import dev.bartuzen.qbitcontroller.utils.text
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull

@AndroidEntryPoint
class EditRssRuleFragment() : Fragment(R.layout.fragment_edit_rss_rule) {
    private val binding by viewBinding(FragmentEditRssRuleBinding::bind)

    private val viewModel: EditRssRuleViewModel by viewModels()

    private val serverId get() = arguments?.getInt("serverId", -1).takeIf { it != -1 }!!
    private val ruleName get() = arguments?.getString("ruleName")!!

    constructor(serverId: Int, ruleName: String) : this() {
        arguments = bundleOf(
            "serverId" to serverId,
            "ruleName" to ruleName
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.rss_edit_rule, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.menu_save -> {
                            val newRuleDefinition = constructRuleDefinition()
                            if (newRuleDefinition != null) {
                                viewModel.setRule(serverId, ruleName, newRuleDefinition)
                            }
                        }
                        else -> return false
                    }
                    return true
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )

        if (!viewModel.isInitialLoadStarted) {
            viewModel.isInitialLoadStarted = true
            viewModel.loadData(serverId, ruleName)
        }

        binding.dropdownAddPaused.setItems(
            R.string.rss_rule_use_global_settings,
            R.string.rss_rule_add_paused_always,
            R.string.rss_rule_add_paused_never
        )

        binding.dropdownContentLayout.setItems(
            R.string.rss_rule_use_global_settings,
            R.string.torrent_add_content_layout_original,
            R.string.torrent_add_content_layout_subfolder,
            R.string.torrent_add_content_layout_no_subfolder
        )

        binding.checkboxSavePathEnabled.setOnCheckedChangeListener { _, isChecked ->
            binding.inputLayoutSavePath.isEnabled = isChecked
        }

        if (viewModel.rssRule.value == null) {
            viewModel.rssRule.filterNotNull().launchAndCollectLatestIn(viewLifecycleOwner) { rssRule ->
                binding.checkboxEnabled.isEnabled = true
                binding.checkboxUseRegex.isEnabled = true
                binding.inputLayoutMustContain.isEnabled = true
                binding.inputLayoutMustNotContain.isEnabled = true
                binding.inputLayoutEpisodeFilter.isEnabled = true
                binding.checkboxSmartEpisodeFilter.isEnabled = true
                binding.checkboxSavePathEnabled.isEnabled = true
                binding.inputLayoutSavePath.isEnabled = true
                binding.inputLayoutIgnoreDays.isEnabled = true
                binding.inputLayoutAddPaused.isEnabled = true
                binding.dropdownAddPaused.isEnabled = true
                binding.inputLayoutContentLayout.isEnabled = true
                binding.dropdownContentLayout.isEnabled = true

                binding.checkboxEnabled.isChecked = rssRule.isEnabled
                binding.checkboxUseRegex.isChecked = rssRule.useRegex
                binding.inputLayoutMustContain.text = rssRule.mustContain
                binding.inputLayoutMustNotContain.text = rssRule.mustNotContain
                binding.inputLayoutEpisodeFilter.text = rssRule.episodeFilter
                binding.checkboxSmartEpisodeFilter.isChecked = rssRule.smartFilter
                binding.checkboxSavePathEnabled.isChecked = rssRule.savePath.isNotEmpty()
                binding.inputLayoutSavePath.text = rssRule.savePath
                binding.inputLayoutIgnoreDays.text = rssRule.ignoreDays.toString()
                binding.dropdownAddPaused.setPosition(
                    when (rssRule.addPaused) {
                        null -> 0
                        true -> 1
                        false -> 3
                    }
                )
                binding.dropdownContentLayout.setPosition(
                    when (rssRule.torrentContentLayout) {
                        "Original" -> 1
                        "Subfolder" -> 2
                        "NoSubfolder" -> 3
                        else -> 0
                    }
                )

                cancel()
            }
        }

        combine(viewModel.rssRule, viewModel.categories) { rssRule, categories ->
            if (rssRule != null && categories != null) {
                rssRule to categories
            } else {
                null
            }
        }.filterNotNull().launchAndCollectLatestIn(viewLifecycleOwner) { (rssRule, categories) ->
            val categoryOptions = categories.toMutableList().apply { add(0, "") }
            binding.dropdownCategory.setItems(categoryOptions)

            if (!binding.dropdownCategory.isEnabled) {
                binding.inputLayoutCategory.isEnabled = true
                binding.dropdownCategory.isEnabled = true
                binding.dropdownCategory.setPosition(categoryOptions.indexOf(rssRule.assignedCategory))
            }
        }

        viewModel.eventFlow.launchAndCollectIn(viewLifecycleOwner) { event ->
            when (event) {
                is EditRssRuleViewModel.Event.Error -> {
                    showSnackbar(getErrorMessage(requireContext(), event.error))
                }
                EditRssRuleViewModel.Event.RuleUpdated -> {
                    showSnackbar(R.string.rss_rule_saved_successfully)
                }
                EditRssRuleViewModel.Event.RuleNotFound -> {
                    showSnackbar(R.string.rss_rule_not_found)
                }
            }
        }
    }

    private fun constructRuleDefinition(): RssRule? {
        val categories = viewModel.categories.value ?: return null

        val isEnabled = binding.checkboxEnabled.isChecked
        val mustContain = binding.inputLayoutMustContain.text
        val mustNotContain = binding.inputLayoutMustNotContain.text
        val useRegex = binding.checkboxUseRegex.isChecked
        val episodeFilter = binding.inputLayoutEpisodeFilter.text
        val ignoreDays = binding.inputLayoutIgnoreDays.text.toIntOrNull() ?: 0
        val addPaused = when (binding.dropdownAddPaused.position) {
            1 -> true
            2 -> false
            else -> null
        }
        val category = binding.dropdownCategory.position.let { position ->
            if (position == 0) {
                ""
            } else {
                categories[position - 1]
            }
        }
        val savePath = if (binding.checkboxSavePathEnabled.isChecked) binding.inputLayoutSavePath.text else ""
        val contentLayout = when (binding.dropdownContentLayout.position) {
            1 -> "Original"
            2 -> "Subfolder"
            3 -> "NoSubfolder"
            else -> null
        }
        val smartFilter = binding.checkboxSmartEpisodeFilter.isChecked

        return RssRule(
            isEnabled = isEnabled,
            mustContain = mustContain,
            mustNotContain = mustNotContain,
            useRegex = useRegex,
            episodeFilter = episodeFilter,
            ignoreDays = ignoreDays,
            addPaused = addPaused,
            assignedCategory = category,
            savePath = savePath,
            torrentContentLayout = contentLayout,
            smartFilter = smartFilter
        )
    }
}
