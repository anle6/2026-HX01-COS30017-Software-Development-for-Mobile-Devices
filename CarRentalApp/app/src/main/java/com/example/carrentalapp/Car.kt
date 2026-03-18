package com.example.carrentalapp

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Car(
    val id: Int,
    val name: String,
    val model: String,
    val year: Int,
    val rating: Float,
    val kilometers: Int,
    val dailyCost: Int,
    @DrawableRes val thumbnailResId: Int, // Specifically for the preview/thumbnail
    val galleryResIds: List<Int>,         // Specifically for the showcase gallery
    val specifications: String,
    var isAvailable: Boolean = true,
    var isFavorite: Boolean = false
) : Parcelable
