package com.example.carrentalapp

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var carsContainer: LinearLayout
    private lateinit var searchView: SearchView
    private lateinit var btnSort: Button
    private lateinit var btnMenu: ImageButton

    private var currentSearchQuery: String = ""
    private var currentSortType: SortType = SortType.NONE

    enum class SortType {
        NONE, RATING_DESC, YEAR_DESC, COST_ASC
    }

    private val detailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        refreshUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        carsContainer = findViewById(R.id.carsContainerLayout)
        searchView = findViewById(R.id.searchView)
        btnSort = findViewById(R.id.btnSort)
        btnMenu = findViewById(R.id.btnMenu)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val moveFactor = drawerView.width * slideOffset
                findViewById<View>(R.id.main_content).translationX = moveFactor
            }
        })

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        setupNavigationView()
        setupSearchAndSort()
        refreshUI()
    }

    private fun setupNavigationView() {
        refreshNavHeader()

        val themeMenuItem = navView.menu.findItem(R.id.nav_theme_toggle)
        val themeSwitch = themeMenuItem.actionView as SwitchCompat
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        themeSwitch.isChecked = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                }
                R.id.nav_rented -> {
                    startActivity(Intent(this, RentedCarsActivity::class.java))
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, PurchaseHistoryActivity::class.java))
                }
                R.id.nav_home -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun refreshNavHeader() {
        val headerView = navView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.navHeaderBalance).text = "Balance: ${CarData.creditBalance} Credits"
        
        headerView.findViewById<ImageButton>(R.id.btnAddCredits).setOnClickListener {
            CarData.addCredits(100)
            Toast.makeText(this, "Added 100 Credits!", Toast.LENGTH_SHORT).show()
            refreshUI()
        }
    }

    private fun setupSearchAndSort() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchQuery = newText ?: ""
                refreshUI()
                return true
            }
        })

        btnSort.setOnClickListener {
            val options = arrayOf("Rating (High to Low)", "Year (Newest to Oldest)", "Cost (Low to High)", "None")
            AlertDialog.Builder(this)
                .setTitle("Sort by")
                .setItems(options) { _, which ->
                    currentSortType = when (which) {
                        0 -> SortType.RATING_DESC
                        1 -> SortType.YEAR_DESC
                        2 -> SortType.COST_ASC
                        else -> SortType.NONE
                    }
                    refreshUI()
                }
                .show()
        }
    }

    private fun refreshUI() {
        refreshNavHeader()
        
        carsContainer.removeAllViews()
        val inflater = LayoutInflater.from(this)
        var displayList = CarData.getAvailableCars()

        if (currentSearchQuery.isNotEmpty()) {
            displayList = displayList.filter { 
                it.name.contains(currentSearchQuery, ignoreCase = true) || 
                it.model.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        displayList = when (currentSortType) {
            SortType.RATING_DESC -> displayList.sortedByDescending { it.rating }
            SortType.YEAR_DESC -> displayList.sortedByDescending { it.year }
            SortType.COST_ASC -> displayList.sortedBy { it.dailyCost }
            SortType.NONE -> displayList
        }

        if (displayList.isEmpty()) {
            val emptyMsg = TextView(this)
            emptyMsg.text = "No cars match your criteria."
            emptyMsg.setPadding(32, 32, 32, 32)
            emptyMsg.gravity = android.view.Gravity.CENTER
            carsContainer.addView(emptyMsg)
        } else {
            displayList.forEach { car ->
                val itemView = inflater.inflate(R.layout.item_car_preview, carsContainer, false)
                itemView.findViewById<ImageView>(R.id.previewImage).setImageResource(car.thumbnailResId)
                itemView.findViewById<TextView>(R.id.previewName).text = car.name
                itemView.findViewById<TextView>(R.id.previewModel).text = car.model
                val favIcon = itemView.findViewById<ImageView>(R.id.previewFavoriteIcon)
                favIcon.visibility = if (car.isFavorite) View.VISIBLE else View.GONE

                itemView.setOnClickListener {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("CAR_ID", car.id)
                    detailLauncher.launch(intent)
                }
                carsContainer.addView(itemView)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshUI()
    }
}
