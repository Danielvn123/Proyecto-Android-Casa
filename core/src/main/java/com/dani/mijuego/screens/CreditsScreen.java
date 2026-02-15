package com.dani.mijuego.screens;

import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.game.GameConfig;

public class CreditsScreen extends BaseScreen {

    public CreditsScreen(Main game) {
        super(game, GameConfig.VW, GameConfig.VH);
    }

    @Override
    public void show() {
        installDefaultInput();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.05f, 0.07f, 0.08f, 1);
        viewport.apply();
        batch.setProjectionMatrix(cam.combined);

        batch.begin();

        fillFont.getData().setScale(2.2f);
        outlineFont.getData().setScale(2.2f);

        String t =
            "CREDITOS\n\n" +
                "Juego hecho por Dani\n" +
                "LibGDX\n\n" +
                "BACK PARA VOLVER";
        layout.setText(fillFont, t);

        float x = cam.position.x - layout.width / 2f;
        float y = cam.position.y + layout.height / 2f;

        drawOutlined(t, x, y, 3.0f);

        fillFont.getData().setScale(1f);
        outlineFont.getData().setScale(1f);

        batch.end();
    }

    @Override
    protected void onBack() {
        game.setScreen(new MenuScreen(game));
    }
}
