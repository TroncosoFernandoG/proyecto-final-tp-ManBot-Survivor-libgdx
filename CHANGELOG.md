## [1.9.0] - 2026-09-04

### Agregado

* Incorporación de una nueva pantalla de opciones accesible desde el menú principal.
* Incorporación de la opción para regresar al menú principal desde la pantalla de opciones.
* Incorporación de indicaciones visuales para volver al menú desde la pantalla de opciones.
* Incorporación de la opción de pausa durante la partida mediante la tecla ESC.
* Incorporación de la opción para volver al menú principal desde la pantalla de pausa mediante la tecla M.

### Modificado

* Actualización del menú principal para permitir el acceso a la pantalla de opciones.
* Actualización del manejo de entradas para controlar la pausa, reanudación y regreso al menú principal.
* Actualización del manejo de la tecla M para evitar que una pulsación realizada durante la partida quede pendiente al entrar en pausa.
* Actualización de la navegación entre las diferentes pantallas y estados del juego.

## [1.8.1] - 2026-09-04

### Modificado
- Actualización del menú principal.
- Eliminación del título "MANBOT SURVIVOR".
- Reubicación de las opciones del menú para mejorar su distribución visual.
- Ajuste del tamaño de fuente de las opciones.
- Uso del fondo `fondo_menu.png` en el menú principal.

## [1.8.0] - 2026-09-04

### Agregado

* Incorporación de contador de enemigos eliminados en el HUD.
* Incorporación de registro de enemigos eliminados en Jugador.

### Modificado

* Actualización del tiempo de supervivencia para mostrarlo en formato MM:SS.
* Actualización en tiempo real de la vida del jugador en el HUD.
* Actualización en tiempo real de la experiencia del jugador en el HUD.
* Actualización en tiempo real del tiempo de supervivencia.
* Actualización en tiempo real del contador de enemigos eliminados.

## [1.7.2] - 2026-09-04

### Agregado

* Incorporación de hitboxes para los enemigos.
* Implementación de colisiones entre enemigos para evitar que se superpongan.
* Incorporación de un sistema de daño de los enemigos hacia ManBot.
* Incorporación de un tiempo de espera entre daños para evitar múltiples impactos por segundo.
* Incorporación del sistema de vida de ManBot, con una vida máxima de 100 puntos.

### Modificado

* Actualización de Enemigo para comprobar colisiones con otros enemigos durante su movimiento.
* Actualización de Enemigo para detectar el contacto con ManBot y aplicar daño.
* Actualización de Jugador para administrar su vida actual y vida máxima.
* Actualización del movimiento de los enemigos para mantener separadas sus hitboxes.

## [1.7.1] - 2026-09-04

### Agregado

* Incorporación de spritesheet animado para los enemigos.
* Incorporación de una representación visual para los disparos.
* Implementación de detección de colisión entre el disparo y el enemigo.
* Implementación de colisión entre los enemigos y ManBot.

### Modificado

* Actualización de Enemigo para utilizar animaciones mediante spritesheet.
* Actualización del movimiento de los enemigos para evitar atravesar al jugador.
* Actualización del sistema de disparo para comprobar colisiones con los enemigos.

## [1.7.0] - 2026-09-04

### Agregado

* Implementación del ataque automático de ManBot hacia el enemigo más cercano.
* Incorporación de un intervalo de 1,5 segundos entre ataques.
* Implementación del sistema de daño y eliminación de enemigos.
* Incorporación de los chips de energía al derrotar enemigos.
* Implementación de la recolección de chips mediante colisión con ManBot.
* Incorporación del sistema de experiencia obtenida al recoger chips.
* Implementación de 10 puntos de experiencia por cada chip recogido.
* Representación temporal de los chips de energía mediante ShapeRenderer.

### Modificado

* Actualización de PantallaJuego para administrar los chips de energía.
* Incorporación de métodos en Jugador para obtener su ancho y alto.
* Actualización del sistema de eliminación de enemigos para generar un chip de energía al derrotarlos.
* Actualización del sistema de renderizado para mostrar los chips de energía.
* Actualización del sistema de interacción para eliminar los chips una vez recogidos.

## [1.6.2] - 2026-09-04

### Agregado

* Incorporación de un ArrayList para gestionar múltiples enemigos.
* Implementación de la detección del enemigo más cercano a ManBot.
* Implementación del cálculo de distancia entre ManBot y los enemigos.

### Modificado

* Actualización de PantallaJuego para recorrer y actualizar múltiples enemigos.
* Actualización de PantallaJuego para dibujar múltiples enemigos.

## [1.6.1] - 2026-09-02

### Agregado

* Integración del enemigo dentro de PantallaJuego.
* Actualización del enemigo en cada ciclo del juego.
* Incorporación del dibujo temporal del enemigo mediante ShapeRenderer.
* Implementación de colisiones entre el enemigo y los obstáculos del mapa.

### Modificado

* Actualización de Enemigo para recibir una referencia al mapa.
* Modificación del movimiento del enemigo para comprobar colisiones antes de cambiar su posición.
* Actualización de PantallaJuego para crear, actualizar y dibujar al enemigo.
* Incorporación de la liberación de los recursos utilizados para dibujar al enemigo.

## [1.6.0] - 2026-08-31

### Añadido
- Implementación de la clase `Enemigo`.
- Incorporación de los datos básicos necesarios para representar enemigos.
- Preparación de la estructura del enemigo para su integración con el jugador y el mapa.

### Modificado
- Actualización de `Jugador.java` para preparar la interacción con los enemigos.

## [1.5.0] - 2026-08-31

### Agregado

* Implementación de la carga y renderizado del mapa mediante Tiled.
* Incorporación de la clase Mapa para gestionar el mapa y sus elementos.
* Implementación de la capa de obstáculos para detectar colisiones.
* Incorporación de colisiones entre el jugador y los obstáculos del mapa.
* Implementación del seguimiento del jugador mediante la cámara.
* Limitación de la cámara para evitar mostrar zonas fuera de los límites del mapa.

### Modificado

* Actualización de PantallaJuego para cargar y renderizar el mapa.
* Actualización de Jugador para utilizar el mapa en la detección de colisiones.
* Ajuste de los límites de movimiento del jugador según las dimensiones del mapa.

## [1.4.1] - 2026-08-31

### Modificado

* Corrección de la configuración del mapa en Tiled.
* Ajuste del tamaño del mapa a 30 tiles de ancho por 20 tiles de alto.
* Ajuste del tamaño de los tiles para mantener una escala adecuada con el entorno del juego.

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
