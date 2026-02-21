package com.dani.mijuego.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.dani.mijuego.game.entities.EnemyType;

import java.util.EnumMap;

public class GameAudio {

    private static final String P_FONDO    = "audio/sonidofondo.mp3";
    private static final String P_GAMEOVER = "audio/gameover.mp3";

    private static final String P_COIN        = "audio/coin.mp3";
    private static final String P_LEVELUP     = "audio/levelup.mp3";
    private static final String P_SALTO       = "audio/salto.mp3";
    private static final String P_CAIDA       = "audio/caidapersonaje.mp3";
    private static final String P_SELECT      = "audio/selectbutton.mp3";
    private static final String P_TENIS       = "audio/tenis.mp3";
    private static final String P_COGERITEM   = "audio/cogeritem.mp3";
    private static final String P_ESCUDO_ROTO = "audio/escudoroto.mp3";
    private static final String P_WIN         = "audio/win.mp3";

    private static final String P_ENEMY_AZUL  = "audio/bichoazul.mp3";
    private static final String P_ENEMY_LILA  = "audio/bicholila.mp3";
    private static final String P_ENEMY_VERDE = "audio/bichoverde.mp3";
    private static final String P_ENEMY_ROJO  = "audio/bichorojo.mp3";

    private boolean enabled = true;
    private float musicVolume = 1f;
    private float sfxVolume = 1f;
    private boolean loaded = false;

    private Music fondo;
    private Music gameOver;

    private Sound coin, levelUp, salto, caida, selectButton, tenis;
    private Sound cogerItem, escudoRoto, win;

    private final EnumMap<EnemyType, Sound> enemySounds = new EnumMap<>(EnemyType.class);

    public void load() {
        if (loaded) return;
        loaded = true;

        fondo = safeMusic(P_FONDO, true);
        gameOver = safeMusic(P_GAMEOVER, false);

        coin = safeSound(P_COIN);
        levelUp = safeSound(P_LEVELUP);
        salto = safeSound(P_SALTO);
        caida = safeSound(P_CAIDA);
        selectButton = safeSound(P_SELECT);
        tenis = safeSound(P_TENIS);
        cogerItem = safeSound(P_COGERITEM);
        escudoRoto = safeSound(P_ESCUDO_ROTO);
        win = safeSound(P_WIN);

        enemySounds.put(EnemyType.AZUL, safeSound(P_ENEMY_AZUL));
        enemySounds.put(EnemyType.LILA, safeSound(P_ENEMY_LILA));
        enemySounds.put(EnemyType.VERDE, safeSound(P_ENEMY_VERDE));
        enemySounds.put(EnemyType.ROJO, safeSound(P_ENEMY_ROJO));

        applyMusicVolume();
    }

    // MUTE TOTAL
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) stopAllMusic();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setMusicVolume(float v) {
        musicVolume = clamp01(v);
        applyMusicVolume();
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setSfxVolume(float v) {
        sfxVolume = clamp01(v);
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    // =========================
    // MUSIC
    // =========================
    public void playFondo() {
        if (!enabled || fondo == null) return;

        fondo.setLooping(true);
        fondo.setVolume(musicVolume);

        if (!fondo.isPlaying()) fondo.play();
    }

    public void stopFondo() {
        stopMusicQuietly(fondo);
    }

    public void playGameOver() {
        if (!enabled || gameOver == null) return;

        gameOver.stop();
        gameOver.setLooping(false);
        gameOver.setVolume(musicVolume);
        gameOver.play();
    }

    public void stopAllMusic() {
        stopMusicQuietly(fondo);
        stopMusicQuietly(gameOver);
    }

    // =========================
    // SFX
    // =========================
    public void playCoin() { play(coin); }
    public void playLevelUp() { play(levelUp); }

    // Si es salto realmente, mejor nombre:
    public void playJump() { play(salto); }

    public void playCaida() { play(caida); }
    public void playCaidaPersonaje() { play(caida); } // alias

    public void playSelectButton() { play(selectButton); }
    public void playTenis() { play(tenis); }
    public void playCogerItem() { play(cogerItem); }
    public void playEscudoRoto() { play(escudoRoto); }
    public void playVictory() { play(win); }

    public void playEnemy(EnemyType type) {
        if (!enabled || type == null) return;
        play(enemySounds.get(type));
    }

    private void play(Sound s) {
        if (!enabled || s == null) return;
        s.play(sfxVolume);
    }

    private void applyMusicVolume() {
        if (fondo != null) fondo.setVolume(musicVolume);
        if (gameOver != null) gameOver.setVolume(musicVolume);
    }

    private static void stopMusicQuietly(Music m) {
        if (m == null) return;
        m.stop(); // suficiente (stop ya deja de sonar)
    }

    private static float clamp01(float v) {
        if (v < 0f) return 0f;
        if (v > 1f) return 1f;
        return v;
    }

    private static Music safeMusic(String path, boolean loop) {
        if (!Gdx.files.internal(path).exists()) return null;
        Music m = Gdx.audio.newMusic(Gdx.files.internal(path));
        m.setLooping(loop);
        return m;
    }

    private static Sound safeSound(String path) {
        if (!Gdx.files.internal(path).exists()) return null;
        return Gdx.audio.newSound(Gdx.files.internal(path));
    }

    public void dispose() {
        stopAllMusic();

        disposeQuietly(fondo); fondo = null;
        disposeQuietly(gameOver); gameOver = null;

        disposeQuietly(coin); coin = null;
        disposeQuietly(levelUp); levelUp = null;
        disposeQuietly(salto); salto = null;
        disposeQuietly(caida); caida = null;
        disposeQuietly(selectButton); selectButton = null;
        disposeQuietly(tenis); tenis = null;
        disposeQuietly(cogerItem); cogerItem = null;
        disposeQuietly(escudoRoto); escudoRoto = null;
        disposeQuietly(win); win = null;

        for (Sound s : enemySounds.values()) disposeQuietly(s);
        enemySounds.clear();

        loaded = false;
    }

    private static void disposeQuietly(Music m) {
        if (m != null) m.dispose();
    }

    private static void disposeQuietly(Sound s) {
        if (s != null) s.dispose();
    }
}
