package com.eren.hospitalui.adminnavigationbar

import android.content.Context
import android.widget.Toast
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
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var appointmentToDelete by remember { mutableStateOf<Appointment?>(null) }

    var isEditing by remember { mutableStateOf(false) }
    var appointmentToEdit by remember { mutableStateOf<Appointment?>(null) }
    var editDate by remember { mutableStateOf("") }
    var editTime by remember { mutableStateOf("") }
    var editDoctorName by remember { mutableStateOf("") }
    var editDepartment by remember { mutableStateOf("") }

    // Fetch all doctors and departments for dropdown menus
    val allDoctors = databaseHelper.getAllDoctors().map { it.name }
    val departments = listOf("Kulak Burun Boğaz", "Kardiyoloji", "Ortopedi", "Dahiliye", "Genel Cerrahi")

    // For time slots
    val availableTimes = remember { generateTimeSlots() }

    LaunchedEffect(Unit) {
        appointments.clear()
        appointments.addAll(databaseHelper.getAllAppointments())
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        appointmentToDelete?.let {
                            databaseHelper.deleteAppointment(it.id)
                            appointments.remove(it)
                        }
                        showDeleteConfirmation = false
                        showToast(context, "Appointment deleted successfully!")
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("No")
                }
            },
            title = { Text("Delete Appointment") },
            text = { Text("Are you sure you want to delete this appointment?") }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
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
                    var isEditingLocal by remember { mutableStateOf(false) }
                    var editDateLocal by remember { mutableStateOf(appointment.date) }
                    var editTimeLocal by remember { mutableStateOf(appointment.time) }
                    var editDoctorNameLocal by remember { mutableStateOf(appointment.doctorName) }
                    var editDepartmentLocal by remember { mutableStateOf(appointment.department) }

                    // Doctor and Department dropdown
                    var isDoctorDropdownExpanded by remember { mutableStateOf(false) }
                    var isDepartmentDropdownExpanded by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (isEditingLocal) {
                                // Date Picker
                                OutlinedTextField(
                                    value = editDateLocal,
                                    onValueChange = {},
                                    label = { Text("Select Date") },
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        val calendar = Calendar.getInstance()
                                        val datePickerDialog = DatePickerDialog(
                                            context,
                                            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                                                val date = "$year-${month + 1}-$dayOfMonth"
                                                editDateLocal = date
                                            },
                                            calendar.get(Calendar.YEAR),
                                            calendar.get(Calendar.MONTH),
                                            calendar.get(Calendar.DAY_OF_MONTH)
                                        )
                                        datePickerDialog.show()
                                    },
                                    readOnly = true
                                )

                                // Time Slot Picker
                                ExposedDropdownMenuBox(
                                    expanded = isDoctorDropdownExpanded,
                                    onExpandedChange = { isDoctorDropdownExpanded = !isDoctorDropdownExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = editTimeLocal,
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
                                                    editTimeLocal = time
                                                    isDoctorDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                // Doctor Dropdown
                                ExposedDropdownMenuBox(
                                    expanded = isDepartmentDropdownExpanded,
                                    onExpandedChange = { isDepartmentDropdownExpanded = !isDepartmentDropdownExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = editDoctorNameLocal,
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
                                                    editDoctorNameLocal = doctor
                                                    isDepartmentDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                // Department Dropdown
                                ExposedDropdownMenuBox(
                                    expanded = isDepartmentDropdownExpanded,
                                    onExpandedChange = { isDepartmentDropdownExpanded = !isDepartmentDropdownExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = editDepartmentLocal,
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
                                                    editDepartmentLocal = department
                                                    isDepartmentDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        onClick = {
                                            if (editDateLocal.isNotBlank() && editTimeLocal.isNotBlank() && editDoctorNameLocal.isNotBlank() && editDepartmentLocal.isNotBlank()) {
                                                databaseHelper.updateAppointment(
                                                    appointment.id, editDateLocal, editTimeLocal, editDoctorNameLocal, editDepartmentLocal
                                                )
                                                appointments[appointments.indexOf(appointment)] = appointment.copy(
                                                    date = editDateLocal,
                                                    time = editTimeLocal,
                                                    doctorName = editDoctorNameLocal,
                                                    department = editDepartmentLocal
                                                )
                                                isEditingLocal = false
                                                showToast(context, "Appointment updated successfully!")
                                            } else {
                                                showToast(context, "Please fill out all fields.")
                                            }
                                        }
                                    ) {
                                        Text("Save")
                                    }

                                    Button(
                                        onClick = { isEditingLocal = false }
                                    ) {
                                        Text("Cancel")
                                    }
                                }
                            } else {
                                Text(text = "Date: ${appointment.date}", style = MaterialTheme.typography.bodyLarge)
                                Text(text = "Time: ${appointment.time}", style = MaterialTheme.typography.bodyLarge)
                                Text(text = "Doctor: ${appointment.doctorName}", style = MaterialTheme.typography.bodyLarge)
                                Text(text = "Department: ${appointment.department}", style = MaterialTheme.typography.bodyLarge)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    IconButton(onClick = {
                                        isEditingLocal = true
                                    }) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Edit Appointment")
                                    }
                                    IconButton(onClick = {
                                        showDeleteConfirmation = true
                                        appointmentToDelete = appointment
                                    }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete Appointment", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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
