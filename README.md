# ManBot Survivor 🤖🎮

**ManBot Survivor** es un videojuego roguelite de supervivencia en 2D con vista cenital (top-down) desarrollado en Java. El jugador controla un robot que se encuentra atrapado en una fábrica abandonada invadida por hordas de robots enemigos defectuosos, donde deberá sobrevivir el mayor tiempo posible recolectando chips de energía (XP), subiendo de nivel y eligiendo mejoras aleatorias. Además, cuenta con un modo cooperativo multijugador en red local (LAN) sincronizado en tiempo real mediante sockets TCP/UDP.

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

🔗 **[Ver la Propuesta Completa en la Wiki]([../../wiki](https://github.com/TroncosoFernandoG/proyecto-final-tp-ManBot-Survivor-libgdx/wiki))**

---

## 🚀 Instrucciones de Compilación y Ejecución

### Requisitos Previos
* Tener instalado **Java JDK 21**.
* Contar con un IDE compatible (se recomienda **IntelliJ IDEA**).

### Pasos para Clonal y Ejecutar

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/TroncosoFernandoG/proyecto-final-tp-ManBot-Survivor-libgd.git](https://github.com/TroncosoFernandoG/proyecto-final-tp-ManBot-Survivor-libgd.git)
   Ejecutar desde la terminal mediante Gradle Wrapper:

**En Windows:**

.\gradlew.bat lwjgl3:run

**En Linux / macOS:**

./gradlew lwjgl3:run

2. **Ejecutar desde el IDE (IntelliJ IDEA):**

* Abrir IntelliJ IDEA.
* Seleccionar File > Open... y elegir el archivo build.gradle en la raíz del proyecto.
* Una vez finalizada la importación, navegar hasta el módulo lwjgl3.
* Ejecutar la clase principal Lwjgl3Launcher.java.

## 📈 Estado Actual del Proyecto
**Fase:** Primera Pre-Entrega (Configuración Inicial del Proyecto y Repositorio).


---
