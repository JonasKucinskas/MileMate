package com.example.milemate

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.Calendar

class NotificationReceiver : BroadcastReceiver() {




    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)

        val notificationTitle = intent.getStringExtra("notification_title")
        val notificationText = intent.getStringExtra("notification_text")
        val notificationId = intent.getIntExtra("notification_id", 0)
        val notificationDate = Calendar.getInstance()
        notificationDate.timeInMillis = intent.getLongExtra("notification_date", 0)

        val currentDate = Calendar.getInstance()

        if (currentDate.get(android.icu.util.Calendar.DAY_OF_MONTH) == notificationDate.get(android.icu.util.Calendar.DAY_OF_MONTH) &&
                currentDate.get(Calendar.MONTH) == notificationDate.get(Calendar.MONTH)){
            val notification = NotificationCompat.Builder(context, "my_channel_id")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()
            notificationManager.notify(notificationId, notification)
        }
    }
}