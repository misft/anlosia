package com.example.anlosia.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.example.anlosia.model.Location
import java.text.SimpleDateFormat
import java.util.*

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

    fun isUserLoggedIn(context: Context) : Boolean {
        val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)

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

    fun dateYMDtoIndo(date: String): String {
        val _date = date.split("-")
        logD(_date.toString())
        val year = _date[0]
        val month = parseMonthToString(_date[1])
        val day = parseDayToString(date)

        return "${_date[2]} ${month} ${year}"
    }

    fun parseMonthToString(month: String): String {
        if(month == "01") return "Januari"
        else if(month == "02") return "February"
        else if(month == "03") return "Maret"
        else if(month == "04") return "April"
        else if(month == "05") return "Mei"
        else if(month == "06") return "Juni"
        else if(month == "07") return "Juli"
        else if(month == "08") return "Agustus"
        else if(month == "09") return "September"
        else if(month == "10") return "Oktober"
        else if(month == "11") return "November"
        else if(month == "12") return "December"
        else return month
    }

    fun parseDayToString(date: String): String {
        val sdf = SimpleDateFormat("EEEE")
        val _date = SimpleDateFormat("YYYY-MM-dd").parse(date)
        val day: String = sdf.format(_date)

        if(day == "Monday") return "Senin"
        else if(day == "Tuesday") return "Selasa"
        else if(day == "Wednesday") return "Rabu"
        else if(day == "Thursday") return "Kamis"
        else if(day == "Friday") return "Jumat"
        else if(day == "Saturday") return "Sabtu"
        else if(day == "Sunday") return "Minggu"

        return day
    }

    fun getCurrentDate(): String {
        val currentDate = SimpleDateFormat("dd MM YYYY").format(Calendar.getInstance().time)
        val _dateSplit: Array<String> = currentDate.split(" ").toTypedArray();
        _dateSplit[1] = parseMonth(_dateSplit)

        return _dateSplit.joinToString(" ")
    }

    fun parseMonth(dateSplit : Array<String>) : String {
        if(dateSplit[1] == "01") return "Januari"
        else if(dateSplit[1] == "02") return "Februari"
        else if(dateSplit[1] == "03") return "Maret"
        else if(dateSplit[1] == "04") return "April"
        else if(dateSplit[1] == "05") return "Mei"
        else if(dateSplit[1] == "06") return "Juni"
        else if(dateSplit[1] == "07") return "Juli"
        else if(dateSplit[1] == "08") return "Agustus"
        else if(dateSplit[1] == "09") return "September"
        else if(dateSplit[1] == "10") return "Oktober"
        else if(dateSplit[1] == "11") return "November"
        else if(dateSplit[1] == "12") return "Desember"
        else
            return "January"
    }

    fun logD(text: String?) {
        Log.d("Ayy", text.toString())
    }

    fun logE(text: String) {
        Log.e("Fuck", "Fuck ${text.toString()}")
    }
}