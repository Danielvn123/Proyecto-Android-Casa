package com.dani.mijuego.game;

import java.util.HashMap;
import java.util.Map;

public final class I18n {

    public enum Lang { ES, EN }

    private static Lang lang = Lang.ES;

    private static final Map<String, String> ES = new HashMap<>();
    private static final Map<String, String> EN = new HashMap<>();

    static {

        // ===== MENU =====
        ES.put("menu_play", "JUGAR");
        ES.put("menu_records", "RECORDS");
        ES.put("menu_options", "OPCIONES");
        ES.put("menu_credits", "CREDITOS");

        EN.put("menu_play", "PLAY");
        EN.put("menu_records", "RECORDS");
        EN.put("menu_options", "OPTIONS");
        EN.put("menu_credits", "CREDITS");

        // ===== GAME HUD / MENSAJES =====
        ES.put("hud_height", "Altura");
        EN.put("hud_height", "Height");

        ES.put("hud_press_start", "PULSA PARA \n COMENZAR");
        EN.put("hud_press_start", "TAP TO \n START");

        // Mensajes de progreso / vueltas
        ES.put("msg_new_lap", "NUEVA VUELTA\n\nEMPIEZA NIVEL 1");
        EN.put("msg_new_lap", "NEW LAP\n\nLEVEL 1 STARTS");

        ES.put("msg_levelup", "SUPERASTE EL NIVEL\n\nCON EXITO");
        EN.put("msg_levelup", "LEVEL CLEARED\n\nSUCCESS");

        ES.put("msg_reached_end", "LLEGASTE AL FINAL");
        EN.put("msg_reached_end", "YOU REACHED THE END");

        ES.put("msg_win", "GANASTE LA PARTIDA");
        EN.put("msg_win", "YOU WON");


        // ===== PAUSA =====
        ES.put("pause_continue", "CONTINUAR");
        ES.put("pause_settings", "AJUSTES");
        ES.put("pause_restart", "VOLVER A EMPEZAR");
        ES.put("pause_exit", "SALIR");

        EN.put("pause_continue", "RESUME");
        EN.put("pause_settings", "SETTINGS");
        EN.put("pause_restart", "RESTART");
        EN.put("pause_exit", "EXIT");

        // ===== OPCIONES =====
        ES.put("opt_language", "IDIOMA");
        ES.put("opt_language_value_es", "CASTELLANO");
        ES.put("opt_language_value_en", "INGLES");

        EN.put("opt_language", "LANGUAGE");
        EN.put("opt_language_value_es", "SPANISH");
        EN.put("opt_language_value_en", "ENGLISH");

        // ===== SELECTOR DE MODO =====
        ES.put("mode_title", "MODO DE JUEGO");
        ES.put("mode_infinite", "INFINITO");
        ES.put("mode_normal", "NORMAL");

        EN.put("mode_title", "GAME MODE");
        EN.put("mode_infinite", "INFINITE");
        EN.put("mode_normal", "NORMAL");

        // ===== SPLASH =====
        ES.put("splash_tap", "TOCA PARA\n EMPEZAR");
        EN.put("splash_tap", "TAP TO\n START");


        // ===== PAUSA =====
        ES.put("pause_continue", "CONTINUAR");
        ES.put("pause_settings", "AJUSTES");
        ES.put("pause_restart", "VOLVER A EMPEZAR");
        ES.put("pause_exit", "SALIR");

        EN.put("pause_continue", "RESUME");
        EN.put("pause_settings", "SETTINGS");
        EN.put("pause_restart", "RESTART");
        EN.put("pause_exit", "EXIT");

        // ===== VICTORY =====
        ES.put("victory_title", "VICTORIA");
        ES.put("victory_height", "ALTURA:");
        ES.put("victory_coins", "MONEDAS:");
        ES.put("victory_tip", "PULSA PARA VOLVER AL MENU");

        EN.put("victory_title", "VICTORY");
        EN.put("victory_height", "HEIGHT:");
        EN.put("victory_coins", "COINS:");
        EN.put("victory_tip", "TAP TO RETURN TO MENU");


        // ===== GAME OVER =====
        ES.put("go_height", "Altura X");
        ES.put("go_coins", "Monedas X");
        ES.put("go_restart", "REINICIAR");

        EN.put("go_height", "Height X");
        EN.put("go_coins", "Coins X");
        EN.put("go_restart", "RESTART");

        ES.put("ui_back_hint", "PULSA PARA IR PARA ATRÁS");
        EN.put("ui_back_hint", "TAP TO GO BACK");



        // Añade aquí el resto de textos del juego (gameover, records, etc.)
    }

    private I18n() {}

    public static void setLang(Lang l) { lang = (l == null ? Lang.ES : l); }
    public static Lang getLang() { return lang; }

    public static void toggle() {
        lang = (lang == Lang.ES) ? Lang.EN : Lang.ES;
    }

    public static String t(String key) {
        if (key == null) return "";
        String v = (lang == Lang.EN) ? EN.get(key) : ES.get(key);
        if (v == null) {
            // fallback: si no existe la key, devolvemos la key para detectarlo rápido
            return key;
        }
        return v;
    }
}
