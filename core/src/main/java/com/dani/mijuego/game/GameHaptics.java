package com.dani.mijuego.game;

import com.badlogic.gdx.Gdx;

public final class GameHaptics {
    private GameHaptics() {}

    public static void vibrate(int ms) {
        if (!GameSave.isVibrationOn()) return;
        try { Gdx.input.vibrate(ms); } catch (Exception ignored) {}
    }
}
