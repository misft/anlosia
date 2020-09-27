package com.example.anlosia

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Window
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.anlosia.model.RecordLocationResponse
import com.example.anlosia.service.PresenceStart
import com.example.anlosia.ui.login.LoginActivity
import com.example.anlosia.util.Util
import com.example.anlosia.viewmodel.LocationViewModel
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {
    private val locationService = PresenceStart()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_notifications, R.id.navigation_dashboard))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val client: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        var location : Location? = null
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(LocationRequest(), object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    location = locationResult.lastLocation
                }
            }, null)
        }
    }

    override fun onBackPressed() {
        if (!Util.isUserLoggedIn(this)) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1) {
            val locationViewModel = LocationViewModel()
            locationViewModel.getRecordLocation().observe(this, Observer<RecordLocationResponse>() {
                if(it != null) {
                    Log.d("Location update", it.toString())
                }
                else {
                    Log.d("Location update", it.toString())
                }
            })
        }
    }
}
