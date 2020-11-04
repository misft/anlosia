package com.example.anlosia.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.anlosia.R
import com.example.anlosia.service.PresenceStart
import com.example.anlosia.ui.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var fragmentActivity: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentActivity = requireActivity()
        sharedPreferences = fragmentActivity.getSharedPreferences("user", Context.MODE_PRIVATE)
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(sharedPreferences) {
            name.text = getString("name", "Anonymous")
            start_work.text = getString("start_work", " ")
            end_work.text = getString("end_work", " ")
            email.text = getString("email", " ")
            phone.text = getString("phone", " ")
            Glide.with(this@ProfileFragment)
                .load(getString("profile_pic", " "))
                .into(profile_pic)
        }
        btn_logout.setOnClickListener {
            fragmentActivity.stopService(Intent(fragmentActivity, PresenceStart::class.java))

            with(sharedPreferences.edit()) {
                clear()
                commit()
            }
            startActivity(Intent(fragmentActivity, LoginActivity::class.java))
            fragmentActivity.finish()
        }
    }
}
