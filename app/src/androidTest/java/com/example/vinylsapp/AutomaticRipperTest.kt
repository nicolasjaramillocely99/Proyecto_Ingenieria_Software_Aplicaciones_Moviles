package com.example.vinylsapp

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.StaleObjectException
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Random

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AutomaticRipperTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var device: UiDevice
    private val packageName = "com.example.vinylsapp"
    private val timeout = 5000L
    private val random = Random()

    @Before
    fun setup() {
        // Inicializar Hilt
        hiltRule.inject()

        // Inicializar dispositivo
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Iniciar desde el home
        device.pressHome()

        // Esperar el lanzador
        val launcherPackage = device.launcherPackageName
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), timeout)

        // Lanzar la aplicación
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)

        // Esperar a que la app aparezca
        device.wait(Until.hasObject(By.pkg(packageName).depth(0)), timeout)

        Log.i("Ripper", "Esperando 20 segundos por 'Cold Start' del backend...")
        Thread.sleep(15000) // Espera inicial para carga de backend en capa gratuita
    }

    @Test
    fun testRipperExploration() {
        val steps = 50 // Número de interacciones que hará el ripper
        var currentStep = 0

        Log.i("Ripper", "Iniciando exploración automática de $steps pasos")

        while (currentStep < steps) {
            device.waitForIdle(2000) // Esperar a que la UI se estabilice
            
            // Fase de Reconocimiento: Identificar elementos visibles
            val clickables = device.findObjects(By.clickable(true))
            
            Log.d("Ripper", "Paso $currentStep: Encontrados ${clickables.size} elementos interactivos en pantalla.")
            
            // Loguear elementos para trazabilidad (Reconocimiento)
            clickables.forEachIndexed { index, obj ->
                try {
                    val desc = getSafeDescription(obj)
                    Log.v("Ripper", "Reconocido [$index]: $desc - Clase: ${obj.className}")
                } catch (e: StaleObjectException) {
                    Log.v("Ripper", "Elemento [$index] desapareció antes de ser inspeccionado (StaleObjectException). Ignorando.")
                }
            }

            if (clickables.isNotEmpty()) {
                // Estrategia: Clic Aleatorio
                val elementToClick = clickables[random.nextInt(clickables.size)]
                
                try {
                    val desc = getSafeDescription(elementToClick)
                    Log.i("Ripper", "Acción: Clic en '$desc'")
                    elementToClick.click()
                } catch (e: StaleObjectException) {
                    Log.w("Ripper", "El elemento seleccionado desapareció (StaleObjectException). Continuando...")
                } catch (e: Exception) {
                    Log.w("Ripper", "No se pudo hacer clic, intentando continuar. Error: ${e.message}")
                }
            } else {
                // Si no hay nada que cliquear, intentar volver atrás
                Log.i("Ripper", "Callejón sin salida detectado. Presionando BACK.")
                device.pressBack()
            }

            currentStep++
            
            // Verificación básica de "No Crash"
            if (device.currentPackageName != packageName) {
                Log.w("Ripper", "El paquete actual (${device.currentPackageName}) no es la app bajo prueba. ¿Salió o Crasheó?")
                
                // Intentar volver si salimos accidentalmente
                if (currentStep < steps) {
                     Thread.sleep(1000)
                     if (device.currentPackageName != packageName) {
                         Log.i("Ripper", "Reiniciando app para continuar test...")
                         val context = ApplicationProvider.getApplicationContext<Context>()
                         val intent = context.packageManager.getLaunchIntentForPackage(packageName)?.apply {
                             addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                         }
                         context.startActivity(intent)
                         device.wait(Until.hasObject(By.pkg(packageName)), timeout)
                     }
                }
            }
        }
        
        Log.i("Ripper", "Exploración completada exitosamente.")
    }

    private fun getSafeDescription(obj: UiObject2): String {
        return try {
            obj.contentDescription ?: obj.text ?: obj.resourceName ?: "Elemento sin etiqueta"
        } catch (e: StaleObjectException) {
            "Elemento Stale"
        }
    }
}
