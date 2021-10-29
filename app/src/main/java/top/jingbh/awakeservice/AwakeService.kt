package top.jingbh.awakeservice

import android.app.*
import android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import top.jingbh.awakeservice.databinding.WindowFloatingBinding

private const val CHANNEL_ID = "AwakeService"

private const val ONGOING_NOTIFICATION_ID = 1

class AwakeService : Service() {
    private lateinit var windowManager: WindowManager

    private var view: View? = null

    override fun onCreate() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createWindow()
        runForeground()
    }

    override fun onDestroy() {
        windowManager.removeView(view)
        stopForeground(true)
        setServiceStarted(false)
        view = null

        Log.i(this::class.simpleName, "Service stopped.")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createWindow() {
        if (view != null) return

        val windowType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        }

        val flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

        val params = WindowManager.LayoutParams(
            windowType,
            flags,
            PixelFormat.TRANSLUCENT
        ).apply {
            width = 0
            height = 0
            gravity = Gravity.CENTER
        }

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = WindowFloatingBinding.inflate(inflater)

        view = binding.root
        windowManager.addView(view, params)
    }

    private fun runForeground() {
        val intent: PendingIntent = Intent(this, SettingsActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(getText(R.string.service_running))
                .setContentText(getText(R.string.service_running_intro))
                .setSmallIcon(R.drawable.ic_round_wb_sunny_24)
                .setContentIntent(intent)
                .setOngoing(true)
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
                }.build()
        } else {
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getText(R.string.service_running))
                .setContentText(getText(R.string.service_running_intro))
                .setSmallIcon(R.drawable.ic_round_wb_sunny_24)
                .setContentIntent(intent)
                .setOngoing(true)
                .build()
        }

        startForeground(ONGOING_NOTIFICATION_ID, notification)
        setServiceStarted(true)

        Log.i(this::class.simpleName, "Service started.")
    }
}
