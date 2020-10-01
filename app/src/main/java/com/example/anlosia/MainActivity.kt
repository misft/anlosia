package com.example.anlosia

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.anlosia.service.PresenceStart
import com.example.anlosia.util.Util
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.maps.android.PolyUtil
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var client: FusedLocationProviderClient
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        setContentView(R.layout.activity_main)

        client = LocationServices.getFusedLocationProviderClient(this)
        sharedPreferences = this.getSharedPreferences("user", Context.MODE_PRIVATE)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_notifications, R.id.navigation_dashboard
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        if (!Util.isUserLoggedIn(applicationContext)) {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()

        checkLocation()
    }

    private fun checkLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            client.lastLocation.addOnSuccessListener { currentLocation ->
                currentLocation?.let { point ->
                    Util.logD(point.toString())
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

                    val point = LatLng(point.latitude, point.longitude)
                    val isInside: Boolean = PolyUtil.containsLocation(point, polygon, true)

                    sharedPreferences.edit().putBoolean("is_inside", isInside).commit()
                    sharedPreferences.edit().putBoolean("is_location_checked", true).commit()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.putFragment(outState, "mainFragment", nav_host_fragment)
    }
}
