package com.dani.mijuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameAudio;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.util.UiHit;
import com.dani.mijuego.game.I18n;

public class PauseScreen extends BaseScreen {

    private Texture fondo, b1, b3, b4;     // <- b2 fuera
    private Rectangle r1, r3, r4;          // <- r2 fuera

    private BitmapFont fillFont;
    private BitmapFont outlineFont;
    private GlyphLayout layout;

    private final GameScreen gameScreen;
    private final GameAudio audio;

    private static final float BASE_SCALE = 1.00f;
    private static final float HOVER_SCALE = 1.08f;
    private static final float SCALE_SPEED = 12f;

    private float s1 = BASE_SCALE, s3 = BASE_SCALE, s4 = BASE_SCALE; // <- s2 fuera

    private Rectangle hoveredBtn;
    private final Vector3 tmp = new Vector3();

    public PauseScreen(Main game, GameScreen gameScreen, GameAudio audio) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.gameScreen = gameScreen;
        this.audio = audio;
    }

    @Override
    public void show() {
        super.show();

        fondo = safeGetTex(Assets.FONDO_MENU);
        b1 = safeGetTex(Assets.BOTONMENU1);
        b3 = safeGetTex(Assets.BOTONMENU3);
        b4 = safeGetTex(Assets.BOTONMENU4);

        fillFont = new BitmapFont();
        outlineFont = new BitmapFont();
        layout = new GlyphLayout();

        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        layoutButtons();
        installDefaultInput();
    }

    @Override
    protected void onResize() {
        layoutButtons();
    }

    private void layoutButtons() {
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        float btnW = 750f;
        float btnH = 250f;
        float gap = 150f;

        float cx = (w - btnW) / 2f;

        // Para 3 botones, los centramos verticalmente:
        float totalH = 3f * btnH + 2f * gap;
        float startY = (h + totalH) / 2f - btnH; // y del primer botón

        r1 = new Rectangle(cx, startY, btnW, btnH);                           // continuar
        r3 = new Rectangle(cx, startY - (btnH + gap), btnW, btnH);            // reiniciar
        r4 = new Rectangle(cx, startY - 2f * (btnH + gap), btnW, btnH);       // salir
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        viewport.apply(true);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        updateHover();

        float t = 1f - (float) Math.exp(-SCALE_SPEED * delta);
        s1 = MathUtils.lerp(s1, (hoveredBtn == r1) ? HOVER_SCALE : BASE_SCALE, t);
        s3 = MathUtils.lerp(s3, (hoveredBtn == r3) ? HOVER_SCALE : BASE_SCALE, t);
        s4 = MathUtils.lerp(s4, (hoveredBtn == r4) ? HOVER_SCALE : BASE_SCALE, t);

        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();
        float left = cam.position.x - w / 2f;
        float bottom = cam.position.y - h / 2f;

        batch.begin();

        if (fondo != null) {
            batch.setColor(1f, 1f, 1f, 1f);
            batch.draw(fondo, left, bottom, w, h);
        }

        drawButtonScaled(b1, r1, s1);
        drawButtonScaled(b3, r3, s3);
        drawButtonScaled(b4, r4, s4);

        drawCenteredOutlined(I18n.t("pause_continue"), r1, s1 * 3.5f);
        drawCenteredOutlined(I18n.t("pause_restart"),  r3, s3 * 3.5f);
        drawCenteredOutlined(I18n.t("pause_exit"),     r4, s4 * 3.5f);

        batch.end();
    }

    private void updateHover() {
        int sx = Gdx.input.getX();
        int sy = Gdx.input.getY();
        if (Gdx.input.isTouched()) {
            sx = Gdx.input.getX(0);
            sy = Gdx.input.getY(0);
        }

        tmp.set(sx, sy, 0);
        viewport.unproject(tmp);

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();
        float visibleLeft = cam.position.x - worldW / 2f;
        float visibleBottom = cam.position.y - worldH / 2f;

        float hudX = tmp.x - visibleLeft;
        float hudY = tmp.y - visibleBottom;

        hoveredBtn = null;
        if (r1 != null && r1.contains(hudX, hudY)) hoveredBtn = r1;
        else if (r3 != null && r3.contains(hudX, hudY)) hoveredBtn = r3;
        else if (r4 != null && r4.contains(hudX, hudY)) hoveredBtn = r4;
    }

    private void drawButtonScaled(Texture t, Rectangle r, float scale) {
        if (t == null || r == null) return;

        float uiLeft = cam.position.x - viewport.getWorldWidth() / 2f;
        float uiBottom = cam.position.y - viewport.getWorldHeight() / 2f;

        float cx = uiLeft + r.x + r.width / 2f;
        float cy = uiBottom + r.y + r.height / 2f;

        float dw = r.width * scale;
        float dh = r.height * scale;

        batch.draw(t, cx - dw / 2f, cy - dh / 2f, dw, dh);
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

    private void drawCenteredOutlined(String text, Rectangle r, float fontScale) {
        if (r == null) return;

        outlineFont.getData().setScale(fontScale);
        fillFont.getData().setScale(fontScale);

        fillFont.setColor(1f, 1f, 1f, 1f);
        outlineFont.setColor(0f, 0f, 0f, 1f);

        layout.setText(fillFont, text);

        float uiLeft = cam.position.x - viewport.getWorldWidth() / 2f;
        float uiBottom = cam.position.y - viewport.getWorldHeight() / 2f;

        float tx = uiLeft + r.x + (r.width - layout.width) / 2f;
        float ty = uiBottom + r.y + (r.height + layout.height) / 2f;

        drawOutlinedText(text, tx, ty, 3.0f);

        outlineFont.getData().setScale(1f);
        fillFont.getData().setScale(1f);
    }

    @Override
    protected boolean onTouchDownHud(float xHud, float yHud) {
        if (UiHit.hit(r1, xHud, yHud)) {
            if (audio != null) audio.playSelectButton();
            game.setScreen(gameScreen);
            return true;
        }
        if (UiHit.hit(r3, xHud, yHud)) {
            if (audio != null) audio.playSelectButton();
            game.setScreen(new GameScreen(game));
            return true;
        }
        if (UiHit.hit(r4, xHud, yHud)) {
            if (audio != null) audio.playSelectButton();
            game.setScreen(new MenuScreen(game));
            return true;
        }
        return false;
    }

    @Override
    protected void onBack() {
        if (audio != null) audio.playSelectButton();
        game.setScreen(gameScreen);
    }

    private Texture safeGetTex(String path) {
        try {
            return game.assets.manager.get(path, Texture.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (fillFont != null) fillFont.dispose();
        if (outlineFont != null) outlineFont.dispose();
    }
}
