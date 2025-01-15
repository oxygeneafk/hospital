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
import com.eren.hospitalui.auth.User
import com.eren.hospitalui.auth.Report
import com.eren.hospitalui.utils.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMedicineScreen(databaseHelper: DatabaseHelper) {
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var selectedReason by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf("") }
    var isUserDropdownExpanded by remember { mutableStateOf(false) }
    var isReasonDropdownExpanded by remember { mutableStateOf(false) }
    var isDaysDropdownExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val users = databaseHelper.getAllUsers()
    val reasons = listOf("Medical", "Personal", "Other")
    val daysOptions = listOf("1", "2", "3", "5", "7", "10")

    var reports by remember { mutableStateOf<List<Report>>(emptyList()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var reportToUpdate by remember { mutableStateOf<Report?>(null) }
    var reportToDelete by remember { mutableStateOf<Report?>(null) }

    // Fetch all reports when the composable is first displayed
    LaunchedEffect(Unit) {
        reports = databaseHelper.getAllReports()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Send New Report",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // User selection dropdown
        ExposedDropdownMenuBox(
            expanded = isUserDropdownExpanded,
            onExpandedChange = { isUserDropdownExpanded = !isUserDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedUser?.username ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select User") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isUserDropdownExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = isUserDropdownExpanded,
                onDismissRequest = { isUserDropdownExpanded = false }
            ) {
                users.forEach { user ->
                    DropdownMenuItem(
                        text = { Text(user.username) },
                        onClick = {
                            selectedUser = user
                            isUserDropdownExpanded = false
                        }
                    )
                }
            }
        }

        // Reason selection dropdown
        ExposedDropdownMenuBox(
            expanded = isReasonDropdownExpanded,
            onExpandedChange = { isReasonDropdownExpanded = !isReasonDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedReason,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Reason") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isReasonDropdownExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = isReasonDropdownExpanded,
                onDismissRequest = { isReasonDropdownExpanded = false }
            ) {
                reasons.forEach { reason ->
                    DropdownMenuItem(
                        text = { Text(reason) },
                        onClick = {
                            selectedReason = reason
                            isReasonDropdownExpanded = false
                        }
                    )
                }
            }
        }

        // Days selection dropdown
        ExposedDropdownMenuBox(
            expanded = isDaysDropdownExpanded,
            onExpandedChange = { isDaysDropdownExpanded = !isDaysDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedDays,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Days of Rest") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDaysDropdownExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = isDaysDropdownExpanded,
                onDismissRequest = { isDaysDropdownExpanded = false }
            ) {
                daysOptions.forEach { days ->
                    DropdownMenuItem(
                        text = { Text(days) },
                        onClick = {
                            selectedDays = days
                            isDaysDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                val days = selectedDays.toIntOrNull()
                if (selectedUser != null && selectedReason.isNotBlank() && days != null && days > 0) {
                    databaseHelper.insertReport(
                        username = selectedUser!!.username,
                        title = selectedReason,
                        content = "Please rest for $days days. Reason: $selectedReason"
                    )
                    showToast(context, "Report sent successfully!")
                    // Fetch all reports again to update the list
                    reports = databaseHelper.getAllReports()
                } else {
                    showToast(context, "Please fill out all fields correctly.")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Report")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "All Reports",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reports) { report ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Title: ${report.title}", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "User: ${report.username}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = report.content, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(onClick = {
                                reportToUpdate = report
                                showUpdateDialog = true
                            }) {
                                Text("Update")
                            }
                            Button(onClick = {
                                reportToDelete = report
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

    if (showDeleteDialog && reportToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this report?") },
            confirmButton = {
                TextButton(onClick = {
                    databaseHelper.deleteReport(reportToDelete!!.id)
                    showToast(context, "Report deleted successfully!")
                    reports = databaseHelper.getAllReports()
                    showDeleteDialog = false
                    reportToDelete = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    reportToDelete = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showUpdateDialog && reportToUpdate != null) {
        var updatedReason by remember { mutableStateOf(reportToUpdate!!.title) }
        var updatedDays by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = { Text("Update Report") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Reason selection dropdown
                    ExposedDropdownMenuBox(
                        expanded = isReasonDropdownExpanded,
                        onExpandedChange = { isReasonDropdownExpanded = !isReasonDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = updatedReason,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Reason") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isReasonDropdownExpanded)
                            },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = isReasonDropdownExpanded,
                            onDismissRequest = { isReasonDropdownExpanded = false }
                        ) {
                            reasons.forEach { reason ->
                                DropdownMenuItem(
                                    text = { Text(reason) },
                                    onClick = {
                                        updatedReason = reason
                                        isReasonDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Days selection dropdown
                    ExposedDropdownMenuBox(
                        expanded = isDaysDropdownExpanded,
                        onExpandedChange = { isDaysDropdownExpanded = !isDaysDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = updatedDays,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Days of Rest") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDaysDropdownExpanded)
                            },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = isDaysDropdownExpanded,
                            onDismissRequest = { isDaysDropdownExpanded = false }
                        ) {
                            daysOptions.forEach { days ->
                                DropdownMenuItem(
                                    text = { Text(days) },
                                    onClick = {
                                        updatedDays = days
                                        isDaysDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val days = updatedDays.toIntOrNull()
                    if (updatedReason.isNotBlank() && days != null && days > 0) {
                        databaseHelper.updateReport(reportToUpdate!!.id, updatedReason, days)
                        showToast(context, "Report updated successfully!")
                        reports = databaseHelper.getAllReports()
                        showUpdateDialog = false
                        reportToUpdate = null
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
                    reportToUpdate = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}