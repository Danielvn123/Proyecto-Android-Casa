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

        fillFont.getData().setScale(2.2f);
        outlineFont.getData().setScale(2.2f);

        String t =
            I18n.t("credits_title") + "\n\n" +
                I18n.t("credits_body");

        layout.setText(fillFont, t);

        float x = cam.position.x - layout.width / 2f;
        float y = cam.position.y + layout.height / 2f;

        drawOutlined(t, x, y, 3.0f);

        drawBottomBackHintIfEnabled(worldW, worldH);

        fillFont.getData().setScale(1f);
        outlineFont.getData().setScale(1f);

        batch.end();
    }

    @Override
    protected void onBack() {
        game.setScreen(new MenuScreen(game));
    }
}
