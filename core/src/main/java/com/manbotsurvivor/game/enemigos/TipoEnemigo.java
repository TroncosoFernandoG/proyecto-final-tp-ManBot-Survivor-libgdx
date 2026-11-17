package com.manbotsurvivor.game.enemigos;

import com.manbotsurvivor.game.cliente.ProtocolosRed;


//segun el tipo que manda el servidor, crea el enemigo que corresponde para dibujarlo
public enum TipoEnemigo {

    DRONE(ProtocolosRed.TIPO_DRONE) {
        @Override
        public Enemigo crearEnemigo(float x, float y) {
            return new DroneBasico(x, y);
        }
    },
    TANQUE(ProtocolosRed.TIPO_TANQUE) {
        @Override
        public Enemigo crearEnemigo(float x, float y) {
            return new RobotTanque(x, y);
        }
    },
    NANOBOT(ProtocolosRed.TIPO_NANOBOT) {
        @Override
        public Enemigo crearEnemigo(float x, float y) {
            return new NanoBot(x, y);
        }
    },
    JEFE(ProtocolosRed.TIPO_JEFE) {
        @Override
        public Enemigo crearEnemigo(float x, float y) {
            return new JefeMecanico(x, y);
        }
    };

    private final String codigo;

    TipoEnemigo(String codigo) {
        this.codigo = codigo;
    }

    public abstract Enemigo crearEnemigo(float x, float y);

    public static TipoEnemigo desdeCodigo(String codigo) {
        for (TipoEnemigo tipo : values()) {
            if (tipo.codigo.equals(codigo)) {
                return tipo;
            }
        }
        return DRONE;
    }

    public static void disponerTexturas() {
        DroneBasico.disponerTextura();
        RobotTanque.disponerTextura();
        NanoBot.disponerTextura();
        JefeMecanico.disponerTextura();
    }
}
