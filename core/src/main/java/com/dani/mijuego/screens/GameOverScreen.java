package com.dani.mijuego.screens;

import com.badlogic.gdx.graphics.Texture;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameAudio;

public class GameOverScreen extends EndScreenBase {

    // Sistema de audio para reproducir sonidos y música
    private final GameAudio audio;

    // Constructor que recibe el juego, estadísticas finales y sistema de audio
    public GameOverScreen(Main game, int score, int coins, GameAudio audio) {
        super(game, score, coins);
        this.audio = audio;
    }

    // Se ejecuta automáticamente al entrar en la pantalla.
    // Detiene la música actual y reproduce el sonido de Game Over.
    @Override
    protected void onEnter() {
        if (audio == null) return;
        audio.stopAllMusic();
        audio.playGameOver();
    }

    // Devuelve la textura de fondo específica para Game Over.
    @Override
    protected Texture resolveBackgroundTexture() {
        return getTex(Assets.GAMEOVER);
    }

    // Devuelve la clave del texto del botón (para internacionalización).
    // En este caso, el texto será algo como "Reiniciar".
    @Override
    protected String buttonTextKey() {
        return "go_restart";
    }

    // Acción que se ejecuta cuando el jugador pulsa el botón.
    // Vuelve al menú principal.
    @Override
    protected void onButtonPressed() {
        game.setScreen(new MenuScreen(game));
    }
}
