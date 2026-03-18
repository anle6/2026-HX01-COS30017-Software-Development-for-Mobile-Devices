package com.example.week6login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Main_Fragment_Activity: AppCompatActivity() {
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_fragment_layout)
    }
    fun showText(firstName: String, lastName: String){
        val bottomFragmentActivity = supportFragmentManager.findFragmentById(R.id.fragment_bottom) as Bottom_Fragment_Activity
        bottomFragmentActivity.showText(firstName, lastName)
    }
}