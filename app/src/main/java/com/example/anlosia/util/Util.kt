package com.example.anlosia.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.example.anlosia.model.Location

object Util {
    fun isMyServiceRunning(serviceClass: Class<*>, mActivity: Activity): Boolean {
        val manager: ActivityManager =
            mActivity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.getClassName()) {
                Log.i("Service status", "Running")
                return true
            }
        }
        Log.i("Service status", "Not running")
        return false
    }

    fun isUserLoggedIn(activity: Activity) : Boolean {
        val sharedPref = activity.getSharedPreferences("user", Context.MODE_PRIVATE)

        if(sharedPref.all["id"] == null) {
            return false
        }
        return true
    }

    fun append(polygon: Array<Array<Double>?>, element: Array<Double>): Array<Array<Double>?> {
        val array = polygon.copyOf(polygon.size + 1)
        array[polygon.size] = element

        return array
    }

    fun isInsidePolygon(location: Location, polygon: Array<Array<Double>?>): Boolean {
        val x = location.lat;
        val y = location.lng;

        var inside = false;
        var j = polygon.size - 1;
        for(i in polygon.indices - 1) {
            var xi = polygon[i]!![0]
            var yi = polygon[i]!![1];
            var xj = polygon[j]!![0]
            var yj = polygon[j]!![1];

            var intersect = ((yi > y!!) != (yj > y)) && (x!! < (xj - xi) * (y - yi) / (yj - yi) + xi)
            if (intersect)
                inside = !inside

            j = i + 1
        }

        return inside;
    }

    fun logD(text: String?) {
        Log.d("Ayy", text.toString())
    }

    fun logE(text: String) {
        Log.e("Fuck", "Fuck ${text.toString()}")
    }
}