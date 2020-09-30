package com.example.anlosia.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.anlosia.R
import com.example.anlosia.ui.camera.CameraPresenceActivity
import com.example.anlosia.util.Util
import com.example.anlosia.viewmodel.PresenceStartViewModel
import kotlinx.android.synthetic.main.fragment_presence_status.*


class HomePresenceStatusFragment : Fragment() {

    private var presenceViewModel = PresenceStartViewModel()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_presence_status, container, false)

        val view : View? = activity?.findViewById(R.id.fragment_home)

        val key: String

        sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        sharedPreferencesListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if(!sharedPreferences.getBoolean("is_inside", false)) {
                parentFragmentManager.commit {
                    replace<HomePresenceStatusDisabledFragment>(R.id.presence_status_fragment)
                }
            }
            if(sharedPreferences.getBoolean("is_inside", false)) {
                parentFragmentManager.commit {
                    replace<HomePresenceStatusFragment>(R.id.presence_status_fragment)
                }
            }
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val manager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {
            parentFragmentManager.commit {
                replace<HomePresenceStatusDisabledFragment>(R.id.presence_status_fragment)
            }
        }
        else if(!sharedPreferences.getBoolean("is_inside", false)) {
            parentFragmentManager.commit {
                replace<HomePresenceStatusDisabledFragment>(R.id.presence_status_fragment)
            }
        }

        if(sharedPreferences.all["is_presenced"] == 1)  {
            parentFragmentManager.commit {
                replace<HomePresenceStatusPresencedFragment>(R.id.presence_status_fragment)
            }
        }

        btn_presence_start.setOnClickListener {
            startActivity(Intent(activity, CameraPresenceActivity::class.java))
        }
    }
}