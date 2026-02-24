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
import com.dani.mijuego.game.GameMode;
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
    final GameMode gameMode;

    static final int MODE_TARGET_METERS = 800;
    int lapIndex = 0;

    // ==========================
    // Fonts / HUD
    // ==========================
    BitmapFont startFillFont;
    BitmapFont startOutlineFont;

    // Caída más rápida que la subida
    static final float FALL_MULT = 2.0f;
    static final float MAX_FALL_SPEED = 3200f;

    Texture btnPauseTex;
    Rectangle btnPause;

    static final float VOID_FALL_DURATION = 3.0f;
    static final float HARD_DEATH_DURATION = 0.35f;

    boolean voidFalling = false;
    boolean playedVoidFallSfx = false;

    // ==========================
    // Fondos base
    // ==========================
    Texture fondoRosa;
    Texture fondoAzul;
    Texture fondoAzulOscuro;
    Texture fondoAmarillo;
    Texture fondoActual;

    // Layers
    Texture fondoNubes;
    boolean cloudsEnabled = false;
    static final float CLOUDS_PARALLAX = 0.25f;
    static final float CLOUDS_ALPHA = 0.90f;

    Texture fondoEstrellas;
    boolean starsEnabled = false;

    Texture fondoEstrellasColores;
    static final float STARS_PARALLAX = 0.18f;
    static final float STARS_ALPHA = 0.95f;

    // ==========================
    // Plataformas
    // ==========================
    Texture plataformaRuinas;
    Texture plataformaMedia;
    Texture plataformaModerna;

    Texture plataformaRotaTex;        // nivel 1
    Texture plataformaMediaRotaTex;   // nivel 2
    Texture plataformaColores;        // nivel 4
    Texture plataformaColoresRotaTex; // nivel 4 rota

    Texture plataformaActual;

    // ==========================
    // Decor / Coins
    // ==========================
    Texture ruinasTex;
    Texture monedaTex;

    // ==========================
    // Enemigos
    // ==========================
    Texture enemyLilaTex;
    Texture enemyAzulTex;
    Texture enemyVerdeTex;
    Texture enemyRojoTex;

    // ==========================
    // Player
    // ==========================
    Texture pIdleTex;
    Texture pIzqTex;
    Texture pDerTex;

    // Escudo skins
    Texture pIdleShieldTex;
    Texture pIzqShieldTex;
    Texture pDerShieldTex;

    // ==========================
    // Tenis
    // ==========================
    Texture bootsTex;
    final JumpBootsSystem bootsSystem = new JumpBootsSystem();
    static final float BOOTS_MULT = 1.9f;
    static final float BOOTS_CHANCE = 0.06f;
    static final float BOOTS_DRAW_SCALE = 1.6f;

    float runTimeSec = 0f;

    // ==========================
    // Escudo
    // ==========================
    Texture shieldTex;
    final ShieldSystem shieldSystem = new ShieldSystem();
    static final float SHIELD_CHANCE = 0.05f;
    static final float SHIELD_DRAW_SCALE = 1.6f;

    // ==========================
    // SETA
    // ==========================
    Texture setaTex;
    Texture pIdleSetaTex;
    Texture pIzqSetaTex;
    Texture pDerSetaTex;

    final MushroomSystem mushroomSystem = new MushroomSystem();
    static final float SETA_CHANCE = 0.04f;
    static final float SETA_DURATION = 5.0f;
    static final float SETA_DRAW_SCALE = 1.6f;

    // ==========================
    // FINAL MODO NIVELES (plataforma + bandera)
    // ==========================
    Texture banderaTex;

    boolean goalSpawned = false;
    boolean goalReached = false;

    Platform goalPlatform = null;
    Rectangle goalFlagRect = null;

    boolean goalCamFrozen = false;
    float goalFrozenCamY = 0f;

    float goalTimer = 0f;
    static final float GOAL_DELAY_TO_SCREEN = 2.0f;

    static final float GOAL_TOP_MARGIN = 180f;
    String goalMsg = null;

    // ==========================
    // HUD Power Icons
    // ==========================
    static final float POWER_ICON_SIZE = 110f;
    static final float POWER_ICON_GAP = 12f;

    // ==========================
    // World / Systems
    // ==========================
    Player player;
    final PlatformSystem platformSystem = new PlatformSystem();
    final RuinsLayer ruins = new RuinsLayer();

    final CoinSystem coinSystem = new CoinSystem();
    final EnemySystem enemySystem = new EnemySystem();
    final PickupSpawner pickupSpawner = new PickupSpawner();

    GameAudio audio;
    boolean started = false;
    boolean initialized = false;

    float maxY = 0f;
    int score = 0;

    /**
     * NIVEL (0..3):
     * 0: lila
     * 1: lila + verde
     * 2: lila + verde + azul
     * 3: lila + verde + azul + rojo
     */
    int nivelVisual = 0;

    float levelUpMsgTime = 0f;
    static final float LEVEL_UP_MSG_DURATION = 2.0f;
    String levelUpMsgText = null;

    float startAnimTime = 0f;

    // ===== Muerte / transición =====
    boolean dying = false;
    float dyingTimer = 0f;

    float groundY = 0f;
    static final float CAM_Y_OFFSET = 500f;
    static final float CAM_FOLLOW_SPEED = 10f;

    static final float HARD_DEATH_EXTRA = 300f;
    static final float FALL_CHECK_RANGE = 1400f;

    // ==========================
    // Helpers
    // ==========================
    final Vector3 tmp = new Vector3();

    final CoinSystem.OnCoinCollected onCoinCollected = new CoinSystem.OnCoinCollected() {
        @Override public void onCoinCollected() { audio.playCoin(); }
    };

    final JumpBootsSystem.OnBootsCollected onBootsCollected = new JumpBootsSystem.OnBootsCollected() {
        @Override public void onBootsCollected() {
            if (player != null) player.giveBoots(1);
            audio.playCogerItem();
        }
    };

    final ShieldSystem.OnShieldCollected onShieldCollected = new ShieldSystem.OnShieldCollected() {
        @Override public void onShieldCollected() {
            if (player != null) player.tryActivateShield();
            audio.playCogerItem();
        }
    };

    final MushroomSystem.OnMushroomCollected onMushroomCollected = new MushroomSystem.OnMushroomCollected() {
        @Override public void onMushroomCollected() {
            if (player == null) return;

            boolean activated = player.tryActivateSeta(SETA_DURATION);
            if (!activated) audio.playEscudoRoto();
            audio.playCogerItem();
        }
    };

    final EnemySystem.OnEnemyHit onEnemyHit = new EnemySystem.OnEnemyHit() {
        @Override public boolean onEnemyHit(EnemyType type) {
            if (player != null && player.blockEnemyHitIfShield()) {
                audio.playEscudoRoto();
                return true;
            }
            audio.playEnemy(type);
            return false;
        }
    };

    GameLogic logic;
    GameRenderer renderer;

    public GameScreen(Main game, GameMode mode) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.gameMode = (mode == null) ? GameMode.LEVELS : mode;
        this.audio = game.audio;
    }

    public GameScreen(Main game) {
        this(game, GameMode.LEVELS);
    }

    @Override
    public void show() {

        if (!initialized) {
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
            plataformaRuinas  = mustTex(Assets.PLAT_RUINAS);
            plataformaMedia   = mustTex(Assets.PLAT_MEDIA);
            plataformaModerna = mustTex(Assets.PLAT_MODERNA);

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

            // Seta (sin strings)
            setaTex      = mustTex(Assets.SETA);
            pIdleSetaTex = mustTex(Assets.PLAYER_SETA_IDLE);
            pIzqSetaTex  = mustTex(Assets.PLAYER_SETA_IZQ);
            pDerSetaTex  = mustTex(Assets.PLAYER_SETA_DER);

            // Bandera
            banderaTex = mustTex(Assets.BANDERA);

            audio.load();

            nivelVisual = -1;
            lapIndex = 0;
            setNivelVisual(0, false);

            logic = new GameLogic(this);
            renderer = new GameRenderer(this);

            updateUiPositions();
            installInput();
        } else {
            updateUiPositions();
            installInput();
        }
    }

    Texture mustTex(String id) {
        Texture t = getTex(id);
        if (t == null) throw new RuntimeException("TEXTURA NULL: " + id);
        return t;
    }

    void installInput() {
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

    void startGameIfNeeded() {
        if (started) return;
        started = true;
        player.velY = GameConfig.JUMP_VEL;
    }

    void updateUiPositions() {
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

        logic.update(dt);

        ScreenUtils.clear(0, 0, 0, 1);
        viewport.apply();
        batch.setProjectionMatrix(cam.combined);

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        batch.begin();
        renderer.draw(worldW, worldH);
        batch.end();
    }

    void drawFullScreen(Texture tex, float worldW, float worldH) {
        if (tex == null) return;
        batch.draw(tex,
            cam.position.x - worldW / 2f,
            cam.position.y - worldH / 2f,
            worldW,
            worldH
        );
    }

    void drawTiledParallaxLayer(Texture tex, float parallax, float alpha, float worldW, float worldH) {
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

    void drawScaledCentered(Texture tex, Rectangle r, float scale) {
        if (tex == null || r == null) return;

        float drawW = r.width * scale;
        float drawH = r.height * scale;
        float drawX = r.x + (r.width - drawW) / 2f;
        float drawY = r.y + (r.height - drawH) / 2f;

        batch.draw(tex, drawX, drawY, drawW, drawH);
    }

    // ======= Nivel visual =======
    void setNivelVisual(int nivel, boolean playSfx) {
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

    // ======= Helpers plataforma =======
    boolean hasAnyThingOnPlatform(Platform p) {
        if (p == null) return false;
        if (PlatformChecks.hasCoinOnPlatform(p, coinSystem)) return true;
        if (PlatformChecks.hasEnemyOnPlatform(p, enemySystem)) return true;
        if (PlatformChecks.hasBootsOnPlatform(p, bootsSystem)) return true;
        if (PlatformChecks.hasShieldOnPlatform(p, shieldSystem)) return true;
        if (PlatformChecks.hasMushroomOnPlatform(p, mushroomSystem)) return true;
        return false;
    }

    // ======= Enemy texture =======
    Texture enemyTexture(EnemyType type) {
        if (type == null) return null;
        switch (type) {
            case LILA:  return enemyLilaTex;
            case VERDE: return enemyVerdeTex;
            case AZUL:  return enemyAzulTex;
            case ROJO:  return enemyRojoTex;
        }
        return null;
    }

    // ======= Pantallas finales =======
    void finishDying() {
        GameSave.addRunToHistory(score, coinSystem.collected, runTimeSec);
        game.setScreen(new GameOverScreen(game, score, coinSystem.collected, audio));
    }

    void finishVictory() {
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
}
