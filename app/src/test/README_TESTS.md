# 🧪 Pruebas - Vinilos App

Esta carpeta contiene todas las pruebas para las funcionalidades de **Álbumes** y **Artistas** de la aplicación Vinilos.

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

## 🎯 Tipos de Pruebas

### 1. **Pruebas Unitarias** (`test/`)

Ejecutan rápidamente en la JVM local sin necesitar un emulador.

#### `AlbumRepositoryTest.kt`
**Objetivo:** Verificar la lógica de obtención de datos del API.

**Pruebas incluidas:**
- ✅ Emisión correcta de estados Loading → Success
- ✅ Manejo de respuestas exitosas con datos
- ✅ Manejo de códigos de error HTTP (404, 500)
- ✅ Manejo de excepciones de red
- ✅ Manejo de listas vacías
- ✅ Obtención de álbum por ID
- ✅ Manejo de errores en getAlbumById

**Tecnologías:**
- JUnit 4
- MockK (mocking)
- Kotlin Coroutines Test

#### `AlbumViewModelTest.kt`
**Objetivo:** Verificar la lógica de presentación y estados de UI.

**Pruebas incluidas:**
- ✅ Carga automática al inicializar
- ✅ Estado inicial de Loading
- ✅ Actualización a Success con datos correctos
- ✅ Actualización a Error en caso de fallo
- ✅ Detección de lista vacía (isEmpty)
- ✅ Funcionalidad de reintentar (retry)
- ✅ Limpieza de errores (clearError)
- ✅ Persistencia de datos tras errores

**Tecnologías:**
- JUnit 4
- MockK (mocking)
- Kotlin Coroutines Test
- InstantTaskExecutorRule (LiveData/StateFlow)

#### `AlbumApiServiceTest.kt`
**Objetivo:** Verificar la integración con el API REST del backend.

**Pruebas incluidas:**
- ✅ Respuesta exitosa del servidor (HTTP 200)
- ✅ Parseo correcto de múltiples álbumes
- ✅ Manejo de arrays vacíos
- ✅ Manejo de errores HTTP 404
- ✅ Manejo de errores HTTP 500
- ✅ Parseo de datos de performers
- ✅ Manejo de performers nulos
- ✅ Parseo de todos los campos JSON
- ✅ Verificación de método HTTP GET
- ✅ Verificación de endpoint correcto

**Tecnologías:**
- JUnit 4
- MockWebServer (simulación de backend)
- Retrofit

---

### 2. **Pruebas de UI con Compose** (`androidTest/`)

Requieren un emulador o dispositivo Android.

#### `AlbumListScreenTest.kt`
**Objetivo:** Verificar componentes individuales de Compose para Álbumes.

**Pruebas incluidas:**
- ✅ Renderizado correcto del grid de álbumes
- ✅ Visualización de nombres de artistas
- ✅ Visualización de recordLabel cuando no hay artistas
- ✅ Callbacks de click en tarjetas
- ✅ Indicador de carga (Loading)
- ✅ Mensaje de error con botón de reintentar
- ✅ Estado vacío (Empty)
- ✅ Layout de 2 columnas
- ✅ Scroll en el grid

**Tecnologías:**
- Compose UI Testing
- JUnit 4

#### `ArtistListScreenTest.kt`
**Objetivo:** Verificar componentes individuales de Compose para Artistas.

**Pruebas incluidas:**
- ✅ Renderizado correcto del grid de artistas
- ✅ Visualización de nombres de artistas
- ✅ Callbacks de click en tarjetas
- ✅ Indicador de carga (Loading)
- ✅ Mensaje de error con botón de reintentar
- ✅ Estado vacío (Empty)
- ✅ Grid es scrollable

**Tecnologías:**
- Compose UI Testing
- JUnit 4

---

### 3. **Pruebas End-to-End (E2E)** (`androidTest/`)

Verifican el flujo completo de la aplicación.

#### `AlbumListE2ETest.kt`
**Objetivo:** Probar el módulo de Álbumes con integración real.

**Pruebas incluidas:**
- ✅ Visualización de lista al iniciar la app
- ✅ Presencia del botón flotante (FAB)
- ✅ Manejo de errores de red
- ✅ Funcionalidad del botón de reintentar
- ✅ Carga de imágenes
- ✅ Layout correcto del grid

**Tecnologías:**
- Hilt Android Testing (inyección de dependencias)
- Compose UI Testing
- Espresso
- JUnit 4

#### `ArtistListE2ETest.kt`
**Objetivo:** Probar el módulo de Artistas con integración real.

**Pruebas incluidas:**
- ✅ Visualización del botón de Artistas al iniciar
- ✅ Carga de imágenes de artistas
- ✅ Manejo de errores de red
- ✅ Funcionalidad del botón de reintentar

**Tecnologías:**
- Hilt Android Testing (inyección de dependencias)
- Compose UI Testing
- Espresso
- JUnit 4

---

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
