package com.dani.mijuego.game.systems;

import com.badlogic.gdx.math.Rectangle;
import com.dani.mijuego.game.entities.Coin;
import com.dani.mijuego.game.entities.Enemy;
import com.dani.mijuego.game.entities.JumpBoots;
import com.dani.mijuego.game.entities.Mushroom;
import com.dani.mijuego.game.entities.Shield;

/**
 * Helper estático para evitar spawns encima de otros pickups/enemigos.
 * Solo comprueba overlaps de rectángulos.
 */
public final class SpawnOverlap {

    private SpawnOverlap() {}

    public static boolean overlapsCoins(Rectangle r, CoinSystem coinSystem) {
        if (r == null || coinSystem == null || coinSystem.coins == null) return false;

        for (int i = 0; i < coinSystem.coins.size; i++) {
            Coin c = coinSystem.coins.get(i);
            if (c != null && c.rect != null && c.rect.overlaps(r)) {
                return true;
            }
        }
        return false;
    }

    public static boolean overlapsEnemies(Rectangle r, EnemySystem enemySystem) {
        if (r == null || enemySystem == null || enemySystem.enemies == null) return false;

        for (int i = 0; i < enemySystem.enemies.size; i++) {
            Enemy e = enemySystem.enemies.get(i);
            if (e != null && e.rect != null && e.rect.overlaps(r)) {
                return true;
            }
        }
        return false;
    }

    public static boolean overlapsBoots(Rectangle r, JumpBootsSystem bootsSystem) {
        if (r == null || bootsSystem == null || bootsSystem.boots == null) return false;

        for (int i = 0; i < bootsSystem.boots.size; i++) {
            JumpBoots b = bootsSystem.boots.get(i);
            if (b != null && b.rect != null && b.rect.overlaps(r)) {
                return true;
            }
        }
        return false;
    }

    public static boolean overlapsMushrooms(Rectangle r, MushroomSystem mushroomSystem) {
        if (r == null || mushroomSystem == null || mushroomSystem.mushrooms == null) return false;

        for (int i = 0; i < mushroomSystem.mushrooms.size; i++) {
            Mushroom m = mushroomSystem.mushrooms.get(i);
            if (m != null && m.rect != null && m.rect.overlaps(r)) {
                return true;
            }
        }
        return false;
    }

    public static boolean overlapsShields(Rectangle r, ShieldSystem shieldSystem) {
        if (r == null || shieldSystem == null || shieldSystem.shields == null) return false;

        for (int i = 0; i < shieldSystem.shields.size; i++) {
            Shield s = shieldSystem.shields.get(i);
            if (s != null && s.rect != null && s.rect.overlaps(r)) {
                return true;
            }
        }
        return false;
    }
}
