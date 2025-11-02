# 🧪 Pruebas - Vinilos App

Esta carpeta contiene todas las pruebas para la funcionalidad de **listar álbumes** de la aplicación Vinilos.

## 📁 Estructura de Pruebas

```
app/src/
├── test/                           # Pruebas Unitarias (JUnit)
│   └── java/com/example/vinylsapp/
│       ├── data/
│       │   └── repository/
│       │       └── AlbumRepositoryTest.kt    # Pruebas del Repository
│       └── ui/
│           └── albums/
│               └── AlbumViewModelTest.kt     # Pruebas del ViewModel
│
└── androidTest/                    # Pruebas de Instrumentación (Espresso + Compose)
    └── java/com/example/vinylsapp/
        ├── CustomTestRunner.kt               # Runner personalizado para Hilt
        ├── AlbumListE2ETest.kt               # Pruebas End-to-End completas
        └── ui/
            └── albums/
                └── AlbumListScreenTest.kt    # Pruebas de UI de Compose
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

---

### 2. **Pruebas de UI con Compose** (`androidTest/`)

Requieren un emulador o dispositivo Android.

#### `AlbumListScreenTest.kt`
**Objetivo:** Verificar componentes individuales de Compose.

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

---

### 3. **Pruebas End-to-End (E2E)** (`androidTest/`)

Verifican el flujo completo de la aplicación.

#### `AlbumListE2ETest.kt`
**Objetivo:** Probar la app completa con integración real.

**Pruebas incluidas:**
- ✅ Visualización de lista al iniciar la app
- ✅ Navegación al hacer click en un álbum
- ✅ Presencia del botón flotante (FAB)
- ✅ Funcionalidad de scroll
- ✅ Manejo de errores de red
- ✅ Funcionalidad del botón de reintentar
- ✅ Carga de imágenes
- ✅ Layout correcto del grid

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

Desde terminal:
```bash
./gradlew test
```

**Resultado:** Se ejecutan en segundos sin necesidad de emulador.

---

### **Pruebas de Instrumentación/E2E** (Lentas)

**⚠️ Requisitos:**
- Emulador de Android ejecutándose
- Backend corriendo en `http://localhost:3000` (para E2E tests)

Desde Android Studio:
1. Inicia el emulador
2. Haz clic derecho en la carpeta `androidTest/`
3. Selecciona **Run 'Tests in app.androidTest'**

Desde terminal:
```bash
# Asegúrate de que el emulador esté corriendo
./gradlew connectedAndroidTest
```

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

---

## 🔧 Configuración Especial

### CustomTestRunner
Se usa un runner personalizado para integrar **Hilt** en las pruebas:

```kotlin
// app/build.gradle.kts
defaultConfig {
    testInstrumentationRunner = "com.example.vinylsapp.CustomTestRunner"
}
```

Esto permite usar `@HiltAndroidTest` en las pruebas E2E.

---

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

---

## 📝 Mejores Prácticas

### ✅ DO (Hacer):
- Ejecutar pruebas unitarias antes de hacer commit
- Mantener las pruebas simples y enfocadas
- Usar nombres descriptivos para los tests
- Probar casos edge (listas vacías, errores, etc.)
- Usar Given-When-Then en los comentarios

### ❌ DON'T (No hacer):
- No hacer las pruebas dependientes entre sí
- No usar datos reales de producción en tests
- No ignorar pruebas fallidas (`@Ignore`)
- No hacer pruebas demasiado largas

---

## 🐛 Troubleshooting

### Error: "Cannot resolve symbol 'mockk'"
**Solución:** Sincroniza Gradle (`File > Sync Project with Gradle Files`)

### Error: "No tests were found"
**Solución:** 
1. Verifica que el archivo termine en `Test.kt`
2. Asegúrate de que los métodos tengan `@Test`
3. Invalida cachés: `File > Invalidate Caches / Restart`

### E2E Tests fallan con "Connection refused"
**Solución:** 
1. Verifica que el backend esté corriendo
2. Usa `http://10.0.2.2:3000` en el emulador (no `localhost`)

### Error: "HiltTestApplication not found"
**Solución:** Verifica que `testInstrumentationRunner` sea `CustomTestRunner`

---

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

---

## ✨ Contribuir

Al agregar nuevas funcionalidades:
1. Escribe primero las pruebas (TDD)
2. Asegúrate de que todas las pruebas pasen
3. Mantén un mínimo de 80% de cobertura
4. Documenta casos edge específicos

---

**Última actualización:** Noviembre 2025
