package me.joo.retrofitdogapi.data


import retrofit2.http.GET
import retrofit2.http.Path

interface DogApiService {
    @GET("breeds/image/random")
    suspend fun randomImage(): DogImageResponse

    @GET("breeds/list/all")
    suspend fun breeds(): BreedsResponse

    @GET("breed/{breed}/images")
    suspend fun breedImages(
        @Path("breed") breed: String
    ): GalleryResponse

    @GET("breed/{breed}/{subBreed}/images")
    suspend fun subBreedImages(
        @Path("breed") breed: String,
        @Path("subBreed") subBreed: String
    ): GalleryResponse
}