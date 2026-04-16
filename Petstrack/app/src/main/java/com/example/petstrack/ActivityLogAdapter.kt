package com.example.petstrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ActivityLogAdapter(
    private var logs: List<ActivityLog>,
    private val onDeleteClick: (ActivityLog) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var weeklyProgress: Int = 0
    private var completedTasksCount: Int = 0
    
    private var totalNotes: Int = 0
    private var latestNoteTime: String = "N/A"
    private var totalNotifications: Int = 0
    private var latestNotificationTime: String = "N/A"

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_LOG = 1
    }

    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.logIcon)
        val type: TextView = itemView.findViewById(R.id.logType)
        val petName: TextView = itemView.findViewById(R.id.logPetName)
        val note: TextView = itemView.findViewById(R.id.logNote)
        val time: TextView = itemView.findViewById(R.id.logTime)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.btnDeleteLog)
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressBar: ProgressBar = itemView.findViewById(R.id.weeklyProgressBar)
        val tvWeeklyCount: TextView = itemView.findViewById(R.id.tvWeeklyCount)
        val tvNotesSummary: TextView = itemView.findViewById(R.id.tvNotesSummary)
        val tvLatestNoteTime: TextView = itemView.findViewById(R.id.tvLatestNoteTime)
        val tvNotificationsSummary: TextView = itemView.findViewById(R.id.tvNotificationsSummary)
        val tvLatestNotificationTime: TextView = itemView.findViewById(R.id.tvLatestNotificationTime)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_LOG
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_status_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_activity_log, parent, false)
            LogViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.progressBar.progress = weeklyProgress
            holder.tvWeeklyCount.text = "$completedTasksCount tasks completed this week"
            
            holder.tvNotesSummary.text = "Total Notes: $totalNotes"
            holder.tvLatestNoteTime.text = "Latest Note at: $latestNoteTime"
            holder.tvNotificationsSummary.text = "Total Reminders Set: $totalNotifications"
            holder.tvLatestNotificationTime.text = "Next Reminder at: $latestNotificationTime"
            
        } else if (holder is LogViewHolder) {
            val log = logs[position - 1]
            holder.type.text = log.type
            holder.petName.text = log.petName
            holder.note.text = log.note
            
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            holder.time.text = format.format(Date(log.dateTime))

            when (log.type.lowercase()) {
                "walk" -> holder.icon.setImageResource(android.R.drawable.ic_menu_directions)
                "food" -> holder.icon.setImageResource(android.R.drawable.ic_menu_set_as)
                "vet" -> holder.icon.setImageResource(android.R.drawable.ic_menu_call)
                else -> holder.icon.setImageResource(android.R.drawable.ic_menu_today)
            }

            holder.deleteBtn.setOnClickListener { onDeleteClick(log) }
        }
    }

    override fun getItemCount(): Int = logs.size + 1

    fun updateLogs(newLogs: List<ActivityLog>) {
        logs = newLogs
        notifyDataSetChanged()
    }

    fun updateWeeklyProgress(completedCount: Int, totalTasks: Int) {
        this.completedTasksCount = completedCount
        this.weeklyProgress = if (totalTasks > 0) (completedCount * 100) / totalTasks else 0
        notifyItemChanged(0)
    }

    fun updateNotesSummary(count: Int, latestTime: String) {
        this.totalNotes = count
        this.latestNoteTime = latestTime
        notifyItemChanged(0)
    }

    fun updateNotificationsSummary(count: Int, nextTime: String) {
        this.totalNotifications = count
        this.latestNotificationTime = nextTime
        notifyItemChanged(0)
    }
}