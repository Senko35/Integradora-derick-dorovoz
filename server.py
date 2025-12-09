from fastapi import FastAPI, HTTPException, status
from pydantic import BaseModel
from typing import List, Optional
import uvicorn

app = FastAPI()

# --- Simulación de Base de Datos en Memoria ---
# En producción, esto debería ser una base de datos real (SQLite, PostgreSQL, etc.)
users_db = []
audios_db = []

# --- Modelos (Coinciden con tus Data Classes de Kotlin) ---

class UserRegister(BaseModel):
    name: str
    email: str
    password: str

class UserLogin(BaseModel):
    email: str
    password: str

class AudioItem(BaseModel):
    fileName: str
    filePath: str
    date: int
    userEmail: str

# --- Endpoints ---

@app.get("/")
def read_root():
    print("GET / solicitado")
    return {"message": "Bienvenido a la API de DiarioVoz"}

# 1. Registro de Usuario
@app.post("/register", status_code=status.HTTP_201_CREATED)
def register(user: UserRegister):
    print(f"--> REGISTER: Intento de registro para: {user.email}")
    # Verificar si el usuario ya existe
    for u in users_db:
        if u['email'] == user.email:
            print(f"Error: El usuario {user.email} ya existe.")
            raise HTTPException(status_code=400, detail="El correo ya está registrado")
    
    users_db.append(user.dict())
    print(f"Exito: Usuario registrado.")
    print(f"DB Usuarios actual: {users_db}")
    return {"message": "Usuario registrado exitosamente", "user": user}

# 2. Login de Usuario
@app.post("/login")
def login(credentials: UserLogin):
    print(f"--> LOGIN: Intento de login para: {credentials.email}")
    for u in users_db:
        if u['email'] == credentials.email and u['password'] == credentials.password:
            # En una app real, aquí devolverías un Token (JWT)
            print(f"Login exitoso para {credentials.email}")
            return {"message": "Login exitoso", "name": u['name'], "email": u['email']}
    
    print(f"Error login: Credenciales incorrectas para {credentials.email}")
    raise HTTPException(status_code=401, detail="Credenciales incorrectas")

# 3. Guardar información de un Audio
@app.post("/audios", status_code=status.HTTP_201_CREATED)
def save_audio(audio: AudioItem):
    print(f"--> SAVE AUDIO: Recibido audio {audio.fileName}")
    audios_db.append(audio.dict())
    print(f"Audio guardado.")
    print(f"DB Audios actual: {audios_db}")
    return {"message": "Audio guardado correctamente"}

# 4. Obtener lista de audios por usuario
@app.get("/audios/{user_email}", response_model=List[AudioItem])
def get_audios(user_email: str):
    print(f"--> GET AUDIOS: Consultando para {user_email}")
    user_audios = [a for a in audios_db if a['userEmail'] == user_email]
    print(f"Se encontraron {len(user_audios)} audios")
    return user_audios

# 5. Eliminar un audio (NUEVO)
@app.delete("/audios")
def delete_audio(fileName: str, userEmail: str):
    global audios_db
    print(f"--> DELETE AUDIO: Solicitud para eliminar {fileName} de {userEmail}")
    initial_count = len(audios_db)
    # Filtramos para mantener solo los que NO coincidan con el archivo y usuario a borrar
    audios_db = [a for a in audios_db if not (a['fileName'] == fileName and a['userEmail'] == userEmail)]
    
    if len(audios_db) < initial_count:
        print(f"Audio eliminado exitosamente.")
        print(f"DB Audios actual: {audios_db}")
        return {"message": "Audio eliminado correctamente"}
    else:
        print(f"Error: Audio no encontrado para eliminar.")
        raise HTTPException(status_code=404, detail="Audio no encontrado")

if __name__ == "__main__":
    # Ejecuta el servidor en localhost:8000
    # Para acceder desde el emulador de Android, usa la IP 10.0.2.2 en lugar de localhost
    print("--- SERVIDOR DIARIOVOZ ACTUALIZADO ---")
    print("Iniciando en puerto 8000...")
    # Nota: Si modificas el código, debes detener y volver a ejecutar el script para ver los cambios
    uvicorn.run(app, host="0.0.0.0", port=8000)
