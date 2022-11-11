package com.weatherforcast.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities




fun Context.isOnline(context: Context?, statusCallBack: Status) {

        var networkStatus = false

        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val n =
            cm.activeNetwork
    if (n != null) {
            val nc = cm.getNetworkCapabilities(n)
            //It will check for both wifi and cellular network

            networkStatus =
                nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI
                )
        }

        if (networkStatus) {
            statusCallBack.online()
        } else {
            statusCallBack.offline()

        }


    }

    interface Status {
        fun online()
        fun offline()
    }

