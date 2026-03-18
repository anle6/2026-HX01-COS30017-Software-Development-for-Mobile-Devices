package com.example.carrentalapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PurchaseHistoryActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var searchView: SearchView
    private var currentQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_purchase_history)

        container = findViewById(R.id.historyContainer)
        searchView = findViewById(R.id.historySearchView)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.btnHome).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText ?: ""
                refreshUI()
                return true
            }
        })

        refreshUI()
    }

    private fun refreshUI() {
        findViewById<TextView>(R.id.creditsGained).text = "${CarData.getCreditsGained()}"
        findViewById<TextView>(R.id.creditsSpent).text = "${CarData.getCreditsSpent()}"
        findViewById<TextView>(R.id.currentBalance).text = "${CarData.creditBalance}"

        container.removeAllViews()
        val inflater = LayoutInflater.from(this)
        
        var history = CarData.purchaseHistory.toList()
        if (currentQuery.isNotEmpty()) {
            history = history.filter { it.carName.contains(currentQuery, ignoreCase = true) }
        }

        if (history.isEmpty()) {
            val emptyMsg = TextView(this)
            emptyMsg.text = if (currentQuery.isEmpty()) "No purchase history found." else "No matches found."
            emptyMsg.setPadding(32, 32, 32, 32)
            container.addView(emptyMsg)
        } else {
            history.asReversed().forEach { transaction ->
                val itemView = inflater.inflate(R.layout.item_transaction, container, false)
                itemView.findViewById<TextView>(R.id.transactionTitle).text = transaction.carName
                itemView.findViewById<TextView>(R.id.transactionDate).text = transaction.date
                
                val amountText = itemView.findViewById<TextView>(R.id.transactionAmount)
                if (transaction.type == "SPENT") {
                    amountText.text = "-${transaction.cost} Credits"
                    amountText.setTextColor(getColor(android.R.color.holo_red_dark))
                } else {
                    amountText.text = "+${transaction.cost} Credits"
                    amountText.setTextColor(getColor(android.R.color.holo_green_dark))
                }
                container.addView(itemView)
            }
        }
    }
}
