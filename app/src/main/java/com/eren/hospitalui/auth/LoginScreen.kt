package com.eren.hospitalui.auth

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.eren.hospital.R
import com.eren.hospitalui.admin.AdminLoginScreen
import com.eren.hospitalui.home.LoginResult
import com.eren.hospitalui.home.LoginViewModel

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onLoginResult: (Boolean) -> Unit,
    onRegisterUser: (String, String, String, String, String?, String?) -> Unit,
    context: Context,
    navController: NavController
) {
    var username by remember { mutableStateOf(loginViewModel.username) }
    var password by remember { mutableStateOf(loginViewModel.password) }
    var navigateToRegister by remember { mutableStateOf(false) }
    var navigateToAdmin by remember { mutableStateOf(false) }
    var loginFailed by remember { mutableStateOf(false) }

    if (navigateToRegister) {
        RegisterScreen(
            onBackPressed = { navigateToRegister = false },
            onRegister = onRegisterUser
        )
    } else if (navigateToAdmin) {
        AdminLoginScreen(
            navController = navController,
            onBackPressed = { navigateToAdmin = false }
        )
    } else {
        val loginResult by loginViewModel.loginResult.collectAsState()

        LaunchedEffect(loginResult) {
            when (loginResult) {
                is LoginResult.Success -> {
                    onLoginResult(true)
                    loginFailed = false // Başarılı girişte hata durumu sıfırlanır
                    username = "" // Kullanıcı adı alanı temizlenir
                    password = "" // Şifre alanı temizlenir
                }
                is LoginResult.Failure -> {
                    onLoginResult(false)
                    loginFailed = true // Başarısız girişte hata durumu aktif edilir
                }
                is LoginResult.None -> { /* Do nothing */ }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(125.dp))
            Image(
                painter = painterResource(id = R.drawable.hospital_logo),
                contentDescription = "Hospital Logo",
                modifier = Modifier.size(250.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Hospital Management",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Manage your hospital tasks with ease",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                isError = loginFailed // Eğer giriş başarısızsa hata gösterecek
            )

            // Hata mesajı, giriş başarısızsa
            if (loginFailed) {
                Text(
                    text = "Invalid username or password",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    loginViewModel.username = username
                    loginViewModel.password = password
                    loginViewModel.login()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text("Log In")
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navigateToAdmin = true }) {
                Text(
                    text = "Admin Log In Screen",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            TextButton(onClick = { navigateToRegister = true }) {
                Text(
                    text = "Don't have an account? Sign up",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}