# ManBot Survivor 🤖🎮

**ManBot Survivor** es un videojuego roguelite de supervivencia en 2D con vista cenital (top-down) desarrollado en Java 21 y LibGDX. El jugador controla un robot en una instalación industrial invadida por hordas mecánicas, donde debe sobrevivir recolectando chips de energía (XP), subiendo de nivel y seleccionando mejoras aleatorias. Cuenta además con un modo cooperativo multijugador en red local (LAN) sincronizado en tiempo real.

---

## 👥 Integrantes del Grupo
* **Sebastian Iuliano**
* **Thomas Nielsen**
* **Fernando Troncoso**

---

## 🛠️ Tecnologías Utilizadas
* **Lenguaje:** Java 21
* **Framework:** LibGDX (v1.14.2)
* **Plataforma Objetivo:** Escritorio (PC - Desktop LWJGL3)
* **Diseño Gráfico y Mapas:** Aseprite y Software Tiled
* **Red:** Arquitectura Cliente-Servidor mediante Sockets de Java en LAN (TCP/UDP)

---

## 📖 Propuesta Detallada del Proyecto
Toda la documentación conceptual, mecánicas de juego, personajes, armas, protocolos de red y detalles académicos se encuentran en la Wiki oficial del proyecto:

🔗 **[Ver la Propuesta Completa en la Wiki](https://github.com/TroncosoFernandoG/proyecto-final-tp-ManBot-Survivor-libgdx/wiki/Home)**

---

## 🚀 Instrucciones de Compilación y Ejecución

### Requisitos Previos
* Tener instalado **Java JDK 21**.
* Contar con un IDE compatible (**Eclipse IDE** o **IntelliJ IDEA**).

### Pasos para Clonar y Ejecutar

**Clonar el repositorio e ingresar a la carpeta del proyecto:**
   ```bash
   git clone [https://github.com/TroncosoFernandoG/proyecto-final-tp-ManBot-Survivor-libgdx.git](https://github.com/TroncosoFernandoG/proyecto-final-tp-ManBot-Survivor-libgdx.git)
   cd proyecto-final-tp-ManBot-Survivor-libgdx
 ```
### Ejecutar desde Eclipse IDE:

Abrir Eclipse y seleccionar File > Import....

Elegir Gradle > Existing Gradle Project y hacer clic en Next.

En Root Directory, seleccionar la carpeta raíz del proyecto clonado y hacer clic en Finish.

Una vez importado el proyecto, desplegar la carpeta del módulo lwjgl3.

Navegar hasta src/main/java/com/manbotsurvivor/game/lwjgl3/Lwjgl3Launcher.java.

Hacer clic derecho sobre Lwjgl3Launcher.java y seleccionar Run As > Java Application.

### Ejecutar desde IntelliJ IDEA:

Abrir IntelliJ IDEA.

Seleccionar File > Open... y elegir el archivo build.gradle en la raíz.

Navegar hasta el módulo lwjgl3 y ejecutar la clase principal Lwjgl3Launcher.java.

### Ejecutar en Windows mediante Terminal (Gradle Wrapper):

gradlew.bat lwjgl3:run

### Ejecutar en Linux / macOS mediante Terminal (Gradle Wrapper):

./gradlew lwjgl3:run

### 📈 Estado Actual del Proyecto
Fase: Pre-Entrega Corregida (Configuración del Repositorio, Entorno Java 21 y Documentación).

Video demostrativo del juego: https://drive.google.com/file/d/1Sw6xuegg1Jv8D2z4C_fNviF33AA4sXh3/view?usp=sharing
---
