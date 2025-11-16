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
        
        // Configurar solo el módulo app - NO usar sonar.modules para evitar duplicación
        // Las rutas son relativas al directorio raíz del proyecto
        property("sonar.sources", "app/src/main/java")
        property("sonar.tests", "app/src/test/java,app/src/androidTest/java")
        property("sonar.java.binaries", "app/build/intermediates/javac/debug/classes")
        property("sonar.kotlin.binaries", "app/build/tmp/kotlin-classes/debug")
        property("sonar.coverage.jacoco.xmlReportPaths", "app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
        property("sonar.junit.reportPaths", "app/build/test-results/testDebugUnitTest")
        property("sonar.android.lint.report", "app/build/reports/lint-results-debug.xml")
        
        // Excluir el directorio raíz para evitar que SonarQube cree un módulo automático
        property("sonar.exclusions", "**/build/**,**/*.gradle.kts")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
