package com.example.petstrack

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        auth = FirebaseAuth.getInstance()
        
        val backgroundImageView = findViewById<ImageView>(R.id.backgroundImageView)
        applySavedTheme(backgroundImageView)

        val resetEmailEditText = findViewById<TextInputEditText>(R.id.resetEmailEditText)
        val sendResetButton = findViewById<Button>(R.id.sendResetButton)
        val backToLoginTextView = findViewById<TextView>(R.id.backToLoginTextView)

        sendResetButton.setOnClickListener {
            val email = resetEmailEditText.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show()
                        finish() // Go back to login
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        backToLoginTextView.setOnClickListener {
            finish()
        }
    }

    private fun applySavedTheme(backgroundImageView: ImageView) {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val themeResId = sharedPref.getInt("background_theme", R.drawable.dark_paws)
        val contrast = sharedPref.getFloat("background_contrast", 0.3f)
        
        backgroundImageView.load(themeResId) {
            crossfade(true)
            allowHardware(false)
        }
        backgroundImageView.alpha = contrast
    }
}