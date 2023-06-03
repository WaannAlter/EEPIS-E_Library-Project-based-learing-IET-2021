package com.example.elibpl.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.example.elibpl.data.LoginRepository
import com.example.elibpl.data.Result

import com.example.elibpl.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // start Firebase authentication check
        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(username, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val displayName = user?.displayName ?: "Unknown User"
                _loginResult.value = LoginResult(success = LoggedInUserView(displayName = displayName))
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }


    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.isEmpty()) {
            false
        } else {
            // Check if the username contains the '@' symbol
            if (!username.contains('@')) {
                return false
            }
            // Further validation for email address format
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        }
    }


    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}