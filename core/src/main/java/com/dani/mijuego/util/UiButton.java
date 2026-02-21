package com.dani.mijuego.util;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

/**
 * Botón UI simple: bounds en coordenadas HUD (0..worldW/0..worldH),
 * hover (solo desktop) + escala suave, dibujado de textura y texto centrado.
 */
public class UiButton {

    public final Rectangle bounds = new Rectangle();

    public float baseScale = 1.00f;
    public float hoverScale = 1.07f;
    public float scaleSpeed = 12f;

    public float scale = 1.0f;
    public boolean hovered = false;

    public UiButton(float x, float y, float w, float h) {
        set(x, y, w, h);
    }

    public void set(float x, float y, float w, float h) {
        bounds.set(x, y, w, h);
        scale = baseScale;
        hovered = false;
    }

    public boolean hit(float hudX, float hudY) {
        return bounds.contains(hudX, hudY);
    }

    public void update(float hudX, float hudY, float delta) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            hovered = bounds.contains(hudX, hudY);
        } else {
            hovered = false;
        }

        float target = hovered ? hoverScale : baseScale;
        float t = 1f - (float) Math.exp(-scaleSpeed * delta);
        scale = MathUtils.lerp(scale, target, t);
    }

    public void drawTexture(SpriteBatch batch, Texture tex, float uiLeft, float uiBottom) {
        if (batch == null || tex == null) return;

        float cx = uiLeft + bounds.x + bounds.width / 2f;
        float cy = uiBottom + bounds.y + bounds.height / 2f;

        float w = bounds.width * scale;
        float h = bounds.height * scale;

        batch.draw(tex, cx - w / 2f, cy - h / 2f, w, h);
    }

    public void drawCenteredOutlinedText(SpriteBatch batch,
                                         BitmapFont outlineFont,
                                         BitmapFont fillFont,
                                         GlyphLayout layout,
                                         String text,
                                         float uiLeft,
                                         float uiBottom,
                                         float baseTextScale,
                                         float outlinePx) {
        if (batch == null || outlineFont == null || fillFont == null || layout == null) return;
        if (text == null) return;

        float s = baseTextScale * scale;
        fillFont.getData().setScale(s);
        outlineFont.getData().setScale(s);

        // Forzamos colores por si algún screen los cambió
        fillFont.setColor(1f, 1f, 1f, 1f);
        outlineFont.setColor(0f, 0f, 0f, 1f);

        layout.setText(fillFont, text);

        float cx = uiLeft + bounds.x + (bounds.width - layout.width) / 2f;
        float cy = uiBottom + bounds.y + (bounds.height + layout.height) / 2f;

        FontUtils.drawOutlined(batch, outlineFont, fillFont, text, cx, cy, outlinePx);

        fillFont.getData().setScale(1f);
        outlineFont.getData().setScale(1f);
    }
}
