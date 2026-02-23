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

    private Texture b1, b3, b4;

    private final UiButton btnContinue = new UiButton(0, 0, 1, 1);
    private final UiButton btnRestart  = new UiButton(0, 0, 1, 1);
    private final UiButton btnExit     = new UiButton(0, 0, 1, 1);

    private final GameScreen gameScreen;
    private final GameAudio audio;

    public PauseScreen(Main game, GameScreen gameScreen, GameAudio audio) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.gameScreen = gameScreen;
        this.audio = audio;
    }

    @Override
    protected boolean useMenuBackground() { return true; }

    @Override
    public void show() {
        super.show();

        b1 = getTex(Assets.BOTONMENU);
        b3 = getTex(Assets.BOTONMENU);
        b4 = getTex(Assets.BOTONMENU);

        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        layoutButtons();
        installDefaultInput();
    }

    private void sfxClick() {
        if (audio != null) audio.playSelectButton();
        else if (game != null && game.audio != null) game.audio.playSelectButton();
    }

    @Override
    protected void onResize() {
        layoutButtons();
    }

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

        Vector3 hud = unprojectToHud(Gdx.input.getX(), Gdx.input.getY());
        btnContinue.update(hud.x, hud.y, delta);
        btnRestart.update(hud.x, hud.y, delta);
        btnExit.update(hud.x, hud.y, delta);

        batch.begin();

        drawMenuBackgroundIfEnabled(worldW, worldH);

        btnContinue.drawTexture(batch, b1, uiLeft, uiBottom);
        btnRestart.drawTexture(batch, b3, uiLeft, uiBottom);
        btnExit.drawTexture(batch, b4, uiLeft, uiBottom);

        btnContinue.drawCenteredOutlinedText(batch, outlineFont, fillFont, layout,
            I18n.t("pause_continue"), uiLeft, uiBottom, UI_SCALE, UI_OUTLINE_PX);

        btnRestart.drawCenteredOutlinedText(batch, outlineFont, fillFont, layout,
            I18n.t("pause_restart"), uiLeft, uiBottom, UI_SCALE, UI_OUTLINE_PX);

        btnExit.drawCenteredOutlinedText(batch, outlineFont, fillFont, layout,
            I18n.t("pause_exit"), uiLeft, uiBottom, UI_SCALE, UI_OUTLINE_PX);

        batch.end();
    }

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

    @Override
    protected void onBack() {
        sfxClick();
        game.setScreen(gameScreen);
    }
}
