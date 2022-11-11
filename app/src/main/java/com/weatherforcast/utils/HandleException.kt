package com.weatherforcast.utils

import android.content.Context
import com.weatherforcast.R
import org.json.JSONException
import java.lang.Exception
import java.lang.NullPointerException
import java.net.ConnectException

fun Context.handleException(t: Throwable): String {
    var errorMessage = ""

    when (t) {
        is ConnectException -> {
          //  errorMessage = getString(R.string.no_internet_connection)
        }

        is JSONException -> {
           // errorMessage = getString(R.string.json_error)
        }

        is NullPointerException -> {
          //  errorMessage = getString(R.string.null_error)

        }

        is Exception -> {
            //errorMessage = getString(R.string.general_error_msg)
        }

    }

    return errorMessage

}