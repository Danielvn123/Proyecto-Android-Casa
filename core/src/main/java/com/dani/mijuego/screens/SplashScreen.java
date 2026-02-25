package com.dani.mijuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameAudio;
import com.dani.mijuego.game.I18n;
import com.dani.mijuego.util.FontUtils;

public class SplashScreen extends BaseScreen {

    // Textura del fondo de la pantalla de inicio (splash)
    private Texture fondo;

    // Tiempo acumulado para animaciones (parpadeo/escala del texto)
    private float animTime = 0f;

    // Tamaño virtual específico de esta pantalla
    private static final float VW = 720f;
    private static final float VH = 1280f;

    // Evita cargar dos veces assets del menú
    private boolean queuedMenu = false;

    // Indica si ya se han cargado los assets mínimos para permitir continuar
    private boolean canContinue = false;

    // Audio específico usado en la pantalla splash (música de fondo)
    private final GameAudio audio = new GameAudio();
    private boolean audioStarted = false;

    // Constructor: inicializa BaseScreen con el tamaño virtual del splash
    public SplashScreen(Main game) {
        super(game, VW, VH);
    }

    // Se ejecuta al entrar en la pantalla:
    // carga el fondo, deja en cola assets del menú, arranca música y configura input
    @Override
    public void show() {

        super.show();

        // Configura colores iniciales de fuentes (luego se modifican en render para el mensaje)
        fillFont.setColor(1f, 1f, 1f, 1f);
        outlineFont.setColor(0f, 0f, 1f, 1f);

        // Carga el fondo del splash (bloqueante para asegurar que esté listo)
        if (!game.assets.manager.isLoaded(Assets.FONDO_TRABAJO, Texture.class)) {
            game.assets.manager.load(Assets.FONDO_TRABAJO, Texture.class);
            game.assets.manager.finishLoadingAsset(Assets.FONDO_TRABAJO);
        }
        fondo = game.assets.manager.get(Assets.FONDO_TRABAJO, Texture.class);

        // Deja “pre-cargados” en cola los assets del menú para que al cambiar de pantalla vaya fluido
        if (!queuedMenu) {
            queuedMenu = true;
            if (!game.assets.manager.isLoaded(Assets.FONDO_MENU, Texture.class)) {
                game.assets.manager.load(Assets.FONDO_MENU, Texture.class);
            }
            if (!game.assets.manager.isLoaded(Assets.BOTONMENU, Texture.class)) {
                game.assets.manager.load(Assets.BOTONMENU, Texture.class);
            }
        }

        // Inicia la música del splash una sola vez
        if (!audioStarted) {
            audioStarted = true;
            audio.load();
            audio.playFondo();
        }

        // Input personalizado del splash:
        // permite pasar al menú tocando la pantalla o pulsando SPACE/ENTER
        Gdx.input.setInputProcessor(new InputAdapter() {

            // Cambia al menú solo si ya han terminado de cargarse los assets necesarios
            private void goMenuIfReady() {
                if (!canContinue) return;
                audio.stopFondo();
                game.setScreen(new MenuScreen(game));
            }

            @Override
            public boolean touchDown(int x, int y, int p, int b) {
                goMenuIfReady();
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.SPACE || keycode == Input.Keys.ENTER) {
                    goMenuIfReady();
                    return true;
                }
                return false;
            }
        });
    }

    // Render principal:
    // actualiza carga de assets, dibuja fondo y muestra el texto animado "toca para empezar"
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        // Actualiza el AssetManager poco a poco hasta que termine (cuando termina, se puede continuar)
        if (!canContinue) {
            canContinue = game.assets.manager.update();
        }

        viewport.apply();
        batch.setProjectionMatrix(cam.combined);

        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        float left = cam.position.x - w / 2f;
        float bottom = cam.position.y - h / 2f;

        batch.begin();

        // Dibuja el fondo del splash ocupando toda la pantalla virtual
        if (fondo != null) {
            batch.draw(fondo, left, bottom, w, h);
        }

        // Actualiza tiempo de animación
        animTime += delta;

        // Mensaje internacionalizado mostrado en splash
        String msg = I18n.t("splash_tap");

        // Animación del texto: alpha y escala oscilan con sin() para efecto de “parpadeo”
        float alpha = 0.65f + 0.35f * MathUtils.sin(animTime * 4f);
        float scale = 2.6f + 0.10f * MathUtils.sin(animTime * 4f);

        outlineFont.getData().setScale(scale);
        fillFont.getData().setScale(scale);

        // Color lila animado para el texto “toca para empezar” con contorno oscuro
        fillFont.setColor(0.75f, 0.35f, 1.0f, alpha);
        outlineFont.setColor(0.15f, 0.0f, 0.25f, alpha);

        // Mide el texto para centrarlo
        layout.setText(fillFont, msg);

        // Posición centrada horizontal y a una altura fija desde abajo
        float x = left + (w - layout.width) / 2f;
        float y = bottom + 200f;

        // Dibuja el texto con borde
        FontUtils.drawOutlined(batch, outlineFont, fillFont, msg, x, y, 3.5f);

        // Restaura el estilo por defecto para no afectar otros textos/screens
        applyDefaultTextStyle();

        batch.end();
    }

    // Se ejecuta cuando la app vuelve desde segundo plano:
    // reanuda la música del splash si estaba iniciada
    @Override
    public void resume() {
        if (audioStarted) audio.playFondo();
    }

    // Se ejecuta cuando se abandona la pantalla:
    // detiene la música del splash para que no se solape con otras pantallas
    @Override
    public void hide() {
        audio.stopFondo();
    }

    // Libera recursos:
    // llama al dispose de BaseScreen y libera el audio del splash
    @Override
    public void dispose() {
        super.dispose();
        audio.dispose();
    }
}
