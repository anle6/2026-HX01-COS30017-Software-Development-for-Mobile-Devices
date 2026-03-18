package com.example.carrentalapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FavoritesActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var noFavoritesText: TextView
    private lateinit var searchView: SearchView
    private var currentQuery: String = ""

    private val detailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        refreshUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorites)

        container = findViewById(R.id.favoritesListContainer)
        noFavoritesText = findViewById(R.id.noFavoritesText)
        searchView = findViewById(R.id.favoritesSearchView)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.btnHome).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText ?: ""
                refreshUI()
                return true
            }
        })

        refreshUI()
    }

    private fun refreshUI() {
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)
        var favorites = CarData.cars.filter { it.isFavorite }

        if (currentQuery.isNotEmpty()) {
            favorites = favorites.filter { 
                it.name.contains(currentQuery, ignoreCase = true) || 
                it.model.contains(currentQuery, ignoreCase = true)
            }
        }

        if (favorites.isEmpty()) {
            noFavoritesText.visibility = View.VISIBLE
            noFavoritesText.text = if (currentQuery.isEmpty()) "No favorite cars yet." else "No matches found."
        } else {
            noFavoritesText.visibility = View.GONE
            favorites.forEach { car ->
                val itemView = inflater.inflate(R.layout.item_car_preview, container, false)
                itemView.findViewById<ImageView>(R.id.previewImage).setImageResource(car.thumbnailResId)
                itemView.findViewById<TextView>(R.id.previewName).text = car.name
                itemView.findViewById<TextView>(R.id.previewModel).text = car.model
                
                itemView.findViewById<ImageView>(R.id.previewFavoriteIcon).visibility = View.VISIBLE

                itemView.setOnClickListener {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("CAR_ID", car.id)
                    detailLauncher.launch(intent)
                }
                container.addView(itemView)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshUI()
    }
}
