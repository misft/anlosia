package com.example.anlosia.ui.list.vacation

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anlosia.R
import com.example.anlosia.model.ListVacationResponse

class ListVacationActivity : AppCompatActivity() {

    private lateinit var listVacationViewModel : ListVacationViewModel
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var recyclerView: RecyclerView
    private lateinit var listVacationAdapter: RecyclerView.Adapter<*>
    private lateinit var listVacationManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        setContentView(R.layout.activity_list_vacation)

        listVacationViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ListVacationViewModel::class.java]

        recyclerView = findViewById(R.id.rc_list_vacation)
        listVacationManager = LinearLayoutManager(this)
        listVacationAdapter = ListVacationAdapter(listOf())

        recyclerView.apply {
            layoutManager = listVacationManager
        }

        listVacationViewModel.getListVacationResponse().observe(this, Observer<ListVacationResponse> {
            it?.let {
                recyclerView.adapter = ListVacationAdapter(it.results)
            }
        })

        sharedPreferences = this.getSharedPreferences("user", Context.MODE_PRIVATE)
        val id_user = sharedPreferences.getInt("id", 0)

        listVacationViewModel.getListVacation(id_user)
    }
}