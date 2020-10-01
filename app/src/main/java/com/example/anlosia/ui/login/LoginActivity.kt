package com.example.anlosia.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.anlosia.MainActivity
import com.example.anlosia.R
import com.example.anlosia.model.LoginResponse
import com.example.anlosia.ui.home.HomeViewModel
import com.example.anlosia.util.Util
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_login.*
import java.text.SimpleDateFormat
import java.util.*


class LoginActivity : AppCompatActivity() {
    //Initiate possible variable
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var dialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        //If user is already signed in, redirect it
        isUserSignedIn()
        //Inflate layout
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        //Initiate view model
        homeViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[HomeViewModel::class.java]

        homeViewModel.getUsersData().observe(this, Observer<LoginResponse> {
            it?.let {
                if (it.api_status != 1) {
                    val dialogSheet = layoutInflater.inflate(R.layout.dialog_auth_fail,null)
                    dialogSheet.findViewById<TextView>(R.id.message).text = it.api_message
                    dialog.setContentView(dialogSheet)
                    dialog.show()

                    return@let
                } else {
                    val data = it.data?.get(0)
                    data?.let {
                        val sharedPref =
                            this.getSharedPreferences("user", Context.MODE_PRIVATE) ?: return@Observer
                        with(sharedPref.edit()) {
                            putInt("id", data.id)
                            putInt("id_company", data.users__id_company)
                            putString("name", data.users__name)
                            putString("email", data.email)
                            putString("phone", data.users__telp)
                            putString(
                                "profile_pic",
                                "https://anlosia.xyz/media/" + data.users__profile_pic
                            )
                            putString("start_work", data.users__id_company__start_work)
                            putString("end_work", data.users__id_company__end_work)
                            putString("location", data.users__id_company__location)
                            putInt("is_presenced", 0)
                            putBoolean("is_location_checked", false)
                            putString("date_presence", "")
                            commit()
                        }
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        })
        dialog = BottomSheetDialog(this)

        //Set login button on click event
        btnLogin.setOnClickListener {
            val dialogSheet = layoutInflater.inflate(R.layout.dialog_loading, null)
            dialog.setContentView(dialogSheet)
            dialog.show()
            homeViewModel.updateUsersData(username.text.toString(), password.text.toString())
        }
    }

    private fun isUserSignedIn() {
        if (Util.isUserLoggedIn(this)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions, 1)
    }

    override fun onBackPressed() {

    }
}