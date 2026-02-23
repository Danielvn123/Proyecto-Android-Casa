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

    // Área del botón en coordenadas HUD (se usa para click/hover y para dibujar)
    public final Rectangle bounds = new Rectangle();

    // Escala normal del botón (sin hover)
    public float baseScale = 1.00f;

    // Escala cuando está el ratón encima (solo en PC)
    public float hoverScale = 1.07f;

    // Velocidad a la que cambia de escala (suavizado)
    public float scaleSpeed = 12f;

    // Escala actual (se interpola entre baseScale y hoverScale)
    public float scale = 1.0f;

    // Indica si está en hover (solo desktop)
    public boolean hovered = false;

    // Crea el botón y le asigna su área inicial
    public UiButton(float x, float y, float w, float h) {
        set(x, y, w, h);
    }

    // Cambia el área del botón y reinicia su estado visual
    public void set(float x, float y, float w, float h) {
        bounds.set(x, y, w, h);
        scale = baseScale;
        hovered = false;
    }

    // Devuelve true si un punto HUD cae dentro del botón
    public boolean hit(float hudX, float hudY) {
        return bounds.contains(hudX, hudY);
    }

    // Actualiza hover (solo desktop) y aplica interpolación de escala suave
    public void update(float hudX, float hudY, float delta) {

        // En PC se permite hover con ratón, en móvil no
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            hovered = bounds.contains(hudX, hudY);
        } else {
            hovered = false;
        }

        // Decide a qué escala debe tender (base o hover)
        float target = hovered ? hoverScale : baseScale;

        // Factor de suavizado dependiente del delta (transición estable)
        float t = 1f - (float) Math.exp(-scaleSpeed * delta);

        // Interpola hacia la escala objetivo
        scale = MathUtils.lerp(scale, target, t);
    }

    // Dibuja la textura centrada dentro del botón aplicando la escala actual
    public void drawTexture(SpriteBatch batch, Texture tex, float uiLeft, float uiBottom) {
        if (batch == null || tex == null) return;

        // Centro del botón en pantalla (coordenadas mundo + offset HUD)
        float cx = uiLeft + bounds.x + bounds.width / 2f;
        float cy = uiBottom + bounds.y + bounds.height / 2f;

        // Tamaño escalado
        float w = bounds.width * scale;
        float h = bounds.height * scale;

        // Dibujo centrado
        batch.draw(tex, cx - w / 2f, cy - h / 2f, w, h);
    }

    // Dibuja texto centrado con borde dentro del botón, también afectado por la escala
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

        // Escala del texto: escala base multiplicada por la escala del botón
        float s = baseTextScale * scale;
        fillFont.getData().setScale(s);
        outlineFont.getData().setScale(s);

        // Colores por defecto (por si otra pantalla los modificó)
        fillFont.setColor(1f, 1f, 1f, 1f);
        outlineFont.setColor(0f, 0f, 0f, 1f);

        // Calcula dimensiones del texto con la fuente de relleno
        layout.setText(fillFont, text);

        // Calcula posición para que quede centrado dentro del botón
        float cx = uiLeft + bounds.x + (bounds.width - layout.width) / 2f;
        float cy = uiBottom + bounds.y + (bounds.height + layout.height) / 2f;

        // Dibuja texto con borde usando FontUtils
        FontUtils.drawOutlined(batch, outlineFont, fillFont, text, cx, cy, outlinePx);

        // Restaura escala de fuentes para no afectar a otros dibujados
        fillFont.getData().setScale(1f);
        outlineFont.getData().setScale(1f);
    }
}
