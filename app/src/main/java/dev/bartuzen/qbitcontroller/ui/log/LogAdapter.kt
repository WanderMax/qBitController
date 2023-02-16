package dev.bartuzen.qbitcontroller.ui.log

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import dev.bartuzen.qbitcontroller.R
import dev.bartuzen.qbitcontroller.databinding.ItemLogBinding
import dev.bartuzen.qbitcontroller.model.Log
import dev.bartuzen.qbitcontroller.model.LogType
import dev.bartuzen.qbitcontroller.utils.formatDate
import dev.bartuzen.qbitcontroller.utils.getColorCompat

class LogAdapter : RecyclerView.Adapter<LogAdapter.ViewHolder>() {
    private var logs: List<Log> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(logs[position])
    }

    override fun getItemCount() = logs.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitLogs(logs: List<Log>) {
        this.logs = logs
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(log: Log) {
            val context = binding.root.context

            val logColor = when (log.type) {
                LogType.NORMAL -> {
                    MaterialColors.getColor(context, android.R.attr.textColorPrimary, Color.TRANSPARENT)
                }
                LogType.INFO -> {
                    context.getColorCompat(R.color.log_info)
                }
                LogType.WARNING -> {
                    context.getColorCompat(R.color.log_warning)
                }
                LogType.CRITICAL -> {
                    context.getColorCompat(R.color.log_critical)
                }
            }

            binding.textLog.text = SpannableStringBuilder()
                .append(formatDate(log.timestamp), ForegroundColorSpan(context.getColorCompat(R.color.log_timestamp)), 0)
                .append(" - ")
                .append(log.message, ForegroundColorSpan(logColor), 0)
        }
    }
}
