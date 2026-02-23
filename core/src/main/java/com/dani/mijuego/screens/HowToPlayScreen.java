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

    // Se ejecuta al entrar en la pantalla
    @Override
    public void show() {
        super.show();
        // Instala el input por defecto (gestión de atrás y toques)
        installDefaultInput();
    }

    // Render principal de la pantalla
    @Override
    public void render(float delta) {
        // Limpia la pantalla a negro
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        // Aplica el viewport y actualiza cámara
        viewport.apply(true);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        // Dimensiones del mundo virtual
        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        batch.begin();

        // Dibuja el fondo de menú si está habilitado
        drawMenuBackgroundIfEnabled(worldW, worldH);

        // Calcula el borde inferior visible de la cámara
        float uiBottom = cam.position.y - worldH / 2f;

        // Dibuja el título centrado (texto localizado)
        drawCenteredTitle(I18n.t("how_title"), uiBottom + worldH * 0.88f);

        // Dibuja el cuerpo del texto en varias líneas, centrado
        //  - Texto obtenido desde I18n
        //  - Posición vertical
        //  - Ancho máximo para hacer salto de línea
        //  - Escala y grosor de contorno definidos en BaseScreen
        drawCenteredMultiline(
            I18n.t("how_body"),
            uiBottom + worldH * 0.82f,
            55f,
            UI_SCALE,
            UI_OUTLINE_PX
        );

        // Dibuja el hint inferior de volver
        drawBottomBackHintIfEnabled(worldW, worldH);

        batch.end();
    }

    // Acción al pulsar atrás
    @Override
    protected void onBack() {
        // Si existe pantalla anterior, vuelve a ella
        if (backScreen != null) game.setScreen(backScreen);
            // Si no, vuelve al menú principal
        else game.setScreen(new MenuScreen(game));
    }
}
