package com.example.week6login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val username = findViewById<EditText>(R.id.editUsername)
        val password = findViewById<EditText>(R.id.editPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)


        btnLogin.setOnClickListener {

            val userText = username.text.toString().trim()
            val passText = password.text.toString().trim()

            if (userText == "anchuoi3" && passText == "12345678") {

                val intent = Intent(this, AdminActivity::class.java)
                intent.putExtra("USERNAME", userText)

                getResult.launch(intent)

            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
    val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode == RESULT_OK) {
                val message = it.data?.getStringExtra("LOGOUT_MESSAGE")
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }