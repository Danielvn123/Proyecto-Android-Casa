package com.dani.mijuego.screens;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.I18n;
import com.dani.mijuego.game.entities.Enemy;
import com.dani.mijuego.game.entities.EnemyType;
import com.dani.mijuego.game.entities.Mushroom;
import com.dani.mijuego.game.world.Platform;

public class GameLogic {

    // Referencia a la pantalla de juego para modificar estado, jugador, cámara y sistemas
    final GameScreen s;

    // Constructor: guarda referencia y reinicia el mundo al crear la lógica
    public GameLogic(GameScreen s) {
        this.s = s;
        resetWorld();
    }

    // Reinicia toda la partida:
    // resetea flags, score, sistemas, cámara, crea la primera plataforma y genera plataformas iniciales
    void resetWorld() {

        s.started = false;
        s.score = 0;
        s.maxY = 0f;

        s.dying = false;
        s.dyingTimer = 0f;

        s.voidFalling = false;
        s.playedVoidFallSfx = false;

        s.lapIndex = 0;

        s.bootsSystem.reset();
        s.shieldSystem.reset();
        s.mushroomSystem.reset();

        s.coinSystem.coins.clear();
        s.coinSystem.collected = 0;

        s.enemySystem.enemies.clear();
        s.pickupSpawner.reset();

        s.runTimeSec = 0f;

        s.platformSystem.platforms.clear();

        s.goalSpawned = false;
        s.goalReached = false;
        s.goalPlatform = null;
        s.goalFlagRect = null;
        s.goalTimer = 0f;
        s.goalMsg = null;

        s.goalCamFrozen = false;
        s.goalFrozenCamY = 0f;

        // Coloca cámara al inicio y calcula el “suelo” visible (groundY)
        s.cam.position.set(GameConfig.VW / 2f, GameConfig.VH / 2f, 0);
        s.cam.update();

        float worldH = s.viewport.getWorldHeight();
        float bottomVisible = s.cam.position.y - worldH / 2f;
        s.groundY = bottomVisible;

        // Resetea el decorado (ruinas) a partir del suelo
        s.ruins.reset(s.groundY);

        // Crea la plataforma inicial centrada
        float firstPlatformX = (GameConfig.VW - GameConfig.PLATFORM_W) / 2f;
        float firstPlatformY = s.groundY + 90f;

        Platform first = new Platform(
            new Rectangle(firstPlatformX, firstPlatformY, GameConfig.PLATFORM_W, GameConfig.PLATFORM_H),
            false, 0f, false
        );
        first.dir = 1;
        s.platformSystem.platforms.add(first);

        // Crea el jugador encima de la plataforma inicial
        s.player = new com.dani.mijuego.game.entities.Player(
            (GameConfig.VW - GameConfig.PLAYER_W) / 2f,
            firstPlatformY + GameConfig.PLATFORM_H,
            s.pIdleTex, s.pIzqTex, s.pDerTex
        );

        s.maxY = s.player.rect.y;

        // Genera un conjunto inicial de plataformas hacia arriba y spawnea objetos/enemigos
        float y = firstPlatformY;

        for (int i = 0; i < 10; i++) {
            y += GameConfig.STEP_Y;
            float x = MathUtils.random(0f, GameConfig.VW - GameConfig.PLATFORM_W);

            Platform p = s.platformSystem.makePlatform(x, y, true);
            s.platformSystem.platforms.add(p);

            // Intenta spawnear pickups/enemigos varias veces si cae en una plataforma libre
            int tries = 2 + extraSpawnAttemptsForNivel();
            for (int t = 0; t < tries; t++) {
                int enemiesBefore = s.enemySystem.enemies.size;
                s.pickupSpawner.trySpawn(p, s.coinSystem, s.enemySystem);
                clampNewEnemiesToLevel(enemiesBefore);
                if (s.hasAnyThingOnPlatform(p)) break;
            }

            // Intenta spawnear powerups si la plataforma sigue libre
            spawnPowerupsOnPlatform(p);
        }

        // Guarda el siguiente Y donde se seguirá generando en infinito
        s.platformSystem.nextY = y;

        // Resetea cámara al centro inicial
        s.cam.position.set(GameConfig.VW / 2f, GameConfig.VH / 2f, 0);
        s.cam.update();
    }

    // Update principal de la lógica del juego:
    // gestiona victoria, movimiento, muerte, cámara, spawn infinito, colisiones y culling
    void update(float dt) {

        // Si ya se alcanzó el objetivo, espera unos segundos y pasa a VictoryScreen
        if (s.goalReached) {
            s.goalTimer -= dt;
            if (s.goalTimer <= 0f) s.finishVictory();
            return;
        }

        // Movimiento horizontal del jugador: bloqueado antes de empezar o durante muerte/victoria
        if (s.player != null) {

            // Antes de empezar: no hay controles y se queda centrado
            if (!s.started || s.dying || s.goalReached) {
                s.player.rect.x = (GameConfig.VW - s.player.rect.width) / 2f;

            } else {
                // Después de empezar: controles normales
                s.player.updateHorizontal(dt);

                // Wrap horizontal: si sale por un lado, aparece por el otro
                float w = s.player.rect.width;
                float worldW = GameConfig.VW;

                if (s.player.rect.x + w < 0f) s.player.rect.x = worldW;
                else if (s.player.rect.x > worldW) s.player.rect.x = -w;
            }
        }

        // Si está muriendo, actualiza la caída/muerte y no ejecuta el resto de lógica normal
        if (s.dying) {
            updateDying(dt);
            return;
        }

        // Tiempo total de la partida (solo cuando ya empezó)
        if (s.started) s.runTimeSec += dt;

        // Actualiza plataformas móviles
        s.platformSystem.updateMoving(dt);

        // Actualiza movimiento vertical del jugador y colisiones (solo si empezó)
        if (s.started && s.player != null) {
            updatePlayerVertical(dt);
        }

        // Actualiza plataformas rompibles (fade / timers)
        s.platformSystem.updateBreakables(dt);

        // Cuenta atrás del mensaje de cambio de nivel/vuelta (lap)
        if (s.levelUpMsgTime > 0f) {
            s.levelUpMsgTime -= dt;
            if (s.levelUpMsgTime < 0f) s.levelUpMsgTime = 0f;
        }

        // Seguimiento de cámara al jugador (con opción de congelarla en final del modo niveles)
        updateCamera(dt);

        // Actualiza decorado de ruinas según la parte baja visible
        float worldH = s.viewport.getWorldHeight();
        float bottomVisible = s.cam.position.y - worldH / 2f;
        s.ruins.update(dt, bottomVisible);

        // Spawneo infinito de plataformas mientras la cámara sube
        // En modo niveles, si ya se spawneó la meta, se deja de generar más
        boolean canSpawnMore = !(s.gameMode == com.dani.mijuego.game.GameMode.LEVELS && s.goalSpawned);
        if (canSpawnMore) {
            float topVisible = s.cam.position.y + worldH / 2f + 300f;
            while (s.platformSystem.nextY < topVisible) spawnPlatformAbove();
        }

        // Línea de muerte por debajo de la pantalla
        float killY = bottomVisible - 200f;

        // Actualiza efectos del jugador (duración de escudo, seta, botas, etc.)
        if (s.player != null) s.player.updateEffects(dt);

        // Actualiza sistemas (recogibles/enemigos) y elimina lo que esté demasiado abajo
        s.coinSystem.update(s.player, killY, s.onCoinCollected);
        s.bootsSystem.update(s.player, killY, s.onBootsCollected);
        s.shieldSystem.update(s.player, killY, s.onShieldCollected);
        s.mushroomSystem.update(s.player, killY, s.onMushroomCollected);
        s.enemySystem.update(s.player, killY, s.onEnemyHit);

        // Limpia plataformas muy por debajo del jugador para optimizar (culling)
        float keepBelow = 3f * GameConfig.STEP_Y + 250f;
        float cullY = s.player.rect.y - keepBelow;
        cullY = Math.min(cullY, bottomVisible - 200f);
        s.platformSystem.cullBelow(cullY);

        // Limpia setas si su plataforma ya no existe
        cullMushroomsWithMissingPlatforms();

        // Detecta colisión del jugador con la bandera de meta (modo niveles)
        if (s.gameMode == com.dani.mijuego.game.GameMode.LEVELS && s.goalSpawned && s.goalFlagRect != null && s.player != null && !s.goalReached) {
            if (s.player.rect.overlaps(s.goalFlagRect)) {
                triggerGoalReached();
                return;
            }
        }

        // Muerte “dura” si el jugador cae demasiado por debajo del killY
        if (s.started && s.player != null && !s.dying) {
            if (s.player.rect.y < killY - s.HARD_DEATH_EXTRA) {
                beginDying(s.HARD_DEATH_DURATION, false);
            }
        }
    }

    // Actualiza la animación/estado de muerte:
    // aplica gravedad (más fuerte si es caída al vacío), sigue al jugador con cámara y al final termina en GameOver
    private void updateDying(float dt) {
        if (s.player != null) {
            float g = GameConfig.GRAVITY;

            if (s.voidFalling) g *= 2.8f;

            s.player.velY -= g * dt;
            s.player.rect.y += s.player.velY * dt;
        }

        // La cámara sigue al jugador durante la caída
        s.cam.position.x = GameConfig.VW / 2f;
        if (s.player != null) {
            float targetY = s.player.rect.y + s.CAM_Y_OFFSET;
            float a = 1f - (float) Math.exp(-s.CAM_FOLLOW_SPEED * dt);
            s.cam.position.y = MathUtils.lerp(s.cam.position.y, targetY, a);
        }
        s.cam.update();

        // Cuenta atrás hasta finalizar la muerte (cambio a GameOver)
        s.dyingTimer -= dt;
        if (s.dyingTimer <= 0f) s.finishDying();
    }

    // Controla la física vertical del jugador:
    // gravedad, colisión con plataformas, detección de caída al vacío y gestión de niveles/laps según altura
    private void updatePlayerVertical(float dt) {

        float oldY = s.player.rect.y;

        // Gravedad distinta si cae (fall multiplier + límite de velocidad)
        if (s.player.velY < 0f) {
            s.player.velY -= GameConfig.GRAVITY * s.FALL_MULT * dt;
            if (s.player.velY < -s.MAX_FALL_SPEED) s.player.velY = -s.MAX_FALL_SPEED;
        } else {
            s.player.velY -= GameConfig.GRAVITY * dt;
        }

        // Aplica movimiento vertical
        s.player.rect.y += s.player.velY * dt;

        // Comprueba colisión contra plataformas y aplica salto automático
        handlePlatformCollision(oldY);

        // Si está cayendo y no hay plataforma debajo en un rango, activa “caída al vacío”
        boolean canTriggerVoidFall = (s.player.velY < 0f)
            && !s.dying
            && !s.goalReached
            && !(s.gameMode == com.dani.mijuego.game.GameMode.LEVELS && s.goalSpawned);

        if (canTriggerVoidFall && !hasPlatformBelowPlayer(s.FALL_CHECK_RANGE)) {
            beginDying(s.VOID_FALL_DURATION, true);
            return;
        }

        // Actualiza altura máxima y score en metros
        if (s.player.rect.y > s.maxY) {
            s.maxY = s.player.rect.y;
            s.score = (int) (s.maxY / 100f);

            // En modo niveles: limita score al objetivo cuando ya existe la meta
            if (s.gameMode == com.dani.mijuego.game.GameMode.LEVELS && s.goalSpawned) {
                if (s.score > s.MODE_TARGET_METERS) s.score = s.MODE_TARGET_METERS;
            }
        }

        // Gestión de dificultad/visual según modo
        if (s.gameMode == com.dani.mijuego.game.GameMode.LEVELS) {
            // Cuando llega al objetivo, spawnea plataforma final
            if (s.score >= s.MODE_TARGET_METERS && !s.goalSpawned) spawnGoalPlatform();
            // Mientras no haya meta, va cambiando el nivel visual por metros
            if (!s.goalSpawned) applyLevelByMeters(s.score, true);
        } else {
            // Modo infinito: usa “laps” (vueltas) cada X metros y reinicia el nivel visual
            int newLap = s.score / s.MODE_TARGET_METERS;
            if (newLap != s.lapIndex) {
                s.lapIndex = newLap;
                s.levelUpMsgText = I18n.t("msg_new_lap");
                s.levelUpMsgTime = s.LEVEL_UP_MSG_DURATION;
                s.audio.playLevelUp();
                s.setNivelVisual(0, false);
            }

            // Dificultad dentro de la vuelta actual
            int lapMeters = s.score % s.MODE_TARGET_METERS;
            applyLevelByMeters(lapMeters, true);
        }
    }

    // Cámara: sigue al jugador con suavizado (lerp/exponencial)
    // y aplica un mínimo para que nunca baje del suelo
    // en modo niveles puede congelarse al llegar al final para encuadrar la meta
    private void updateCamera(float dt) {
        if (s.player == null) return;

        s.cam.position.x = GameConfig.VW / 2f;

        float minCamY = s.groundY + s.viewport.getWorldHeight() / 2f;
        float targetY;

        if (s.gameMode == com.dani.mijuego.game.GameMode.LEVELS && s.goalCamFrozen) {
            targetY = s.goalFrozenCamY;
        } else {
            targetY = s.player.rect.y + s.CAM_Y_OFFSET;
            if (targetY < minCamY) targetY = minCamY;
        }

        float a = 1f - (float) Math.exp(-s.CAM_FOLLOW_SPEED * dt);
        s.cam.position.y = MathUtils.lerp(s.cam.position.y, targetY, a);

        if (s.cam.position.y < minCamY) s.cam.position.y = minCamY;

        s.cam.update();
    }

    // Inicia el proceso de muerte:
    // marca flags, define duración, decide si es “void fall” y reproduce audio correspondiente
    private void beginDying(float duration, boolean isVoidFall) {
        if (s.dying) return;

        s.dying = true;
        s.dyingTimer = duration;

        s.voidFalling = isVoidFall;
        s.playedVoidFallSfx = false;

        s.audio.stopFondo();

        if (s.voidFalling) {
            s.audio.playCaidaPersonaje();
            s.playedVoidFallSfx = true;
        }
    }

    // Comprueba si existe alguna plataforma “válida” debajo del jugador dentro de un rango vertical
    // se usa para detectar la caída al vacío
    private boolean hasPlatformBelowPlayer(float range) {
        if (s.player == null) return false;

        float minY = s.player.rect.y - range;

        for (int i = 0; i < s.platformSystem.platforms.size; i++) {
            Platform p = s.platformSystem.platforms.get(i);
            if (p == null || p.broken) continue;

            float platTop = p.rect.y + p.rect.height;

            if (platTop < s.player.rect.y && platTop > minY) {
                return true;
            }
        }
        return false;
    }

    // Cambia el nivel visual/dificultad según metros:
    // 0 -> 1 -> 2 -> 3 al superar 200/400/600
    private void applyLevelByMeters(int meters, boolean playSfx) {
        if (meters >= 600) s.setNivelVisual(3, playSfx);
        else if (meters >= 400) s.setNivelVisual(2, playSfx);
        else if (meters >= 200) s.setNivelVisual(1, playSfx);
        else s.setNivelVisual(0, false);
    }

    // ==========================
    // SPAWN
    // ==========================

    // Genera una nueva plataforma por encima del límite actual y spawnea objetos/enemigos/powerups
    private void spawnPlatformAbove() {
        s.platformSystem.nextY += GameConfig.STEP_Y;
        float x = MathUtils.random(0f, GameConfig.VW - GameConfig.PLATFORM_W);

        Platform p = s.platformSystem.makePlatform(x, s.platformSystem.nextY, true);
        s.platformSystem.platforms.add(p);

        // Intenta generar pickups/enemigos varias veces (más intentos según nivel)
        int tries = 2 + extraSpawnAttemptsForNivel();
        for (int t = 0; t < tries; t++) {
            int enemiesBefore = s.enemySystem.enemies.size;
            s.pickupSpawner.trySpawn(p, s.coinSystem, s.enemySystem);
            clampNewEnemiesToLevel(enemiesBefore);
        }

        // Intenta generar powerups si queda libre
        spawnPowerupsOnPlatform(p);
    }

    // Intenta spawnear power-ups en una plataforma si no hay nada encima:
    // primero botas, luego escudo, luego seta (en orden de prioridad)
    private void spawnPowerupsOnPlatform(Platform p) {
        if (p == null) return;
        if (s.hasAnyThingOnPlatform(p)) return;

        s.bootsSystem.trySpawnOnPlatformIfFree(p, s.coinSystem, s.enemySystem, s.BOOTS_CHANCE, false);
        if (s.hasAnyThingOnPlatform(p)) return;

        s.shieldSystem.trySpawnOnPlatformIfFree(p, s.coinSystem, s.enemySystem, s.bootsSystem, s.SHIELD_CHANCE, false);
        if (s.hasAnyThingOnPlatform(p)) return;

        s.mushroomSystem.trySpawnOnPlatformIfFree(
            p, s.coinSystem, s.enemySystem, s.bootsSystem, s.shieldSystem,
            s.SETA_CHANCE, false
        );
    }

    // ==========================
    // FINAL MODO NIVELES
    // ==========================

    // Genera la plataforma final y la bandera en una posición visible de cámara:
    // recorta plataformas superiores, congela la cámara y muestra mensaje de final
    private void spawnGoalPlatform() {

        s.goalSpawned = true;

        float worldH = s.viewport.getWorldHeight();

        float goalX = (GameConfig.VW - GameConfig.PLATFORM_W) / 2f;

        float desiredPlatformTopOnScreen = (s.cam.position.y + worldH / 2f) - s.GOAL_TOP_MARGIN;
        float goalY = desiredPlatformTopOnScreen - GameConfig.PLATFORM_H;

        float minFinalY = s.MODE_TARGET_METERS * 100f + 120f;
        if (goalY < minFinalY) goalY = minFinalY;

        Platform p = new Platform(
            new Rectangle(goalX, goalY, GameConfig.PLATFORM_W, GameConfig.PLATFORM_H),
            false, 0f, false
        );
        p.dir = 0;

        s.platformSystem.platforms.add(p);
        s.goalPlatform = p;

        // Crea el rectángulo de la bandera para detectar colisión con el jugador
        float fw = 120f;
        float fh = 160f;
        float fx = goalX + (GameConfig.PLATFORM_W - fw) / 2f;
        float fy = goalY + GameConfig.PLATFORM_H + 10f;
        s.goalFlagRect = new Rectangle(fx, fy, fw, fh);

        // Ajusta nextY para que no sigan apareciendo plataformas por encima
        s.platformSystem.nextY = goalY;

        // Elimina plataformas que queden por encima de la meta para dejar el final limpio
        for (int i = s.platformSystem.platforms.size - 1; i >= 0; i--) {
            Platform other = s.platformSystem.platforms.get(i);
            if (other != p && other.rect.y > goalY + 20f) s.platformSystem.platforms.removeIndex(i);
        }

        // Congela cámara para encuadrar la zona final
        s.goalCamFrozen = true;

        float desiredCamTop = (goalY + GameConfig.PLATFORM_H) + s.GOAL_TOP_MARGIN;
        s.goalFrozenCamY = desiredCamTop - worldH / 2f;

        s.cam.position.y = s.goalFrozenCamY;
        s.cam.update();

        // Mensaje corto de final alcanzado
        s.levelUpMsgText = I18n.t("msg_reached_end");
        s.levelUpMsgTime = 1.5f;
        s.audio.playLevelUp();
    }

    // Marca que se alcanzó la meta:
    // activa un timer y reproduce sonido de victoria; luego se pasa a VictoryScreen
    private void triggerGoalReached() {
        if (s.goalReached) return;
        s.goalReached = true;
        s.goalTimer = s.GOAL_DELAY_TO_SCREEN;
        s.goalMsg = I18n.t("msg_win");

        s.audio.stopFondo();
        s.audio.playVictory();
    }

    // Elimina setas que ya no tienen plataforma válida (porque fue eliminada del array)
    private void cullMushroomsWithMissingPlatforms() {
        for (int i = s.mushroomSystem.mushrooms.size - 1; i >= 0; i--) {
            Mushroom m = s.mushroomSystem.mushrooms.get(i);
            if (m == null || m.platform == null || !platformStillExists(m.platform)) {
                s.mushroomSystem.mushrooms.removeIndex(i);
            }
        }
    }

    // Comprueba si una plataforma sigue existiendo en el array actual
    private boolean platformStillExists(Platform plat) {
        for (int i = 0; i < s.platformSystem.platforms.size; i++) {
            if (s.platformSystem.platforms.get(i) == plat) return true;
        }
        return false;
    }

    // ==========================
    // Colisión plataformas
    // ==========================

    // Detecta colisión del jugador con la parte superior de plataformas al caer:
    // usa “pies” estrechos, detecta cruce de la parte superior y aplica salto automático
    private void handlePlatformCollision(float oldY) {
        if (s.player == null) return;
        if (s.player.velY >= 0) return;

        float feetW = s.player.rect.width * 0.35f;
        float feetX = s.player.rect.x + (s.player.rect.width - feetW) / 2f;

        float oldFeetY = oldY;
        float newFeetY = s.player.rect.y;

        float fallDist = oldFeetY - newFeetY;
        if (fallDist <= 0f) return;

        float eps = Math.max(25f, fallDist + 5f);

        Platform best = null;
        float bestTop = -Float.MAX_VALUE;

        // Busca la mejor plataforma candidata (la más alta que se haya cruzado)
        for (int i = 0; i < s.platformSystem.platforms.size; i++) {
            Platform p = s.platformSystem.platforms.get(i);
            if (p == null || p.broken) continue;

            float platTop = p.rect.y + p.rect.height;

            boolean crossedTop = oldFeetY >= platTop && newFeetY <= platTop;
            if (!crossedTop) continue;

            boolean feetOverX = feetX < p.rect.x + p.rect.width && (feetX + feetW) > p.rect.x;
            if (!feetOverX) continue;

            if (platTop > bestTop && (Math.abs(newFeetY - platTop) <= eps)) {
                bestTop = platTop;
                best = p;
            }
        }

        if (best == null) return;

        // Coloca al jugador justo encima de la plataforma y ejecuta salto automático
        s.player.rect.y = bestTop;

        float jump = GameConfig.JUMP_VEL;

        boolean usedBoots = false;

        // Si tiene botas, aumenta el salto y consume un salto de botas
        if (s.player.consumeBootsJump()) {
            jump *= s.BOOTS_MULT;
            usedBoots = true;
        }

        s.player.velY = jump;

        // Sonidos diferentes si salta con botas o normal
        if (usedBoots) s.audio.playTenis();
        else s.audio.playJump();

        // Si la plataforma es rompible, se marca como rota para que desaparezca luego
        if (best.breakable) {
            best.broken = true;
            best.brokenTime = 0f;
        }
    }

    // ==========================
    // Enemigos por nivel
    // ==========================

    // Devuelve un tipo de enemigo permitido según el nivel visual actual
    // (al principio solo lila, luego se van habilitando más colores/tipos)
    private EnemyType randomAllowedTypeForCurrentLevel() {
        if (s.nivelVisual == 0) return EnemyType.LILA;

        if (s.nivelVisual == 1) {
            return MathUtils.randomBoolean() ? EnemyType.LILA : EnemyType.VERDE;
        }

        if (s.nivelVisual == 2) {
            int r = MathUtils.random(0, 2);
            if (r == 0) return EnemyType.LILA;
            if (r == 1) return EnemyType.VERDE;
            return EnemyType.AZUL;
        }

        int r = MathUtils.random(0, 3);
        if (r == 0) return EnemyType.LILA;
        if (r == 1) return EnemyType.VERDE;
        if (r == 2) return EnemyType.AZUL;
        return EnemyType.ROJO;
    }

    // Ajusta el tipo de los enemigos recién creados para que respeten el nivel actual
    // (se usa después del spawner para “capar” enemigos que no toquen aún)
    private void clampNewEnemiesToLevel(int startIndex) {
        for (int i = startIndex; i < s.enemySystem.enemies.size; i++) {
            Enemy e = s.enemySystem.enemies.get(i);
            if (e == null) continue;
            e.type = randomAllowedTypeForCurrentLevel();
        }
    }

    // Devuelve intentos extra de spawn según nivel visual:
    // a mayor nivel, más intentos de generar cosas en plataformas nuevas
    private int extraSpawnAttemptsForNivel() {
        switch (s.nivelVisual) {
            case 0: return 1;
            case 1: return 2;
            case 2: return 3;
            case 3: return 4;
            default: return 1;
        }
    }
}
