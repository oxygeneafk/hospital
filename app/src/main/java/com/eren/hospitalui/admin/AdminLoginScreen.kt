package com.eren.hospitalui.admin

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.TextButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.eren.hospital.R

@Composable
fun AdminLoginScreen(
    navController: NavController,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current

    // Admin login bilgilerinin yerel olarak belirlenmesi
    val validAdminUsername = "admin"
    val validAdminPassword = "admin123"

    var adminUsername by remember { mutableStateOf("") }
    var adminPassword by remember { mutableStateOf("") }
    var loginFailed by remember { mutableStateOf(false) }

    // Oturum kontrolü
    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("AdminPrefs", Context.MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("username", null)
        val savedPassword = sharedPreferences.getString("password", null)
        if (savedUsername == validAdminUsername && savedPassword == validAdminPassword) {
            navController.navigate("adminAccount") {
                popUpTo("adminLogin") { inclusive = true }
            }
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
            text = "Admin Login",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Access admin features",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = adminUsername,
            onValueChange = { adminUsername = it },
            label = { Text("Admin Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = adminPassword,
            onValueChange = { adminPassword = it },
            label = { Text("Admin Password") },
            modifier = Modifier.fillMaxWidth(),
            isError = loginFailed // Eğer giriş başarısızsa hata gösterilecek
        )

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
                if (adminUsername == validAdminUsername && adminPassword == validAdminPassword) {
                    loginFailed = false
                    val sharedPreferences = context.getSharedPreferences("AdminPrefs", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putString("username", adminUsername)
                        putString("password", adminPassword)
                        apply()
                    }
                    navController.navigate("adminAccount")  // Başarılı girişte yönlendirme
                } else {
                    loginFailed = true  // Başarısız giriş
                }
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

        TextButton(onClick = onBackPressed) {
            Text(
                text = "Back",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}