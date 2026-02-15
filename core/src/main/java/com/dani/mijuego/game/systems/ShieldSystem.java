package com.dani.mijuego.game.systems;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.dani.mijuego.game.entities.Coin;
import com.dani.mijuego.game.entities.Enemy;
import com.dani.mijuego.game.entities.JumpBoots;
import com.dani.mijuego.game.entities.Player;
import com.dani.mijuego.game.entities.Shield;
import com.dani.mijuego.game.world.Platform;

public class ShieldSystem {

    public interface OnShieldCollected {
        void onShieldCollected();
    }

    public final Array<Shield> shields = new Array<>();

    public static final float SHIELD_W = 90f;
    public static final float SHIELD_H = 90f;

    private static final int MAX_ON_SCREEN = 1;

    public void reset() {
        shields.clear();
    }

    public void trySpawnOnPlatformIfFree(
        Platform p,
        CoinSystem coinSystem,
        EnemySystem enemySystem,
        JumpBootsSystem bootsSystem,
        float chance,
        boolean force
    ) {
        if (p == null) return;
        if (shields.size >= MAX_ON_SCREEN) return;

        if (!force && MathUtils.random() > chance) return;

        float x = p.rect.x + (p.rect.width - SHIELD_W) / 2f;
        float y = p.rect.y + p.rect.height + 35f;

        Rectangle spawnRect = new Rectangle(x, y, SHIELD_W, SHIELD_H);

        if (overlapsCoin(spawnRect, coinSystem)) return;
        if (overlapsEnemy(spawnRect, enemySystem)) return;
        if (overlapsBoots(spawnRect, bootsSystem)) return;

        Shield s = new Shield(x, y, SHIELD_W, SHIELD_H);

        // ANCLAR A LA PLATAFORMA
        s.platform = p;
        s.offsetX = x - p.rect.x;
        s.offsetY = 35f;

        shields.add(s);
    }

    public void update(Player player, float killY, OnShieldCollected cb) {
        for (int i = shields.size - 1; i >= 0; i--) {
            Shield s = shields.get(i);

            if (s.platform != null) {
                s.rect.x = s.platform.rect.x + s.offsetX;
                s.rect.y = s.platform.rect.y + s.platform.rect.height + s.offsetY;
            }

            if (s.rect.y + s.rect.height < killY) {
                shields.removeIndex(i);
                continue;
            }

            if (player != null && s.rect.overlaps(player.rect)) {
                shields.removeIndex(i);
                if (cb != null) cb.onShieldCollected();
            }
        }
    }

    private boolean overlapsCoin(Rectangle r, CoinSystem coinSystem) {
        if (coinSystem == null || coinSystem.coins == null) return false;
        for (int i = 0; i < coinSystem.coins.size; i++) {
            Coin c = coinSystem.coins.get(i);
            if (c != null && c.rect != null && c.rect.overlaps(r)) return true;
        }
        return false;
    }

    private boolean overlapsEnemy(Rectangle r, EnemySystem enemySystem) {
        if (enemySystem == null || enemySystem.enemies == null) return false;
        for (int i = 0; i < enemySystem.enemies.size; i++) {
            Enemy e = enemySystem.enemies.get(i);
            if (e != null && e.rect != null && e.rect.overlaps(r)) return true;
        }
        return false;
    }

    private boolean overlapsBoots(Rectangle r, JumpBootsSystem bootsSystem) {
        if (bootsSystem == null || bootsSystem.boots == null) return false;
        for (int i = 0; i < bootsSystem.boots.size; i++) {
            JumpBoots b = bootsSystem.boots.get(i);
            if (b != null && b.rect != null && b.rect.overlaps(r)) return true;
        }
        return false;
    }
}
