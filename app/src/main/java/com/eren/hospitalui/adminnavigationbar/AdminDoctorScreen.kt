package com.eren.hospitalui.adminnavigationbar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.eren.hospitalui.auth.DatabaseHelper
import com.eren.hospitalui.auth.Doctor
import com.eren.hospitalui.utils.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDoctorScreen(databaseHelper: DatabaseHelper) {
    var selectedDoctor by remember { mutableStateOf<Doctor?>(null) }
    var selectedDepartment by remember { mutableStateOf("") }
    var isDoctorDropdownExpanded by remember { mutableStateOf(false) }
    var isDepartmentDropdownExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val doctors = databaseHelper.getAllDoctors()
    val departments = listOf("Kulak Burun Boğaz", "Kardiyoloji", "Ortopedi", "Dahiliye", "Genel Cerrahi")

    var doctorRecords by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var doctorToUpdate by remember { mutableStateOf<Doctor?>(null) }
    var doctorToDelete by remember { mutableStateOf<Doctor?>(null) }

    LaunchedEffect(Unit) {
        doctorRecords = databaseHelper.getAllDoctors()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add New Doctor Record",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Doctor selection dropdown
        ExposedDropdownMenuBox(
            expanded = isDoctorDropdownExpanded,
            onExpandedChange = { isDoctorDropdownExpanded = !isDoctorDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedDoctor?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Doctor") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDoctorDropdownExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = isDoctorDropdownExpanded,
                onDismissRequest = { isDoctorDropdownExpanded = false }
            ) {
                doctors.forEach { doctor ->
                    DropdownMenuItem(
                        text = { Text(doctor.name) },
                        onClick = {
                            selectedDoctor = doctor
                            isDoctorDropdownExpanded = false
                        }
                    )
                }
            }
        }

        // Department selection dropdown
        ExposedDropdownMenuBox(
            expanded = isDepartmentDropdownExpanded,
            onExpandedChange = { isDepartmentDropdownExpanded = !isDepartmentDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedDepartment,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Department") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDepartmentDropdownExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = isDepartmentDropdownExpanded,
                onDismissRequest = { isDepartmentDropdownExpanded = false }
            ) {
                departments.forEach { department ->
                    DropdownMenuItem(
                        text = { Text(department) },
                        onClick = {
                            selectedDepartment = department
                            isDepartmentDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                if (selectedDoctor != null && selectedDepartment.isNotBlank()) {
                    databaseHelper.addDoctor(
                        name = selectedDoctor!!.name,
                        department = selectedDepartment // Ensure department is passed correctly here
                    )
                    showToast(context, "Doctor record added successfully!")
                    doctorRecords = databaseHelper.getAllDoctors()
                } else {
                    showToast(context, "Please fill out all fields correctly.")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Doctor Record")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "All Doctor Records",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(doctorRecords) { doctor ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Name: ${doctor.name}", style = MaterialTheme.typography.titleMedium)
                        Text(text = "Department: ${doctor.department}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(onClick = {
                                doctorToUpdate = doctor
                                showUpdateDialog = true
                            }) {
                                Text("Update")
                            }
                            Button(onClick = {
                                doctorToDelete = doctor
                                showDeleteDialog = true
                            }) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && doctorToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this doctor record?") },
            confirmButton = {
                TextButton(onClick = {
                    databaseHelper.deleteDoctor(doctorToDelete!!.id)
                    showToast(context, "Doctor record deleted successfully!")
                    doctorRecords = databaseHelper.getAllDoctors()
                    showDeleteDialog = false
                    doctorToDelete = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    doctorToDelete = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showUpdateDialog && doctorToUpdate != null) {
        var updatedDepartment by remember { mutableStateOf(doctorToUpdate!!.department) }

        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = { Text("Update Doctor Record") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = isDepartmentDropdownExpanded,
                        onExpandedChange = { isDepartmentDropdownExpanded = !isDepartmentDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = updatedDepartment,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Department") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDepartmentDropdownExpanded)
                            },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = isDepartmentDropdownExpanded,
                            onDismissRequest = { isDepartmentDropdownExpanded = false }
                        ) {
                            departments.forEach { department ->
                                DropdownMenuItem(
                                    text = { Text(department) },
                                    onClick = {
                                        updatedDepartment = department
                                        isDepartmentDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (updatedDepartment.isNotBlank()) {
                        databaseHelper.updateDoctor(doctorToUpdate!!.id, doctorToUpdate!!.name, updatedDepartment)
                        showToast(context, "Doctor record updated successfully!")
                        doctorRecords = databaseHelper.getAllDoctors()
                        showUpdateDialog = false
                        doctorToUpdate = null
                    } else {
                        showToast(context, "Please fill out all fields correctly.")
                    }
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showUpdateDialog = false
                    doctorToUpdate = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}