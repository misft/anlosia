package com.example.anlosia.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import com.example.anlosia.R
import com.example.anlosia.repositories.RecordLocationRepo
import com.example.anlosia.util.Util
import com.example.anlosia.viewmodel.LocationViewModel
import com.example.anlosia.model.Location as LocationModel
import com.google.android.gms.location.*
import java.util.*

class PresenceStart() : LifecycleService() {
    var counter = 0
    val location = MutableLiveData<Location>()
    val recordLocationRepo : RecordLocationRepo = RecordLocationRepo()
    private val TAG = "LocationService"
    var locationViewModel : LocationViewModel = LocationViewModel()
    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) createNotificationChanel() else startForeground(
            1,
            Notification()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChanel() {
        val NOTIFICATION_CHANNEL_ID = "com.anlos"
        val channelName = "Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setContentTitle("App is running count::" + counter)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startTimer()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stoptimertask()
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, RestartBackgroundService::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    fun startTimer() {
        timer = Timer()
        timerTask = object : TimerTask(), LifecycleOwner {
            override fun run() {
                var count = counter++
                val id = 3

                val client = LocationServices.getFusedLocationProviderClient(applicationContext)
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    var polygon: Array<Array<Double>?> = arrayOf()
                    val sharedPreferences = applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
                    var location = sharedPreferences.all["location"].toString()
                    location = location.replace("],[", "|")
                    location = location.replace("[[", "")
                    location = location.replace("]]", "")
                    var n = 0
                    location.split("|").forEach {
                        var latlng = it.split(",")
                        polygon = Util.append(polygon, arrayOf(latlng[0].toDouble(), latlng[1].toDouble()))
                    }

                    val point = client.lastLocation.addOnSuccessListener {
                        val location = LocationModel(it.latitude, it.longitude)
                        val isInside = Util.isInsidePolygon(location, polygon)
                    }
                }
            }

            override fun getLifecycle(): Lifecycle {
                return lifecycle
            }
        }

        timer!!.schedule(
            timerTask,
            0,
            10000
        )
    }

    fun stoptimertask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }
}