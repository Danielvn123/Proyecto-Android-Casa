package com.dani.mijuego.game.systems;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.GameHaptics; // ✅ NUEVO
import com.dani.mijuego.game.entities.Enemy;
import com.dani.mijuego.game.entities.EnemyType;
import com.dani.mijuego.game.entities.Player;

public class EnemySystem {

    public final Array<Enemy> enemies = new Array<>();

    public interface OnEnemyHit {
        // devuelve true si el golpe está “bloqueado” (no aplicar empujón/daño)
        boolean onEnemyHit(EnemyType type);
    }

    public void update(Player player, float killY, OnEnemyHit callback) {
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);

            if (e.platform == null) {
                enemies.removeIndex(i);
                continue;
            }

            if (e.platform.rect.y + e.platform.rect.height < killY) {
                enemies.removeIndex(i);
                continue;
            }

            e.sync();

            if (player != null && e.rect.overlaps(player.rect)) {

                // ✅ VIBRACIÓN al tocar un bicho (respeta opción OFF)
                GameHaptics.vibrate(30);

                // 1) Preguntamos primero si está bloqueado (escudo)
                boolean blocked = false;
                if (callback != null) {
                    blocked = callback.onEnemyHit(e.type);
                }

                // 2) Si NO está bloqueado, aplicamos empujón normal
                if (!blocked) {
                    float playerCx = player.rect.x + player.rect.width * 0.5f;
                    float enemyCx  = e.rect.x + e.rect.width * 0.5f;
                    float dir = (playerCx < enemyCx) ? -1f : 1f;

                    player.knockbackX = dir * GameConfig.ENEMY_PUSH_X;
                    player.velY = Math.max(player.velY, GameConfig.ENEMY_PUSH_Y);
                }

                // 3) El bicho desaparece siempre al tocar (con o sin escudo)
                enemies.removeIndex(i);
            }
        }
    }

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

    public static EnemyType randomType() {
        int r = MathUtils.random(2);
        if (r == 0) return EnemyType.LILA;
        if (r == 1) return EnemyType.AZUL;
        return EnemyType.VERDE;
    }
}
