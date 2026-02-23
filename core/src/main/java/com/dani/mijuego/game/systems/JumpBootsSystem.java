package com.dani.mijuego.game.systems;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.dani.mijuego.game.entities.JumpBoots;
import com.dani.mijuego.game.entities.Player;
import com.dani.mijuego.game.world.Platform;

// Sistema que gestiona las botas de salto del juego
public class JumpBootsSystem {

    // Lista de botas activas en pantalla
    public final Array<JumpBoots> boots = new Array<>();

    // Interfaz para avisar cuando el jugador recoge unas botas
    public interface OnBootsCollected {
        void onBootsCollected();
    }

    // Tamaño del ítem en pantalla
    public static final float BOOTS_W = 90f;
    public static final float BOOTS_H = 90f;

    // Máximo número de botas que puede haber a la vez
    private static final int MAX_ON_SCREEN = 1;

    // Rectángulo temporal para comprobar si el spawn choca con otros objetos
    private final Rectangle tmpSpawnRect = new Rectangle();

    // Limpia todas las botas
    public void reset() {
        boots.clear();
    }

    // Intenta generar unas botas en una plataforma si está libre
    public void trySpawnOnPlatformIfFree(
        Platform p,
        CoinSystem coinSystem,
        EnemySystem enemySystem,
        float chance,
        boolean force
    ) {
        // Si la plataforma no existe, no hacemos nada
        if (p == null) return;

        // Si ya hay botas en pantalla, no generamos más
        if (boots.size >= MAX_ON_SCREEN) return;

        // Si en esa plataforma ya hay moneda o enemigo, no se generan botas
        if (PlatformChecks.hasCoinOnPlatform(p, coinSystem) || PlatformChecks.hasEnemyOnPlatform(p, enemySystem)) return;

        // Si no es forzado, aplicamos probabilidad de aparición
        if (!force && MathUtils.random() > chance) return;

        // Calcula posición centrada en la plataforma
        float x = p.rect.x + (p.rect.width - BOOTS_W) / 2f;

        // Coloca las botas un poco por encima de la plataforma
        float y = p.rect.y + p.rect.height + 35f;

        // Guardamos el rectángulo de spawn para comprobar solapamientos
        tmpSpawnRect.set(x, y, BOOTS_W, BOOTS_H);

        // Comprobación extra por si hay monedas o enemigos cerca aunque sea otra plataforma
        if (SpawnOverlap.overlapsCoins(tmpSpawnRect, coinSystem)) return;
        if (SpawnOverlap.overlapsEnemies(tmpSpawnRect, enemySystem)) return;

        // Creamos las botas
        JumpBoots jb = new JumpBoots(x, y, BOOTS_W, BOOTS_H);

        // Asociamos botas a la plataforma para que la sigan si se mueve
        jb.platform = p;
        jb.offsetX = x - p.rect.x;
        jb.offsetY = 35f;

        // Añadimos a la lista de botas activas
        boots.add(jb);
    }

    // Actualiza botas: seguimiento, eliminación y recogida
    public void update(Player player, float killY, OnBootsCollected cb) {

        // Recorremos al revés para poder eliminar sin problemas
        for (int i = boots.size - 1; i >= 0; i--) {

            JumpBoots b = boots.get(i);

            // Si por algún motivo es null, se elimina
            if (b == null) {
                boots.removeIndex(i);
                continue;
            }

            // Hace que las botas sigan a su plataforma
            b.followPlatform();

            // Si salen por debajo del límite, se eliminan
            if (b.rect.y + b.rect.height < killY) {
                boots.removeIndex(i);
                continue;
            }

            // Si el jugador colisiona con las botas, se recogen
            if (player != null && b.rect.overlaps(player.rect)) {
                boots.removeIndex(i);

                // Llamamos al callback para aplicar efecto (salto extra, sonido, etc.)
                if (cb != null) cb.onBootsCollected();
            }
        }
    }
}
