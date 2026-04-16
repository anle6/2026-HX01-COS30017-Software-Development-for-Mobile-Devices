package com.example.petstrack

data class ActivityLog(
    val id: String = "",
    val petId: String = "",
    val petName: String = "",
    val type: String = "", // e.g., Walk, Food, Bath, Vet
    val dateTime: Long = 0,
    val note: String = ""
)