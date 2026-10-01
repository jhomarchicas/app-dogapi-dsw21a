package me.joo.retrofitdogapi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.joo.retrofitdogapi.data.DogApi
import me.joo.retrofitdogapi.ui.components.LoadingState
import me.joo.retrofitdogapi.ui.components.StatusMessage
import java.io.IOException
import retrofit2.HttpException

@Composable
fun BreedsScreen(
    onOpenGallery: (String, String?) -> Unit
) {
    var breeds by remember {
        mutableStateOf<Map<String, List<String>>>(emptyMap())
    }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var search by rememberSaveable { mutableStateOf("") }
    var expandedBreed by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var request by remember { mutableIntStateOf(0) }

    LaunchedEffect(request) {
        loading = true
        error = null

        try {
            val response = DogApi.service.breeds()
            check(response.status == "success")
            breeds = response.message.toSortedMap()
        } catch (e: IOException) {
            error = "No se pudo conectar. Revisa tu conexión a Internet."
        } catch (e: HttpException) {
            error = "No se pudieron cargar las razas."
        } catch (e: IllegalStateException) {
            error = "La respuesta de las razas no fue válida."
        } finally {
            loading = false
        }
    }

    val query = search.trim()

    val filtered = breeds.entries.filter { (breed, subBreeds) ->
        breed.contains(query, ignoreCase = true) ||
                subBreeds.any {
                    it.contains(query, ignoreCase = true)
                }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Buscar raza o subraza") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        when {
            loading -> LoadingState()

            error != null -> StatusMessage(
                message = error!!,
                onRetry = { request++ },
                modifier = Modifier.fillMaxSize()
            )

            filtered.isEmpty() -> StatusMessage(
                message = "No se encontraron razas.",
                modifier = Modifier.fillMaxSize()
            )

            else -> LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(filtered, key = { it.key }) { entry ->
                    val breed = entry.key
                    val subBreeds = entry.value
                    val expanded = expandedBreed == breed

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        text = breed.replaceFirstChar {
                                            it.uppercase()
                                        },
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Text(
                                        text = if (subBreeds.isEmpty()) {
                                            "Sin subrazas"
                                        } else {
                                            "${subBreeds.size} subrazas"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        onOpenGallery(breed, null)
                                    }
                                ) {
                                    Text("Galería")
                                }
                            }

                            if (subBreeds.isNotEmpty()) {
                                TextButton(
                                    onClick = {
                                        expandedBreed =
                                            if (expanded) null else breed
                                    }
                                ) {
                                    Text(
                                        if (expanded) "Ocultar subrazas"
                                        else "Ver subrazas"
                                    )
                                }

                                if (expanded) {
                                    subBreeds.sorted().forEach { subBreed ->
                                        OutlinedButton(
                                            onClick = {
                                                onOpenGallery(breed, subBreed)
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                subBreed.replaceFirstChar {
                                                    it.uppercase()
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}