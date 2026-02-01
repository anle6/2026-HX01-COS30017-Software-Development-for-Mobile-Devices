package com.example.gymnasticapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.res.ColorStateList
class MainActivity : AppCompatActivity() {

    private lateinit var txtScoreLabel: TextView
    private lateinit var txtScoreValue: TextView
    private lateinit var txtElement: TextView

    private lateinit var btnPerform: Button
    private lateinit var btnDeduction: Button
    private lateinit var btnReset: Button

    private var score = 0
    private var element = 1
    private var routineEnded = false

    private lateinit var rootLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtScoreLabel = findViewById(R.id.txtScoreLabel)
        txtScoreValue = findViewById(R.id.txtScoreValue)
        txtElement = findViewById(R.id.txtElement)

        btnPerform = findViewById(R.id.btnPerform)
        btnDeduction = findViewById(R.id.btnDeduction)
        btnReset = findViewById(R.id.btnReset)

        rootLayout = findViewById(R.id.rootLayout)

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt("score")
            element = savedInstanceState.getInt("element")
            routineEnded = savedInstanceState.getBoolean("ended")
        }

        updateUI()

        btnPerform.setOnClickListener {
            it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            performElement()
        }

        btnDeduction.setOnClickListener { it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            takeDeduction() }

        btnReset.setOnClickListener { it.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
            resetGame() }
    }

    private fun performElement() {

        if (routineEnded) return

        if (element > 10) {
            Log.d("GYM", "Routine finished")
            return
        }

        val points = when (element) {
            in 1..3 -> 1
            in 4..7 -> 2
            else -> 3
        }

        score += points
        score = score.coerceAtMost(20)

        Log.d("GYM", "Performed element $element, +$points, score=$score")

        element++

        if (element > 10 && !routineEnded) {
            routineEnded = true

            Log.d("GYM", "Routine complete. Final score = $score")

            txtElement.text = getString(R.string.routine_complete)

            Toast.makeText(
                this,
                getString(R.string.routine_complete),
                Toast.LENGTH_SHORT
            ).show()
        }

        updateUI()
    }

    private fun takeDeduction() {

        if (routineEnded) return

        if (element == 1) {
            Log.d("GYM", "Deduction blocked before first element")
            return
        }

        score -= 2
        score = score.coerceAtLeast(0)

        routineEnded = true

        btnPerform.isEnabled = false
        btnDeduction.isEnabled = false

        Log.d("GYM", "Deduction taken, score=$score")

        txtElement.text = getString(R.string.deduction_stop)

        updateUI()
    }

    private fun resetGame() {
        score = 0
        element = 1
        routineEnded = false

        btnPerform.isEnabled = true
        btnDeduction.isEnabled = true

        Log.d("GYM", "Game reset")

        updateUI()
    }

    private fun updateUI() {

        txtScoreValue.text = score.toString()

        if (!routineEnded)
            txtElement.text = getString(R.string.element, element)

        val color = when (element) {
            in 1..3 -> Color.parseColor("#BBDEFB")
            in 4..7 -> Color.parseColor("#C8E6C9")
            else -> Color.parseColor("#FFA500")
        }

        txtScoreValue.setTextColor(color)
        rootLayout.setBackgroundColor(color)

        btnPerform.backgroundTintList = ColorStateList.valueOf(color)
        btnDeduction.backgroundTintList = ColorStateList.valueOf(color)

        btnPerform.setTextColor(Color.BLACK)
        btnDeduction.setTextColor(Color.BLACK)

        btnReset.backgroundTintList = ColorStateList.valueOf(Color.DKGRAY)
        btnReset.setTextColor(Color.WHITE)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("score", score)
        outState.putInt("element", element)
        outState.putBoolean("ended", routineEnded)
    }
}
