package com.example.anlosia.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.anlosia.R
import com.example.anlosia.model.IsPresencedResponse
import com.example.anlosia.ui.camera.CameraPresenceActivity
import com.example.anlosia.util.Util
import com.example.anlosia.viewmodel.IsPresencedViewModel
import com.example.anlosia.viewmodel.PresenceStartViewModel
import com.example.anlosia.viewmodel.PresenceStateAllowedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_presence_status.*
import java.text.SimpleDateFormat
import java.util.*


class HomePresenceStatusFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var manager: LocationManager
    private lateinit var presenceStateAllowedViewModel: PresenceStateAllowedViewModel
    private lateinit var isPresencedViewModel: IsPresencedViewModel
    private lateinit var observePresenceState: Observer<Boolean>
    private lateinit var observeIsPresenced: Observer<IsPresencedResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        manager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
        presenceStateAllowedViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())[PresenceStateAllowedViewModel::class.java]
        isPresencedViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())[IsPresencedViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_presence_status, container, false)

        observePresenceState = Observer<Boolean> {
            it?.let {
                Util.logD(it.toString())
                if(it) {
                    root.findViewById<Button>(R.id.btn_presence_start).visibility = View.VISIBLE
                    root.findViewById<Button>(R.id.btn_presence_disabled).visibility = View.GONE
                }
                else {
                    root.findViewById<Button>(R.id.btn_presence_start).visibility = View.GONE
                    root.findViewById<Button>(R.id.btn_presence_disabled).visibility = View.VISIBLE
                }
            }
        }

        val id_user = sharedPreferences.getInt("id", 0)
        val date_presence = SimpleDateFormat("YYYY-MM-dd").format(Date().time)
        isPresencedViewModel.postIsPresenced(id_user, date_presence)

        presenceStateAllowedViewModel.getIsAllowed().observe(requireActivity(), observePresenceState)

        sharedPreferencesListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {
                presenceStateAllowedViewModel.unsetAllowed()
            }
            if(!sharedPreferences.getBoolean("is_inside", false)) {
                presenceStateAllowedViewModel.unsetAllowed()
            }
            else {
                presenceStateAllowedViewModel.setAllowed()
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)


        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {
            presenceStateAllowedViewModel.unsetAllowed()
        }
        if(!sharedPreferences.getBoolean("is_inside", false)) {
            presenceStateAllowedViewModel.unsetAllowed()
        }
        else {
            presenceStateAllowedViewModel.setAllowed()
        }

        val view : View? = activity?.findViewById(R.id.fragment_home)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_presence_start.setOnClickListener {
            val id_user = sharedPreferences.getInt("id", 0)
            val date_presence = SimpleDateFormat("YYYY-MM-dd").format(Date().time)
            isPresencedViewModel.postIsPresenced(id_user, date_presence)

            if(isPresencedViewModel.getIsPresenced().value?.api_status == 1) {
                startActivity(Intent(activity, CameraPresenceActivity::class.java))
            }
            else {
                val dialog = BottomSheetDialog(requireContext())
                dialog.setContentView(R.layout.dialog_is_presenced)
                dialog.show()
            }
        }
    }
}