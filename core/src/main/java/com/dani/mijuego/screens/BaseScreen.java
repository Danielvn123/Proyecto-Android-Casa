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

    protected BitmapFont font;         // legacy (algunas pantallas lo usaban)
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

    // ==========================
    // Estilo global texto
    // ==========================
    protected static final float TITLE_SCALE = 3.0f;
    protected static final float TITLE_OUTLINE_PX = 3.5f;

    protected static final float UI_SCALE = 2.4f;
    protected static final float UI_OUTLINE_PX = 3.0f;

    protected static final float BACK_HINT_SCALE = 2.2f;
    protected static final float BACK_HINT_OUTLINE_PX = 3.0f;

    protected BaseScreen(Main game, float vw, float vh) {
        this.game = game;

        batch = new SpriteBatch();
        cam = new OrthographicCamera();

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

        applyDefaultTextStyle();
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
    // Assets helper (Quita safeGetTex repetido)
    // ==========================
    protected Texture getTex(String assetPath) {
        try {
            return game.assets.manager.get(assetPath, Texture.class);
        } catch (Exception e) {
            Gdx.app.error("ASSETS", "No se pudo obtener textura: " + assetPath, e);
            return null;
        }
    }

    // ==========================
    // Estilo global: blanco/negro
    // ==========================
    protected void applyDefaultTextStyle() {
        if (batch != null) batch.setColor(1f, 1f, 1f, 1f);
        if (fillFont != null) fillFont.setColor(1f, 1f, 1f, 1f);
        if (outlineFont != null) outlineFont.setColor(0f, 0f, 0f, 1f);
    }

    protected void resetFontScale() {
        if (fillFont != null) fillFont.getData().setScale(1f);
        if (outlineFont != null) outlineFont.getData().setScale(1f);
        if (font != null) font.getData().setScale(1f);
    }

    protected void drawOutlined(String text, float x, float y, float outlinePx) {
        applyDefaultTextStyle();
        FontUtils.drawOutlined(batch, outlineFont, fillFont, text, x, y, outlinePx);
    }

    // ==========================
    // NUEVO: título centrado reutilizable
    // ==========================
    protected void drawCenteredTitle(String text, float yWorld) {
        if (text == null) text = "";
        applyDefaultTextStyle();

        fillFont.getData().setScale(TITLE_SCALE);
        outlineFont.getData().setScale(TITLE_SCALE);

        layout.setText(fillFont, text);

        float x = cam.position.x - layout.width / 2f;
        drawOutlined(text, x, yWorld, TITLE_OUTLINE_PX);

        resetFontScale();
    }

    // ==========================
    // NUEVO: multiline centrado reutilizable
    // ==========================
    protected void drawCenteredMultiline(String text,
                                         float startY,
                                         float lineSpacing,
                                         float scale,
                                         float outlinePx) {
        if (text == null) text = "";
        applyDefaultTextStyle();

        fillFont.getData().setScale(scale);
        outlineFont.getData().setScale(scale);

        String[] lines = text.split("\n");
        float y = startY;

        for (String line : lines) {
            if (line == null) line = "";
            layout.setText(fillFont, line);
            float x = cam.position.x - layout.width / 2f;
            drawOutlined(line, x, y, outlinePx);
            y -= lineSpacing;
        }

        resetFontScale();
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

        applyDefaultTextStyle();

        fillFont.getData().setScale(BACK_HINT_SCALE);
        outlineFont.getData().setScale(BACK_HINT_SCALE);

        layout.setText(fillFont, hint);

        float x = uiLeft + (worldW - layout.width) / 2f;
        float y = uiBottom + BACK_HINT_Y;

        drawOutlined(hint, x, y, BACK_HINT_OUTLINE_PX);

        resetFontScale();
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
    // Helpers (compat)
    // ==========================
    protected void setTitleStyle() {
        applyDefaultTextStyle();
        fillFont.getData().setScale(TITLE_SCALE);
        outlineFont.getData().setScale(TITLE_SCALE);
    }

    protected void setUiStyle() {
        applyDefaultTextStyle();
        fillFont.getData().setScale(UI_SCALE);
        outlineFont.getData().setScale(UI_SCALE);
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
