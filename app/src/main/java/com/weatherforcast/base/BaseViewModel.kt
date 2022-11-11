package com.weatherforcast.base

import androidx.lifecycle.ViewModel
import com.weatherforcast.repository.remote.Repository
import javax.inject.Inject

abstract class BaseViewModel : ViewModel() {

    @Inject
    protected lateinit var repository: Repository

}
