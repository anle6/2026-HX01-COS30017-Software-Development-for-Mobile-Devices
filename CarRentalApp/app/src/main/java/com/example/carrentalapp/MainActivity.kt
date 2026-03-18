package com.example.carrentalapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var currentIndex = 0
    private var currentImageIndex = 0
    private lateinit var availableCars: List<Car>

    private val rentResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        refreshAvailableCars()
        
        val startId = intent.getIntExtra("CAR_ID", -1)
        val allCars = CarData.cars
        currentIndex = allCars.indexOfFirst { it.id == startId }.coerceAtLeast(0)

        if (allCars.isEmpty()) {
            Toast.makeText(this, "No cars available", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        displayCar(currentIndex)

        findViewById<Button>(R.id.nextButton).setOnClickListener {
            if (allCars.isNotEmpty()) {
                currentIndex = (currentIndex + 1) % allCars.size
                displayCar(currentIndex)
            }
        }

        findViewById<Button>(R.id.prevButton).setOnClickListener {
            if (allCars.isNotEmpty()) {
                currentIndex = if (currentIndex > 0) currentIndex - 1 else allCars.size - 1
                displayCar(currentIndex)
            }
        }

        findViewById<ImageButton>(R.id.btnHome).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.rentButton).setOnClickListener {
            val car = allCars[currentIndex]
            if (!car.isAvailable) {
                Toast.makeText(this, "This car is already rented!", Toast.LENGTH_SHORT).show()
            } else if (car.dailyCost > 400) {
                Toast.makeText(this, "Rental cost exceeds 400 credit limit!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, RentActivity::class.java)
                intent.putExtra("SELECTED_CAR", car)
                rentResultLauncher.launch(intent)
            }
        }

        findViewById<ImageButton>(R.id.favoriteButton).setOnClickListener {
            val car = allCars[currentIndex]
            val globalCar = CarData.cars.find { it.id == car.id }
            globalCar?.let {
                it.isFavorite = !it.isFavorite
                updateFavoriteIcon(it.isFavorite)
                val status = if (it.isFavorite) "added to" else "removed from"
                Toast.makeText(this, "${it.name} $status favorites", Toast.LENGTH_SHORT).show()
            }
        }

        // Image Gallery Navigation
        findViewById<ImageButton>(R.id.btnNextImage).setOnClickListener {
            val car = allCars[currentIndex]
            currentImageIndex = (currentImageIndex + 1) % car.galleryResIds.size
            updateCarImage(car)
        }

        findViewById<ImageButton>(R.id.btnPrevImage).setOnClickListener {
            val car = allCars[currentIndex]
            currentImageIndex = if (currentImageIndex > 0) currentImageIndex - 1 else car.galleryResIds.size - 1
            updateCarImage(car)
        }

        // Collapsible Specifications
        val btnSpecsToggle = findViewById<Button>(R.id.btnSpecsToggle)
        val tvSpecifications = findViewById<TextView>(R.id.tvSpecifications)
        btnSpecsToggle.setOnClickListener {
            if (tvSpecifications.visibility == View.VISIBLE) {
                tvSpecifications.visibility = View.GONE
                btnSpecsToggle.text = "▼ Technical Specifications"
            } else {
                tvSpecifications.visibility = View.VISIBLE
                btnSpecsToggle.text = "▲ Technical Specifications"
            }
        }
    }

    private fun refreshAvailableCars() {
        availableCars = CarData.getAvailableCars()
    }

    private fun displayCar(index: Int) {
        val allCars = CarData.cars
        if (index < 0 || index >= allCars.size) return
        
        val car = allCars[index]
        currentImageIndex = 0 // Reset gallery to first image
        updateCarImage(car)
        
        findViewById<TextView>(R.id.carName).text = car.name
        findViewById<TextView>(R.id.carDetails).text = "Model: ${car.model} | Year: ${car.year}"
        findViewById<TextView>(R.id.carStats).text = "Kilometers: ${car.kilometers} | Cost: ${car.dailyCost} Credits/day"
        findViewById<RatingBar>(R.id.carRating).rating = car.rating
        
        val tvSpecifications = findViewById<TextView>(R.id.tvSpecifications)
        tvSpecifications.text = car.specifications
        tvSpecifications.visibility = View.GONE
        findViewById<Button>(R.id.btnSpecsToggle).text = "▼ Technical Specifications"

        val rentButton = findViewById<Button>(R.id.rentButton)
        if (!car.isAvailable) {
            rentButton.text = "ALREADY RENTED"
            rentButton.isEnabled = false
            rentButton.alpha = 0.5f
        } else {
            rentButton.text = "RENT THIS CAR"
            rentButton.isEnabled = true
            rentButton.alpha = 1.0f
        }
        
        updateFavoriteIcon(car.isFavorite)
    }

    private fun updateCarImage(car: Car) {
        findViewById<ImageView>(R.id.carImage).setImageResource(car.galleryResIds[currentImageIndex])
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        val icon = if (isFavorite) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off
        findViewById<ImageButton>(R.id.favoriteButton).setImageResource(icon)
    }
}
