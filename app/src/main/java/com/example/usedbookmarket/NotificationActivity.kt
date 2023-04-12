package com.example.usedbookmarket

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationActivity : AppCompatActivity() {
    val CHANNEL_ID = "null"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val email = sharedPref.getString("email","")
        val msg = sharedPref.getString("msg","")

//        // Create an explicit intent for an Activity in your app
//        val intent = Intent(this, AlertDetails::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
//
//        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(R.drawable.notification_icon)
//            .setContentTitle("My notification")
//            .setContentText("Hello World!")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            // Set the intent that will fire when the user taps the notification
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)


        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(email)
            .setContentText(msg)
            .setStyle(
                NotificationCompat.BigTextStyle()
                .bigText("Much longer text that cannot fit one line..."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            val notificationId = 0
            notify(notificationId, builder.build())

            createNotificationChannel()
        }

    }
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }

    }
}