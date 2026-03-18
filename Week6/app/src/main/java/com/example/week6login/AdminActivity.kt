package com.example.week6login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_page)

        val welcomeText = findViewById<TextView>(R.id.txtWelcome)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val username = intent.getStringExtra("USERNAME")

        welcomeText.text = "Welcome, $username"

        btnLogout.setOnClickListener {

            val resultIntent = Intent()
            resultIntent.putExtra("LOGOUT_MESSAGE", "Logged out successfully")

            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
