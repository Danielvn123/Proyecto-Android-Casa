// =========================
// RecordsScreen (con BaseScreen nuevo)
// =========================
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

        // Título
        drawTitle(I18n.t("RÉCORDS"));

        // TOP 10
        drawTop10();

        // Hint inferior (BaseScreen ya aplica estilo)
        drawBottomBackHintIfEnabled(worldW, worldH);

        batch.end();
    }

    private void drawTitle(String text) {
        if (text == null) text = "";

        setTitleStyle();
        layout.setText(fillFont, text);

        float x = cam.position.x - layout.width / 2f;
        float y = cam.position.y + viewport.getWorldHeight() * 0.38f;

        drawOutlined(text, x, y, TITLE_OUTLINE_PX);
        resetFontScale();
    }

    private void drawTop10() {
        boolean en = (I18n.getLang() == I18n.Lang.EN);
        String topLabel = en ? "TOP 10 RUNS" : "TOP 10 PARTIDAS";

        // Cabecera encima del top
        String header = en ? "HEIGHT | COINS | TIME" : "ALTURA | MONEDAS | TIEMPO";

        Array<GameSave.Run> runs = GameSave.getRunsHistorySorted();

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        float centerX = cam.position.x;
        float centerY = cam.position.y;

        // Tamaño más grande
        float bigScale = 3.0f;
        fillFont.getData().setScale(bigScale);
        outlineFont.getData().setScale(bigScale);

        int show = (runs == null) ? 0 : Math.min(10, runs.size);
        String[] lines;

        if (show == 0) {
            lines = new String[] { en ? "No runs saved yet." : "Aun no hay partidas guardadas." };
        } else {
            lines = new String[show];
            for (int i = 0; i < show; i++) {
                GameSave.Run r = runs.get(i);
                lines[i] =
                    (i + 1) + ")   " +
                        r.heightMeters + "m   |   " +
                        r.coins + "   |   " +
                        formatTime(r.timeSec);
            }
        }

        // Calculamos el ancho máximo del bloque (título + cabecera + filas)
        float maxW = 0f;

        layout.setText(fillFont, topLabel);
        maxW = Math.max(maxW, layout.width);

        layout.setText(fillFont, header);
        maxW = Math.max(maxW, layout.width);

        for (String line : lines) {
            layout.setText(fillFont, line);
            maxW = Math.max(maxW, layout.width);
        }

        // Este es el X del bloque centrado REAL
        float leftX = centerX - maxW / 2f;

        // Colocación vertical
        float titleY = centerY + worldH * 0.25f;

        // Título TOP centrado dentro del bloque
        layout.setText(fillFont, topLabel);
        float titleX = leftX + (maxW - layout.width) / 2f;
        drawOutlined(topLabel, titleX, titleY, 4.0f);

        // Cabecera debajo del TOP
        float headerY = titleY - 90f;
        layout.setText(fillFont, header);
        float headerX = leftX + (maxW - layout.width) / 2f;
        drawOutlined(header, headerX, headerY, 4.0f);

        // Filas (empiezan debajo de la cabecera)
        float rowSpacing = 80f;
        float rowY = headerY - 110f;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            layout.setText(fillFont, line);

            float x = leftX + (maxW - layout.width) / 2f;
            float y = rowY - i * rowSpacing;

            drawOutlined(line, x, y, 4.0f);
        }

        resetFontScale();
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
