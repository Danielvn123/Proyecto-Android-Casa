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

        // Título (MISMO estilo que OptionsScreen)
        drawTitle(I18n.t("RÉCORDS"));

        // Contenido
        drawRecordsContent();

        // Hint inferior desde BaseScreen
        drawBottomBackHintIfEnabled(worldW, worldH);

        batch.end();
    }

    // ==========================
    // MISMO drawTitle que OptionsScreen
    // ==========================
    private void drawTitle(String text) {
        if (text == null) text = "";

        float scale = 4.2F;
        fillFont.getData().setScale(scale);
        outlineFont.getData().setScale(scale);

        layout.setText(fillFont, text);

        float x = cam.position.x - layout.width / 2f;
        float y = cam.position.y + viewport.getWorldHeight() * 0.38f;

        drawOutlined(text, x, y, 4.5F);

        fillFont.getData().setScale(3f);
        outlineFont.getData().setScale(3f);
    }

    private void drawRecordsContent() {
        boolean en = (I18n.getLang() == I18n.Lang.EN);

        String bestLabel = en ? "BEST" : "MEJOR";
        String lastLabel = en ? "LAST RUN" : "ULTIMA PARTIDA";
        String topLabel  = en ? "TOP 10 RUNS" : "TOP 10 PARTIDAS";

        String heightLabel = en ? "HEIGHT" : "ALTURA";
        String coinsLabel  = en ? "COINS"  : "MONEDAS";
        String timeLabel   = en ? "TIME"   : "TIEMPO";

        int bestH = GameSave.getBestHeight();
        int bestC = GameSave.getBestCoins();
        float bestT = GameSave.getBestTime();

        int lastH = GameSave.getLastHeight();
        int lastC = GameSave.getLastCoins();
        float lastT = GameSave.getLastTime();

        Array<GameSave.Run> runs = GameSave.getRunsHistorySorted();

        // Posición como HUD (igual que OptionsScreen)
        float uiLeft = cam.position.x - viewport.getWorldWidth() / 2f;
        float uiBottom = cam.position.y - viewport.getWorldHeight() / 2f;

        // Colocación: debajo del título, centrado pero alineado a la izquierda en un bloque
        float startX = uiLeft + 110f;
        float startY = cam.position.y + viewport.getWorldHeight() * 0.25f;

        // Bloques grandes (mismo rollo que Options: escala alta)
        float blockScale = 2.15f;
        outlineFont.getData().setScale(blockScale);
        fillFont.getData().setScale(blockScale);

        String bestText =
            bestLabel + "\n" +
                heightLabel + ": " + bestH + " m\n" +
                coinsLabel  + ": " + bestC + "\n" +
                timeLabel   + ": " + formatTime(bestT);

        drawOutlined(bestText, startX, startY, 3.0f);

        float gap = 210f;

        String lastText =
            lastLabel + "\n" +
                heightLabel + ": " + lastH + " m\n" +
                coinsLabel  + ": " + lastC + "\n" +
                timeLabel   + ": " + formatTime(lastT);

        drawOutlined(lastText, startX, startY - gap, 3.0f);

        // TOP 10 título un pelín más grande
        float topTitleScale = 2.35f;
        outlineFont.getData().setScale(topTitleScale);
        fillFont.getData().setScale(topTitleScale);

        float topY = startY - gap - 240f;
        drawOutlined(topLabel, startX, topY, 3.2f);

        // Filas más compactas pero aún grandes
        float rowScale = 1.85f;
        outlineFont.getData().setScale(rowScale);
        fillFont.getData().setScale(rowScale);

        float rowY = topY - 70f;

        if (runs == null || runs.size == 0) {
            drawOutlined(en ? "No runs saved yet." : "Aun no hay partidas guardadas.", startX, rowY, 2.8f);
        } else {
            int show = Math.min(10, runs.size);

            for (int i = 0; i < show; i++) {
                GameSave.Run r = runs.get(i);

                // Línea corta y clara (se ve mucho más "guay" que 3 labels por línea)
                String row =
                    (i + 1) + ")   " +
                        r.heightMeters + "m   |   " +
                        r.coins + "   |   " +
                        formatTime(r.timeSec);

                drawOutlined(row, startX, rowY - i * 58f, 2.6f);
            }
        }

        // Reset
        outlineFont.getData().setScale(1f);
        fillFont.getData().setScale(1f);
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
