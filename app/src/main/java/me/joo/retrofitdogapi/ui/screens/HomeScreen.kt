package me.joo.retrofitdogapi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.joo.retrofitdogapi.data.DogApi
import me.joo.retrofitdogapi.ui.components.DogPhoto
import me.joo.retrofitdogapi.ui.components.StatusMessage
import java.io.IOException
import retrofit2.HttpException

@Composable
fun HomeScreen(onExplore: () -> Unit) {
    var imageUrl by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var request by remember { mutableIntStateOf(0) }

    LaunchedEffect(request) {
        loading = true
        error = null

        try {
            val response = DogApi.service.randomImage()
            check(response.status == "success")
            imageUrl = response.message
        } catch (e: IOException) {
            error = "No se pudo conectar. Revisa tu conexión a Internet."
        } catch (e: HttpException) {
            error = "El servidor no pudo completar la petición."
        } catch (e: IllegalStateException) {
            error = "No se pudo obtener una fotografía."
        } finally {
            loading = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Text(
                text = "Fotos de los\nperritos de DogAPI",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Mira fotos, razas y subrazas.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        loading -> CircularProgressIndicator()

                        error != null -> StatusMessage(
                            message = error!!,
                            onRetry = { request++ }
                        )

                        imageUrl != null -> DogPhoto(
                            url = imageUrl!!,
                            description = "Fotografía de un perro",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = { request++ },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver otro perrito")
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = onExplore,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Explorar razas")
            }
        }
    }
}