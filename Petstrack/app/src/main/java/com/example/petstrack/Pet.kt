package com.example.petstrack

data class Pet(
    val id: String = "",
    val name: String = "",
    val species: String = "",
    val age: String = "",
    val gender: String = "",
    val favoriteFood: String = "",
    val rabiesVaccinatedDate: String = "",
    val isSterilized: Boolean = false,
    val healthIssues: String = "",
    val imageUrl: String = "",
    val ownerId: String = ""
)