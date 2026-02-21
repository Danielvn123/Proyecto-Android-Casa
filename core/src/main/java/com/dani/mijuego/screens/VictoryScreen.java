package com.dani.mijuego.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameAudio;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.I18n;

public class VictoryScreen extends BaseScreen {

    private final int score;
    private final int coins;
    private final GameAudio audio;

    private Texture victoryTex;
    private Rectangle btnVolver;

    public VictoryScreen(Main game, int score, int coins, GameAudio audio) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.score = score;
        this.coins = coins;
        this.audio = audio;
    }

    @Override
    public void show() {
        victoryTex = game.assets.manager.get(Assets.VICTORY, Texture.class);

        float w = viewport.getWorldWidth();

        float bw = 520f;
        float bh = 140f;
        float bx = (w - bw) / 2f;
        float by = 90f;

        btnVolver = new Rectangle(bx, by, bw, bh);

        installDefaultInput();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        viewport.apply();
        batch.setProjectionMatrix(cam.combined);

        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        batch.begin();

        // Fondo
        if (victoryTex != null) {
            batch.draw(
                victoryTex,
                cam.position.x - w / 2f,
                cam.position.y - h / 2f,
                w,
                h
            );
        }

        // =====================
        // ALTURA Y MONEDAS ARRIBA
        // =====================
        float uiLeft   = cam.position.x - w / 2f;
        float uiTop    = cam.position.y + h / 2f;
        float uiBottom = cam.position.y - h / 2f;

        font.getData().setScale(4.5f);
        font.setColor(1f, 1f, 1f, 1f);


        String alturaTxt = I18n.t("go_height") + " " + score + " m";
        layout.setText(font, alturaTxt);
        font.draw(batch, layout, uiLeft + 340f, uiTop - 250f);

        String monedasTxt = I18n.t("go_coins") + " " + coins;
        layout.setText(font, monedasTxt);
        font.draw(batch, layout, uiLeft + 340f, uiTop - 380f);

        // =====================
        // BOTÓN DENTRO DEL CUADRADO
        // =====================
        String btnText = I18n.t("go_restart");
        layout.setText(font, btnText);

        float textX = uiLeft
            + btnVolver.x
            + (btnVolver.width - layout.width) / 2.5f;

        float textY = uiBottom
            + btnVolver.y
            + btnVolver.height * 0.95f;

        font.draw(batch, layout, textX, textY);

        font.getData().setScale(1f);

        batch.end();
    }

    @Override
    protected boolean onTouchDownHud(float xHud, float yHud) {
        if (btnVolver != null && btnVolver.contains(xHud, yHud)) {
            if (audio != null) audio.playSelectButton();
            game.setScreen(new MenuScreen(game));
            return true;
        }
        return false;
    }

    @Override
    protected void onBack() {
        if (audio != null) audio.playSelectButton();
        game.setScreen(new MenuScreen(game));
    }
}
