package com.dani.mijuego.game.systems;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.dani.mijuego.game.entities.Player;
import com.dani.mijuego.game.entities.Shield;
import com.dani.mijuego.game.world.Platform;

public class ShieldSystem {

    public final Array<Shield> shields = new Array<>();

    public interface OnShieldCollected {
        void onShieldCollected();
    }

    public static final float SHIELD_W = 90f;
    public static final float SHIELD_H = 90f;

    private static final int MAX_ON_SCREEN = 1;

    private final Rectangle tmpSpawnRect = new Rectangle();

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

        // bloqueo rápido por plataforma
        if (PlatformChecks.hasCoinOnPlatform(p, coinSystem) || PlatformChecks.hasEnemyOnPlatform(p, enemySystem)) return;
        if (PlatformChecks.hasBootsOnPlatform(p, bootsSystem)) return;

        if (!force && MathUtils.random() > chance) return;

        float x = p.rect.x + (p.rect.width - SHIELD_W) / 2f;
        float y = p.rect.y + p.rect.height + 35f;

        tmpSpawnRect.set(x, y, SHIELD_W, SHIELD_H);

        // seguridad extra por overlaps
        if (SpawnOverlap.overlapsCoins(tmpSpawnRect, coinSystem)) return;
        if (SpawnOverlap.overlapsEnemies(tmpSpawnRect, enemySystem)) return;
        if (SpawnOverlap.overlapsBoots(tmpSpawnRect, bootsSystem)) return;

        Shield s = new Shield(x, y, SHIELD_W, SHIELD_H);

        s.platform = p;
        s.offsetX = x - p.rect.x;
        s.offsetY = 35f;

        shields.add(s);
    }

    public void update(Player player, float killY, OnShieldCollected cb) {
        for (int i = shields.size - 1; i >= 0; i--) {
            Shield s = shields.get(i);
            if (s == null) {
                shields.removeIndex(i);
                continue;
            }

            s.followPlatform();

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
}
