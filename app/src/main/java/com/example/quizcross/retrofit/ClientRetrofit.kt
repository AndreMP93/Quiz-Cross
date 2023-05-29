package com.example.quizcross.retrofit


import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ClientRetrofit {
    private const val BASE_URL = "https://opentdb.com/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val openTriviaApiService: ServiceRetrofit by lazy {
        retrofit.create(ServiceRetrofit::class.java)
    }
}

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