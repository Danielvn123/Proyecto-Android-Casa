package com.dani.mijuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameAudio;

public class SplashScreen extends BaseScreen {

    private Texture fondo;

    private float animTime = 0f;

    private static final float VW = 720f;
    private static final float VH = 1280f;

    private boolean queuedMenu = false;
    private boolean canContinue = false;

    private final GameAudio audio = new GameAudio();
    private boolean audioStarted = false;

    public SplashScreen(Main game) {
        super(game, VW, VH);
    }

    @Override
    public void show() {

        fillFont.setColor(1f, 1f, 1f, 1f);
        outlineFont.setColor(0f, 0f, 1f, 1f);

        if (!game.assets.manager.isLoaded(Assets.FONDO_TRABAJO, Texture.class)) {
            game.assets.manager.load(Assets.FONDO_TRABAJO, Texture.class);
            game.assets.manager.finishLoadingAsset(Assets.FONDO_TRABAJO);
        }
        fondo = game.assets.manager.get(Assets.FONDO_TRABAJO, Texture.class);

        if (!queuedMenu) {
            queuedMenu = true;
            game.assets.manager.load(Assets.FONDO_MENU, Texture.class);
            game.assets.manager.load(Assets.BOTONMENU1, Texture.class);
            game.assets.manager.load(Assets.BOTONMENU2, Texture.class);
            game.assets.manager.load(Assets.BOTONMENU3, Texture.class);
            game.assets.manager.load(Assets.BOTONMENU4, Texture.class);
        }

        if (!audioStarted) {
            audioStarted = true;
            audio.load();
            audio.playFondo();
        }

        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(int x, int y, int p, int b) {
                if (canContinue) {
                    audio.stopFondo();
                    game.setScreen(new MenuScreen(game));
                }
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.SPACE || keycode == Input.Keys.ENTER) {
                    if (canContinue) {
                        audio.stopFondo();
                        game.setScreen(new MenuScreen(game));
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void drawOutlinedText(String text, float x, float y, float outlinePx) {
        outlineFont.draw(batch, text, x - outlinePx, y);
        outlineFont.draw(batch, text, x + outlinePx, y);
        outlineFont.draw(batch, text, x, y - outlinePx);
        outlineFont.draw(batch, text, x, y + outlinePx);

        outlineFont.draw(batch, text, x - outlinePx, y - outlinePx);
        outlineFont.draw(batch, text, x + outlinePx, y - outlinePx);
        outlineFont.draw(batch, text, x - outlinePx, y + outlinePx);
        outlineFont.draw(batch, text, x + outlinePx, y + outlinePx);

        fillFont.draw(batch, text, x, y);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        if (!canContinue) {
            canContinue = game.assets.manager.update();
        }

        viewport.apply();
        batch.setProjectionMatrix(cam.combined);

        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        float left = cam.position.x - w / 2f;
        float bottom = cam.position.y - h / 2f;

        batch.begin();

        if (fondo != null) {
            // Igual que tu versión antigua: ocupa TODO el viewport (en Android Stretch => ocupa toda la pantalla)
            batch.draw(fondo, left, bottom, w, h);
        }

        animTime += delta;

        String msg = "TOCA PARA\n EMPEZAR";

        float alpha = 0.65f + 0.35f * MathUtils.sin(animTime * 4f);
        float scale = 2.6f + 0.10f * MathUtils.sin(animTime * 4f);

        outlineFont.getData().setScale(scale);
        fillFont.getData().setScale(scale);

        fillFont.setColor(1f, 1f, 1f, 1f);
        outlineFont.setColor(0f, 0f, 1f, 1f);

        layout.setText(fillFont, msg);

        float x = left + (w - layout.width) / 2f;
        float y = bottom + 200f;

        drawOutlinedText(msg, x, y, 3.5f);

        outlineFont.setColor(Color.BLACK);
        fillFont.setColor(Color.WHITE);

        batch.end();
    }

    @Override
    public void resize(int w, int h) {
        super.resize(w, h);
    }

    @Override
    public void resume() {
        if (audioStarted) audio.playFondo();
    }

    @Override
    public void hide() {
        audio.stopFondo();
    }

    @Override
    public void dispose() {
        super.dispose();
        audio.dispose();
    }
}
