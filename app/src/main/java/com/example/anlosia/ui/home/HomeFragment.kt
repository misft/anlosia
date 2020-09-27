package com.example.anlosia.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.lifecycle.ViewModelProvider
import com.example.anlosia.R
import com.example.anlosia.ui.list.presence.ListPresenceActivity
import com.example.anlosia.util.Util
import com.example.anlosia.viewmodel.PresenceStartViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val sharedPref =  requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
        val view : View? = activity?.findViewById(R.id.container)

        val sdf = SimpleDateFormat("HH:mm")
        val now: Date = sdf.parse(sdf.format(Calendar.getInstance().time))
        val startWork: Date = sdf.parse(sharedPref.all["start_work"].toString())
        val endWork: Date = sdf.parse(sharedPref.all["end_work"].toString())
        val isPresenced : Int = sharedPref.all["is_presenced"].toString().toInt()

        sharedPref.registerOnSharedPreferenceChangeListener(listener)
        childFragmentManager.commit {
            if(isPresenced == 1) {
                replace<HomePresenceStatusPresencedFragment>(R.id.presence_status_fragment, null)
            }
            else if(now.before(endWork) && isPresenced != 1) {
                replace<HomePresenceStatusFragment>(R.id.presence_status_fragment, null)
            }
            if(now.after(endWork)) {
                replace<HomePresenceStatusEndFragment>(R.id.presence_status_fragment, null)
            }
            addToBackStack(null)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presence_date.text =  getCurrentDate()

        tx_list_presence.setOnClickListener {
            startActivity(Intent(requireActivity(), ListPresenceActivity::class.java))
        }
    }

    fun getCurrentDate(): String {
        val currentDate = SimpleDateFormat("dd MM YYYY").format(Calendar.getInstance().time)
        val _dateSplit: Array<String> = currentDate.split(" ").toTypedArray();
        _dateSplit[1] = parseMonth(_dateSplit)

        return _dateSplit.joinToString(" ")
    }

    private fun parseMonth(dateSplit : Array<String>) : String {
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
}
