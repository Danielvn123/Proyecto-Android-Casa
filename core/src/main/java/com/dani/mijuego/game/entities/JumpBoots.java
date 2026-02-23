package com.dani.mijuego.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.dani.mijuego.game.world.Platform;

//Ítem que mejora el salto del jugador
public class JumpBoots {

    // Rectángulo de colisión
    public final Rectangle rect;

    // Plataforma asociada
    public Platform platform;

    public float offsetX;
    public float offsetY;

    //Constructor del ítem
    public JumpBoots(float x, float y, float w, float h) {
        this.rect = new Rectangle(x, y, w, h);
    }

    //Hace que el ítem siga a la plataforma
    public void followPlatform() {
        if (platform == null) return;
        rect.x = platform.rect.x + offsetX;
        rect.y = platform.rect.y + platform.rect.height + offsetY;
    }
}
