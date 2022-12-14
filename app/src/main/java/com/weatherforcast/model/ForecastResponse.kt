package com.weatherforcast.model


data class ForecastResponse(
    val city: City?,
    val cnt: Int?,
    val cod: String?,
    val list: List<WeatherData>?,
    val message: Int?
) {
    data class City(
        val coord: Coord?,
        val country: String?,
        val id: Int?,
        val name: String?,
        val population: Int?,
        val sunrise: Int?,
        val sunset: Int?,
        val timezone: Int?
    ) {
        data class Coord(
            val lat: Double?,
            val lon: Double?
        )
    }

    data class WeatherData  (
        val clouds: Clouds?,
        var dt: Long?,
        val dt_txt: String?,
        val main: Main?,
        val pop: Int?,
        val sys: Sys?,
        val visibility: Int?,
        val weather: List<Weather?>?,
        val wind: Wind?
    ) {
        data class Clouds(
            val all: Int?
        )

        data class Main(
            val feels_like: Double?,
            val grnd_level: Int?,
            val humidity: Int?,
            val pressure: Int?,
            val sea_level: Int?,
            val temp: Double?,
            val temp_kf: Double?,
            val temp_max: Double?,
            val temp_min: Double?
        )

        data class Sys(
            val pod: String?
        )

        data class Weather(
            val description: String?,
            val icon: String?,
            val id: Int?,
            val main: String?
        )

        data class Wind(
            val deg: Int?,
            val gust: Double?,
            val speed: Double?
        )
    }

    override fun toString(): String {
        return "ForecastResponse(city=$city, cnt=$cnt, cod=$cod, list=$list, message=$message)"
    }
}

