package com.dani.mijuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.GameMode;
import com.dani.mijuego.game.I18n;
import com.dani.mijuego.util.UiButton;

public class ModeSelectScreen extends BaseScreen {

    // Textura base utilizada para dibujar los botones
    private Texture btnTex;

    // Botones para seleccionar el modo de juego
    private final UiButton btnInfinite = new UiButton(0, 0, 1, 1);
    private final UiButton btnLevels   = new UiButton(0, 0, 1, 1);

    // Constructor de la pantalla de selección de modo
    public ModeSelectScreen(Main game) {
        super(game, GameConfig.VW, GameConfig.VH);
    }

    // Indica que esta pantalla usa el fondo tipo menú
    @Override
    protected boolean useMenuBackground() { return true; }

    // Indica que debe mostrarse el texto inferior para volver atrás
    @Override
    protected boolean useBottomBackHint() { return true; }

    // Se ejecuta cuando la pantalla se muestra.
    // Carga la textura de botones, posiciona los botones e instala el input.
    @Override
    public void show() {
        super.show();

        btnTex = getTex(Assets.BOTONMENU);

        layoutButtons();
        installDefaultInput();
    }

    // Reproduce el sonido al pulsar un botón
    private void click() {
        if (game != null && game.audio != null) game.audio.playSelectButton();
    }

    // Se ejecuta cuando cambia el tamaño de pantalla.
    // Recalcula la posición de los botones.
    @Override
    protected void onResize() {
        layoutButtons();
    }

    // Calcula y posiciona los botones centrados horizontalmente.
    private void layoutButtons() {
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        float btnW = 750f;
        float btnH = 300f;
        float gap  = 150f;

        float x = (w - btnW) / 2f;

        float startY = h * 0.52f;
        btnInfinite.set(x, startY, btnW, btnH);
        btnLevels.set(x, startY - (btnH + gap), btnW, btnH);
    }

    // Render principal de la pantalla.
    // Dibuja fondo, título, botones y texto inferior para volver.
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        viewport.apply(true);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        float uiLeft = cam.position.x - worldW / 2f;
        float uiBottom = cam.position.y - worldH / 2f;

        // Obtiene posición actual del ratón/toque en coordenadas HUD
        Vector3 hud = unprojectToHud(Gdx.input.getX(), Gdx.input.getY());

        // Actualiza estado hover/animaciones de botones
        btnInfinite.update(hud.x, hud.y, delta);
        btnLevels.update(hud.x, hud.y, delta);

        batch.begin();

        // Dibuja el fondo
        drawMenuBackgroundIfEnabled(worldW, worldH);

        // Dibuja el título centrado (internacionalizado)
        drawCenteredTitle(I18n.t("mode_title"), uiBottom + worldH * 0.88f);

        // Dibuja las texturas de los botones
        btnInfinite.drawTexture(batch, btnTex, uiLeft, uiBottom);
        btnLevels.drawTexture(batch, btnTex, uiLeft, uiBottom);

        // Dibuja el texto centrado dentro de cada botón
        btnInfinite.drawCenteredOutlinedText(batch, outlineFont, fillFont, layout,
            I18n.t("mode_infinite"), uiLeft, uiBottom, UI_SCALE, UI_OUTLINE_PX);

        btnLevels.drawCenteredOutlinedText(batch, outlineFont, fillFont, layout,
            I18n.t("mode_normal"), uiLeft, uiBottom, UI_SCALE, UI_OUTLINE_PX);

        // Dibuja el texto inferior para volver atrás
        drawBottomBackHintIfEnabled(worldW, worldH);

        batch.end();
    }

    // Detecta qué botón ha sido pulsado y lanza el modo correspondiente.
    @Override
    protected boolean onTouchDownHud(float xHud, float yHud) {

        if (btnInfinite.hit(xHud, yHud)) {
            click();
            game.setScreen(new GameScreen(game, GameMode.INFINITE));
            return true;
        }

        if (btnLevels.hit(xHud, yHud)) {
            click();
            game.setScreen(new GameScreen(game, GameMode.LEVELS));
            return true;
        }

        return false;
    }

    // Acción al pulsar atrás: vuelve al menú principal.
    @Override
    protected void onBack() {
        click();
        game.setScreen(new MenuScreen(game));
    }
}
