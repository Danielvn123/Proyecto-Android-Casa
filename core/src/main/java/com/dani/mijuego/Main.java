package com.dani.mijuego;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.screens.SplashScreen;

public class Main extends Game {

    public Assets assets;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        assets = new Assets();
        assets.queue();

        setScreen(new SplashScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        if (assets != null) assets.dispose();
    }
}
