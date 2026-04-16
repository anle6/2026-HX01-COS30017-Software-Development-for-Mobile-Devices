package com.example.petstrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    
    private val RC_SIGN_IN = 9001
    private var isLoggingIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        
        // Configure background and text size
        val backgroundImageView = findViewById<ImageView>(R.id.backgroundImageView)
        applySavedPreferences(backgroundImageView)
        
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Configure Facebook Login
        callbackManager = CallbackManager.Factory.create()

        val emailEditText = findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordEditText = findViewById<TextInputEditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val forgotPasswordTextView = findViewById<TextView>(R.id.forgotPasswordTextView)
        val registerTextView = findViewById<TextView>(R.id.registerTextView)
        val rememberMeCheckBox = findViewById<CheckBox>(R.id.rememberMeCheckBox)
        val googleSignInButton = findViewById<View>(R.id.googleSignInButton)
        val facebookLoginButton = findViewById<LoginButton>(R.id.facebookLoginButton)

        // Load remembered credentials if any
        val sharedPref = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        if (sharedPref.getBoolean("rememberMe", false)) {
            emailEditText.setText(sharedPref.getString("email", ""))
            passwordEditText.setText(sharedPref.getString("password", ""))
            rememberMeCheckBox.isChecked = true
        }

        loginButton.setOnClickListener {
            if (isLoggingIn) return@setOnClickListener
            
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            setLoginUIEnabled(false)
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        handleLoginSuccess(email, password, rememberMeCheckBox.isChecked)
                    } else {
                        setLoginUIEnabled(true)
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        forgotPasswordTextView.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        registerTextView.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        googleSignInButton.setOnClickListener {
            if (isLoggingIn) return@setOnClickListener
            setLoginUIEnabled(false)
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        facebookLoginButton.setPermissions("email", "public_profile")
        facebookLoginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                setLoginUIEnabled(true)
                Toast.makeText(this@LoginActivity, "Facebook Login Cancelled", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException) {
                setLoginUIEnabled(true)
                Toast.makeText(this@LoginActivity, "Facebook Login Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setLoginUIEnabled(enabled: Boolean) {
        isLoggingIn = !enabled
        findViewById<Button>(R.id.loginButton).isEnabled = enabled
        findViewById<View>(R.id.googleSignInButton).isEnabled = enabled
        findViewById<LoginButton>(R.id.facebookLoginButton).isEnabled = enabled
    }

    private fun applySavedPreferences(backgroundImageView: ImageView) {
        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val themeResId = sharedPref.getInt("background_theme", R.drawable.cute_pattern_with_dogs)
        val contrast = sharedPref.getFloat("background_contrast", 0.3f)
        val textSize = sharedPref.getFloat("text_size", 16f)
        
        backgroundImageView.load(themeResId) {
            crossfade(true)
            allowHardware(false)
        }
        backgroundImageView.alpha = contrast

        // Apply text size to title elements
        findViewById<TextView>(R.id.forgotPasswordTextView).textSize = textSize
        findViewById<TextView>(R.id.registerTextView).textSize = textSize
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    setLoginUIEnabled(true)
                    Toast.makeText(this, "Facebook Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                setLoginUIEnabled(true)
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    setLoginUIEnabled(true)
                    Toast.makeText(this, "Google Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun handleLoginSuccess(email: String, password: String, rememberMe: Boolean) {
        val sharedPref = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        if (rememberMe) {
            editor.putBoolean("rememberMe", true)
            editor.putString("email", email)
            editor.putString("password", password)
        } else {
            editor.clear()
        }
        editor.apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}