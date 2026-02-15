package com.dani.mijuego.game;

public final class GameConfig {

    private GameConfig() {}

    public static final float VW = 1080f;
    public static final float VH = 1920f;

    public static final float GRAVITY = 1800f;
    public static final float JUMP_VEL = 1500f;

    public static final float PLAYER_W = 240f;
    public static final float PLAYER_H = 260f;

    public static final float MOVE_SPEED = 650f;
    public static final float DEAD_ZONE = 0.25f;
    public static final float SMOOTH = 0.12f;

    public static final float PLATFORM_W = 280f;
    public static final float PLATFORM_H = 150f;
    public static final float STEP_Y = 300f;

    public static final float MOVING_CHANCE = 0.30f;
    public static final float MOVING_SPEED_MIN = 120f;
    public static final float MOVING_SPEED_MAX = 260f;

    public static final float BREAKABLE_CHANCE = 0.20f;
    public static final float BROKEN_FADE_TIME = 0.45f;

    public static final float BROKEN_SCALE_X = 1.25f;
    public static final float BROKEN_SCALE_Y = 1.25f;

    public static final float RUINAS_H = 1000f;
    public static final float RUINAS_PARALLAX = 0.35f;
    public static final float RUINAS_FADE_SPEED = 0.9f;

    public static final float COIN_W = 90f;
    public static final float COIN_H = 90f;
    public static final float COIN_CHANCE = 0.25f;
    public static final float COIN_MIN_GAP_Y = 520f;

    public static final float ENEMY_W = 140f;
    public static final float ENEMY_H = 140f;
    public static final float ENEMY_CHANCE = 0.35f;
    public static final float ENEMY_MIN_GAP_Y = 450f;

    public static final float PICKUP_CHANCE = 0.45f;
    public static final float PICKUP_COIN_WEIGHT = 0.65f;


    public static final float ENEMY_PUSH_X = 2500f;
    public static final float ENEMY_PUSH_Y = 1200f;
    public static final float KNOCKBACK_DAMP = 8f;
}
