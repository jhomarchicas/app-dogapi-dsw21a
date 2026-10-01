package me.joo.retrofitdogapi.data

data class DogImageResponse(
    val message: String,
    val status: String
)

data class BreedsResponse(
    val message: Map<String, List<String>>,
    val status: String
)

data class GalleryResponse(
    val message: List<String>,
    val status: String
)