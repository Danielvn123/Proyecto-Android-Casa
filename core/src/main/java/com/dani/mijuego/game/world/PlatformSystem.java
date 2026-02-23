package com.dani.mijuego.game.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.dani.mijuego.game.GameConfig;

// Sistema que gestiona todas las plataformas del juego
public class PlatformSystem {

    // Lista de plataformas activas en el mundo
    public final Array<Platform> platforms = new Array<>();

    // Altura Y donde se generará la siguiente plataforma
    public float nextY = 0f;

    // Crea una nueva plataforma con propiedades aleatorias
    public Platform makePlatform(float x, float y, boolean allowMoving) {

        // Decide si la plataforma será móvil según probabilidad
        boolean moving = allowMoving && MathUtils.randomBoolean(GameConfig.MOVING_CHANCE);

        // Si es móvil, asigna velocidad aleatoria dentro de un rango
        float speed = moving
            ? MathUtils.random(GameConfig.MOVING_SPEED_MIN, GameConfig.MOVING_SPEED_MAX)
            : 0f;

        // Decide si será rompible según probabilidad
        boolean breakable = MathUtils.randomBoolean(GameConfig.BREAKABLE_CHANCE);

        // Devuelve la nueva plataforma con su rectángulo
        return new Platform(
            new Rectangle(x, y, GameConfig.PLATFORM_W, GameConfig.PLATFORM_H),
            moving,
            speed,
            breakable
        );
    }

    // Actualiza el movimiento de las plataformas móviles
    public void updateMoving(float dt) {

        for (Platform p : platforms) {

            // Si no es móvil o está rota, no se mueve
            if (!p.moving || p.broken) continue;

            // Movimiento horizontal según velocidad y dirección
            p.rect.x += p.speed * p.dir * dt;

            // Rebote contra borde izquierdo
            if (p.rect.x <= 0f) {
                p.rect.x = 0f;
                p.dir = 1;
            }
            // Rebote contra borde derecho
            else if (p.rect.x + p.rect.width >= GameConfig.VW) {
                p.rect.x = GameConfig.VW - p.rect.width;
                p.dir = -1;
            }
        }
    }

    // Actualiza plataformas rompibles y elimina las que ya han desaparecido
    public void updateBreakables(float dt) {

        // Recorre al revés para poder eliminar sin problemas
        for (int i = platforms.size - 1; i >= 0; i--) {
            Platform p = platforms.get(i);

            // Solo interesa si está rota
            if (!p.broken) continue;

            // Aumenta el tiempo desde que se rompió
            p.brokenTime += dt;

            // Si ha pasado el tiempo de desaparición, se elimina
            if (p.brokenTime >= GameConfig.BROKEN_FADE_TIME) {
                platforms.removeIndex(i);
            }
        }
    }

    // Elimina plataformas que estén por debajo del límite visible
    public void cullBelow(float killY) {

        for (int i = platforms.size - 1; i >= 0; i--) {
            Platform p = platforms.get(i);

            // Si está completamente por debajo del límite, se elimina
            if (p.rect.y + p.rect.height < killY) {
                platforms.removeIndex(i);
            }
        }
    }
}
