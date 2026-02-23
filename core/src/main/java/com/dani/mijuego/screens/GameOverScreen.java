package com.dani.mijuego.screens;

import com.badlogic.gdx.graphics.Texture;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameAudio;

// Pantalla de Game Over.
// Hereda de EndScreenBase y define:
public class GameOverScreen extends EndScreenBase {

    // Referencia al sistema de audio para poder parar música y reproducir sonido de derrota
    private final GameAudio audio;

    // Constructor: recibe juego, score final, monedas y sistema de audio
    public GameOverScreen(Main game, int score, int coins, GameAudio audio) {
        super(game, score, coins);
        this.audio = audio;
    }

    // Se ejecuta al entrar en la pantalla
    @Override
    public void show() {
        // Ejecuta la lógica común de EndScreenBase (fondo, botón, input)
        super.show();

        // Si existe sistema de audio
        if (audio != null) {
            // Detiene toda la música actual (por ejemplo la del gameplay)
            audio.stopAllMusic();
            // Reproduce el sonido específico de Game Over
            audio.playGameOver();
        }
    }

    // Devuelve la textura de fondo que usará esta pantalla
    @Override
    protected Texture resolveBackgroundTexture() {
        // Obtiene la textura GAMEOVER desde el sistema de Assets
        return getTex(Assets.GAMEOVER);
    }

    // Devuelve la clave i18n del texto del botón
    @Override
    protected String buttonTextKey() {
        // go_restart → texto localizado tipo "Reiniciar" / "Restart"
        return "go_restart";
    }

    // Acción que ocurre al pulsar el botón
    @Override
    protected void onButtonPressed() {
        // Vuelve al menú principal
        game.setScreen(new MenuScreen(game));
    }
}
