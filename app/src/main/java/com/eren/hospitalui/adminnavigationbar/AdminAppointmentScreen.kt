package com.eren.hospitalui.adminnavigationbar

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eren.hospitalui.auth.Appointment
import com.eren.hospitalui.auth.DatabaseHelper
import com.eren.hospitalui.utils.showToast
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAppointmentScreen(databaseHelper: DatabaseHelper) {
    val context = LocalContext.current
    val appointments = remember { mutableStateListOf<Appointment>() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var appointmentToDelete by remember { mutableStateOf<Appointment?>(null) }
    var appointmentToUpdate by remember { mutableStateOf<Appointment?>(null) }
    var editDate by remember { mutableStateOf("") }
    var editTime by remember { mutableStateOf("") }
    var editDoctorName by remember { mutableStateOf("") }
    var editDepartment by remember { mutableStateOf("") }

    val allDoctors = databaseHelper.getAllDoctors().map { it.name }
    val departments = listOf("Kulak Burun Boğaz", "Kardiyoloji", "Ortopedi", "Dahiliye", "Genel Cerrahi")

    // For time slots
    val availableTimes = remember { generateTimeSlots() }

    LaunchedEffect(Unit) {
        appointments.clear()
        appointments.addAll(databaseHelper.getAllAppointments())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Admin Panel - Manage Appointments",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (appointments.isEmpty()) {
            Text("No appointments available.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(appointments) { appointment ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Date: ${appointment.date}", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Time: ${appointment.time}", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Doctor: ${appointment.doctorName}", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Department: ${appointment.department}", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(onClick = {
                                    appointmentToUpdate = appointment
                                    editDate = appointment.date
                                    editTime = appointment.time
                                    editDoctorName = appointment.doctorName
                                    editDepartment = appointment.department
                                    showUpdateDialog = true
                                }) {
                                    Text("Update")
                                }
                                Button(onClick = {
                                    appointmentToDelete = appointment
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
    }

    if (showDeleteDialog && appointmentToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this appointment?") },
            confirmButton = {
                TextButton(onClick = {
                    databaseHelper.deleteAppointment(appointmentToDelete!!.id)
                    showToast(context, "Appointment deleted successfully!")
                    appointments.remove(appointmentToDelete)
                    showDeleteDialog = false
                    appointmentToDelete = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    appointmentToDelete = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showUpdateDialog && appointmentToUpdate != null) {
        var isDoctorDropdownExpanded by remember { mutableStateOf(false) }
        var isDepartmentDropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = { Text("Update Appointment") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = editDate,
                        onValueChange = {},
                        label = { Text("Select Date") },
                        modifier = Modifier.fillMaxWidth().clickable {
                            val calendar = Calendar.getInstance()
                            val datePickerDialog = DatePickerDialog(
                                context,
                                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                                    val date = "$year-${month + 1}-$dayOfMonth"
                                    editDate = date
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                            datePickerDialog.show()
                        },
                        readOnly = true
                    )

                    ExposedDropdownMenuBox(
                        expanded = isDoctorDropdownExpanded,
                        onExpandedChange = { isDoctorDropdownExpanded = !isDoctorDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = editTime,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Time") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDoctorDropdownExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = isDoctorDropdownExpanded,
                            onDismissRequest = { isDoctorDropdownExpanded = false }
                        ) {
                            availableTimes.forEach { time ->
                                DropdownMenuItem(
                                    text = { Text(time) },
                                    onClick = {
                                        editTime = time
                                        isDoctorDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = isDepartmentDropdownExpanded,
                        onExpandedChange = { isDepartmentDropdownExpanded = !isDepartmentDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = editDoctorName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Doctor Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDepartmentDropdownExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = isDepartmentDropdownExpanded,
                            onDismissRequest = { isDepartmentDropdownExpanded = false }
                        ) {
                            allDoctors.forEach { doctor ->
                                DropdownMenuItem(
                                    text = { Text(doctor) },
                                    onClick = {
                                        editDoctorName = doctor
                                        isDepartmentDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = isDepartmentDropdownExpanded,
                        onExpandedChange = { isDepartmentDropdownExpanded = !isDepartmentDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = editDepartment,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Department") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDepartmentDropdownExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = isDepartmentDropdownExpanded,
                            onDismissRequest = { isDepartmentDropdownExpanded = false }
                        ) {
                            departments.forEach { department ->
                                DropdownMenuItem(
                                    text = { Text(department) },
                                    onClick = {
                                        editDepartment = department
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
                    if (editDate.isNotBlank() && editTime.isNotBlank() && editDoctorName.isNotBlank() && editDepartment.isNotBlank()) {
                        databaseHelper.updateAppointment(
                            appointmentToUpdate!!.id, editDate, editTime, editDoctorName, editDepartment
                        )
                        appointments[appointments.indexOf(appointmentToUpdate)] = appointmentToUpdate!!.copy(
                            date = editDate,
                            time = editTime,
                            doctorName = editDoctorName,
                            department = editDepartment
                        )
                        showUpdateDialog = false
                        appointmentToUpdate = null
                        showToast(context, "Appointment updated successfully!")
                    } else {
                        showToast(context, "Please fill out all fields.")
                    }
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showUpdateDialog = false
                    appointmentToUpdate = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Generate available times in 30-minute intervals
fun generateTimeSlots(): List<String> {
    val timeSlots = mutableListOf<String>()
    val calendar = Calendar.getInstance()
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    calendar.set(Calendar.HOUR_OF_DAY, 8)
    calendar.set(Calendar.MINUTE, 0)

    for (i in 0 until 24) {
        timeSlots.add(timeFormatter.format(calendar.time))
        calendar.add(Calendar.MINUTE, 30)
    }

    return timeSlots
}