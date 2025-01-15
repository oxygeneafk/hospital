package com.eren.hospitalui.admin

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.eren.hospitalui.adminnavigationbar.AdminAccountScreen
import com.eren.hospitalui.adminnavigationbar.AdminAppointmentScreen
import com.eren.hospitalui.adminnavigationbar.AdminDoctorScreen
import com.eren.hospitalui.adminnavigationbar.AdminMedicineScreen
import com.eren.hospitalui.auth.DatabaseHelper
import com.eren.hospitalui.home.LoginViewModel

@Composable
fun AdminHomeScreen(
    navController: NavController,
    databaseHelper: DatabaseHelper,
    loginViewModel: LoginViewModel,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // Seçilen sekme durumu
    val context = LocalContext.current

    // Oturum kontrolü
    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("AdminPrefs", Context.MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("username", null)
        val savedPassword = sharedPreferences.getString("password", null)
        if (savedUsername == null || savedPassword == null) {
            navController.navigate("login") {
                popUpTo("adminHome") { inclusive = true }
            }
        }
    }

    Scaffold(
        bottomBar = {
            AdminBottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onLogout(context, navController)
            }) {
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> HomeScreen()
                1 -> AdminAppointmentScreen(databaseHelper)
                2 -> AdminDoctorScreen(databaseHelper)
                3 -> AdminMedicineScreen(databaseHelper)
                4 -> AdminAccountScreen(databaseHelper)
            }
        }
    }
}

@Composable
fun AdminBottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar {
        // Alt gezinme çubuğu öğeleri
        val items = listOf(
            com.eren.hospitalui.home.NavigationItem("Home", Icons.Default.Home),
            com.eren.hospitalui.home.NavigationItem("Appointment", Icons.Default.List), // Ana ekran simgesi
            com.eren.hospitalui.home.NavigationItem("Doctor", Icons.Default.MedicalServices), // Randevu simgesi
            com.eren.hospitalui.home.NavigationItem("Medicine", Icons.Default.Healing), // İlaç simgesi
            com.eren.hospitalui.home.NavigationItem("Account", Icons.Default.AccountCircle), // Hesap simgesi
        )

        // Her bir gezinme çubuğu öğesi için
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedTab == index, // Seçili sekme kontrolü
                onClick = { onTabSelected(index) }, // Sekme seçimi
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.label) // Sekme simgesi
                },
                label = {
                    Text(text = item.label) // Sekme etiketi
                }
            )
        }
    }
}

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Admin Home",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// onLogout işlevi kullanıcı bilgilerini temizler ve giriş ekranına yönlendirir
fun onLogout(context: Context, navController: NavController) {
    val sharedPreferences = context.getSharedPreferences("AdminPrefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        clear()
        apply()
    }
    navController.navigate("login") {
        popUpTo("adminHome") { inclusive = true }
    }
}