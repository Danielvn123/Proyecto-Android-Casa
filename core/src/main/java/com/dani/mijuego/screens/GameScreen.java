package com.dani.mijuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameAudio;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.GameSave;
import com.dani.mijuego.game.I18n;
import com.dani.mijuego.game.entities.Coin;
import com.dani.mijuego.game.entities.Enemy;
import com.dani.mijuego.game.entities.EnemyType;
import com.dani.mijuego.game.entities.JumpBoots;
import com.dani.mijuego.game.entities.Mushroom;
import com.dani.mijuego.game.entities.Player;
import com.dani.mijuego.game.entities.Shield;
import com.dani.mijuego.game.systems.CoinSystem;
import com.dani.mijuego.game.systems.EnemySystem;
import com.dani.mijuego.game.systems.JumpBootsSystem;
import com.dani.mijuego.game.systems.PickupSpawner;
import com.dani.mijuego.game.systems.ShieldSystem;
import com.dani.mijuego.game.world.Platform;
import com.dani.mijuego.game.world.PlatformSystem;
import com.dani.mijuego.game.world.RuinsLayer;
import com.dani.mijuego.util.FontUtils;

public class GameScreen extends BaseScreen {

    // ==========================
    // MODOS
    // ==========================
    public enum GameMode { INFINITE, LEVELS }
    private final GameMode gameMode;

    private static final int MODE_TARGET_METERS = 800; // final modo niveles + vuelta en infinito
    private int lapIndex = 0;

    // ==========================
    // Fonts / HUD
    // ==========================
    private BitmapFont startFillFont;
    private BitmapFont startOutlineFont;

    // Caída más rápida que la subida
    private static final float FALL_MULT = 2.0f;      // prueba 1.8..2.6
    private static final float MAX_FALL_SPEED = 3200f; // cap (en unidades de tu juego)


    private Texture btnPauseTex;
    private Rectangle btnPause;

    private static final float VOID_FALL_DURATION = 4.0f;     // ✅ 3 segundos cayendo al vacío
    private static final float HARD_DEATH_DURATION = 0.35f;   // como lo tenías antes

    private boolean voidFalling = false;   // ✅ estamos en caída libre final
    private boolean playedVoidFallSfx = false;


    // ==========================
    // Fondos base
    // ==========================
    private Texture fondoRosa;
    private Texture fondoAzul;
    private Texture fondoAzulOscuro;
    private Texture fondoAmarillo;
    private Texture fondoActual;

    // NUBES (nivel 1 y 2) - layer
    private Texture fondoNubes;
    private boolean cloudsEnabled = false;
    private static final float CLOUDS_PARALLAX = 0.25f;
    private static final float CLOUDS_ALPHA = 0.90f;

    // ESTRELLAS (nivel 3 y 4) - layer
    private Texture fondoEstrellas;
    private boolean starsEnabled = false;
    private static final float STARS_PARALLAX = 0.18f;
    private static final float STARS_ALPHA = 0.95f;

    // ==========================
    // Plataformas
    // ==========================
    private Texture plataformaRuinas;
    private Texture plataformaMedia;
    private Texture plataformaModerna;

    private Texture plataformaRotaTex;       // nivel 1
    private Texture plataformaMediaRotaTex;  // nivel 2
    private Texture plataformaColores;       // nivel 4
    private Texture plataformaColoresRotaTex;// nivel 4 rota

    private Texture plataformaActual;

    // ==========================
    // Decor / Coins
    // ==========================
    private Texture ruinasTex;
    private Texture monedaTex;

    // ==========================
    // Enemigos
    // ==========================
    private Texture enemyLilaTex;
    private Texture enemyAzulTex;
    private Texture enemyVerdeTex;
    private Texture enemyRojoTex;

    // ==========================
    // Player
    // ==========================
    private Texture pIdleTex;
    private Texture pIzqTex;
    private Texture pDerTex;

    // Escudo skins
    private Texture pIdleShieldTex;
    private Texture pIzqShieldTex;
    private Texture pDerShieldTex;

    // ==========================
    // Tenis
    // ==========================
    private Texture bootsTex;
    private final JumpBootsSystem bootsSystem = new JumpBootsSystem();
    private int bootsJumpsLeft = 0;
    private static final float BOOTS_MULT = 1.9f;
    private static final float BOOTS_CHANCE = 0.06f;
    private static final float BOOTS_DRAW_SCALE = 1.6f;

    private float runTimeSec = 0f;

    // ==========================
    // Escudo
    // ==========================
    private Texture shieldTex;
    private final ShieldSystem shieldSystem = new ShieldSystem();
    private boolean shieldActive = false;
    private static final float SHIELD_CHANCE = 0.05f;
    private static final float SHIELD_DRAW_SCALE = 1.6f;

    // ==========================
    // SETA
    // ==========================
    private Texture setaTex;
    private Texture pIdleSetaTex;
    private Texture pIzqSetaTex;
    private Texture pDerSetaTex;

    private final Array<Mushroom> mushrooms = new Array<>();
    private static final float SETA_CHANCE = 0.04f;
    private static final float SETA_DURATION = 5.0f;
    private static final float SETA_DRAW_SCALE = 1.6f;

    private boolean setaActive = false;
    private float setaTimer = 0f;

    // ==========================
    // FINAL MODO NIVELES (plataforma + bandera)
    // ==========================
    private Texture banderaTex;

    private boolean goalSpawned = false;   // ya existe el final
    private boolean goalReached = false;   // tocó la bandera

    private Platform goalPlatform = null;
    private Rectangle goalFlagRect = null;

    private boolean goalCamFrozen = false;
    private float goalFrozenCamY = 0f;

    private float goalTimer = 0f;
    private static final float GOAL_DELAY_TO_SCREEN = 2.0f;

    private static final float GOAL_TOP_MARGIN = 180f; // margen para dejar la plataforma arriba del todo
    private String goalMsg = null;

    // ==========================
    // HUD Power Icons
    // ==========================
    private static final float POWER_ICON_SIZE = 110f;
    private static final float POWER_ICON_GAP  = 12f;

    // ==========================
    // World / Systems
    // ==========================
    private Player player;
    private final PlatformSystem platformSystem = new PlatformSystem();
    private final RuinsLayer ruins = new RuinsLayer();

    private final CoinSystem coinSystem = new CoinSystem();
    private final EnemySystem enemySystem = new EnemySystem();
    private final PickupSpawner pickupSpawner = new PickupSpawner();

    private final GameAudio audio = new GameAudio();

    private boolean started = false;

    private boolean initialized = false;
    private float maxY = 0f;
    private int score = 0;

    /**
     * NIVEL (0..3):
     * 0: lila
     * 1: lila + verde
     * 2: lila + verde + azul
     * 3: lila + verde + azul + rojo
     */
    private int nivelVisual = 0;

    private float levelUpMsgTime = 0f;
    private static final float LEVEL_UP_MSG_DURATION = 2.0f;
    private String levelUpMsgText = null;

    private float startAnimTime = 0f;

    // ===== Muerte / transición =====
    private boolean dying = false;
    private float dyingTimer = 0f;
    private static final float FAST_DYING_DURATION = 4.0f;

    private float frozenCamY = 0f;

    // ===== Cámara =====
    private float groundY = 0f;
    private static final float CAM_Y_OFFSET = 500f;
    private static final float CAM_FOLLOW_SPEED = 10f;

    // ===== Regla de “definitivo” =====
    private static final float HARD_DEATH_EXTRA = 300f;
    private static final float FALL_CHECK_RANGE = 1400f;

    public GameScreen(Main game, GameMode mode) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.gameMode = (mode == null) ? GameMode.LEVELS : mode;
    }

    public GameScreen(Main game) {
        this(game, GameMode.LEVELS);
    }

    @Override
    public void show() {

        // ✅ Si ya estabas jugando y vuelves del PauseScreen:
        // NO reinicies nada, solo re-instala input y recalcula UI.
        if (initialized) {
            updateUiPositions();
            installInput();
            return;
        }
        initialized = true;

        font = new BitmapFont();
        startFillFont = new BitmapFont();
        startOutlineFont = new BitmapFont();
        layout = new GlyphLayout();

        startFillFont.setColor(1f, 0.90f, 0.10f, 1f);
        startOutlineFont.setColor(0f, 0f, 0f, 1f);

        // Fondos base
        fondoRosa = getTex(Assets.FONDO_NARANJA);
        fondoAzul = getTex(Assets.FONDO_AZUL);
        fondoAzulOscuro = getTex(Assets.FONDO_LILA);
        fondoAmarillo = getTex(Assets.FONDO_AMARILLO);

        // Layers
        fondoNubes = getTex(Assets.NUBES);
        fondoEstrellas = getTex(Assets.ESTRELLAS);

        // Plataformas
        plataformaRuinas = getTex(Assets.PLAT_RUINAS);
        plataformaMedia = getTex(Assets.PLAT_MEDIA);
        plataformaModerna = getTex(Assets.PLAT_MODERNA);

        plataformaRotaTex = getTex(Assets.PLAT_ROTA);
        plataformaMediaRotaTex = getTex(Assets.PLAT_MEDIAROTA);

        plataformaColores = getTex(Assets.PLAT_COLORES);
        plataformaColoresRotaTex = getTex(Assets.PLAT_COLORES_ROTA);

        ruinasTex = getTex(Assets.RUINAS);

        // Player
        pIdleTex = getTex(Assets.PLAYER_IDLE);
        pIzqTex  = getTex(Assets.PLAYER_IZQ);
        pDerTex  = getTex(Assets.PLAYER_DER);

        // Moneda
        monedaTex = getTex(Assets.MONEDA);

        // Enemigos
        enemyAzulTex  = getTex(Assets.BICHOAZUL);
        enemyLilaTex  = getTex(Assets.BICHOLILA);
        enemyVerdeTex = getTex(Assets.BICHOVERDE);
        enemyRojoTex  = getTex(Assets.BICHOROJO);

        // Pause
        btnPauseTex = getTex(Assets.BTN_PAUSE);

        // Powerups
        bootsTex  = getTex(Assets.ZAPATOS);
        shieldTex = getTex(Assets.ESCUDO);

        pIdleShieldTex = getTex(Assets.PLAYER_ESCUDO_IDLE);
        pIzqShieldTex  = getTex(Assets.PLAYER_ESCUDO_IZQ);
        pDerShieldTex  = getTex(Assets.PLAYER_ESCUDO_DER);

        // SETA
        setaTex = getTex(Assets.SETA);
        pIdleSetaTex = getTex("personaje/personajeseta.png");
        pIzqSetaTex  = getTex("personaje/personajesetaizquierda.png");
        pDerSetaTex  = getTex("personaje/personajesetaderecha.png");

        // BANDERA
        banderaTex = getTex(Assets.BANDERA);

        // Requires (si algo falta, te lo canta al arrancar)
        require(fondoRosa, "FONDO_ROSA");
        require(fondoAzul, "FONDO_AZUL");
        require(fondoAzulOscuro, "FONDO_AZULOSCURO");
        require(fondoAmarillo, "FONDO_AMARILLO");

        require(fondoNubes, "NUBES");
        require(fondoEstrellas, "ESTRELLAS");

        require(plataformaRuinas, "PLAT_RUINAS");
        require(plataformaMedia, "PLAT_MEDIA");
        require(plataformaModerna, "PLAT_MODERNA");
        require(plataformaRotaTex, "PLAT_ROTA");
        require(plataformaMediaRotaTex, "PLAT_MEDIAROTA");

        require(plataformaColores, "PLAT_COLORES");
        require(plataformaColoresRotaTex, "PLAT_COLORES_ROTA");

        require(pIdleTex, "PLAYER_IDLE");
        require(pIzqTex, "PLAYER_IZQ");
        require(pDerTex, "PLAYER_DER");

        require(monedaTex, "MONEDA");
        require(btnPauseTex, "BTN_PAUSE");
        require(ruinasTex, "RUINAS");

        require(bootsTex, "ZAPATOS");
        require(shieldTex, "ESCUDO");

        require(pIdleShieldTex, "PLAYER_ESCUDO_IDLE");
        require(pIzqShieldTex, "PLAYER_ESCUDO_IZQ");
        require(pDerShieldTex, "PLAYER_ESCUDO_DER");

        require(enemyLilaTex, "BICHOLILA");
        require(enemyVerdeTex, "BICHOVERDE");
        require(enemyAzulTex, "BICHOAZUL");
        require(enemyRojoTex, "BICHOROJO");

        require(setaTex, "SETA");
        require(pIdleSetaTex, "personajeseta");
        require(pIzqSetaTex, "personajesetaizquierda");
        require(pDerSetaTex, "personajesetaderecha");

        require(banderaTex, "BANDERA");

        audio.load();

        nivelVisual = -1;
        lapIndex = 0;
        setNivelVisual(0, false);

        resetWorld();
        updateUiPositions();
        installInput();
    }

    private Texture getTex(String path) {
        try {
            if (game.assets.manager.isLoaded(path, Texture.class)) {
                return game.assets.manager.get(path, Texture.class);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private void require(Texture t, String name) {
        if (t == null) throw new RuntimeException("TEXTURA NULL: " + name);
    }

    private void drawIfNotNull(Texture t, float x, float y, float w, float h) {
        if (t != null) batch.draw(t, x, y, w, h);
    }

    private void resetWorld() {
        started = false;
        score = 0;
        maxY = 0f;

        dying = false;
        dyingTimer = 0f;
        frozenCamY = 0f;

        lapIndex = 0;

        bootsSystem.reset();
        shieldSystem.reset();
        bootsJumpsLeft = 0;
        shieldActive = false;

        mushrooms.clear();
        setaActive = false;
        setaTimer = 0f;

        coinSystem.coins.clear();
        coinSystem.collected = 0;

        enemySystem.enemies.clear();
        pickupSpawner.reset();

        runTimeSec = 0f;

        platformSystem.platforms.clear();

        // Final reset
        goalSpawned = false;
        goalReached = false;
        goalPlatform = null;
        goalFlagRect = null;
        goalTimer = 0f;
        goalMsg = null;

        goalCamFrozen = false;
        goalFrozenCamY = 0f;

        cam.position.set(GameConfig.VW / 2f, GameConfig.VH / 2f, 0);
        cam.update();

        float worldH = viewport.getWorldHeight();
        float bottomVisible = cam.position.y - worldH / 2f;
        groundY = bottomVisible;

        ruins.reset(groundY);

        float firstPlatformX = (GameConfig.VW - GameConfig.PLATFORM_W) / 2f;
        float firstPlatformY = groundY + 90f;

        Platform first = new Platform(
            new Rectangle(firstPlatformX, firstPlatformY, GameConfig.PLATFORM_W, GameConfig.PLATFORM_H),
            false, 0f, false
        );
        first.dir = 1;
        platformSystem.platforms.add(first);

        // ✅ empieza tocando la plataforma
        player = new Player(
            (GameConfig.VW - GameConfig.PLAYER_W) / 2f,
            firstPlatformY + GameConfig.PLATFORM_H,
            pIdleTex, pIzqTex, pDerTex
        );

        maxY = player.rect.y;

        float y = firstPlatformY;

        // Spawn inicial hacia arriba
        for (int i = 0; i < 10; i++) {
            y += GameConfig.STEP_Y;
            float x = MathUtils.random(0f, GameConfig.VW - GameConfig.PLATFORM_W);

            Platform p = platformSystem.makePlatform(x, y, true);
            platformSystem.platforms.add(p);

            int tries = 2 + extraSpawnAttemptsForNivel(); // 🔥 +1 intento extra siempre
            for (int t = 0; t < tries; t++) {
                int enemiesBefore = enemySystem.enemies.size;
                pickupSpawner.trySpawn(p, coinSystem, enemySystem);
                clampNewEnemiesToLevel(enemiesBefore);
                if (hasAnyThingOnPlatform(p)) break;
            }

            bootsSystem.trySpawnOnPlatformIfFree(p, coinSystem, enemySystem, BOOTS_CHANCE, false);
            shieldSystem.trySpawnOnPlatformIfFree(p, coinSystem, enemySystem, bootsSystem, SHIELD_CHANCE, false);
            trySpawnMushroomOnPlatformIfFree(p, SETA_CHANCE);
        }

        platformSystem.nextY = y;

        cam.position.set(GameConfig.VW / 2f, GameConfig.VH / 2f, 0);
        cam.update();
    }

    private void startGameIfNeeded() {
        if (started) return;
        started = true;
        player.velY = GameConfig.JUMP_VEL;
    }

    private void installInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
                    audio.playSelectButton();
                    game.setScreen(new PauseScreen(game, GameScreen.this, audio));
                    return true;
                }

                if (!started && (keycode == Input.Keys.SPACE || keycode == Input.Keys.ENTER)) {
                    startGameIfNeeded();
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 hud = unprojectToHud(screenX, screenY);

                if (btnPause != null && btnPause.contains(hud.x, hud.y)) {
                    audio.playSelectButton();
                    game.setScreen(new PauseScreen(game, GameScreen.this, audio));
                    return true;
                }

                startGameIfNeeded();
                return true;
            }
        });
    }

    private void updateUiPositions() {
        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        float pauseSize = 150f;
        float margin = 12f;

        btnPause = new Rectangle(
            worldW - pauseSize - margin,
            worldH - pauseSize - margin,
            pauseSize,
            pauseSize
        );
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        updateUiPositions();
    }

    @Override
    public void render(float delta) {
        float dt = Math.min(delta, 1f / 30f);
        update(dt);

        ScreenUtils.clear(0, 0, 0, 1);
        viewport.apply();
        batch.setProjectionMatrix(cam.combined);

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        batch.begin();

        // 1) fondo base
        drawIfNotNull(
            fondoActual,
            cam.position.x - worldW / 2f,
            cam.position.y - worldH / 2f,
            worldW,
            worldH
        );

        // 2) layer nubes / estrellas
        if (cloudsEnabled) drawCloudsLayer(worldW, worldH);
        if (starsEnabled)  drawStarsLayer(worldW, worldH);

        // 3) ruinas
        if (!ruins.disabled && ruinasTex != null) {
            float bottomVisible = cam.position.y - worldH / 2f;

            float oldA = batch.getColor().a;
            batch.setColor(1f, 1f, 1f, oldA * ruins.alpha);

            float ruinasY = ruins.computeDrawY(bottomVisible);
            batch.draw(
                ruinasTex,
                cam.position.x - worldW / 2f,
                ruinasY,
                worldW,
                GameConfig.RUINAS_H
            );

            batch.setColor(1f, 1f, 1f, oldA);
        }

        // 4) plataformas
        for (int i = 0; i < platformSystem.platforms.size; i++) {
            Platform p = platformSystem.platforms.get(i);

            if (!p.broken) {
                drawIfNotNull(plataformaActual, p.rect.x, p.rect.y, p.rect.width, p.rect.height);
                continue;
            }

            // Nivel 3: NO dibujar rota, solo desaparece
            if (nivelVisual == 2) continue;

            Texture brokenTex = plataformaRotaTex; // nivel 1
            if (nivelVisual == 1) brokenTex = plataformaMediaRotaTex;   // nivel 2
            if (nivelVisual == 3) brokenTex = plataformaColoresRotaTex; // nivel 4

            if (brokenTex == null) continue;

            float t = MathUtils.clamp(p.brokenTime / GameConfig.BROKEN_FADE_TIME, 0f, 1f);
            float a = 1f - t;

            float drawW = p.rect.width * GameConfig.BROKEN_SCALE_X;
            float drawH = p.rect.height * GameConfig.BROKEN_SCALE_Y;

            float drawX = p.rect.x + (p.rect.width - drawW) / 2f;
            float drawY = p.rect.y + (p.rect.height - drawH) / 2f;

            float oldA = batch.getColor().a;
            batch.setColor(1f, 1f, 1f, oldA * a);
            batch.draw(brokenTex, drawX, drawY, drawW, drawH);
            batch.setColor(1f, 1f, 1f, oldA);
        }

        // 5) bandera
        if (banderaTex != null && goalFlagRect != null) {
            batch.draw(banderaTex, goalFlagRect.x, goalFlagRect.y, goalFlagRect.width, goalFlagRect.height);
        }

        // 6) monedas
        if (monedaTex != null) {
            for (int i = 0; i < coinSystem.coins.size; i++) {
                Coin c = coinSystem.coins.get(i);
                batch.draw(monedaTex, c.rect.x, c.rect.y, c.rect.width, c.rect.height);
            }
        }

        // 7) tenis
        if (bootsTex != null) {
            for (int i = 0; i < bootsSystem.boots.size; i++) {
                JumpBoots b = bootsSystem.boots.get(i);

                float drawW = b.rect.width * BOOTS_DRAW_SCALE;
                float drawH = b.rect.height * BOOTS_DRAW_SCALE;
                float drawX = b.rect.x + (b.rect.width - drawW) / 2f;
                float drawY = b.rect.y + (b.rect.height - drawH) / 2f;

                batch.draw(bootsTex, drawX, drawY, drawW, drawH);
            }
        }

        // 8) escudos
        if (shieldTex != null) {
            for (int i = 0; i < shieldSystem.shields.size; i++) {
                Shield s = shieldSystem.shields.get(i);

                float drawW = s.rect.width * SHIELD_DRAW_SCALE;
                float drawH = s.rect.height * SHIELD_DRAW_SCALE;
                float drawX = s.rect.x + (s.rect.width - drawW) / 2f;
                float drawY = s.rect.y + (s.rect.height - drawH) / 2f;

                batch.draw(shieldTex, drawX, drawY, drawW, drawH);
            }
        }

        // 9) setas
        if (setaTex != null) {
            for (int i = 0; i < mushrooms.size; i++) {
                Mushroom m = mushrooms.get(i);

                float drawW = m.rect.width * SETA_DRAW_SCALE;
                float drawH = m.rect.height * SETA_DRAW_SCALE;
                float drawX = m.rect.x + (m.rect.width - drawW) / 2f;
                float drawY = m.rect.y + (m.rect.height - drawH) / 2f;

                batch.draw(setaTex, drawX, drawY, drawW, drawH);
            }
        }

        // 10) enemigos
        for (int i = 0; i < enemySystem.enemies.size; i++) {
            Enemy e = enemySystem.enemies.get(i);
            Texture t = enemyTexture(e.type);
            if (t == null) continue;

            float s = EnemySystem.getDrawScale(e.type);

            float drawW = e.rect.width * s;
            float drawH = e.rect.height * s;

            float drawX = e.rect.x + (e.rect.width - drawW) / 2f;
            float drawY = e.rect.y + (e.rect.height - drawH) / 2f;

            batch.draw(t, drawX, drawY, drawW, drawH);
        }

        // 11) player (prioridad escudo > seta)
        if (player != null) {
            Texture base = player.getTexture();
            Texture toDraw = base;

            if (shieldActive) {
                if (base == pIzqTex) toDraw = pIzqShieldTex;
                else if (base == pDerTex) toDraw = pDerShieldTex;
                else toDraw = pIdleShieldTex;
            } else if (setaActive) {
                if (base == pIzqTex) toDraw = pIzqSetaTex;
                else if (base == pDerTex) toDraw = pDerSetaTex;
                else toDraw = pIdleSetaTex;
            }

            drawIfNotNull(toDraw, player.rect.x, player.rect.y, player.rect.width, player.rect.height);
        }

        // 12) pause
        if (btnPauseTex != null && btnPause != null) {
            float uiLeft = cam.position.x - worldW / 2f;
            float uiBottom = cam.position.y - worldH / 2f;

            batch.draw(
                btnPauseTex,
                uiLeft + btnPause.x,
                uiBottom + btnPause.y,
                btnPause.width,
                btnPause.height
            );
        }

        drawHud(worldW, worldH);

        batch.end();
    }

    // ==========================
    // Layers
    // ==========================
    private void drawCloudsLayer(float worldW, float worldH) {
        if (fondoNubes == null) return;

        float uiLeft = cam.position.x - worldW / 2f;
        float uiBottom = cam.position.y - worldH / 2f;

        float scroll = cam.position.y * CLOUDS_PARALLAX;

        float tileH = worldH;
        float mod = scroll % tileH;
        if (mod < 0) mod += tileH;

        float y0 = uiBottom - mod - tileH;

        float oldA = batch.getColor().a;
        batch.setColor(1f, 1f, 1f, oldA * CLOUDS_ALPHA);

        batch.draw(fondoNubes, uiLeft, y0,             worldW, tileH);
        batch.draw(fondoNubes, uiLeft, y0 + tileH,     worldW, tileH);
        batch.draw(fondoNubes, uiLeft, y0 + 2 * tileH, worldW, tileH);

        batch.setColor(1f, 1f, 1f, oldA);
    }

    private void drawStarsLayer(float worldW, float worldH) {
        if (fondoEstrellas == null) return;

        float uiLeft = cam.position.x - worldW / 2f;
        float uiBottom = cam.position.y - worldH / 2f;

        float scroll = cam.position.y * STARS_PARALLAX;

        float tileH = worldH;
        float mod = scroll % tileH;
        if (mod < 0) mod += tileH;

        float y0 = uiBottom - mod - tileH;

        float oldA = batch.getColor().a;
        batch.setColor(1f, 1f, 1f, oldA * STARS_ALPHA);

        batch.draw(fondoEstrellas, uiLeft, y0,             worldW, tileH);
        batch.draw(fondoEstrellas, uiLeft, y0 + tileH,     worldW, tileH);
        batch.draw(fondoEstrellas, uiLeft, y0 + 2 * tileH, worldW, tileH);

        batch.setColor(1f, 1f, 1f, oldA);
    }

    // ==========================
    // HUD
    // ==========================
    private void drawHud(float worldW, float worldH) {
        float uiLeft = cam.position.x - worldW / 2f;
        float uiBottom = cam.position.y - worldH / 2f;

        // Moneda + contador + powerups
        if (monedaTex != null && btnPause != null) {
            float coinSize = 100f;

            float coinY = uiBottom + btnPause.y + (btnPause.height - coinSize) / 2f;

            font.getData().setScale(4f);
            String txt = "x " + coinSystem.collected;
            layout.setText(font, txt);

            float gap = 14f;

            float blockRight = uiLeft + btnPause.x - 18f;
            float blockW = coinSize + gap + layout.width;

            float coinX = blockRight - blockW;
            float textX = coinX + coinSize + gap;

            float textY = coinY + coinSize * 0.70f;

            batch.draw(monedaTex, coinX, coinY, coinSize, coinSize);

            boolean showBoots  = bootsJumpsLeft > 0;
            boolean showShield = shieldActive;
            boolean showSeta   = setaActive;

            int count = 0;
            if (showShield && shieldTex != null) count++;
            if (showBoots && bootsTex != null) count++;
            if (showSeta && setaTex != null) count++;

            if (count > 0) {
                float totalIconsW = count * POWER_ICON_SIZE + (count - 1) * POWER_ICON_GAP;
                float startX = coinX - 18f - totalIconsW;

                float coinCenterY = coinY + coinSize / 2f;
                float iconY = coinCenterY - POWER_ICON_SIZE / 2f;

                float x = startX;

                if (showShield && shieldTex != null) {
                    batch.draw(shieldTex, x, iconY, POWER_ICON_SIZE, POWER_ICON_SIZE);
                    x += POWER_ICON_SIZE + POWER_ICON_GAP;
                }

                if (showBoots && bootsTex != null) {
                    batch.draw(bootsTex, x, iconY, POWER_ICON_SIZE, POWER_ICON_SIZE);
                    x += POWER_ICON_SIZE + POWER_ICON_GAP;
                }

                if (showSeta && setaTex != null) {
                    batch.draw(setaTex, x, iconY, POWER_ICON_SIZE, POWER_ICON_SIZE);
                }
            }

            font.setColor(1f, 1f, 1f, 1f);
            font.draw(batch, layout, textX, textY);
            font.getData().setScale(1f);
        }

        // Score
        float margin = 70f;
        float textX = uiLeft + margin;
        float textY = uiBottom + worldH - margin;

        font.getData().setScale(3.5f);
        String scoreText = I18n.t("hud_height") + " " + score + " m";
        layout.setText(font, scoreText);

        font.setColor(1f, 1f, 1f, 1f);
        font.draw(batch, layout, textX, textY);
        font.getData().setScale(1f);

        // Mensaje level up
        if (levelUpMsgTime > 0f && levelUpMsgText != null) {
            float t = levelUpMsgTime / LEVEL_UP_MSG_DURATION;
            float alpha = MathUtils.clamp(t, 0f, 1f);

            float cx = cam.position.x;
            float cy = cam.position.y + 250f;

            float scale = 2.2f;

            startOutlineFont.getData().setScale(scale);
            startFillFont.getData().setScale(scale);

            startOutlineFont.setColor(0f, 0f, 0f, alpha);
            startFillFont.setColor(1f, 1f, 1f, alpha);

            layout.setText(startFillFont, levelUpMsgText);
            float x = cx - layout.width / 2f;
            float y = cy + layout.height / 2f;

            FontUtils.drawOutlined(batch, startOutlineFont, startFillFont, levelUpMsgText, x, y, 3.5f);

            startOutlineFont.setColor(Color.BLACK);
            startFillFont.setColor(Color.WHITE);
        }

        // Mensaje final (ganaste)
        if (goalReached && goalMsg != null) {
            float cx = cam.position.x;
            float cy = cam.position.y + 140f;

            float scale = 2.6f;
            startOutlineFont.getData().setScale(scale);
            startFillFont.getData().setScale(scale);

            startOutlineFont.setColor(0f, 0f, 0f, 1f);
            startFillFont.setColor(1f, 1f, 1f, 1f);

            layout.setText(startFillFont, goalMsg);
            float x = cx - layout.width / 2f;
            float y = cy + layout.height / 2f;

            FontUtils.drawOutlined(batch, startOutlineFont, startFillFont, goalMsg, x, y, 3.5f);

            startOutlineFont.setColor(Color.BLACK);
            startFillFont.setColor(Color.WHITE);
        }

        // Texto start
        if (!started) {
            startAnimTime += Gdx.graphics.getDeltaTime();

            float alpha = 0.65f + 0.35f * MathUtils.sin(startAnimTime * 4f);
            float scale = 2.8f + 0.12f * MathUtils.sin(startAnimTime * 4f);

            String t = I18n.t("hud_press_start");

            startOutlineFont.getData().setScale(scale);
            startFillFont.getData().setScale(scale);

            startOutlineFont.setColor(0f, 0f, 0f, alpha);
            startFillFont.setColor(1f, 1f, 1f, alpha);

            layout.setText(startFillFont, t);

            float x = cam.position.x - layout.width / 2f;
            float y = cam.position.y - 170f;

            FontUtils.drawOutlined(batch, startOutlineFont, startFillFont, t, x, y, 3.5f);

            startOutlineFont.setColor(Color.BLACK);
            startFillFont.setColor(Color.WHITE);
        }
    }

    // ==========================
    // UPDATE
    // ==========================
    private void update(float dt) {

        // ✅ IMPORTANTÍSIMO: si tocó la bandera, esperar 2s y pasar a VictoryScreen
        if (goalReached) {
            goalTimer -= dt;
            if (goalTimer <= 0f) {
                finishVictory();
            }
            return;
        }

        // Movimiento horizontal (wrap siempre) + seta (invertir)
        if (player != null) {
            float oldX = player.rect.x;

            player.updateHorizontal(dt);

            // si la seta invierte controles, invierte el desplazamiento (pero NO si hay escudo)
            if (setaActive && !shieldActive) {
                float dx = player.rect.x - oldX;
                player.rect.x = oldX - dx;
            }

            // ✅ WRAP SIEMPRE (con o sin poderes)
            float w = player.rect.width;
            float worldW = GameConfig.VW;

            if (player.rect.x + w < 0f) {
                player.rect.x = worldW;
            } else if (player.rect.x > worldW) {
                player.rect.x = -w;
            }
        }

        // DYING
        if (dying) {

            // Sonido de caída SOLO si es muerte por vacío
            if (voidFalling && !playedVoidFallSfx) {
                audio.playCaidaPersonaje(); // ✅ caidapersonaje.mp3
                playedVoidFallSfx = true;
            }

            // física de caída
            if (player != null) {
                player.velY -= GameConfig.GRAVITY * dt;
                player.rect.y += player.velY * dt;
            }

            // ✅ cámara sigue al player hacia abajo (sin minCamY)
            cam.position.x = GameConfig.VW / 2f;
            if (player != null) {
                float targetY = player.rect.y + CAM_Y_OFFSET;
                float a = 1f - (float) Math.exp(-CAM_FOLLOW_SPEED * dt);
                cam.position.y = MathUtils.lerp(cam.position.y, targetY, a);
            }
            cam.update();

            dyingTimer -= dt;
            if (dyingTimer <= 0f) {
                finishDying();
            }
            return;
        }

        if (started && !dying) {
            runTimeSec += dt;
        }

        // Mover plataformas
        platformSystem.updateMoving(dt);

        // Seta sigue a su plataforma
        updateMushroomsFollowPlatforms();

        // Física vertical
        if (started && player != null) {
            float oldY = player.rect.y;

            // Física vertical (caer más rápido que subir)
            if (player.velY < 0f) {
                player.velY -= GameConfig.GRAVITY * FALL_MULT * dt;  // cayendo => más gravedad
                // cap de caída para que no sea exagerado
                if (player.velY < -MAX_FALL_SPEED) player.velY = -MAX_FALL_SPEED;
            } else {
                player.velY -= GameConfig.GRAVITY * dt;              // subiendo => normal
            }

            player.rect.y += player.velY * dt;

            handlePlatformCollision(oldY);

            if (player.rect.y > maxY) {
                maxY = player.rect.y;
                score = (int) (maxY / 100f);

                // En modo niveles, cuando ya hay final, no subimos score más de 800
                if (gameMode == GameMode.LEVELS && goalSpawned) {
                    if (score > MODE_TARGET_METERS) score = MODE_TARGET_METERS;
                }
            }

            // MODOS
            if (gameMode == GameMode.LEVELS) {

                // al llegar a 800, spawnea final (una vez)
                if (score >= MODE_TARGET_METERS && !goalSpawned) {
                    spawnGoalPlatform();
                }

                // mientras no haya final, niveles normales
                if (!goalSpawned) {
                    applyLevelByMeters(score, true);
                }

            } else {
                // INFINITE: bucle cada 800
                int newLap = score / MODE_TARGET_METERS;
                if (newLap != lapIndex) {
                    lapIndex = newLap;

                    levelUpMsgText = I18n.t("msg_new_lap");
                    levelUpMsgTime = LEVEL_UP_MSG_DURATION;
                    audio.playLevelUp();

                    setNivelVisual(0, false);
                }

                int lapMeters = score % MODE_TARGET_METERS;
                applyLevelByMeters(lapMeters, true);
            }
        }

        // Plataformas rotas timer
        platformSystem.updateBreakables(dt);

        if (levelUpMsgTime > 0f) {
            levelUpMsgTime -= dt;
            if (levelUpMsgTime < 0f) levelUpMsgTime = 0f;
        }

        // CÁMARA (si final: congelada)
        if (player != null) {
            cam.position.x = GameConfig.VW / 2f;

            float minCamY = groundY + viewport.getWorldHeight() / 2f;
            float targetY;

            if (gameMode == GameMode.LEVELS && goalCamFrozen) {
                targetY = goalFrozenCamY;
            } else {
                targetY = player.rect.y + CAM_Y_OFFSET;
                if (targetY < minCamY) targetY = minCamY;
            }

            float a = 1f - (float) Math.exp(-CAM_FOLLOW_SPEED * dt);
            cam.position.y = MathUtils.lerp(cam.position.y, targetY, a);

            if (cam.position.y < minCamY) cam.position.y = minCamY;

            cam.update();
        }

        float worldH = viewport.getWorldHeight();
        float bottomVisible = cam.position.y - worldH / 2f;
        ruins.update(dt, bottomVisible);

        // NO spawnear más plataformas por arriba si ya hay final en modo niveles
        boolean canSpawnMore = !(gameMode == GameMode.LEVELS && goalSpawned);

        if (canSpawnMore) {
            float topVisible = cam.position.y + worldH / 2f + 300f;
            while (platformSystem.nextY < topVisible) {
                spawnPlatformAbove();
            }
        }

        // killY
        float killY = bottomVisible - 200f;

        // ✅ Detectar “caída libre definitiva” ANTES de que salga de pantalla
        if (started && player != null && !dying) {

            if (player.velY < 0f) {

                boolean hasPlatformBelow = false;
                float minY = player.rect.y - FALL_CHECK_RANGE;

                for (int i = 0; i < platformSystem.platforms.size; i++) {
                    Platform p = platformSystem.platforms.get(i);
                    if (p == null || p.broken) continue;

                    float platTop = p.rect.y + p.rect.height;

                    if (platTop < player.rect.y && platTop > minY) {
                        hasPlatformBelow = true;
                        break;
                    }
                }

                if (!hasPlatformBelow) {
                    beginVoidFall();
                    return;
                }
            }
        }

        // SETA
        updateMushrooms(dt, killY);

        // updates sistemas
        coinSystem.update(player, killY, new CoinSystem.OnCoinCollected() {
            @Override
            public void onCoinCollected() {
                audio.playCoin();
            }
        });

        bootsSystem.update(player, killY, new JumpBootsSystem.OnBootsCollected() {
            @Override
            public void onBootsCollected() {
                bootsJumpsLeft = 1;
                audio.playCogerItem();
            }
        });

        shieldSystem.update(player, killY, new ShieldSystem.OnShieldCollected() {
            @Override
            public void onShieldCollected() {

                // ✅ Si la SETA está activa, el escudo NO hace efecto
                if (!setaActive) {
                    shieldActive = true;
                }

                // (igual que con la seta bloqueada por escudo: se recoge y suena, pero sin efecto)
                audio.playCogerItem();
            }
        });


        enemySystem.update(player, killY, new EnemySystem.OnEnemyHit() {
            @Override
            public boolean onEnemyHit(EnemyType type) {
                if (shieldActive) {
                    shieldActive = false;
                    audio.playEscudoRoto();
                    return true;
                }
                audio.playEnemy(type);
                return false;
            }
        });

        // Cull plataformas: conservar aprox 3 por debajo
        float keepBelow = 3f * GameConfig.STEP_Y + 250f;
        float cullY = player.rect.y - keepBelow;

        // no cullar por encima de lo visible
        cullY = Math.min(cullY, bottomVisible - 200f);

        platformSystem.cullBelow(cullY);

        // limpiar setas si su plataforma ya no existe
        cullMushroomsWithMissingPlatforms();

        // Tocar bandera
        if (gameMode == GameMode.LEVELS && goalSpawned && goalFlagRect != null && player != null && !goalReached) {
            if (player.rect.overlaps(goalFlagRect)) {
                triggerGoalReached();
                return;
            }
        }

        // PERDER (solo definitivo)
        // PERDER (solo definitivo)
        if (started && player != null && !dying) {

            // muerte “dura” (muy abajo) -> rápida, SIN sonido de caída
            if (player.rect.y < killY - HARD_DEATH_EXTRA) {
                beginDying(HARD_DEATH_DURATION, false);
                return;
            }

            // si cae por debajo de killY, comprobamos si hay plataformas debajo
            if (player.rect.y < killY) {

                boolean hasPlatformBelow = false;
                float minY = player.rect.y - FALL_CHECK_RANGE;

                for (int i = 0; i < platformSystem.platforms.size; i++) {
                    Platform p = platformSystem.platforms.get(i);
                    if (p == null || p.broken) continue;

                    float platTop = p.rect.y + p.rect.height;

                    if (platTop < player.rect.y && platTop > minY) {
                        hasPlatformBelow = true;
                        break;
                    }
                }

                // ✅ caída libre final -> 4s + sonido caida + cámara bajando
                if (!hasPlatformBelow) {
                    beginDying(VOID_FALL_DURATION, true);
                    return;
                }
            }
        }
    }

    private void beginDying(float duration, boolean playVoidFallSfx) {
        if (dying) return;

        dying = true;
        dyingTimer = duration;

        voidFalling = playVoidFallSfx;
        playedVoidFallSfx = false;

        // si quieres parar música al morir:
        audio.stopFondo();

        // NO pongas aquí frozenCamY si quieres cámara libre
    }

    private void applyLevelByMeters(int meters, boolean playSfx) {
        if (meters >= 600) setNivelVisual(3, playSfx);
        else if (meters >= 400) setNivelVisual(2, playSfx);
        else if (meters >= 200) setNivelVisual(1, playSfx);
        else setNivelVisual(0, false);
    }

    private void spawnPlatformAbove() {
        platformSystem.nextY += GameConfig.STEP_Y;
        float x = MathUtils.random(0f, GameConfig.VW - GameConfig.PLATFORM_W);

        Platform p = platformSystem.makePlatform(x, platformSystem.nextY, true);
        platformSystem.platforms.add(p);

        // 1) primero intenta spawner general (monedas/enemigos)
        int tries = 2 + extraSpawnAttemptsForNivel(); // 🔥 más bichos (y más cosas) por plataforma
        for (int t = 0; t < tries; t++) {
            int enemiesBefore = enemySystem.enemies.size;
            pickupSpawner.trySpawn(p, coinSystem, enemySystem);
            clampNewEnemiesToLevel(enemiesBefore);
        }

        // si ya apareció algo, NO ponemos nada más
        if (hasAnyThingOnPlatform(p)) return;

        // 2) tenis
        bootsSystem.trySpawnOnPlatformIfFree(p, coinSystem, enemySystem, BOOTS_CHANCE, false);
        if (hasAnyThingOnPlatform(p)) return;

        // 3) escudo
        shieldSystem.trySpawnOnPlatformIfFree(p, coinSystem, enemySystem, bootsSystem, SHIELD_CHANCE, false);
        if (hasAnyThingOnPlatform(p)) return;

        // 4) seta
        trySpawnMushroomOnPlatformIfFree(p, SETA_CHANCE);
    }


    // ==========================
    // FINAL MODO NIVELES
    // ==========================
    private void spawnGoalPlatform() {
        goalSpawned = true;

        float worldH = viewport.getWorldHeight();

        // centrada
        float goalX = (GameConfig.VW - GameConfig.PLATFORM_W) / 2f;

        // colocarla arriba del todo respecto a la cámara actual
        float desiredPlatformTopOnScreen = (cam.position.y + worldH / 2f) - GOAL_TOP_MARGIN;
        float goalY = desiredPlatformTopOnScreen - GameConfig.PLATFORM_H;

        // asegurar que es "final" 800m o más
        float minFinalY = MODE_TARGET_METERS * 100f + 120f;
        if (goalY < minFinalY) goalY = minFinalY;

        Platform p = new Platform(
            new Rectangle(goalX, goalY, GameConfig.PLATFORM_W, GameConfig.PLATFORM_H),
            false, 0f, false
        );
        p.dir = 0;

        platformSystem.platforms.add(p);
        goalPlatform = p;

        // bandera encima
        float fw = 120f;
        float fh = 160f;
        float fx = goalX + (GameConfig.PLATFORM_W - fw) / 2f;
        float fy = goalY + GameConfig.PLATFORM_H + 10f;
        goalFlagRect = new Rectangle(fx, fy, fw, fh);

        // cortar techo: no más nextY
        platformSystem.nextY = goalY;

        // eliminar posibles plataformas por encima
        for (int i = platformSystem.platforms.size - 1; i >= 0; i--) {
            Platform other = platformSystem.platforms.get(i);
            if (other != p && other.rect.y > goalY + 20f) {
                platformSystem.platforms.removeIndex(i);
            }
        }

        // congelar cámara para que se quede con la plataforma final arriba
        goalCamFrozen = true;

        float desiredCamTop = (goalY + GameConfig.PLATFORM_H) + GOAL_TOP_MARGIN;
        goalFrozenCamY = desiredCamTop - worldH / 2f;

        cam.position.y = goalFrozenCamY;
        cam.update();

        levelUpMsgText = I18n.t("msg_reached_end");
        levelUpMsgTime = 1.5f;
        audio.playLevelUp();
    }

    private void triggerGoalReached() {
        if (goalReached) return;
        goalReached = true;
        goalTimer = GOAL_DELAY_TO_SCREEN;
        goalMsg = I18n.t("msg_win");

        audio.stopFondo();
        audio.playVictory(); // win.mp3
    }

    // ==========================
    // SETA
    // ==========================
    private void updateMushroomsFollowPlatforms() {
        for (int i = 0; i < mushrooms.size; i++) {
            mushrooms.get(i).followPlatform();
        }
    }

    private void updateMushrooms(float dt, float killY) {
        if (setaActive) {
            setaTimer -= dt;
            if (setaTimer <= 0f) {
                setaTimer = 0f;
                setaActive = false;
            }
        }

        // limpiar por debajo
        for (int i = mushrooms.size - 1; i >= 0; i--) {
            Mushroom m = mushrooms.get(i);
            if (m.rect.y < killY) mushrooms.removeIndex(i);
        }

        // recoger
        if (player == null) return;
        for (int i = mushrooms.size - 1; i >= 0; i--) {
            Mushroom m = mushrooms.get(i);
            if (player.rect.overlaps(m.rect)) {
                mushrooms.removeIndex(i);

                // ✅ Si hay escudo: se rompe el escudo, PERO la seta NO hace efecto
                if (shieldActive) {
                    shieldActive = false;
                    audio.playEscudoRoto();  // opcional, pero queda bien
                    audio.playCogerItem();   // sonido de recoger (si quieres, puedes quitarlo)
                    break;
                }

                // ✅ Si NO hay escudo: seta normal
                setaActive = true;
                setaTimer = SETA_DURATION;
                audio.playCogerItem();
                break;
            }
        }
    }

    private void cullMushroomsWithMissingPlatforms() {
        for (int i = mushrooms.size - 1; i >= 0; i--) {
            Mushroom m = mushrooms.get(i);
            if (m.platform == null || !platformStillExists(m.platform)) {
                mushrooms.removeIndex(i);
            }
        }
    }

    private boolean platformStillExists(Platform plat) {
        for (int i = 0; i < platformSystem.platforms.size; i++) {
            if (platformSystem.platforms.get(i) == plat) return true;
        }
        return false;
    }

    private void trySpawnMushroomOnPlatformIfFree(Platform p, float chance) {
        if (p == null) return;
        if (MathUtils.random() > chance) return;

        // limitar cantidad total
        if (mushrooms.size >= 4) return;

        // si ya hay moneda/tenis/escudo/seta en esa plataforma: no spawnear
        if (hasAnyPickupOnPlatform(p)) return;

        float w = 70f;
        float h = 70f;

        float x = p.rect.x + (p.rect.width - w) / 2f;
        float y = p.rect.y + p.rect.height + 18f;

        Rectangle r = new Rectangle(x, y, w, h);
        if (!isPickupAreaFree(r)) return;

        mushrooms.add(new Mushroom(p, x, y, w, h));
    }

    private boolean hasAnyPickupOnPlatform(Platform p) {
        if (p == null) return false;

        float px1 = p.rect.x;
        float px2 = p.rect.x + p.rect.width;
        float top = p.rect.y + p.rect.height;

        float yMin = top - 5f;
        float yMax = top + 180f;

        for (int i = 0; i < coinSystem.coins.size; i++) {
            Coin c = coinSystem.coins.get(i);
            if (isRectOnPlatformArea(c.rect, px1, px2, yMin, yMax)) return true;
        }

        for (int i = 0; i < bootsSystem.boots.size; i++) {
            JumpBoots b = bootsSystem.boots.get(i);
            if (isRectOnPlatformArea(b.rect, px1, px2, yMin, yMax)) return true;
        }

        for (int i = 0; i < shieldSystem.shields.size; i++) {
            Shield s = shieldSystem.shields.get(i);
            if (isRectOnPlatformArea(s.rect, px1, px2, yMin, yMax)) return true;
        }

        for (int i = 0; i < mushrooms.size; i++) {
            Mushroom m = mushrooms.get(i);
            if (isRectOnPlatformArea(m.rect, px1, px2, yMin, yMax)) return true;
        }

        return false;
    }

    private boolean hasAnyThingOnPlatform(Platform p) {
        if (p == null) return false;

        float px1 = p.rect.x;
        float px2 = p.rect.x + p.rect.width;
        float top = p.rect.y + p.rect.height;

        float yMin = top - 10f;
        float yMax = top + 220f;

        // monedas
        for (int i = 0; i < coinSystem.coins.size; i++) {
            Coin c = coinSystem.coins.get(i);
            if (isRectOnPlatformArea(c.rect, px1, px2, yMin, yMax)) return true;
        }

        // tenis
        for (int i = 0; i < bootsSystem.boots.size; i++) {
            JumpBoots b = bootsSystem.boots.get(i);
            if (isRectOnPlatformArea(b.rect, px1, px2, yMin, yMax)) return true;
        }

        // escudos
        for (int i = 0; i < shieldSystem.shields.size; i++) {
            Shield s = shieldSystem.shields.get(i);
            if (isRectOnPlatformArea(s.rect, px1, px2, yMin, yMax)) return true;
        }

        // setas
        for (int i = 0; i < mushrooms.size; i++) {
            Mushroom m = mushrooms.get(i);
            if (isRectOnPlatformArea(m.rect, px1, px2, yMin, yMax)) return true;
        }

        // enemigos (IMPORTANTE: también cuenta como “item”)
        for (int i = 0; i < enemySystem.enemies.size; i++) {
            Enemy e = enemySystem.enemies.get(i);
            if (isRectOnPlatformArea(e.rect, px1, px2, yMin, yMax)) return true;
        }

        return false;
    }


    private boolean isRectOnPlatformArea(Rectangle r, float px1, float px2, float yMin, float yMax) {
        float cx = r.x + r.width * 0.5f;
        float cy = r.y + r.height * 0.5f;
        return (cx >= px1 && cx <= px2 && cy >= yMin && cy <= yMax);
    }

    private boolean isPickupAreaFree(Rectangle r) {
        for (int i = 0; i < coinSystem.coins.size; i++) if (coinSystem.coins.get(i).rect.overlaps(r)) return false;
        for (int i = 0; i < enemySystem.enemies.size; i++) if (enemySystem.enemies.get(i).rect.overlaps(r)) return false;
        for (int i = 0; i < bootsSystem.boots.size; i++) if (bootsSystem.boots.get(i).rect.overlaps(r)) return false;
        for (int i = 0; i < shieldSystem.shields.size; i++) if (shieldSystem.shields.get(i).rect.overlaps(r)) return false;
        for (int i = 0; i < mushrooms.size; i++) if (mushrooms.get(i).rect.overlaps(r)) return false;
        if (goalFlagRect != null && goalFlagRect.overlaps(r)) return false;
        return true;
    }

    // ==========================
    // Colisión plataformas
    // ==========================
    private void handlePlatformCollision(float oldY) {
        if (player == null) return;
        if (player.velY >= 0) return; // solo cuando cae

        // pies "más estrechos" para evitar engancharse por los lados
        float feetW = player.rect.width * 0.35f;
        float feetX = player.rect.x + (player.rect.width - feetW) / 2f;

        float oldFeetY = oldY;              // y anterior (abajo del player)
        float newFeetY = player.rect.y;     // y nuevo (abajo del player)

        // distancia caída en este frame
        float fallDist = oldFeetY - newFeetY;
        if (fallDist <= 0f) return;

        // margen dinámico: si caes mucho en un frame, aumentamos tolerancia
        float eps = Math.max(25f, fallDist + 5f);

        Platform best = null;
        float bestTop = -Float.MAX_VALUE;

        for (int i = 0; i < platformSystem.platforms.size; i++) {
            Platform p = platformSystem.platforms.get(i);
            if (p == null || p.broken) continue;

            float platTop = p.rect.y + p.rect.height;

            // cruzó el top de la plataforma en este frame
            boolean crossedTop = oldFeetY >= platTop && newFeetY <= platTop;
            if (!crossedTop) continue;

            // pies solapan en X
            boolean feetOverX = feetX < p.rect.x + p.rect.width && (feetX + feetW) > p.rect.x;
            if (!feetOverX) continue;

            // la "más alta" gana (la que realmente tocarías primero)
            if (platTop > bestTop && (Math.abs(newFeetY - platTop) <= eps)) {
                bestTop = platTop;
                best = p;
            }
        }

        if (best == null) return;

        // aterriza
        player.rect.y = bestTop;

        float jump = GameConfig.JUMP_VEL;
        boolean usedBoots = false;

        if (bootsJumpsLeft > 0) {
            jump *= BOOTS_MULT;
            bootsJumpsLeft--;
            usedBoots = true;
        }

        player.velY = jump;

        if (usedBoots) audio.playTenis();
        else audio.playLand();

        if (best.breakable) {
            best.broken = true;
            best.brokenTime = 0f;
        }
    }

    // ==========================
    // Nivel visual
    // ==========================
    private void setNivelVisual(int nivel, boolean playSfx) {
        if (nivel == nivelVisual) return;

        int oldNivel = nivelVisual;
        nivelVisual = nivel;

        if (nivelVisual == 0) {
            fondoActual = fondoRosa;
            plataformaActual = plataformaRuinas;
            cloudsEnabled = true;
            starsEnabled = false;

        } else if (nivelVisual == 1) {
            fondoActual = fondoAzul;
            plataformaActual = plataformaMedia;
            cloudsEnabled = true;
            starsEnabled = false;

        } else if (nivelVisual == 2) {
            fondoActual = fondoAzulOscuro;
            plataformaActual = plataformaModerna;
            cloudsEnabled = false;
            starsEnabled = true;

        } else {
            fondoActual = fondoAmarillo;
            plataformaActual = plataformaColores;
            cloudsEnabled = false;
            starsEnabled = true;
        }

        if (playSfx && oldNivel >= 0 && (nivelVisual == 1 || nivelVisual == 2 || nivelVisual == 3)) {
            levelUpMsgText = I18n.t("msg_levelup");
            levelUpMsgTime = LEVEL_UP_MSG_DURATION;
            audio.playLevelUp();
        }
    }

    // ==========================
    // Enemigos por nivel
    // ==========================
    private EnemyType randomAllowedTypeForCurrentLevel() {
        if (nivelVisual == 0) return EnemyType.LILA;

        if (nivelVisual == 1) {
            return MathUtils.randomBoolean() ? EnemyType.LILA : EnemyType.VERDE;
        }

        if (nivelVisual == 2) {
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
        for (int i = startIndex; i < enemySystem.enemies.size; i++) {
            Enemy e = enemySystem.enemies.get(i);
            if (e == null) continue;
            e.type = randomAllowedTypeForCurrentLevel();
        }
    }

    private int extraSpawnAttemptsForNivel() {
        switch (nivelVisual) {
            case 0: return 1; // antes 0
            case 1: return 2; // antes 1
            case 2: return 3; // antes 2
            case 3: return 4; // antes 3
            default: return 1;
        }
    }

    private Texture enemyTexture(EnemyType type) {
        if (type == null) return null;
        switch (type) {
            case LILA:  return enemyLilaTex;
            case VERDE: return enemyVerdeTex;
            case AZUL:  return enemyAzulTex;
            case ROJO:  return enemyRojoTex;
        }
        return null;
    }

    // ==========================
    // Muerte / Victoria
    // ==========================
    private void beginVoidFall() {
        if (dying) return;

        dying = true;
        voidFalling = true;
        dyingTimer = VOID_FALL_DURATION;

        // parar música de fondo si quieres
        audio.stopFondo();

        // ✅ sonido de caída al empezar la caída libre final
        audio.playCaidaPersonaje();
    }

    private void finishDying() {
        // ✅ una sola vez (antes lo tenías duplicado)
        GameSave.addRunToHistory(score, coinSystem.collected, runTimeSec);
        game.setScreen(new GameOverScreen(game, score, coinSystem.collected));
    }

    private void finishVictory() {
        // ✅ una sola vez (antes lo tenías duplicado)
        GameSave.addRunToHistory(score, coinSystem.collected, runTimeSec);
        game.setScreen(new VictoryScreen(game, score, coinSystem.collected, audio));
    }

    @Override
    public void dispose() {
        super.dispose();
        if (startFillFont != null) startFillFont.dispose();
        if (startOutlineFont != null) startOutlineFont.dispose();
        audio.dispose();
    }

    public void saveGame() {
        if (player == null) return;

        GameSave.State s = new GameSave.State();
        s.started = started;
        s.score = score;
        s.maxY = maxY;
        s.nivelVisual = nivelVisual;

        s.player = player;
        s.platforms = platformSystem;

        s.camX = cam.position.x;
        s.camY = cam.position.y;

        s.ruinasBaseY = ruins.baseY;
        s.ruinasAlpha = ruins.alpha;
        s.ruinasFading = ruins.fading;
        s.ruinasOff = ruins.disabled;

        GameSave.save(s);
    }
}
