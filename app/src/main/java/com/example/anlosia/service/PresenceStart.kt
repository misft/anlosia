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
import com.example.anlosia.model.PresenceResponse
import com.example.anlosia.repositories.PresenceEndRepo
import com.example.anlosia.util.Util
import com.example.anlosia.viewmodel.PresenceEndViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class PresenceStart() : LifecycleService() {
    val location = MutableLiveData<Location>()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var client: FusedLocationProviderClient
    private lateinit var presenceEndRepo: PresenceEndRepo
    private lateinit var mLocationCallback: LocationCallback
    val NOTIFICATION_CHANNEL_ID = "com.anlosia"
    val channelName = "Background Service"

    override fun onCreate() {
        super.onCreate()

        sharedPreferences = applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
        client = FusedLocationProviderClient(applicationContext)
        presenceEndRepo = PresenceEndRepo()

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
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, RestartBackgroundService::class.java)
        this.sendBroadcast(broadcastIntent)
    }

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

        mLocationCallback = object : LocationCallback() {
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
                    val intent = Intent(
                        applicationContext,
                        MainActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    val pendingIntent = PendingIntent.getActivity(
                        applicationContext,
                        0,
                        intent,
                        PendingIntent.FLAG_ONE_SHOT
                    )

                    val notification =
                        NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                            .setContentTitle("Anda dimana?")
                            .setContentText("Anda terdeteksi berada diluar lokasi")
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setPriority(NotificationManager.IMPORTANCE_MIN)
                            .setContentIntent(pendingIntent)
                            .setCategory(Notification.CATEGORY_MESSAGE)
                            .build()

                    if(sharedPreferences.getInt("is_presenced", 0) == 1 || sharedPreferences.getInt("id_presence", 0) != 0) {
                        checkWorkingHour()
                    }
                    if (!isInside) {
                        NotificationManagerCompat.from(applicationContext).notify(3, notification)
                    }
                }
            }
        }
        client.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper())
    }

    private fun checkWorkingHour() {
        val endWork = sharedPreferences.getString("end_work", "")
        val hourEndWork = SimpleDateFormat("HH:mm:ss").parse(endWork)
        val hourNow =
            SimpleDateFormat("HH:mm:ss").parse(SimpleDateFormat("HH:mm:ss").format(Date().time))

        if (hourNow.before(hourEndWork)) {
            return
        }

        val id_presence = sharedPreferences.getInt("id_presence", 0)
        val id_user = sharedPreferences.getInt("id", 0)
        val id_company = sharedPreferences.getInt("id_company", 0)
        val date_presence = SimpleDateFormat("YYYY-MM-dd").format(Date())
        val end_presence = SimpleDateFormat("HH:mm:ss").format(Date())

        val body: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("id_user", id_user.toString())
            .addFormDataPart("id_company", id_company.toString())
            .addFormDataPart("date_presence", date_presence)
            .addFormDataPart("end_presence", end_presence)
            .build()

        presenceEndRepo.postPresenceEnd(id_presence, body)
            .enqueue(object : Callback<PresenceResponse> {
                override fun onFailure(call: Call<PresenceResponse>, t: Throwable) {

                }
                override fun onResponse(
                    call: Call<PresenceResponse>,
                    response: Response<PresenceResponse>
                ) {
                    if(response.code() == 200) {
                        with(sharedPreferences.edit()) {
                            remove("id_presence")
                            remove("is_presenced")
                            apply()
                        }

                        stopForeground(true)
                    }
                }
            })
    }
}
