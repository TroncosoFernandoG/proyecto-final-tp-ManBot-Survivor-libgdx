package com.manbotsurvivor.game.cliente;

// Todos los mensajes que se intercambian el cliente y el servidor por UDP.
// Esta clase es igual en el proyecto del cliente y en el del servidor (solo cambia el paquete).
public final class ProtocolosRed {

    public static final int PUERTO_SERVIDOR = 6413;

    // Un datagrama UDP puede llevar hasta 65507 bytes. Usamos el maximo porque
    // la lista de enemigos y chips puede superar los 1024 bytes.
    public static final int TAMAÑO_PAQUETE = 65507;

    public static final String SEPARADOR = "_";
    public static final String SEPARADOR_ELEMENTO = "/";
    public static final String SEPARADOR_DATO = ",";
    public static final String SEPARADOR_OPCION = ":";

    // Busqueda del servidor por broadcast (el cliente no necesita saber la IP)
    public static final String BUSCAR_SERVIDOR = "BuscarServidor";
    public static final String SERVIDOR_ENCONTRADO = "ServidorEncontrado";

    // Conexion
    public static final String CONEXION = "Conexion";
    public static final String OK = "OK";
    public static final String NUMERO_JUGADOR = "NumeroJugador";
    public static final String CUENTA_REGRESIVA = "CuentaRegresiva";
    public static final String EMPEZAR = "Empezar";
    public static final String ERROR_LIMITE_CLIENTES = "ERROR_limiteDeClientesAlcanzado";

    // Comprobacion de que el servidor sigue vivo
    public static final String ESTAS_AHI = "estasAhi";
    public static final String ESTOY_ACA = "estoyAca";

    // Cliente -> Servidor
    public static final String TECLAS = "Teclas";       // Teclas_arriba_abajo_izquierda_derecha
    public static final String MEJORA = "Mejora";       // Mejora_idSeleccion_opcion

    // Servidor -> Clientes (estado del juego)
    public static final String PARTIDA = "Partida";         // Partida_estado_tiempo_vida_vidaMax_nivel_xp_xpNecesaria_jefe
    public static final String JUGADOR = "Jugador";         // Jugador_numero_conectado_x_y_mirandoDerecha_moviendose_kills
    public static final String SELECCION = "Seleccion";     // Seleccion_numero_id_activa_op:op:op
    public static final String ENEMIGOS = "Enemigos";       // Enemigos_id,tipo,x,y/id,tipo,x,y
    public static final String PROYECTILES = "Proyectiles"; // Proyectiles_L,x,y,n/G,x,y/X,x,y,radio,ms
    public static final String CHIPS = "Chips";             // Chips_x,y/x,y

    // Desconexiones
    public static final String JUGADOR_DESCONECTADO = "JugadorDesconectado";
    public static final String SE_SALIO_DEL_JUEGO = "SeSalioDelJuego";
    public static final String SERVIDOR_CERRADO = "ServidorCerrado";

    // Fin de partida
    public static final String JUGAR_OTRA = "JUGAR_OTRA";
    public static final String VOLVER_MENU = "VOLVER_MENU";
    public static final String ESPERANDO_NUEVA_PARTIDA = "ESPERANDO_NUEVA_PARTIDA";
    public static final String NUEVA_PARTIDA = "NUEVA_PARTIDA";
    public static final String FIN_SESION = "FIN_SESION";

    // Tipos de enemigos y proyectiles
    public static final String TIPO_DRONE = "D";
    public static final String TIPO_TANQUE = "T";
    public static final String TIPO_NANOBOT = "N";
    public static final String TIPO_JEFE = "J";

    public static final String PROYECTIL_LASER = "L";
    public static final String PROYECTIL_GRANADA = "G";
    public static final String PROYECTIL_EXPLOSION = "X";

    private ProtocolosRed() {}
}
