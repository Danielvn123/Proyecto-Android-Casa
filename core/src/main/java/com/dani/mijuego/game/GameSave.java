package com.dani.mijuego.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
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

    private static final String K_RUINAS_BASEY  = "ruinasBaseY";
    private static final String K_RUINAS_ALPHA  = "ruinasAlpha";
    private static final String K_RUINAS_FADING = "ruinasFading";
    private static final String K_RUINAS_OFF    = "ruinasOff";

    private static final String K_PLAT_COUNT   = "platCount";
    private static final String K_PLAT_X       = "platX_";
    private static final String K_PLAT_Y       = "platY_";
    private static final String K_PLAT_MOV     = "platMov_";
    private static final String K_PLAT_SPD     = "platSpd_";
    private static final String K_PLAT_DIR     = "platDir_";
    private static final String K_PLAT_BREAK   = "platBreak_";
    private static final String K_PLAT_BROKEN  = "platBroken_";
    private static final String K_PLAT_BROKENT = "platBrokenT_";

    // =========================
    // IDIOMA
    // =========================
    private static final String K_LANG = "lang"; // "ES" o "EN"

    // =========================
    // OPCIONES
    // =========================
    private static final String K_MUSIC_ON  = "music_on";
    private static final String K_VIBRA_ON  = "vibra_on";
    private static final String K_CTRL_MODE = "ctrl_mode"; // 0=TOUCH, 1=TILT
    private static final String K_CTRL_SENS = "ctrl_sens"; // 0.8..1.4

    // =========================
    // TOP 10
    // =========================
    private static final String K_RUNS = "runs_history";
    private static final int MAX_RUNS = 10;

    public static final class Run {
        public long timeMs;
        public int heightMeters;
        public int coins;
        public float timeSec;
    }

    // =========================
    // CLEAR ALL
    // =========================
    public static void clear() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.clear();
        prefs.flush();
    }

    // =========================
    // IDIOMA
    // =========================
    public static void setLang(String lang) {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putString(K_LANG, (lang == null) ? "ES" : lang);
        prefs.flush();
    }

    public static String getLang() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        return prefs.getString(K_LANG, "ES");
    }

    // =========================
    // OPCIONES
    // =========================
    public static void setMusicOn(boolean on) {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putBoolean(K_MUSIC_ON, on);
        prefs.flush();
    }

    public static boolean isMusicOn() {
        return Gdx.app.getPreferences(PREFS_NAME).getBoolean(K_MUSIC_ON, true);
    }

    public static void setVibrationOn(boolean on) {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putBoolean(K_VIBRA_ON, on);
        prefs.flush();
    }

    public static boolean isVibrationOn() {
        return Gdx.app.getPreferences(PREFS_NAME).getBoolean(K_VIBRA_ON, true);
    }

    public static void setControlModeTilt(boolean tilt) {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putInteger(K_CTRL_MODE, tilt ? 1 : 0);
        prefs.flush();
    }

    public static boolean isControlModeTilt() {
        return Gdx.app.getPreferences(PREFS_NAME).getInteger(K_CTRL_MODE, 0) == 1;
    }

    public static void setControlSensitivity(float sens) {
        if (sens < 0.8f) sens = 0.8f;
        if (sens > 1.4f) sens = 1.4f;

        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putFloat(K_CTRL_SENS, sens);
        prefs.flush();
    }

    public static float getControlSensitivity() {
        return Gdx.app.getPreferences(PREFS_NAME).getFloat(K_CTRL_SENS, 1.0f);
    }

    // =========================================================
    // SAVE PARTIDA (completo, compatible con tu saveGame())
    // =========================================================
    public static void save(State s) {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);

        prefs.putBoolean(K_HAS_SAVE, true);

        prefs.putBoolean(K_STARTED, s.started);
        prefs.putInteger(K_SCORE, s.score);
        prefs.putFloat(K_MAXY, s.maxY);
        prefs.putFloat(K_VELY, s.player != null ? s.player.velY : 0f);
        prefs.putInteger(K_NIVEL, s.nivelVisual);

        if (s.player != null) {
            prefs.putFloat(K_PLAYERX, s.player.rect.x);
            prefs.putFloat(K_PLAYERY, s.player.rect.y);
        }

        prefs.putFloat(K_CAMX, s.camX);
        prefs.putFloat(K_CAMY, s.camY);

        if (s.platforms != null) {
            prefs.putFloat(K_NEXTY, s.platforms.nextY);

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
        } else {
            prefs.putInteger(K_PLAT_COUNT, 0);
            prefs.putFloat(K_NEXTY, 0f);
        }

        prefs.putFloat(K_RUINAS_BASEY, s.ruinasBaseY);
        prefs.putFloat(K_RUINAS_ALPHA, s.ruinasAlpha);
        prefs.putBoolean(K_RUINAS_FADING, s.ruinasFading);
        prefs.putBoolean(K_RUINAS_OFF, s.ruinasOff);

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

    // =========================================================
    // TOP 10: añade, ordena y recorta a 10
    // Orden: altura desc, monedas desc, tiempo desc
    // =========================================================
    public static void addRunToHistory(int heightMeters, int coins, float timeSeconds) {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);

        Array<Run> runs = parseRuns(prefs.getString(K_RUNS, ""));

        Run r = new Run();
        r.timeMs = System.currentTimeMillis();
        r.heightMeters = heightMeters;
        r.coins = coins;
        r.timeSec = timeSeconds;

        runs.add(r);
        sortRuns(runs);

        while (runs.size > MAX_RUNS) {
            runs.removeIndex(runs.size - 1);
        }

        prefs.putString(K_RUNS, encodeRuns(runs));
        prefs.flush();
    }

    public static Array<Run> getRunsHistorySorted() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        Array<Run> runs = parseRuns(prefs.getString(K_RUNS, ""));
        sortRuns(runs);
        return runs;
    }

    public static void clearRunsHistory() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.remove(K_RUNS);
        prefs.flush();
    }

    private static void sortRuns(Array<Run> runs) {
        if (runs == null) return;
        runs.sort((a, b) -> compareRuns(a, b));
    }

    // <0 => a mejor que b
    private static int compareRuns(Run a, Run b) {
        if (b.heightMeters != a.heightMeters) return b.heightMeters - a.heightMeters;
        if (b.coins != a.coins) return b.coins - a.coins;
        return Float.compare(b.timeSec, a.timeSec);
    }

    private static Array<Run> parseRuns(String raw) {
        Array<Run> out = new Array<>();
        if (raw == null || raw.isEmpty()) return out;

        String[] items = raw.split(";");
        for (String it : items) {
            if (it == null || it.isEmpty()) continue;

            String[] p = it.split("\\|");
            if (p.length < 4) continue;

            try {
                Run r = new Run();
                r.timeMs = Long.parseLong(p[0]);
                r.heightMeters = Integer.parseInt(p[1]);
                r.coins = Integer.parseInt(p[2]);
                r.timeSec = Float.parseFloat(p[3]);
                out.add(r);
            } catch (Exception ignored) {}
        }
        return out;
    }

    private static String encodeRuns(Array<Run> runs) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < runs.size; i++) {
            Run r = runs.get(i);
            if (i > 0) sb.append(";");
            sb.append(r.timeMs).append("|")
                .append(r.heightMeters).append("|")
                .append(r.coins).append("|")
                .append(r.timeSec);
        }
        return sb.toString();
    }
}
