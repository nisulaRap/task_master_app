package com.example.taskmasterapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("titleExtra") ?: "Reminder"
        val message = intent.getStringExtra("messageExtra") ?: "You have a task reminder"

        // Create the notification
        createNotification(context, title, message)
    }

    private fun createNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1
        val channelId = "task_channel"
        val channelName = "Task Notifications"

        // Create the notification channel (for API 26+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                enableVibration(true)
                // Set a default vibration pattern for the channel (optional)
                vibrationPattern = longArrayOf(0, 5000, 2000, 5000) // This pattern vibrates for 1 second, pauses for half a second, and then vibrates for another second.
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification intent
        val intent = Intent(context, AddTaskActivity::class.java) // Adjust if necessary
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Build the notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_reminder) // Replace with your icon
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 5000, 2000, 5000)) // Set the custom vibration pattern here
            .build()

        // Show the notification
        notificationManager.notify(notificationId, notification)
    }
}