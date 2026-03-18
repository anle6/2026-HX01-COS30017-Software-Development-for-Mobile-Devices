package com.example.carrentalapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.slider.Slider
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RentActivity : AppCompatActivity() {

    private var rentalDays = 1
    private var totalCost = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rent)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val car = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("SELECTED_CAR", Car::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("SELECTED_CAR")
        } ?: return

        findViewById<ImageView>(R.id.iv_rent_car_image).setImageResource(car.thumbnailResId)
        findViewById<TextView>(R.id.tv_rent_car_title).text = "${car.name} (${car.year})"
        findViewById<TextView>(R.id.userBalanceValue).text = "${CarData.creditBalance} Credits"

        val tvDaysLabel = findViewById<TextView>(R.id.tv_rent_days_label)
        val tvTotalCost = findViewById<TextView>(R.id.tv_rent_total_cost)
        val slider = findViewById<Slider>(R.id.slider_rent_days)

        fun updateCost() {
            totalCost = car.dailyCost * rentalDays
            tvDaysLabel.text = "Rental Duration: $rentalDays ${if (rentalDays > 1) "Days" else "Day"}"
            tvTotalCost.text = "Total Cost: $totalCost Credits"
        }

        updateCost()

        slider.addOnChangeListener { _, value, _ ->
            rentalDays = value.toInt()
            updateCost()
        }

        findViewById<ImageButton>(R.id.btnHome).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        val saveButton = findViewById<Button>(R.id.btn_save_booking)
        val errorText = findViewById<TextView>(R.id.errorText)

        saveButton.setOnClickListener {
            if (CarData.creditBalance >= totalCost) {
                // Perform Booking
                CarData.creditBalance -= totalCost
                
                // Add to Purchase History
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                CarData.purchaseHistory.add(Transaction(
                    carId = car.id,
                    carName = "${car.name} ($rentalDays days)",
                    cost = totalCost,
                    type = "SPENT",
                    date = date
                ))
                
                // Update the original car object in our global list
                CarData.cars.find { it.id == car.id }?.isAvailable = false
                
                Toast.makeText(this, "Booking Successful for ${car.name}!", Toast.LENGTH_LONG).show()
                setResult(RESULT_OK)
                finish()
            } else {
                errorText.text = "Insufficient balance. Need $totalCost, have ${CarData.creditBalance}."
                errorText.visibility = View.VISIBLE
            }
        }
    }
}
