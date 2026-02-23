package com.dani.mijuego.game.entities;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.dani.mijuego.game.GameConfig;

// Clase que representa al jugador del juego
// Controla su movimiento horizontal y su representación gráfica
public class Player {

    // Rectángulo usado para posición y colisiones
    public final Rectangle rect;

    // Velocidad vertical usada en el salto
    public float velY = 0f;

    // Empuje horizontal
    public float knockbackX = 0f;

    // Se usa para suavizar el movimiento del jugador cuando inclinas el móvil
    private float tiltFiltered = 0f;

    // Texturas según dirección
    private final Texture idle;
    private final Texture left;
    private final Texture right;

    // Textura actual a dibujar
    private Texture current;

    // ==========================
    // POWER-UPS (estado del jugador)
    // ==========================
    private int bootsJumpsLeft = 0;

    private boolean shieldActive = false;

    private boolean setaActive = false;
    private float setaTimer = 0f;

    // Constructor del jugador
    public Player(float x, float y, Texture idle, Texture left, Texture right) {
        this.rect = new Rectangle(x, y, GameConfig.PLAYER_W, GameConfig.PLAYER_H);
        this.idle = idle;
        this.left = left;
        this.right = right;
        this.current = idle;
    }

    // Devuelve la textura actual del jugador
    public Texture getTexture() {
        return current;
    }

    // ==========================
    // POWER-UPS (métodos)
    // ==========================

    // Actualiza temporizadores de efectos (llamar 1 vez por frame)
    public void updateEffects(float dt) {
        if (setaActive) {
            setaTimer -= dt;
            if (setaTimer <= 0f) {
                setaTimer = 0f;
                setaActive = false;
            }
        }
    }

    // Da botas (por ejemplo 1 salto extra)
    public void giveBoots(int jumps) {
        bootsJumpsLeft = Math.max(bootsJumpsLeft, jumps);
    }

    // Consume un salto de botas si hay (devuelve true si se usó)
    public boolean consumeBootsJump() {
        if (bootsJumpsLeft > 0) {
            bootsJumpsLeft--;
            return true;
        }
        return false;
    }

    // Intenta activar escudo (si la seta está activa, no hace efecto)
    public boolean tryActivateShield() {
        if (setaActive) return false;
        shieldActive = true;
        return true;
    }

    // Intenta activar seta:
    // - si hay escudo: lo rompe y NO activa la seta (devuelve false)
    // - si no: activa la seta (devuelve true)
    public boolean tryActivateSeta(float duration) {
        if (shieldActive) {
            shieldActive = false;
            return false;
        }
        setaActive = true;
        setaTimer = duration;
        return true;
    }

    // Bloquea golpe de enemigo si hay escudo (lo rompe y devuelve true)
    public boolean blockEnemyHitIfShield() {
        if (!shieldActive) return false;
        shieldActive = false;
        return true;
    }

    public boolean isShieldActive() { return shieldActive; }
    public boolean isSetaActive() { return setaActive; }
    public int getBootsJumpsLeft() { return bootsJumpsLeft; }

    // Invertir controles solo si seta activa y NO hay escudo
    private boolean invertControls() {
        return setaActive && !shieldActive;
    }

    // ==========================
    // Movimiento
    // ==========================

    // Actualiza el movimiento horizontal del jugador
    public void updateHorizontal(float dt) {

        // Guardamos posición X para poder invertir el movimiento si hay seta
        float oldX = rect.x;

        // Lectura del acelerómetro
        float tiltRaw = -Gdx.input.getAccelerometerX();

        // Suavizado del movimiento
        tiltFiltered = tiltFiltered + (tiltRaw - tiltFiltered) * GameConfig.SMOOTH;

        // Así evitamos que el personaje se mueva cuando el móvil está recto
        float tilt = deadZone(tiltFiltered, GameConfig.DEAD_ZONE);

        // Control alternativo con teclado
        float kb = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) kb -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) kb += 1f;

        // Elige qué control usar
        float control = (kb != 0f) ? kb : tilt;

        // Limita velocidad horizontal
        float vx = MathUtils.clamp(
            control * GameConfig.MOVE_SPEED,
            -GameConfig.MOVE_SPEED,
            GameConfig.MOVE_SPEED
        );

        // Reducción progresiva del knockback
        knockbackX = MathUtils.lerp(
            knockbackX,
            0f,
            1f - (float) Math.exp(-GameConfig.KNOCKBACK_DAMP * dt)
        );

        // Movimiento horizontal
        rect.x += (vx + knockbackX) * dt;

        // Si la seta está activa, invertimos el desplazamiento que se ha aplicado
        if (invertControls()) {
            float dx = rect.x - oldX;
            rect.x = oldX - dx;
        }

        // Cambio de sprite según dirección (usamos vx, no dx)
        if (vx > 40) current = right;
        else if (vx < -40) current = left;
        else current = idle;

        // Salto de un lado a otro de la pantalla
        if (rect.x + rect.width < 0) rect.x = GameConfig.VW;
        if (rect.x > GameConfig.VW) rect.x = -rect.width;
    }

    // Aplica zona muerta al acelerómetro
    private float deadZone(float v, float dz) {
        return (Math.abs(v) < dz) ? 0f : v;
    }
}
