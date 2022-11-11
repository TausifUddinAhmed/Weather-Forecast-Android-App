package com.weatherforcast.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.weatherforcast.BuildConfig
import com.weatherforcast.network.ApiInterface
import com.weatherforcast.repository.remote.Repository
import com.weatherforcast.utils.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.Interceptor


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient
        .Builder()
        .build()


    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {


        val interceptor = HttpLoggingInterceptor()

        val clientInterceptor = Interceptor { chain: Interceptor.Chain ->
            var request = chain.request()
            val url = request.url.newBuilder().addQueryParameter(
                Constant.APP_ID, BuildConfig.API_KEY
            ).addQueryParameter(Constant.UNITS, "metric").build()
            request = request.newBuilder().url(url).build()
            chain.proceed(request)
        }


        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }


        val client = OkHttpClient.Builder()
            .addNetworkInterceptor(clientInterceptor)
            .addInterceptor(interceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()


        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

    }


    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiInterface =
        retrofit.create(ApiInterface::class.java)


    @Singleton
    @Provides
    fun providesRepository(apiService: ApiInterface) = Repository(apiService)


}