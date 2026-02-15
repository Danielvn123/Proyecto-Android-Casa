package com.dani.mijuego.game.world;

import com.dani.mijuego.game.GameConfig;

public class RuinsLayer {

    public boolean disabled = false;
    public float baseY = 0f;

    public float alpha = 1f;
    public boolean fading = false;

    public void reset(float baseY) {
        this.disabled = false;
        this.baseY = baseY;
        this.alpha = 1f;
        this.fading = false;
    }

    public void update(float dt, float bottomVisible) {
        if (disabled) return;

        if (!fading) {
            if (bottomVisible > baseY + GameConfig.RUINAS_H * 0.65f) {
                fading = true;
            }
        } else {
            alpha -= GameConfig.RUINAS_FADE_SPEED * dt;
            if (alpha <= 0f) {
                alpha = 0f;
                disabled = true;
            }
        }
    }

    public float computeDrawY(float bottomVisible) {
        float groundY = bottomVisible;
        float deltaUp = Math.max(0f, bottomVisible - baseY);
        float parallaxOffset = -deltaUp * (1f - GameConfig.RUINAS_PARALLAX);
        return groundY + parallaxOffset;
    }
}
