package com.dani.mijuego.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.dani.mijuego.game.world.Platform;

//Ítem que protege al jugador de un impacto
public class Shield {

    public final Rectangle rect;

    public Platform platform;
    public float offsetX;
    public float offsetY;

    //Constructor del escudo
    public Shield(float x, float y, float w, float h) {
        this.rect = new Rectangle(x, y, w, h);
    }

    //Actualiza su posición respecto a la plataforma
    public void followPlatform() {
        if (platform == null) return;
        rect.x = platform.rect.x + offsetX;
        rect.y = platform.rect.y + platform.rect.height + offsetY;
    }
}
