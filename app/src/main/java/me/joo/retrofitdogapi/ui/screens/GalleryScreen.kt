package me.joo.retrofitdogapi.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import me.joo.retrofitdogapi.data.DogApi
import me.joo.retrofitdogapi.ui.components.DogPhoto
import me.joo.retrofitdogapi.ui.components.LoadingState
import me.joo.retrofitdogapi.ui.components.StatusMessage
import java.io.IOException
import retrofit2.HttpException

@Composable
fun GalleryScreen(
    breed: String,
    subBreed: String?
) {
    var images by remember {
        mutableStateOf<List<String>>(emptyList())
    }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedImage by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var request by remember { mutableIntStateOf(0) }

    LaunchedEffect(breed, subBreed, request) {
        loading = true
        error = null

        try {
            val response = if (subBreed == null) {
                DogApi.service.breedImages(breed)
            } else {
                DogApi.service.subBreedImages(breed, subBreed)
            }

            check(response.status == "success")
            images = response.message.distinct()
        } catch (e: IOException) {
            error = "No se pudo conectar. Revisa tu conexión a Internet."
        } catch (e: HttpException) {
            error = "No se pudo cargar la galería."
        } catch (e: IllegalStateException) {
            error = "La respuesta de la galería no fue válida."
        } finally {
            loading = false
        }
    }

    when {
        loading -> LoadingState()

        error != null -> StatusMessage(
            message = error!!,
            onRetry = { request++ },
            modifier = Modifier.fillMaxSize()
        )

        images.isEmpty() -> StatusMessage(
            message = "Esta raza no tiene fotografías disponibles.",
            modifier = Modifier.fillMaxSize()
        )

        else -> Column(Modifier.fillMaxSize()) {
            Text(
                text = "${images.size} fotografías · Toca una para ampliarla",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                )
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(images, key = { it }) { url ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { selectedImage = url }
                    ) {
                        DogPhoto(
                            url = url,
                            description = "Perro de raza $breed",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    selectedImage?.let { url ->
        Dialog(
            onDismissRequest = { selectedImage = null }
        ) {
            Card(
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DogPhoto(
                        url = url,
                        description = "Fotografía ampliada",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp)
                    )

                    TextButton(
                        onClick = { selectedImage = null }
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}