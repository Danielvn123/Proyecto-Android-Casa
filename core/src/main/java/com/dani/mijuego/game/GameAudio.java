package com.dani.mijuego.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.dani.mijuego.game.entities.EnemyType;

public class GameAudio {

    // =========================
    // BICHOS
    // =========================
    private static final String P_ENEMY_AZUL  = "audio/bichoazul.mp3";
    private static final String P_ENEMY_LILA  = "audio/bicholila.mp3";
    private static final String P_ENEMY_VERDE = "audio/bichoverde.mp3";
    private static final String P_ENEMY_ROJO  = "audio/bichorojo.mp3";   // NUEVO

    // =========================
    // CAIDA PERSONAJE
    // =========================
    private static final String P_CAIDA = "audio/caidapersonaje.mp3";

    // =========================
    // MONEDA
    // =========================
    private static final String P_COIN = "audio/coin.mp3";

    // =========================
    // GAMEOVER
    // =========================
    private static final String P_GAMEOVER = "audio/gameover.mp3";

    // =========================
    // LEVELUP
    // =========================
    private static final String P_LEVELUP = "audio/levelup.mp3";

    // =========================
    // SALTO
    // =========================
    private static final String P_SALTO = "audio/salto.mp3";

    // =========================
    // BOTON SELECCIONADO
    // =========================
    private static final String P_SELECT = "audio/selectbutton.mp3";

    // =========================
    // SONIDO FONDO
    // =========================
    private static final String P_FONDO = "audio/sonidofondo.mp3";

    // =========================
    // SONIDO ZAPATOS
    // =========================
    private static final String P_TENIS = "audio/tenis.mp3";

    // =========================
    // COGER ITEM
    // =========================
    private static final String P_COGERITEM = "audio/cogeritem.mp3";

    // =========================
    // SONIDO ESCUDO ROTO
    // =========================
    private static final String P_ESCUDO_ROTO = "audio/escudoroto.mp3";

    // =========================
    // VICTORIA
    // =========================
    private static final String P_WIN = "audio/win.mp3";   // NUEVO (victoria)

    // Estado
    private boolean enabled = true;
    private float musicVolume = 1f;
    private float sfxVolume = 1f;
    private boolean loaded = false;

    // Music
    private Music fondo;
    private Music gameOver;

    // Sounds (SFX)
    private Sound coin;
    private Sound levelUp;
    private Sound salto;
    private Sound caida;
    private Sound selectButton;
    private Sound tenis;
    private Sound cogerItem;
    private Sound escudoRoto;

    private Sound enemyAzul;
    private Sound enemyLila;
    private Sound enemyVerde;
    private Sound enemyRojo;

    private Sound win;

    public void load() {
        if (loaded) return;
        loaded = true;

        // Music
        fondo = safeMusic(P_FONDO, true);
        gameOver = safeMusic(P_GAMEOVER, false);

        // SFX
        coin = safeSound(P_COIN);
        levelUp = safeSound(P_LEVELUP);
        salto = safeSound(P_SALTO);
        caida = safeSound(P_CAIDA);
        selectButton = safeSound(P_SELECT);
        tenis = safeSound(P_TENIS);

        cogerItem = safeSound(P_COGERITEM);
        escudoRoto = safeSound(P_ESCUDO_ROTO);

        enemyAzul = safeSound(P_ENEMY_AZUL);
        enemyLila = safeSound(P_ENEMY_LILA);
        enemyVerde = safeSound(P_ENEMY_VERDE);
        enemyRojo = safeSound(P_ENEMY_ROJO);

        win = safeSound(P_WIN);

        applyMusicVolume();
    }

    // Config

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) stopAllMusic();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = clamp01(volume);
        applyMusicVolume();
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = clamp01(volume);
    }

    // Music

    public void playFondo() {
        if (!enabled || fondo == null) return;

        fondo.setLooping(true);
        fondo.setVolume(musicVolume);

        if (!fondo.isPlaying()) {
            fondo.play();
        }
    }

    public void stopFondo() {
        if (fondo == null) return;

        try { fondo.pause(); } catch (Exception ignored) {}
        try { fondo.stop(); } catch (Exception ignored) {}
    }

    public void playGameOver() {
        if (!enabled || gameOver == null) return;
        gameOver.stop();
        gameOver.setVolume(musicVolume);
        gameOver.play();
    }

    public void stopAllMusic() {
        stopFondo();
        if (gameOver != null) {
            try { gameOver.stop(); } catch (Exception ignored) {}
        }
    }

    // SFX

    public void playCoin() { play(coin); }
    public void playLevelUp() { play(levelUp); }
    public void playLand() { play(salto); }
    public void playCaida() { play(caida); }
    public void playSelectButton() { play(selectButton); }
    public void playTenis() { play(tenis); }
    public void playCogerItem() { play(cogerItem); }
    public void playEscudoRoto() { play(escudoRoto); }

    // NUEVO: victoria
    public void playVictory() { play(win); }

    public void playEnemy(EnemyType type) {
        if (!enabled || type == null) return;
        switch (type) {
            case AZUL:  play(enemyAzul);  break;
            case LILA:  play(enemyLila);  break;
            case VERDE: play(enemyVerde); break;
            case ROJO:  play(enemyRojo);  break;
        }
    }

    // Dispose

    public void dispose() {
        stopAllMusic();

        // Music
        if (fondo != null) fondo.dispose();
        if (gameOver != null) gameOver.dispose();

        // Sounds
        if (coin != null) coin.dispose();
        if (levelUp != null) levelUp.dispose();
        if (salto != null) salto.dispose();
        if (caida != null) caida.dispose();
        if (selectButton != null) selectButton.dispose();
        if (tenis != null) tenis.dispose();
        if (cogerItem != null) cogerItem.dispose();
        if (escudoRoto != null) escudoRoto.dispose();

        if (enemyAzul != null) enemyAzul.dispose();
        if (enemyLila != null) enemyLila.dispose();
        if (enemyVerde != null) enemyVerde.dispose();
        if (enemyRojo != null) enemyRojo.dispose(); // NUEVO

        if (win != null) win.dispose();             // NUEVO

        fondo = null; gameOver = null;
        coin = null; levelUp = null; salto = null; caida = null;
        selectButton = null; tenis = null;
        cogerItem = null;
        escudoRoto = null;

        enemyAzul = null; enemyLila = null; enemyVerde = null; enemyRojo = null;
        win = null;

        loaded = false;
    }

    // Helpers

    private void play(Sound s) {
        if (!enabled || s == null) return;
        s.play(sfxVolume);
    }

    private void applyMusicVolume() {
        if (fondo != null) fondo.setVolume(musicVolume);
        if (gameOver != null) gameOver.setVolume(musicVolume);
    }

    private Music safeMusic(String path, boolean loop) {
        if (!Gdx.files.internal(path).exists()) {
            Gdx.app.error("AUDIO", "No existe Music: " + path);
            return null;
        }
        Music m = Gdx.audio.newMusic(Gdx.files.internal(path));
        m.setLooping(loop);
        return m;
    }

    private Sound safeSound(String path) {
        if (!Gdx.files.internal(path).exists()) {
            Gdx.app.error("AUDIO", "No existe Sound: " + path);
            return null;
        }
        return Gdx.audio.newSound(Gdx.files.internal(path));
    }

    private float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }
}
