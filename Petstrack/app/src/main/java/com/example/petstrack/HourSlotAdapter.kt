package com.example.petstrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class HourSlotAdapter(
    private val hours: List<String>,
    private var notes: List<CalendarNote> = emptyList(),
    private var selectedDate: Calendar = Calendar.getInstance()
) : RecyclerView.Adapter<HourSlotAdapter.HourViewHolder>() {

    class HourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHour: TextView = itemView.findViewById(R.id.tvHour)
        val tvNoteContent: TextView = itemView.findViewById(R.id.tvNoteContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hour_slot, parent, false)
        return HourViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        val hourText = hours[position]
        holder.tvHour.text = hourText
        
        // Find notes for this specific hour on the selected date
        val hourInt = hourText.split(":")[0].toInt()
        
        val noteForThisHour = notes.find { note ->
            val cal = Calendar.getInstance().apply { timeInMillis = note.timestamp }
            cal.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
            cal.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR) &&
            cal.get(Calendar.HOUR_OF_DAY) == hourInt
        }

        if (noteForThisHour != null) {
            holder.tvNoteContent.visibility = View.VISIBLE
            holder.tvNoteContent.text = "${noteForThisHour.title}: ${noteForThisHour.content}"
        } else {
            holder.tvNoteContent.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = hours.size

    fun updateData(newNotes: List<CalendarNote>, date: Calendar) {
        this.notes = newNotes
        this.selectedDate = date
        notifyDataSetChanged()
    }
}