package com.dani.mijuego.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.I18n;

public abstract class EndScreenBase extends BaseScreen {

    // Puntuación final (altura alcanzada)
    protected final int score;

    // Monedas recogidas en la partida
    protected final int coins;

    // Textura de fondo (GameOver o Victory)
    protected Texture bgTex;

    // Rectángulo que define el botón inferior
    protected Rectangle btn;

    // Dimensiones y posición del botón
    private static final float BTN_W = 520f;
    private static final float BTN_H = 140f;
    private static final float BTN_Y = 90f;

    // Constructor base para pantallas finales.
    // Recibe el juego y las estadísticas finales (score y monedas).
    protected EndScreenBase(Main game, int score, int coins) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.score = score;
        this.coins = coins;
    }

    // Devuelve la textura de fondo específica (la implementan GameOver o Victory).
    protected abstract Texture resolveBackgroundTexture();

    // Devuelve la clave del texto del botón (para internacionalización).
    protected abstract String buttonTextKey();

    // Acción que se ejecuta al pulsar el botón.
    protected abstract void onButtonPressed();

    // Método opcional que se ejecuta al entrar en la pantalla (hook).
    protected void onEnter() {}

    // Se ejecuta cuando la pantalla se muestra.
    // Inicializa fondo, botón y sistema de input.
    @Override
    public void show() {
        super.show();

        // Obtiene la textura de fondo concreta
        bgTex = resolveBackgroundTexture();

        // Centra el botón horizontalmente en pantalla
        float w = viewport.getWorldWidth();
        btn = new Rectangle((w - BTN_W) / 2f, BTN_Y, BTN_W, BTN_H);

        // Hook opcional
        onEnter();

        // Instala input por defecto (tecla atrás + touch)
        installDefaultInput();
    }

    // Render principal de la pantalla final.
    // Dibuja fondo, estadísticas y botón.
    @Override
    public void render(float delta) {
        // Limpia la pantalla en negro
        ScreenUtils.clear(0, 0, 0, 1);

        // Aplica viewport y actualiza cámara
        viewport.apply(true);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        // Calcula límites visibles
        float uiLeft = cam.position.x - w / 2f;
        float uiTop = cam.position.y + h / 2f;
        float uiBottom = cam.position.y - h / 2f;

        batch.begin();

        // Dibuja el fondo completo si existe
        if (bgTex != null) {
            batch.draw(bgTex, uiLeft, uiBottom, w, h);
        }

        // Texto de estadísticas

        font.getData().setScale(4.5f);
        font.setColor(1f, 1f, 1f, 1f);

        // Texto altura final
        String alturaTxt = I18n.t("go_height") + " " + score + " m";
        layout.setText(font, alturaTxt);
        font.draw(batch, layout, uiLeft + 340f, uiTop - 250f);

        // Texto monedas recogidas
        String monedasTxt = I18n.t("go_coins") + " " + coins;
        layout.setText(font, monedasTxt);
        font.draw(batch, layout, uiLeft + 340f, uiTop - 380f);

        // Texto del botón

        // Obtiene texto internacionalizado del botón
        String btnText = I18n.t(buttonTextKey());
        layout.setText(font, btnText);

        // Centra el texto dentro del rectángulo del botón
        float textX = uiLeft + btn.x + (btn.width - layout.width) / 2f;
        float textY = uiBottom + btn.y + (btn.height + layout.height) / 2f;

        font.draw(batch, layout, textX, textY);

        // Restaura escala de fuente
        font.getData().setScale(1f);

        batch.end();
    }

    // Detecta toques en el HUD.
    // Si el toque cae dentro del botón, ejecuta la acción correspondiente.
    @Override
    protected boolean onTouchDownHud(float xHud, float yHud) {
        if (btn != null && btn.contains(xHud, yHud)) {
            onButtonPressed();
            return true;
        }
        return false;
    }
}
