package com.dani.mijuego.game.world;

import com.dani.mijuego.game.GameConfig;

// Clase que gestiona la capa decorativa de ruinas del fondo
public class RuinsLayer {

    // Indica si la capa ya está desactivada (no se dibuja más)
    public boolean disabled = false;

    // Altura base donde se colocan inicialmente las ruinas
    public float baseY = 0f;

    // Transparencia actual (1 = visible, 0 = invisible)
    public float alpha = 1f;

    // Indica si está en proceso de desaparecer
    public boolean fading = false;

    // Reinicia el estado de la capa
    public void reset(float baseY) {
        this.disabled = false;
        this.baseY = baseY;
        this.alpha = 1f;
        this.fading = false;
    }

    // Actualiza la lógica de desvanecimiento según la posición de cámara
    public void update(float dt, float bottomVisible) {

        // Si ya está desactivada, no hace nada
        if (disabled) return;

        // Si aún no está en proceso de fade
        if (!fading) {

            // Cuando la cámara ha subido lo suficiente, empieza a desvanecerse
            if (bottomVisible > baseY + GameConfig.RUINAS_H * 0.65f) {
                fading = true;
            }

        } else {

            // Reduce la transparencia progresivamente
            alpha -= GameConfig.RUINAS_FADE_SPEED * dt;

            // Cuando llega a 0, se desactiva completamente
            if (alpha <= 0f) {
                alpha = 0f;
                disabled = true;
            }
        }
    }

    // Calcula la posición Y donde se deben dibujar las ruinas
    public float computeDrawY(float bottomVisible) {

        // Base inferior visible (posición de la cámara)
        float groundY = bottomVisible;

        // Cuánto ha subido la cámara respecto al punto inicial
        float deltaUp = Math.max(0f, bottomVisible - baseY);

        // Aplica efecto parallax (se mueve más lento que el mundo)
        float parallaxOffset = -deltaUp * (1f - GameConfig.RUINAS_PARALLAX);

        return groundY + parallaxOffset;
    }
}
