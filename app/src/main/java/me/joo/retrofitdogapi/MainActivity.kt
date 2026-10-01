package me.joo.retrofitdogapi


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import me.joo.retrofitdogapi.ui.DogApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFFEC744A),
                    onPrimary = Color.White,
                    secondary = Color(0xFF526B85),
                    background = Color(0xFFFFF8F3),
                    surface = Color.White
                )
            ) {
                DogApp()
            }
        }
    }
}