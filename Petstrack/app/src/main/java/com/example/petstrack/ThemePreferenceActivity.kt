package com.example.petstrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.google.android.material.slider.Slider

class ThemePreferenceActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private val themes = listOf(
        ThemeItem("Dark Paws", R.drawable.dark_paws, "dark_paws"),
        ThemeItem("Cat Theme", R.drawable.cat_theme, "cat_theme"),
        ThemeItem("Dog Holding Hand", R.drawable.dog_hand_holding, "dog_hand_holding"),
        ThemeItem("Cute Dogs Pattern", R.drawable.cute_pattern_with_dogs, "cute_pattern_with_dogs")
    )

    private var selectedTextSize: Float = 16f
    private var selectedContrast: Float = 0.3f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_preference)

        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val currentThemeResId = sharedPref.getInt("background_theme", R.drawable.dark_paws)
        selectedTextSize = sharedPref.getFloat("text_size", 16f)
        selectedContrast = sharedPref.getFloat("background_contrast", 0.3f)

        val toolbar: Toolbar = findViewById(R.id.themeToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val backgroundImageView = findViewById<ImageView>(R.id.prefBackgroundImageView)
        applySavedTheme(backgroundImageView)

        // ViewPager for Theme selection
        viewPager = findViewById(R.id.themeViewPager)
        val adapter = ThemePreviewAdapter(themes, selectedTextSize, selectedContrast)
        viewPager.adapter = adapter
        val currentPosition = themes.indexOfFirst { it.resId == currentThemeResId }.let { if (it == -1) 0 else it }
        viewPager.setCurrentItem(currentPosition, false)

        // Text Size Slider
        val textSizeSlider: Slider = findViewById(R.id.textSizeSlider)
        textSizeSlider.value = selectedTextSize.coerceIn(12f, 32f)
        textSizeSlider.addOnChangeListener { _, value, _ ->
            selectedTextSize = value
            updatePreview()
        }

        // Contrast Slider
        val contrastSlider: Slider = findViewById(R.id.contrastSlider)
        contrastSlider.value = selectedContrast.coerceIn(0.1f, 1.0f)
        contrastSlider.addOnChangeListener { _, value, _ ->
            selectedContrast = value
            updatePreview()
        }

        findViewById<Button>(R.id.btnApplyPreferences).setOnClickListener {
            val selectedTheme = themes[viewPager.currentItem]
            saveAllPreferences(selectedTheme.resId)
            
            // Restart MainActivity to apply global changes
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun updatePreview() {
        val adapter = ThemePreviewAdapter(themes, selectedTextSize, selectedContrast)
        val currentPos = viewPager.currentItem
        viewPager.adapter = adapter
        viewPager.setCurrentItem(currentPos, false)
    }

    private fun applySavedTheme(backgroundImageView: ImageView) {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val themeResId = sharedPref.getInt("background_theme", R.drawable.dark_paws)
        backgroundImageView.load(themeResId)
        backgroundImageView.alpha = selectedContrast
    }

    private fun saveAllPreferences(themeResId: Int) {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("background_theme", themeResId)
            putFloat("text_size", selectedTextSize)
            putFloat("background_contrast", selectedContrast)
            apply()
        }
    }
}