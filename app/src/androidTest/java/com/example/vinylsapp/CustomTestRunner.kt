package com.example.vinylsapp

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Custom runner para pruebas con Hilt
 * Necesario para poder usar @HiltAndroidTest en las pruebas
 */
class CustomTestRunner : AndroidJUnitRunner() {
    
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
    
    override fun onCreate(arguments: Bundle?) {
        super.onCreate(arguments)
        // Set properties to help with Espresso initialization
        // These may help with InputManager issues on some emulators
        System.setProperty("androidx.test.espresso.base.IdlingResourcePolicy.waitForIdleSync", "false")
    }
}
