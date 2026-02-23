package com.dani.mijuego.game.entities;

import com.badlogic.gdx.math.Rectangle;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.world.Platform;

//Representa una moneda que el jugador puede recoger
public class Coin {

    // Plataforma a la que pertenece la moneda
    public final Platform platform;

    // Desplazamiento respecto a la plataforma
    public final float offX;
    public final float offY;

    // Rectángulo de colisión
    public final Rectangle rect;

    //Constructor de la moneda
    public Coin(Platform platform, float offX, float offY) {
        this.platform = platform;
        this.offX = offX;
        this.offY = offY;

        this.rect = new Rectangle(0, 0, GameConfig.COIN_W, GameConfig.COIN_H);
        sync();
    }

    //Actualiza la posición de la moneda según la plataforma
    public void sync() {
        rect.x = platform.rect.x + offX;
        rect.y = platform.rect.y + offY;
    }
}
