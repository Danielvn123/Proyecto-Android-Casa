package com.dani.mijuego.game.entities;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.dani.mijuego.game.GameConfig;
public class Player {

    public final Rectangle rect;

    public float velY = 0f;
    public float knockbackX = 0f;

    private float tiltFiltered = 0f;

    private final Texture idle;
    private final Texture left;
    private final Texture right;

    private Texture current;

    public Player(float x, float y, Texture idle, Texture left, Texture right) {
        this.rect = new Rectangle(x, y, GameConfig.PLAYER_W, GameConfig.PLAYER_H);
        this.idle = idle;
        this.left = left;
        this.right = right;
        this.current = idle;
    }

    public Texture getTexture() {
        return current;
    }

    public void updateHorizontal(float dt) {
        float tiltRaw = -Gdx.input.getAccelerometerX();
        tiltFiltered = tiltFiltered + (tiltRaw - tiltFiltered) * GameConfig.SMOOTH;

        float tilt = deadZone(tiltFiltered, GameConfig.DEAD_ZONE);

        float kb = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) kb -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) kb += 1f;

        float control = (kb != 0f) ? kb : tilt;
        float vx = MathUtils.clamp(control * GameConfig.MOVE_SPEED, -GameConfig.MOVE_SPEED, GameConfig.MOVE_SPEED);

        knockbackX = MathUtils.lerp(knockbackX, 0f, 1f - (float) Math.exp(-GameConfig.KNOCKBACK_DAMP * dt));

        rect.x += (vx + knockbackX) * dt;

        if (vx > 40) current = right;
        else if (vx < -40) current = left;
        else current = idle;

        if (rect.x + rect.width < 0) rect.x = GameConfig.VW;
        if (rect.x > GameConfig.VW) rect.x = -rect.width;
    }

    private float deadZone(float v, float dz) {
        return (Math.abs(v) < dz) ? 0f : v;
    }
}
