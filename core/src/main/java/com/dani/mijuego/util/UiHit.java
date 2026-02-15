package com.dani.mijuego.util;

import com.badlogic.gdx.math.Rectangle;

public final class UiHit {

    private UiHit() {}

    public static boolean hit(Rectangle r, float x, float y) {
        return r != null && r.contains(x, y);
    }
}
