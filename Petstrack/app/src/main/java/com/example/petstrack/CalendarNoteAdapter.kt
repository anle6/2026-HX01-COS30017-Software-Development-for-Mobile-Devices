package com.example.petstrack

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CalendarNoteAdapter(
    private var noteList: List<CalendarNote>,
    private val onEditClick: (CalendarNote) -> Unit = {},
    private val onDeleteClick: (CalendarNote) -> Unit = {}
) : RecyclerView.Adapter<CalendarNoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.noteTitle)
        val contentTextView: TextView = itemView.findViewById(R.id.noteContent)
        val timeTextView: TextView = itemView.findViewById(R.id.noteTime)
        val btnEditNote: ImageView = itemView.findViewById(R.id.btnEditNote)
        val btnDeleteNote: ImageView = itemView.findViewById(R.id.btnDeleteNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = noteList[position]
        
        val sharedPref = holder.itemView.context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val textSize = sharedPref.getFloat("text_size", 16f)

        holder.titleTextView.text = note.title
        holder.titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize + 2)
        
        holder.contentTextView.text = note.content
        holder.contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
        
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.timeTextView.text = format.format(Date(note.timestamp))
        holder.timeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize - 4)

        holder.btnEditNote.setOnClickListener {
            onEditClick(note)
        }

        holder.btnDeleteNote.setOnClickListener {
            onDeleteClick(note)
        }
    }

    override fun getItemCount(): Int = noteList.size

    fun updateNotes(newNotes: List<CalendarNote>) {
        noteList = newNotes
        notifyDataSetChanged()
    }
}