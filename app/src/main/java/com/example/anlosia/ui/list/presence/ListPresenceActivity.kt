package com.example.anlosia.ui.list.presence

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anlosia.R
import com.example.anlosia.model.ListPresenceResponse
import com.example.anlosia.util.Util

class ListPresenceActivity : AppCompatActivity() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var listPresenceLayoutManager: RecyclerView.LayoutManager
    private lateinit var listPresenceAdapter: RecyclerView.Adapter<*>

    private lateinit var listPresenceViewModel: ListPresenceViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        setContentView(R.layout.activity_list_presence)

        listPresenceViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ListPresenceViewModel::class.java]
        sharedPreferences = this.getSharedPreferences("user", Context.MODE_PRIVATE)

        listPresenceLayoutManager = LinearLayoutManager(this)

        recyclerView = findViewById(R.id.rc_list_presence)
        progressBar = findViewById(R.id.loading)
        progressBar.visibility = View.VISIBLE

        recyclerView.apply {
            layoutManager = listPresenceLayoutManager
        }

        listPresenceViewModel.getListPresenceResponse().observe(this, Observer<ListPresenceResponse> {
            it?.let {
                progressBar.visibility = View.GONE
                recyclerView.adapter = ListPresenceAdapter(it.results)
            }
        })

        val id_user = sharedPreferences.getInt("id", 0)
        listPresenceViewModel.getListPresence(id_user)
    }
}