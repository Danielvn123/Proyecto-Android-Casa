package com.dani.mijuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.GameSave;
import com.dani.mijuego.game.I18n;
import com.dani.mijuego.util.UiHit;

public class OptionsScreen extends BaseScreen {

    private final Screen backScreen;

    private Rectangle rLang, rMusic, rVibra, rClearRecords;
    private Rectangle hovered = null;

    private Texture texOn;
    private Texture texOff;
    private Texture texMenuBtn; // BOTONMENU1

    private BitmapFont fillFont;
    private BitmapFont outlineFont;
    private GlyphLayout layout;

    private static final float BASE_SCALE = 1.00f;
    private static final float HOVER_SCALE = 1.07f;
    private static final float SCALE_SPEED = 12f;

    private float sLang = BASE_SCALE;
    private float sMusic = BASE_SCALE;
    private float sVibra = BASE_SCALE;
    private float sClear = BASE_SCALE;

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

        texOn  = game.assets.manager.get(Assets.BTN_ON, Texture.class);
        texOff = game.assets.manager.get(Assets.BTN_OFF, Texture.class);
        texMenuBtn = game.assets.manager.get(Assets.BOTONMENU1, Texture.class);

        fillFont = new BitmapFont();
        outlineFont = new BitmapFont();
        layout = new GlyphLayout();

        // (como lo tenías) aplica el mute guardado al audio global
        if (game != null && game.audio != null) {
            boolean on = GameSave.isMusicOn();
            game.audio.setEnabled(on);
            if (!on) game.audio.stopAllMusic();
        }

        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        layoutUi();
        installDefaultInput();
    }

    // ✅ sonido click (selectbutton) SOLO añadimos esto y lo llamamos en los 4 botones
    private void playUiClick() {
        if (game != null && game.audio != null) {
            game.audio.playSelectButton();
        }
    }

    private void layoutUi() {
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        float btnW = 720f;
        float btnH = 150f;
        float gap = 40f;

        float x = (w - btnW) / 2f;
        float startY = h * 0.70f;

        rLang  = new Rectangle(x, startY, btnW, btnH);
        rMusic = new Rectangle(x, startY - (btnH + gap), btnW, btnH);
        rVibra = new Rectangle(x, startY - (btnH + gap) * 2f, btnW, btnH);
        rClearRecords = new Rectangle(x, startY - (btnH + gap) * 3f, btnW, btnH);
    }

    private void updateHover(int screenX, int screenY) {
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
        else if (rMusic != null && rMusic.contains(hudX, hudY)) hovered = rMusic;
        else if (rVibra != null && rVibra.contains(hudX, hudY)) hovered = rVibra;
        else if (rClearRecords != null && rClearRecords.contains(hudX, hudY)) hovered = rClearRecords;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        viewport.apply(true);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        updateHover(Gdx.input.getX(), Gdx.input.getY());

        float t = 1f - (float) Math.exp(-SCALE_SPEED * delta);
        sLang  = MathUtils.lerp(sLang,  hovered == rLang  ? HOVER_SCALE : BASE_SCALE, t);
        sMusic = MathUtils.lerp(sMusic, hovered == rMusic ? HOVER_SCALE : BASE_SCALE, t);
        sVibra = MathUtils.lerp(sVibra, hovered == rVibra ? HOVER_SCALE : BASE_SCALE, t);
        sClear = MathUtils.lerp(sClear, hovered == rClearRecords ? HOVER_SCALE : BASE_SCALE, t);

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        batch.begin();

        // Esto lo sigues usando de BaseScreen
        drawMenuBackgroundIfEnabled(worldW, worldH);

        // ✅ Estos 3 ya NO dan error porque están aquí abajo
        drawTitle(I18n.t("menu_options"));

        drawToggle(
            rLang,
            I18n.getLang() == I18n.Lang.EN ? "LANGUAGE" : "IDIOMA",
            I18n.getLang() == I18n.Lang.EN,
            sLang,
            "EN", "ES"
        );

        drawToggle(
            rMusic,
            I18n.getLang() == I18n.Lang.EN ? "MUSIC" : "MUSICA",
            GameSave.isMusicOn(),
            sMusic,
            "ON", "OFF"
        );

        drawToggle(
            rVibra,
            I18n.getLang() == I18n.Lang.EN ? "VIBRATION" : "VIBRACION",
            GameSave.isVibrationOn(),
            sVibra,
            "ON", "OFF"
        );

        drawActionButton(
            rClearRecords,
            I18n.getLang() == I18n.Lang.EN ? "CLEAR RECORDS" : "BORRAR RECORDS",
            sClear
        );

        drawBottomBackHintIfEnabled(worldW, worldH);

        batch.end();
    }

    @Override
    protected boolean onTouchDownHud(float xHud, float yHud) {

        // ✅ IDIOMA (suena + cambia idioma)
        if (UiHit.hit(rLang, xHud, yHud)) {
            playUiClick();
            I18n.toggle();
            GameSave.setLang(I18n.getLang() == I18n.Lang.EN ? "EN" : "ES");
            return true;
        }

        // ✅ MUSIC (suena + hace lo mismo que tenías)
        if (UiHit.hit(rMusic, xHud, yHud)) {
            playUiClick();

            boolean newOn = !GameSave.isMusicOn();
            GameSave.setMusicOn(newOn);

            if (game != null && game.audio != null) {
                game.audio.setEnabled(newOn);
                if (!newOn) {
                    game.audio.stopAllMusic();
                }
            }
            return true;
        }

        // ✅ VIBRACIÓN (suena + toggle vibración)
        if (UiHit.hit(rVibra, xHud, yHud)) {
            playUiClick();
            GameSave.setVibrationOn(!GameSave.isVibrationOn());
            return true;
        }

        // ✅ BORRAR RECORDS (suena + borra records)
        if (UiHit.hit(rClearRecords, xHud, yHud)) {
            playUiClick();
            GameSave.clearRunsHistory();
            return true;
        }

        return false;
    }

    @Override
    protected void onBack() {
        playUiClick();
        if (backScreen != null) game.setScreen(backScreen);
        else game.setScreen(new MenuScreen(game));
    }

    // =========================================================
    // ✅ LOS "draw" QUE TE DABAN FALLO, AHORA ESTÁN AQUÍ
    // =========================================================

    private void drawTitle(String text) {
        if (text == null) return;

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        float uiLeft = cam.position.x - worldW / 2f;
        float uiBottom = cam.position.y - worldH / 2f;

        float scale = 3.2f;
        fillFont.getData().setScale(scale);
        outlineFont.getData().setScale(scale);

        fillFont.setColor(1f, 1f, 1f, 1f);
        outlineFont.setColor(0f, 0f, 0f, 1f);

        layout.setText(fillFont, text);
        float x = uiLeft + (worldW - layout.width) / 2f;
        float y = uiBottom + worldH * 0.90f;

        drawOutlinedText(text, x, y, 3.0f);

        fillFont.getData().setScale(1f);
        outlineFont.getData().setScale(1f);
    }

    private void drawToggle(Rectangle r, String label, boolean isOn, float scaleMul, String leftText, String rightText) {
        if (r == null) return;

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();
        float uiLeft = cam.position.x - worldW / 2f;
        float uiBottom = cam.position.y - worldH / 2f;

        float centerY = uiBottom + r.y + r.height / 2f;
        float leftX = uiLeft + r.x;

        // Label
        float labelScale = 2.2f * scaleMul;
        fillFont.getData().setScale(labelScale);
        outlineFont.getData().setScale(labelScale);
        fillFont.setColor(1f, 1f, 1f, 1f);
        outlineFont.setColor(0f, 0f, 0f, 1f);

        layout.setText(fillFont, label);
        drawOutlinedText(label, leftX, centerY + layout.height / 2f, 2.5f);

        // ON/OFF image
        Texture tex = isOn ? texOn : texOff;

        float btnW = 450f * scaleMul;
        float btnH = 150f * scaleMul;
        float btnX = uiLeft + r.x + r.width - btnW;
        float btnY = centerY - btnH / 2f;

        if (tex != null) batch.draw(tex, btnX, btnY, btnW, btnH);

        // ON / OFF text inside toggle
        float smallScale = 1.8f * scaleMul;
        fillFont.getData().setScale(smallScale);
        outlineFont.getData().setScale(smallScale);

        layout.setText(fillFont, leftText);
        float leftTx = btnX + btnW * 0.25f - layout.width / 2f;
        drawOutlinedText(leftText, leftTx, centerY + layout.height / 2f, 2.0f);

        layout.setText(fillFont, rightText);
        float rightTx = btnX + btnW * 0.75f - layout.width / 2f;
        drawOutlinedText(rightText, rightTx, centerY + layout.height / 2f, 2.0f);

        fillFont.getData().setScale(1f);
        outlineFont.getData().setScale(1f);
    }

    private void drawActionButton(Rectangle r, String label, float scaleMul) {
        if (r == null) return;

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();
        float uiLeft = cam.position.x - worldW / 2f;
        float uiBottom = cam.position.y - worldH / 2f;

        float centerY = uiBottom + r.y + r.height / 2f;

        float btnW = r.width * scaleMul;
        float btnH = r.height * scaleMul;
        float btnX = uiLeft + r.x + (r.width - btnW) / 2f;
        float btnY = centerY - btnH / 2f;

        if (texMenuBtn != null) batch.draw(texMenuBtn, btnX, btnY, btnW, btnH);

        float fontScale = 2.2f * scaleMul;
        fillFont.getData().setScale(fontScale);
        outlineFont.getData().setScale(fontScale);

        fillFont.setColor(1f, 1f, 1f, 1f);
        outlineFont.setColor(0f, 0f, 0f, 1f);

        layout.setText(fillFont, label);
        float tx = btnX + btnW / 2f - layout.width / 2f;
        float ty = centerY + layout.height / 2f;

        drawOutlinedText(label, tx, ty, 2.5f);

        fillFont.getData().setScale(1f);
        outlineFont.getData().setScale(1f);
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
    public void dispose() {
        super.dispose();
        if (fillFont != null) fillFont.dispose();
        if (outlineFont != null) outlineFont.dispose();
    }
}
