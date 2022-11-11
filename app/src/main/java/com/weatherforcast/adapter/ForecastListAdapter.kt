package com.weatherforcast.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.weatherforcast.R
import com.weatherforcast.databinding.ItemWeatherForecastListBinding
import com.weatherforcast.db.weather_data.WeatherDataDbEntity
import com.weatherforcast.utils.Constant
import com.weatherforcast.utils.Constant.ICON_URL_EXTENSION
import javax.inject.Inject

class ForecastListAdapter @Inject constructor() :
    RecyclerView.Adapter<ForecastListAdapter.ForeListViewHolder>() {

    private var oldItemList = emptyList<WeatherDataDbEntity>()
    private var context: Context? = null


    class ForeListViewHolder(val binding: ItemWeatherForecastListBinding) : RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ForeListViewHolder {

        val binding =
            ItemWeatherForecastListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForeListViewHolder(binding)
    }

    override fun onBindViewHolder(holderProductSaleRegList: ForeListViewHolder, position: Int) {


        addData(holderProductSaleRegList, position)




    }

    private fun addData(
        holderSaleReg: ForeListViewHolder,
        position: Int
    ) {

        holderSaleReg.binding.apply {



            try {

                tvWeatherCondition.text = oldItemList[position].weather_status
                tvDate.text = oldItemList[position].date?.substring(0,10).toString()
                tvTemperature.text = oldItemList[position].temp+context?.getString(R.string.degree_symbol)
                tvFeelLike.text = oldItemList[position].feels_like+context?.getString(R.string.degree_symbol)
                tvTime.text  = oldItemList[position].date.substring(10,).toString()
                tvHumidity.text  = oldItemList[position].humidity+context?.getString(R.string.degree_symbol)

                Glide.with(context!!)
                    .load(Constant.ICON_URL+ oldItemList[position].image_url+ICON_URL_EXTENSION)
                    .placeholder(R.drawable.ic_place_holder_image)
                    .into(imageViewWeatherImage)




            }catch ( e :Exception){

                Log.e("ForecastListAdapter", e.stackTraceToString())

            }




        }

        holderSaleReg.itemView.setOnClickListener {
            //   onItemClick.getSku(oldItemList[position].sku)
        }
    }

    override fun getItemCount(): Int {
        return oldItemList.size
    }


    fun setData(context: Context, newItemList: List<WeatherDataDbEntity>) {

        this.context = context
        oldItemList = newItemList!!
        //   this.onCollectionListItemClickListener = onCollectionListItemClickListener
        val diffUtil = ForecastListDiffUtils(oldItemList, newItemList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        diffResult.dispatchUpdatesTo(this)
    }



}