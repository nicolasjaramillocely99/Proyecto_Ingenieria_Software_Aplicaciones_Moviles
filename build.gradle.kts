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
// Aplicar configuración solo al módulo app, no al proyecto raíz
subprojects {
    sonar {
        properties {
            property("sonar.projectKey", "misw-kotlinvinilos")
            property("sonar.organization", "misw-kotlinvinilos")
            property("sonar.host.url", "https://sonarcloud.io")
            property("sonar.sources", "src/main/java")
            property("sonar.tests", "src/test/java,src/androidTest/java")
            property("sonar.java.binaries", "build/intermediates/javac/debug/classes")
            property("sonar.kotlin.binaries", "build/tmp/kotlin-classes/debug")
            property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
            property("sonar.junit.reportPaths", "build/test-results/testDebugUnitTest")
            property("sonar.android.lint.report", "build/reports/lint-results-debug.xml")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
