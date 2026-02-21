package com.dani.mijuego.game.systems;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.dani.mijuego.game.entities.JumpBoots;
import com.dani.mijuego.game.entities.Player;
import com.dani.mijuego.game.world.Platform;

public class JumpBootsSystem {

    public final Array<JumpBoots> boots = new Array<>();

    public interface OnBootsCollected {
        void onBootsCollected();
    }

    public static final float BOOTS_W = 90f;
    public static final float BOOTS_H = 90f;

    private static final int MAX_ON_SCREEN = 1;

    // evita new Rectangle cada spawn
    private final Rectangle tmpSpawnRect = new Rectangle();

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

        // si ya hay algo en esa plataforma, fuera (más rápido que overlaps)
        if (PlatformChecks.hasCoinOnPlatform(p, coinSystem) || PlatformChecks.hasEnemyOnPlatform(p, enemySystem)) return;

        if (!force && MathUtils.random() > chance) return;

        float x = p.rect.x + (p.rect.width - BOOTS_W) / 2f;
        float y = p.rect.y + p.rect.height + 35f;

        tmpSpawnRect.set(x, y, BOOTS_W, BOOTS_H);

        // seguridad extra (si hay coin/enemy cerca aunque no sea misma plataforma)
        if (SpawnOverlap.overlapsCoins(tmpSpawnRect, coinSystem)) return;
        if (SpawnOverlap.overlapsEnemies(tmpSpawnRect, enemySystem)) return;

        JumpBoots jb = new JumpBoots(x, y, BOOTS_W, BOOTS_H);

        jb.platform = p;
        jb.offsetX = x - p.rect.x;
        jb.offsetY = 35f;

        boots.add(jb);
    }

    public void update(Player player, float killY, OnBootsCollected cb) {
        for (int i = boots.size - 1; i >= 0; i--) {
            JumpBoots b = boots.get(i);
            if (b == null) {
                boots.removeIndex(i);
                continue;
            }

            b.followPlatform();

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
}
