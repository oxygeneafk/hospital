package com.eren.hospitalui.home

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eren.hospitalui.auth.DatabaseHelper
import com.eren.hospitalui.auth.LoginScreen
import com.eren.hospitalui.theme.HospitalTheme
import com.eren.hospital.R
import com.eren.hospitalui.admin.AdminHomeScreen
import com.eren.hospitalui.admin.AdminLoginScreen
import com.eren.hospitalui.navigationbar.AccountScreen
import com.eren.hospitalui.navigationbar.AppointmentScreen
import com.eren.hospitalui.navigationbar.MedicineScreen
import com.eren.hospitalui.repository.AnnouncementRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Veritabanı işlemleri için bir yardımcı sınıf oluşturuluyor
        val databaseHelper = DatabaseHelper(this)

        // LoginViewModel örneği oluşturuluyor (ViewModel, giriş işlemlerini yönetiyor)
        val loginViewModel: LoginViewModel by viewModels {
            LoginViewModelFactory(this) // ViewModel oluşturucu kullanılıyor
        }

        // Uygulama içeriği Compose ile tanımlanıyor
        setContent {
            HospitalTheme { // Uygulama genelinde tema ayarları
                val navController = rememberNavController()

                // NavHost tanımlaması
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            loginViewModel = loginViewModel,
                            onLoginResult = { success ->
                                if (success) {
                                    navController.navigate("main")
                                }
                            },
                            onRegisterUser = { name, surname, username, password, address, bloodGroup ->
                                databaseHelper.insertUser(name, surname, username, password, bloodGroup, address)
                            },
                            context = this@MainActivity,
                            navController = navController
                        )
                    }
                    composable("main") {
                        MainScreen(
                            databaseHelper = databaseHelper,
                            onLogout = {
                                navController.popBackStack("login", inclusive = false)
                            },
                            loginViewModel = loginViewModel,
                            navController = navController
                        )
                    }
                    composable("adminLogin") {
                        AdminLoginScreen(
                            navController = navController,
                            onBackPressed = { navController.popBackStack() }
                        )
                    }
                    composable("adminAccount") {
                        AdminHomeScreen(
                            databaseHelper = databaseHelper,
                            loginViewModel = loginViewModel,
                            onLogout = { /* Logout işlemi */ },
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    loginViewModel: LoginViewModel,
    onLogout: () -> Unit,
    databaseHelper: DatabaseHelper,
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column {
                // Üst Kısım: Başlık ve logo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "oxyyylesnar Hospital",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.hospital_logo),
                            contentDescription = "Hospital Logo",
                            modifier = Modifier.size(64.dp)
                        )
                        IconButton(onClick = { onLogout(context = context, navController = navController) }) {
                            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout")
                        }
                    }
                }
                Box(modifier = Modifier.padding(innerPadding)) {
                    when (selectedTab) {
                        0 -> HomeScreenContent(onLogout = onLogout)
                        1 -> AppointmentScreen(databaseHelper = databaseHelper)
                        2 -> MedicineScreen(databaseHelper = databaseHelper, username = loginViewModel.username)
                        3 -> AccountScreen(databaseHelper = databaseHelper, loginViewModel = loginViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar {
        val items = listOf(
            NavigationItem("Home", Icons.Default.Home),
            NavigationItem("Appointment", Icons.Default.List),
            NavigationItem("Medicine", Icons.Default.Healing),
            NavigationItem("Account", Icons.Default.AccountCircle),
        )

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.label)
                },
                label = {
                    Text(text = item.label)
                }
            )
        }
    }
}

@Composable
fun HomeScreenContent(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Hastane bilgisi
        Text(
            text = "Hastanemiz",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val hospitalInfo = """
            Hastanemiz oldukça saygın bir hastanedir. Sağlık hizmetlerindeki 
            uzmanlığı ve hasta memnuniyeti odaklı yaklaşımıyla bilinir. 
            Uzman kadromuz ve modern altyapımızla sizin için buradayız.
        """.trimIndent()

        Text(
            text = hospitalInfo,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Duyurular başlığı
        Text(
            text = "Duyurular",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Duyurular listesi
        val announcements = AnnouncementRepository.getAnnouncements()

        // Duyuru kartları
        announcements.forEach { announcement ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = announcement.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = announcement.content,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        /*Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text("Çıkış Yap")
        }*/
    }
}

data class NavigationItem(val label: String, val icon: ImageVector)

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
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
        popUpTo("main") { inclusive = true }
    }
}