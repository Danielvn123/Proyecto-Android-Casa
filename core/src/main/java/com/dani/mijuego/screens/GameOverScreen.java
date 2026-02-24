package com.dani.mijuego.screens;

import com.badlogic.gdx.graphics.Texture;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameAudio;

public class GameOverScreen extends EndScreenBase {

    private final GameAudio audio;

    public GameOverScreen(Main game, int score, int coins, GameAudio audio) {
        super(game, score, coins);
        this.audio = audio;
    }

    @Override
    protected void onEnter() {
        if (audio == null) return;
        audio.stopAllMusic();
        audio.playGameOver();
    }

    @Override
    protected Texture resolveBackgroundTexture() {
        return getTex(Assets.GAMEOVER);
    }

    @Override
    protected String buttonTextKey() {
        return "go_restart";
    }

    @Override
    protected void onButtonPressed() {
        game.setScreen(new MenuScreen(game));
    }
}
