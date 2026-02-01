package com.example.week3

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var btnLogin: Button
    lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        btnLogin = findViewById(R.id.login)
        btnCancel = findViewById(R.id.cancel)

        btnLogin.setOnClickListener(this)
        btnCancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.login -> {
                val userText = username.text.toString()
                val passText = password.text.toString()
            }
            R.id.cancel -> {
                username.text.clear()
                password.text.clear()
            }
        }
    }
}
