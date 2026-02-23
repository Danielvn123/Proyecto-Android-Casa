package com.dani.mijuego.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.world.Platform;

//Representa un enemigo del juego
public class Enemy {

    // Plataforma donde está el enemigo
    public final Platform platform;

    // Desplazamiento respecto a la plataforma
    public final float offX;
    public final float offY;

    // Tipo de enemigo
    public EnemyType type;

    // Rectángulo de colisión
    public final Rectangle rect;

    //Constructor del enemigo
        public Enemy(Platform platform, float offX, float offY, EnemyType type) {
        this.platform = platform;
        this.offX = offX;
        this.offY = offY;
        this.type = type;

        this.rect = new Rectangle(0, 0, GameConfig.ENEMY_W, GameConfig.ENEMY_H);
        sync();
    }

    //Actualiza su posición siguiendo a la plataforma
    public void sync() {
        rect.x = platform.rect.x + offX;
        rect.y = platform.rect.y + offY;
    }
}
