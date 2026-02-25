package com.dani.mijuego.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.I18n;

// Pantalla "Cómo jugar"
// Muestra instrucciones del juego y permite volver atrás
public class HowToPlayScreen extends BaseScreen {

    // Pantalla a la que se vuelve cuando se pulsa atrás
    private final Screen backScreen;

    // Constructor simple: si no se indica pantalla de retorno, volverá al menú
    public HowToPlayScreen(Main game) {
        this(game, null);
    }

    // Constructor principal: recibe el juego y opcionalmente una pantalla anterior
    public HowToPlayScreen(Main game, Screen backScreen) {
        // Inicializa BaseScreen con el tamaño virtual configurado
        super(game, GameConfig.VW, GameConfig.VH);
        // Guarda la pantalla a la que se volverá
        this.backScreen = backScreen;
    }

    // Indica que esta pantalla usa el fondo de menú
    @Override
    protected boolean useMenuBackground() { return true; }

    // Indica que debe mostrarse el texto inferior de "volver"
    @Override
    protected boolean useBottomBackHint() { return true; }

    // Se ejecuta al entrar en la pantalla.
    // Instala el sistema de entrada por defecto.
    @Override
    public void show() {
        super.show();
        installDefaultInput();
    }

    // Render principal de la pantalla.
    // Dibuja fondo, título, instrucciones y texto inferior para volver.
    @Override
    public void render(float delta) {

        // Limpia la pantalla
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        // Aplica viewport y actualiza cámara
        viewport.apply(true);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        // Dimensiones del mundo virtual
        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        batch.begin();

        // Dibuja el fondo de menú si está habilitado
        drawMenuBackgroundIfEnabled(worldW, worldH);

        // Calcula el borde inferior visible
        float uiBottom = cam.position.y - worldH / 2f;

        // Dibuja el título centrado usando el sistema de internacionalización
        drawCenteredTitle(I18n.t("how_title"), uiBottom + worldH * 0.88f);

        // Dibuja el cuerpo de instrucciones en varias líneas centradas
        drawCenteredMultiline(
            I18n.t("how_body"),
            uiBottom + worldH * 0.82f,
            55f,
            UI_SCALE,
            UI_OUTLINE_PX
        );

        // Dibuja el texto inferior de "tocar para volver"
        drawBottomBackHintIfEnabled(worldW, worldH);

        batch.end();
    }

    // Acción que se ejecuta al pulsar atrás.
    // Vuelve a la pantalla anterior si existe, o al menú principal.
    @Override
    protected void onBack() {
        if (backScreen != null) game.setScreen(backScreen);
        else game.setScreen(new MenuScreen(game));
    }
}
