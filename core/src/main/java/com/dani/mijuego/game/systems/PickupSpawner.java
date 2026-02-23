package com.dani.mijuego.game.systems;

import com.badlogic.gdx.math.MathUtils;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.entities.Coin;
import com.dani.mijuego.game.entities.Enemy;
import com.dani.mijuego.game.world.Platform;

// Clase encargada de decidir cuándo aparecen monedas o enemigos
public class PickupSpawner {

    // Guarda la última altura donde apareció una moneda
    private float lastCoinY = -999999f;

    // Guarda la última altura donde apareció un enemigo
    private float lastEnemyY = -999999f;

    // Reinicia los valores (por ejemplo al empezar una partida nueva)
    public void reset() {
        lastCoinY = -999999f;
        lastEnemyY = -999999f;
    }

    // Intenta generar algo (moneda o enemigo) en una plataforma
    public void trySpawn(Platform p, CoinSystem coinSystem, EnemySystem enemySystem) {

        // Si la plataforma no existe, no hacemos nada
        if (p == null) return;

        // Si ya hay moneda o enemigo en la plataforma, no generamos nada
        if (PlatformChecks.hasCoinOnPlatform(p, coinSystem) || PlatformChecks.hasEnemyOnPlatform(p, enemySystem)) return;

        // Probabilidad general de que aparezca algo
        if (!MathUtils.randomBoolean(GameConfig.PICKUP_CHANCE)) return;

        // Decide si queremos moneda o enemigo según peso configurado
        boolean wantCoin = MathUtils.random() < GameConfig.PICKUP_COIN_WEIGHT;

        // Guardamos tamaños antes para comprobar si realmente se añadió algo
        int coinsBefore = coinSystem.coins.size;
        int enemiesBefore = enemySystem.enemies.size;

        if (wantCoin) {
            // Intenta generar moneda
            trySpawnCoin(p, coinSystem);

            // Si no se generó moneda, intenta generar enemigo
            if (coinSystem.coins.size == coinsBefore) {
                trySpawnEnemy(p, enemySystem);
            }
        } else {
            // Intenta generar enemigo
            trySpawnEnemy(p, enemySystem);

            // Si no se generó enemigo, intenta generar moneda
            if (enemySystem.enemies.size == enemiesBefore) {
                trySpawnCoin(p, coinSystem);
            }
        }
    }

    // Intenta generar una moneda en la plataforma
    private void trySpawnCoin(Platform p, CoinSystem coinSystem) {

        // Si ya hay moneda en esa plataforma, salir
        if (PlatformChecks.hasCoinOnPlatform(p, coinSystem)) return;

        float baseY = p.rect.y;

        // Evita que las monedas aparezcan demasiado juntas en altura
        if (Math.abs(baseY - lastCoinY) < GameConfig.COIN_MIN_GAP_Y) return;

        // Probabilidad de aparición de moneda
        if (!MathUtils.randomBoolean(GameConfig.COIN_CHANCE)) return;

        // Calcula posición centrada sobre la plataforma
        float offX = (p.rect.width - GameConfig.COIN_W) / 2f;
        float offY = p.rect.height + 10f;

        // Crea la moneda y la añade al sistema
        Coin c = new Coin(p, offX, offY);
        coinSystem.coins.add(c);

        // Guarda la altura donde apareció
        lastCoinY = c.rect.y;
    }

    // Intenta generar un enemigo en la plataforma
    private void trySpawnEnemy(Platform p, EnemySystem enemySystem) {

        // Si ya hay enemigo en esa plataforma, salir
        if (PlatformChecks.hasEnemyOnPlatform(p, enemySystem)) return;

        float baseY = p.rect.y;

        // Evita que los enemigos aparezcan demasiado juntos en altura
        if (Math.abs(baseY - lastEnemyY) < GameConfig.ENEMY_MIN_GAP_Y) return;

        // Probabilidad de aparición de enemigo
        if (!MathUtils.randomBoolean(GameConfig.ENEMY_CHANCE)) return;

        // Calcula posición centrada sobre la plataforma
        float offX = (p.rect.width - GameConfig.ENEMY_W) / 2f;
        float offY = p.rect.height + 10f;

        // Crea enemigo con tipo aleatorio
        Enemy e = new Enemy(p, offX, offY, EnemySystem.randomType());
        enemySystem.enemies.add(e);

        // Guarda la altura donde apareció
        lastEnemyY = e.rect.y;
    }
}
