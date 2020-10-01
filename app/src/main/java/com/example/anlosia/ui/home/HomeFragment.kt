package com.example.anlosia.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.*
import com.example.anlosia.R
import com.example.anlosia.ui.list.presence.ListPresenceActivity
import com.example.anlosia.util.Util
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var client: FusedLocationProviderClient
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        sharedPref = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
        val view: View? = activity?.findViewById(R.id.container)

        client = LocationServices.getFusedLocationProviderClient(requireActivity())

        refreshFragment()

        return root
    }

    private fun refreshFragment() {
        val sdf = SimpleDateFormat("HH:mm")
        val now: Date = sdf.parse(sdf.format(Calendar.getInstance().time))
        val startWork: Date = sdf.parse(sharedPref.getString("start_work", " "))
        val endWork: Date = sdf.parse(sharedPref.getString("end_work", " "))
        val isPresenced: Int = sharedPref.getInt("is_presenced", 0)
        val datePresence = sharedPref.getString("date_presence", "")

        with(childFragmentManager.beginTransaction()) {
            if(datePresence != "") {
                val dateNow = SimpleDateFormat("YYYY-MM-dd").parse(Date().toString())
                val latestPresence = SimpleDateFormat("YYYY-MM-dd").parse(datePresence)
                if(latestPresence.before(dateNow)) {
                    if(isPresenced == 1) {
                        replace<HomePresenceStatusPresencedFragment>(R.id.presence_status_fragment, null)
                    }
                    else if(now.after(endWork)) {
                        replace<HomePresenceStatusEndFragment>(R.id.presence_status_fragment, null)
                    }
                    else if(now.before(endWork) && isPresenced == 0) {
                        replace<HomePresenceStatusFragment>(R.id.presence_status_fragment, null)
                    }
                }
                else {
                    replace<HomePresenceStatusPresencedFragment>(R.id.presence_status_fragment, null)
                }
            }
            else {
                if(isPresenced == 1) {
                    replace<HomePresenceStatusPresencedFragment>(R.id.presence_status_fragment, null)
                }
                else if(now.after(endWork)) {
                    replace<HomePresenceStatusEndFragment>(R.id.presence_status_fragment, null)
                }
                else if(now.before(endWork) && isPresenced == 0) {
                    replace<HomePresenceStatusFragment>(R.id.presence_status_fragment, null)
                }
            }
            addToBackStack(null)
            commitAllowingStateLoss()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presence_date.text = Util.getCurrentDate()

        tx_list_presence.setOnClickListener {
            startActivity(Intent(requireActivity(), ListPresenceActivity::class.java))
        }
    }
}
