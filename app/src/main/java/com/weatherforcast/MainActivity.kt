package com.weatherforcast

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.weatherforcast.application.MainApplication
import com.weatherforcast.dailog.OnGenericDialogButtonClickListener
import com.weatherforcast.dailog.GenericDialog
import com.weatherforcast.view_model.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.weatherforcast.base.BaseActivity
import com.weatherforcast.databinding.ActivityMainBinding
import com.weatherforcast.utils.Constant
import com.weatherforcast.utils.Constant.GPS_REQUEST_CODE
import com.weatherforcast.utils.Constant.REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
import com.weatherforcast.utils.checkGPSLocationService
import com.weatherforcast.utils.checkNetworkLocationService
import java.lang.Exception


@AndroidEntryPoint
class MainActivity : BaseActivity(), OnGenericDialogButtonClickListener {


    private lateinit var binding: ActivityMainBinding
    private val locationViewModel: LocationViewModel by viewModels()
    var isNeverAskClick: Boolean = false
    lateinit var dialog: DialogFragment
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        viewModelObserver()


    }


    private fun viewModelObserver() {


        locationViewModel.checkLocationPermissionAndGetLocationDataLiveData.observe(this, Observer {
            if (it) {


                if (foregroundPermissionApproved()) {
                    if (checkNetworkLocationService(locationManager))

                        registerLocationProvider(Constant.LOCATION_PROVIDER_NETWORK)

                    else if (checkGPSLocationService(locationManager)) {
                        registerLocationProvider(Constant.LOCATION_PROVIDER_GPS)
                    }                     else {
                        checkGPSEnabled()
                    }

                } else {
                    requestForegroundPermissions()
                }

            } else {
                try {
                    locationManager.removeUpdates(gpsLocationListener)
                    locationManager.removeUpdates(networkLocationListener)
                } catch (e: Exception) {

                }
            }
        })

    }

    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    protected fun requestForegroundPermissions() {
        val provideRationale = foregroundPermissionApproved()

        // If the user denied a previous request, but didn't check "Don't ask again", provide
        // additional rationale.
        if (provideRationale) {
           /* Snackbar.make(
                findViewById(R.id.coordinatorLayout),
                 MainApplication.context.permission_denied_explanation,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.result_ok) {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
                    )
                }
                .show()*/
        } else {
            //Log.e(TAG, "Request foreground only permission")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {

            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE -> when {


                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED -> {

                     if (checkNetworkLocationService(locationManager)) {

                        registerLocationProvider(Constant.LOCATION_PROVIDER_NETWORK)

                    }
                    else if (checkGPSLocationService(locationManager) || checkNetworkLocationService(
                            locationManager
                        )
                    ) {

                        registerLocationProvider(Constant.LOCATION_PROVIDER_GPS)

                    }  else {


                        checkGPSEnabled()


                    }


                }

                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED -> {

                    // Permission denied.

                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        // now, user has denied permission (but not permanently!)
                        val args = Bundle()
                        args.putString(
                            "title",
                            "Permission was denied, but is needed for weather forecast for your location"
                        )
                        args.putString("subTitle", "Please approve.")
                        dialog = GenericDialog(this)
                        dialog.arguments = args
                        dialog.show(this.supportFragmentManager, "SuccessDialog")


                    } else {

                        // now, user has denied permission permanently!


                        isNeverAskClick = true

                        val args = Bundle()
                        args.putString(
                            "title",
                            "Permission was denied, but is needed for weather forecast for your location"
                        )
                        args.putString(
                            "subTitle",
                            "Please approve from app settings."
                        )
                        dialog = GenericDialog(this)
                        dialog.arguments = args
                        dialog.show(this.supportFragmentManager, "SuccessDialog")

                    }
                }


            }


            else -> {

            }
        }


    }


    @SuppressLint("MissingPermission")
    private fun registerLocationProvider(location_provider: String) {


        if (Constant.LOCATION_PROVIDER_GPS == location_provider) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                0F,
                gpsLocationListener
            )
        }

        if (Constant.LOCATION_PROVIDER_NETWORK == location_provider) {

            //Log.e("Data", "hasNetwork"+hasNetwork)
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000,
                0F,
                networkLocationListener
            )

        }


    }

    val gpsLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {

            Log.e("locationGPS", location.latitude.toString())
            PrefManager.updateValue(Constant.LATITUDE, location.latitude.toFloat())
            PrefManager.updateValue(Constant.LONGITUDE, location.longitude.toFloat())
            locationViewModel.sendLocationDataToFragment(location)


        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    val networkLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.e("locationNet", location.latitude.toString())
            PrefManager.updateValue(Constant.LATITUDE, location.latitude.toFloat())
            PrefManager.updateValue(Constant.LONGITUDE, location.longitude.toFloat())
            locationViewModel.sendLocationDataToFragment(location)

        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }


    private fun checkGPSEnabled() {
        val manager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER).not()) {
            turnOnGPS()
        }
    }

    private fun turnOnGPS() {

        val request = LocationRequest.create().apply {
            interval = 2000
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(request)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnFailureListener {

            // Log.e("ErrorGPS", it.toString())
            if (it is ResolvableApiException) {
                try {

                    it.startResolutionForResult(this, GPS_REQUEST_CODE)


                } catch (sendEx: IntentSender.SendIntentException) {

                }
            }

        }

        task.addOnSuccessListener(this) {
            // Log.e("GPS_main", "OnSuccess")
            // GPS is ON
        }

        /*  .addOnSuccessListener {

            Log.e("GPS", "turned on")
        }*/
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GPS_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {

                    Log.e("GPS_main", "turn on")
                    registerLocationProvider(Constant.LOCATION_PROVIDER_GPS)


                }
                RESULT_CANCELED -> {
                    Log.e("GPS", "cancelled")
                }
                else -> {

                }
            }
        }
    }

    override fun onOkButtonClick() {
        dialog.dismiss()

        if (isNeverAskClick) {


            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts(
                "package",
                BuildConfig.APPLICATION_ID,
                null
            )
            intent.data = uri
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        } else {

            requestForegroundPermissions()

        }
    }




}