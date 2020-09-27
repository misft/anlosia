package com.example.anlosia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import com.example.anlosia.model.Location
import com.example.anlosia.model.RecordLocationResponse
import com.example.anlosia.repositories.RecordLocationRepo

class LocationViewModel {
    val recordLocationRepo : RecordLocationRepo = RecordLocationRepo()

    var recordLocation = MutableLiveData<RecordLocationResponse>()

    init {
        recordLocation.value = null
    }

    fun getRecordLocation() : LiveData<RecordLocationResponse> {
        return recordLocation
    }
}