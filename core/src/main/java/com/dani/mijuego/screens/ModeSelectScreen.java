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

public class ModeSelectScreen extends BaseScreen {

    private Texture b1, b2;

    private final UiButton btnInfinite = new UiButton(0, 0, 1, 1);
    private final UiButton btnLevels   = new UiButton(0, 0, 1, 1);

    public ModeSelectScreen(Main game) {
        super(game, GameConfig.VW, GameConfig.VH);
    }

    @Override
    protected boolean useMenuBackground() { return true; }

    @Override
    protected boolean useBottomBackHint() { return true; }

    @Override
    public void show() {
        super.show();

        b1 = getTex(Assets.BOTONMENU);
        b2 = getTex(Assets.BOTONMENU);

        layoutButtons();
        installDefaultInput();
    }

    private void click() {
        if (game != null && game.audio != null) game.audio.playSelectButton();
    }

    @Override
    protected void onResize() {
        layoutButtons();
    }

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
        btnInfinite.update(hud.x, hud.y, delta);
        btnLevels.update(hud.x, hud.y, delta);

        batch.begin();

        drawMenuBackgroundIfEnabled(worldW, worldH);

        // Título (usa helper común)
        drawCenteredTitle(I18n.t("mode_title"), uiBottom + worldH * 0.88f);

        // Botones
        btnInfinite.drawTexture(batch, b1, uiLeft, uiBottom);
        btnLevels.drawTexture(batch, b2, uiLeft, uiBottom);

        btnInfinite.drawCenteredOutlinedText(batch, outlineFont, fillFont, layout,
            I18n.t("mode_infinite"), uiLeft, uiBottom, UI_SCALE, UI_OUTLINE_PX);

        btnLevels.drawCenteredOutlinedText(batch, outlineFont, fillFont, layout,
            I18n.t("mode_normal"), uiLeft, uiBottom, UI_SCALE, UI_OUTLINE_PX);

        drawBottomBackHintIfEnabled(worldW, worldH);

        batch.end();
    }

    @Override
    protected boolean onTouchDownHud(float xHud, float yHud) {

        if (btnInfinite.hit(xHud, yHud)) {
            click();
            game.setScreen(new GameScreen(game, GameScreen.GameMode.INFINITE));
            return true;
        }

        if (btnLevels.hit(xHud, yHud)) {
            click();
            game.setScreen(new GameScreen(game, GameScreen.GameMode.LEVELS));
            return true;
        }

        return false;
    }

    @Override
    protected void onBack() {
        click();
        game.setScreen(new MenuScreen(game));
    }
}
