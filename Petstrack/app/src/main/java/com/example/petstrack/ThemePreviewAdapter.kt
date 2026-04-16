package com.example.petstrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class ThemePreviewAdapter(
    private val themes: List<ThemeItem>,
    private val textSize: Float,
    private val contrast: Float
) : RecyclerView.Adapter<ThemePreviewAdapter.PreviewViewHolder>() {

    class PreviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val background: ImageView = itemView.findViewById(R.id.previewBackgroundImageView)
        val welcomeText: TextView = itemView.findViewById(R.id.previewWelcomeText)
        val themeName: TextView = itemView.findViewById(R.id.previewThemeName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_theme_preview, parent, false)
        return PreviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: PreviewViewHolder, position: Int) {
        val theme = themes[position]
        
        // Use Coil to load and automatically downsample the image
        holder.background.load(theme.resId) {
            crossfade(true)
            allowHardware(false) // Prevents Canvas drawing errors on some devices
        }
        
        holder.background.alpha = contrast
        holder.welcomeText.textSize = textSize
        holder.themeName.text = theme.name
    }

    override fun getItemCount(): Int = themes.size
}