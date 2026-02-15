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
        if (!MathUtils.randomBoolean(GameConfig.PICKUP_CHANCE)) return;

        boolean wantCoin = MathUtils.random() < GameConfig.PICKUP_COIN_WEIGHT;

        int coinsBefore = coinSystem.coins.size;
        int enemiesBefore = enemySystem.enemies.size;

        if (wantCoin) {
            trySpawnCoin(p, coinSystem);
            if (coinSystem.coins.size == coinsBefore) trySpawnEnemy(p, enemySystem);
        } else {
            trySpawnEnemy(p, enemySystem);
            if (enemySystem.enemies.size == enemiesBefore) trySpawnCoin(p, coinSystem);
        }
    }

    private void trySpawnCoin(Platform p, CoinSystem coinSystem) {
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
        float baseY = p.rect.y;
        if (Math.abs(baseY - lastEnemyY) < GameConfig.ENEMY_MIN_GAP_Y) return;
        if (!MathUtils.randomBoolean(GameConfig.ENEMY_CHANCE)) return;

        float offX = (p.rect.width - GameConfig.ENEMY_W) / 2f;
        float offY = p.rect.height + 10f;

        Enemy e = new Enemy(p, offX, offY, EnemySystem.randomType());
        enemySystem.enemies.add(e);
        lastEnemyY = e.rect.y;
    }
}
