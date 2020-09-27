package com.example.anlosia.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anlosia.model.LoginResponse
import com.example.anlosia.model.LoginResponseUsersData
import com.example.anlosia.repositories.LoginRepo
import com.example.anlosia.util.Util

class HomeViewModel : ViewModel() {
    var usersData = MutableLiveData<LoginResponse>()

    init {
        usersData.value = null
    }

    fun getUsersData() : LiveData<LoginResponse> {
        return usersData
    }

    fun updateUsersData(username: String, password: String) {
        usersData.value = null
        val executor = LoginRepo().execute(username, password).get()

        usersData.value = executor
    }
}