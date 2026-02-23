package com.dani.mijuego.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.dani.mijuego.game.world.Platform;

//Ítem que aplica un efecto especial al jugador
public class Mushroom {

    public final Rectangle rect;

    public Platform platform;
    public float offsetX;
    public float offsetY;

    //Constructor de la seta
    public Mushroom(float x, float y, float w, float h) {
        this.rect = new Rectangle(x, y, w, h);
    }

    //Hace que la seta siga a la plataforma
    public void followPlatform() {
        if (platform == null) return;
        rect.x = platform.rect.x + offsetX;
        rect.y = platform.rect.y + platform.rect.height + offsetY;
    }
}
