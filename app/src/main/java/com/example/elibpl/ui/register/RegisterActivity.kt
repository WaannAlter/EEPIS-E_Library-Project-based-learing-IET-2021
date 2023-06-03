package com.example.elibpl

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.elibpl.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val fullNameEditText = findViewById<EditText>(R.id.full_name)
        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val repeatPasswordEditText = findViewById<EditText>(R.id.repeat_password)
        val phoneNumberEditText = findViewById<EditText>(R.id.phone_number)
        val registerButton = findViewById<Button>(R.id.register)
        val signInText = findViewById<TextView>(R.id.sign_in_text)

        signInText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        registerButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val repeatPassword = repeatPasswordEditText.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()

            if (email.isBlank() || password.isBlank() || repeatPassword.isBlank() || fullName.isBlank() || phoneNumber.isBlank()) {
                Toast.makeText(baseContext, "All fields must be filled in.", Toast.LENGTH_SHORT).show()
            } else if (password != repeatPassword) {
                Toast.makeText(baseContext, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = auth.currentUser

                            // Build a UserProfileChangeRequest to update the display name
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName)
                                .build()

                            user?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "User profile updated.")
                                    }
                                }

                            Toast.makeText(baseContext, "Registration successful.", Toast.LENGTH_SHORT).show()

                            // After successful registration, redirect to LoginActivity
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        }
                    }

            }
        }
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }
}

