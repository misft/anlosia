package com.example.anlosia.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.anlosia.R
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(sharedPreferences) {
            name.text = getString("name", "Anonymous")
            start_work.text = getString("start_work", " ")
            end_work.text = getString("end_work", " ")
        }
        btn_logout.setOnClickListener {
            with(sharedPreferences.edit()) {
                clear()
                apply()
            }
        }
    }
}
