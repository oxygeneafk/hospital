package com.eren.hospitalui.navigationbar

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eren.hospital.R
import com.eren.hospitalui.auth.DatabaseHelper
import com.eren.hospitalui.auth.User
import com.eren.hospitalui.home.LoginViewModel
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme

@Composable
fun AccountScreen(
    databaseHelper: DatabaseHelper,
    loginViewModel: LoginViewModel
) {
    val currentUser = loginViewModel.loggedInUser.collectAsState().value // Giriş yapmış kullanıcıyı al

    var showUpdateScreen by remember { mutableStateOf(false) }

    if (showUpdateScreen && currentUser != null) {
        UpdateUserScreen(
            user = currentUser,
            onUpdate = { id, newName, newSurname, newUsername, newPassword, newBloodGroup, newAddress ->
                databaseHelper.updateUser(
                    id,
                    newName,
                    newSurname,
                    newUsername,
                    newPassword,
                    newBloodGroup,
                    newAddress
                )
                showUpdateScreen = false
            },
            onCancel = { showUpdateScreen = false }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color(0xFFFFFFFF))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Account",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3E50) // Başlık rengi
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Image(
                    painter = painterResource(id = R.drawable.hospital_logo),
                    contentDescription = "Hospital Logo",
                    modifier = Modifier.size(80.dp) // Logo boyutunu küçülttük
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Account Information",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3E50) // Başlık rengi
                ),
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Kullanıcı bilgilerini şık bir şekilde kutular içinde göster
            currentUser?.let { user ->
                AccountInfoCard(label = "Name", value = user.name)
                AccountInfoCard(label = "Surname", value = user.surname)
                AccountInfoCard(label = "Username", value = user.username)
                AccountInfoCard(label = "Password", value = user.password)
                AccountInfoCard(label = "Blood Group", value = user.bloodGroup ?: "N/A")
                AccountInfoCard(label = "Address", value = user.address ?: "N/A")

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showUpdateScreen = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Information")
                }
            } ?: Text("No user information available.")
        }
    }
}

@Composable
fun AccountInfoCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp) // CardElevation kullanarak Dp değerini belirtiyoruz
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$label:",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3E50)
                ),
                modifier = Modifier.weight(0.3f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                modifier = Modifier.weight(0.7f)
            )
        }
    }
}

@Composable
fun UpdateUserScreen(
    user: User,
    onUpdate: (
        Int,
        String,
        String,
        String,
        String,
        String,
        String
    ) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var surname by remember { mutableStateOf(user.surname) }
    var username by remember { mutableStateOf(user.username) }
    var password by remember { mutableStateOf(user.password) }
    var bloodGroup by remember { mutableStateOf(user.bloodGroup ?: "") } // Kan grubu boşsa "" olarak başlatıyoruz
    var address by remember { mutableStateOf(user.address ?: "") } // Adres boşsa "" olarak başlatıyoruz

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Update Information", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = surname,
            onValueChange = { surname = it },
            label = { Text("Surname") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Kan grubu inputu
        OutlinedTextField(
            value = bloodGroup,
            onValueChange = { bloodGroup = it },
            label = { Text("Blood Group") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Adres inputu
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onCancel) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    onUpdate(
                        user.id,
                        name,
                        surname,
                        username,
                        password,
                        bloodGroup, // Kan grubunu doğru şekilde gönderiyoruz
                        address // Adresi doğru şekilde gönderiyoruz
                    )
                }
            ) {
                Text("Update")
            }
        }
    }
}


/*@Composable
fun UpdateUserScreen(
    user: User,
    onUpdate: (
        Int,
        String,
        String,
        String,
        String,
        String,
        String
    ) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var surname by remember { mutableStateOf(user.surname) }
    var username by remember { mutableStateOf(user.username) }
    var password by remember { mutableStateOf(user.password) }
    var bloodGroup by remember { mutableStateOf(user.bloodGroup ?: "") } // Kan grubu boşsa "" olarak başlatıyoruz
    var address by remember { mutableStateOf(user.address ?: "") } // Adres boşsa "" olarak başlatıyoruz

    var showUpdateScreen by remember { mutableStateOf(true) }

    if (showUpdateScreen) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color(0xFFFFFFFF))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Update Information",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D3E50) // Başlık rengi
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Image(
                    painter = painterResource(id = R.drawable.hospital_logo),
                    contentDescription = "Hospital Logo",
                    modifier = Modifier.size(80.dp) // Logo boyutunu küçülttük
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Kullanıcı bilgilerini güncellemek için alanlar
            AccountInfoCard(label = "Name", value = name, onValueChange = { name = it })
            AccountInfoCard(label = "Surname", value = surname, onValueChange = { surname = it })
            AccountInfoCard(label = "Username", value = username, onValueChange = { username = it })
            AccountInfoCard(label = "Password", value = password, onValueChange = { password = it })
            AccountInfoCard(label = "Blood Group", value = bloodGroup, onValueChange = { bloodGroup = it })
            AccountInfoCard(label = "Address", value = address, onValueChange = { address = it })

            Spacer(modifier = Modifier.height(24.dp))

            // Güncelleme ve iptal butonları
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    onUpdate(
                        user.id,
                        name,
                        surname,
                        username,
                        password,
                        bloodGroup,
                        address
                    )
                    showUpdateScreen = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update")
            }
        }
    }
}

@Composable
fun AccountInfoCard(label: String, value: String, onValueChange: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp) // CardElevation kullanarak Dp değerini belirtiyoruz
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "$label:",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3E50)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text("Enter $label") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
*/