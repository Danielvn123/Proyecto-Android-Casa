package com.dani.mijuego.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.dani.mijuego.game.world.Platform;

public class Mushroom {

    public final Rectangle rect;
    public final Platform platform;

    public final float offsetX;
    public final float offsetY;

    public Mushroom(Platform platform, float x, float y, float w, float h) {
        this.platform = platform;
        this.rect = new Rectangle(x, y, w, h);

        this.offsetX = x - platform.rect.x;
        this.offsetY = y - platform.rect.y;
    }

    public void followPlatform() {
        if (platform == null) return;
        rect.x = platform.rect.x + offsetX;
        rect.y = platform.rect.y + offsetY;
    }
}
