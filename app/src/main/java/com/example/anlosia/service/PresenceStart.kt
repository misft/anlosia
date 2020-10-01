    package com.example.anlosia.service

    import android.Manifest
    import android.app.*
    import android.content.Context
    import android.content.Intent
    import android.content.SharedPreferences
    import android.content.pm.PackageManager
    import android.graphics.Color
    import android.location.Location
    import android.os.Build
    import android.os.Looper
    import androidx.annotation.RequiresApi
    import androidx.core.app.ActivityCompat
    import androidx.core.app.NotificationCompat
    import androidx.core.app.NotificationManagerCompat
    import androidx.lifecycle.*
    import com.example.anlosia.MainActivity
    import com.example.anlosia.R
    import com.example.anlosia.util.Util
    import com.google.android.gms.location.*
    import com.google.android.gms.maps.model.LatLng
    import com.google.maps.android.PolyUtil
    import java.util.*

    class PresenceStart() : LifecycleService() {
        var counter = 0
        val location = MutableLiveData<Location>()
        private lateinit var sharedPreferences: SharedPreferences
        private lateinit var client: FusedLocationProviderClient

        val NOTIFICATION_CHANNEL_ID = "com.anlosia"
        val channelName = "Background Service"

        override fun onCreate() {
            super.onCreate()

            sharedPreferences = applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
            client = FusedLocationProviderClient(applicationContext)

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) createNotificationChanel() else startForeground(
                1,
                Notification()
            )
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun createNotificationChanel() {
            val chan = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_MIN
            )
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)
            val notificationBuilder =
                NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification: Notification = notificationBuilder
                .setOngoing(true)
                .setContentTitle("Anlosia")
                .setContentText("Menggunakan GPS")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            startForeground(2, notification)
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
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Util.logD("Cannot get location")
            }

            val locationRequest = LocationRequest.create()
            locationRequest.interval = 1000
            locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

            val mLocationCallback: LocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    with(locationResult.lastLocation) {
                        var polygon = mutableListOf<LatLng>()
                        var location = sharedPreferences.getString("location", " ")!!
                        location = location.replace("],[", "|")
                        location = location.replace("[[", "")
                        location = location.replace("]]", "")
                        var n = 0

                        location.split("|").forEach {
                            var latlng = it.split(",")
                            polygon.add(LatLng(latlng[0].toDouble(), latlng[1].toDouble()))
                        }

                        val point = LatLng(this.latitude, this.longitude)
                        val isInside: Boolean = PolyUtil.containsLocation(point, polygon, true)
                        sharedPreferences.edit().putBoolean("is_inside", isInside).commit()
                        val intent = Intent(applicationContext, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)

                        val notification =
                            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                                .setContentTitle("Anda dimana?")
                                .setContentText("Anda terdeteksi berada diluar lokasi")
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setPriority(NotificationManager.IMPORTANCE_MIN)
                                .setContentIntent(pendingIntent)
                                .setCategory(Notification.CATEGORY_MESSAGE)
                                .build()
                        if(!isInside) {
                            NotificationManagerCompat.from(applicationContext).notify(3, notification)
                        }
                    }
                }
            }

            client.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper())
        }

        fun stoptimertask() {
            if (timer != null) {
                timer!!.cancel()
                timer = null
            }
        }
    }
