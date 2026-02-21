package com.dani.mijuego.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.I18n;

public class HowToPlayScreen extends BaseScreen {

    private final Screen backScreen;

    public HowToPlayScreen(Main game) {
        this(game, null);
    }

    public HowToPlayScreen(Main game, Screen backScreen) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.backScreen = backScreen;
    }

    @Override
    protected boolean useMenuBackground() { return true; }

    @Override
    protected boolean useBottomBackHint() { return true; }

    @Override
    public void show() {
        super.show();
        installDefaultInput();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        viewport.apply(true);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        batch.begin();

        drawMenuBackgroundIfEnabled(worldW, worldH);

        float uiBottom = cam.position.y - worldH / 2f;

        drawCenteredTitle(I18n.t("how_title"), uiBottom + worldH * 0.88f);

        drawCenteredMultiline(
            I18n.t("how_body"),
            uiBottom + worldH * 0.72f,
            75f,
            UI_SCALE,
            UI_OUTLINE_PX
        );

        drawBottomBackHintIfEnabled(worldW, worldH);

        batch.end();
    }

    @Override
    protected void onBack() {
        if (backScreen != null) game.setScreen(backScreen);
        else game.setScreen(new MenuScreen(game));
    }
}
