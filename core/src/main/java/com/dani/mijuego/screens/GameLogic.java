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

    final GameScreen s;

    public GameLogic(GameScreen s) {
        this.s = s;
        resetWorld();
    }

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

        s.cam.position.set(GameConfig.VW / 2f, GameConfig.VH / 2f, 0);
        s.cam.update();

        float worldH = s.viewport.getWorldHeight();
        float bottomVisible = s.cam.position.y - worldH / 2f;
        s.groundY = bottomVisible;

        s.ruins.reset(s.groundY);

        float firstPlatformX = (GameConfig.VW - GameConfig.PLATFORM_W) / 2f;
        float firstPlatformY = s.groundY + 90f;

        Platform first = new Platform(
            new Rectangle(firstPlatformX, firstPlatformY, GameConfig.PLATFORM_W, GameConfig.PLATFORM_H),
            false, 0f, false
        );
        first.dir = 1;
        s.platformSystem.platforms.add(first);

        s.player = new com.dani.mijuego.game.entities.Player(
            (GameConfig.VW - GameConfig.PLAYER_W) / 2f,
            firstPlatformY + GameConfig.PLATFORM_H,
            s.pIdleTex, s.pIzqTex, s.pDerTex
        );

        s.maxY = s.player.rect.y;

        float y = firstPlatformY;

        for (int i = 0; i < 10; i++) {
            y += GameConfig.STEP_Y;
            float x = MathUtils.random(0f, GameConfig.VW - GameConfig.PLATFORM_W);

            Platform p = s.platformSystem.makePlatform(x, y, true);
            s.platformSystem.platforms.add(p);

            int tries = 2 + extraSpawnAttemptsForNivel();
            for (int t = 0; t < tries; t++) {
                int enemiesBefore = s.enemySystem.enemies.size;
                s.pickupSpawner.trySpawn(p, s.coinSystem, s.enemySystem);
                clampNewEnemiesToLevel(enemiesBefore);
                if (s.hasAnyThingOnPlatform(p)) break;
            }

            spawnPowerupsOnPlatform(p);
        }

        s.platformSystem.nextY = y;

        s.cam.position.set(GameConfig.VW / 2f, GameConfig.VH / 2f, 0);
        s.cam.update();
    }

    void update(float dt) {

        if (s.goalReached) {
            s.goalTimer -= dt;
            if (s.goalTimer <= 0f) s.finishVictory();
            return;
        }

        if (s.player != null) {
            s.player.updateHorizontal(dt);

            float w = s.player.rect.width;
            float worldW = GameConfig.VW;

            if (s.player.rect.x + w < 0f) s.player.rect.x = worldW;
            else if (s.player.rect.x > worldW) s.player.rect.x = -w;
        }

        if (s.dying) {
            updateDying(dt);
            return;
        }

        if (s.started) s.runTimeSec += dt;

        s.platformSystem.updateMoving(dt);

        if (s.started && s.player != null) {
            updatePlayerVertical(dt);
        }

        s.platformSystem.updateBreakables(dt);

        if (s.levelUpMsgTime > 0f) {
            s.levelUpMsgTime -= dt;
            if (s.levelUpMsgTime < 0f) s.levelUpMsgTime = 0f;
        }

        updateCamera(dt);

        float worldH = s.viewport.getWorldHeight();
        float bottomVisible = s.cam.position.y - worldH / 2f;
        s.ruins.update(dt, bottomVisible);

        boolean canSpawnMore = !(s.gameMode == com.dani.mijuego.game.GameMode.LEVELS && s.goalSpawned);
        if (canSpawnMore) {
            float topVisible = s.cam.position.y + worldH / 2f + 300f;
            while (s.platformSystem.nextY < topVisible) spawnPlatformAbove();
        }

        float killY = bottomVisible - 200f;

        if (s.player != null) s.player.updateEffects(dt);

        s.coinSystem.update(s.player, killY, s.onCoinCollected);
        s.bootsSystem.update(s.player, killY, s.onBootsCollected);
        s.shieldSystem.update(s.player, killY, s.onShieldCollected);
        s.mushroomSystem.update(s.player, killY, s.onMushroomCollected);
        s.enemySystem.update(s.player, killY, s.onEnemyHit);

        float keepBelow = 3f * GameConfig.STEP_Y + 250f;
        float cullY = s.player.rect.y - keepBelow;
        cullY = Math.min(cullY, bottomVisible - 200f);
        s.platformSystem.cullBelow(cullY);

        cullMushroomsWithMissingPlatforms();

        if (s.gameMode == com.dani.mijuego.game.GameMode.LEVELS && s.goalSpawned && s.goalFlagRect != null && s.player != null && !s.goalReached) {
            if (s.player.rect.overlaps(s.goalFlagRect)) {
                triggerGoalReached();
                return;
            }
        }

        if (s.started && s.player != null && !s.dying) {
            if (s.player.rect.y < killY - s.HARD_DEATH_EXTRA) {
                beginDying(s.HARD_DEATH_DURATION, false);
            }
        }
    }

    private void updateDying(float dt) {
        if (s.player != null) {
            float g = GameConfig.GRAVITY;

            if (s.voidFalling) g *= 2.8f;

            s.player.velY -= g * dt;
            s.player.rect.y += s.player.velY * dt;
        }

        s.cam.position.x = GameConfig.VW / 2f;
        if (s.player != null) {
            float targetY = s.player.rect.y + s.CAM_Y_OFFSET;
            float a = 1f - (float) Math.exp(-s.CAM_FOLLOW_SPEED * dt);
            s.cam.position.y = MathUtils.lerp(s.cam.position.y, targetY, a);
        }
        s.cam.update();

        s.dyingTimer -= dt;
        if (s.dyingTimer <= 0f) s.finishDying();
    }

    private void updatePlayerVertical(float dt) {

        float oldY = s.player.rect.y;

        if (s.player.velY < 0f) {
            s.player.velY -= GameConfig.GRAVITY * s.FALL_MULT * dt;
            if (s.player.velY < -s.MAX_FALL_SPEED) s.player.velY = -s.MAX_FALL_SPEED;
        } else {
            s.player.velY -= GameConfig.GRAVITY * dt;
        }

        s.player.rect.y += s.player.velY * dt;

        handlePlatformCollision(oldY);

        boolean canTriggerVoidFall = (s.player.velY < 0f)
            && !s.dying
            && !s.goalReached
            && !(s.gameMode == com.dani.mijuego.game.GameMode.LEVELS && s.goalSpawned);

        if (canTriggerVoidFall && !hasPlatformBelowPlayer(s.FALL_CHECK_RANGE)) {
            beginDying(s.VOID_FALL_DURATION, true);
            return;
        }

        if (s.player.rect.y > s.maxY) {
            s.maxY = s.player.rect.y;
            s.score = (int) (s.maxY / 100f);

            if (s.gameMode == com.dani.mijuego.game.GameMode.LEVELS && s.goalSpawned) {
                if (s.score > s.MODE_TARGET_METERS) s.score = s.MODE_TARGET_METERS;
            }
        }

        if (s.gameMode == com.dani.mijuego.game.GameMode.LEVELS) {
            if (s.score >= s.MODE_TARGET_METERS && !s.goalSpawned) spawnGoalPlatform();
            if (!s.goalSpawned) applyLevelByMeters(s.score, true);
        } else {

            int newLap = s.score / s.MODE_TARGET_METERS;
            if (newLap != s.lapIndex) {
                s.lapIndex = newLap;
                s.levelUpMsgText = I18n.t("msg_new_lap");
                s.levelUpMsgTime = s.LEVEL_UP_MSG_DURATION;
                s.audio.playLevelUp();
                s.setNivelVisual(0, false);
            }

            int lapMeters = s.score % s.MODE_TARGET_METERS;
            applyLevelByMeters(lapMeters, true);
        }
    }

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

    private void applyLevelByMeters(int meters, boolean playSfx) {
        if (meters >= 600) s.setNivelVisual(3, playSfx);
        else if (meters >= 400) s.setNivelVisual(2, playSfx);
        else if (meters >= 200) s.setNivelVisual(1, playSfx);
        else s.setNivelVisual(0, false);
    }

    // ==========================
    // SPAWN
    // ==========================
    private void spawnPlatformAbove() {
        s.platformSystem.nextY += GameConfig.STEP_Y;
        float x = MathUtils.random(0f, GameConfig.VW - GameConfig.PLATFORM_W);

        Platform p = s.platformSystem.makePlatform(x, s.platformSystem.nextY, true);
        s.platformSystem.platforms.add(p);

        int tries = 2 + extraSpawnAttemptsForNivel();
        for (int t = 0; t < tries; t++) {
            int enemiesBefore = s.enemySystem.enemies.size;
            s.pickupSpawner.trySpawn(p, s.coinSystem, s.enemySystem);
            clampNewEnemiesToLevel(enemiesBefore);
        }

        spawnPowerupsOnPlatform(p);
    }

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

        float fw = 120f;
        float fh = 160f;
        float fx = goalX + (GameConfig.PLATFORM_W - fw) / 2f;
        float fy = goalY + GameConfig.PLATFORM_H + 10f;
        s.goalFlagRect = new Rectangle(fx, fy, fw, fh);

        s.platformSystem.nextY = goalY;

        for (int i = s.platformSystem.platforms.size - 1; i >= 0; i--) {
            Platform other = s.platformSystem.platforms.get(i);
            if (other != p && other.rect.y > goalY + 20f) s.platformSystem.platforms.removeIndex(i);
        }

        s.goalCamFrozen = true;

        float desiredCamTop = (goalY + GameConfig.PLATFORM_H) + s.GOAL_TOP_MARGIN;
        s.goalFrozenCamY = desiredCamTop - worldH / 2f;

        s.cam.position.y = s.goalFrozenCamY;
        s.cam.update();

        s.levelUpMsgText = I18n.t("msg_reached_end");
        s.levelUpMsgTime = 1.5f;
        s.audio.playLevelUp();
    }

    private void triggerGoalReached() {
        if (s.goalReached) return;
        s.goalReached = true;
        s.goalTimer = s.GOAL_DELAY_TO_SCREEN;
        s.goalMsg = I18n.t("msg_win");

        s.audio.stopFondo();
        s.audio.playVictory();
    }

    private void cullMushroomsWithMissingPlatforms() {
        for (int i = s.mushroomSystem.mushrooms.size - 1; i >= 0; i--) {
            Mushroom m = s.mushroomSystem.mushrooms.get(i);
            if (m == null || m.platform == null || !platformStillExists(m.platform)) {
                s.mushroomSystem.mushrooms.removeIndex(i);
            }
        }
    }

    private boolean platformStillExists(Platform plat) {
        for (int i = 0; i < s.platformSystem.platforms.size; i++) {
            if (s.platformSystem.platforms.get(i) == plat) return true;
        }
        return false;
    }

    // ==========================
    // Colisión plataformas
    // ==========================
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

        s.player.rect.y = bestTop;

        float jump = GameConfig.JUMP_VEL;

        boolean usedBoots = false;

        if (s.player.consumeBootsJump()) {
            jump *= s.BOOTS_MULT;
            usedBoots = true;
        }

        s.player.velY = jump;

        if (usedBoots) s.audio.playTenis();
        else s.audio.playJump();

        if (best.breakable) {
            best.broken = true;
            best.brokenTime = 0f;
        }
    }

    // ==========================
    // Enemigos por nivel
    // ==========================
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

    private void clampNewEnemiesToLevel(int startIndex) {
        for (int i = startIndex; i < s.enemySystem.enemies.size; i++) {
            Enemy e = s.enemySystem.enemies.get(i);
            if (e == null) continue;
            e.type = randomAllowedTypeForCurrentLevel();
        }
    }

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
