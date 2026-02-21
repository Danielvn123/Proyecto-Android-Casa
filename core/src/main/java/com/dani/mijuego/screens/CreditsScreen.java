package com.dani.mijuego.screens;

import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.I18n;

public class CreditsScreen extends BaseScreen {

    public CreditsScreen(Main game) {
        super(game, GameConfig.VW, GameConfig.VH);
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

        drawTitle(I18n.t("credits_title"));
        drawCreditsBody(worldW, worldH);

        drawBottomBackHintIfEnabled(worldW, worldH);

        batch.end();
    }

    private void drawTitle(String text) {
        if (text == null) text = "";

        setTitleStyle();
        layout.setText(fillFont, text);

        float x = cam.position.x - layout.width / 2f;
        float y = cam.position.y + viewport.getWorldHeight() * 0.38f;

        drawOutlined(text, x, y, TITLE_OUTLINE_PX);
        resetFontScale();
    }

    private void drawCreditsBody(float worldW, float worldH) {

        setUiStyle();

        float startY = cam.position.y + worldH * 0.20f;
        float lineSpacing = 90f;

        String body = I18n.t("credits_body");
        String[] lines = (body == null ? new String[0] : body.split("\n"));

        float y = startY;

        for (String line : lines) {
            if (line == null) line = "";

            layout.setText(fillFont, line);
            float x = cam.position.x - layout.width / 2f;

            drawOutlined(line, x, y, UI_OUTLINE_PX);
            y -= lineSpacing;
        }

        resetFontScale();
    }

    @Override
    protected void onBack() {
        game.setScreen(new MenuScreen(game));
    }
}
