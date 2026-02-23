package com.dani.mijuego.util;

import com.badlogic.gdx.math.Rectangle;

// Clase utilitaria para comprobar si un punto toca un rectángulo UI
public final class UiHit {

    // Constructor privado para evitar instanciación (solo métodos estáticos)
    private UiHit() {}

    public static boolean hit(Rectangle r, float x, float y) {

        // Comprueba que el rectángulo no sea null
        // y que el punto esté contenido dentro de él
        return r != null && r.contains(x, y);
    }
}
