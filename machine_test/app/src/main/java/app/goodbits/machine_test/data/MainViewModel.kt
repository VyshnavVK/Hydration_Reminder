package com.tailormade.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers


class MainViewModel:ViewModel() {
    private val mainRepo = MainRepository()

    fun stateWise(context: Context) = liveData(Dispatchers.IO) {
        emit(mainRepo.stateWise(context))
    }


}