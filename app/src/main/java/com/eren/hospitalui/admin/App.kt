package com.eren.hospitalui.admin

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eren.hospitalui.auth.DatabaseHelper
import com.eren.hospitalui.home.LoginViewModel

@Composable
fun App(databaseHelper: DatabaseHelper, loginViewModel: LoginViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "adminLogin") {
        composable("adminLogin") {
            AdminLoginScreen(
                navController = navController,
                onBackPressed = { /* Handle back press */ }
            )
        }
        composable("adminAccount") {
            AdminHomeScreen(
                navController = navController,
                databaseHelper = databaseHelper,
                loginViewModel = loginViewModel,
                onLogout = {
                    navController.popBackStack("adminLogin", inclusive = true)
                }
            )
        }
    }
}