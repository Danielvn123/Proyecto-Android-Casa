package com.dani.mijuego.screens;

import com.badlogic.gdx.graphics.Texture;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameAudio;

public class VictoryScreen extends EndScreenBase {

    private final GameAudio audio;

    public VictoryScreen(Main game, int score, int coins, GameAudio audio) {
        super(game, score, coins);
        this.audio = audio;
    }

    @Override
    protected void onEnter() {
        if (audio != null) audio.playSelectButton();
    }

    @Override
    protected Texture resolveBackgroundTexture() {
        return getTex(Assets.VICTORY);
    }

    @Override
    protected String buttonTextKey() {
        return "go_restart";
    }

    @Override
    protected void onButtonPressed() {
        if (audio != null) audio.playSelectButton();
        game.setScreen(new MenuScreen(game));
    }

    @Override
    protected void onBack() {
        if (audio != null) audio.playSelectButton();
        game.setScreen(new MenuScreen(game));
    }
}
