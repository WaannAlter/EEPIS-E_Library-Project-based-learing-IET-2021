package com.example.elibpl

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class admin_login_page : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var loginButton: Button? = null
    private var usernameEditText: EditText? = null
    private var passwordEditText: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login_page)
        loginButton = findViewById(R.id.login_button)
        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        auth = Firebase.auth

        // Set up text change listeners for the username and password fields
        usernameEditText?.addTextChangedListener(loginTextWatcher)
        passwordEditText?.addTextChangedListener(loginTextWatcher)
        loginButton?.setOnClickListener(View.OnClickListener { // Handle button click event here
            goToAdminHomePage()
        })
    }

    private fun goToAdminHomePage() {
        val email = usernameEditText?.text.toString()
        val password = passwordEditText?.text.toString()

        // Check if email matches the admin email
        if (email == "admin@pens.ac.id") {
            // Try to sign in with Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in successful, navigate to admin home page
                        val intent = Intent(this, admin_homepage::class.java)
                        startActivity(intent)
                        Toast.makeText(baseContext, "Welcome, Admin!", Toast.LENGTH_SHORT).show()

                        // Finish the current activity if needed
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            // If email does not match the admin email, display a message to the user
            Toast.makeText(baseContext, "Invalid admin email.", Toast.LENGTH_SHORT).show()
        }
    }


    // TextWatcher to monitor changes in the username and password fields
    private val loginTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            // Check if both username and password fields are filled
            val isUsernameFilled = usernameEditText?.text?.length ?: 0 > 0
            val isPasswordFilled = passwordEditText?.text?.length ?: 0 > 0

            // Enable or disable the login button based on the fields' status
            loginButton?.isEnabled = isUsernameFilled && isPasswordFilled
        }
    }
}
