package com.manbotsurvivor.game.elementos;

public enum Mejora {
	MEJORAR_LASER("Mejorar láser"),
    MEJORAR_GRANADA("Mejorar granada"),
    RECUPERAR_VIDA("Recuperar 30 puntos de vida"),
    AUMENTAR_VELOCIDAD("Aumentar velocidad");

    private final String descripcion;

    Mejora(String descripcion) {
        this.descripcion = descripcion;
    }

    public String obtenerDescripcion() {
        return descripcion;
    }
}
