package com.dani.mijuego.screens;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.GameSave;
import com.dani.mijuego.game.I18n;

public class RecordsScreen extends BaseScreen {

    // Constructor de la pantalla de récords (Top 10)
    public RecordsScreen(Main game) {
        super(game, GameConfig.VW, GameConfig.VH);
    }

    // Indica que esta pantalla usa el fondo tipo menú
    @Override
    protected boolean useMenuBackground() { return true; }

    // Indica que debe mostrarse el texto inferior para volver atrás
    @Override
    protected boolean useBottomBackHint() { return true; }

    // Se ejecuta al entrar en la pantalla: instala el input por defecto
    @Override
    public void show() {
        super.show();
        installDefaultInput();
    }

    // Render principal: dibuja fondo, título, lista Top 10 y hint inferior
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        viewport.apply(true);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        batch.begin();

        // Dibuja el fondo de menú
        drawMenuBackgroundIfEnabled(worldW, worldH);

        float uiBottom = cam.position.y - worldH / 2f;

        // Dibuja el título centrado usando el sistema I18n
        drawCenteredTitle(I18n.t("menu_records"), uiBottom + worldH * 0.88f);

        // Dibuja el bloque Top 10 con altura, monedas y tiempo
        drawTop10(uiBottom, worldH);

        // Dibuja el hint inferior de volver
        drawBottomBackHintIfEnabled(worldW, worldH);

        batch.end();
    }

    // Construye y dibuja el texto del Top 10 (según idioma) usando los datos guardados en GameSave
    private void drawTop10(float uiBottom, float worldH) {
        boolean en = (I18n.getLang() == I18n.Lang.EN);

        String topLabel = en ? "TOP 10 RUNS" : "TOP 10 PARTIDAS";
        String header = en ? "HEIGHT | COINS | TIME" : "ALTURA | MONEDAS | TIEMPO";

        // Obtiene las partidas guardadas ordenadas (mejores primero) y limita a 10
        Array<GameSave.Run> runs = GameSave.getRunsHistorySorted();
        int show = (runs == null) ? 0 : Math.min(10, runs.size);

        // Monta todo el texto en un único String para dibujarlo como bloque centrado
        StringBuilder sb = new StringBuilder();
        sb.append(topLabel).append("\n");
        sb.append(header).append("\n");

        // Si no hay partidas guardadas, muestra un mensaje
        if (show == 0) {
            sb.append(en ? "No runs saved yet." : "Aun no hay partidas guardadas.");
        } else {
            // Recorre las partidas y construye las filas con formato: posición, altura, monedas y tiempo
            for (int i = 0; i < show; i++) {
                GameSave.Run r = runs.get(i);
                sb.append(i + 1).append(")   ")
                    .append(r.heightMeters).append("m   |   ")
                    .append(r.coins).append("   |   ")
                    .append(formatTime(r.timeSec));
                if (i < show - 1) sb.append("\n");
            }
        }

        // Dibuja todo el bloque de texto con un solo draw multilínea centrado
        drawCenteredMultiline(
            sb.toString(),
            uiBottom + worldH * 0.72f,
            80f,
            3.0f,
            4.0f
        );
    }

    // Convierte segundos a formato mm:ss (por ejemplo 2:05)
    private String formatTime(float sec) {
        int s = Math.max(0, (int) sec);
        int m = s / 60;
        int r = s % 60;
        return m + ":" + (r < 10 ? "0" + r : "" + r);
    }

    // Acción al pulsar atrás: vuelve al menú principal
    @Override
    protected void onBack() {
        game.setScreen(new MenuScreen(game));
    }
}
