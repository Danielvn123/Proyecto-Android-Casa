package com.dani.mijuego.game;

import com.badlogic.gdx.Gdx;

// Clase auxiliar para gestionar la vibración del dispositivo
public final class GameHaptics {

    // Constructor privado para que no se pueda instanciar
    private GameHaptics() {}

    // Activa la vibración durante el tiempo indicado
    public static void vibrate(int ms) {

        // Si la vibración está desactivada en opciones, no hace nada
        if (!GameSave.isVibrationOn()) return;

        try {
            // Llama al sistema del dispositivo para vibrar
            Gdx.input.vibrate(ms);
        } catch (Exception ignored) {
            // Si el dispositivo no soporta vibración, se ignora el error
        }
    }
}
