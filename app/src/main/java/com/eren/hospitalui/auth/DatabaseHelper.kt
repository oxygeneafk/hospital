package com.eren.hospitalui.auth

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "hospital.db"
        private const val DATABASE_VERSION = 4 // Version number updated

        // Tablo ve sütun isimleri
        const val TABLE_USERS = "users"
        const val TABLE_APPOINTMENTS = "appointments"
        const val TABLE_ADMINS = "admins"
        const val TABLE_DOCTORS = "doctors"
        const val TABLE_REPORTS = "reports"
        const val TABLE_ANNOUNCEMENTS = "announcements" // New table for announcements

        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_SURNAME = "surname"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_BLOOD_GROUP = "blood_group"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_APPOINTMENT_ID = "id"
        const val COLUMN_APPOINTMENT_DATE = "date"
        const val COLUMN_APPOINTMENT_TIME = "time"
        const val COLUMN_DOCTOR_NAME = "doctor_name"
        const val COLUMN_APPOINTMENT_DEPARTMENT = "department"
        const val COLUMN_ADMIN_USERNAME = "adminusername"
        const val COLUMN_ADMIN_PASSWORD = "adminpassword"
        const val COLUMN_DOCTOR_ID = "id"
        const val COLUMN_DOCTOR_DEPARTMENT = "department"
        const val COLUMN_REPORT_ID = "id"
        const val COLUMN_REPORT_TITLE = "title"
        const val COLUMN_REPORT_CONTENT = "content"
        const val COLUMN_REPORT_USERNAME = "username"
        const val COLUMN_ANNOUNCEMENT_ID = "id" // New column for announcement ID
        const val COLUMN_ANNOUNCEMENT_TITLE = "title" // New column for announcement title
        const val COLUMN_ANNOUNCEMENT_CONTENT = "content" // New column for announcement content
        const val COLUMN_ANNOUNCEMENT_TIMESTAMP = "timestamp" // New column for announcement timestamp
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_USERS_TABLE = """
    CREATE TABLE $TABLE_USERS (
        $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_NAME TEXT NOT NULL,
        $COLUMN_SURNAME TEXT NOT NULL,
        $COLUMN_USERNAME TEXT NOT NULL UNIQUE,
        $COLUMN_PASSWORD TEXT NOT NULL,
        $COLUMN_BLOOD_GROUP TEXT,
        $COLUMN_ADDRESS TEXT
    )
    """

        val CREATE_APPOINTMENTS_TABLE = """
        CREATE TABLE $TABLE_APPOINTMENTS (
            $COLUMN_APPOINTMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_APPOINTMENT_DATE TEXT NOT NULL,
            $COLUMN_APPOINTMENT_TIME TEXT NOT NULL,
            $COLUMN_DOCTOR_NAME TEXT NOT NULL,
            $COLUMN_APPOINTMENT_DEPARTMENT TEXT NOT NULL
        )
    """

        val CREATE_ADMINS_TABLE = """
        CREATE TABLE $TABLE_ADMINS (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_ADMIN_USERNAME TEXT NOT NULL UNIQUE,
            $COLUMN_ADMIN_PASSWORD TEXT NOT NULL
        )
    """

        val CREATE_DOCTORS_TABLE = """
        CREATE TABLE $TABLE_DOCTORS (
            $COLUMN_DOCTOR_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT NOT NULL,
            $COLUMN_DOCTOR_DEPARTMENT TEXT NOT NULL
        )
    """

        val CREATE_REPORTS_TABLE = """
        CREATE TABLE $TABLE_REPORTS (
            $COLUMN_REPORT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_REPORT_TITLE TEXT NOT NULL,
            $COLUMN_REPORT_CONTENT TEXT NOT NULL,
            $COLUMN_REPORT_USERNAME TEXT NOT NULL
        )
    """

        val CREATE_ANNOUNCEMENTS_TABLE = """
        CREATE TABLE $TABLE_ANNOUNCEMENTS (
            $COLUMN_ANNOUNCEMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_ANNOUNCEMENT_TITLE TEXT NOT NULL,
            $COLUMN_ANNOUNCEMENT_CONTENT TEXT NOT NULL,
            $COLUMN_ANNOUNCEMENT_TIMESTAMP INTEGER NOT NULL
        )
    """

        db.execSQL(CREATE_USERS_TABLE)
        db.execSQL(CREATE_APPOINTMENTS_TABLE)
        db.execSQL(CREATE_ADMINS_TABLE)
        db.execSQL(CREATE_DOCTORS_TABLE)
        db.execSQL(CREATE_REPORTS_TABLE)
        db.execSQL(CREATE_ANNOUNCEMENTS_TABLE) // Execute the create table statement for announcements
        Log.d("DatabaseHelper", "Database created successfully!")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_APPOINTMENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ADMINS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DOCTORS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REPORTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ANNOUNCEMENTS") // Drop the announcements table if it exists
        onCreate(db)
    }

    /**
     * Yeni bir kullanıcı ekle
     */
    fun insertUser(name: String, surname: String, username: String, password: String, bloodGroup: String?, address: String?): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_SURNAME, surname)
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_BLOOD_GROUP, bloodGroup)
            put(COLUMN_ADDRESS, address)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result
    }

    fun insertAppointment(date: String, time: String, doctorName: String, department: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_APPOINTMENT_DATE, date)
            put(COLUMN_APPOINTMENT_TIME, time)
            put(COLUMN_DOCTOR_NAME, doctorName)
            put(COLUMN_APPOINTMENT_DEPARTMENT, department)
        }
        val result = db.insert(TABLE_APPOINTMENTS, null, values)
        db.close()
        Log.d("DatabaseHelper", "insertAppointment result: $result")
        if (result == -1L) {
            Log.e("DatabaseHelper", "Failed to insert appointment for $date at $time with doctor $doctorName in $department department")
        }
        return result
    }

    fun updateAppointment(id: Int, date: String, time: String, doctorName: String, department: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_APPOINTMENT_DATE, date)
            put(COLUMN_APPOINTMENT_TIME, time)
            put(COLUMN_DOCTOR_NAME, doctorName)
            put(COLUMN_APPOINTMENT_DEPARTMENT, department)
        }
        val rowsAffected = db.update(TABLE_APPOINTMENTS, values, "$COLUMN_APPOINTMENT_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected
    }

    fun deleteAppointment(id: Int): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_APPOINTMENTS, "$COLUMN_APPOINTMENT_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }

    fun isAppointmentTaken(date: String, time: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_APPOINTMENTS WHERE $COLUMN_APPOINTMENT_DATE = ? AND $COLUMN_APPOINTMENT_TIME = ?",
            arrayOf(date, time)
        )
        val isTaken = cursor.count > 0
        cursor.close()
        db.close()
        Log.d("DatabaseHelper", "isAppointmentTaken: $isTaken for $date at $time")
        return isTaken
    }

    fun getAllAppointments(): List<Appointment> {
        val appointments = mutableListOf<Appointment>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_APPOINTMENTS", null)

        if (cursor.moveToFirst()) {
            do {
                val appointment = Appointment(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APPOINTMENT_ID)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPOINTMENT_DATE)),
                    time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPOINTMENT_TIME)),
                    doctorName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR_NAME)),
                    department = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPOINTMENT_DEPARTMENT))
                )
                appointments.add(appointment)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return appointments
    }

    fun getDoctorsByDepartment(department: String): List<Doctor> {
        val doctorList = mutableListOf<Doctor>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_DOCTORS,
            null,
            "$COLUMN_DOCTOR_DEPARTMENT = ?",
            arrayOf(department),
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val dept = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR_DEPARTMENT))
                doctorList.add(Doctor(id, name, dept))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return doctorList
    }

    /**
     * Yeni bir doktor ekle
     */
    fun insertDoctor(name: String, department: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_DOCTOR_DEPARTMENT, department)
        }
        val result = db.insert(TABLE_DOCTORS, null, values)
        db.close()
        return result
    }



    fun isDoctorExists(name: String, department: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_DOCTORS WHERE $COLUMN_NAME = ? AND $COLUMN_DOCTOR_DEPARTMENT = ?"
        val cursor = db.rawQuery(query, arrayOf(name, department))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getAllDoctors(): List<Doctor> {
        val doctors = mutableListOf<Doctor>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_DOCTORS", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val department = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOCTOR_DEPARTMENT))
            doctors.add(Doctor(id, name, department))
        }
        cursor.close()
        return doctors
    }

    fun addDoctor(name: String, department: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_DOCTOR_DEPARTMENT, department)
        }
        return db.insert(TABLE_DOCTORS, null, values)
    }

    fun updateDoctor(id: Int, name: String, department: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_DOCTOR_DEPARTMENT, department)
        }
        val rowsAffected = db.update(TABLE_DOCTORS, values, "$COLUMN_DOCTOR_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected
    }

    fun deleteDoctor(id: Int): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_DOCTORS, "$COLUMN_DOCTOR_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }

    /**
     * Kullanıcıyı kontrol et (giriş doğrulama)
     */
    fun getUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password),
            null,
            null,
            null
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists
    }

    fun getUserDetails(username: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null,
            null,
            null
        )
        val user = if (cursor.moveToFirst()) {
            User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                surname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURNAME)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                bloodGroup = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BLOOD_GROUP)),
                address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS))
            )
        } else {
            null
        }
        cursor.close()
        db.close()
        return user
    }

    /**
     * Kullanıcı bilgilerini güncelle
     */
    fun updateUser(id: Int, name: String, surname: String, username: String, password: String, bloodGroup: String, address: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_SURNAME, surname)
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_BLOOD_GROUP, bloodGroup)
            put(COLUMN_ADDRESS, address)
        }
        val rowsAffected = db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected
    }

    /**
     * Kullanıcıyı sil
     */
    fun deleteUser(id: Int): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_USERS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }

    /**
     * Tüm kullanıcıları listele
     */
    fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_USERS, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val surname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SURNAME))
                val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
                val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
                val bloodGroup = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BLOOD_GROUP))
                val address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS))
                userList.add(User(id, name, surname, username, password, bloodGroup, address))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return userList
    }

    /**
     * Yeni bir admin ekle
     */
    fun insertAdmin(username: String, password: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ADMIN_USERNAME, username)
            put(COLUMN_ADMIN_PASSWORD, password)
        }
        val result = db.insert(TABLE_ADMINS, null, values)
        db.close()
        return result
    }

    /**
     * Admini kontrol et (giriş doğrulama)
     */
    fun getAdmin(username: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_ADMINS,
            arrayOf(COLUMN_ID),
            "$COLUMN_ADMIN_USERNAME = ? AND $COLUMN_ADMIN_PASSWORD = ?",
            arrayOf(username, password),
            null,
            null,
            null
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists
    }

    fun insertReport(username: String, title: String, content: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_REPORT_USERNAME, username)
            put(COLUMN_REPORT_TITLE, title)
            put(COLUMN_REPORT_CONTENT, content)
        }
        db.insert(TABLE_REPORTS, null, values)
        db.close()
    }

    fun getUserReports(username: String): List<Report> {
        val reports = mutableListOf<Report>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_REPORTS WHERE $COLUMN_REPORT_USERNAME = ?", arrayOf(username))
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REPORT_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REPORT_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REPORT_CONTENT))
            reports.add(Report(id, title, content,username))
        }
        cursor.close()
        db.close()
        return reports
    }

    fun getAllReports(): List<Report> {
        val reports = mutableListOf<Report>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id, title, content, username FROM reports", null)
        if (cursor.moveToFirst()) {
            do {
                val report = Report(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    content = cursor.getString(cursor.getColumnIndexOrThrow("content")),
                    username = cursor.getString(cursor.getColumnIndexOrThrow("username")) // Yeni eklenen sütun
                )
                reports.add(report)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return reports
    }

    fun deleteReport(reportId: Int) {
        val db = writableDatabase
        db.delete(TABLE_REPORTS, "$COLUMN_REPORT_ID = ?", arrayOf(reportId.toString()))
        db.close()
    }

    fun updateReport(reportId: Int, reason: String, days: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_REPORT_TITLE, reason)
            put(COLUMN_REPORT_CONTENT, "Please rest for $days days. Reason: $reason")
        }
        db.update(TABLE_REPORTS, values, "$COLUMN_REPORT_ID = ?", arrayOf(reportId.toString()))
        db.close()
    }

    /**
     * Yeni bir duyuru ekle
     */
    fun insertAnnouncement(title: String, content: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ANNOUNCEMENT_TITLE, title)
            put(COLUMN_ANNOUNCEMENT_CONTENT, content)
            put(COLUMN_ANNOUNCEMENT_TIMESTAMP, System.currentTimeMillis())
        }
        val result = db.insert(TABLE_ANNOUNCEMENTS, null, values)
        db.close()
        return result
    }

    /**
     * Duyuru güncelle
     */
    fun updateAnnouncement(id: Int, title: String, content: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ANNOUNCEMENT_TITLE, title)
            put(COLUMN_ANNOUNCEMENT_CONTENT, content)
        }
        val rowsAffected = db.update(TABLE_ANNOUNCEMENTS, values, "$COLUMN_ANNOUNCEMENT_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected
    }

    /**
     * Duyuru sil
     */
    fun deleteAnnouncement(id: Int): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_ANNOUNCEMENTS, "$COLUMN_ANNOUNCEMENT_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }

    /**
     * Tüm duyuruları al
     */
    fun getAllAnnouncements(): List<Announcement> {
        val announcements = mutableListOf<Announcement>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_ANNOUNCEMENTS", null)

        if (cursor.moveToFirst()) {
            do {
                val announcement = Announcement(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANNOUNCEMENT_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANNOUNCEMENT_TITLE)),
                    content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANNOUNCEMENT_CONTENT)),
                    timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ANNOUNCEMENT_TIMESTAMP))
                )
                announcements.add(announcement)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return announcements
    }

}
data class User(
    val id: Int,
    val name: String,
    val surname: String,
    val username: String,
    val password: String,
    val bloodGroup: String?,
    val address: String?
)

data class Appointment(
    val id: Int,
    var date: String,
    var time: String,
    var doctorName: String,
    var department: String
)

data class Doctor(
    val id: Int,
    val name: String,
    val department: String
)

data class Report(
    val id: Int,
    val title: String,
    val content: String,
    val username: String
)

data class Announcement(
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: Long
)