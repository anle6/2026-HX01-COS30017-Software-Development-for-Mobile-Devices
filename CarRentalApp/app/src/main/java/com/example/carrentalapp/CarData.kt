package com.example.carrentalapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val carId: Int? = null,
    val carName: String,
    val cost: Int,
    val type: String, // "SPENT" or "GAINED"
    val date: String,
    var isCancelled: Boolean = false
) : Parcelable

object CarData {
    var initialBalance = 500
    var creditBalance = 500
    val purchaseHistory = mutableListOf<Transaction>()

    val cars = mutableListOf(
        Car(1, "Können", "Quarts R", 2013, 4.9f, 1200, 150, 
            R.drawable.car_quarts,
            listOf(R.drawable.quarts_front, R.drawable.quarts_back, R.drawable.quarts_left, R.drawable.quarts_right), 
            "Engine: 5.0L V8\nHorsepower: 1140hp\nTop Speed: 440 km/h\n0-100 km/h: 2.8s"),
        Car(2, "Blair", "OP-3", 2013, 4.7f, 3400, 120, 
            R.drawable.car_blair,
            listOf(R.drawable.blair_front, R.drawable.blair_back, R.drawable.blair_left, R.drawable.blair_right), 
            "Engine: 3.8L V8 Twin-Turbo\nHorsepower: 903hp\nTop Speed: 350 km/h\n0-100 km/h: 2.8s"),
        Car(3, "Venstaior", "Poison SP", 2011, 4.8f, 5600, 140, 
            R.drawable.car_poison,
            listOf(R.drawable.poison_front, R.drawable.poison_back, R.drawable.poison_left, R.drawable.poison_right),
            "Engine: 6.2L V8 Supercharged\nHorsepower: 1244hp\nTop Speed: 435 km/h\n0-100 km/h: 2.7s"),
        Car(4, "Können", "Sento Deadly Arrow", 2026, 5.0f, 100, 180, 
            R.drawable.car_sento,
            listOf(R.drawable.sento_front, R.drawable.sento_back, R.drawable.sento_left, R.drawable.sento_right), 
            "Engine: 5.1L V8 Twin-Turbo\nHorsepower: 1600hp\nTop Speed: 480+ km/h\n0-100 km/h: 2.5s"),
        Car(5, "Hollow", "IV Orange Snake", 2019, 4.6f, 8900, 110, 
            R.drawable.car_snake,
            listOf(R.drawable.snake_front, R.drawable.snake_back, R.drawable.snake_left, R.drawable.snake_right), 
            "Engine: 6.3L V12\nHorsepower: 789hp\nTop Speed: 350 km/h\n0-100 km/h: 2.8s")
    )

    fun getAvailableCars() = cars.filter { it.isAvailable }
    fun getRentedCars() = cars.filter { !it.isAvailable }
    
    fun getCreditsSpent() = purchaseHistory.filter { it.type == "SPENT" && !it.isCancelled }.sumOf { it.cost }
    fun getCreditsGained() = initialBalance + purchaseHistory.filter { it.type == "GAINED" }.sumOf { it.cost }

    fun addCredits(amount: Int) {
        creditBalance += amount
        purchaseHistory.add(Transaction(
            carName = "Credit Top-up",
            cost = amount,
            type = "GAINED",
            date = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        ))
    }

    fun cancelRental(carId: Int) {
        val car = cars.find { it.id == carId }
        if (car != null && !car.isAvailable) {
            car.isAvailable = true
            // Find the active transaction for this car
            val transaction = purchaseHistory.find { it.carId == carId && !it.isCancelled }
            transaction?.let {
                it.isCancelled = true
                creditBalance += it.cost // Refund credits
                purchaseHistory.add(Transaction(
                    carName = "Refund: ${car.name}",
                    cost = it.cost,
                    type = "GAINED",
                    date = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                ))
            }
        }
    }
}
