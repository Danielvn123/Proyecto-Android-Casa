package com.dani.mijuego.util;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// Clase utilitaria para dibujar texto con borde (outline)
public final class FontUtils {

    // Constructor privado para evitar que se instancie (solo métodos estáticos)
    private FontUtils() {}

    public static void drawOutlined(SpriteBatch batch, BitmapFont outline, BitmapFont fill,
                                    String text, float x, float y, float outlinePx) {

        // Dibuja el texto desplazado en 4 direcciones principales
        outline.draw(batch, text, x - outlinePx, y); // izquierda
        outline.draw(batch, text, x + outlinePx, y); // derecha
        outline.draw(batch, text, x, y - outlinePx); // abajo
        outline.draw(batch, text, x, y + outlinePx); // arriba

        // Dibuja también en las 4 diagonales para un borde más uniforme
        outline.draw(batch, text, x - outlinePx, y - outlinePx); // abajo-izquierda
        outline.draw(batch, text, x + outlinePx, y - outlinePx); // abajo-derecha
        outline.draw(batch, text, x - outlinePx, y + outlinePx); // arriba-izquierda
        outline.draw(batch, text, x + outlinePx, y + outlinePx); // arriba-derecha

        // Finalmente dibuja el texto principal encima (relleno)
        fill.draw(batch, text, x, y);
    }
}
