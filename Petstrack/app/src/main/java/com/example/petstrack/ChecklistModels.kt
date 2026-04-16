package com.example.petstrack

import java.util.UUID

data class Checklist(
    val id: String = "",
    val title: String = "",
    val ownerId: String = "",
    val petId: String = "",
    val petName: String = "",
    val items: List<ChecklistItem> = emptyList()
)

data class ChecklistItem(
    val id: String = UUID.randomUUID().toString(),
    val task: String = "",
    var isChecked: Boolean = false
)