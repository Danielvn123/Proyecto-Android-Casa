package com.dani.mijuego.game.systems;

import com.dani.mijuego.game.entities.Coin;
import com.dani.mijuego.game.entities.Enemy;
import com.dani.mijuego.game.entities.JumpBoots;
import com.dani.mijuego.game.entities.Mushroom;
import com.dani.mijuego.game.entities.Shield;
import com.dani.mijuego.game.world.Platform;

public final class PlatformChecks {

    private PlatformChecks() {}

    public static boolean hasCoinOnPlatform(Platform p, CoinSystem coinSystem) {
        if (p == null || coinSystem == null || coinSystem.coins == null) return false;
        for (int i = 0; i < coinSystem.coins.size; i++) {
            Coin c = coinSystem.coins.get(i);
            if (c != null && c.platform == p) return true;
        }
        return false;
    }

    public static boolean hasEnemyOnPlatform(Platform p, EnemySystem enemySystem) {
        if (p == null || enemySystem == null || enemySystem.enemies == null) return false;
        for (int i = 0; i < enemySystem.enemies.size; i++) {
            Enemy e = enemySystem.enemies.get(i);
            if (e != null && e.platform == p) return true;
        }
        return false;
    }

    public static boolean hasMushroomOnPlatform(Platform p, MushroomSystem mushroomSystem) {
        if (p == null || mushroomSystem == null || mushroomSystem.mushrooms == null) return false;
        for (int i = 0; i < mushroomSystem.mushrooms.size; i++) {
            Mushroom m = mushroomSystem.mushrooms.get(i);
            if (m != null && m.platform == p) return true;
        }
        return false;
    }

    public static boolean hasBootsOnPlatform(Platform p, JumpBootsSystem bootsSystem) {
        if (p == null || bootsSystem == null || bootsSystem.boots == null) return false;
        for (int i = 0; i < bootsSystem.boots.size; i++) {
            JumpBoots b = bootsSystem.boots.get(i);
            if (b != null && b.platform == p) return true;
        }
        return false;
    }

    public static boolean hasShieldOnPlatform(Platform p, ShieldSystem shieldSystem) {
        if (p == null || shieldSystem == null || shieldSystem.shields == null) return false;
        for (int i = 0; i < shieldSystem.shields.size; i++) {
            Shield s = shieldSystem.shields.get(i);
            if (s != null && s.platform == p) return true;
        }
        return false;
    }
}
