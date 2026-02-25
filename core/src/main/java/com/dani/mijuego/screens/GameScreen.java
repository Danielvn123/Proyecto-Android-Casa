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

    // Modo de juego actual (LEVELS o INFINITE)
    final GameMode gameMode;

    // Objetivo de metros por “vuelta”/nivel (también usado como meta en modo niveles)
    static final int MODE_TARGET_METERS = 800;

    // Índice de vuelta actual en modo infinito (cada 800m incrementa)
    int lapIndex = 0;

    // ==========================
    // Fonts / HUD
    // ==========================

    // Fuentes dedicadas para mensajes grandes (inicio, level up, etc.)
    BitmapFont startFillFont;
    BitmapFont startOutlineFont;

    // Multiplicador de caída para que caiga más rápido que sube + límite de velocidad
    static final float FALL_MULT = 2.0f;
    static final float MAX_FALL_SPEED = 3200f;

    // Textura y rectángulo del botón de pausa
    Texture btnPauseTex;
    Rectangle btnPause;

    // Duración de animación/muerte por caída al vacío y muerte rápida “hard death”
    static final float VOID_FALL_DURATION = 3.0f;
    static final float HARD_DEATH_DURATION = 0.35f;

    // Flags para el estado de “caída al vacío” y evitar repetir SFX
    boolean voidFalling = false;
    boolean playedVoidFallSfx = false;

    // ==========================
    // Fondos base
    // ==========================

    // Texturas de los fondos según nivel visual
    Texture fondoRosa;
    Texture fondoAzul;
    Texture fondoAzulOscuro;
    Texture fondoAmarillo;
    Texture fondoActual;

    // Layers
    // Capa de nubes (parallax) y su configuración
    Texture fondoNubes;
    boolean cloudsEnabled = false;
    static final float CLOUDS_PARALLAX = 0.25f;
    static final float CLOUDS_ALPHA = 0.90f;

    // Capa de estrellas (parallax) y su configuración
    Texture fondoEstrellas;
    boolean starsEnabled = false;

    // Variante de estrellas (colores) y parámetros de parallax/alpha
    Texture fondoEstrellasColores;
    static final float STARS_PARALLAX = 0.18f;
    static final float STARS_ALPHA = 0.95f;

    // ==========================
    // Plataformas
    // ==========================

    // Texturas de plataformas por “era”/nivel visual
    Texture plataformaRuinas;
    Texture plataformaMedia;
    Texture plataformaModerna;

    // Texturas de plataformas rotas según nivel visual
    Texture plataformaRotaTex;        // nivel 1
    Texture plataformaMediaRotaTex;   // nivel 2
    Texture plataformaColores;        // nivel 4
    Texture plataformaColoresRotaTex; // nivel 4 rota

    // Textura de plataforma actualmente usada según nivel visual
    Texture plataformaActual;

    // ==========================
    // Decor / Coins
    // ==========================

    // Textura de ruinas (decoración) y moneda
    Texture ruinasTex;
    Texture monedaTex;

    // ==========================
    // Enemigos
    // ==========================

    // Texturas de enemigos por tipo/color
    Texture enemyLilaTex;
    Texture enemyAzulTex;
    Texture enemyVerdeTex;
    Texture enemyRojoTex;

    // ==========================
    // Player
    // ==========================

    // Texturas base del jugador (idle, izquierda, derecha)
    Texture pIdleTex;
    Texture pIzqTex;
    Texture pDerTex;

    // Escudo skins
    // Texturas del jugador cuando el escudo está activo
    Texture pIdleShieldTex;
    Texture pIzqShieldTex;
    Texture pDerShieldTex;

    // ==========================
    // Tenis
    // ==========================

    // Textura de botas y sistema que las gestiona
    Texture bootsTex;
    final JumpBootsSystem bootsSystem = new JumpBootsSystem();

    // Multiplicador de salto, probabilidad de spawn y escala de dibujo
    static final float BOOTS_MULT = 1.9f;
    static final float BOOTS_CHANCE = 0.06f;
    static final float BOOTS_DRAW_SCALE = 1.6f;

    // Tiempo total de partida (para récords)
    float runTimeSec = 0f;

    // ==========================
    // Escudo
    // ==========================

    // Textura de escudo y sistema de gestión
    Texture shieldTex;
    final ShieldSystem shieldSystem = new ShieldSystem();

    // Probabilidad de spawn y escala
    static final float SHIELD_CHANCE = 0.05f;
    static final float SHIELD_DRAW_SCALE = 1.6f;

    // ==========================
    // SETA
    // ==========================

    // Texturas de seta y skins del jugador cuando la seta está activa
    Texture setaTex;
    Texture pIdleSetaTex;
    Texture pIzqSetaTex;
    Texture pDerSetaTex;

    // Sistema de setas + probabilidad, duración del efecto y escala de dibujo
    final MushroomSystem mushroomSystem = new MushroomSystem();
    static final float SETA_CHANCE = 0.04f;
    static final float SETA_DURATION = 5.0f;
    static final float SETA_DRAW_SCALE = 1.6f;

    // ==========================
    // FINAL MODO NIVELES (plataforma + bandera)
    // ==========================

    // Textura de la bandera final
    Texture banderaTex;

    // Flags del final del modo niveles
    boolean goalSpawned = false;
    boolean goalReached = false;

    // Referencias a la plataforma final y al rectángulo de colisión de la bandera
    Platform goalPlatform = null;
    Rectangle goalFlagRect = null;

    // Congelar cámara para que se vea bien la meta al generarla
    boolean goalCamFrozen = false;
    float goalFrozenCamY = 0f;

    // Timer para retrasar el paso a la VictoryScreen
    float goalTimer = 0f;
    static final float GOAL_DELAY_TO_SCREEN = 2.0f;

    // Margen superior para colocar la meta en pantalla y texto de victoria
    static final float GOAL_TOP_MARGIN = 180f;
    String goalMsg = null;

    // ==========================
    // HUD Power Icons
    // ==========================

    // Tamaño y separación de iconos de power-ups en el HUD
    static final float POWER_ICON_SIZE = 110f;
    static final float POWER_ICON_GAP = 12f;

    // ==========================
    // World / Systems
    // ==========================

    // Jugador y sistemas principales del mundo
    Player player;
    final PlatformSystem platformSystem = new PlatformSystem();
    final RuinsLayer ruins = new RuinsLayer();

    // Sistemas de monedas, enemigos y spawner de pickups
    final CoinSystem coinSystem = new CoinSystem();
    final EnemySystem enemySystem = new EnemySystem();
    final PickupSpawner pickupSpawner = new PickupSpawner();

    // Audio principal del juego
    GameAudio audio;

    // Flags de inicio y de inicialización de recursos
    boolean started = false;
    boolean initialized = false;

    // Altura máxima alcanzada y score en metros
    float maxY = 0f;
    int score = 0;

    /**
     * NIVEL (0..3):
     * 0: lila
     * 1: lila + verde
     * 2: lila + verde + azul
     * 3: lila + verde + azul + rojo
     */
    // Nivel visual actual (determina fondos/plataformas y tipos de enemigos)
    int nivelVisual = 0;

    // Mensaje temporal de “subida de nivel” (texto + tiempo restante)
    float levelUpMsgTime = 0f;
    static final float LEVEL_UP_MSG_DURATION = 2.0f;
    String levelUpMsgText = null;

    // Tiempo acumulado para animación del texto de inicio
    float startAnimTime = 0f;

    // ===== Muerte / transición =====

    // Estado de muerte y temporizador de transición a GameOver
    boolean dying = false;
    float dyingTimer = 0f;

    // Suelo inicial (parte baja visible al inicio)
    float groundY = 0f;

    // Configuración cámara: offset vertical respecto al jugador y suavizado
    static final float CAM_Y_OFFSET = 500f;
    static final float CAM_FOLLOW_SPEED = 10f;

    // Parámetros para muerte dura y detección de caída al vacío
    static final float HARD_DEATH_EXTRA = 300f;
    static final float FALL_CHECK_RANGE = 1400f;

    // ==========================
    // Helpers
    // ==========================

    // Vector temporal reutilizable para conversiones de coordenadas
    final Vector3 tmp = new Vector3();

    // Callback: cuando se recoge una moneda, reproduce sonido de moneda
    final CoinSystem.OnCoinCollected onCoinCollected = new CoinSystem.OnCoinCollected() {
        @Override public void onCoinCollected() { audio.playCoin(); }
    };

    // Callback: cuando se recogen botas, se da un salto extra de botas al jugador y suena SFX
    final JumpBootsSystem.OnBootsCollected onBootsCollected = new JumpBootsSystem.OnBootsCollected() {
        @Override public void onBootsCollected() {
            if (player != null) player.giveBoots(1);
            audio.playCogerItem();
        }
    };

    // Callback: cuando se recoge escudo, intenta activar escudo en el jugador y suena SFX
    final ShieldSystem.OnShieldCollected onShieldCollected = new ShieldSystem.OnShieldCollected() {
        @Override public void onShieldCollected() {
            if (player != null) player.tryActivateShield();
            audio.playCogerItem();
        }
    };

    // Callback: cuando se recoge seta, intenta activar el efecto (invertir controles)
    // si no se puede activar, reproduce sonido (por ejemplo “bloqueado”)
    final MushroomSystem.OnMushroomCollected onMushroomCollected = new MushroomSystem.OnMushroomCollected() {
        @Override public void onMushroomCollected() {
            if (player == null) return;

            boolean activated = player.tryActivateSeta(SETA_DURATION);
            if (!activated) audio.playEscudoRoto();
            audio.playCogerItem();
        }
    };

    // Callback: cuando se golpea un enemigo:
    // si hay escudo activo, bloquea el golpe y consume/rompe escudo; si no, reproduce sonido del enemigo
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

    // Lógica y renderer separadas para mantener GameScreen limpia (update vs draw)
    GameLogic logic;
    GameRenderer renderer;

    // Constructor principal: crea la GameScreen con modo y enlaza audio del juego
    public GameScreen(Main game, GameMode mode) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.gameMode = (mode == null) ? GameMode.LEVELS : mode;
        this.audio = game.audio;
    }

    // Constructor por defecto: modo niveles
    public GameScreen(Main game) {
        this(game, GameMode.LEVELS);
    }

    // Se ejecuta al entrar en GameScreen:
    // carga texturas, configura fuentes, audio, crea lógica/renderer, inicializa UI e input
    @Override
    public void show() {

        if (!initialized) {
            initialized = true;

            // Inicializa fuentes y layout (HUD y textos)
            font = new BitmapFont();
            startFillFont = new BitmapFont();
            startOutlineFont = new BitmapFont();
            layout = new GlyphLayout();

            // Estilo de los textos grandes (relleno + contorno)
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

            // Carga audio (música y efectos)
            audio.load();

            // Fuerza recomputar nivel visual al iniciar (para asignar texturas correctas)
            nivelVisual = -1;
            lapIndex = 0;
            setNivelVisual(0, false);

            // Crea la lógica del juego y el renderer
            logic = new GameLogic(this);
            renderer = new GameRenderer(this);

            // Calcula UI (botón pausa) y configura input
            updateUiPositions();
            installInput();
        } else {
            // Si ya estaba inicializado (por ejemplo al volver desde pausa), solo recalcula UI e input
            updateUiPositions();
            installInput();
        }
    }

    // Helper: obtiene una textura y lanza excepción si no existe (para detectar fallos de assets)
    Texture mustTex(String id) {
        Texture t = getTex(id);
        if (t == null) throw new RuntimeException("TEXTURA NULL: " + id);
        return t;
    }

    // Instala el input principal del juego:
    // - BACK/ESC abre PauseScreen
    // - SPACE/ENTER o touch inicia la partida si aún no ha empezado
    // - Touch en botón pausa abre PauseScreen
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

    // Inicia el juego si todavía no ha empezado:
    // marca started y aplica un salto inicial al jugador
    void startGameIfNeeded() {
        if (started) return;
        started = true;
        player.velY = GameConfig.JUMP_VEL;
    }

    // Recalcula posiciones del HUD (principalmente el botón de pausa) según el tamaño del mundo
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

    // Resize: actualiza el viewport y recoloca UI (sin centrar cámara automáticamente)
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        updateUiPositions();
    }

    // Render principal:
    // limita delta para estabilidad, actualiza lógica y luego dibuja con GameRenderer
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

    // Dibuja una textura a pantalla completa alineada con la cámara (fondo principal)
    void drawFullScreen(Texture tex, float worldW, float worldH) {
        if (tex == null) return;
        batch.draw(tex,
            cam.position.x - worldW / 2f,
            cam.position.y - worldH / 2f,
            worldW,
            worldH
        );
    }

    // Dibuja una capa parallax “tileada” verticalmente según la altura de la cámara
    // (se repite 3 veces para cubrir el scroll)
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

    // Dibuja una textura centrada sobre un Rectangle aplicando un multiplicador de escala
    // (ideal para power-ups para que se vean más grandes)
    void drawScaledCentered(Texture tex, Rectangle r, float scale) {
        if (tex == null || r == null) return;

        float drawW = r.width * scale;
        float drawH = r.height * scale;
        float drawX = r.x + (r.width - drawW) / 2f;
        float drawY = r.y + (r.height - drawH) / 2f;

        batch.draw(tex, drawX, drawY, drawW, drawH);
    }

    // ======= Nivel visual =======

    // Cambia el “nivel visual”:
    // asigna fondo/plataforma, activa/desactiva nubes/estrellas y opcionalmente reproduce SFX + mensaje
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

        // Si se permite, reproduce sonido de subida de nivel y muestra mensaje
        if (playSfx && oldNivel >= 0 && (nivelVisual == 1 || nivelVisual == 2 || nivelVisual == 3)) {
            levelUpMsgText = I18n.t("msg_levelup");
            levelUpMsgTime = LEVEL_UP_MSG_DURATION;
            audio.playLevelUp();
        }
    }

    // ======= Helpers plataforma =======

    // Comprueba si una plataforma ya tiene cualquier cosa spawneada encima
    // (moneda, enemigo, botas, escudo o seta)
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

    // Devuelve la textura del enemigo según su tipo/color
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

    // Termina la muerte: guarda partida en historial y pasa a GameOverScreen
    void finishDying() {
        GameSave.addRunToHistory(score, coinSystem.collected, runTimeSec);
        game.setScreen(new GameOverScreen(game, score, coinSystem.collected, audio));
    }

    // Termina la victoria: guarda partida en historial y pasa a VictoryScreen
    void finishVictory() {
        GameSave.addRunToHistory(score, coinSystem.collected, runTimeSec);
        game.setScreen(new VictoryScreen(game, score, coinSystem.collected, audio));
    }

    // Libera recursos extra de esta pantalla (fuentes y audio)
    @Override
    public void dispose() {
        super.dispose();
        if (startFillFont != null) startFillFont.dispose();
        if (startOutlineFont != null) startOutlineFont.dispose();
        audio.dispose();
    }
}
