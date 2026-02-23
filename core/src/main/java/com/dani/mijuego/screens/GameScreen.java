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
import com.dani.mijuego.game.systems.MushroomSystem;
import com.dani.mijuego.game.systems.PickupSpawner;
import com.dani.mijuego.game.systems.PlatformChecks;
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

    private static final int MODE_TARGET_METERS = 800;
    private int lapIndex = 0;

    // ==========================
    // Fonts / HUD
    // ==========================
    private BitmapFont startFillFont;
    private BitmapFont startOutlineFont;

    // Caída más rápida que la subida
    private static final float FALL_MULT = 2.0f;
    private static final float MAX_FALL_SPEED = 3200f;

    private Texture btnPauseTex;
    private Rectangle btnPause;

    private static final float VOID_FALL_DURATION = 3.0f;
    private static final float HARD_DEATH_DURATION = 0.35f;

    private boolean voidFalling = false;
    private boolean playedVoidFallSfx = false;

    // ==========================
    // Fondos base
    // ==========================
    private Texture fondoRosa;
    private Texture fondoAzul;
    private Texture fondoAzulOscuro;
    private Texture fondoAmarillo;
    private Texture fondoActual;

    // Layers
    private Texture fondoNubes;
    private boolean cloudsEnabled = false;
    private static final float CLOUDS_PARALLAX = 0.25f;
    private static final float CLOUDS_ALPHA = 0.90f;

    private Texture fondoEstrellas;
    private boolean starsEnabled = false;

    private Texture fondoEstrellasColores;
    private static final float STARS_PARALLAX = 0.18f;
    private static final float STARS_ALPHA = 0.95f;

    // ==========================
    // Plataformas
    // ==========================
    private Texture plataformaRuinas;
    private Texture plataformaMedia;
    private Texture plataformaModerna;

    private Texture plataformaRotaTex;        // nivel 1
    private Texture plataformaMediaRotaTex;   // nivel 2
    private Texture plataformaColores;        // nivel 4
    private Texture plataformaColoresRotaTex; // nivel 4 rota

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
    private static final float BOOTS_MULT = 1.9f;
    private static final float BOOTS_CHANCE = 0.06f;
    private static final float BOOTS_DRAW_SCALE = 1.6f;

    private float runTimeSec = 0f;

    // ==========================
    // Escudo
    // ==========================
    private Texture shieldTex;
    private final ShieldSystem shieldSystem = new ShieldSystem();
    private static final float SHIELD_CHANCE = 0.05f;
    private static final float SHIELD_DRAW_SCALE = 1.6f;

    // ==========================
    // SETA
    // ==========================
    private Texture setaTex;
    private Texture pIdleSetaTex;
    private Texture pIzqSetaTex;
    private Texture pDerSetaTex;

    private final MushroomSystem mushroomSystem = new MushroomSystem();
    private static final float SETA_CHANCE = 0.04f;
    private static final float SETA_DURATION = 5.0f;
    private static final float SETA_DRAW_SCALE = 1.6f;

    // ==========================
    // FINAL MODO NIVELES (plataforma + bandera)
    // ==========================
    private Texture banderaTex;

    private boolean goalSpawned = false;
    private boolean goalReached = false;

    private Platform goalPlatform = null;
    private Rectangle goalFlagRect = null;

    private boolean goalCamFrozen = false;
    private float goalFrozenCamY = 0f;

    private float goalTimer = 0f;
    private static final float GOAL_DELAY_TO_SCREEN = 2.0f;

    private static final float GOAL_TOP_MARGIN = 180f;
    private String goalMsg = null;

    // ==========================
    // HUD Power Icons
    // ==========================
    private static final float POWER_ICON_SIZE = 110f;
    private static final float POWER_ICON_GAP = 12f;

    // ==========================
    // World / Systems
    // ==========================
    private Player player;
    private final PlatformSystem platformSystem = new PlatformSystem();
    private final RuinsLayer ruins = new RuinsLayer();

    private final CoinSystem coinSystem = new CoinSystem();
    private final EnemySystem enemySystem = new EnemySystem();
    private final PickupSpawner pickupSpawner = new PickupSpawner();

    private GameAudio audio;
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

    private float groundY = 0f;
    private static final float CAM_Y_OFFSET = 500f;
    private static final float CAM_FOLLOW_SPEED = 10f;

    private static final float HARD_DEATH_EXTRA = 300f;
    private static final float FALL_CHECK_RANGE = 1400f;

    // ==========================
    // Listeners (evita new cada frame)
    // ==========================
    private final CoinSystem.OnCoinCollected onCoinCollected = new CoinSystem.OnCoinCollected() {
        @Override public void onCoinCollected() { audio.playCoin(); }
    };

    private final JumpBootsSystem.OnBootsCollected onBootsCollected = new JumpBootsSystem.OnBootsCollected() {
        @Override public void onBootsCollected() {
            if (player != null) player.giveBoots(1);
            audio.playCogerItem();
        }
    };

    private final ShieldSystem.OnShieldCollected onShieldCollected = new ShieldSystem.OnShieldCollected() {
        @Override public void onShieldCollected() {
            if (player != null) player.tryActivateShield(); // si seta activa, no hace efecto
            audio.playCogerItem();
        }
    };

    private final MushroomSystem.OnMushroomCollected onMushroomCollected = new MushroomSystem.OnMushroomCollected() {
        @Override public void onMushroomCollected() {
            if (player == null) return;

            boolean activated = player.tryActivateSeta(SETA_DURATION);
            if (!activated) {
                // Había escudo y se rompió, la seta no se activó
                audio.playEscudoRoto();
            }
            audio.playCogerItem();
        }
    };

    private final EnemySystem.OnEnemyHit onEnemyHit = new EnemySystem.OnEnemyHit() {
        @Override public boolean onEnemyHit(EnemyType type) {
            if (player != null && player.blockEnemyHitIfShield()) {
                audio.playEscudoRoto();
                return true; // bloqueado
            }
            audio.playEnemy(type);
            return false;
        }
    };

    public GameScreen(Main game, GameMode mode) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.gameMode = (mode == null) ? GameMode.LEVELS : mode;
        this.audio = game.audio; // 🔥 usar audio global
    }

    public GameScreen(Main game) {
        this(game, GameMode.LEVELS);
    }

    @Override
    public void show() {
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

        // Fondos
        fondoRosa       = mustTex(Assets.FONDO_NARANJA);
        fondoAzul       = mustTex(Assets.FONDO_AZUL);
        fondoAzulOscuro = mustTex(Assets.FONDO_LILA);
        fondoAmarillo   = mustTex(Assets.FONDO_AMARILLO);

        // Layers
        fondoNubes     = mustTex(Assets.NUBES);
        fondoEstrellas = mustTex(Assets.ESTRELLAS);
        fondoEstrellasColores = mustTex(Assets.ESTRELLASCOLORES);

        // Plataformas
        plataformaRuinas = mustTex(Assets.PLAT_RUINAS);
        plataformaMedia  = mustTex(Assets.PLAT_MEDIA);
        plataformaModerna= mustTex(Assets.PLAT_MODERNA);

        plataformaRotaTex      = mustTex(Assets.PLAT_ROTA);
        plataformaMediaRotaTex = mustTex(Assets.PLAT_MEDIAROTA);

        plataformaColores        = mustTex(Assets.PLAT_COLORES);
        plataformaColoresRotaTex = mustTex(Assets.PLAT_COLORES_ROTA);

        // Decor
        ruinasTex = mustTex(Assets.RUINAS);

        // Player
        pIdleTex = mustTex(Assets.PLAYER_IDLE);
        pIzqTex  = mustTex(Assets.PLAYER_IZQ);
        pDerTex  = mustTex(Assets.PLAYER_DER);

        // Moneda
        monedaTex = mustTex(Assets.MONEDA);

        // Enemigos
        enemyAzulTex  = mustTex(Assets.BICHOAZUL);
        enemyLilaTex  = mustTex(Assets.BICHOLILA);
        enemyVerdeTex = mustTex(Assets.BICHOVERDE);
        enemyRojoTex  = mustTex(Assets.BICHOROJO);

        // Pause
        btnPauseTex = mustTex(Assets.BTN_PAUSE);

        // Powerups
        bootsTex  = mustTex(Assets.ZAPATOS);
        shieldTex = mustTex(Assets.ESCUDO);

        pIdleShieldTex = mustTex(Assets.PLAYER_ESCUDO_IDLE);
        pIzqShieldTex  = mustTex(Assets.PLAYER_ESCUDO_IZQ);
        pDerShieldTex  = mustTex(Assets.PLAYER_ESCUDO_DER);

        // Seta
        setaTex     = mustTex(Assets.SETA);
        pIdleSetaTex= mustTex("personaje/personajeseta.png");
        pIzqSetaTex = mustTex("personaje/personajesetaizquierda.png");
        pDerSetaTex = mustTex("personaje/personajesetaderecha.png");

        // Bandera
        banderaTex = mustTex(Assets.BANDERA);

        audio.load();

        nivelVisual = -1;
        lapIndex = 0;
        setNivelVisual(0, false);

        resetWorld();
        updateUiPositions();
        installInput();
    }

    // ==========================
    // Texturas helpers
    // ==========================
    private Texture mustTex(String id) {
        Texture t = getTex(id);
        if (t == null) throw new RuntimeException("TEXTURA NULL: " + id);
        return t;
    }

    private void drawFullScreen(Texture tex, float worldW, float worldH) {
        if (tex == null) return;
        batch.draw(tex,
            cam.position.x - worldW / 2f,
            cam.position.y - worldH / 2f,
            worldW,
            worldH
        );
    }

    private void drawTiledParallaxLayer(Texture tex, float parallax, float alpha, float worldW, float worldH) {
        if (tex == null) return;

        float uiLeft = cam.position.x - worldW / 2f;
        float uiBottom = cam.position.y - worldH / 2f;

        float scroll = cam.position.y * parallax;

        float tileH = worldH;
        float mod = scroll % tileH;
        if (mod < 0) mod += tileH;

        float y0 = uiBottom - mod - tileH;

        float oldA = batch.getColor().a;
        batch.setColor(1f, 1f, 1f, oldA * alpha);

        batch.draw(tex, uiLeft, y0,             worldW, tileH);
        batch.draw(tex, uiLeft, y0 + tileH,     worldW, tileH);
        batch.draw(tex, uiLeft, y0 + 2 * tileH, worldW, tileH);

        batch.setColor(1f, 1f, 1f, oldA);
    }

    private void drawRect(Texture tex, Rectangle r) {
        if (tex == null || r == null) return;
        batch.draw(tex, r.x, r.y, r.width, r.height);
    }

    private void drawScaledCentered(Texture tex, Rectangle r, float scale) {
        if (tex == null || r == null) return;

        float drawW = r.width * scale;
        float drawH = r.height * scale;
        float drawX = r.x + (r.width - drawW) / 2f;
        float drawY = r.y + (r.height - drawH) / 2f;

        batch.draw(tex, drawX, drawY, drawW, drawH);
    }

    // ==========================
    // Reset
    // ==========================
    private void resetWorld() {
        started = false;
        score = 0;
        maxY = 0f;

        dying = false;
        dyingTimer = 0f;

        voidFalling = false;
        playedVoidFallSfx = false;

        lapIndex = 0;

        bootsSystem.reset();
        shieldSystem.reset();
        mushroomSystem.reset();

        coinSystem.coins.clear();
        coinSystem.collected = 0;

        enemySystem.enemies.clear();
        pickupSpawner.reset();

        runTimeSec = 0f;

        platformSystem.platforms.clear();

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

        player = new Player(
            (GameConfig.VW - GameConfig.PLAYER_W) / 2f,
            firstPlatformY + GameConfig.PLATFORM_H,
            pIdleTex, pIzqTex, pDerTex
        );

        maxY = player.rect.y;

        float y = firstPlatformY;

        for (int i = 0; i < 10; i++) {
            y += GameConfig.STEP_Y;
            float x = MathUtils.random(0f, GameConfig.VW - GameConfig.PLATFORM_W);

            Platform p = platformSystem.makePlatform(x, y, true);
            platformSystem.platforms.add(p);

            int tries = 2 + extraSpawnAttemptsForNivel();
            for (int t = 0; t < tries; t++) {
                int enemiesBefore = enemySystem.enemies.size;
                pickupSpawner.trySpawn(p, coinSystem, enemySystem);
                clampNewEnemiesToLevel(enemiesBefore);
                if (hasAnyThingOnPlatform(p)) break;
            }

            bootsSystem.trySpawnOnPlatformIfFree(p, coinSystem, enemySystem, BOOTS_CHANCE, false);
            shieldSystem.trySpawnOnPlatformIfFree(p, coinSystem, enemySystem, bootsSystem, SHIELD_CHANCE, false);

            mushroomSystem.trySpawnOnPlatformIfFree(
                p, coinSystem, enemySystem, bootsSystem, shieldSystem,
                SETA_CHANCE, false
            );
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
        drawFullScreen(fondoActual, worldW, worldH);

        // 2) layer nubes / estrellas
        if (cloudsEnabled) drawTiledParallaxLayer(fondoNubes, CLOUDS_PARALLAX, CLOUDS_ALPHA, worldW, worldH);
        if (starsEnabled) {
            Texture starsTex = (nivelVisual == 3 && fondoEstrellasColores != null)
                ? fondoEstrellasColores
                : fondoEstrellas;

            drawTiledParallaxLayer(starsTex, STARS_PARALLAX, STARS_ALPHA, worldW, worldH);
        }

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
                if (plataformaActual != null) batch.draw(plataformaActual, p.rect.x, p.rect.y, p.rect.width, p.rect.height);
                continue;
            }

            // Nivel 3: NO dibujar rota, solo desaparece
            if (nivelVisual == 2) continue;

            Texture brokenTex = plataformaRotaTex;           // nivel 1
            if (nivelVisual == 1) brokenTex = plataformaMediaRotaTex;    // nivel 2
            if (nivelVisual == 3) brokenTex = plataformaColoresRotaTex;  // nivel 4
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
        drawRect(banderaTex, goalFlagRect);

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
                drawScaledCentered(bootsTex, b.rect, BOOTS_DRAW_SCALE);
            }
        }

        // 8) escudos
        if (shieldTex != null) {
            for (int i = 0; i < shieldSystem.shields.size; i++) {
                Shield s = shieldSystem.shields.get(i);
                drawScaledCentered(shieldTex, s.rect, SHIELD_DRAW_SCALE);
            }
        }

        // 9) setas
        if (setaTex != null) {
            for (int i = 0; i < mushroomSystem.mushrooms.size; i++) {
                Mushroom m = mushroomSystem.mushrooms.get(i);
                drawScaledCentered(setaTex, m.rect, SETA_DRAW_SCALE);
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

            if (player.isShieldActive()) {
                if (base == pIzqTex) toDraw = pIzqShieldTex;
                else if (base == pDerTex) toDraw = pDerShieldTex;
                else toDraw = pIdleShieldTex;
            } else if (player.isSetaActive()) {
                if (base == pIzqTex) toDraw = pIzqSetaTex;
                else if (base == pDerTex) toDraw = pDerSetaTex;
                else toDraw = pIdleSetaTex;
            }

            if (toDraw != null) batch.draw(toDraw, player.rect.x, player.rect.y, player.rect.width, player.rect.height);
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
    // HUD
    // ==========================
    private void drawHud(float worldW, float worldH) {
        float uiLeft = cam.position.x - worldW / 2f;
        float uiBottom = cam.position.y - worldH / 2f;

        // Moneda + contador + iconos powers
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

            boolean showBoots  = (player != null && player.getBootsJumpsLeft() > 0);
            boolean showShield = (player != null && player.isShieldActive());
            boolean showSeta   = (player != null && player.isSetaActive());

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

        // Altura
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

            // 🔥 Centro exacto de la pantalla
            float cx = cam.position.x;
            float cy = cam.position.y;

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

        // Mensaje victoria
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

        // Press start
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

        if (goalReached) {
            goalTimer -= dt;
            if (goalTimer <= 0f) finishVictory();
            return;
        }

        // Movimiento horizontal (wrap). La inversión por seta ya está dentro de Player
        if (player != null) {
            player.updateHorizontal(dt);

            float w = player.rect.width;
            float worldW = GameConfig.VW;

            if (player.rect.x + w < 0f) player.rect.x = worldW;
            else if (player.rect.x > worldW) player.rect.x = -w;
        }

        // ==========================
        // DYING (incluye void fall)
        // ==========================
        if (dying) {

            if (player != null) {
                float g = GameConfig.GRAVITY;

                // ✅ si es caída libre definitiva, cae más rápido visualmente
                if (voidFalling) g *= 2.8f;

                player.velY -= g * dt;
                player.rect.y += player.velY * dt;
            }

            cam.position.x = GameConfig.VW / 2f;
            if (player != null) {
                float targetY = player.rect.y + CAM_Y_OFFSET;
                float a = 1f - (float) Math.exp(-CAM_FOLLOW_SPEED * dt);
                cam.position.y = MathUtils.lerp(cam.position.y, targetY, a);
            }
            cam.update();

            dyingTimer -= dt;
            if (dyingTimer <= 0f) finishDying();
            return;
        }

        if (started) runTimeSec += dt;

        platformSystem.updateMoving(dt);

        // ==========================
        // Física vertical + colisiones
        // ==========================
        if (started && player != null) {
            float oldY = player.rect.y;

            if (player.velY < 0f) {
                player.velY -= GameConfig.GRAVITY * FALL_MULT * dt;
                if (player.velY < -MAX_FALL_SPEED) player.velY = -MAX_FALL_SPEED;
            } else {
                player.velY -= GameConfig.GRAVITY * dt;
            }

            player.rect.y += player.velY * dt;

            handlePlatformCollision(oldY);

            // ✅ Detectar caída libre DEFINITIVA aquí (NO esperar a killY)
            // Solo cuando ya va cayendo
            boolean canTriggerVoidFall = (player.velY < 0f)
                && !dying
                && !goalReached
                && !(gameMode == GameMode.LEVELS && goalSpawned); // en final no queremos

            if (canTriggerVoidFall && !hasPlatformBelowPlayer(FALL_CHECK_RANGE)) {
                beginDying(VOID_FALL_DURATION, true); // ✅ 3s SIEMPRE
                return;
            }

            if (player.rect.y > maxY) {
                maxY = player.rect.y;
                score = (int) (maxY / 100f);

                if (gameMode == GameMode.LEVELS && goalSpawned) {
                    if (score > MODE_TARGET_METERS) score = MODE_TARGET_METERS;
                }
            }

            if (gameMode == GameMode.LEVELS) {
                if (score >= MODE_TARGET_METERS && !goalSpawned) spawnGoalPlatform();
                if (!goalSpawned) applyLevelByMeters(score, true);
            } else {
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

        platformSystem.updateBreakables(dt);

        if (levelUpMsgTime > 0f) {
            levelUpMsgTime -= dt;
            if (levelUpMsgTime < 0f) levelUpMsgTime = 0f;
        }

        // ==========================
        // Cámara (si final: congelada)
        // ==========================
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

        boolean canSpawnMore = !(gameMode == GameMode.LEVELS && goalSpawned);

        if (canSpawnMore) {
            float topVisible = cam.position.y + worldH / 2f + 300f;
            while (platformSystem.nextY < topVisible) spawnPlatformAbove();
        }

        float killY = bottomVisible - 200f;

        // Timers de power-ups del jugador
        if (player != null) player.updateEffects(dt);

        // Systems
        coinSystem.update(player, killY, onCoinCollected);
        bootsSystem.update(player, killY, onBootsCollected);
        shieldSystem.update(player, killY, onShieldCollected);
        mushroomSystem.update(player, killY, onMushroomCollected);
        enemySystem.update(player, killY, onEnemyHit);

        // Cull plataformas
        float keepBelow = 3f * GameConfig.STEP_Y + 250f;
        float cullY = player.rect.y - keepBelow;
        cullY = Math.min(cullY, bottomVisible - 200f);
        platformSystem.cullBelow(cullY);

        // Cull setas
        cullMushroomsWithMissingPlatforms();

        // Tocar bandera
        if (gameMode == GameMode.LEVELS && goalSpawned && goalFlagRect != null && player != null && !goalReached) {
            if (player.rect.overlaps(goalFlagRect)) {
                triggerGoalReached();
                return;
            }
        }

        // Hard death solo (por si cae MUY por debajo)
        if (started && player != null && !dying) {
            if (player.rect.y < killY - HARD_DEATH_EXTRA) {
                beginDying(HARD_DEATH_DURATION, false);
                return;
            }
        }
    }

    private void beginDying(float duration, boolean isVoidFall) {
        if (dying) return;

        dying = true;
        dyingTimer = duration;

        voidFalling = isVoidFall;
        playedVoidFallSfx = false;

        audio.stopFondo();

        // ✅ Si es caída libre definitiva: sonido INMEDIATO (no esperar a estar abajo)
        if (voidFalling) {
            audio.playCaidaPersonaje();
            playedVoidFallSfx = true;
        }
    }

    private boolean hasPlatformBelowPlayer(float range) {
        if (player == null) return false;

        float minY = player.rect.y - range;

        for (int i = 0; i < platformSystem.platforms.size; i++) {
            Platform p = platformSystem.platforms.get(i);
            if (p == null || p.broken) continue;

            float platTop = p.rect.y + p.rect.height;

            if (platTop < player.rect.y && platTop > minY) {
                return true;
            }
        }
        return false;
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

        int tries = 2 + extraSpawnAttemptsForNivel();
        for (int t = 0; t < tries; t++) {
            int enemiesBefore = enemySystem.enemies.size;
            pickupSpawner.trySpawn(p, coinSystem, enemySystem);
            clampNewEnemiesToLevel(enemiesBefore);
        }

        if (hasAnyThingOnPlatform(p)) return;

        bootsSystem.trySpawnOnPlatformIfFree(p, coinSystem, enemySystem, BOOTS_CHANCE, false);
        if (hasAnyThingOnPlatform(p)) return;

        shieldSystem.trySpawnOnPlatformIfFree(p, coinSystem, enemySystem, bootsSystem, SHIELD_CHANCE, false);
        if (hasAnyThingOnPlatform(p)) return;

        mushroomSystem.trySpawnOnPlatformIfFree(
            p, coinSystem, enemySystem, bootsSystem, shieldSystem,
            SETA_CHANCE, false
        );
    }

    // ==========================
    // FINAL MODO NIVELES
    // ==========================
    private void spawnGoalPlatform() {
        goalSpawned = true;

        float worldH = viewport.getWorldHeight();

        float goalX = (GameConfig.VW - GameConfig.PLATFORM_W) / 2f;

        float desiredPlatformTopOnScreen = (cam.position.y + worldH / 2f) - GOAL_TOP_MARGIN;
        float goalY = desiredPlatformTopOnScreen - GameConfig.PLATFORM_H;

        float minFinalY = MODE_TARGET_METERS * 100f + 120f;
        if (goalY < minFinalY) goalY = minFinalY;

        Platform p = new Platform(
            new Rectangle(goalX, goalY, GameConfig.PLATFORM_W, GameConfig.PLATFORM_H),
            false, 0f, false
        );
        p.dir = 0;

        platformSystem.platforms.add(p);
        goalPlatform = p;

        float fw = 120f;
        float fh = 160f;
        float fx = goalX + (GameConfig.PLATFORM_W - fw) / 2f;
        float fy = goalY + GameConfig.PLATFORM_H + 10f;
        goalFlagRect = new Rectangle(fx, fy, fw, fh);

        platformSystem.nextY = goalY;

        for (int i = platformSystem.platforms.size - 1; i >= 0; i--) {
            Platform other = platformSystem.platforms.get(i);
            if (other != p && other.rect.y > goalY + 20f) platformSystem.platforms.removeIndex(i);
        }

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
        audio.playVictory();
    }

    private void cullMushroomsWithMissingPlatforms() {
        for (int i = mushroomSystem.mushrooms.size - 1; i >= 0; i--) {
            Mushroom m = mushroomSystem.mushrooms.get(i);
            if (m == null || m.platform == null || !platformStillExists(m.platform)) {
                mushroomSystem.mushrooms.removeIndex(i);
            }
        }
    }

    private boolean platformStillExists(Platform plat) {
        for (int i = 0; i < platformSystem.platforms.size; i++) {
            if (platformSystem.platforms.get(i) == plat) return true;
        }
        return false;
    }

    // ==========================
    // Helpers plataforma: “hay algo encima”
    // ==========================
    private boolean hasAnyThingOnPlatform(Platform p) {
        if (p == null) return false;
        if (PlatformChecks.hasCoinOnPlatform(p, coinSystem)) return true;
        if (PlatformChecks.hasEnemyOnPlatform(p, enemySystem)) return true;
        if (PlatformChecks.hasBootsOnPlatform(p, bootsSystem)) return true;
        if (PlatformChecks.hasShieldOnPlatform(p, shieldSystem)) return true;
        if (PlatformChecks.hasMushroomOnPlatform(p, mushroomSystem)) return true;
        return false;
    }

    // ==========================
    // Colisión plataformas
    // ==========================
    private void handlePlatformCollision(float oldY) {
        if (player == null) return;
        if (player.velY >= 0) return;

        float feetW = player.rect.width * 0.35f;
        float feetX = player.rect.x + (player.rect.width - feetW) / 2f;

        float oldFeetY = oldY;
        float newFeetY = player.rect.y;

        float fallDist = oldFeetY - newFeetY;
        if (fallDist <= 0f) return;

        float eps = Math.max(25f, fallDist + 5f);

        Platform best = null;
        float bestTop = -Float.MAX_VALUE;

        for (int i = 0; i < platformSystem.platforms.size; i++) {
            Platform p = platformSystem.platforms.get(i);
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

        player.rect.y = bestTop;

        float jump = GameConfig.JUMP_VEL;

        boolean usedBoots = false;

        if (player != null && player.consumeBootsJump()) {
            jump *= BOOTS_MULT;
            usedBoots = true;
        }

        player.velY = jump;

        if (usedBoots) {
            audio.playTenis();
        } else {
            audio.playJump();
        }

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

        switch (nivelVisual) {
            case 0:
                fondoActual = fondoRosa;
                plataformaActual = plataformaRuinas;
                cloudsEnabled = true;
                starsEnabled = false;
                break;
            case 1:
                fondoActual = fondoAzul;
                plataformaActual = plataformaMedia;
                cloudsEnabled = true;
                starsEnabled = false;
                break;
            case 2:
                fondoActual = fondoAzulOscuro;
                plataformaActual = plataformaModerna;
                cloudsEnabled = false;
                starsEnabled = true;
                break;
            default:
                fondoActual = fondoAmarillo;
                plataformaActual = plataformaColores;
                cloudsEnabled = false;
                starsEnabled = true;
                break;
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
            case 0: return 1;
            case 1: return 2;
            case 2: return 3;
            case 3: return 4;
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
    private void finishDying() {

        GameSave.addRunToHistory(score, coinSystem.collected, runTimeSec);

        game.setScreen(new GameOverScreen(
            game,
            score,
            coinSystem.collected,
            audio   // 🔥 ahora pasamos el audio
        ));
    }

    private void finishVictory() {
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
