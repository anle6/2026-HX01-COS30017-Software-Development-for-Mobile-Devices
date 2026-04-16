package com.example.petstrack

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddCalendarNoteActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: CalendarDatabase
    private var selectedCalendar = Calendar.getInstance()
    private var noteId: Int = 0
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_calendar_note)

        auth = FirebaseAuth.getInstance()
        database = CalendarDatabase.getDatabase(this)

        val noteTitleEditText = findViewById<TextInputEditText>(R.id.noteTitleEditText)
        val noteContentEditText = findViewById<TextInputEditText>(R.id.noteContentEditText)
        val btnSelectDateTime = findViewById<Button>(R.id.btnSelectDateTime)
        val tvSelectedDateTime = findViewById<TextView>(R.id.tvSelectedDateTime)
        val btnSaveNote = findViewById<Button>(R.id.btnSaveNote)

        // Check if we are in edit mode
        noteId = intent.getIntExtra("NOTE_ID", 0)
        if (noteId != 0) {
            isEditMode = true
            noteTitleEditText.setText(intent.getStringExtra("NOTE_TITLE"))
            noteContentEditText.setText(intent.getStringExtra("NOTE_CONTENT"))
            val timestamp = intent.getLongExtra("NOTE_TIMESTAMP", System.currentTimeMillis())
            selectedCalendar.timeInMillis = timestamp
            
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            tvSelectedDateTime.text = format.format(selectedCalendar.time)
            btnSaveNote.text = "Update Note"
        }

        btnSelectDateTime.setOnClickListener {
            val currentCalendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                selectedCalendar.set(Calendar.YEAR, year)
                selectedCalendar.set(Calendar.MONTH, month)
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                TimePickerDialog(this, { _, hourOfDay, minute ->
                    selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedCalendar.set(Calendar.MINUTE, minute)
                    selectedCalendar.set(Calendar.SECOND, 0)

                    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    tvSelectedDateTime.text = format.format(selectedCalendar.time)
                }, currentCalendar.get(Calendar.HOUR_OF_DAY), currentCalendar.get(Calendar.MINUTE), true).show()

            }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnSaveNote.setOnClickListener {
            val title = noteTitleEditText.text.toString().trim()
            val content = noteContentEditText.text.toString().trim()
            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val note = CalendarNote(
                id = noteId,
                title = title,
                content = content,
                timestamp = selectedCalendar.timeInMillis,
                userId = userId
            )

            CoroutineScope(Dispatchers.IO).launch {
                if (isEditMode) {
                    database.calendarNoteDao().updateNote(note)
                } else {
                    database.calendarNoteDao().insertNote(note)
                }
                scheduleNotification(title, content, selectedCalendar.timeInMillis)
                runOnUiThread {
                    val message = if (isEditMode) "Note updated!" else "Note saved and reminder set!"
                    Toast.makeText(this@AddCalendarNoteActivity, message, Toast.LENGTH_SHORT).show()
                    
                    val intent = Intent(this@AddCalendarNoteActivity, MainActivity::class.java)
                    intent.putExtra("NAVIGATE_TO", R.id.nav_calendar)
                    intent.putExtra("SELECTED_DATE", selectedCalendar.timeInMillis)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun scheduleNotification(title: String, message: String, timeInMillis: Long) {
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            timeInMillis.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }
}