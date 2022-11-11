package com.weatherforcast.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.weatherforcast.PrefManager
import com.weatherforcast.R
import com.weatherforcast.dailog.GenericDialog
import com.weatherforcast.dailog.OnGenericDialogButtonClickListener
import com.weatherforcast.Resource
import com.weatherforcast.base.BaseFragmentWithBinding
import com.weatherforcast.adapter.ForecastListAdapter
import com.weatherforcast.application.MainApplication
import com.weatherforcast.databinding.FragmentForecastListBinding
import com.weatherforcast.db.DbViewModelFactory
import com.weatherforcast.db.weather_data.WeatherDataDbEntity
import com.weatherforcast.db.weather_data.WeatherDataRoomViewModel
import com.weatherforcast.utils.*
import com.weatherforcast.utils.Constant.IS_WEATHER_DATA_LOADED_FIRST_TIME
import com.weatherforcast.work_manager.PeriodicWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class ForecastListFragment : BaseFragmentWithBinding<FragmentForecastListBinding>(
    FragmentForecastListBinding::inflate
), OnGenericDialogButtonClickListener {

    private val forecastListViewModel by viewModels<ForecastListViewModel>()

    @Inject
    lateinit var forecastListAdapter: ForecastListAdapter
    lateinit var dialog: DialogFragment

    var weatherDataList: MutableList<WeatherDataDbEntity> = mutableListOf()


    private lateinit var periodicWorkRequest: WorkRequest

    private val weatherDataRoomViewModel: WeatherDataRoomViewModel by viewModels {
        DbViewModelFactory(
            weatherDataDao = (activity?.application as MainApplication).database.dao()
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewWeatherForecastList.layoutManager =
            LinearLayoutManager(requireContext())

        periodicWorkRequest = PeriodicWorkRequestBuilder<PeriodicWorker>(12, TimeUnit.HOURS).build()
        onLocation().checkLocationPermissionAndGetLocationData(true)
        viewModelObserver()


    }


    private fun viewModelObserver() {


        onLocation().getLocationUpdateLiveData.observe(viewLifecycleOwner) {

            requireActivity().isOnline(requireContext(), object : Status {
                override fun online() {

                    lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.STARTED) {

                            binding.progressBar.visibility = View.VISIBLE
                            forecastListViewModel.getWeatherForecast(
                                it.latitude, it.longitude
                            ).collectLatest {
                                when (it) {

                                    is Resource.Success -> {

                                        for (weathearData in it.response?.list!!) {

                                            weatherDataList.add(
                                                WeatherDataDbEntity(
                                                    0,
                                                    weathearData.weather!![0]?.main.toString(),
                                                    weathearData.main!!.temp.toString(),
                                                    weathearData.main.feels_like.toString(),
                                                    weathearData.main.humidity.toString(),
                                                    weathearData.dt_txt.toString(),
                                                    weathearData.weather[0]!!.icon.toString()
                                                )
                                            )

                                        }

                                        PrefManager.updateValue(
                                            Constant.CITY,
                                            it.response.city?.name
                                        )

                                        checkAndSetRainForecast(requireContext(),it.response,)
                                        showDataInView(weatherDataList)

                                    }
                                    is Resource.Error -> {

                                        binding.progressBar.visibility = View.INVISIBLE
                                        Toast.makeText(
                                            requireContext(),
                                            requireContext().getString(R.string.something_went_wrong),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }

                    }

                }

                override fun offline() {

                    if (PrefManager.getBool(Constant.IS_WEATHER_DATA_LOADED_FIRST_TIME)) {

                        lifecycleScope.launch {
                            callLocalDbData()
                        }
                    } else {

                        binding.progressBar.visibility = View.INVISIBLE
                        onLocation().checkLocationPermissionAndGetLocationData(false)
                        val args = Bundle()
                        args.putString("title", "Error")
                        args.putString(
                            "subTitle",
                            "Please turn on Internet Connection"
                        )
                        dialog = GenericDialog(this@ForecastListFragment)
                        dialog.arguments = args
                        dialog.show(childFragmentManager, "GenericDialog")


                    }
                }


            })

        }

    }

    private fun showDataInView(weatherList: List<WeatherDataDbEntity>) {


        forecastListAdapter.setData(
            requireActivity(),
            weatherList
        )
        binding.recyclerViewWeatherForecastList.adapter = forecastListAdapter
        binding.recyclerViewWeatherForecastList.visibility = View.VISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        onLocation().checkLocationPermissionAndGetLocationData(false)
        PrefManager.updateValue(IS_WEATHER_DATA_LOADED_FIRST_TIME, true)
        WorkManager.getInstance(requireActivity().applicationContext).enqueue(periodicWorkRequest)
        binding.textViewCity.text = PrefManager.getString(Constant.CITY)


    }

    override fun onOkButtonClick() {
        dialog.dismiss()
    }


    private suspend fun callLocalDbData() {

        weatherDataRoomViewModel.getWeatherData().collect { dbList ->
            Log.e("dblist", "TTT" + dbList.toString())
            showDataInView(dbList)
        }
    }

    override fun onPause() {
        super.onPause()
        onLocation().checkLocationPermissionAndGetLocationData(false)
    }


}