package com.eren.hospitalui.navigationbar

import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.eren.hospitalui.auth.Doctor
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AppointmentScreen(databaseHelper: DatabaseHelper) {
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var selectedDepartment by remember { mutableStateOf("") }
    var selectedDoctor by remember { mutableStateOf("") }
    var appointmentBooked by remember { mutableStateOf(false) }
    var showAppointments by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val calendar = Calendar.getInstance()
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val availableTimes = remember { generateTimeSlots() }
    val departments = listOf("Kulak Burun Boğaz", "Kardiyoloji", "Ortopedi", "Dahiliye", "Genel Cerrahi")
    val doctors = remember { mutableStateListOf<Doctor>() }
    val appointments = remember { mutableStateListOf<Appointment>() }

    val showDatePicker = {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = dateFormatter.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Appointment Booking",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            label = { Text("Select Date") },
            placeholder = { Text("yyyy-MM-dd") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedDate.isNotEmpty()) {
            Text("Available Times:", style = MaterialTheme.typography.bodyLarge)

            LazyColumn(modifier = Modifier.fillMaxHeight(0.2f)) {
                items(availableTimes) { time ->
                    val isSelected = selectedTime == time
                    Button(
                        onClick = { selectedTime = time },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = time)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Select Department:", style = MaterialTheme.typography.bodyLarge)

            LazyColumn(modifier = Modifier.fillMaxHeight(0.2f)) {
                items(departments) { department ->
                    val isSelected = selectedDepartment == department
                    Button(
                        onClick = {
                            selectedDepartment = department
                            doctors.clear()
                            doctors.addAll(databaseHelper.getDoctorsByDepartment(department))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = department)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedDepartment.isNotEmpty()) {
                Text("Select Doctor:", style = MaterialTheme.typography.bodyLarge)

                LazyColumn(modifier = Modifier.fillMaxHeight(0.2f)) {
                    items(doctors) { doctor ->
                        val isSelected = selectedDoctor == doctor.name
                        Button(
                            onClick = { selectedDoctor = doctor.name },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = doctor.name)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty() && selectedDepartment.isNotEmpty() && selectedDoctor.isNotEmpty()) {
                    val isTaken = databaseHelper.isAppointmentTaken(selectedDate, selectedTime)
                    if (!isTaken) {
                        val result = databaseHelper.insertAppointment(selectedDate, selectedTime, selectedDoctor, selectedDepartment)
                        if (result != -1L) {
                            // Randevu başarılı ise tüm seçimleri temizle
                            appointmentBooked = true
                            selectedDate = ""
                            selectedTime = ""
                            selectedDepartment = ""
                            selectedDoctor = ""
                            doctors.clear()
                            Toast.makeText(context, "Appointment booked successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to book the appointment", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "This slot is already taken", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please select a valid date, time, department, and doctor", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text("Book Appointment")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                showAppointments = !showAppointments
                if (showAppointments) {
                    appointments.clear()
                    appointments.addAll(databaseHelper.getAllAppointments())
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White
            )
        ) {
            Text(if (showAppointments) "Hide Appointments" else "Show My Appointments")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showAppointments) {
            Text(
                text = "My Appointments",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(appointments) { appointment ->
                    AppointmentCard(appointment = appointment)
                }
            }
        }

        if (appointmentBooked) {
            Text(
                text = "Appointment booked successfully!",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Green
            )
        }
    }
}

@Composable
fun AppointmentCard(appointment: Appointment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Date: ${appointment.date}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Time: ${appointment.time}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Doctor: ${appointment.doctorName}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Department: ${appointment.department}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

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