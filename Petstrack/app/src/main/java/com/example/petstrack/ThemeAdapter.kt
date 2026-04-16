package com.example.petstrack

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class ThemeAdapter(
    private val themes: List<ThemeItem>,
    private var selectedPosition: Int = -1,
    private val onThemeSelected: (ThemeItem) -> Unit
) : RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder>() {

    class ThemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView as MaterialCardView
        val preview: ImageView = itemView.findViewById(R.id.themePreviewImageView)
        val name: TextView = itemView.findViewById(R.id.themeNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_theme, parent, false)
        return ThemeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        val theme = themes[position]
        holder.preview.setImageResource(theme.resId)
        holder.name.text = theme.name
        
        // Highlight selected theme
        if (selectedPosition == position) {
            holder.card.strokeWidth = 8
            holder.card.setStrokeColor(Color.parseColor("#800080")) // Purple
        } else {
            holder.card.strokeWidth = 0
        }

        holder.itemView.setOnClickListener { 
            val oldPos = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(oldPos)
            notifyItemChanged(selectedPosition)
            onThemeSelected(theme) 
        }
    }

    override fun getItemCount(): Int = themes.size
}