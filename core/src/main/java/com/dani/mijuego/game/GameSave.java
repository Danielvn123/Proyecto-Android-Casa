package com.dani.mijuego.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Rectangle;
import com.dani.mijuego.game.entities.Player;
import com.dani.mijuego.game.world.Platform;
import com.dani.mijuego.game.world.PlatformSystem;
public final class GameSave {

    private GameSave() {}

    private static final String PREFS_NAME = "savegame";

    private static final String K_HAS_SAVE = "hasSave";
    private static final String K_STARTED  = "started";
    private static final String K_SCORE    = "score";
    private static final String K_MAXY     = "maxY";
    private static final String K_VELY     = "velY";
    private static final String K_NIVEL    = "nivelVisual";

    private static final String K_PLAYERX  = "playerX";
    private static final String K_PLAYERY  = "playerY";

    private static final String K_CAMX     = "camX";
    private static final String K_CAMY     = "camY";

    private static final String K_NEXTY    = "nextY";

    private static final String K_RUINAS_BASEY = "ruinasBaseY";
    private static final String K_RUINAS_ALPHA = "ruinasAlpha";
    private static final String K_RUINAS_FADING = "ruinasFading";
    private static final String K_RUINAS_OFF = "ruinasOff";

    private static final String K_PLAT_COUNT = "platCount";
    private static final String K_PLAT_X = "platX_";
    private static final String K_PLAT_Y = "platY_";
    private static final String K_PLAT_MOV = "platMov_";
    private static final String K_PLAT_SPD = "platSpd_";
    private static final String K_PLAT_DIR = "platDir_";
    private static final String K_PLAT_BREAK = "platBreak_";
    private static final String K_PLAT_BROKEN = "platBroken_";
    private static final String K_PLAT_BROKENT = "platBrokenT_";

    public static void clear() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.clear();
        prefs.flush();
    }

    public static void save(State s) {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);

        prefs.putBoolean(K_HAS_SAVE, true);

        prefs.putBoolean(K_STARTED, s.started);
        prefs.putInteger(K_SCORE, s.score);
        prefs.putFloat(K_MAXY, s.maxY);
        prefs.putFloat(K_VELY, s.player.velY);
        prefs.putInteger(K_NIVEL, s.nivelVisual);

        prefs.putFloat(K_PLAYERX, s.player.rect.x);
        prefs.putFloat(K_PLAYERY, s.player.rect.y);

        prefs.putFloat(K_CAMX, s.camX);
        prefs.putFloat(K_CAMY, s.camY);

        prefs.putFloat(K_NEXTY, s.platforms.nextY);

        prefs.putFloat(K_RUINAS_BASEY, s.ruinasBaseY);
        prefs.putFloat(K_RUINAS_ALPHA, s.ruinasAlpha);
        prefs.putBoolean(K_RUINAS_FADING, s.ruinasFading);
        prefs.putBoolean(K_RUINAS_OFF, s.ruinasOff);

        prefs.putInteger(K_PLAT_COUNT, s.platforms.platforms.size);
        for (int i = 0; i < s.platforms.platforms.size; i++) {
            Platform p = s.platforms.platforms.get(i);

            prefs.putFloat(K_PLAT_X + i, p.rect.x);
            prefs.putFloat(K_PLAT_Y + i, p.rect.y);

            prefs.putBoolean(K_PLAT_MOV + i, p.moving);
            prefs.putFloat(K_PLAT_SPD + i, p.speed);
            prefs.putInteger(K_PLAT_DIR + i, p.dir);

            prefs.putBoolean(K_PLAT_BREAK + i, p.breakable);
            prefs.putBoolean(K_PLAT_BROKEN + i, p.broken);
            prefs.putFloat(K_PLAT_BROKENT + i, p.brokenTime);
        }

        prefs.flush();
    }

    public static Loaded loadOrNull() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        if (!prefs.getBoolean(K_HAS_SAVE, false)) return null;

        Loaded l = new Loaded();
        l.started = prefs.getBoolean(K_STARTED, false);
        l.score = prefs.getInteger(K_SCORE, 0);
        l.maxY = prefs.getFloat(K_MAXY, 0f);
        l.velY = prefs.getFloat(K_VELY, 0f);
        l.nivelVisual = prefs.getInteger(K_NIVEL, 0);

        l.playerX = prefs.getFloat(K_PLAYERX, (GameConfig.VW - GameConfig.PLAYER_W) / 2f);
        l.playerY = prefs.getFloat(K_PLAYERY, 200f);

        l.camX = prefs.getFloat(K_CAMX, GameConfig.VW / 2f);
        l.camY = prefs.getFloat(K_CAMY, GameConfig.VH / 2f);

        l.nextY = prefs.getFloat(K_NEXTY, l.playerY);

        l.ruinasBaseY = prefs.getFloat(K_RUINAS_BASEY, 0f);
        l.ruinasAlpha = prefs.getFloat(K_RUINAS_ALPHA, 1f);
        l.ruinasFading = prefs.getBoolean(K_RUINAS_FADING, false);
        l.ruinasOff = prefs.getBoolean(K_RUINAS_OFF, false);

        l.platformCount = prefs.getInteger(K_PLAT_COUNT, 0);
        l.platX = new float[l.platformCount];
        l.platY = new float[l.platformCount];
        l.platMov = new boolean[l.platformCount];
        l.platSpd = new float[l.platformCount];
        l.platDir = new int[l.platformCount];
        l.platBreak = new boolean[l.platformCount];
        l.platBroken = new boolean[l.platformCount];
        l.platBrokenT = new float[l.platformCount];

        for (int i = 0; i < l.platformCount; i++) {
            l.platX[i] = prefs.getFloat(K_PLAT_X + i, 0f);
            l.platY[i] = prefs.getFloat(K_PLAT_Y + i, 0f);
            l.platMov[i] = prefs.getBoolean(K_PLAT_MOV + i, false);
            l.platSpd[i] = prefs.getFloat(K_PLAT_SPD + i, 0f);
            l.platDir[i] = prefs.getInteger(K_PLAT_DIR + i, 1);
            l.platBreak[i] = prefs.getBoolean(K_PLAT_BREAK + i, false);
            l.platBroken[i] = prefs.getBoolean(K_PLAT_BROKEN + i, false);
            l.platBrokenT[i] = prefs.getFloat(K_PLAT_BROKENT + i, 0f);
        }

        return l;
    }

    public static final class State {
        public boolean started;
        public int score;
        public float maxY;
        public int nivelVisual;

        public Player player;
        public PlatformSystem platforms;

        public float camX;
        public float camY;

        public float ruinasBaseY;
        public float ruinasAlpha;
        public boolean ruinasFading;
        public boolean ruinasOff;
    }

    public static final class Loaded {
        public boolean started;
        public int score;
        public float maxY;
        public float velY;
        public int nivelVisual;

        public float playerX;
        public float playerY;

        public float camX;
        public float camY;

        public float nextY;

        public float ruinasBaseY;
        public float ruinasAlpha;
        public boolean ruinasFading;
        public boolean ruinasOff;

        public int platformCount;
        public float[] platX;
        public float[] platY;
        public boolean[] platMov;
        public float[] platSpd;
        public int[] platDir;
        public boolean[] platBreak;
        public boolean[] platBroken;
        public float[] platBrokenT;
    }
}
