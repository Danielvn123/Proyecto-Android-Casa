package com.dani.mijuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameAudio;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.I18n;
import com.dani.mijuego.util.UiButton;

public class PauseScreen extends BaseScreen {

    // Textura base para dibujar los botones
    private Texture btnTex;

    // Botones del menú de pausa
    private final UiButton btnContinue = new UiButton(0, 0, 1, 1);
    private final UiButton btnRestart  = new UiButton(0, 0, 1, 1);
    private final UiButton btnExit     = new UiButton(0, 0, 1, 1);

    // Referencia a la GameScreen que se estaba jugando (para volver al continuar)
    private final GameScreen gameScreen;

    // Sistema de audio usado para reproducir el sonido de selección
    private final GameAudio audio;

    // Constructor: recibe el juego, la pantalla de juego actual y el audio
    public PauseScreen(Main game, GameScreen gameScreen, GameAudio audio) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.gameScreen = gameScreen;
        this.audio = audio;
    }

    // Indica que esta pantalla usa el fondo tipo menú
    @Override
    protected boolean useMenuBackground() { return true; }

    // Se ejecuta al entrar en la pantalla:
    // carga textura de botones, captura tecla BACK y prepara UI e input
    @Override
    public void show() {
        super.show();

        btnTex = getTex(Assets.BOTONMENU);

        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        layoutButtons();
        installDefaultInput();
    }

    // Reproduce el sonido de click (usa audio recibido o el del juego si existe)
    private void sfxClick() {
        if (audio != null) audio.playSelectButton();
        else if (game != null && game.audio != null) game.audio.playSelectButton();
    }

    // Cuando cambia el tamaño, recalcula la posición de los botones
    @Override
    protected void onResize() {
        layoutButtons();
    }

    // Posiciona los 3 botones centrados vertical y horizontalmente
    private void layoutButtons() {
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        float btnW = 750f;
        float btnH = 250f;
        float gap = 150f;

        float x = (w - btnW) / 2f;

        float totalH = 3f * btnH + 2f * gap;
        float startY = (h + totalH) / 2f - btnH;

        btnContinue.set(x, startY, btnW, btnH);
        btnRestart.set(x, startY - (btnH + gap), btnW, btnH);
        btnExit.set(x, startY - 2f * (btnH + gap), btnW, btnH);
    }

    // Render principal del PauseScreen:
    // dibuja fondo, botones y textos internacionalizados
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

        // Actualiza estado hover/animación de botones usando coordenadas HUD
        Vector3 hud = unprojectToHud(Gdx.input.getX(), Gdx.input.getY());
        btnContinue.update(hud.x, hud.y, delta);
        btnRestart.update(hud.x, hud.y, delta);
        btnExit.update(hud.x, hud.y, delta);

        batch.begin();

        // Fondo de menú
        drawMenuBackgroundIfEnabled(worldW, worldH);

        // Dibuja textura base de cada botón
        btnContinue.drawTexture(batch, btnTex, uiLeft, uiBottom);
        btnRestart.drawTexture(batch, btnTex, uiLeft, uiBottom);
        btnExit.drawTexture(batch, btnTex, uiLeft, uiBottom);

        // Dibuja textos centrados (traducciones con I18n)
        btnContinue.drawCenteredOutlinedText(batch, outlineFont, fillFont, layout,
            I18n.t("pause_continue"), uiLeft, uiBottom, UI_SCALE, UI_OUTLINE_PX);

        btnRestart.drawCenteredOutlinedText(batch, outlineFont, fillFont, layout,
            I18n.t("pause_restart"), uiLeft, uiBottom, UI_SCALE, UI_OUTLINE_PX);

        btnExit.drawCenteredOutlinedText(batch, outlineFont, fillFont, layout,
            I18n.t("pause_exit"), uiLeft, uiBottom, UI_SCALE, UI_OUTLINE_PX);

        batch.end();
    }

    // Gestiona los toques en HUD:
    // - Continuar: vuelve a la GameScreen existente
    // - Reiniciar: crea una nueva GameScreen y empieza desde cero
    // - Salir: vuelve al menú principal
    @Override
    protected boolean onTouchDownHud(float xHud, float yHud) {

        if (btnContinue.hit(xHud, yHud)) {
            sfxClick();
            game.setScreen(gameScreen);
            return true;
        }

        if (btnRestart.hit(xHud, yHud)) {
            sfxClick();
            game.setScreen(new GameScreen(game));
            return true;
        }

        if (btnExit.hit(xHud, yHud)) {
            sfxClick();
            game.setScreen(new MenuScreen(game));
            return true;
        }

        return false;
    }

    // Acción al pulsar BACK: equivale a continuar (volver al juego)
    @Override
    protected void onBack() {
        sfxClick();
        game.setScreen(gameScreen);
    }
}
