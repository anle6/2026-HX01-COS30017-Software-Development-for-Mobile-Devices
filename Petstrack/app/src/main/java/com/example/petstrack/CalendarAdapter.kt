package com.example.petstrack

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapter(private var eventList: List<CalendarEvent>) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val summaryTextView: TextView = itemView.findViewById(R.id.eventSummary)
        val timeTextView: TextView = itemView.findViewById(R.id.eventTime)
        val locationTextView: TextView = itemView.findViewById(R.id.eventLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_event, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val event = eventList[position]
        
        val sharedPref = holder.itemView.context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val textSize = sharedPref.getFloat("text_size", 16f)

        holder.summaryTextView.text = event.summary
        holder.summaryTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize + 2)
        
        holder.timeTextView.text = event.startTime
        holder.timeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize - 2)
        
        holder.locationTextView.text = event.location
        holder.locationTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize - 2)
    }

    override fun getItemCount(): Int = eventList.size

    fun updateEvents(newEvents: List<CalendarEvent>) {
        eventList = newEvents
        notifyDataSetChanged()
    }
}