package com.dani.mijuego.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.I18n;
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

    // ==========================
    // MENU CHROME (fondo + hint abajo)
    // ==========================
    private Texture menuBgTex = null;

    protected static final float BACK_HINT_ZONE_H = 220f; // zona inferior tocable
    protected static final float BACK_HINT_Y = 120f;      // altura del texto

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

    // ==========================
    // Activadores (override en screens)
    // ==========================
    protected boolean useMenuBackground() { return false; }
    protected boolean useBottomBackHint() { return false; }

    // ==========================
    // HUD unproject
    // ==========================
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

    // ==========================
    // Texto con borde
    // ==========================
    protected void drawOutlined(String text, float x, float y, float outlinePx) {
        FontUtils.drawOutlined(batch, outlineFont, fillFont, text, x, y, outlinePx);
    }

    // ==========================
    // Fondo tipo menú
    // ==========================
    protected Texture getMenuBackground() {
        if (menuBgTex != null) return menuBgTex;

        try {
            if (game != null && game.assets != null) {
                if (!game.assets.manager.isLoaded(Assets.FONDO_MENU, Texture.class)) {
                    game.assets.manager.load(Assets.FONDO_MENU, Texture.class);
                    game.assets.manager.finishLoadingAsset(Assets.FONDO_MENU);
                }
                menuBgTex = game.assets.manager.get(Assets.FONDO_MENU, Texture.class);
            }
        } catch (Exception ignored) {}

        return menuBgTex;
    }

    protected void drawMenuBackgroundIfEnabled(float worldW, float worldH) {
        if (!useMenuBackground()) return;

        Texture bg = getMenuBackground();
        if (bg == null) return;

        float uiLeft = cam.position.x - worldW / 2f;
        float uiBottom = cam.position.y - worldH / 2f;

        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(bg, uiLeft, uiBottom, worldW, worldH);
    }

    // ==========================
    // Hint abajo + tap para volver
    // ==========================
    protected void drawBottomBackHintIfEnabled(float worldW, float worldH) {
        if (!useBottomBackHint()) return;

        String hint = I18n.t("ui_back_hint");

        float uiLeft = cam.position.x - worldW / 2f;
        float uiBottom = cam.position.y - worldH / 2f;

        float scale = 2.2f;
        fillFont.getData().setScale(scale);
        outlineFont.getData().setScale(scale);

        layout.setText(fillFont, hint);

        float x = uiLeft + (worldW - layout.width) / 2f;
        float y = uiBottom + BACK_HINT_Y;

        drawOutlined(hint, x, y, 3.0f);

        fillFont.getData().setScale(1f);
        outlineFont.getData().setScale(1f);
    }

    protected boolean handleBottomBackTap(float xHud, float yHud) {
        if (!useBottomBackHint()) return false;
        if (yHud <= BACK_HINT_ZONE_H) {
            onBack();
            return true;
        }
        return false;
    }

    // ==========================
    // Input por defecto
    // ==========================
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

                // primero: zona inferior para volver
                if (handleBottomBackTap(hud.x, hud.y)) return true;

                return onTouchDownHud(hud.x, hud.y);
            }
        });
    }

    // ==========================
    // Hooks
    // ==========================
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
