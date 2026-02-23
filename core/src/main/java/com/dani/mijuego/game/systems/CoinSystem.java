package com.dani.mijuego.game.systems;

import com.badlogic.gdx.utils.Array;
import com.dani.mijuego.game.entities.Coin;
import com.dani.mijuego.game.entities.Player;

// Sistema que gestiona todas las monedas del juego
public class CoinSystem {

    // Lista de monedas activas
    public final Array<Coin> coins = new Array<>();

    // Contador de monedas recogidas
    public int collected = 0;

    // Interfaz para avisar cuando se recoge una moneda
    public interface OnCoinCollected {
        void onCoinCollected();
    }

    // Actualiza las monedas del juego
    public void update(Player player, float killY, OnCoinCollected callback) {

        // Recorremos la lista desde el final para poder eliminar elementos sin errores
        for (int i = coins.size - 1; i >= 0; i--) {

            Coin c = coins.get(i);

            // Si la plataforma ya no existe, eliminamos la moneda
            if (c.platform == null) {
                coins.removeIndex(i);
                continue;
            }

            // Si la plataforma está por debajo del límite (fuera de pantalla), eliminamos la moneda
            if (c.platform.rect.y + c.platform.rect.height < killY) {
                coins.removeIndex(i);
                continue;
            }

            // Sincroniza la posición de la moneda con su plataforma
            c.sync();

            // Si el jugador existe y colisiona con la moneda
            if (player != null && c.rect.overlaps(player.rect)) {

                // Aumenta el contador de monedas recogidas
                collected++;

                // Ejecuta el callback si existe (por ejemplo para sonido o puntuación)
                if (callback != null) callback.onCoinCollected();

                // Elimina la moneda tras recogerla
                coins.removeIndex(i);
            }
        }
    }
}
