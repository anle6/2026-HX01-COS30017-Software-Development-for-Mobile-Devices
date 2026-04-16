package com.example.petstrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import coil.load
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import java.text.SimpleDateFormat
import java.util.*

class SettingsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = FirebaseAuth.getInstance()

        drawerLayout = findViewById(R.id.drawer_layout)
        val btnMenu: View = findViewById(R.id.btnMenu)
        val btnBack: View = findViewById(R.id.btnBack)
        val navView: NavigationView = findViewById(R.id.nav_view)

        navView.setNavigationItemSelectedListener(this)

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        
        btnBack.setOnClickListener {
            navigateToHome()
        }

        val backgroundImageView = findViewById<ImageView>(R.id.settingsBackgroundImageView)
        applySavedPreferences(backgroundImageView)

        // Set email in nav header
        val headerView = navView.getHeaderView(0)
        val navHeaderEmail = headerView.findViewById<TextView>(R.id.nav_header_email)
        navHeaderEmail.text = auth.currentUser?.email

        // Account Settings
        findViewById<MaterialCardView>(R.id.cardAccount).setOnClickListener {
            val user = auth.currentUser
            val email = user?.email ?: "Unknown"
            val nickname = user?.displayName ?: "No nickname set"
            
            val creationTimestamp = user?.metadata?.creationTimestamp ?: 0L
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val signUpDate = if (creationTimestamp != 0L) sdf.format(Date(creationTimestamp)) else "Unknown"
            
            AlertDialog.Builder(this)
                .setTitle("Account Details")
                .setMessage("Nickname: $nickname\nEmail: $email\nSign up date: $signUpDate")
                .setPositiveButton("Change Nickname") { _, _ ->
                    showChangeNicknameDialog()
                }
                .setNegativeButton("Close", null)
                .show()
        }

        // Security Settings
        findViewById<MaterialCardView>(R.id.cardSecurity).setOnClickListener {
            val options = arrayOf("Reset Password", "Change Email Address")
            
            AlertDialog.Builder(this)
                .setTitle("Security Settings")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> startActivity(Intent(this, ResetPasswordActivity::class.java))
                        1 -> showChangeEmailDialog()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Notification Settings
        findViewById<MaterialCardView>(R.id.cardNotification).setOnClickListener {
            showNotificationSettingsDialog()
        }

        // App Preference
        findViewById<MaterialCardView>(R.id.cardAppPreference).setOnClickListener {
            startActivity(Intent(this, ThemePreferenceActivity::class.java))
        }

        findViewById<Button>(R.id.settingsLogoutButton).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    auth.signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("NAVIGATE_TO", R.id.nav_home)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                navigateToHome()
            }
            R.id.nav_logout -> {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            R.id.nav_settings -> {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            else -> {
                // Pass the item ID to MainActivity to navigate to the correct section
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("NAVIGATE_TO", item.itemId)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showNotificationSettingsDialog() {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPref.getBoolean("notifications_enabled", true)
        val soundEnabled = sharedPref.getBoolean("notifications_sound", true)

        val options = arrayOf(
            "${if (notificationsEnabled) "Disable" else "Enable"} Notifications",
            "${if (soundEnabled) "Disable" else "Enable"} Sound"
        )

        AlertDialog.Builder(this)
            .setTitle("Notification Settings")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        sharedPref.edit().putBoolean("notifications_enabled", !notificationsEnabled).apply()
                        Toast.makeText(this, "Notifications ${if (!notificationsEnabled) "Enabled" else "Disabled"}", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        sharedPref.edit().putBoolean("notifications_sound", !soundEnabled).apply()
                        Toast.makeText(this, "Sound ${if (!soundEnabled) "Enabled" else "Disabled"}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Back", null)
            .show()
    }

    private fun showChangeNicknameDialog() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 20, 50, 0)

        val input = EditText(this)
        input.hint = "Enter New Nickname"
        input.setText(auth.currentUser?.displayName)
        layout.addView(input)

        AlertDialog.Builder(this)
            .setTitle("Change Nickname")
            .setView(layout)
            .setPositiveButton("Update") { _, _ ->
                val newNickname = input.text.toString().trim()
                if (newNickname.isNotEmpty()) {
                    val profileUpdates = userProfileChangeRequest {
                        displayName = newNickname
                    }
                    auth.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Nickname updated successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun applySavedPreferences(backgroundImageView: ImageView) {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val themeResId = sharedPref.getInt("background_theme", R.drawable.dark_paws)
        val contrast = sharedPref.getFloat("background_contrast", 0.3f)
        val textSize = sharedPref.getFloat("text_size", 16f)
        
        backgroundImageView.load(themeResId) {
            crossfade(true)
            allowHardware(false)
        }
        backgroundImageView.alpha = contrast

        findViewById<TextView>(R.id.accountTitle).textSize = textSize
        findViewById<TextView>(R.id.securityTitle).textSize = textSize
        findViewById<TextView>(R.id.notificationTitle).textSize = textSize
        findViewById<TextView>(R.id.appPreferenceTitle).textSize = textSize
    }

    private fun showChangeEmailDialog() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 20, 50, 0)

        val input = EditText(this)
        input.hint = "New Email Address"
        layout.addView(input)

        AlertDialog.Builder(this)
            .setTitle("Change Email")
            .setMessage("Enter your new email address. Note: Firebase may require recent login.")
            .setView(layout)
            .setPositiveButton("Update") { _, _ ->
                val newEmail = input.text.toString().trim()
                if (newEmail.isNotEmpty()) {
                    auth.currentUser?.updateEmail(newEmail)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Email updated successfully!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
