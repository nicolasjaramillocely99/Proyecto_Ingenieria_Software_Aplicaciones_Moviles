// Top-level build file
buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("com.android.application") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("org.sonarqube") version "5.1.0.4882"
}

// Configuración de SonarQube
sonar {
    properties {
        property("sonar.projectKey", "misw-kotlinvinilos")
        property("sonar.organization", "misw-kotlinvinilos")
        property("sonar.host.url", "https://sonarcloud.io")
        
        // Configurar solo el módulo app para evitar duplicación
        // NO definir sonar.sources ni sonar.tests a nivel raíz
        // Solo configurar el módulo 'app'
        property("sonar.modules", "app")
        
        // Configuración del módulo app (rutas relativas desde app/)
        property("sonar.app.sources", "src/main/java")
        property("sonar.app.tests", "src/test/java,src/androidTest/java")
        property("sonar.app.java.binaries", "build/intermediates/javac/debug/classes")
        property("sonar.app.kotlin.binaries", "build/tmp/kotlin-classes/debug")
        property("sonar.app.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
        property("sonar.app.junit.reportPaths", "build/test-results/testDebugUnitTest")
        property("sonar.app.android.lint.report", "build/reports/lint-results-debug.xml")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
