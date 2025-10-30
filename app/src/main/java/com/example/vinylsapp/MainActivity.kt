package com.example.vinylsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.vinylsapp.ui.navigation.VinylsApp
import com.example.vinylsapp.ui.theme.VinylsAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal de la aplicación
 * Configurada con Hilt para inyección de dependencias
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VinylsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VinylsApp()
                }
            }
        }
    }
}
