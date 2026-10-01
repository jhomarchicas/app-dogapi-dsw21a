package me.joo.retrofitdogapi.ui


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import me.joo.retrofitdogapi.ui.screens.BreedsScreen
import me.joo.retrofitdogapi.ui.screens.GalleryScreen
import me.joo.retrofitdogapi.ui.screens.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DogApp() {
    var screen by rememberSaveable { mutableStateOf("home") }
    var breed by rememberSaveable { mutableStateOf("") }
    var subBreed by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    fun goBack() {
        screen = if (screen == "gallery") "breeds" else "home"
    }

    BackHandler(enabled = screen != "home") {
        goBack()
    }

    val title = when (screen) {
        "breeds" -> "Razas y subrazas"
        "gallery" -> listOfNotNull(
            breed.replaceFirstChar { it.uppercase() },
            subBreed?.replaceFirstChar { it.uppercase() }
        ).joinToString(" · ")
        else -> "DSW21A - DogAPI"
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (screen != "home") {
                        TextButton(onClick = { goBack() }) {
                            Text("← Volver")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (screen) {
                "home" -> HomeScreen(
                    onExplore = { screen = "breeds" }
                )

                "breeds" -> BreedsScreen(
                    onOpenGallery = { selectedBreed, selectedSubBreed ->
                        breed = selectedBreed
                        subBreed = selectedSubBreed
                        screen = "gallery"
                    }
                )

                "gallery" -> GalleryScreen(
                    breed = breed,
                    subBreed = subBreed
                )
            }
        }
    }
}