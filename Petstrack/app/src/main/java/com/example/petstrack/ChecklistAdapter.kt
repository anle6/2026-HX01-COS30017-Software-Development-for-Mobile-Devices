package com.example.petstrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChecklistAdapter(
    private var checklists: List<Checklist>,
    private val onChecklistDelete: (Checklist) -> Unit,
    private val onItemToggle: (Checklist, ChecklistItem) -> Unit
) : RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder>() {

    class ChecklistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.checklistTitle)
        val petName: TextView = itemView.findViewById(R.id.tvChecklistPetName)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.btnDeleteChecklist)
        val itemsContainer: LinearLayout = itemView.findViewById(R.id.itemsContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checklist, parent, false)
        return ChecklistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        val checklist = checklists[position]
        holder.title.text = checklist.title
        holder.petName.text = if (checklist.petName.isNotEmpty()) "For: ${checklist.petName}" else "General"
        holder.deleteBtn.setOnClickListener { onChecklistDelete(checklist) }

        holder.itemsContainer.removeAllViews()
        checklist.items.forEach { item ->
            val itemView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.item_checklist_task, holder.itemsContainer, false)
            val checkBox = itemView.findViewById<CheckBox>(R.id.taskCheckBox)
            checkBox.text = item.task
            checkBox.isChecked = item.isChecked
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
                onItemToggle(checklist, item)
            }
            holder.itemsContainer.addView(itemView)
        }
    }

    override fun getItemCount(): Int = checklists.size

    fun updateData(newList: List<Checklist>) {
        checklists = newList
        notifyDataSetChanged()
    }
}
