package com.dani.mijuego.game;

// Clase que contiene todas las constantes globales del juego
// Sirve para centralizar valores y poder ajustarlos fácilmente
public final class GameConfig {

    // Constructor privado para que no se pueda instanciar
    private GameConfig() {}


    // Tamaño del mundo virtual
    public static final float VW = 1080f;   // Ancho virtual
    public static final float VH = 1920f;   // Alto virtual


    // Física del jugador
    public static final float GRAVITY = 1800f;    // Fuerza de gravedad
    public static final float JUMP_VEL = 1500f;   // Velocidad inicial del salto

    // Tamaño del jugador
    public static final float PLAYER_W = 240f;
    public static final float PLAYER_H = 260f;

    // Movimiento horizontal
    public static final float MOVE_SPEED = 650f;  // Velocidad lateral máxima
    public static final float DEAD_ZONE = 0.25f;  // Zona muerta del acelerómetro
    public static final float SMOOTH = 0.12f;     // Suavizado del movimiento

    // Plataformas
    public static final float PLATFORM_W = 280f;
    public static final float PLATFORM_H = 150f;
    public static final float STEP_Y = 300f;      // Distancia vertical entre plataformas

    // Probabilidad y velocidad de plataformas móviles
    public static final float MOVING_CHANCE = 0.30f;
    public static final float MOVING_SPEED_MIN = 120f;
    public static final float MOVING_SPEED_MAX = 260f;

    // Plataformas rompibles
    public static final float BREAKABLE_CHANCE = 0.20f;
    public static final float BROKEN_FADE_TIME = 0.45f;  // Tiempo hasta desaparecer

    // Escala visual cuando se rompen
    public static final float BROKEN_SCALE_X = 1.25f;
    public static final float BROKEN_SCALE_Y = 1.25f;

    // Capa de ruinas (decoración fondo)
    public static final float RUINAS_H = 1000f;          // Altura de la imagen
    public static final float RUINAS_PARALLAX = 0.35f;   // Efecto parallax
    public static final float RUINAS_FADE_SPEED = 0.9f;  // Velocidad de desaparición


    // Monedas
    public static final float COIN_W = 90f;
    public static final float COIN_H = 90f;
    public static final float COIN_CHANCE = 0.25f;       // Probabilidad de aparición
    public static final float COIN_MIN_GAP_Y = 520f;     // Distancia mínima vertical entre monedas

    // Enemigos
    public static final float ENEMY_W = 140f;
    public static final float ENEMY_H = 140f;
    public static final float ENEMY_CHANCE = 0.35f;      // Probabilidad de aparición
    public static final float ENEMY_MIN_GAP_Y = 450f;    // Distancia mínima vertical entre enemigos

    // Spawner general
    public static final float PICKUP_CHANCE = 0.45f;     // Probabilidad de generar algo (coin/enemy)
    public static final float PICKUP_COIN_WEIGHT = 0.65f;// Peso para elegir moneda frente a enemigo

    // Empuje enemigo (knockback)
    public static final float ENEMY_PUSH_X = 2500f;  // Empuje horizontal
    public static final float ENEMY_PUSH_Y = 1200f;  // Empuje vertical
    public static final float KNOCKBACK_DAMP = 8f;   // Suavizado del empuje
}
