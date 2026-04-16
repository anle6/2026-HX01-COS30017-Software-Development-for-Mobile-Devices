package com.example.petstrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        
        // Apply theme preferences
        val backgroundImageView = findViewById<ImageView>(R.id.backgroundImageView)
        applySavedPreferences(backgroundImageView)

        val emailEditText = findViewById<TextInputEditText>(R.id.signUpEmailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.signUpPasswordEditText)
        val confirmPasswordEditText = findViewById<TextInputEditText>(R.id.confirmPasswordEditText)
        val termsCheckBox = findViewById<CheckBox>(R.id.termsCheckBox)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val loginTextView = findViewById<TextView>(R.id.loginTextView)

        termsCheckBox.setOnCheckedChangeListener { _, isChecked ->
            signUpButton.isEnabled = isChecked
        }

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (!termsCheckBox.isChecked) {
                Toast.makeText(this, "Please agree to the terms and service", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finishAffinity() // Close all previous activities
                    } else {
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        loginTextView.setOnClickListener {
            finish()
        }
    }

    private fun applySavedPreferences(backgroundImageView: ImageView) {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val themeResId = sharedPref.getInt("background_theme", R.drawable.cute_pattern_with_dogs)
        val contrast = sharedPref.getFloat("background_contrast", 0.3f)
        val textSize = sharedPref.getFloat("text_size", 16f)
        
        backgroundImageView.load(themeResId) {
            crossfade(true)
            allowHardware(false)
        }
        backgroundImageView.alpha = contrast

        // Apply text size to title elements
        findViewById<TextView>(R.id.loginTextView).textSize = textSize
    }
}