package com.example.carrentalapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RentedCarsActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var noRentedText: TextView
    private lateinit var searchView: SearchView
    private var currentQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rented_cars)

        container = findViewById(R.id.rentedListContainer)
        noRentedText = findViewById(R.id.noRentedText)
        searchView = findViewById(R.id.rentedSearchView)

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
        var rentedCars = CarData.getRentedCars()

        if (currentQuery.isNotEmpty()) {
            rentedCars = rentedCars.filter { 
                it.name.contains(currentQuery, ignoreCase = true) || 
                it.model.contains(currentQuery, ignoreCase = true)
            }
        }

        if (rentedCars.isEmpty()) {
            noRentedText.visibility = View.VISIBLE
            noRentedText.text = if (currentQuery.isEmpty()) "No cars rented yet." else "No matches found."
        } else {
            noRentedText.visibility = View.GONE
            rentedCars.forEach { car ->
                val itemView = inflater.inflate(R.layout.item_car_preview, container, false)
                itemView.findViewById<ImageView>(R.id.previewImage).setImageResource(car.thumbnailResId)
                itemView.findViewById<TextView>(R.id.previewName).text = car.name
                itemView.findViewById<TextView>(R.id.previewModel).text = car.model
                
                val btnCancel = itemView.findViewById<Button>(R.id.btnCancelRent)
                btnCancel.visibility = View.VISIBLE
                btnCancel.setOnClickListener {
                    CarData.cancelRental(car.id)
                    Toast.makeText(this, "Rental cancelled and refunded!", Toast.LENGTH_SHORT).show()
                    refreshUI()
                }

                itemView.setOnClickListener {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("CAR_ID", car.id)
                    startActivity(intent)
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
