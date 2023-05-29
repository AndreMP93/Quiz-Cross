package com.example.quizcross.retrofit


import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient private constructor() {
    companion object{
        private lateinit var INSTANCE: Retrofit

        private fun getRetrofitInstance(): Retrofit{
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request()
                        .newBuilder()
                        .build()
                    return chain.proceed(request)
                }

            })
            if(!::INSTANCE.isInitialized){
                synchronized(RetrofitClient::class.java){
                    INSTANCE = Retrofit.Builder()
                        .baseUrl("https://opentdb.com/")
                        .client(httpClient.build())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                }
            }
            return INSTANCE
        }

        fun <T> getServices(serviceClass: Class<T>): T{
            return getRetrofitInstance().create(serviceClass)
        }
    }
}