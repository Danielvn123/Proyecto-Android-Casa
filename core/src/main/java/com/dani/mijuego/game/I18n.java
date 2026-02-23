package com.dani.mijuego.game;

import java.util.HashMap;
import java.util.Map;

// Clase de traducciones del juego (i18n)
// Guarda textos en ES/EN y devuelve el que toque según el idioma activo
public final class I18n {

    // Idiomas disponibles
    public enum Lang { ES, EN }

    // Idioma actual (por defecto español)
    private static Lang lang = Lang.ES;

    // Mapas de traducciones por clave
    private static final Map<String, String> ES = new HashMap<>();
    private static final Map<String, String> EN = new HashMap<>();

    // Bloque estático: se ejecuta una vez y rellena las traducciones
    static {

        // MENU
        ES.put("menu_play", "JUGAR");
        ES.put("menu_records", "RÉCORDS");
        ES.put("menu_options", "OPCIONES");
        ES.put("menu_credits", "CRÉDITOS");
        ES.put("menu_instructions", "INSTRUCCIONES");

        EN.put("menu_play", "PLAY");
        EN.put("menu_records", "RECORDS");
        EN.put("menu_options", "OPTIONS");
        EN.put("menu_credits", "CREDITS");
        EN.put("menu_instructions", "INSTRUCTIONS");

        // GAME HUD / MENSAJES
        ES.put("hud_height", "Altura");
        EN.put("hud_height", "Height");

        ES.put("hud_press_start", "PULSA PARA \n COMENZAR");
        EN.put("hud_press_start", "TAP TO \n START");

        ES.put("msg_new_lap", "NUEVA VUELTA\n\nEMPIEZA NIVEL 1");
        EN.put("msg_new_lap", "NEW LAP\n\nLEVEL 1 STARTS");

        ES.put("msg_levelup", "SUPERASTE EL NIVEL\n\nCON EXITO");
        EN.put("msg_levelup", "LEVEL CLEARED\n\nSUCCESS");

        ES.put("msg_reached_end", "LLEGASTE AL FINAL");
        EN.put("msg_reached_end", "YOU REACHED THE END");

        ES.put("msg_win", "GANASTE LA PARTIDA");
        EN.put("msg_win", "YOU WON");

        // PAUSA
        ES.put("pause_continue", "CONTINUAR");
        ES.put("pause_settings", "AJUSTES");
        ES.put("pause_restart", "VOLVER A EMPEZAR");
        ES.put("pause_exit", "SALIR");

        EN.put("pause_continue", "RESUME");
        EN.put("pause_settings", "SETTINGS");
        EN.put("pause_restart", "RESTART");
        EN.put("pause_exit", "EXIT");

        // OPCIONES
        ES.put("opt_language", "IDIOMA");
        ES.put("opt_language_value_es", "CASTELLANO");
        ES.put("opt_language_value_en", "INGLÉS");
        ES.put("opt_music", "MÚSICA");
        ES.put("opt_vibration", "VIBRACIÓN");
        ES.put("opt_clear_records", "BORRAR RÉCORDS");
        ES.put("opt_on", "ON");
        ES.put("opt_off", "OFF");

        EN.put("opt_language", "LANGUAGE");
        EN.put("opt_language_value_es", "SPANISH");
        EN.put("opt_language_value_en", "ENGLISH");
        EN.put("opt_music", "MUSIC");
        EN.put("opt_vibration", "VIBRATION");
        EN.put("opt_clear_records", "CLEAR RECORDS");
        EN.put("opt_on", "ON");
        EN.put("opt_off", "OFF");

        // SELECTOR DE MODO
        ES.put("mode_title", "MODO DE JUEGO");
        ES.put("mode_infinite", "INFINITO");
        ES.put("mode_normal", "NORMAL");

        EN.put("mode_title", "GAME MODE");
        EN.put("mode_infinite", "INFINITE");
        EN.put("mode_normal", "NORMAL");

        // SPLASH
        ES.put("splash_tap", "TOCA PARA\n EMPEZAR");
        EN.put("splash_tap", "TAP TO\n START");

        // CREDITS
        ES.put("credits_title", "CRÉDITOS");
        EN.put("credits_title", "CREDITS");

        // Texto largo de créditos
        ES.put("credits_body",
            "Autor:\n" +
                "Daniel Vilas Noya\n" +
                "\n" +
                "Aplicaciones utilizadas:\n" +
                "LibGDX\n" +
                "Android Studio\n" +
                "Java\n" +
                "ChatGPT\n" +
                "Gemini"
        );

        EN.put("credits_body",
            "Author:\n" +
                "Daniel Vilas Noya\n" +
                "\n" +
                "Tools used:\n" +
                "LibGDX\n" +
                "Android Studio\n" +
                "Java\n" +
                "ChatGPT\n" +
                "Gemini"
        );

        // HOW TO PLAY
        ES.put("how_title", "INSTRUCCIONES");
        EN.put("how_title", "HOW TO PLAY");

        // Texto largo de instrucciones
        ES.put("how_body",
            "OBJETIVO\n" +
                "SUBE LO MÁS ALTO POSIBLE\n" +
                "CONSIGUE TODAS LAS MONEDAS QUE PUEDAS\n" +
                "\n" +
                "CONTROLES MÓVIL\n" +
                "- TOCA O PULSA PARA EMPEZAR\n" +
                "- INCLINA EL MÓVIL DE IZQUIERDA A DERECHA\n" +
                "- CAER SOBRE PLATAFORMAS TE HACE SALTAR\n" +
                "\n" +
                "CONTROLES ORDENADOR\n" +
                "- TOCA O PULSA PARA EMPEZAR\n" +
                "- MUEVE EL PERSONAJE CON A,W,S,D\n" +
                "- O USA LAS FLECHAS DEL TECLADO\n" +
                "\n" +
                "POWER UPS\n" +
                "- MONEDA: SUMA PUNTOS\n" +
                "- SETA: INVIERTE LOS CONTROLES\n" +
                "- TENIS: SALTAS MÁS ALTO\n" +
                "- ESCUDO: BLOQUEA UN GOLPE\n" +
                "\n" +
                "ENEMIGOS\n" +
                "- EVÍTALOS O PERDERÁS LA PARTIDA\n" +
                "\n" +
                "CUANTO MÁS SUBAS, MAYOR SERÁ LA DIFICULTAD\n"
        );

        EN.put("how_body",
            "OBJECTIVE\n" +
                "CLIMB AS HIGH AS YOU CAN\n" +
                "\n" +
                "CONTROLS\n" +
                "- TAP OR PRESS TO START\n" +
                "- MOVE LEFT / RIGHT\n" +
                "- LAND ON PLATFORMS TO JUMP\n" +
                "\n" +
                "POWER UPS\n" +
                "- COIN: GET POINTS\n" +
                "- MUSHROOM: JUMP HIGHER\n" +
                "- SHIELD: BLOCK HITS\n"
        );

        // VICTORY
        ES.put("victory_title", "VICTORIA");
        ES.put("victory_height", "ALTURA:");
        ES.put("victory_coins", "MONEDAS:");
        ES.put("victory_tip", "PULSA PARA VOLVER AL MENU");

        EN.put("victory_title", "VICTORY");
        EN.put("victory_height", "HEIGHT:");
        EN.put("victory_coins", "COINS:");
        EN.put("victory_tip", "TAP TO RETURN TO MENU");

        // GAME OVER
        ES.put("go_height", "Altura X");
        ES.put("go_coins", "Monedas X");
        ES.put("go_restart", "REINICIAR");

        EN.put("go_height", "Height X");
        EN.put("go_coins", "Coins X");
        EN.put("go_restart", "RESTART");

        // UI
        ES.put("ui_back_hint", "PULSA PARA IR PARA ATRÁS");
        EN.put("ui_back_hint", "TAP TO GO BACK");
    }

    // Evita instanciar la clase (todo es estático)
    private I18n() {}

    // Cambia el idioma actual
    public static void setLang(Lang l) {
        if (l != null) lang = l;
    }

    // Devuelve el idioma actual
    public static Lang getLang() {
        return lang;
    }

    // Alterna entre ES y EN
    public static void toggle() {
        lang = (lang == Lang.ES) ? Lang.EN : Lang.ES;
    }

    // Devuelve el texto traducido para una clave
    public static String t(String key) {
        if (key == null) return "";

        // Busca primero en el idioma actual
        String v = (lang == Lang.ES) ? ES.get(key) : EN.get(key);

        // Si no existe, intenta buscar en el otro idioma como fallback
        if (v == null) {
            v = (lang == Lang.ES) ? EN.get(key) : ES.get(key);
        }

        // Si sigue sin existir, devuelve la propia clave para detectar errores
        return (v == null) ? key : v;
    }
}
