package com.example.anlosia.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.FaceDetector
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.anlosia.R
import com.example.anlosia.model.PresenceResponse
import com.example.anlosia.service.PresenceStart
import com.example.anlosia.util.Util
import com.example.anlosia.viewmodel.PresenceEndViewModel
import kotlinx.android.synthetic.main.fragment_home_presence_status_presenced.*
import java.text.SimpleDateFormat
import java.util.*

class HomePresenceStatusPresencedFragment : Fragment() {
    private var presenceEndViewModel = PresenceEndViewModel()
    private lateinit var sharedPref : SharedPreferences
    private val locationService = PresenceStart()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenceEndViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.NewInstanceFactory()
        )[PresenceEndViewModel::class.java]

        startRecordLocation()

        //Get sharedPref instance
        sharedPref = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        presenceEndViewModel.getPresence().observe(requireActivity(), Observer<PresenceResponse> {
            it?.let {
                btn_presence_end.isEnabled = false
                btn_presence_end.setBackgroundResource(R.drawable.presence_button_disabled)

                requireActivity().stopService(Intent(requireActivity(), PresenceStart::class.java))

                parentFragmentManager.commit {
                    replace<HomePresenceStatusEndFragment>(R.id.presence_status_fragment)
                }
            }
        })

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_presence_status_presenced, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        btn_presence_end.setOnClickListener {
            val id_presence = sharedPref.getInt("id_presence", 0)
            val id_user = sharedPref.getInt("id", 0)
            val id_company = sharedPref.getInt("id_company", 0)
            val myDate = Date()
            val date_presence = SimpleDateFormat("YYYY-MM-dd").format(myDate)
            val end_presence = SimpleDateFormat("HH:mm:ss").format(myDate)

            presenceEndViewModel.postPresence(id_presence, id_user, id_company, date_presence, end_presence)
        }
    }

    private fun startRecordLocation() {
        if (Util.isMyServiceRunning(locationService.javaClass, requireActivity())) {
            requireActivity().stopService(Intent(requireActivity(), PresenceStart::class.java))
        }
        requireActivity().startService(Intent(requireActivity(), PresenceStart::class.java))
    }
}