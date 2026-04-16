package com.example.petstrack

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class NewPetProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    
    private var selectedImageUri: Uri? = null
    private val calendar = Calendar.getInstance()

    private lateinit var petImageView: ImageView
    private lateinit var petNameEditText: TextInputEditText
    private lateinit var petSpeciesEditText: TextInputEditText
    private lateinit var petAgeEditText: TextInputEditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var favoriteFoodEditText: TextInputEditText
    private lateinit var rabiesVaccinatedEditText: TextInputEditText
    private lateinit var cbSterilized: CheckBox
    private lateinit var healthIssuesEditText: TextInputEditText
    private lateinit var tvTitle: TextView
    private lateinit var saveButton: Button

    private var existingPetId: String? = null
    private var existingImageUrl: String? = null

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            petImageView.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_pet_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Initialize UI components
        val btnBack: View = findViewById(R.id.btnBack)
        petImageView = findViewById(R.id.petImageView)
        petNameEditText = findViewById(R.id.petNameEditText)
        petSpeciesEditText = findViewById(R.id.petSpeciesEditText)
        petAgeEditText = findViewById(R.id.petAgeEditText)
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        favoriteFoodEditText = findViewById(R.id.favoriteFoodEditText)
        rabiesVaccinatedEditText = findViewById(R.id.rabiesVaccinatedEditText)
        cbSterilized = findViewById(R.id.cbSterilized)
        healthIssuesEditText = findViewById(R.id.healthIssuesEditText)
        tvTitle = findViewById(R.id.tvNewPetTitle)
        saveButton = findViewById(R.id.savePetButton)

        // Check if we are in Edit Mode
        existingPetId = intent.getStringExtra("PET_ID")
        if (existingPetId != null) {
            tvTitle.text = "Customize Pet Profile"
            saveButton.text = "Save Changes"
            populateFields()
        }

        // Handle back button
        btnBack.setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnSelectImage).setOnClickListener {
            getImage.launch("image/*")
        }

        rabiesVaccinatedEditText.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                calendar.set(year, month, day)
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                rabiesVaccinatedEditText.setText(format.format(calendar.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        saveButton.setOnClickListener {
            savePetProfile()
        }
    }

    private fun populateFields() {
        petNameEditText.setText(intent.getStringExtra("PET_NAME"))
        petSpeciesEditText.setText(intent.getStringExtra("PET_SPECIES"))
        petAgeEditText.setText(intent.getStringExtra("PET_AGE"))
        favoriteFoodEditText.setText(intent.getStringExtra("PET_FOOD"))
        rabiesVaccinatedEditText.setText(intent.getStringExtra("PET_VACCINATED"))
        cbSterilized.isChecked = intent.getBooleanExtra("PET_STERILIZED", false)
        healthIssuesEditText.setText(intent.getStringExtra("PET_HEALTH"))
        
        val gender = intent.getStringExtra("PET_GENDER")
        if (gender == "Male") {
            findViewById<RadioButton>(R.id.rbMale).isChecked = true
        } else if (gender == "Female") {
            findViewById<RadioButton>(R.id.rbFemale).isChecked = true
        }

        existingImageUrl = intent.getStringExtra("PET_IMAGE")
        if (!existingImageUrl.isNullOrEmpty()) {
            petImageView.load(existingImageUrl)
        }
    }

    private fun savePetProfile() {
        val name = petNameEditText.text.toString().trim()
        val species = petSpeciesEditText.text.toString().trim()
        val age = petAgeEditText.text.toString().trim()
        val food = favoriteFoodEditText.text.toString().trim()
        val rabiesDate = rabiesVaccinatedEditText.text.toString().trim()
        val healthIssues = healthIssuesEditText.text.toString().trim()
        val isSterilized = cbSterilized.isChecked
        
        val genderId = genderRadioGroup.checkedRadioButtonId
        val gender = if (genderId == R.id.rbMale) "Male" else if (genderId == R.id.rbFemale) "Female" else "Unknown"

        val userId = auth.currentUser?.uid ?: return

        if (name.isEmpty() || species.isEmpty() || age.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields (*)", Toast.LENGTH_SHORT).show()
            return
        }

        // Only check for duplicate name if creating a NEW profile
        if (existingPetId == null) {
            db.collection("pets")
                .whereEqualTo("ownerId", userId)
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        Toast.makeText(this, "A pet with the name '$name' already exists!", Toast.LENGTH_LONG).show()
                    } else {
                        startSavingProcess(name, species, age, gender, food, rabiesDate, isSterilized, healthIssues, userId)
                    }
                }
        } else {
            // In edit mode, proceed directly to saving
            startSavingProcess(name, species, age, gender, food, rabiesDate, isSterilized, healthIssues, userId)
        }
    }

    private fun startSavingProcess(name: String, species: String, age: String, gender: String, food: String, rabiesDate: String, sterilized: Boolean, issues: String, userId: String) {
        if (selectedImageUri != null) {
            uploadImageAndSave(name, species, age, gender, food, rabiesDate, sterilized, issues, userId)
        } else {
            saveToFirestore(name, species, age, gender, food, rabiesDate, sterilized, issues, existingImageUrl ?: "", userId)
        }
    }

    private fun uploadImageAndSave(name: String, species: String, age: String, gender: String, food: String, rabiesDate: String, sterilized: Boolean, issues: String, userId: String) {
        val ref = storage.reference.child("pet_images/${UUID.randomUUID()}")
        ref.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    saveToFirestore(name, species, age, gender, food, rabiesDate, sterilized, issues, uri.toString(), userId)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveToFirestore(name: String, species: String, age: String, gender: String, food: String, rabiesDate: String, sterilized: Boolean, issues: String, imageUrl: String, userId: String) {
        val pet = hashMapOf(
            "name" to name,
            "species" to species,
            "age" to age,
            "gender" to gender,
            "favoriteFood" to food,
            "rabiesVaccinatedDate" to rabiesDate,
            "isSterilized" to sterilized,
            "healthIssues" to issues,
            "imageUrl" to imageUrl,
            "ownerId" to userId
        )

        val task = if (existingPetId == null) {
            db.collection("pets").add(pet)
        } else {
            db.collection("pets").document(existingPetId!!).set(pet)
        }

        task.addOnSuccessListener {
            val message = if (existingPetId == null) "New profile for $name have been created" else "Profile for $name updated!"
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            finish()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
