package com.dani.mijuego.game.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Platform {

    public final Rectangle rect;

    public boolean moving;
    public float speed;
    public int dir;

    public boolean breakable;
    public boolean broken;
    public float brokenTime;

    public Platform(Rectangle rect, boolean moving, float speed, boolean breakable) {
        this.rect = rect;
        this.moving = moving;
        this.speed = speed;
        this.breakable = breakable;

        this.broken = false;
        this.brokenTime = 0f;
        this.dir = MathUtils.randomBoolean() ? 1 : -1;
    }
}
