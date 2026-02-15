package com.dani.mijuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameAudio;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.util.FontUtils;

public class VictoryScreen extends BaseScreen {

    private final int score;
    private final int coins;
    private final GameAudio audio;

    private Texture fondo;

    private BitmapFont fillFont;
    private BitmapFont outlineFont;
    private GlyphLayout layout;

    public VictoryScreen(Main game, int score, int coins, GameAudio audio) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.score = score;
        this.coins = coins;
        this.audio = audio;
    }

    @Override
    public void show() {
        fondo = getTex(Assets.VICTORY); // o el que quieras
        fillFont = new BitmapFont();
        outlineFont = new BitmapFont();
        layout = new GlyphLayout();

        fillFont.setColor(Color.WHITE);
        outlineFont.setColor(Color.BLACK);

        installInput();
    }

    private Texture getTex(String path) {
        try {
            if (game.assets.manager.isLoaded(path, Texture.class)) {
                return game.assets.manager.get(path, Texture.class);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void installInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.SPACE || keycode == Input.Keys.ENTER ||
                    keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
                    audio.playSelectButton();
                    game.setScreen(new MenuScreen(game));
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 hud = unprojectToHud(screenX, screenY);
                audio.playSelectButton();
                game.setScreen(new MenuScreen(game));
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0,1);
        viewport.apply();
        batch.setProjectionMatrix(cam.combined);

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        float uiLeft = cam.position.x - worldW / 2f;
        float uiBottom = cam.position.y - worldH / 2f;

        batch.begin();

        if (fondo != null) batch.draw(fondo, uiLeft, uiBottom, worldW, worldH);

        float titleScale = 4.0f;
        outlineFont.getData().setScale(titleScale);
        fillFont.getData().setScale(titleScale);

        String title = "VICTORIA";
        layout.setText(fillFont, title);

        float x = cam.position.x - layout.width / 2f;
        float y = cam.position.y + 260f;

        FontUtils.drawOutlined(batch, outlineFont, fillFont, title, x, y, 4f);

        float infoScale = 2.4f;
        outlineFont.getData().setScale(infoScale);
        fillFont.getData().setScale(infoScale);

        String info = "ALTURA: " + score + " m\nMONEDAS: " + coins;
        layout.setText(fillFont, info);

        float ix = cam.position.x - layout.width / 2f;
        float iy = cam.position.y + 40f;

        FontUtils.drawOutlined(batch, outlineFont, fillFont, info, ix, iy, 3.5f);

        float tipScale = 1.8f;
        outlineFont.getData().setScale(tipScale);
        fillFont.getData().setScale(tipScale);

        String tip = "PULSA PARA VOLVER AL MENU";
        layout.setText(fillFont, tip);

        float tx = cam.position.x - layout.width / 2f;
        float ty = cam.position.y - 250f;

        FontUtils.drawOutlined(batch, outlineFont, fillFont, tip, tx, ty, 3f);

        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (fillFont != null) fillFont.dispose();
        if (outlineFont != null) outlineFont.dispose();
    }
}
