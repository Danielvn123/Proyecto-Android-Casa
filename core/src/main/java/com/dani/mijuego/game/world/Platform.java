package com.dani.mijuego.game.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

// Clase que representa una plataforma del juego
public class Platform {

    // Rectángulo que define posición y tamaño
    public final Rectangle rect;

    // Indica si la plataforma se mueve horizontalmente
    public boolean moving;

    // Velocidad de movimiento horizontal
    public float speed;

    // Dirección del movimiento (1 derecha, -1 izquierda)
    public int dir;

    // Indica si la plataforma puede romperse al pisarla
    public boolean breakable;

    // Indica si ya está rota
    public boolean broken;

    // Tiempo acumulado desde que se rompió
    public float brokenTime;

    // Constructor de la plataforma
    public Platform(Rectangle rect, boolean moving, float speed, boolean breakable) {
        this.rect = rect;
        this.moving = moving;
        this.speed = speed;
        this.breakable = breakable;

        // Al crearla no está rota
        this.broken = false;

        // Tiempo de rotura empieza en 0
        this.brokenTime = 0f;

        // Dirección inicial aleatoria si es móvil
        this.dir = MathUtils.randomBoolean() ? 1 : -1;
    }
}
