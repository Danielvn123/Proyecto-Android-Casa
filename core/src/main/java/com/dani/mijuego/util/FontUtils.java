package com.dani.mijuego.util;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public final class FontUtils {

    private FontUtils() {}

    public static void drawOutlined(SpriteBatch batch, BitmapFont outline, BitmapFont fill,
                                    String text, float x, float y, float outlinePx) {

        outline.draw(batch, text, x - outlinePx, y);
        outline.draw(batch, text, x + outlinePx, y);
        outline.draw(batch, text, x, y - outlinePx);
        outline.draw(batch, text, x, y + outlinePx);

        outline.draw(batch, text, x - outlinePx, y - outlinePx);
        outline.draw(batch, text, x + outlinePx, y - outlinePx);
        outline.draw(batch, text, x - outlinePx, y + outlinePx);
        outline.draw(batch, text, x + outlinePx, y + outlinePx);

        fill.draw(batch, text, x, y);
    }
}
