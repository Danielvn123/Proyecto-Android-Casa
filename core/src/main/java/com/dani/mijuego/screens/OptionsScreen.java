package com.dani.mijuego.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.game.GameConfig;

public class OptionsScreen extends BaseScreen {

    private final Screen backScreen;

    public OptionsScreen(Main game) {
        this(game, null);
    }

    public OptionsScreen(Main game, Screen backScreen) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.backScreen = backScreen;
    }

    @Override
    public void show() {
        installDefaultInput();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.05f, 0.05f, 0.07f, 1);
        viewport.apply();
        batch.setProjectionMatrix(cam.combined);

        batch.begin();

        fillFont.getData().setScale(2.4f);
        outlineFont.getData().setScale(2.4f);

        String t = "OPCIONES\n\n(Aqui pondras volumen,\ncontroles, etc.)\n\nBACK PARA VOLVER";
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
        if (backScreen != null) game.setScreen(backScreen);
        else game.setScreen(new MenuScreen(game));
    }
}
