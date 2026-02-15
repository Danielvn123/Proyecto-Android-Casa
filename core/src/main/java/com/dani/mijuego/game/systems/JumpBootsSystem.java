package com.dani.mijuego.game.systems;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.dani.mijuego.game.entities.Coin;
import com.dani.mijuego.game.entities.Enemy;
import com.dani.mijuego.game.entities.JumpBoots;
import com.dani.mijuego.game.entities.Player;
import com.dani.mijuego.game.world.Platform;

public class JumpBootsSystem {

    public interface OnBootsCollected {
        void onBootsCollected();
    }

    public final Array<JumpBoots> boots = new Array<>();

    public static final float BOOTS_W = 90f;
    public static final float BOOTS_H = 90f;

    private static final int MAX_ON_SCREEN = 1;

    public void reset() {
        boots.clear();
    }

    public void trySpawnOnPlatformIfFree(
        Platform p,
        CoinSystem coinSystem,
        EnemySystem enemySystem,
        float chance,
        boolean force
    ) {
        if (p == null) return;
        if (boots.size >= MAX_ON_SCREEN) return;

        if (!force && MathUtils.random() > chance) return;

        float x = p.rect.x + (p.rect.width - BOOTS_W) / 2f;
        float y = p.rect.y + p.rect.height + 35f;

        Rectangle spawnRect = new Rectangle(x, y, BOOTS_W, BOOTS_H);

        if (overlapsCoin(spawnRect, coinSystem)) return;
        if (overlapsEnemy(spawnRect, enemySystem)) return;

        JumpBoots jb = new JumpBoots(x, y, BOOTS_W, BOOTS_H);

        // ANCLAR A LA PLATAFORMA
        jb.platform = p;
        jb.offsetX = x - p.rect.x;
        jb.offsetY = 35f;

        boots.add(jb);
    }

    public void update(Player player, float killY, OnBootsCollected cb) {
        // Actualizar posición siguiendo la plataforma
        for (int i = boots.size - 1; i >= 0; i--) {
            JumpBoots b = boots.get(i);

            if (b.platform != null) {
                b.rect.x = b.platform.rect.x + b.offsetX;
                b.rect.y = b.platform.rect.y + b.platform.rect.height + b.offsetY;
            }

            if (b.rect.y + b.rect.height < killY) {
                boots.removeIndex(i);
                continue;
            }

            if (player != null && b.rect.overlaps(player.rect)) {
                boots.removeIndex(i);
                if (cb != null) cb.onBootsCollected();
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
}
