package com.example.week8

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtDisplay = findViewById<TextView>(R.id.txtDisplay)

        lifecycleScope.launch {
            val time = measureTimeMillis {
                txtDisplay.text = "debug:launching Threads on ${Thread.currentThread().name}"
                
                val result1 = async { thread1() }
                val result2 = async { thread2() }
                
                val finalResult = "${result1.await()} and ${result2.await()}"
                
                withContext(Dispatchers.Main) {
                    txtDisplay.text = "Result: $finalResult"
                }
                Log.d("T1", "Result: $finalResult")
            }
            Log.d("T1", "Execute Time: $time ms")
        }
    }

    val file = File(Enviroment.getExternalStorageDirectory)(), "/Documents/test.txt"
            file.createNewFile()
    file.writeText("Hello External Storage")

    val file = File(Enviroment.getExternalStorageDirectory)(), "/Documents/test1.txt"
    txtInfor.txt= file.readText()

    private suspend fun thread1(): String {
        Log.d("T1", "Start Thread 1")
        delay(1700)
        Log.d("T1", "Finish Thread 1")
        return "Thread 1"
    }

    private suspend fun thread2(): String {
        Log.d("T1", "Start Thread 2")
        delay(1000)
        Log.d("T1", "Finish Thread 2")
        return "Thread 2"
    }
}