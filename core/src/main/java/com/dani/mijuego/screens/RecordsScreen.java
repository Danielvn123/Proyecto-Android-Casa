package com.dani.mijuego.screens;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.GameSave;
import com.dani.mijuego.game.I18n;

public class RecordsScreen extends BaseScreen {

    public RecordsScreen(Main game) {
        super(game, GameConfig.VW, GameConfig.VH);
    }

    @Override
    protected boolean useMenuBackground() { return true; }

    @Override
    protected boolean useBottomBackHint() { return true; }

    @Override
    public void show() {
        super.show();
        installDefaultInput();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        viewport.apply(true);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        batch.begin();

        drawMenuBackgroundIfEnabled(worldW, worldH);

        float uiBottom = cam.position.y - worldH / 2f;

        // FIX: antes hacías I18n.t("RÉCORDS") (literal). Ahora usamos key real:
        drawCenteredTitle(I18n.t("menu_records"), uiBottom + worldH * 0.88f);

        drawTop10(uiBottom, worldH);

        drawBottomBackHintIfEnabled(worldW, worldH);

        batch.end();
    }

    private void drawTop10(float uiBottom, float worldH) {
        boolean en = (I18n.getLang() == I18n.Lang.EN);

        String topLabel = en ? "TOP 10 RUNS" : "TOP 10 PARTIDAS";
        String header = en ? "HEIGHT | COINS | TIME" : "ALTURA | MONEDAS | TIEMPO";

        Array<GameSave.Run> runs = GameSave.getRunsHistorySorted();
        int show = (runs == null) ? 0 : Math.min(10, runs.size);

        StringBuilder sb = new StringBuilder();
        sb.append(topLabel).append("\n");
        sb.append(header).append("\n");

        if (show == 0) {
            sb.append(en ? "No runs saved yet." : "Aun no hay partidas guardadas.");
        } else {
            for (int i = 0; i < show; i++) {
                GameSave.Run r = runs.get(i);
                sb.append(i + 1).append(")   ")
                    .append(r.heightMeters).append("m   |   ")
                    .append(r.coins).append("   |   ")
                    .append(formatTime(r.timeSec));
                if (i < show - 1) sb.append("\n");
            }
        }

        // Un solo draw multiline para todo el bloque
        drawCenteredMultiline(
            sb.toString(),
            uiBottom + worldH * 0.72f,
            80f,
            3.0f,     // igual que tu "bigScale"
            4.0f
        );
    }

    private String formatTime(float sec) {
        int s = Math.max(0, (int) sec);
        int m = s / 60;
        int r = s % 60;
        return m + ":" + (r < 10 ? "0" + r : "" + r);
    }

    @Override
    protected void onBack() {
        game.setScreen(new MenuScreen(game));
    }
}
