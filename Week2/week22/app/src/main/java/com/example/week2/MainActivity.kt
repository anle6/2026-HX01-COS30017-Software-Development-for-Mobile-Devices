package com.example.week2
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var btnPlus: Button
    lateinit var btnMinus: Button
    lateinit var btnMultiply: Button
    lateinit var btnDivide: Button
    lateinit var firstNum: EditText
    lateinit var secondNum: EditText
    lateinit var result: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPlus = findViewById(R.id.plus)
        btnMinus = findViewById(R.id.minus)
        btnMultiply = findViewById(R.id.multiply)
        btnDivide = findViewById(R.id.divide)

        firstNum = findViewById(R.id.first_num)
        secondNum = findViewById(R.id.second_num)
        result = findViewById(R.id.result)

        btnPlus.setOnClickListener(this)
        btnMinus.setOnClickListener(this)
        btnMultiply.setOnClickListener(this)
        btnDivide.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val num1 = firstNum.text.toString().toDoubleOrNull()
        val num2 = secondNum.text.toString().toDoubleOrNull()

        if (num1 == null || num2 == null) {
            result.setText("Invalid input")
            return
        }

        val res = when (v?.id) {
            R.id.plus -> num1 + num2
            R.id.minus -> num1 - num2
            R.id.multiply -> num1 * num2
            R.id.divide -> {
                if (num2 == 0.0) {
                    result.setText("Cannot divide by 0")
                    return
                }
                num1 / num2
            }
            else -> 0.0
        }

        result.setText(res.toString())
    }
}