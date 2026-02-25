package com.dani.mijuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.dani.mijuego.Main;
import com.dani.mijuego.assets.Assets;
import com.dani.mijuego.game.GameConfig;
import com.dani.mijuego.game.GameSave;
import com.dani.mijuego.game.I18n;
import com.dani.mijuego.util.UiButton;

public class OptionsScreen extends BaseScreen {

    // Pantalla a la que se vuelve al pulsar atrás (si es null, vuelve al menú)
    private final Screen backScreen;

    // Texturas para representar estados ON/OFF y el botón base del menú
    private Texture texOn;
    private Texture texOff;
    private Texture texMenuBtn;

    // Botones de opciones (idioma, música, vibración, borrar récords)
    private final UiButton btnLang  = new UiButton(0, 0, 1, 1);
    private final UiButton btnMusic = new UiButton(0, 0, 1, 1);
    private final UiButton btnVibra = new UiButton(0, 0, 1, 1);
    private final UiButton btnClear = new UiButton(0, 0, 1, 1);

    // Constructor rápido: si no se indica pantalla previa, se vuelve al menú
    public OptionsScreen(Main game) {
        this(game, null);
    }

    // Constructor principal: inicializa la pantalla y guarda a dónde volver
    public OptionsScreen(Main game, Screen backScreen) {
        super(game, GameConfig.VW, GameConfig.VH);
        this.backScreen = backScreen;
    }

    // Indica que esta pantalla usa el fondo tipo menú
    @Override
    protected boolean useMenuBackground() { return true; }

    // Indica que debe mostrarse el hint inferior para volver atrás
    @Override
    protected boolean useBottomBackHint() { return true; }

    // Se ejecuta al entrar en la pantalla:
    // carga texturas, aplica ajustes guardados (música), configura BACK y coloca UI
    @Override
    public void show() {
        super.show();

        texOn = getTex(Assets.BTN_ON);
        texOff = getTex(Assets.BTN_OFF);
        texMenuBtn = getTex(Assets.BOTONMENU);

        // Aplica música guardada
        if (game != null && game.audio != null) {
            game.audio.setEnabled(GameSave.isMusicOn());
        }

        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        layoutUi();
        installDefaultInput();
    }

    // Sonido de click al pulsar botones
    private void click() {
        if (game != null && game.audio != null) game.audio.playSelectButton();
    }

    // Cuando cambia el tamaño, recalcula posiciones de la UI
    @Override
    protected void onResize() {
        layoutUi();
    }

    // Calcula y posiciona los botones centrados y separados verticalmente
    private void layoutUi() {
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        float btnW = 720f;
        float btnH = 150f;
        float gap = 40f;

        float x = (w - btnW) / 2f;
        float startY = h * 0.70f;

        btnLang.set(x, startY, btnW, btnH);
        btnMusic.set(x, startY - (btnH + gap), btnW, btnH);
        btnVibra.set(x, startY - 2f * (btnH + gap), btnW, btnH);
        btnClear.set(x, startY - 3f * (btnH + gap), btnW, btnH);
    }

    // Render principal: dibuja fondo, título, toggles (idioma/música/vibración),
    // botón de borrar récords y el hint inferior de volver
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        viewport.apply(true);
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        float uiLeft = cam.position.x - worldW / 2f;
        float uiBottom = cam.position.y - worldH / 2f;

        // Actualiza animación/hover de botones según posición del ratón/toque
        Vector3 hud = unprojectToHud(Gdx.input.getX(), Gdx.input.getY());
        btnLang.update(hud.x, hud.y, delta);
        btnMusic.update(hud.x, hud.y, delta);
        btnVibra.update(hud.x, hud.y, delta);
        btnClear.update(hud.x, hud.y, delta);

        batch.begin();

        drawMenuBackgroundIfEnabled(worldW, worldH);

        // Título
        drawCenteredTitle(I18n.t("menu_options"), uiBottom + worldH * 0.90f);

        // Toggle idioma (muestra si está EN y los textos EN/ES)
        drawToggle(btnLang,
            I18n.t("opt_language"),
            I18n.getLang() == I18n.Lang.EN,
            I18n.t("opt_language_value_en"),
            I18n.t("opt_language_value_es"),
            uiLeft, uiBottom);

        // Toggle música (ON/OFF persistente en GameSave)
        drawToggle(btnMusic,
            I18n.t("opt_music"),
            GameSave.isMusicOn(),
            I18n.t("opt_on"),
            I18n.t("opt_off"),
            uiLeft, uiBottom);

        // Toggle vibración (ON/OFF persistente en GameSave)
        drawToggle(btnVibra,
            I18n.t("opt_vibration"),
            GameSave.isVibrationOn(),
            I18n.t("opt_on"),
            I18n.t("opt_off"),
            uiLeft, uiBottom);

        // Botón de acción: borrar historial de récords
        drawActionButton(btnClear, I18n.t("opt_clear_records"), uiLeft, uiBottom);

        drawBottomBackHintIfEnabled(worldW, worldH);

        batch.end();
    }

    // Dibuja una opción tipo "toggle":
    // - Texto etiqueta a la izquierda
    // - Imagen ON/OFF a la derecha
    // - Texto dentro del switch (por ejemplo ON/OFF o EN/ES)
    private void drawToggle(UiButton btn,
                            String label,
                            boolean isOn,
                            String leftText,
                            String rightText,
                            float uiLeft,
                            float uiBottom) {

        float centerY = uiBottom + btn.bounds.y + btn.bounds.height / 2f;
        float leftX = uiLeft + btn.bounds.x;

        // Label
        float labelScale = 2.2f * btn.scale;
        fillFont.getData().setScale(labelScale);
        outlineFont.getData().setScale(labelScale);
        layout.setText(fillFont, label);
        drawOutlined(label, leftX, centerY + layout.height / 2f, 2.5f);

        // ON/OFF image
        Texture tex = isOn ? texOn : texOff;
        float btnW = 450f * btn.scale;
        float btnH = 150f * btn.scale;
        float bx = uiLeft + btn.bounds.x + btn.bounds.width - btnW;
        float by = centerY - btnH / 2f;
        if (tex != null) batch.draw(tex, bx, by, btnW, btnH);

        // ON / OFF text inside
        float smallScale = 1.8f * btn.scale;
        fillFont.getData().setScale(smallScale);
        outlineFont.getData().setScale(smallScale);

        layout.setText(fillFont, leftText);
        float lx = bx + btnW * 0.25f - layout.width / 2f;
        drawOutlined(leftText, lx, centerY + layout.height / 2f, 2.0f);

        layout.setText(fillFont, rightText);
        float rx = bx + btnW * 0.75f - layout.width / 2f;
        drawOutlined(rightText, rx, centerY + layout.height / 2f, 2.0f);

        resetFontScale();
    }

    // Dibuja un botón normal de acción (sin toggle), centrando textura y texto
    private void drawActionButton(UiButton btn, String label, float uiLeft, float uiBottom) {
        float centerY = uiBottom + btn.bounds.y + btn.bounds.height / 2f;

        float bw = btn.bounds.width * btn.scale;
        float bh = btn.bounds.height * btn.scale;
        float bx = uiLeft + btn.bounds.x + (btn.bounds.width - bw) / 2f;
        float by = centerY - bh / 2f;

        if (texMenuBtn != null) batch.draw(texMenuBtn, bx, by, bw, bh);

        float fontScale = 2.2f * btn.scale;
        fillFont.getData().setScale(fontScale);
        outlineFont.getData().setScale(fontScale);

        layout.setText(fillFont, label);
        float tx = bx + bw / 2f - layout.width / 2f;
        float ty = centerY + layout.height / 2f;
        drawOutlined(label, tx, ty, 2.5f);

        resetFontScale();
    }

    // Gestiona los toques en los botones:
    // - Idioma: alterna EN/ES y guarda en GameSave
    // - Música: activa/desactiva y aplica al sistema de audio
    // - Vibración: alterna y guarda preferencia
    // - Borrar récords: limpia historial guardado
    @Override
    protected boolean onTouchDownHud(float xHud, float yHud) {

        if (btnLang.hit(xHud, yHud)) {
            click();
            I18n.toggle();
            GameSave.setLang(I18n.getLang() == I18n.Lang.EN ? "EN" : "ES");
            return true;
        }

        if (btnMusic.hit(xHud, yHud)) {
            click();
            boolean newOn = !GameSave.isMusicOn();
            GameSave.setMusicOn(newOn);
            if (game != null && game.audio != null) {
                game.audio.setEnabled(newOn);
                if (!newOn) game.audio.stopAllMusic();
            }
            return true;
        }

        if (btnVibra.hit(xHud, yHud)) {
            click();
            GameSave.setVibrationOn(!GameSave.isVibrationOn());
            return true;
        }

        if (btnClear.hit(xHud, yHud)) {
            click();
            GameSave.clearRunsHistory();
            return true;
        }

        return false;
    }

    // Acción al pulsar atrás: vuelve a la pantalla anterior si existe, o al menú
    @Override
    protected void onBack() {
        click();
        if (backScreen != null) game.setScreen(backScreen);
        else game.setScreen(new MenuScreen(game));
    }
}
