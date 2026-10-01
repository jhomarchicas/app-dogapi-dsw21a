package me.joo.retrofitdogapi.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DogApi {
    val service: DogApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DogApiService::class.java)
    }
}