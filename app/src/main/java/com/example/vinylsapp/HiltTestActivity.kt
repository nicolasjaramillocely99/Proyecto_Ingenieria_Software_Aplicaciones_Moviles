package com.example.vinylsapp

import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity de prueba habilitada con Hilt para tests E2E
 * Permite usar hiltViewModel() en las pantallas de Compose durante las pruebas
 * 
 * Esta Activity es creada automáticamente por createAndroidComposeRule<HiltTestActivity>()
 * y no necesita implementar onCreate() ya que setContent() es llamado por la regla de prueba
 * 
 * Nota: Esta Activity está en el source set principal para que pueda ser referenciada
 * desde los manifests de test. No es exportada y no tiene intent-filters, por lo que
 * no es accesible para usuarios finales.
 */
@AndroidEntryPoint
class HiltTestActivity : ComponentActivity()

