package com.dani.mijuego.screens;

import com.badlogic.gdx.graphics.Texture;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameAudio;

public class VictoryScreen extends EndScreenBase {

    // Sistema de audio para reproducir efectos al entrar o pulsar botones
    private final GameAudio audio;

    // Constructor: recibe el juego, la puntuación final, las monedas y el sistema de audio
    public VictoryScreen(Main game, int score, int coins, GameAudio audio) {
        super(game, score, coins);
        this.audio = audio;
    }

    // Se ejecuta al entrar en la pantalla de victoria.
    // Reproduce un sonido de confirmación/celebración.
    @Override
    protected void onEnter() {
        if (audio != null) audio.playSelectButton();
    }

    // Devuelve la textura de fondo específica de la pantalla de victoria.
    @Override
    protected Texture resolveBackgroundTexture() {
        return getTex(Assets.VICTORY);
    }

    // Devuelve la clave del texto del botón (usado por I18n para mostrar "Reiniciar").
    @Override
    protected String buttonTextKey() {
        return "go_restart";
    }

    // Acción que se ejecuta al pulsar el botón principal.
    // Reproduce sonido y vuelve al menú principal.
    @Override
    protected void onButtonPressed() {
        if (audio != null) audio.playSelectButton();
        game.setScreen(new MenuScreen(game));
    }

    // Acción al pulsar atrás.
    // También vuelve al menú principal con sonido.
    @Override
    protected void onBack() {
        if (audio != null) audio.playSelectButton();
        game.setScreen(new MenuScreen(game));
    }
}
