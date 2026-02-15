package com.dani.mijuego.screens;

import static com.badlogic.gdx.utils.Align.bottom;
import static com.badlogic.gdx.utils.Align.left;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.util.UiHit;
import com.dani.mijuego.game.I18n;


public class MenuScreen extends BaseScreen {

    private Texture fondo;
    private Texture b1, b2, b3, b4;

    private Rectangle r1, r2, r3, r4;

    private BitmapFont fillFont;
    private BitmapFont outlineFont;
    private GlyphLayout layout;

    private static final float BASE_SCALE = 1.00f;
    private static final float HOVER_SCALE = 1.07f;
    private static final float SCALE_SPEED = 12f;

    private float s1 = BASE_SCALE, s2 = BASE_SCALE, s3 = BASE_SCALE, s4 = BASE_SCALE;
    private Rectangle hovered = null;

    private final Vector3 tmp = new Vector3();

    private static final String CLICK_MUSIC_PATH = "audio/selectbutton.mp3";
    private Music clickMusic;
    private boolean soundOn = true;

    public MenuScreen(Main game) {
        super(game, GameConfig.VW, GameConfig.VH);
    }

    @Override
    public void show() {
        super.show();

        if (!game.assets.manager.isLoaded(Assets.FONDO_MENU, Texture.class)) {
            Gdx.app.error("MENU", "Assets del menú NO cargados. Volviendo a SplashScreen...");
            game.setScreen(new SplashScreen(game));
            return;
        }

        fondo = safeGetTex(Assets.FONDO_MENU);
        b1 = safeGetTex(Assets.BOTONMENU1);
        b2 = safeGetTex(Assets.BOTONMENU2);
        b3 = safeGetTex(Assets.BOTONMENU3);
        b4 = safeGetTex(Assets.BOTONMENU4);

        fillFont = new BitmapFont();
        outlineFont = new BitmapFont();
        layout = new GlyphLayout();

        if (Gdx.files.internal(CLICK_MUSIC_PATH).exists()) {
            clickMusic = Gdx.audio.newMusic(Gdx.files.internal(CLICK_MUSIC_PATH));
            clickMusic.setLooping(false);
            clickMusic.setVolume(1f);
        } else {
            Gdx.app.error("AUDIO", "NO EXISTE: " + CLICK_MUSIC_PATH);
        }

        layoutButtons();
        installDefaultInput();
    }

    private void playClick() {
        if (!soundOn || clickMusic == null) return;
        clickMusic.stop();
        clickMusic.play();
    }

    private Texture safeGetTex(String path) {
        try {
            return game.assets.manager.get(path, Texture.class);
        } catch (Exception e) {
            Gdx.app.error("MENU", "No se pudo obtener textura: " + path, e);
            return null;
        }
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
        float startY = h * 0.75f;

        r1 = new Rectangle(cx, startY, btnW, btnH);
        r2 = new Rectangle(cx, startY - (btnH + gap), btnW, btnH);
        r3 = new Rectangle(cx, startY - 2f * (btnH + gap), btnW, btnH);
        r4 = new Rectangle(cx, startY - 3f * (btnH + gap), btnW, btnH);
    }

    private void updateHoverFromScreen(int screenX, int screenY) {
        if (viewport == null || cam == null) return;

        tmp.set(screenX, screenY, 0);
        viewport.unproject(tmp);

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        float visibleLeft = cam.position.x - worldW / 2f;
        float visibleBottom = cam.position.y - worldH / 2f;

        float hudX = tmp.x - visibleLeft;
        float hudY = tmp.y - visibleBottom;

        hovered = null;
        if (r1 != null && r1.contains(hudX, hudY)) hovered = r1;
        else if (r2 != null && r2.contains(hudX, hudY)) hovered = r2;
        else if (r3 != null && r3.contains(hudX, hudY)) hovered = r3;
        else if (r4 != null && r4.contains(hudX, hudY)) hovered = r4;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        if (batch == null || cam == null || viewport == null) return;

        viewport.apply(true);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        updateHoverFromScreen(Gdx.input.getX(), Gdx.input.getY());

        float t = 1f - (float) Math.exp(-SCALE_SPEED * delta);
        s1 = MathUtils.lerp(s1, (hovered == r1) ? HOVER_SCALE : BASE_SCALE, t);
        s2 = MathUtils.lerp(s2, (hovered == r2) ? HOVER_SCALE : BASE_SCALE, t);
        s3 = MathUtils.lerp(s3, (hovered == r3) ? HOVER_SCALE : BASE_SCALE, t);
        s4 = MathUtils.lerp(s4, (hovered == r4) ? HOVER_SCALE : BASE_SCALE, t);

        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        batch.begin();
        batch.setColor(1f, 1f, 1f, 1f);

        if (fondo != null) {
            batch.draw(fondo, left, bottom, w, h);        }

        drawButtonScaled(b1, r1, s1);
        drawButtonScaled(b2, r2, s2);
        drawButtonScaled(b3, r3, s3);
        drawButtonScaled(b4, r4, s4);

        drawButtonText(I18n.t("menu_play"), r1, s1);
        drawButtonText(I18n.t("menu_records"), r2, s2);
        drawButtonText(I18n.t("menu_options"), r3, s3);
        drawButtonText(I18n.t("menu_credits"), r4, s4);

        batch.end();
    }

    private void drawButtonScaled(Texture t, Rectangle r, float scale) {
        if (t == null || r == null) return;

        float uiLeft = cam.position.x - viewport.getWorldWidth() / 2f;
        float uiBottom = cam.position.y - viewport.getWorldHeight() / 2f;

        float x = uiLeft + r.x;
        float y = uiBottom + r.y;

        float ox = r.width / 2f;
        float oy = r.height / 2f;

        batch.draw(
            t,
            x, y,
            ox, oy,
            r.width, r.height,
            scale, scale,
            0f,
            0, 0,
            t.getWidth(), t.getHeight(),
            false, false
        );
    }

    private void drawButtonText(String text, Rectangle r, float btnScale) {
        if (text == null || r == null || fillFont == null || outlineFont == null || layout == null) return;

        float uiLeft = cam.position.x - viewport.getWorldWidth() / 2f;
        float uiBottom = cam.position.y - viewport.getWorldHeight() / 2f;

        float scale = 3.5f * btnScale;
        fillFont.getData().setScale(scale);
        outlineFont.getData().setScale(scale);

        fillFont.setColor(1f, 1f, 1f, 1f);
        outlineFont.setColor(0f, 0f, 0f, 1f);

        layout.setText(fillFont, text);

        float cx = uiLeft + r.x + (r.width - layout.width) / 2f;
        float cy = uiBottom + r.y + (r.height + layout.height) / 2f;

        float o = 2.5f;
        outlineFont.draw(batch, text, cx - o, cy);
        outlineFont.draw(batch, text, cx + o, cy);
        outlineFont.draw(batch, text, cx, cy - o);
        outlineFont.draw(batch, text, cx, cy + o);
        outlineFont.draw(batch, text, cx - o, cy - o);
        outlineFont.draw(batch, text, cx + o, cy - o);
        outlineFont.draw(batch, text, cx - o, cy + o);
        outlineFont.draw(batch, text, cx + o, cy + o);

        fillFont.draw(batch, text, cx, cy);

        fillFont.getData().setScale(1f);
        outlineFont.getData().setScale(1f);
    }

    @Override
    protected boolean onTouchDownHud(float xHud, float yHud) {
        if (UiHit.hit(r1, xHud, yHud)) {
            playClick();
            game.setScreen(new ModeSelectScreen(game));
            return true;
        }
        if (UiHit.hit(r2, xHud, yHud)) {
            playClick();
            game.setScreen(new RecordsScreen(game));
            return true;
        }
        if (UiHit.hit(r3, xHud, yHud)) {
            playClick();
            game.setScreen(new OptionsScreen(game));
            return true;
        }
        if (UiHit.hit(r4, xHud, yHud)) {
            playClick();
            game.setScreen(new CreditsScreen(game));
            return true;
        }
        return false;
    }

    @Override
    protected void onBack() {
        Gdx.app.exit();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (fillFont != null) fillFont.dispose();
        if (outlineFont != null) outlineFont.dispose();
        if (clickMusic != null) clickMusic.dispose();
    }
}
