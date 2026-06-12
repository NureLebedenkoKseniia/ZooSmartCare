package com.example.zoosmartcare.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.zoosmartcare.MainActivity
import com.example.zoosmartcare.data.repository.AlertRepository
import kotlinx.coroutines.*

class AlertPollingService : Service() {
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private val alertRepo = AlertRepository()
    private val notifiedAlertIds = mutableSetOf<Int>()

    private val CHANNEL_ID = "AlertPollingServiceChannel"
    private val NOTIFICATION_ID = 101

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = createNotification("ZooSmartCare is monitoring alerts...")
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startPolling()
        return START_STICKY
    }

    private fun startPolling() {
        serviceScope.launch {
            while (isActive) {
                try {
                    alertRepo.getActive().onSuccess { activeAlerts ->
                        val newAlerts = activeAlerts.filter { 
                            it.status == "New" && !notifiedAlertIds.contains(it.alert_id) 
                        }
                        for (alert in newAlerts) {
                            showAlertDialog(alert.alert_type ?: "Alert", alert.message ?: "New critical alert!")
                            notifiedAlertIds.add(alert.alert_id)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(10000) // Poll every 10 seconds
            }
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alertNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), alertNotification)
    }

    private fun createNotification(contentText: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ZooSmartCare Monitor")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Alert Polling Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
