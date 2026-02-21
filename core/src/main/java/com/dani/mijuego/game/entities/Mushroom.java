package com.dani.mijuego.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.dani.mijuego.game.world.Platform;

public class Mushroom {

    public final Rectangle rect;

    // Para seguir la plataforma
    public Platform platform;
    public float offsetX;
    public float offsetY;

    public Mushroom(float x, float y, float w, float h) {
        this.rect = new Rectangle(x, y, w, h);
    }

    public void followPlatform() {
        if (platform == null) return;
        rect.x = platform.rect.x + offsetX;
        rect.y = platform.rect.y + platform.rect.height + offsetY;
    }
}
