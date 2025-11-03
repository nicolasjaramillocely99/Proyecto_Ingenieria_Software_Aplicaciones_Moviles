# Vinilos App - Aplicación Móvil Android

Aplicación móvil para gestionar una colección de álbumes de vinilo desarrollada con **Kotlin** y **Jetpack Compose**.

## 📋 Tabla de Contenidos

- [Requisitos Previos](#-requisitos-previos)
- [Instalación Paso a Paso](#-instalación-paso-a-paso)
- [Configuración del Backend](#-configuración-del-backend)
- [Ejecución de la Aplicación](#-ejecución-de-la-aplicación)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)

---

## 📋 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado lo siguiente:

### 1. **Java Development Kit (JDK) 17 o superior**
   - [Descargar JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
   - Verifica la instalación:
     ```bash
     java -version
     ```

### 2. **Android Studio** (última versión estable)
   - [Descargar Android Studio](https://developer.android.com/studio)
   - Incluye:
     - Android SDK
     - Android SDK Platform-Tools
     - Emulador de Android

### 4. **Git** (para clonar el repositorio)
   - [Descargar Git](https://git-scm.com/downloads)

---

## 🚀 Instalación Paso a Paso

### **Paso 1: Clonar el Repositorio**

```bash
# Clona el repositorio
git clone https://github.com/nicolasjaramillocely99/Proyecto_Ingenieria_Software_Aplicaciones_Moviles.git

# Navega al directorio del proyecto
cd Proyecto_Ingenieria_Software_Aplicaciones_Moviles
```

## � Configuración del Backend

La aplicación necesita el backend ejecutándose para funcionar. Este se encuentra ya configurado en la aplicacion y se esta ejecutando en el enlace :  https://backvynils-8c16.onrender.com/

## 🔧 Configuración de Android Studio

### **Paso 2: Abrir el Proyecto en Android Studio**

1. Abre **Android Studio**
2. Selecciona **Open** (Abrir)
3. Navega a la carpeta `Proyecto_Ingenieria_Software_Aplicaciones_Moviles`
4. Haz clic en **OK**

### **Paso 3: Sincronizar Gradle**

Android Studio sincronizará automáticamente el proyecto con Gradle. Esto puede tomar algunos minutos la primera vez.

**Si aparece un error de sincronización:**
- Ve a `File > Invalidate Caches / Restart`
- Selecciona `Invalidate and Restart`


### **Paso 4: Descargar Dependencias**

Gradle descargará automáticamente todas las dependencias necesarias:
- Retrofit
- Coil
- Hilt
- Jetpack Compose
- etc.

**Espera a que termine la descarga** (verás una barra de progreso en la parte inferior).

---

## 📱 Ejecución de la Aplicación

#### **Paso 5: Crear un Emulador (si no tienes uno)**

1. En Android Studio, haz clic en **Device Manager** (ícono de teléfono)
2. Haz clic en **Create Device**
3. Selecciona un dispositivo (ej: Pixel 5)
4. Selecciona una imagen del sistema (recomendado: **API 33** o superior)
5. Haz clic en **Finish**

#### **Paso 6: Iniciar el Emulador**

1. En **Device Manager**, haz clic en ▶️ junto a tu emulador
2. Espera a que el emulador inicie completamente

#### **Paso 7: Ejecutar la Aplicación**

1. Haz clic en el botón **Run** (▶️) en la barra superior de Android Studio
2. Selecciona tu emulador de la lista
3. Haz clic en **OK**

La aplicación se compilará, instalará y ejecutará automáticamente.

#### **Paso 8: **Alternativa local** En la raiz del proyecto tambien se encuentra un archivo apk para su instalacion en dispositivos fisicos **


## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Arquitectura:** MVVM + Repository Pattern
- **Inyección de Dependencias:** Hilt (Dagger)
- **Networking:** Retrofit + OkHttp
- **Imágenes:** Coil
- **Asincronía:** Coroutines + Flow
- **Navegación:** Navigation Compose


# 🧪 Pruebas - Vinilos App
El documento de estrategia de pruebas y arquitectura de la aplicacion se encuentra en la pagina sprint 01 en la wiki del repositorio

## 📁 Estructura de Pruebas

```
app/src/
├── test/                           # Pruebas Unitarias (JUnit)
│   └── java/com/example/vinylsapp/
│       ├── data/
│       │   ├── network/
│       │   │   └── AlbumApiServiceTest.kt    # Pruebas del API Service
│       │   └── repository/
│       │       └── AlbumRepositoryTest.kt    # Pruebas del Repository
│       └── ui/
│           └── albums/
│               └── AlbumViewModelTest.kt     # Pruebas del ViewModel
│
└── androidTest/                    # Pruebas de Instrumentación (Espresso + Compose)
    └── java/com/example/vinylsapp/
        ├── CustomTestRunner.kt               # Runner personalizado para Hilt
        ├── AlbumListE2ETest.kt               # Pruebas E2E de Álbumes
        ├── ArtistListE2ETest.kt              # Pruebas E2E de Artistas
        └── ui/
            ├── albums/
            │   └── AlbumListScreenTest.kt    # Pruebas UI de Álbumes
            └── artists/
                └── ArtistListScreenTest.kt   # Pruebas UI de Artistas
```

---

## 🎯 Ejecucion de pruebas

## 🚀 Cómo Ejecutar las Pruebas

### **Pruebas Unitarias** (Rápidas)

Desde Android Studio:
1. Haz clic derecho en la carpeta `test/`
2. Selecciona **Run 'Tests in app.test'**


**Resultado:** Se ejecutan en segundos sin necesidad de emulador.

---

### **Pruebas de Instrumentación/E2E** (Lentas)

**⚠️ Requisitos:**
- Emulador de Android ejecutándose
- Backend corriendo en `https://backvynils-8c16.onrender.com/` (para E2E tests)

Desde Android Studio:
1. Inicia el emulador
2. Haz clic derecho en la carpeta `androidTest/`
3. Selecciona **Run 'Tests in app.androidTest'**



**Resultado:** Se instala la app en el emulador y ejecuta las pruebas.

---

### **Ejecutar una prueba específica**

Desde Android Studio:
1. Abre el archivo de prueba
2. Haz clic en el ícono verde ▶️ junto al nombre de la clase o método
3. Selecciona **Run**

---

## 📊 Ver Resultados

### Desde Android Studio:
- Los resultados aparecen en la pestaña **Run** en la parte inferior
- ✅ Verde = Pasó
- ❌ Rojo = Falló

### Reportes HTML:
```bash
# Pruebas unitarias
open app/build/reports/tests/testDebugUnitTest/index.html

# Pruebas de instrumentación
open app/build/reports/androidTests/connected/index.html
```



## 🛠️ Tecnologías y Frameworks

### Pruebas Unitarias:
- **JUnit 4**: Framework de pruebas estándar
- **MockK**: Librería de mocking para Kotlin
- **Coroutines Test**: Testing de coroutines
- **Turbine**: Testing de Flows (opcional, añadido en dependencias)

### Pruebas de UI/E2E:
- **Espresso**: Framework de pruebas de UI de Android
- **Compose UI Testing**: Testing específico para Jetpack Compose
- **Hilt Testing**: Inyección de dependencias en pruebas
- **MockWebServer**: Simular respuestas del backend (opcional)



## 📚 Referencias

- [Testing en Android](https://developer.android.com/training/testing)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [MockK Documentation](https://mockk.io/)
- [Hilt Testing Guide](https://developer.android.com/training/dependency-injection/hilt-testing)
- [Espresso Basics](https://developer.android.com/training/testing/espresso)

---

## 🎯 Coverage (Cobertura)

Para generar un reporte de cobertura:

```bash
./gradlew testDebugUnitTestCoverage
```

Abre el reporte en:
```
app/build/reports/coverage/test/debug/index.html
```


**Última actualización:** 02 Noviembre 2025
