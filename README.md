# 🎙️ DiarioVoz  
Aplicación Android desarrollada en Kotlin que permite a los usuarios **grabar audio**, **reproducirlo**, **gestionar un diario auditivo**, todo acompañado de un sistema de **registro e inicio de sesión**.

---

## 📱 Características principales

### 🎤 Grabación de audio
- Graba notas de voz desde la app.
- Guarda archivos de audio en almacenamiento interno.
- Control mediante `AudioRecordViewModel`.

### ▶️ Reproducción de audio
- Lista audios guardados.
- Reproducir audio desde la pantalla de lista.
- Implementado con `PlayAudioWorker` y `AudioListViewModel`.

### 🔐 Autenticación de usuarios
- Registro de usuarios (email/contraseña).
- Inicio de sesión con validaciones.
- Manejado mediante `LoginViewModel` y `RegisterViewModel`.

### 🎨 Interfaz moderna con Jetpack Compose
- Tipografías, colores y estilos definidos en `/theme`.
- Componentes personalizados como `DoroButton`.

---

## 🧩 Arquitectura del Proyecto

El proyecto sigue una arquitectura basada en capas:
app/src/main/java/com/integradora/diariovoz/

app/
└── src/
    └── main/
        └── java/
            └── com/
                └── integradora/
                    └── diariovoz/
                        ├── data/
                        │   ├── api/          # Acceso remoto (si aplica)
                        │   ├── database/     # Persistencia local
                        │   └── model/        # Modelos de datos
                        │
                        ├── repository/       # Repositorios que conectan datos y UI
                        │
                        ├── ui/               # Pantallas, navegación y componentes visuales
                        │
                        ├── viewmodel/        # Lógica de presentación (MVVM)
                        │   ├── AudioListViewModel.kt
                        │   ├── AudioRecordViewModel.kt
                        │   ├── AudioSchedulerViewModel.kt
                        │   ├── LoginViewModel.kt
                        │   └── RegisterViewModel.kt
                        │
                        ├── theme/            # Colores, tipografías y estilos globales
                        │
                        └── MainActivity.kt   # Entry point de la app


---

## 🛠️ Tecnologías utilizadas

- **Kotlin**
- **Android Jetpack Compose**
- **MVVM (Model–View–ViewModel)**
- **WorkManager** – para tareas programadas
- **MediaRecorder / MediaPlayer**
- **Room (si se usa en el proyecto)**
- **Gradle**

---

## 📦 Instalación y ejecución

### 1️⃣ Requisitos
- Android Studio Flamingo o superior  
- JDK 17  
- Gradle 8+ (incluido en el repo)

### 2️⃣ Clonar el repositorio

```bash
git clone https://github.com/usuario/Integradora-derick-dorovoz.git

3️⃣ Abrir en Android Studio

Abrir Android Studio

Seleccionar File → Open

Elegir la carpeta del proyecto

Esperar a que Gradle sincronice

4️⃣ Ejecutar la app

Conectar un dispositivo físico o crear un emulador Android.

Presionar Run ▶️.

📁 Recursos e imágenes

Las imágenes y recursos están dentro de:

app/src/main/res/drawable/


Incluye el ícono personalizado doro.jpg y recursos de launcher.

👤 Autor

Alex Axel Rodrigues Morales
Yahir Fuentes Martinez 
Trejo Rojas Mario Alberto
4°A DSM
Proyecto integrador — DiarioVoz
