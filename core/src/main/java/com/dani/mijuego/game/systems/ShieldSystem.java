package com.dani.mijuego.game.systems;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.dani.mijuego.game.entities.Player;
import com.dani.mijuego.game.entities.Shield;
import com.dani.mijuego.game.world.Platform;

// Sistema que gestiona los escudos del juego
public class ShieldSystem {

    // Lista de escudos activos
    public final Array<Shield> shields = new Array<>();

    // Interfaz para avisar cuando el jugador recoge un escudo
    public interface OnShieldCollected {
        void onShieldCollected();
    }

    // Tamaño del escudo
    public static final float SHIELD_W = 90f;
    public static final float SHIELD_H = 90f;

    // Máximo número de escudos en pantalla
    private static final int MAX_ON_SCREEN = 1;

    // Rectángulo temporal para comprobar solapamientos al generar
    private final Rectangle tmpSpawnRect = new Rectangle();

    // Borra todos los escudos
    public void reset() {
        shields.clear();
    }

    // Intenta generar un escudo encima de una plataforma si hay hueco
    public void trySpawnOnPlatformIfFree(
        Platform p,
        CoinSystem coinSystem,
        EnemySystem enemySystem,
        JumpBootsSystem bootsSystem,
        float chance,
        boolean force
    ) {
        // Si la plataforma no existe, no hacemos nada
        if (p == null) return;

        // Si ya hay un escudo en pantalla, no generamos más
        if (shields.size >= MAX_ON_SCREEN) return;

        // Comprobación rápida: no spawnear si ya hay moneda, enemigo o botas en esa plataforma
        if (PlatformChecks.hasCoinOnPlatform(p, coinSystem) || PlatformChecks.hasEnemyOnPlatform(p, enemySystem)) return;
        if (PlatformChecks.hasBootsOnPlatform(p, bootsSystem)) return;

        // Si no es forzado, aplicamos probabilidad
        if (!force && MathUtils.random() > chance) return;

        // Calcula posición centrada en la plataforma
        float x = p.rect.x + (p.rect.width - SHIELD_W) / 2f;

        // Coloca el escudo un poco por encima de la plataforma
        float y = p.rect.y + p.rect.height + 35f;

        // Guardamos rectángulo de spawn para comprobar solapamientos
        tmpSpawnRect.set(x, y, SHIELD_W, SHIELD_H);

        // Seguridad extra: evita que se solape con objetos cercanos
        if (SpawnOverlap.overlapsCoins(tmpSpawnRect, coinSystem)) return;
        if (SpawnOverlap.overlapsEnemies(tmpSpawnRect, enemySystem)) return;
        if (SpawnOverlap.overlapsBoots(tmpSpawnRect, bootsSystem)) return;

        // Creamos el escudo
        Shield s = new Shield(x, y, SHIELD_W, SHIELD_H);

        // Lo anclamos a la plataforma para que se mueva con ella
        s.platform = p;
        s.offsetX = x - p.rect.x;
        s.offsetY = 35f;

        // Añadimos el escudo a la lista
        shields.add(s);
    }

    // Actualiza escudos: seguimiento, eliminación y recogida
    public void update(Player player, float killY, OnShieldCollected cb) {

        // Recorremos al revés para poder eliminar sin problemas
        for (int i = shields.size - 1; i >= 0; i--) {

            Shield s = shields.get(i);

            // Si por algún motivo es null, se elimina
            if (s == null) {
                shields.removeIndex(i);
                continue;
            }

            // Hace que el escudo siga a la plataforma
            s.followPlatform();

            // Si baja por debajo del límite, se elimina
            if (s.rect.y + s.rect.height < killY) {
                shields.removeIndex(i);
                continue;
            }

            // Si el jugador toca el escudo, se recoge
            if (player != null && s.rect.overlaps(player.rect)) {
                shields.removeIndex(i);

                // Llamamos al callback para activar el escudo en el jugador
                if (cb != null) cb.onShieldCollected();
            }
        }
    }
}
