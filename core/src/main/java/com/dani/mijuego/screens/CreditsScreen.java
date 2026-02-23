package com.dani.mijuego.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.I18n;

// Pantalla de créditos del juego.
// Muestra un título y un texto con los créditos, y permite volver atrás al menú o a la pantalla anterior
public class CreditsScreen extends BaseScreen {

    // Pantalla a la que se vuelve al pulsar "atrás"
    private final Screen backScreen;

    // Constructor cómodo: si no se pasa backScreen, se asume null
    public CreditsScreen(Main game) {
        this(game, null);
    }

    // Constructor principal: recibe el juego y opcionalmente una pantalla de retorno.
    public CreditsScreen(Main game, Screen backScreen) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.backScreen = backScreen;
    }

    // Indica a BaseScreen que en esta pantalla se debe dibujar el fondo de menú.
    @Override
    protected boolean useMenuBackground() { return true; }

    // Indica a BaseScreen que debe mostrar el hint de "volver" en la parte inferior.
    @Override
    protected boolean useBottomBackHint() { return true; }

    // Se ejecuta al entrar en la pantalla: prepara lo común y activa el input por defecto (atrás/toques)
    @Override
    public void show() {
        super.show();
        installDefaultInput();
    }

    // Render principal: limpia pantalla, aplica viewport/cámara y dibuja UI (fondo, textos, hint)
    @Override
    public void render(float delta) {
        // Limpia el frame anterior (negro, opaco).
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        // Aplica el viewport para que el sistema de coordenadas sea consistente en cualquier resolución
        viewport.apply(true);

        // Actualiza la cámara y asigna su matriz al SpriteBatch
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        // Dimensiones del mundo (no píxeles): útiles para colocar elementos proporcionalmente
        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        batch.begin();

        // Dibuja el fondo de menú si esta pantalla lo tiene habilitado
        drawMenuBackgroundIfEnabled(worldW, worldH);

        // Coordenada Y del borde inferior visible de la cámara (para posicionar UI por porcentajes)
        float uiBottom = cam.position.y - worldH / 2f;

        // Dibuja el título centrado (texto localizado)
        drawCenteredTitle(I18n.t("credits_title"), uiBottom + worldH * 0.88f);

        // Dibuja el cuerpo de créditos centrado y en varias líneas (texto localizado)
        drawCenteredMultiline(
            I18n.t("credits_body"),
            uiBottom + worldH * 0.70f,
            90f,
            UI_SCALE,
            UI_OUTLINE_PX
        );

        // Dibuja el hint inferior de "volver" si está habilitado
        drawBottomBackHintIfEnabled(worldW, worldH);

        batch.end();
    }

    // Acción al pulsar "atrás": vuelve a la pantalla anterior si existe; si no, vuelve al menú
    @Override
    protected void onBack() {
        if (backScreen != null) game.setScreen(backScreen);
        else game.setScreen(new MenuScreen(game));
    }
}
