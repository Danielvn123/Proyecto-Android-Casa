package com.dani.mijuego.game.systems;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.dani.mijuego.game.entities.Mushroom;
import com.dani.mijuego.game.entities.Player;
import com.dani.mijuego.game.world.Platform;

public class MushroomSystem {

    public final Array<Mushroom> mushrooms = new Array<>();

    public interface OnMushroomCollected {
        void onMushroomCollected();
    }

    public static final float MUSHROOM_W = 90f;
    public static final float MUSHROOM_H = 90f;

    private static final int MAX_ON_SCREEN = 1;

    // Evita new Rectangle cada spawn (menos GC)
    private final Rectangle tmpSpawnRect = new Rectangle();

    public void reset() {
        mushrooms.clear();
    }

    /**
     * Intenta spawnear una seta encima de la plataforma p si hay hueco.
     * - chance: probabilidad (0..1)
     * - force: si true, ignora chance
     */
    public void trySpawnOnPlatformIfFree(
        Platform p,
        CoinSystem coinSystem,
        EnemySystem enemySystem,
        JumpBootsSystem bootsSystem,
        ShieldSystem shieldSystem,
        float chance,
        boolean force
    ) {
        if (p == null) return;
        if (mushrooms.size >= MAX_ON_SCREEN) return;

        // Bloqueo rápido por plataforma (si ya hay algo pegado a esa plataforma)
        if (PlatformChecks.hasCoinOnPlatform(p, coinSystem)) return;
        if (PlatformChecks.hasEnemyOnPlatform(p, enemySystem)) return;
        if (PlatformChecks.hasBootsOnPlatform(p, bootsSystem)) return;
        if (PlatformChecks.hasShieldOnPlatform(p, shieldSystem)) return;
        if (PlatformChecks.hasMushroomOnPlatform(p, this)) return;

        if (!force && MathUtils.random() > chance) return;

        float x = p.rect.x + (p.rect.width - MUSHROOM_W) / 2f;
        float y = p.rect.y + p.rect.height + 35f;

        tmpSpawnRect.set(x, y, MUSHROOM_W, MUSHROOM_H);

        // Seguridad extra por overlaps (por si hay cosas cerca aunque estén en otra plataforma)
        if (SpawnOverlap.overlapsCoins(tmpSpawnRect, coinSystem)) return;
        if (SpawnOverlap.overlapsEnemies(tmpSpawnRect, enemySystem)) return;
        if (SpawnOverlap.overlapsBoots(tmpSpawnRect, bootsSystem)) return;
        if (SpawnOverlap.overlapsShields(tmpSpawnRect, shieldSystem)) return;
        if (SpawnOverlap.overlapsMushrooms(tmpSpawnRect, this)) return;

        Mushroom m = new Mushroom(x, y, MUSHROOM_W, MUSHROOM_H);

        // Anclar a plataforma
        m.platform = p;
        m.offsetX = x - p.rect.x;
        m.offsetY = 35f;

        mushrooms.add(m);
    }

    public void update(Player player, float killY, OnMushroomCollected cb) {
        for (int i = mushrooms.size - 1; i >= 0; i--) {
            Mushroom m = mushrooms.get(i);
            if (m == null) {
                mushrooms.removeIndex(i);
                continue;
            }

            m.followPlatform();

            if (m.rect.y + m.rect.height < killY) {
                mushrooms.removeIndex(i);
                continue;
            }

            if (player != null && m.rect.overlaps(player.rect)) {
                mushrooms.removeIndex(i);
                if (cb != null) cb.onMushroomCollected();
            }
        }
    }
}
