package com.ritualkart.sevahetu.network

import com.ritualkart.sevahetu.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {

    private const val BASE_URL = "https://raw.githubusercontent.com/sevahetu/sevaheu/main/"


    private fun getLogging(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .callTimeout(120, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging)
            return httpClient.build()
        }
        return httpClient.build()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getLogging())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
   /* private fun getBannerRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BANNER_URL)
            .client(getLogging())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }*/

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
  //  val apiBannerService: ApiService = getBannerRetrofit().create(ApiService::class.java)

}