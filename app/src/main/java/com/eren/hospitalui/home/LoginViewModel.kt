package com.eren.hospitalui.home

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eren.hospitalui.auth.DatabaseHelper
import com.eren.hospitalui.auth.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginResult {
    object Success : LoginResult()
    object Failure : LoginResult()
    object None : LoginResult()
}

class LoginViewModel(private val context: Context) : ViewModel() {

    private val databaseHelper = DatabaseHelper(context)

    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.None)
    val loginResult: StateFlow<LoginResult> = _loginResult

    private val _loggedInUser = MutableStateFlow<User?>(null)
    val loggedInUser: StateFlow<User?> = _loggedInUser

    var username: String = ""
    var password: String = ""

    fun login() {
        viewModelScope.launch {
            if (databaseHelper.getUser(username, password)) {
                _loginResult.value = LoginResult.Success
                _loggedInUser.value = databaseHelper.getUserDetails(username)
            } else {
                _loginResult.value = LoginResult.Failure
                Toast.makeText(context, "Giriş başarısız", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun register(
        name: String,
        surname: String,
        newUsername: String,
        newPassword: String,
        bloodGroup: String?,
        address: String?
    ) {
        viewModelScope.launch {
            databaseHelper.insertUser(
                name = name,
                surname = surname,
                username = newUsername,
                password = newPassword,
                bloodGroup = bloodGroup,
                address = address
            )
        }
    }

}

