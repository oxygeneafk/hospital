// AdminHomeScreen.kt

package com.eren.hospitalui.admin

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.eren.hospitalui.auth.DatabaseHelper
import com.eren.hospitalui.home.LoginViewModel
import com.eren.hospitalui.repository.Announcement
import com.eren.hospitalui.repository.AnnouncementRepository
import com.eren.hospital.R
import com.eren.hospitalui.adminnavigationbar.AdminAccountScreen
import com.eren.hospitalui.adminnavigationbar.AdminAppointmentScreen
import com.eren.hospitalui.adminnavigationbar.AdminDoctorScreen
import com.eren.hospitalui.adminnavigationbar.AdminMedicineScreen

@Composable
fun AdminHomeScreen(
    navController: NavController,
    databaseHelper: DatabaseHelper,
    loginViewModel: LoginViewModel,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
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
        floatingActionButtonPosition = FabPosition.Center
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
                        text = "Admin Home",
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
                        IconButton(onClick = { onLogout(context, navController) }) {
                            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout")
                        }
                    }
                }
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
}

@Composable
fun AdminBottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar {
        val items = listOf(
            com.eren.hospitalui.home.NavigationItem("Home", Icons.Default.Home),
            com.eren.hospitalui.home.NavigationItem("Appointment", Icons.Default.List),
            com.eren.hospitalui.home.NavigationItem("Doctor", Icons.Default.MedicalServices),
            com.eren.hospitalui.home.NavigationItem("Medicine", Icons.Default.Healing),
            com.eren.hospitalui.home.NavigationItem("Account", Icons.Default.AccountCircle),
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

        // Add form to add announcements
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Announcement Title") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Announcement Content") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                val newAnnouncement = Announcement(
                    id = AnnouncementRepository.getAnnouncements().size + 1,
                    title = title,
                    content = content,
                    timestamp = System.currentTimeMillis()
                )
                AnnouncementRepository.addAnnouncement(newAnnouncement)
                title = ""
                content = ""
            },
            modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
        ) {
            Text("Add Announcement")
        }

        // Display existing announcements
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Existing Announcements",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        val announcements = AnnouncementRepository.getAnnouncements()
        LazyColumn {
            items(announcements) { announcement ->
                AnnouncementCard(announcement = announcement)
            }
        }
    }
}

@Composable
fun AnnouncementCard(announcement: Announcement) {
    var isEditing by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(announcement.title) }
    var content by remember { mutableStateOf(announcement.content) }

    if (isEditing) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {
                    isEditing = false
                    // Update announcement in the repository
                    val updatedAnnouncement = announcement.copy(title = title, content = content)
                    AnnouncementRepository.updateAnnouncement(updatedAnnouncement)
                }) {
                    Text("Save")
                }
                Button(onClick = {
                    isEditing = false
                    title = announcement.title
                    content = announcement.content
                }) {
                    Text("Cancel")
                }
            }
        }
    } else {
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Timestamp: ${announcement.timestamp}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Row {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = {
                            // Remove announcement from the repository
                            AnnouncementRepository.removeAnnouncement(announcement.id)
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
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