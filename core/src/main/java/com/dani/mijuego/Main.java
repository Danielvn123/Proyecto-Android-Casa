package com.dani.mijuego;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameAudio;
import com.dani.mijuego.game.GameSave;
import com.dani.mijuego.screens.SplashScreen;

public class Main extends Game {

    public Assets assets;
    public GameAudio audio; // ✅ NUEVO

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        assets = new Assets();
        assets.queue();

        audio = new GameAudio();
        audio.load();

        // NO reproducimos música aquí

        setScreen(new SplashScreen(this));
    }


    @Override
    public void dispose() {
        super.dispose();
        if (audio != null) audio.dispose();   // ✅ NUEVO
        if (assets != null) assets.dispose();
    }
}
