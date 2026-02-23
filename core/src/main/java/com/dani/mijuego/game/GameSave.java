package com.dani.mijuego.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.dani.mijuego.game.entities.Player;
import com.dani.mijuego.game.world.Platform;
import com.dani.mijuego.game.world.PlatformSystem;

// Clase encargada de guardar y cargar datos usando Preferences (guardado local)
public final class GameSave {

    // Evita instanciar la clase (todo es estático)
    private GameSave() {}

    // Nombre del archivo de preferencias
    private static final String PREFS_NAME = "savegame";

    // Referencia cacheada a Preferences
    private static Preferences PREFS;

    // Devuelve Preferences inicializándolo si hace falta
    private static Preferences prefs() {
        if (PREFS == null) PREFS = Gdx.app.getPreferences(PREFS_NAME);
        return PREFS;
    }

    // Fuerza a escribir a disco los cambios pendientes
    private static void flush() {
        prefs().flush();
    }

    // KEYS
    // Claves para guardar/leer datos de la partida
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

    // Claves para guardar el estado de la capa de ruinas
    private static final String K_RUINAS_BASEY  = "ruinasBaseY";
    private static final String K_RUINAS_ALPHA  = "ruinasAlpha";
    private static final String K_RUINAS_FADING = "ruinasFading";
    private static final String K_RUINAS_OFF    = "ruinasOff";

    // Claves para guardar plataformas
    private static final String K_PLAT_COUNT   = "platCount";
    private static final String K_PLAT_X       = "platX_";
    private static final String K_PLAT_Y       = "platY_";
    private static final String K_PLAT_MOV     = "platMov_";
    private static final String K_PLAT_SPD     = "platSpd_";
    private static final String K_PLAT_DIR     = "platDir_";
    private static final String K_PLAT_BREAK   = "platBreak_";
    private static final String K_PLAT_BROKEN  = "platBroken_";
    private static final String K_PLAT_BROKENT = "platBrokenT_";

    // IDIOMA
    private static final String K_LANG = "lang"; // "ES" o "EN"

    // OPCIONES
    private static final String K_MUSIC_ON  = "music_on";
    private static final String K_VIBRA_ON  = "vibra_on";
    private static final String K_CTRL_MODE = "ctrl_mode"; // 0=TOUCH, 1=TILT
    private static final String K_CTRL_SENS = "ctrl_sens"; // 0.8..1.4

    // TOP 10
    private static final String K_RUNS = "runs_history";
    private static final int MAX_RUNS = 10;

    // Estructura para guardar una partida en el historial
    public static final class Run {
        public long timeMs;
        public int heightMeters;
        public int coins;
        public float timeSec;
    }

    // CLEAR
    // Borra absolutamente todo (idioma/opciones/partida/top10)
    public static void clearAll() {
        Preferences p = prefs();
        p.clear();
        p.flush();
    }

    // Borra SOLO el guardado de la partida (mantiene idioma/opciones/top10)
    public static void clearRunSave() {
        Preferences p = prefs();

        // Borra claves principales del estado
        p.remove(K_HAS_SAVE);
        p.remove(K_STARTED);
        p.remove(K_SCORE);
        p.remove(K_MAXY);
        p.remove(K_VELY);
        p.remove(K_NIVEL);
        p.remove(K_PLAYERX);
        p.remove(K_PLAYERY);
        p.remove(K_CAMX);
        p.remove(K_CAMY);
        p.remove(K_NEXTY);

        // Borra estado de ruinas
        p.remove(K_RUINAS_BASEY);
        p.remove(K_RUINAS_ALPHA);
        p.remove(K_RUINAS_FADING);
        p.remove(K_RUINAS_OFF);

        // Borra plataformas guardadas
        int n = p.getInteger(K_PLAT_COUNT, 0);
        p.remove(K_PLAT_COUNT);
        for (int i = 0; i < n; i++) {
            p.remove(K_PLAT_X + i);
            p.remove(K_PLAT_Y + i);
            p.remove(K_PLAT_MOV + i);
            p.remove(K_PLAT_SPD + i);
            p.remove(K_PLAT_DIR + i);
            p.remove(K_PLAT_BREAK + i);
            p.remove(K_PLAT_BROKEN + i);
            p.remove(K_PLAT_BROKENT + i);
        }

        p.flush();
    }

    // IDIOMA
    // Guarda idioma (por defecto ES)
    public static void setLang(String lang) {
        Preferences p = prefs();
        p.putString(K_LANG, (lang == null) ? "ES" : lang);
        p.flush();
    }

    // Lee idioma guardado
    public static String getLang() {
        return prefs().getString(K_LANG, "ES");
    }

    // OPCIONES
    // Guarda opción música (y hace flush)
    public static void setMusicOn(boolean on) {
        Preferences p = prefs();
        p.putBoolean(K_MUSIC_ON, on);
        p.flush();
    }

    // Lee opción música
    public static boolean isMusicOn() {
        return prefs().getBoolean(K_MUSIC_ON, true);
    }

    // Guarda opción vibración (y hace flush)
    public static void setVibrationOn(boolean on) {
        Preferences p = prefs();
        p.putBoolean(K_VIBRA_ON, on);
        p.flush();
    }

    // Lee opción vibración
    public static boolean isVibrationOn() {
        return prefs().getBoolean(K_VIBRA_ON, true);
    }

    // Guarda modo de control (tilt o touch)
    public static void setControlModeTilt(boolean tilt) {
        Preferences p = prefs();
        p.putInteger(K_CTRL_MODE, tilt ? 1 : 0);
        p.flush();
    }

    // Lee modo de control
    public static boolean isControlModeTilt() {
        return prefs().getInteger(K_CTRL_MODE, 0) == 1;
    }

    // Guarda sensibilidad del control (limitada a rango)
    public static void setControlSensitivity(float sens) {
        sens = clamp(sens, 0.8f, 1.4f);
        Preferences p = prefs();
        p.putFloat(K_CTRL_SENS, sens);
        p.flush();
    }

    // Lee sensibilidad del control
    public static float getControlSensitivity() {
        return prefs().getFloat(K_CTRL_SENS, 1.0f);
    }

    // Permite guardar varias opciones sin flush (para hacerlo una sola vez al final)
    public static void setOptionsNoFlush(boolean musicOn, boolean vibraOn, boolean tilt, float sens) {
        Preferences p = prefs();
        p.putBoolean(K_MUSIC_ON, musicOn);
        p.putBoolean(K_VIBRA_ON, vibraOn);
        p.putInteger(K_CTRL_MODE, tilt ? 1 : 0);
        p.putFloat(K_CTRL_SENS, clamp(sens, 0.8f, 1.4f));
    }

    // Flush manual de las opciones
    public static void flushOptions() {
        flush();
    }

    // SAVE PARTIDA
    // Guarda el estado actual de la partida
    public static void save(State s) {
        Preferences p = prefs();

        // Marca que existe un guardado
        p.putBoolean(K_HAS_SAVE, true);

        // Datos básicos de partida
        p.putBoolean(K_STARTED, s.started);
        p.putInteger(K_SCORE, s.score);
        p.putFloat(K_MAXY, s.maxY);
        p.putFloat(K_VELY, s.player != null ? s.player.velY : 0f);
        p.putInteger(K_NIVEL, s.nivelVisual);

        // Posición del jugador
        if (s.player != null) {
            p.putFloat(K_PLAYERX, s.player.rect.x);
            p.putFloat(K_PLAYERY, s.player.rect.y);
        }

        // Posición de cámara
        p.putFloat(K_CAMX, s.camX);
        p.putFloat(K_CAMY, s.camY);

        // Datos de plataformas
        if (s.platforms != null) {
            p.putFloat(K_NEXTY, s.platforms.nextY);

            int count = s.platforms.platforms.size;
            p.putInteger(K_PLAT_COUNT, count);

            for (int i = 0; i < count; i++) {
                Platform plat = s.platforms.platforms.get(i);

                // Rectángulo
                p.putFloat(K_PLAT_X + i, plat.rect.x);
                p.putFloat(K_PLAT_Y + i, plat.rect.y);

                // Movimiento
                p.putBoolean(K_PLAT_MOV + i, plat.moving);
                p.putFloat(K_PLAT_SPD + i, plat.speed);
                p.putInteger(K_PLAT_DIR + i, plat.dir);

                // Rompible/rota
                p.putBoolean(K_PLAT_BREAK + i, plat.breakable);
                p.putBoolean(K_PLAT_BROKEN + i, plat.broken);
                p.putFloat(K_PLAT_BROKENT + i, plat.brokenTime);
            }
        } else {
            // Si no hay plataformas, guarda valores vacíos
            p.putInteger(K_PLAT_COUNT, 0);
            p.putFloat(K_NEXTY, 0f);
        }

        // Estado de ruinas
        p.putFloat(K_RUINAS_BASEY, s.ruinasBaseY);
        p.putFloat(K_RUINAS_ALPHA, s.ruinasAlpha);
        p.putBoolean(K_RUINAS_FADING, s.ruinasFading);
        p.putBoolean(K_RUINAS_OFF, s.ruinasOff);

        // Escribe a disco
        p.flush();
    }

    // Carga partida si existe, si no devuelve null
    public static Loaded loadOrNull() {
        Preferences p = prefs();
        if (!p.getBoolean(K_HAS_SAVE, false)) return null;

        Loaded l = new Loaded();

        // Datos básicos
        l.started = p.getBoolean(K_STARTED, false);
        l.score = p.getInteger(K_SCORE, 0);
        l.maxY = p.getFloat(K_MAXY, 0f);
        l.velY = p.getFloat(K_VELY, 0f);
        l.nivelVisual = p.getInteger(K_NIVEL, 0);

        // Posición jugador (con defaults razonables)
        l.playerX = p.getFloat(K_PLAYERX, (GameConfig.VW - GameConfig.PLAYER_W) / 2f);
        l.playerY = p.getFloat(K_PLAYERY, 200f);

        // Posición cámara
        l.camX = p.getFloat(K_CAMX, GameConfig.VW / 2f);
        l.camY = p.getFloat(K_CAMY, GameConfig.VH / 2f);

        // Siguiente altura de spawn
        l.nextY = p.getFloat(K_NEXTY, l.playerY);

        // Estado ruinas
        l.ruinasBaseY = p.getFloat(K_RUINAS_BASEY, 0f);
        l.ruinasAlpha = p.getFloat(K_RUINAS_ALPHA, 1f);
        l.ruinasFading = p.getBoolean(K_RUINAS_FADING, false);
        l.ruinasOff = p.getBoolean(K_RUINAS_OFF, false);

        // Número de plataformas guardadas
        l.platformCount = p.getInteger(K_PLAT_COUNT, 0);

        // Arrays para reconstruir plataformas
        l.platX = new float[l.platformCount];
        l.platY = new float[l.platformCount];
        l.platMov = new boolean[l.platformCount];
        l.platSpd = new float[l.platformCount];
        l.platDir = new int[l.platformCount];
        l.platBreak = new boolean[l.platformCount];
        l.platBroken = new boolean[l.platformCount];
        l.platBrokenT = new float[l.platformCount];

        // Lee datos de cada plataforma
        for (int i = 0; i < l.platformCount; i++) {
            l.platX[i] = p.getFloat(K_PLAT_X + i, 0f);
            l.platY[i] = p.getFloat(K_PLAT_Y + i, 0f);
            l.platMov[i] = p.getBoolean(K_PLAT_MOV + i, false);
            l.platSpd[i] = p.getFloat(K_PLAT_SPD + i, 0f);
            l.platDir[i] = p.getInteger(K_PLAT_DIR + i, 1);
            l.platBreak[i] = p.getBoolean(K_PLAT_BREAK + i, false);
            l.platBroken[i] = p.getBoolean(K_PLAT_BROKEN + i, false);
            l.platBrokenT[i] = p.getFloat(K_PLAT_BROKENT + i, 0f);
        }

        return l;
    }

    // Clase con el estado que se guarda desde GameScreen
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

    // Clase con los datos cargados para reconstruir la partida
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

    // TOP 10
    // Añade una partida al historial y la guarda ordenada
    public static void addRunToHistory(int heightMeters, int coins, float timeSeconds) {
        Preferences p = prefs();

        // Lee historial actual
        Array<Run> runs = parseRuns(p.getString(K_RUNS, ""));

        // Crea nuevo registro
        Run r = new Run();
        r.timeMs = System.currentTimeMillis();
        r.heightMeters = heightMeters;
        r.coins = coins;
        r.timeSec = timeSeconds;

        // Añade y ordena
        runs.add(r);
        sortRuns(runs);

        // Limita a top 10
        while (runs.size > MAX_RUNS) runs.removeIndex(runs.size - 1);

        // Guarda codificado como String
        p.putString(K_RUNS, encodeRuns(runs));
        p.flush();
    }

    // Devuelve el historial ordenado
    public static Array<Run> getRunsHistorySorted() {
        Preferences p = prefs();
        Array<Run> runs = parseRuns(p.getString(K_RUNS, ""));
        sortRuns(runs);
        return runs;
    }

    // Borra el historial del top 10
    public static void clearRunsHistory() {
        Preferences p = prefs();
        p.remove(K_RUNS);
        p.flush();
    }

    // Ordena la lista usando el comparador de abajo
    private static void sortRuns(Array<Run> runs) {
        if (runs == null) return;
        runs.sort(GameSave::compareRuns);
    }

    // Comparación para ordenar: primero altura, luego monedas, luego tiempo
    // <0 => a mejor que b
    private static int compareRuns(Run a, Run b) {
        if (b.heightMeters != a.heightMeters) return b.heightMeters - a.heightMeters;
        if (b.coins != a.coins) return b.coins - a.coins;
        return Float.compare(b.timeSec, a.timeSec);
    }

    // Convierte el String guardado a lista de runs
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
            } catch (Exception ignored) {
                // Si un registro está corrupto, se ignora
            }
        }
        return out;
    }

    // Convierte lista de runs a un String para guardarlo en Preferences
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

    // Limita un valor dentro de un rango
    private static float clamp(float v, float min, float max) {
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }
}
