package com.eren.hospitalui.adminnavigationbar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eren.hospitalui.auth.DatabaseHelper
import com.eren.hospitalui.auth.User

@Composable
fun AdminAccountScreen(databaseHelper: DatabaseHelper) {
    var users by remember { mutableStateOf(listOf<User>()) }
    var showUpdateScreen by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        users = databaseHelper.getAllUsers()
    }

    if (showUpdateScreen && selectedUser != null) {
        UpdateUserScreen(
            user = selectedUser!!,
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
                users = databaseHelper.getAllUsers()
                showUpdateScreen = false
            },
            onCancel = { showUpdateScreen = false }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Admin Account Management",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(users) { user ->
                    UserCard(
                        user = user,
                        onUpdateClick = {
                            selectedUser = user
                            showUpdateScreen = true
                        },
                        onDeleteClick = {
                            selectedUser = user
                            showDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showDialog && selectedUser != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete User") },
            text = { Text("Are you sure you want to delete this user?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        databaseHelper.deleteUser(selectedUser!!.id)
                        users = databaseHelper.getAllUsers()
                        showDialog = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun UserCard(user: User, onUpdateClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Name: ${user.name}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Surname: ${user.surname}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Username: ${user.username}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Blood Group: ${user.bloodGroup ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Address: ${user.address ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onUpdateClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Update")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete")
                }
            }
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
    var bloodGroup by remember { mutableStateOf(user.bloodGroup ?: "") }
    var address by remember { mutableStateOf(user.address ?: "") }

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

        OutlinedTextField(
            value = bloodGroup,
            onValueChange = { bloodGroup = it },
            label = { Text("Blood Group") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                        bloodGroup,
                        address
                    )
                }
            ) {
                Text("Update")
            }
        }
    }
}