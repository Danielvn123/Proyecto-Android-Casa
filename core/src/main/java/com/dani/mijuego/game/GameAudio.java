package com.dani.mijuego.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.dani.mijuego.game.entities.EnemyType;

public class GameAudio {

    private static final String P_FONDO    = "audio/sonidofondo.mp3";
    private static final String P_GAMEOVER = "audio/gameover.mp3";

    private static final String P_COIN = "audio/coin.mp3";
    private static final String P_LEVELUP = "audio/levelup.mp3";
    private static final String P_SALTO = "audio/salto.mp3";
    private static final String P_CAIDA = "audio/caidapersonaje.mp3";
    private static final String P_SELECT = "audio/selectbutton.mp3";
    private static final String P_TENIS = "audio/tenis.mp3";
    private static final String P_COGERITEM = "audio/cogeritem.mp3";
    private static final String P_ESCUDO_ROTO = "audio/escudoroto.mp3";
    private static final String P_WIN = "audio/win.mp3";

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
    private Sound enemyAzul, enemyLila, enemyVerde, enemyRojo;

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

        enemyAzul = safeSound(P_ENEMY_AZUL);
        enemyLila = safeSound(P_ENEMY_LILA);
        enemyVerde = safeSound(P_ENEMY_VERDE);
        enemyRojo = safeSound(P_ENEMY_ROJO);

        applyMusicVolume();
    }

    // 🔥 MUTE TOTAL
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) stopAllMusic();
    }

    public boolean isEnabled() {
        return enabled;
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
        if (fondo == null) return;
        try { fondo.setVolume(0f); } catch (Exception ignored) {}
        try { fondo.pause(); } catch (Exception ignored) {}
        try { fondo.stop(); } catch (Exception ignored) {}
    }

    public void playGameOver() {
        if (!enabled || gameOver == null) return;
        try { gameOver.stop(); } catch (Exception ignored) {}
        gameOver.setVolume(musicVolume);
        gameOver.play();
    }

    public void stopAllMusic() {
        stopFondo();
        if (gameOver != null) {
            try { gameOver.setVolume(0f); } catch (Exception ignored) {}
            try { gameOver.pause(); } catch (Exception ignored) {}
            try { gameOver.stop(); } catch (Exception ignored) {}
        }
    }

    // =========================
    // SFX
    // =========================
    public void playCoin() { play(coin); }
    public void playLevelUp() { play(levelUp); }
    public void playLand() { play(salto); }
    public void playCaida() { play(caida); }  // ✅ caidapersonaje.mp3

    // ✅ Alias (por si prefieres llamarlo así desde GameScreen)
    public void playCaidaPersonaje() { play(caida); }

    public void playSelectButton() { play(selectButton); }
    public void playTenis() { play(tenis); }
    public void playCogerItem() { play(cogerItem); }
    public void playEscudoRoto() { play(escudoRoto); }
    public void playVictory() { play(win); }

    public void playEnemy(EnemyType type) {
        if (!enabled || type == null) return;

        switch (type) {
            case AZUL:  play(enemyAzul); break;
            case LILA:  play(enemyLila); break;
            case VERDE: play(enemyVerde); break;
            case ROJO:  play(enemyRojo); break;
        }
    }

    private void play(Sound s) {
        if (!enabled || s == null) return;
        s.play(sfxVolume);
    }

    private void applyMusicVolume() {
        if (fondo != null) fondo.setVolume(musicVolume);
        if (gameOver != null) gameOver.setVolume(musicVolume);
    }

    private Music safeMusic(String path, boolean loop) {
        if (!Gdx.files.internal(path).exists()) return null;
        Music m = Gdx.audio.newMusic(Gdx.files.internal(path));
        m.setLooping(loop);
        return m;
    }

    private Sound safeSound(String path) {
        if (!Gdx.files.internal(path).exists()) return null;
        return Gdx.audio.newSound(Gdx.files.internal(path));
    }

    public void dispose() {
        stopAllMusic();

        // ✅ liberar music
        if (fondo != null) fondo.dispose();
        if (gameOver != null) gameOver.dispose();

        // ✅ liberar sfx (esto faltaba en tu clase)
        if (coin != null) coin.dispose();
        if (levelUp != null) levelUp.dispose();
        if (salto != null) salto.dispose();
        if (caida != null) caida.dispose();
        if (selectButton != null) selectButton.dispose();
        if (tenis != null) tenis.dispose();
        if (cogerItem != null) cogerItem.dispose();
        if (escudoRoto != null) escudoRoto.dispose();
        if (win != null) win.dispose();

        if (enemyAzul != null) enemyAzul.dispose();
        if (enemyLila != null) enemyLila.dispose();
        if (enemyVerde != null) enemyVerde.dispose();
        if (enemyRojo != null) enemyRojo.dispose();

        loaded = false;
    }
}
