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
import androidx.fragment.app.FragmentActivity
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
    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var dialog: BottomSheetDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialog = BottomSheetDialog(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentActivity = requireActivity()
        manager = fragmentActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        sharedPreferences = fragmentActivity.getSharedPreferences("user", Context.MODE_PRIVATE)
        presenceStateAllowedViewModel = ViewModelProvider(fragmentActivity, ViewModelProvider.NewInstanceFactory())[PresenceStateAllowedViewModel::class.java]
        isPresencedViewModel = ViewModelProvider(fragmentActivity, ViewModelProvider.NewInstanceFactory())[IsPresencedViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_presence_status, container, false)
        val view : View? = activity?.findViewById(R.id.fragment_home)

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

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            presenceStateAllowedViewModel.unsetAllowed()
        }
        if(!sharedPreferences.getBoolean("is_inside", false)) {
            presenceStateAllowedViewModel.unsetAllowed()
        }
        else {
            presenceStateAllowedViewModel.setAllowed()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_presence_start.setOnClickListener {
            val id_user = sharedPreferences.getInt("id", 0)
            val date_presence = SimpleDateFormat("YYYY-MM-dd").format(Date().time)
            dialog.dismiss()
            dialog.setContentView(R.layout.dialog_checking_presence)
            dialog.show()
            isPresencedViewModel.postIsPresenced(id_user, date_presence)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(dialog.isShowing)
            dialog.dismiss()

        presenceStateAllowedViewModel.getIsAllowed().removeObservers(this)
        observePresenceState = Observer<Boolean> {
            it?.let {
                if(it) {
                    view?.findViewById<Button>(R.id.btn_presence_start)!!.visibility = View.VISIBLE
                    view?.findViewById<Button>(R.id.btn_presence_disabled)!!.visibility = View.GONE
                }
                else {
                    view?.findViewById<Button>(R.id.btn_presence_start)!!.visibility = View.GONE
                    view?.findViewById<Button>(R.id.btn_presence_disabled)!!.visibility = View.VISIBLE
                }
            }
        }
        isPresencedViewModel.getIsPresenced().removeObservers(this)
        observeIsPresenced = Observer<IsPresencedResponse> {
            it?.let {
                dialog.dismiss()
                if(it.api_status == 1) {
                    startActivity(Intent(fragmentActivity, CameraPresenceActivity::class.java))
                }
                else {
                    dialog.setContentView(R.layout.dialog_is_presenced)
                    dialog.show()
                }
                isPresencedViewModel.isPresenced.value = null
            }
        }

        isPresencedViewModel.getIsPresenced().observe(viewLifecycleOwner, observeIsPresenced)
        presenceStateAllowedViewModel.getIsAllowed().observe(viewLifecycleOwner, observePresenceState)
    }
}



