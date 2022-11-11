package com.weatherforcast.network

import android.util.Log
import com.weatherforcast.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response


fun <T> result(callApi: suspend () -> Response<T>): Flow<Resource<T?>> {
    return flow<Resource<T?>> {
        delay(500)
        try {
            val response = callApi.invoke()
            response.let {

                if (response.isSuccessful) {
                    emit(Resource.Success(response.body()))

                } else {
                    emit(Resource.Error(response.message(), response.code()))
                }


            }
            delay(500)

        } catch (e: Exception) {
           // val errorMsg = MainApplication.context.handleException(t)
            //emit(Resource.Error(errorMsg))
            Log.e("Error", e.stackTraceToString())

        }
    }.flowOn(Dispatchers.IO)
}

