package com.dani.mijuego.game.systems;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.GameHaptics;
import com.dani.mijuego.game.entities.Enemy;
import com.dani.mijuego.game.entities.EnemyType;
import com.dani.mijuego.game.entities.Player;

// Sistema que gestiona todos los enemigos del juego
public class EnemySystem {

    // Lista de enemigos activos
    public final Array<Enemy> enemies = new Array<>();

    // Interfaz que se usa para avisar cuando el jugador toca un enemigo
    public interface OnEnemyHit {
        // Devuelve true si el golpe está bloqueado por el escudo
        boolean onEnemyHit(EnemyType type);
    }

    // Actualiza los enemigos del juego
    public void update(Player player, float killY, OnEnemyHit callback) {

        // Recorremos desde el final para poder eliminar sin errores
        for (int i = enemies.size - 1; i >= 0; i--) {

            Enemy e = enemies.get(i);

            // Si la plataforma ya no existe, eliminamos el enemigo
            if (e.platform == null) {
                enemies.removeIndex(i);
                continue;
            }

            // Si la plataforma está por debajo del límite, eliminamos el enemigo
            if (e.platform.rect.y + e.platform.rect.height < killY) {
                enemies.removeIndex(i);
                continue;
            }

            // Sincroniza la posición del enemigo con su plataforma
            e.sync();

            // Si el jugador existe y colisiona con el enemigo
            if (player != null && e.rect.overlaps(player.rect)) {

                // Vibración al tocar un enemigo
                GameHaptics.vibrate(30);

                // Preguntamos si el golpe está bloqueado por el escudo
                boolean blocked = false;
                if (callback != null) {
                    blocked = callback.onEnemyHit(e.type);
                }

                // Si no está bloqueado, aplicamos empujón al jugador
                if (!blocked) {

                    // Calculamos el centro del jugador y del enemigo
                    float playerCx = player.rect.x + player.rect.width * 0.5f;
                    float enemyCx  = e.rect.x + e.rect.width * 0.5f;

                    // Dirección del empujón
                    float dir = (playerCx < enemyCx) ? -1f : 1f;

                    // Aplicamos empuje horizontal
                    player.knockbackX = dir * GameConfig.ENEMY_PUSH_X;

                    // Aplicamos empuje vertical mínimo
                    player.velY = Math.max(player.velY, GameConfig.ENEMY_PUSH_Y);
                }

                // El enemigo desaparece siempre al tocarlo
                enemies.removeIndex(i);
            }
        }
    }

    // Devuelve la escala de dibujo según el tipo de enemigo
    public static float getDrawScale(EnemyType type) {

        if (type == null) return 1f;

        switch (type) {
            case LILA:  return 1.0f;
            case VERDE: return 1.30f;
            case AZUL:  return 1.10f;
            case ROJO:  return 1.30f;
            default:    return 1f;
        }
    }

    // Devuelve un tipo de enemigo aleatorio
    public static EnemyType randomType() {

        int r = MathUtils.random(3);

        if (r == 0) return EnemyType.LILA;
        if (r == 1) return EnemyType.AZUL;
        if (r == 2) return EnemyType.VERDE;

        return EnemyType.ROJO;
    }
}
