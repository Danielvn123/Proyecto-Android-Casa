package com.dani.mijuego.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dani.mijuego.Main;
import com.dani.mijuego.util.FontUtils;

public abstract class BaseScreen extends ScreenAdapter {

    protected final Main game;

    protected SpriteBatch batch;
    protected OrthographicCamera cam;
    protected Viewport viewport;

    protected BitmapFont font;
    protected BitmapFont fillFont;
    protected BitmapFont outlineFont;
    protected GlyphLayout layout;

    private final Vector3 tmp = new Vector3();

    protected BaseScreen(Main game, float vw, float vh) {
        this.game = game;

        batch = new SpriteBatch();
        cam = new OrthographicCamera();

        // Desktop: FitViewport -> mantiene proporción (puede dejar bordes)
        // Android: StretchViewport -> ocupa toda la pantalla (sin bordes, sin recorte, puede estirar)
        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            viewport = new FitViewport(vw, vh, cam);
        } else {
            viewport = new StretchViewport(vw, vh, cam);
        }

        viewport.apply(true);
        cam.position.set(vw / 2f, vh / 2f, 0f);
        cam.update();

        font = new BitmapFont();
        fillFont = new BitmapFont();
        outlineFont = new BitmapFont();
        layout = new GlyphLayout();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        onResize();
    }

    protected void onResize() {}

    protected Vector3 unprojectToHud(int screenX, int screenY) {
        tmp.set(screenX, screenY, 0f);
        viewport.unproject(tmp);

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        float visibleLeft = cam.position.x - worldW / 2f;
        float visibleBottom = cam.position.y - worldH / 2f;

        tmp.x = tmp.x - visibleLeft;
        tmp.y = tmp.y - visibleBottom;

        return tmp;
    }

    protected void drawOutlined(String text, float x, float y, float outlinePx) {
        FontUtils.drawOutlined(batch, outlineFont, fillFont, text, x, y, outlinePx);
    }

    protected void installDefaultInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
                    onBack();
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Vector3 hud = unprojectToHud(screenX, screenY);
                return onTouchDownHud(hud.x, hud.y);
            }
        });
    }

    protected void onBack() {}

    protected boolean onTouchDownHud(float xHud, float yHud) {
        return false;
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (fillFont != null) fillFont.dispose();
        if (outlineFont != null) outlineFont.dispose();
    }
}
