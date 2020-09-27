package com.example.anlosia.ui.list.vacation

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.anlosia.R
import com.example.anlosia.model.ListVacationResponse

class ListVacationActivity : AppCompatActivity() {

    private lateinit var listVacationViewModel : ListVacationViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listVacationViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ListVacationViewModel::class.java]

        listVacationViewModel.getListVacationResponse().observe(this, Observer<ListVacationResponse> {
            it?.let {

            }
        })

        sharedPreferences = this.getSharedPreferences("user", Context.MODE_PRIVATE)
        val id_user = sharedPreferences.getInt("id", 0)

        listVacationViewModel.getListVacation(id_user)
    }
}