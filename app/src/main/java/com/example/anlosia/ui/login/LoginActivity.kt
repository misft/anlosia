package com.example.anlosia.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.anlosia.MainActivity
import com.example.anlosia.R
import com.example.anlosia.model.LoginResponse
import com.example.anlosia.model.LoginResponseUsersData
import com.example.anlosia.ui.home.HomeViewModel
import com.example.anlosia.util.Util
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.delay


class LoginActivity : AppCompatActivity() {
    //Initiate possible variable
    private var homeViewModel: HomeViewModel = HomeViewModel()
    private lateinit var progressBarContainer: RelativeLayout
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        //If user is already signed in, redirect it
        isUserSignedIn()

        //Inflate layout
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        setContentView(R.layout.activity_login)

        //Get UI to interact later
        progressBarContainer = findViewById(R.id.progress_bar)
        loginButton = findViewById(R.id.btnLogin)

        //Initiate view model
        homeViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[HomeViewModel::class.java]

        //Set login button on click event
        loginButton.setOnClickListener {
            //Make progress bar visible
            progressBarContainer.visibility = View.VISIBLE
            //Logged in the user
            homeViewModel.updateUsersData(username.text.toString(), password.text.toString())
        }

        //Observe changes in viewmodel to logged in user
        observeResponse()
    }

    private fun isUserSignedIn() {
        if (Util.isUserLoggedIn(this)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeResponse() {
        homeViewModel.getUsersData().observe(this, loginObserver)
    }

    override fun onResume() {
        super.onResume()

        //Request permission to access location
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions, 1)
    }

    override fun onStop() {
        super.onStop()
        //Remove observer
        homeViewModel.getUsersData().removeObserver(loginObserver)
    }

    private val loginObserver = Observer<LoginResponse> {
        Util.logD(it?.toString())
        it?.let {
            if (it.api_status == 0) {
                Toast.makeText(this, "Username atau password anda salah", Toast.LENGTH_SHORT)
                    .show()
                return@let
            }
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
                    putString("profile_pic", "https://anlosia.xyz/media/" + data.users__profile_pic)
                    putString("start_work", data.users__start_work)
                    putString("end_work", data.users__end_work)
                    putString("location", data.users__location)
                    putInt("is_presenced", 0)
                    apply()
                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        progressBarContainer.visibility = View.GONE
    }
}
