package com.example.petstrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2

class ChangeThemeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private val themes = listOf(
        ThemeItem("Dark Paws", R.drawable.dark_paws, "dark_paws"),
        ThemeItem("Cat Theme", R.drawable.cat_theme, "cat_theme"),
        ThemeItem("Dog Holding Hand", R.drawable.dog_hand_holding, "dog_hand_holding"),
        ThemeItem("Cute Dogs Pattern", R.drawable.cute_pattern_with_dogs, "cute_pattern_with_dogs")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_theme)

        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val currentThemeResId = sharedPref.getInt("background_theme", R.drawable.dark_paws)
        val currentTextSize = sharedPref.getFloat("text_size", 16f)
        val currentContrast = sharedPref.getFloat("background_contrast", 0.3f)

        val toolbar: Toolbar = findViewById(R.id.themeToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        viewPager = findViewById(R.id.themeViewPager)
        val adapter = ThemePreviewAdapter(themes, currentTextSize, currentContrast)
        viewPager.adapter = adapter

        // Set to current theme position
        val currentPosition = themes.indexOfFirst { it.resId == currentThemeResId }.let { if (it == -1) 0 else it }
        viewPager.setCurrentItem(currentPosition, false)

        findViewById<Button>(R.id.btnApplyTheme).setOnClickListener {
            val selectedTheme = themes[viewPager.currentItem]
            saveTheme(selectedTheme.resId)
            
            // Restart MainActivity to apply changes
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun saveTheme(themeResId: Int) {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("background_theme", themeResId)
            apply()
        }
    }
}