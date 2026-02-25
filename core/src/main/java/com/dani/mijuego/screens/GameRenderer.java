package com.dani.mijuego.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.dani.mijuego.game.I18n;
import com.dani.mijuego.game.entities.Coin;
import com.dani.mijuego.game.entities.Enemy;
import com.dani.mijuego.game.entities.EnemyType;
import com.dani.mijuego.game.entities.JumpBoots;
import com.dani.mijuego.game.entities.Mushroom;
import com.dani.mijuego.game.entities.Shield;
import com.dani.mijuego.game.world.Platform;
import com.dani.mijuego.util.FontUtils;

public class GameRenderer {

    // Referencia al GameScreen para acceder a cámara, batch, sistemas y texturas
    final GameScreen s;

    // Constructor: guarda la referencia a la pantalla de juego
    public GameRenderer(GameScreen s) {
        this.s = s;
    }

    // Método principal de dibujo: pinta el juego por capas en un orden concreto
    // (fondo -> decorado -> plataformas -> objetos -> enemigos -> jugador -> UI)
    public void draw(float worldW, float worldH) {

        drawBackground(worldW, worldH);
        drawRuins(worldW, worldH);
        drawPlatforms();
        drawGoalFlag();
        drawPickups();
        drawEnemies();
        drawPlayer();
        drawPauseButton(worldW, worldH);
        drawHud(worldW, worldH);
    }

    // Dibuja el fondo actual del nivel y, si están habilitadas, capas parallax de nubes/estrellas
    private void drawBackground(float worldW, float worldH) {

        s.drawFullScreen(s.fondoActual, worldW, worldH);

        if (s.cloudsEnabled) {
            s.drawTiledParallaxLayer(s.fondoNubes, s.CLOUDS_PARALLAX, s.CLOUDS_ALPHA, worldW, worldH);
        }

        if (s.starsEnabled) {
            Texture starsTex = (s.nivelVisual == 3 && s.fondoEstrellasColores != null)
                ? s.fondoEstrellasColores
                : s.fondoEstrellas;

            s.drawTiledParallaxLayer(starsTex, s.STARS_PARALLAX, s.STARS_ALPHA, worldW, worldH);
        }
    }

    // Dibuja el decorado de ruinas en la parte baja de la pantalla con transparencia (alpha)
    // y con posición calculada según el fondo visible
    private void drawRuins(float worldW, float worldH) {
        if (s.ruins.disabled || s.ruinasTex == null) return;

        float bottomVisible = s.cam.position.y - worldH / 2f;

        float oldA = s.batch.getColor().a;
        s.batch.setColor(1f, 1f, 1f, oldA * s.ruins.alpha);

        float ruinasY = s.ruins.computeDrawY(bottomVisible);
        s.batch.draw(
            s.ruinasTex,
            s.cam.position.x - worldW / 2f,
            ruinasY,
            worldW,
            com.dani.mijuego.game.GameConfig.RUINAS_H
        );

        s.batch.setColor(1f, 1f, 1f, oldA);
    }

    // Dibuja todas las plataformas:
    // - Si no están rotas, dibuja la textura normal
    // - Si están rotas, según nivel visual puede desaparecer o dibujar la textura rota con fade
    private void drawPlatforms() {
        for (int i = 0; i < s.platformSystem.platforms.size; i++) {
            Platform p = s.platformSystem.platforms.get(i);

            if (!p.broken) {
                if (s.plataformaActual != null) {
                    s.batch.draw(s.plataformaActual, p.rect.x, p.rect.y, p.rect.width, p.rect.height);
                }
                continue;
            }

            // Nivel 3: NO dibujar rota, solo desaparece
            if (s.nivelVisual == 2) continue;

            Texture brokenTex = brokenPlatformTex();
            if (brokenTex == null) continue;

            drawBrokenPlatform(brokenTex, p);
        }
    }

    // Devuelve la textura rota a usar según el nivel visual
    private Texture brokenPlatformTex() {
        if (s.nivelVisual == 1) return s.plataformaMediaRotaTex;
        if (s.nivelVisual == 3) return s.plataformaColoresRotaTex;
        return s.plataformaRotaTex;
    }

    // Dibuja una plataforma rota con efecto de fade-out:
    // usa brokenTime para reducir alpha hasta que desaparece
    private void drawBrokenPlatform(Texture brokenTex, Platform p) {
        float t = MathUtils.clamp(p.brokenTime / com.dani.mijuego.game.GameConfig.BROKEN_FADE_TIME, 0f, 1f);
        float a = 1f - t;

        float drawW = p.rect.width * com.dani.mijuego.game.GameConfig.BROKEN_SCALE_X;
        float drawH = p.rect.height * com.dani.mijuego.game.GameConfig.BROKEN_SCALE_Y;

        float drawX = p.rect.x + (p.rect.width - drawW) / 2f;
        float drawY = p.rect.y + (p.rect.height - drawH) / 2f;

        float oldA = s.batch.getColor().a;
        s.batch.setColor(1f, 1f, 1f, oldA * a);
        s.batch.draw(brokenTex, drawX, drawY, drawW, drawH);
        s.batch.setColor(1f, 1f, 1f, oldA);
    }

    // Dibuja la bandera objetivo si existe (modo niveles)
    private void drawGoalFlag() {
        if (s.banderaTex == null || s.goalFlagRect == null) return;
        s.batch.draw(s.banderaTex, s.goalFlagRect.x, s.goalFlagRect.y, s.goalFlagRect.width, s.goalFlagRect.height);
    }

    // Dibuja todos los objetos recogibles (monedas, botas, escudos, setas)
    // usando los sistemas correspondientes (CoinSystem, BootsSystem, etc.)
    private void drawPickups() {

        if (s.monedaTex != null) {
            for (int i = 0; i < s.coinSystem.coins.size; i++) {
                Coin c = s.coinSystem.coins.get(i);
                s.batch.draw(s.monedaTex, c.rect.x, c.rect.y, c.rect.width, c.rect.height);
            }
        }

        if (s.bootsTex != null) {
            for (int i = 0; i < s.bootsSystem.boots.size; i++) {
                JumpBoots b = s.bootsSystem.boots.get(i);
                s.drawScaledCentered(s.bootsTex, b.rect, s.BOOTS_DRAW_SCALE);
            }
        }

        if (s.shieldTex != null) {
            for (int i = 0; i < s.shieldSystem.shields.size; i++) {
                Shield sh = s.shieldSystem.shields.get(i);
                s.drawScaledCentered(s.shieldTex, sh.rect, s.SHIELD_DRAW_SCALE);
            }
        }

        if (s.setaTex != null) {
            for (int i = 0; i < s.mushroomSystem.mushrooms.size; i++) {
                Mushroom m = s.mushroomSystem.mushrooms.get(i);
                s.drawScaledCentered(s.setaTex, m.rect, s.SETA_DRAW_SCALE);
            }
        }
    }

    // Dibuja enemigos del EnemySystem, aplicando escala según el tipo de enemigo
    private void drawEnemies() {
        for (int i = 0; i < s.enemySystem.enemies.size; i++) {
            Enemy e = s.enemySystem.enemies.get(i);
            Texture t = s.enemyTexture(e.type);
            if (t == null) continue;

            float sc = com.dani.mijuego.game.systems.EnemySystem.getDrawScale(e.type);

            float drawW = e.rect.width * sc;
            float drawH = e.rect.height * sc;

            float drawX = e.rect.x + (e.rect.width - drawW) / 2f;
            float drawY = e.rect.y + (e.rect.height - drawH) / 2f;

            s.batch.draw(t, drawX, drawY, drawW, drawH);
        }
    }

    // Dibuja el jugador, eligiendo la textura final según estado:
    // normal / con escudo / con seta, y según dirección (izq/der/idle)
    private void drawPlayer() {
        if (s.player == null) return;

        Texture base = s.player.getTexture();
        Texture toDraw = base;

        if (s.player.isShieldActive()) {
            if (base == s.pIzqTex) toDraw = s.pIzqShieldTex;
            else if (base == s.pDerTex) toDraw = s.pDerShieldTex;
            else toDraw = s.pIdleShieldTex;
        } else if (s.player.isSetaActive()) {
            if (base == s.pIzqTex) toDraw = s.pIzqSetaTex;
            else if (base == s.pDerTex) toDraw = s.pDerSetaTex;
            else toDraw = s.pIdleSetaTex;
        }

        if (toDraw != null) {
            s.batch.draw(toDraw, s.player.rect.x, s.player.rect.y, s.player.rect.width, s.player.rect.height);
        }
    }

    // Dibuja el botón de pausa en coordenadas de interfaz (HUD)
    private void drawPauseButton(float worldW, float worldH) {
        if (s.btnPauseTex == null || s.btnPause == null) return;

        float uiLeft = s.cam.position.x - worldW / 2f;
        float uiBottom = s.cam.position.y - worldH / 2f;

        s.batch.draw(
            s.btnPauseTex,
            uiLeft + s.btnPause.x,
            uiBottom + s.btnPause.y,
            s.btnPause.width,
            s.btnPause.height
        );
    }

    // Dibuja el HUD: monedas, iconos de power-ups activos, altura actual,
    // mensajes temporales (level up, objetivo) y el “pulsa para empezar”
    private void drawHud(float worldW, float worldH) {
        float uiLeft = s.cam.position.x - worldW / 2f;
        float uiBottom = s.cam.position.y - worldH / 2f;

        // Bloque de monedas + iconos de powerups a la derecha del HUD
        if (s.monedaTex != null && s.btnPause != null) {
            float coinSize = 100f;
            float coinY = uiBottom + s.btnPause.y + (s.btnPause.height - coinSize) / 2f;

            // Dibuja el texto "x N" y calcula el ancho del bloque para alinear a la derecha
            s.font.getData().setScale(4f);
            String txt = "x " + s.coinSystem.collected;
            s.layout.setText(s.font, txt);

            float gap = 14f;

            float blockRight = uiLeft + s.btnPause.x - 18f;
            float blockW = coinSize + gap + s.layout.width;

            float coinX = blockRight - blockW;
            float textX = coinX + coinSize + gap;

            float textY = coinY + coinSize * 0.70f;

            // Dibuja moneda y contador
            s.batch.draw(s.monedaTex, coinX, coinY, coinSize, coinSize);

            // Decide qué iconos mostrar según el estado del jugador
            boolean showBoots  = (s.player != null && s.player.getBootsJumpsLeft() > 0);
            boolean showShield = (s.player != null && s.player.isShieldActive());
            boolean showSeta   = (s.player != null && s.player.isSetaActive());

            int count = 0;
            if (showShield && s.shieldTex != null) count++;
            if (showBoots && s.bootsTex != null) count++;
            if (showSeta && s.setaTex != null) count++;

            // Si hay power-ups activos, dibuja iconos a la izquierda del bloque de monedas
            if (count > 0) {
                float totalIconsW = count * s.POWER_ICON_SIZE + (count - 1) * s.POWER_ICON_GAP;
                float startX = coinX - 18f - totalIconsW;

                float coinCenterY = coinY + coinSize / 2f;
                float iconY = coinCenterY - s.POWER_ICON_SIZE / 2f;

                float x = startX;

                if (showShield && s.shieldTex != null) {
                    s.batch.draw(s.shieldTex, x, iconY, s.POWER_ICON_SIZE, s.POWER_ICON_SIZE);
                    x += s.POWER_ICON_SIZE + s.POWER_ICON_GAP;
                }
                if (showBoots && s.bootsTex != null) {
                    s.batch.draw(s.bootsTex, x, iconY, s.POWER_ICON_SIZE, s.POWER_ICON_SIZE);
                    x += s.POWER_ICON_SIZE + s.POWER_ICON_GAP;
                }
                if (showSeta && s.setaTex != null) {
                    s.batch.draw(s.setaTex, x, iconY, s.POWER_ICON_SIZE, s.POWER_ICON_SIZE);
                }
            }

            // Dibuja el texto del contador de monedas y restaura escala
            s.font.setColor(1f, 1f, 1f, 1f);
            s.font.draw(s.batch, s.layout, textX, textY);
            s.font.getData().setScale(1f);
        }

        // Bloque de altura/puntuación en la esquina superior izquierda
        float margin = 70f;
        float textX = uiLeft + margin;
        float textY = uiBottom + worldH - margin;

        s.font.getData().setScale(3.5f);
        String scoreText = I18n.t("hud_height") + " " + s.score + " m";
        s.layout.setText(s.font, scoreText);

        s.font.setColor(1f, 1f, 1f, 1f);
        s.font.draw(s.batch, s.layout, textX, textY);
        s.font.getData().setScale(1f);

        // Mensaje temporal de subida de nivel (aparece y se desvanece con alpha)
        if (s.levelUpMsgTime > 0f && s.levelUpMsgText != null) {
            float t = s.levelUpMsgTime / s.LEVEL_UP_MSG_DURATION;
            float alpha = MathUtils.clamp(t, 0f, 1f);

            float cx = s.cam.position.x;
            float cy = s.cam.position.y;

            float scale = 2.2f;

            s.startOutlineFont.getData().setScale(scale);
            s.startFillFont.getData().setScale(scale);

            s.startOutlineFont.setColor(0f, 0f, 0f, alpha);
            s.startFillFont.setColor(1f, 1f, 1f, alpha);

            s.layout.setText(s.startFillFont, s.levelUpMsgText);

            float x = cx - s.layout.width / 2f;
            float y = cy + s.layout.height / 2f;

            FontUtils.drawOutlined(s.batch, s.startOutlineFont, s.startFillFont, s.levelUpMsgText, x, y, 3.5f);

            s.startOutlineFont.setColor(Color.BLACK);
            s.startFillFont.setColor(Color.WHITE);
        }

        // Mensaje de objetivo alcanzado (modo niveles) centrado en pantalla
        if (s.goalReached && s.goalMsg != null) {
            float cx = s.cam.position.x;
            float cy = s.cam.position.y + 140f;

            float scale = 2.6f;
            s.startOutlineFont.getData().setScale(scale);
            s.startFillFont.getData().setScale(scale);

            s.startOutlineFont.setColor(0f, 0f, 0f, 1f);
            s.startFillFont.setColor(1f, 1f, 1f, 1f);

            s.layout.setText(s.startFillFont, s.goalMsg);
            float x = cx - s.layout.width / 2f;
            float y = cy + s.layout.height / 2f;

            FontUtils.drawOutlined(s.batch, s.startOutlineFont, s.startFillFont, s.goalMsg, x, y, 3.5f);

            s.startOutlineFont.setColor(Color.BLACK);
            s.startFillFont.setColor(Color.WHITE);
        }

        // Mensaje inicial “pulsa/toca para empezar” cuando aún no se ha iniciado la partida
        if (!s.started) {
            s.startAnimTime += com.badlogic.gdx.Gdx.graphics.getDeltaTime();

            float alpha = 0.65f + 0.35f * MathUtils.sin(s.startAnimTime * 4f);
            float scale = 2.8f + 0.12f * MathUtils.sin(s.startAnimTime * 4f);

            String t = I18n.t("hud_press_start");

            s.startOutlineFont.getData().setScale(scale);
            s.startFillFont.getData().setScale(scale);

            s.startOutlineFont.setColor(0f, 0f, 0f, alpha);
            s.startFillFont.setColor(1f, 1f, 1f, alpha);

            s.layout.setText(s.startFillFont, t);

            float x = s.cam.position.x - s.layout.width / 2f;
            float y = s.cam.position.y - 170f;

            FontUtils.drawOutlined(s.batch, s.startOutlineFont, s.startFillFont, t, x, y, 3.5f);

            s.startOutlineFont.setColor(Color.BLACK);
            s.startFillFont.setColor(Color.WHITE);
        }
    }
}
