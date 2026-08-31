**Todos los cambios significativos en este proyecto serán documentados en este archivo siguiendo el formato estándar de "Keep a Changelog".**

## [1.4.0] - 2026-08-31

### Agregado

* Integración del mapa creado con Tiled como entorno principal de juego.
* Implementación del renderizado del mapa mediante OrthogonalTiledMapRenderer.
* Implementación de una cámara que sigue al jugador durante el desplazamiento.
* Adaptación de los límites de movimiento del jugador al tamaño real del mapa.
* Limitación de la cámara para evitar que muestre zonas fuera de los límites del mapa.

### Modificado

* Actualización de PantallaJuego para integrar el mapa y la cámara.
* Actualización de Jugador para utilizar las dimensiones del mapa como límites de movimiento.

## [1.3.0] - 2026-08-30

### Agregado

* Incorporación del spritesheet de ManBot como recurso gráfico del personaje.
* Implementación de una animación mediante la clase Animation de LibGDX.
* División del spritesheet en cinco cuadros de animación de 64x64 píxeles.
* Incorporación de reproducción continua de los cuadros mediante el modo LOOP.
* Incorporación del control del tiempo de animación mediante delta.

### Modificado

* Reemplazo de la representación temporal del jugador mediante ShapeRenderer por el spritesheet de ManBot.
* Modificación del sistema de renderizado del jugador para utilizar SpriteBatch.
* Integración de la animación del personaje con la cámara del juego.
* Actualización de la liberación de recursos del jugador para liberar la textura utilizada por ManBot.

## [1.2.0] - 2026-08-30

### Agregado

* Implementación de la clase Jugador para representar al personaje dentro del juego.
* Implementación de la clase ControladorEntrada mediante InputAdapter de LibGDX.
* Incorporación del movimiento del personaje mediante las teclas W, A, S y D.
* Incorporación del movimiento diagonal mediante la combinación de teclas.
* Implementación del movimiento independiente de los FPS utilizando delta.
* Incorporación de una representación visual temporal del jugador mediante ShapeRenderer.
* Incorporación de límites de movimiento para impedir que el jugador salga del área virtual del juego.

### Modificado

* Conexión del jugador con la pantalla de juego y el sistema de entrada de LibGDX.
* Integración del jugador con la cámara del juego y el FitViewport.
* Incorporación de la liberación de los recursos utilizados por el jugador.


## [1.1.0] - 2026-08-30

### Agregado
* Implementación de la pantalla principal del juego.
* Incorporación de la navegación entre el menú y la pantalla de juego.
* Agregado del inicio de partida mediante la tecla ENTER.
* Agregado de la opción para salir del juego mediante la tecla ESC.
* Incorporación de un FitViewport para mantener la proporción de la interfaz al modificar el tamaño de la ventana.

### Modificado
* Organización de la pantalla de juego dentro del paquete pantallas.
* Renombrado de PlaySreen a PantallaJuego.
* Actualización de las referencias a la pantalla de juego en ManBotSurvivor.
# Registro de Cambios (Changelog) - ManBot Survivor

## [1.0.0] - 2026-07-15

### Agregado
* Creación y estructuración inicial del proyecto con LibGDX Liftoff (Java 21 y LibGDX 1.14.2).
* Configuración del archivo `.gitignore` optimizado para LibGDX, Gradle e IDEs.
* Inicialización del repositorio de control de versiones en GitHub y adición de colaboradores.
* Creación del archivo `README.md` con la presentación del proyecto y comandos de ejecución.
* Activación de la Wiki del proyecto configurando la propuesta principal en la página Home.
