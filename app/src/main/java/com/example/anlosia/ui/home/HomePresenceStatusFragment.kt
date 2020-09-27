package com.example.anlosia.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.anlosia.R
import com.example.anlosia.service.PresenceStart
import com.example.anlosia.ui.camera.CameraPresenceActivity
import com.example.anlosia.util.Util
import com.example.anlosia.viewmodel.PresenceStartViewModel
import kotlinx.android.synthetic.main.fragment_presence_status.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class HomePresenceStatusFragment : Fragment() {
    private var presenceViewModel = PresenceStartViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_presence_status, container, false)

        val view : View? = activity?.findViewById(R.id.fragment_home)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)!!

        if(sharedPref.all["is_presenced"] == 1)  {
            parentFragmentManager.commit {
                replace<HomePresenceStatusPresencedFragment>(R.id.presence_status_fragment)
            }
        }

        btn_presence_start.setOnClickListener {
            startActivity(Intent(activity, CameraPresenceActivity::class.java))
        }
    }
}