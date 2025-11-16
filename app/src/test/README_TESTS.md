# 🧪 Pruebas - Vinilos App

Esta carpeta contiene todas las pruebas para las funcionalidades de **Álbumes** (listar, crear y detalle), **Artistas** (listar y detalle) y **Coleccionistas** (listar) de la aplicación Vinilos.

## 📁 Estructura de Pruebas

```
app/src/
├── test/                           # Pruebas Unitarias (JUnit)
│   └── java/com/example/vinylsapp/
│       ├── data/
│       │   ├── network/
│       │   │   ├── AlbumApiServiceTest.kt    # Pruebas del API Service de Álbumes
│       │   │   └── ArtistApiServiceTest.kt   # Pruebas del API Service de Artistas
│       │   └── repository/
│       │       ├── AlbumRepositoryTest.kt     # Pruebas del Repository de Álbumes
│       │       └── ArtistRepositoryTest.kt    # Pruebas del Repository de Artistas
│       └── ui/
│           ├── albums/
│           │   ├── AlbumViewModelTest.kt          # Pruebas del ViewModel (listar)
│           │   ├── AlbumDetailViewModelTest.kt    # Pruebas del ViewModel (detalle)
│           │   └── CreateAlbumViewModelTest.kt    # Pruebas del ViewModel (crear)
│           └── collectors/
│               └── CollectorViewModelTest.kt      # Pruebas del ViewModel de Coleccionistas
│
└── androidTest/                    # Pruebas de Instrumentación (Espresso + Compose)
    └── java/com/example/vinylsapp/
        ├── CustomTestRunner.kt               # Runner personalizado para Hilt
        ├── AlbumListE2ETest.kt               # Pruebas E2E de Álbumes (listar)
        ├── AlbumDetailE2ETest.kt            # Pruebas E2E de Álbumes (detalle)
        ├── CreateAlbumE2ETest.kt             # Pruebas E2E de Álbumes (crear)
        ├── ArtistListE2ETest.kt              # Pruebas E2E de Artistas (listar)
        ├── ArtistDetailE2ETest.kt             # Pruebas E2E de Artistas (detalle)
        ├── CollectorListE2ETest.kt           # Pruebas E2E de Coleccionistas
        └── ui/
            ├── albums/
            │   ├── AlbumListScreenTest.kt       # Pruebas UI de Álbumes (listar)
            │   ├── AlbumDetailScreenTest.kt     # Pruebas UI de Álbumes (detalle)
            │   └── CreateAlbumScreenTest.kt     # Pruebas UI de Álbumes (crear)
            └── artists/
                ├── ArtistListScreenTest.kt      # Pruebas UI de Artistas (listar)
                └── ArtistDetailScreenTest.kt    # Pruebas UI de Artistas (detalle)
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

#### `CreateAlbumViewModelTest.kt`
**Objetivo:** Verificar la lógica de creación de álbumes y validación de formulario.

**Pruebas incluidas:**
- ✅ Estado inicial vacío
- ✅ Actualización de campos individuales (nombre, portada, fecha, descripción, género, sello)
- ✅ Validación de formulario incompleto
- ✅ Validación de formulario completo
- ✅ Creación exitosa emite Loading → Success
- ✅ Manejo de errores en creación
- ✅ Limpieza de mensajes de error
- ✅ Reseteo completo del formulario

**Tecnologías:**
- JUnit 4
- MockK (mocking)
- Kotlin Coroutines Test
- InstantTaskExecutorRule (LiveData/StateFlow)

#### `AlbumDetailViewModelTest.kt`
**Objetivo:** Verificar la lógica de presentación y estados de UI para el detalle de álbum.

**Pruebas incluidas:**
- ✅ Carga automática al inicializar con albumId válido
- ✅ No carga si el albumId es inválido (0 o negativo)
- ✅ Estado inicial de Loading cuando se carga el álbum
- ✅ Actualización a Success con datos correctos del álbum
- ✅ Actualización a Error cuando hay un fallo
- ✅ Selección de tracks (selectTrack)
- ✅ Deselección de tracks al hacer click en el mismo track
- ✅ Cambio de selección a otro track diferente
- ✅ Reintento manual de carga (loadAlbumDetails)
- ✅ No hace nada si albumId es inválido al cargar manualmente
- ✅ Integridad de datos de tracks en el álbum

**Tecnologías:**
- JUnit 4
- MockK (mocking)
- Kotlin Coroutines Test
- InstantTaskExecutorRule (LiveData/StateFlow)

#### `CollectorViewModelTest.kt`
**Objetivo:** Verificar la lógica de presentación y estados de UI para la lista de coleccionistas.

**Pruebas incluidas:**
- ✅ Carga automática al inicializar (init triggers loadCollectors)
- ✅ Estado de Loading mientras el repositorio emite Loading
- ✅ Actualización con coleccionistas cuando hay éxito
- ✅ Actualización con error cuando el repositorio falla
- ✅ Marcado como vacío cuando el repositorio retorna lista vacía

**Tecnologías:**
- JUnit 4
- MockK (mocking)
- Kotlin Coroutines Test
- InstantTaskExecutorRule (LiveData/StateFlow)

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

#### `CreateAlbumScreenTest.kt`
**Objetivo:** Verificar componentes del formulario de creación de álbumes.

**Pruebas incluidas:**
- ✅ Visualización de todos los campos del formulario
- ✅ Campos de texto permiten entrada
- ✅ Dropdown de género muestra todas las opciones
- ✅ Dropdown de género permite selección
- ✅ Dropdown de sello discográfico muestra opciones
- ✅ Dropdown de sello permite selección
- ✅ Botón crear deshabilitado con formulario vacío
- ✅ Mensajes de error se muestran correctamente
- ✅ Botón de retroceso existe
- ✅ Título de pantalla correcto
- ✅ Campo de fecha muestra formato esperado

**Tecnologías:**
- Compose UI Testing
- JUnit 4

#### `AlbumDetailScreenTest.kt`
**Objetivo:** Verificar componentes individuales de Compose para el detalle de álbum.

**Pruebas incluidas:**
- ✅ Visualización del título del álbum
- ✅ Visualización del nombre del artista
- ✅ Visualización del año y género
- ✅ Visualización de la descripción del álbum
- ✅ Visualización del encabezado de canciones con botón "+"
- ✅ Funcionalidad del botón de agregar canción
- ✅ Visualización de todas las canciones en la lista
- ✅ Visualización de número de pista, título y duración de cada track
- ✅ Callback de click en items de tracks
- ✅ Renderizado correcto del estado de carga
- ✅ Visualización de la portada del álbum
- ✅ Visualización de recordLabel cuando no hay performers

**Tecnologías:**
- Compose UI Testing
- JUnit 4

#### `ArtistDetailScreenTest.kt`
**Objetivo:** Verificar componentes individuales de Compose para el detalle de artista.

**Pruebas incluidas:**
- ✅ Visualización del título del artista
- ✅ Visualización de la descripción del artista
- ✅ Visualización de todos los álbumes del artista
- ✅ Visualización del año de lanzamiento de los álbumes
- ✅ Visualización de la imagen del artista

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

#### `CreateAlbumE2ETest.kt`
**Objetivo:** Probar el flujo completo de creación de álbumes.

**Pruebas incluidas:**
- ✅ Pantalla muestra todos los campos
- ✅ Todos los campos pueden ser llenados
- ✅ Botón crear se habilita cuando formulario está completo
- ✅ Dropdowns muestran opciones correctas
- ✅ Botón de retroceso navega hacia atrás
- ✅ Campo fecha muestra hint de formato

**Tecnologías:**
- Hilt Android Testing (inyección de dependencias)
- Compose UI Testing
- Espresso
- JUnit 4

#### `AlbumDetailE2ETest.kt`
**Objetivo:** Probar el flujo completo de detalle de álbum con integración real.

**Pruebas incluidas:**
- ✅ Navegación a pantalla de detalle cuando se hace click en un álbum
- ✅ Visualización del título "Detalles del Álbum"
- ✅ Funcionalidad del botón de retroceso
- ✅ Visualización de información del álbum
- ✅ Visualización de la sección de canciones
- ✅ Verificación de que la pantalla es scrolleable

**Tecnologías:**
- Hilt Android Testing (inyección de dependencias)
- Compose UI Testing
- Espresso
- JUnit 4

#### `ArtistDetailE2ETest.kt`
**Objetivo:** Probar el flujo completo de detalle de artista con integración real.

**Pruebas incluidas:**
- ✅ Visualización del botón de Artistas al iniciar
- ✅ Navegación a pantalla de detalle cuando se hace click en un artista
- ✅ Visualización del título "Detalles del Artista"
- ✅ Funcionalidad del botón de retroceso
- ✅ Visualización de información del artista
- ✅ Visualización de la sección de álbumes

**Tecnologías:**
- Hilt Android Testing (inyección de dependencias)
- Compose UI Testing
- Espresso
- JUnit 4

#### `CollectorListE2ETest.kt`
**Objetivo:** Probar el flujo completo de listar coleccionistas con integración real.

**Pruebas incluidas:**
- ✅ Visualización de la sección de coleccionistas al navegar
- ✅ Visualización del botón flotante (FAB) de agregar coleccionista
- ✅ Manejo de errores de red cuando el backend no está disponible
- ✅ Funcionalidad del botón de reintentar
- ✅ Carga de imágenes en las tarjetas de coleccionistas

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


**Última actualización:** 16 Noviembre 2025

---

## 📋 Resumen de Cobertura de Pruebas

### Funcionalidades Cubiertas

| Funcionalidad | Unitarias | UI Tests | E2E Tests | Estado |
|---------------|-----------|----------|-----------|--------|
| **Álbumes - Listar** | ✅ | ✅ | ✅ | Completo |
| **Álbumes - Crear** | ✅ | ✅ | ✅ | Completo |
| **Álbumes - Detalle** | ✅ | ✅ | ✅ | Completo |
| **Artistas - Listar** | ✅ | ✅ | ✅ | Completo |
| **Artistas - Detalle** | ✅ | ✅ | ✅ | Parcial |
| **Coleccionistas - Listar** | ✅ | ❌ | ✅ | Parcial |

### Totales

- **Pruebas Unitarias:** 8 archivos
- **Pruebas UI:** 6 archivos
- **Pruebas E2E:** 6 archivos
- **Total de archivos de prueba:** 20 archivos
