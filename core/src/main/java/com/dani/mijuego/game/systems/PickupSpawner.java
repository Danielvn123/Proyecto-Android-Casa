package com.dani.mijuego.game.systems;

import com.badlogic.gdx.math.MathUtils;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.entities.Coin;
import com.dani.mijuego.game.entities.Enemy;
import com.dani.mijuego.game.world.Platform;

public class PickupSpawner {

    private float lastCoinY = -999999f;
    private float lastEnemyY = -999999f;

    public void reset() {
        lastCoinY = -999999f;
        lastEnemyY = -999999f;
    }

    public void trySpawn(Platform p, CoinSystem coinSystem, EnemySystem enemySystem) {
        if (p == null || p.broken) return;

        // ✅ BLOQUEO TOTAL: si YA hay algo en esa plataforma, no spawnear nada
        if (hasCoinOnPlatform(p, coinSystem) || hasEnemyOnPlatform(p, enemySystem)) return;

        if (!MathUtils.randomBoolean(GameConfig.PICKUP_CHANCE)) return;

        boolean wantCoin = MathUtils.random() < GameConfig.PICKUP_COIN_WEIGHT;

        int coinsBefore = coinSystem.coins.size;
        int enemiesBefore = enemySystem.enemies.size;

        if (wantCoin) {
            trySpawnCoin(p, coinSystem);
            // si no pudo, intentamos enemy
            if (coinSystem.coins.size == coinsBefore) {
                trySpawnEnemy(p, enemySystem);
            }
        } else {
            trySpawnEnemy(p, enemySystem);
            // si no pudo, intentamos coin
            if (enemySystem.enemies.size == enemiesBefore) {
                trySpawnCoin(p, coinSystem);
            }
        }
    }

    private void trySpawnCoin(Platform p, CoinSystem coinSystem) {
        // ✅ por si acaso: si ya hay algo, no
        if (hasCoinOnPlatform(p, coinSystem)) return;

        float baseY = p.rect.y;
        if (Math.abs(baseY - lastCoinY) < GameConfig.COIN_MIN_GAP_Y) return;
        if (!MathUtils.randomBoolean(GameConfig.COIN_CHANCE)) return;

        float offX = MathUtils.random(10f, p.rect.width - GameConfig.COIN_W - 10f);
        float offY = p.rect.height + 25f;

        Coin c = new Coin(p, offX, offY);
        coinSystem.coins.add(c);
        lastCoinY = c.rect.y;
    }

    private void trySpawnEnemy(Platform p, EnemySystem enemySystem) {
        // ✅ por si acaso: si ya hay algo, no
        if (hasEnemyOnPlatform(p, enemySystem)) return;

        float baseY = p.rect.y;
        if (Math.abs(baseY - lastEnemyY) < GameConfig.ENEMY_MIN_GAP_Y) return;
        if (!MathUtils.randomBoolean(GameConfig.ENEMY_CHANCE)) return;

        float offX = (p.rect.width - GameConfig.ENEMY_W) / 2f;
        float offY = p.rect.height + 10f;

        Enemy e = new Enemy(p, offX, offY, EnemySystem.randomType());
        enemySystem.enemies.add(e);
        lastEnemyY = e.rect.y;
    }

    // ==========================
    // ✅ Helpers: detectar si ya hay cosas en la plataforma
    // ==========================
    private boolean hasEnemyOnPlatform(Platform p, EnemySystem enemySystem) {
        if (p == null || enemySystem == null) return false;
        for (int i = 0; i < enemySystem.enemies.size; i++) {
            Enemy e = enemySystem.enemies.get(i);
            if (e != null && e.platform == p) return true;
        }
        return false;
    }

    private boolean hasCoinOnPlatform(Platform p, CoinSystem coinSystem) {
        if (p == null || coinSystem == null) return false;
        for (int i = 0; i < coinSystem.coins.size; i++) {
            Coin c = coinSystem.coins.get(i);
            if (c != null && c.platform == p) return true; // ✅ tu Coin ya guarda platform (porque lo creas con new Coin(p,...))
        }
        return false;
    }
}
