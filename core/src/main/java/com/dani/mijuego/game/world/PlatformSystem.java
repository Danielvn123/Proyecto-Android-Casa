package com.dani.mijuego.game.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.dani.mijuego.game.GameConfig;

public class PlatformSystem {

    public final Array<Platform> platforms = new Array<>();

    public float nextY = 0f;

    public Platform makePlatform(float x, float y, boolean allowMoving) {
        boolean moving = allowMoving && MathUtils.randomBoolean(GameConfig.MOVING_CHANCE);
        float speed = moving ? MathUtils.random(GameConfig.MOVING_SPEED_MIN, GameConfig.MOVING_SPEED_MAX) : 0f;
        boolean breakable = MathUtils.randomBoolean(GameConfig.BREAKABLE_CHANCE);
        return new Platform(new Rectangle(x, y, GameConfig.PLATFORM_W, GameConfig.PLATFORM_H), moving, speed, breakable);
    }

    public void updateMoving(float dt) {
        for (Platform p : platforms) {
            if (!p.moving || p.broken) continue;

            p.rect.x += p.speed * p.dir * dt;

            if (p.rect.x <= 0f) {
                p.rect.x = 0f;
                p.dir = 1;
            } else if (p.rect.x + p.rect.width >= GameConfig.VW) {
                p.rect.x = GameConfig.VW - p.rect.width;
                p.dir = -1;
            }
        }
    }

    public void updateBreakables(float dt) {
        for (int i = platforms.size - 1; i >= 0; i--) {
            Platform p = platforms.get(i);
            if (!p.broken) continue;

            p.brokenTime += dt;
            if (p.brokenTime >= GameConfig.BROKEN_FADE_TIME) {
                platforms.removeIndex(i);
            }
        }
    }

    public void cullBelow(float killY) {
        for (int i = platforms.size - 1; i >= 0; i--) {
            Platform p = platforms.get(i);
            if (p.rect.y + p.rect.height < killY) platforms.removeIndex(i);
        }
    }
}
