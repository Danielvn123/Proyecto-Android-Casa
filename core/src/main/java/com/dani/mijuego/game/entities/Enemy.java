package com.dani.mijuego.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.world.Platform;
public class Enemy {

    public final Platform platform;
    public final float offX;
    public final float offY;
    public EnemyType type;
    public final Rectangle rect;

    public Enemy(Platform platform, float offX, float offY, EnemyType type) {
        this.platform = platform;
        this.offX = offX;
        this.offY = offY;
        this.type = type;
        this.rect = new Rectangle(0, 0, GameConfig.ENEMY_W, GameConfig.ENEMY_H);
        sync();
    }

    public void sync() {
        rect.x = platform.rect.x + offX;
        rect.y = platform.rect.y + offY;
    }
}
