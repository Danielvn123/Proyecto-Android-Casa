package com.dani.mijuego;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameAudio;
import com.dani.mijuego.game.GameSave;
import com.dani.mijuego.screens.SplashScreen;

// Clase principal del juego (punto de entrada en LibGDX)
// Extiende Game para poder cambiar entre pantallas (Screens)
public class Main extends Game {

    // Gestor de recursos (texturas, etc.)
    public Assets assets;

    // Sistema global de audio del juego
    public GameAudio audio;

    @Override
    public void create() {

        // Activa logs en modo debug
        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        // Inicializa y encola los assets para cargarlos
        assets = new Assets();
        assets.queue();

        // Inicializa el sistema de audio
        audio = new GameAudio();
        audio.load();

        // Activa o desactiva el audio según lo guardado en opciones
        audio.setEnabled(GameSave.isMusicOn());

        // Establece la primera pantalla del juego (Splash)
        setScreen(new SplashScreen(this));
    }

    @Override
    public void dispose() {

        // Llama al dispose de Game (libera pantalla actual)
        super.dispose();

        // Libera recursos de audio si existen
        if (audio != null) audio.dispose();

        // Libera recursos gráficos si existen
        if (assets != null) assets.dispose();
    }
}
