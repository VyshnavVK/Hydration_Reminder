package app.vyshnav.hydrationreminder.Receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import app.vyshnav.hydrationreminder.R
import com.app.myapplication.util.sp


class AlarmReceiver : BroadcastReceiver() {

    var channelID = "app.hydrationreminder"
    var channelNAME = "Hydration Reminder"
    lateinit var notificationManager: NotificationManager
    var defaultSoundUri: Uri? = null

    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == "FOO_ACTION") {
            // p0?.startActivity(Intent(p0,DrinkWaterActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            Toast.makeText(p0, "Alarm triggered", Toast.LENGTH_SHORT).show()
            if (p0 != null) {
                sendNotification(p0)
            }
        }
    }


    private fun sendNotification(context: Context) {
        notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel(channelID, channelNAME, NotificationManager.IMPORTANCE_HIGH,context)
        }
        sendTextNotification(context)
    }


    private fun sendTextNotification(context: Context) {
        val notificationBuilder = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Drink water")
            .setContentText(sp.with(context).getString(sp.NOTIFICATION_KEY,""))
            .setAutoCancel(true)
            .setSound(Uri.parse("android.resource://" + context.packageName + "/" + sp.with(context).getInt(sp.NOTIFICATION_VALUE_KEY,0)))
            .setSilent(sp.with(context).getBoolean(sp.SOUND_KEY,false))
           /* .setContentIntent(pendingIntent)*/
        notificationBuilder.setLargeIcon(
            BitmapFactory.decodeResource(
                context.resources,
                R.mipmap.ic_launcher
            )
        )
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        notificationManager.notify(0, notificationBuilder.build())
    }

    fun makeNotificationChannel(id: String?, name: String?, importance: Int?,context: Context) {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        defaultSoundUri = Uri.parse("android.resource://" + context.packageName + "/" + sp.with(context).getInt(sp.NOTIFICATION_VALUE_KEY,0))

        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(channelID)
            NotificationChannel(id, name, importance!!)
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        channel.importance = NotificationManager.IMPORTANCE_HIGH
        channel.setSound(defaultSoundUri,audioAttributes)
        channel.setShowBadge(true) // set false to disable badges, Oreo exclusive
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}