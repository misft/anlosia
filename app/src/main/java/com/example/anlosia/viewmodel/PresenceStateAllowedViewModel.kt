package com.example.anlosia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PresenceStateAllowedViewModel : ViewModel() {
    val isAllowed = MutableLiveData<Boolean>()

    init {
        isAllowed.value = null
    }

    fun getIsAllowed() : LiveData<Boolean> {
        return isAllowed
    }

    fun setAllowed() {
        isAllowed.value = true
    }

    fun unsetAllowed() {
        isAllowed.value = false
    }
}