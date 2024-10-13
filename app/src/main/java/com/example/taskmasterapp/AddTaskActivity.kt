package com.example.taskmasterapp

import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmasterapp.databinding.ActivityAddTaskBinding
import com.example.taskmasterapp.models.Task
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var task: Task
    private lateinit var oldTask: Task
    var isUpdate = false
    private lateinit var spinnerPriority: Spinner
    private lateinit var spinnerStatus: Spinner

    private lateinit var tvDeadlineDate: TextView
    private lateinit var tvDeadlineTime: TextView

    private var deadlineDate: String? = null // Class-level variable
    private var deadlineTime: String? = null // Class-level variable

    // Declare priority and status options at class level
    private val priorityOptions = arrayOf("Select Priority", "Low", "Medium", "High")
    private val statusOptions = arrayOf("Select Status", "Not Started", "In Progress", "Completed")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        spinnerPriority = findViewById(R.id.spinner_priority)
        spinnerStatus = findViewById(R.id.spinner_status)

        // Setting up priority options
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorityOptions)
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = priorityAdapter

        // Setting up status options
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = statusAdapter

        // Initialize the TextViews for deadline date and time
        tvDeadlineDate = findViewById(R.id.tv_deadline_date)
        tvDeadlineTime = findViewById(R.id.tv_deadline_time)

        // Set click listener for deadline date
        tvDeadlineDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Set click listener for deadline time
        tvDeadlineTime.setOnClickListener {
            showTimePickerDialog()
        }

        // Handle task update or creation
        handleTaskUpdate()

        binding.imgCheck.setOnClickListener {
            if (validateInput()) {
                setTaskData()
                setResult(Activity.RESULT_OK, Intent().putExtra("task", task))
                finish()
            } else {
                Toast.makeText(this, "Please enter some data", Toast.LENGTH_LONG).show()
            }
        }

        binding.imgDelete.setOnClickListener {
            deleteTask()
        }

        binding.imgBackArrow.setOnClickListener {
            onBackPressed()
        }

        // Set button click listener for scheduling notifications
        binding.setButton.setOnClickListener {
            scheduleNotification()

            if (isUpdate) {
                scheduleNotification()
            }
        }
    }

    private fun handleTaskUpdate() {
        try {
            oldTask = intent.getSerializableExtra("current_task") as Task
            binding.etName.setText(oldTask.name)
            binding.etDescription.setText(oldTask.description)

            // Set selected priority and status
            spinnerPriority.setSelection(priorityOptions.indexOf(oldTask.priority))
            spinnerStatus.setSelection(statusOptions.indexOf(oldTask.status))

            // Set deadline date and time if available
            if (oldTask.deadlineDate != null) {
                tvDeadlineDate.text = oldTask.deadlineDate
                deadlineDate = oldTask.deadlineDate
            }
            if (oldTask.deadlineTime != null) {
                tvDeadlineTime.text = oldTask.deadlineTime
                deadlineTime = oldTask.deadlineTime
            }

            isUpdate = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (isUpdate) {
            binding.imgDelete.visibility = View.VISIBLE
        } else {
            binding.imgDelete.visibility = View.INVISIBLE
        }
    }

    private fun validateInput(): Boolean {
        val name = binding.etName.text.toString()
        val taskDescription = binding.etDescription.text.toString()
        return name.isNotEmpty() && taskDescription.isNotEmpty()
    }

    private fun setTaskData() {
        val name = binding.etName.text.toString()
        val taskDescription = binding.etDescription.text.toString()
        val priority = spinnerPriority.selectedItem.toString()
        val status = spinnerStatus.selectedItem.toString()

        if (isUpdate) {
            task = Task(oldTask.id, name, taskDescription, priority, status, deadlineDate, deadlineTime)
        } else {
            task = Task(null, name, taskDescription, priority, status, deadlineDate, deadlineTime)
        }
    }

    private fun deleteTask() {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra("task", oldTask)
            putExtra("delete_task", true)
        })
        finish()
    }

    private fun scheduleNotification() {
        val intent = Intent(applicationContext, ReminderReceiver::class.java)
        val title = binding.etName.text.toString()
        //val message = "Reminder for task: ${binding.etDescription.text}"
        val message = binding.etDescription.text.toString()

        intent.putExtra("titleExtra", title)
        intent.putExtra("messageExtra", message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val timeInMillis = getTimeInMillis() // Ensure this returns a future time

        // Log for debugging
        Log.d("AddTaskActivity", "Scheduling notification for time: $timeInMillis")

        if (timeInMillis > System.currentTimeMillis()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
            Toast.makeText(this, "Notification scheduled for $title", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please select a future date and time", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getTimeInMillis(): Long {
        val calendar = Calendar.getInstance()
        val dateParts = deadlineDate?.split("/") ?: return 0L
        val timeParts = deadlineTime?.split(":") ?: return 0L

        val day = dateParts[0].toInt()
        val month = dateParts[1].toInt() - 1 // Calendar months are zero-based
        val year = dateParts[2].toInt()

        val hour = timeParts[0].toInt()
        val minute = timeParts[1].substringBefore(" ").toInt()
        val amPm = timeParts[1].substringAfter(" ")

        val hourIn24Format = if (amPm == "PM" && hour != 12) hour + 12 else if (amPm == "AM" && hour == 12) 0 else hour

        calendar.set(year, month, day, hourIn24Format, minute, 0)

        return calendar.timeInMillis
    }

    // Function to show the DatePickerDialog and set the selected date
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            tvDeadlineDate.text = formattedDate
            deadlineDate = formattedDate
        }, year, month, day)

        datePickerDialog.show()
    }

    // Function to show the TimePickerDialog and set the selected time
    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val amPm = if (selectedHour >= 12) "PM" else "AM"
            val hourIn12Format = if (selectedHour % 12 == 0) 12 else selectedHour % 12

            val formattedTime = String.format("%02d:%02d $amPm", hourIn12Format, selectedMinute)
            tvDeadlineTime.text = formattedTime
            deadlineTime = formattedTime
        }, hour, minute, false)

        timePickerDialog.show()
    }

    private companion object {
        const val notificationID = 1
    }
}
