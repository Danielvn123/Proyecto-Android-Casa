package com.dani.mijuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.GameSave;
import com.dani.mijuego.game.I18n;
import com.dani.mijuego.util.UiHit;

public class OptionsScreen extends BaseScreen {

    private final Screen backScreen;

    private Rectangle rLang;
    private Rectangle hovered = null;

    private static final float BASE_SCALE = 1.00f;
    private static final float HOVER_SCALE = 1.07f;
    private static final float SCALE_SPEED = 12f;

    private float sLang = BASE_SCALE;

    private final Vector3 tmp = new Vector3();

    public OptionsScreen(Main game) {
        this(game, null);
    }

    public OptionsScreen(Main game, Screen backScreen) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.backScreen = backScreen;
    }

    @Override
    protected boolean useMenuBackground() { return true; }

    @Override
    protected boolean useBottomBackHint() { return true; }

    @Override
    public void show() {
        super.show();

        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        layoutUi();
        installDefaultInput();
    }

    @Override
    protected void onResize() {
        layoutUi();
    }

    private void layoutUi() {
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        float btnW = 720f;
        float btnH = 160f;

        float cx = (w - btnW) / 2f;
        float y = h * 0.62f;

        rLang = new Rectangle(cx, y, btnW, btnH);
    }

    private void updateHoverFromScreen(int screenX, int screenY) {
        tmp.set(screenX, screenY, 0);
        viewport.unproject(tmp);

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();
        float visibleLeft = cam.position.x - worldW / 2f;
        float visibleBottom = cam.position.y - worldH / 2f;

        float hudX = tmp.x - visibleLeft;
        float hudY = tmp.y - visibleBottom;

        hovered = null;
        if (rLang != null && rLang.contains(hudX, hudY)) hovered = rLang;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        viewport.apply(true);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        updateHoverFromScreen(Gdx.input.getX(), Gdx.input.getY());

        float t = 1f - (float) Math.exp(-SCALE_SPEED * delta);
        sLang = MathUtils.lerp(sLang, (hovered == rLang) ? HOVER_SCALE : BASE_SCALE, t);

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        batch.begin();

        drawMenuBackgroundIfEnabled(worldW, worldH);

        // Título
        drawTitle(I18n.t("menu_options"));

        // Botón idioma
        drawLangButton();

        // Hint inferior desde BaseScreen
        drawBottomBackHintIfEnabled(worldW, worldH);

        batch.end();
    }

    private void drawTitle(String text) {
        if (text == null) text = "";

        float scale = 3.0f;
        fillFont.getData().setScale(scale);
        outlineFont.getData().setScale(scale);

        layout.setText(fillFont, text);

        float x = cam.position.x - layout.width / 2f;
        float y = cam.position.y + viewport.getWorldHeight() * 0.38f;

        drawOutlined(text, x, y, 3.5f);

        fillFont.getData().setScale(1f);
        outlineFont.getData().setScale(1f);
    }

    private void drawLangButton() {
        if (rLang == null) return;

        String label = I18n.t("opt_language");
        String value = (I18n.getLang() == I18n.Lang.EN)
            ? I18n.t("opt_language_value_en")
            : I18n.t("opt_language_value_es");

        String text = label + ": " + value;

        float uiLeft = cam.position.x - viewport.getWorldWidth() / 2f;
        float uiBottom = cam.position.y - viewport.getWorldHeight() / 2f;

        float cx = uiLeft + rLang.x + rLang.width / 2f;
        float cy = uiBottom + rLang.y + rLang.height / 2f;

        float fontScale = 2.4f * sLang;
        outlineFont.getData().setScale(fontScale);
        fillFont.getData().setScale(fontScale);

        fillFont.setColor(1f, 1f, 1f, 1f);
        outlineFont.setColor(0f, 0f, 0f, 1f);

        layout.setText(fillFont, text);

        float tx = cx - layout.width / 2f;
        float ty = cy + layout.height / 2f;

        drawOutlined(text, tx, ty, 3.0f);

        outlineFont.getData().setScale(1f);
        fillFont.getData().setScale(1f);
    }

    @Override
    protected boolean onTouchDownHud(float xHud, float yHud) {
        // IMPORTANTE: el “toque abajo para volver” ya lo maneja BaseScreen ANTES de llegar aquí.

        if (UiHit.hit(rLang, xHud, yHud)) {
            I18n.toggle();
            GameSave.setLang(I18n.getLang() == I18n.Lang.EN ? "EN" : "ES");
            return true;
        }

        return false;
    }

    @Override
    protected void onBack() {
        if (backScreen != null) game.setScreen(backScreen);
        else game.setScreen(new MenuScreen(game));
    }
}
