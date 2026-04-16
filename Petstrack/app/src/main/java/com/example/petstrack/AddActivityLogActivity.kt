package com.example.petstrack

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddActivityLogActivity : AppCompatActivity() {

    private val viewModel: ActivityViewModel by viewModels()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var petSpinner: Spinner
    private lateinit var typeSpinner: Spinner
    private lateinit var noteEditText: EditText
    private lateinit var saveButton: Button

    private var petList: List<Pet> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_activity_log)

        petSpinner = findViewById(R.id.petSpinner)
        typeSpinner = findViewById(R.id.typeSpinner)
        noteEditText = findViewById(R.id.logNoteEditText)
        saveButton = findViewById(R.id.btnSaveLog)

        setupSpinners()
        loadPets()

        saveButton.setOnClickListener {
            saveLog()
        }
    }

    private fun setupSpinners() {
        val types = arrayOf("Walk", "Food", "Bath", "Vet", "Medicine", "Play")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = adapter
    }

    private fun loadPets() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("pets")
            .whereEqualTo("ownerId", userId)
            .get()
            .addOnSuccessListener { documents ->
                petList = documents.map { doc ->
                    Pet(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        species = doc.getString("species") ?: ""
                    )
                }
                val petNames = petList.map { it.name }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, petNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                petSpinner.adapter = adapter
            }
    }

    private fun saveLog() {
        if (petList.isEmpty()) {
            Toast.makeText(this, "No pets found. Please create a pet profile first.", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedPet = petList[petSpinner.selectedItemPosition]
        val selectedType = typeSpinner.selectedItem.toString()
        val note = noteEditText.text.toString()

        val newLog = ActivityLog(
            petId = selectedPet.id,
            petName = selectedPet.name,
            type = selectedType,
            dateTime = System.currentTimeMillis(),
            note = note
        )

        viewModel.addActivity(newLog)
        Toast.makeText(this, "Activity logged!", Toast.LENGTH_SHORT).show()
        finish()
    }
}