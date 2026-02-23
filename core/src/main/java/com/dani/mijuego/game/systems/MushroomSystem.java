package com.dani.mijuego.game.systems;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.dani.mijuego.game.entities.Mushroom;
import com.dani.mijuego.game.entities.Player;
import com.dani.mijuego.game.world.Platform;

// Sistema que gestiona las setas del juego
public class MushroomSystem {

    // Lista de setas activas
    public final Array<Mushroom> mushrooms = new Array<>();

    // Interfaz para avisar cuando el jugador recoge una seta
    public interface OnMushroomCollected {
        void onMushroomCollected();
    }

    // Tamaño del ítem
    public static final float MUSHROOM_W = 90f;
    public static final float MUSHROOM_H = 90f;

    // Máximo número de setas en pantalla
    private static final int MAX_ON_SCREEN = 1;

    // Rectángulo temporal para comprobar colisiones al generar
    private final Rectangle tmpSpawnRect = new Rectangle();

    // Borra todas las setas activas
    public void reset() {
        mushrooms.clear();
    }

    // Intenta generar unas setas en una plataforma si está libre

    public void trySpawnOnPlatformIfFree(
        Platform p,
        CoinSystem coinSystem,
        EnemySystem enemySystem,
        JumpBootsSystem bootsSystem,
        ShieldSystem shieldSystem,
        float chance,
        boolean force
    ) {
        // Si la plataforma no existe, no hacemos nada
        if (p == null) return;

        // Si ya hay una seta en pantalla, no generamos más
        if (mushrooms.size >= MAX_ON_SCREEN) return;

        // Comprobación rápida: no spawnear si ya hay algo en esa misma plataforma
        if (PlatformChecks.hasCoinOnPlatform(p, coinSystem)) return;
        if (PlatformChecks.hasEnemyOnPlatform(p, enemySystem)) return;
        if (PlatformChecks.hasBootsOnPlatform(p, bootsSystem)) return;
        if (PlatformChecks.hasShieldOnPlatform(p, shieldSystem)) return;
        if (PlatformChecks.hasMushroomOnPlatform(p, this)) return;

        // Si no es forzado, aplicamos probabilidad de aparición
        if (!force && MathUtils.random() > chance) return;

        // Calcula posición centrada en la plataforma
        float x = p.rect.x + (p.rect.width - MUSHROOM_W) / 2f;

        // Coloca la seta un poco por encima de la plataforma
        float y = p.rect.y + p.rect.height + 35f;

        // Guardamos el rectángulo del spawn para comprobar solapamientos
        tmpSpawnRect.set(x, y, MUSHROOM_W, MUSHROOM_H);

        // Seguridad extra: evita que se solape con cosas cercanas aunque estén en otra plataforma
        if (SpawnOverlap.overlapsCoins(tmpSpawnRect, coinSystem)) return;
        if (SpawnOverlap.overlapsEnemies(tmpSpawnRect, enemySystem)) return;
        if (SpawnOverlap.overlapsBoots(tmpSpawnRect, bootsSystem)) return;
        if (SpawnOverlap.overlapsShields(tmpSpawnRect, shieldSystem)) return;
        if (SpawnOverlap.overlapsMushrooms(tmpSpawnRect, this)) return;

        // Creamos la seta
        Mushroom m = new Mushroom(x, y, MUSHROOM_W, MUSHROOM_H);

        // La anclamos a la plataforma para que se mueva con ella
        m.platform = p;
        m.offsetX = x - p.rect.x;
        m.offsetY = 35f;

        // Añadimos la seta a la lista
        mushrooms.add(m);
    }

    // Actualiza setas: seguimiento, eliminación y recogida
    public void update(Player player, float killY, OnMushroomCollected cb) {

        // Recorremos al revés para poder eliminar sin problemas
        for (int i = mushrooms.size - 1; i >= 0; i--) {

            Mushroom m = mushrooms.get(i);

            // Si por algún motivo es null, se elimina
            if (m == null) {
                mushrooms.removeIndex(i);
                continue;
            }

            // Hace que la seta siga a su plataforma
            m.followPlatform();

            // Si baja por debajo del límite, se elimina
            if (m.rect.y + m.rect.height < killY) {
                mushrooms.removeIndex(i);
                continue;
            }

            // Si el jugador toca la seta, se recoge
            if (player != null && m.rect.overlaps(player.rect)) {
                mushrooms.removeIndex(i);

                // Llamamos al callback para aplicar el efecto (malus, sonido, etc.)
                if (cb != null) cb.onMushroomCollected();
            }
        }
    }
}
