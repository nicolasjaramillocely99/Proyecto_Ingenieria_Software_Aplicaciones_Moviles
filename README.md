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

### 3. **Docker Desktop** (para el backend)
   - [Descargar Docker Desktop](https://www.docker.com/products/docker-desktop/)
   - O **Node.js** (v18 o superior) si prefieres ejecutar sin Docker

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

### **Paso 2: Verificar la Estructura del Proyecto**

Asegúrate de que tienes la siguiente estructura:

```
Proyecto_Ingenieria_Software_Aplicaciones_Moviles/
├── app/                    # Código de la aplicación Android
├── gradle/                 # Configuración de Gradle
├── build.gradle.kts        # Configuración del proyecto
├── settings.gradle.kts
└── README.md
```

Y en el directorio padre:

```
Proyecto_1/
├── Proyecto_Ingenieria_Software_Aplicaciones_Moviles/  # App Android
└── back/BackVynils/        # Backend (NestJS)
```

---

## � Configuración del Backend

La aplicación necesita el backend ejecutándose para funcionar. Sigue estos pasos:

### **Opción A: Usar Docker (Recomendado)**

#### **Paso 1: Navegar al directorio del backend**

```bash
cd ../back/BackVynils
```

#### **Paso 2: Iniciar el backend con Docker Compose**

```bash
# Construye e inicia los contenedores
docker-compose up -d

# Verifica que los contenedores estén corriendo
docker ps
```

Deberías ver dos contenedores:
- `backvynils-app` (Backend NestJS) - Puerto 3000
- `backvynils-postgres` (Base de datos PostgreSQL) - Puerto 5432

#### **Paso 3: Verificar que el backend está funcionando**

Abre tu navegador y ve a:
```
http://localhost:3000/albums
```

Deberías ver una respuesta JSON con la lista de álbumes.

#### **Paso 4: Detener el backend (cuando termines)**

```bash
docker-compose down
```

---


## 🔧 Configuración de Android Studio

### **Paso 1: Abrir el Proyecto en Android Studio**

1. Abre **Android Studio**
2. Selecciona **Open** (Abrir)
3. Navega a la carpeta `Proyecto_Ingenieria_Software_Aplicaciones_Moviles`
4. Haz clic en **OK**

### **Paso 2: Sincronizar Gradle**

Android Studio sincronizará automáticamente el proyecto con Gradle. Esto puede tomar algunos minutos la primera vez.

**Si aparece un error de sincronización:**
- Ve a `File > Invalidate Caches / Restart`
- Selecciona `Invalidate and Restart`

### **Paso 3: Verificar la Configuración de JDK**

1. Ve a `File > Project Structure > SDK Location`
2. Asegúrate de que **Gradle JDK** esté configurado en **JDK 17** o superior

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

### **Opción 1: Ejecutar en un Emulador**

#### **Paso 1: Crear un Emulador (si no tienes uno)**

1. En Android Studio, haz clic en **Device Manager** (ícono de teléfono)
2. Haz clic en **Create Device**
3. Selecciona un dispositivo (ej: Pixel 5)
4. Selecciona una imagen del sistema (recomendado: **API 33** o superior)
5. Haz clic en **Finish**

#### **Paso 2: Iniciar el Emulador**

1. En **Device Manager**, haz clic en ▶️ junto a tu emulador
2. Espera a que el emulador inicie completamente

#### **Paso 3: Ejecutar la Aplicación**

1. Haz clic en el botón **Run** (▶️) en la barra superior de Android Studio
2. Selecciona tu emulador de la lista
3. Haz clic en **OK**

La aplicación se compilará, instalará y ejecutará automáticamente.

**✅ Configuración por defecto:** La app está configurada para usar `http://10.0.2.2:3000/` que es la IP especial del emulador para acceder a `localhost` del host.

---



## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Arquitectura:** MVVM + Repository Pattern
- **Inyección de Dependencias:** Hilt (Dagger)
- **Networking:** Retrofit + OkHttp
- **Imágenes:** Coil
- **Asincronía:** Coroutines + Flow
- **Navegación:** Navigation Compose

---

## � Documentación Adicional

Para información detallada sobre la arquitectura y los patrones de diseño, consulta:

- **ARCHITECTURE.md** - Arquitectura de la aplicación

---


