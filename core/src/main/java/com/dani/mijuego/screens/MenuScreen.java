package com.dani.mijuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.I18n;
import com.dani.mijuego.util.UiButton;

public class MenuScreen extends BaseScreen {

    // Textura que se usa como imagen base de todos los botones
    private Texture btnTex;

    // Botones del menú principal
    private final UiButton btnPlay    = new UiButton(0, 0, 1, 1);
    private final UiButton btnRecords = new UiButton(0, 0, 1, 1);
    private final UiButton btnOptions = new UiButton(0, 0, 1, 1);
    private final UiButton btnCredits = new UiButton(0, 0, 1, 1);
    private final UiButton btnHowTo   = new UiButton(0, 0, 1, 1);

    // Constructor del menú principal
    public MenuScreen(Main game) {
        super(game, GameConfig.VW, GameConfig.VH);
    }

    // Indica que esta pantalla usa el fondo tipo menú
    @Override
    protected boolean useMenuBackground() { return true; }

    // Se ejecuta cuando la pantalla se muestra.
    // Carga la textura de botones, organiza posiciones e instala el input.
    @Override
    public void show() {
        super.show();

        btnTex = getTex(Assets.BOTONMENU);

        layoutButtons();
        installDefaultInput();
    }

    // Reproduce el sonido de selección de botón
    private void click() {
        if (game != null && game.audio != null) game.audio.playSelectButton();
    }

    // Se ejecuta cuando cambia el tamaño de pantalla.
    // Recalcula la posición de los botones.
    @Override
    protected void onResize() {
        layoutButtons();
    }

    // Calcula y posiciona todos los botones centrados vertical y horizontalmente.
    private void layoutButtons() {
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        float btnW = 750f;
        float btnH = 230f;
        float gap = 90f;

        float x = (w - btnW) / 2f;
        float totalH = 5f * btnH + 4f * gap;
        float startY = (h + totalH) / 2f - btnH;

        btnPlay.set(x, startY, btnW, btnH);
        btnRecords.set(x, startY - 1f * (btnH + gap), btnW, btnH);
        btnOptions.set(x, startY - 2f * (btnH + gap), btnW, btnH);
        btnCredits.set(x, startY - 3f * (btnH + gap), btnW, btnH);
        btnHowTo.set(x, startY - 4f * (btnH + gap), btnW, btnH);
    }

    // Render principal del menú.
    // Dibuja fondo, botones y textos centrados.
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

        // Obtiene la posición actual del ratón/toque en coordenadas HUD
        Vector3 hud = unprojectToHud(Gdx.input.getX(), Gdx.input.getY());

        // Actualiza estado hover/animaciones de los botones
        btnPlay.update(hud.x, hud.y, delta);
        btnRecords.update(hud.x, hud.y, delta);
        btnOptions.update(hud.x, hud.y, delta);
        btnCredits.update(hud.x, hud.y, delta);
        btnHowTo.update(hud.x, hud.y, delta);

        batch.begin();

        // Dibuja el fondo del menú
        drawMenuBackgroundIfEnabled(worldW, worldH);

        // Dibuja textura base de cada botón
        btnPlay.drawTexture(batch, btnTex, uiLeft, uiBottom);
        btnRecords.drawTexture(batch, btnTex, uiLeft, uiBottom);
        btnOptions.drawTexture(batch, btnTex, uiLeft, uiBottom);
        btnCredits.drawTexture(batch, btnTex, uiLeft, uiBottom);
        btnHowTo.drawTexture(batch, btnTex, uiLeft, uiBottom);

        // Dibuja el texto centrado dentro de cada botón usando I18n
        btnPlay.drawCenteredOutlinedText(batch, outlineFont, fillFont, layout,
            I18n.t("menu_play"), uiLeft, uiBottom, UI_SCALE, UI_OUTLINE_PX);

        btnRecords.drawCenteredOutlinedText(batch, outlineFont, fillFont, layout,
            I18n.t("menu_records"), uiLeft, uiBottom, UI_SCALE, UI_OUTLINE_PX);

        btnOptions.drawCenteredOutlinedText(batch, outlineFont, fillFont, layout,
            I18n.t("menu_options"), uiLeft, uiBottom, UI_SCALE, UI_OUTLINE_PX);

        btnCredits.drawCenteredOutlinedText(batch, outlineFont, fillFont, layout,
            I18n.t("menu_credits"), uiLeft, uiBottom, UI_SCALE, UI_OUTLINE_PX);

        btnHowTo.drawCenteredOutlinedText(batch, outlineFont, fillFont, layout,
            I18n.t("menu_instructions"), uiLeft, uiBottom, UI_SCALE, UI_OUTLINE_PX);

        batch.end();
    }

    // Detecta qué botón ha sido pulsado y cambia de pantalla según corresponda.
    @Override
    protected boolean onTouchDownHud(float xHud, float yHud) {

        if (btnPlay.hit(xHud, yHud)) {
            click();
            game.setScreen(new ModeSelectScreen(game));
            return true;
        }

        if (btnRecords.hit(xHud, yHud)) {
            click();
            game.setScreen(new RecordsScreen(game));
            return true;
        }

        if (btnOptions.hit(xHud, yHud)) {
            click();
            game.setScreen(new OptionsScreen(game, this));
            return true;
        }

        if (btnCredits.hit(xHud, yHud)) {
            click();
            game.setScreen(new CreditsScreen(game, this));
            return true;
        }

        if (btnHowTo.hit(xHud, yHud)) {
            click();
            game.setScreen(new HowToPlayScreen(game, this));
            return true;
        }

        return false;
    }

    // En el menú principal no se hace nada al pulsar atrás.
    @Override
    protected void onBack() {
        // En menú principal no hacemos nada con back.
    }
}
