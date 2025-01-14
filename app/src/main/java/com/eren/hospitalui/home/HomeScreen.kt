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
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eren.hospitalui.auth.DatabaseHelper
import com.eren.hospitalui.auth.LoginScreen
//import com.eren.hospitalui.nurses.list.ListScreen ŞU ANDA BUNU KULLANMIYORUM PROJEDEN ALDIM DAHA SONRA EKLENECEK
import com.eren.hospitalui.theme.HospitalTheme
import com.eren.hospital.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.eren.hospitalui.admin.AdminHomeScreen
import com.eren.hospitalui.admin.AdminLoginScreen
import com.eren.hospitalui.admin.App
import com.eren.hospitalui.navigationbar.AccountScreen
import com.eren.hospitalui.navigationbar.AppointmentScreen
import com.eren.hospitalui.navigationbar.MedicineScreen

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
                            loginViewModel = loginViewModel
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
                            databaseHelper = databaseHelper,  // Gerekli parametreleri burada geçin
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
    loginViewModel: LoginViewModel, // ViewModel, giriş ve uygulama verilerini yönetir
    onLogout: () -> Unit, // Çıkış yapıldığında çağrılan geri çağırma
    databaseHelper: DatabaseHelper // Veritabanı işlemleri için
) {
    var selectedTab by remember { mutableStateOf(0) } // Seçilen sekme durumu

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab, // Seçili sekme
                onTabSelected = { selectedTab = it } // Sekme seçildiğinde çağrılır
            )
        }
    ) { innerPadding -> // Ana içerik için dolgu alanı
        Box(modifier = Modifier.padding(innerPadding)) {
            // Seçilen sekmeye göre ekran değişir
            when (selectedTab) {
                0 -> HomeScreen(onLogout = onLogout) // Ana ekran
                1 -> AppointmentScreen(databaseHelper = databaseHelper) // Randevu alma ekranı
                2 -> MedicineScreen(databaseHelper = databaseHelper, username = loginViewModel.username)
                3 -> AccountScreen(databaseHelper = databaseHelper, loginViewModel = loginViewModel) // Hesap ekranı
            }
        }
    }
}


@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar {
        // Alt gezinme çubuğu öğeleri
        val items = listOf(
            NavigationItem("Home", Icons.Default.Home), // Ana ekran simgesi
            NavigationItem("Appointment", Icons.Default.List), // Randevu simgesi
            NavigationItem("Medicine", Icons.Default.Healing), // İlaç simgesi
            NavigationItem("Account", Icons.Default.AccountCircle), // Hesap simgesi
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
fun HomeScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize() // Tüm ekranı kapla
            .padding(16.dp) // Kenar boşlukları
    ) {
        // Üst Kısım: Başlık ve logo
        Row(
            modifier = Modifier
                .fillMaxWidth() // Tüm genişliği kapla
                .padding(bottom = 16.dp), // Alt boşluk
            horizontalArrangement = Arrangement.SpaceBetween, // Başlık ve logoyu ayırır
            verticalAlignment = Alignment.CenterVertically // Ortada hizalar
        ) {
            Text(
                text = "My Hospital", // Uygulama başlığı
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Image(
                painter = painterResource(id = R.drawable.hospital_logo), // Hastane logosu
                contentDescription = "Hospital Logo",
                modifier = Modifier.size(64.dp) // Logo boyutu
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Aralık ekleme

        // Hastane bilgisi
        Text(
            text = "Hastanemiz", // Alt başlık
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Hastane detayları
        val hospitalInfo = """
            Hastanemiz oldukça saygın bir hastanedir. Sağlık hizmetlerindeki 
            uzmanlığı ve hasta memnuniyeti odaklı yaklaşımıyla bilinir. 
            Uzman kadromuz ve modern altyapımızla sizin için buradayız.
        """.trimIndent()

        Text(
            text = hospitalInfo, // Hastane açıklaması
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
        val announcements = listOf(
            "Bugün saat 14:00'te sağlık kontrolü etkinliği düzenlenecektir.",
            "Aşı kampanyası yarın başlayacaktır.",
            "Hastane sistem güncellemesi 18:00'de yapılacaktır."
        )

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
                Text(
                    text = announcement,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Çıkış yap butonu
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text("Çıkış Yap") // Çıkış yap butonu etiketi
        }
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