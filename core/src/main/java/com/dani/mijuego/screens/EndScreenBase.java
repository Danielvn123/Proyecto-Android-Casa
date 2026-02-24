package com.dani.mijuego.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.I18n;

public abstract class EndScreenBase extends BaseScreen {

    protected final int score;
    protected final int coins;
    protected Texture bgTex;
    protected Rectangle btn;

    private static final float BTN_W = 520f;
    private static final float BTN_H = 140f;
    private static final float BTN_Y = 90f;

    protected EndScreenBase(Main game, int score, int coins) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.score = score;
        this.coins = coins;
    }

    protected abstract Texture resolveBackgroundTexture();
    protected abstract String buttonTextKey();
    protected abstract void onButtonPressed();

    protected void onEnter() {
    }

    @Override
    public void show() {
        super.show();
        bgTex = resolveBackgroundTexture();

        float w = viewport.getWorldWidth();
        btn = new Rectangle((w - BTN_W) / 2f, BTN_Y, BTN_W, BTN_H);

        onEnter();
        installDefaultInput();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        viewport.apply();
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        float uiLeft = cam.position.x - w / 2f;
        float uiTop = cam.position.y + h / 2f;
        float uiBottom = cam.position.y - h / 2f;

        batch.begin();

        if (bgTex != null) {
            batch.draw(bgTex, uiLeft, uiBottom, w, h);
        }

        font.getData().setScale(4.5f);
        font.setColor(1f, 1f, 1f, 1f);

        String alturaTxt = I18n.t("go_height") + " " + score + " m";
        layout.setText(font, alturaTxt);
        font.draw(batch, layout, uiLeft + 340f, uiTop - 250f);

        String monedasTxt = I18n.t("go_coins") + " " + coins;
        layout.setText(font, monedasTxt);
        font.draw(batch, layout, uiLeft + 340f, uiTop - 380f);

        String btnText = I18n.t(buttonTextKey());
        layout.setText(font, btnText);

        float textX = uiLeft + btn.x + (btn.width - layout.width) / 2f;
        float textY = uiBottom + btn.y + btn.height * 0.78f;

        font.draw(batch, layout, textX, textY);

        font.getData().setScale(1f);

        batch.end();
    }

    @Override
    protected boolean onTouchDownHud(float xHud, float yHud) {
        if (btn != null && btn.contains(xHud, yHud)) {
            onButtonPressed();
            return true;
        }
        return false;
    }
}
