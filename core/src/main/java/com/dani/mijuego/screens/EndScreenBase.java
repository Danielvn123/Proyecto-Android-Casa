package com.dani.mijuego.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.I18n;

// Clase base abstracta para pantallas de final (GameOver / Victory / Fin de nivel, etc.)
// Se encarga de: configurar fondo, mostrar score/coins y un botón central que ejecuta una acción
public abstract class EndScreenBase extends BaseScreen {

    // Altura/score final a mostrar en la pantalla
    protected final int score;

    // Monedas recogidas a mostrar en la pantalla
    protected final int coins;

    // Textura de fondo que usa la pantalla
    protected Texture bgTex;

    // Rectángulo clickable del botón
    protected Rectangle btn;

    // Constructor: recibe game + valores a mostrar (score y coins)
    protected EndScreenBase(Main game, int score, int coins) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.score = score;
        this.coins = coins;
    }

    // Cada pantalla hija debe devolver la textura de fondo que quiere usar
    protected abstract Texture resolveBackgroundTexture();

    // Cada pantalla hija debe devolver la key de i18n para el texto del botón
    protected abstract String buttonTextKey();

    // Acción que ocurre al pulsar el botón (reiniciar, ir al menú, siguiente nivel, etc.)
    protected abstract void onButtonPressed();

    // Hook opcional al entrar en la pantalla (ej: reproducir sonido, parar música, vibrar, etc.)
    protected void onEnter() {
        // opcional para sonido etc
    }

    // Se ejecuta al entrar en la pantalla: carga fondo, calcula botón, llama hooks e instala input
    @Override
    public void show() {
        super.show();

        // Resuelve el fondo según la implementación concreta
        bgTex = resolveBackgroundTexture();

        // Ancho del mundo (coordenadas del viewport, no píxeles)
        float w = viewport.getWorldWidth();

        // Tamaño del botón (diseño fijo)
        float bw = 520f;
        float bh = 140f;

        // Centra horizontalmente el botón
        float bx = (w - bw) / 2f;

        // Posición vertical del botón (cerca de la parte inferior)
        float by = 90f;

        // Crea el rectángulo clickable del botón
        btn = new Rectangle(bx, by, bw, bh);

        // Hook de entrada (sonido, etc.)
        onEnter();

        // Input por defecto de BaseScreen (atrás, toque en HUD, etc.)
        installDefaultInput();
    }

    // Render: limpia pantalla, aplica cámara/viewport y dibuja fondo + textos + botón
    @Override
    public void render(float delta) {
        // Limpia el frame
        ScreenUtils.clear(0, 0, 0, 1);

        // Aplica viewport (sin "centerCamera" forzado; usas el default)
        viewport.apply();

        // Actualiza cámara y matriz de dibujo
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        // Dimensiones del mundo
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        // Bordes visibles de la cámara (para posicionar UI)
        float uiLeft = cam.position.x - w / 2f;
        float uiTop = cam.position.y + h / 2f;
        float uiBottom = cam.position.y - h / 2f;

        batch.begin();

        // Fondo a pantalla completa
        if (bgTex != null) {
            batch.draw(bgTex, uiLeft, uiBottom, w, h);
        }

        // TEXTO ALTURA / MONEDAS
        // Escala grande del font para estos textos.
        font.getData().setScale(4.5f);
        font.setColor(1f, 1f, 1f, 1f);

        // Texto de altura: "Altura 123 m" (localizado con i18n)
        String alturaTxt = I18n.t("go_height") + " " + score + " m";
        layout.setText(font, alturaTxt);
        // Posición fija relativa al borde superior e izquierdo
        font.draw(batch, layout, uiLeft + 340f, uiTop - 250f);

        // Texto de monedas: "Monedas 10" (localizado con i18n)
        String monedasTxt = I18n.t("go_coins") + " " + coins;
        layout.setText(font, monedasTxt);
        font.draw(batch, layout, uiLeft + 340f, uiTop - 380f);

        // TEXTO DEL BOTÓN
        // Resuelve el texto del botón desde la key que da la subclase
        String btnText = I18n.t(buttonTextKey());
        layout.setText(font, btnText);

        // Calcula posición del texto dentro del botón
        // Nota: aquí centras "a ojo" con /2.5f (no totalmente centrado exacto)
        float textX = uiLeft + btn.x + (btn.width - layout.width) / 2.5f;
        // Coloca el texto cerca de la parte superior del botón
        float textY = uiBottom + btn.y + btn.height * 0.95f;

        // Dibuja el texto del botón (no se dibuja un sprite/rectángulo, solo el texto)
        font.draw(batch, layout, textX, textY);

        // Restaura escala del font para no afectar a otros dibujados
        font.getData().setScale(1f);

        batch.end();
    }

    // Maneja toques en el HUD: si el toque cae dentro del rectángulo del botón, ejecuta acción
    @Override
    protected boolean onTouchDownHud(float xHud, float yHud) {
        // Comprueba que el botón exista y que la pulsación esté dentro
        if (btn != null && btn.contains(xHud, yHud)) {
            // Ejecuta la acción concreta (la decide la subclase)
            onButtonPressed();
            return true;
        }
        return false;
    }
}
