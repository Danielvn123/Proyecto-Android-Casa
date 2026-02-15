package com.dani.mijuego.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.I18n;

public class GameOverScreen extends BaseScreen {

    private final int score;
    private final int coins;

    private Texture gameOverTex;
    private Rectangle btnReiniciar;

    public GameOverScreen(Main game, int score, int coins) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.score = score;
        this.coins = coins;
    }

    @Override
    public void show() {
        gameOverTex = game.assets.manager.get(Assets.GAMEOVER, Texture.class);

        float w = viewport.getWorldWidth();

        float bw = 520f;
        float bh = 140f;
        float bx = (w - bw) / 2f;
        float by = 90f;

        btnReiniciar = new Rectangle(bx, by, bw, bh);

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

        if (gameOverTex != null) {
            batch.draw(
                gameOverTex,
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

        // ============
        // "REINICIAR" / "RESTART"
        // ============
        String btnText = I18n.t("go_restart");
        layout.setText(font, btnText);

        float textX = uiLeft
            + btnReiniciar.x
            + (btnReiniciar.width - layout.width) / 2.5f;

        float textY = uiBottom
            + btnReiniciar.y
            + btnReiniciar.height * 0.95f;

        font.draw(batch, layout, textX, textY);

        font.getData().setScale(1f);

        batch.end();
    }

    @Override
    protected boolean onTouchDownHud(float xHud, float yHud) {
        if (btnReiniciar != null && btnReiniciar.contains(xHud, yHud)) {
            game.setScreen(new MenuScreen(game));
            return true;
        }
        return false;
    }

    @Override
    protected void onBack() {
        // si quieres: volver al menú también con back
        // game.setScreen(new MenuScreen(game));
    }
}
