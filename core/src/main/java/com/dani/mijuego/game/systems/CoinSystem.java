package com.dani.mijuego.game.systems;

import com.badlogic.gdx.utils.Array;
import com.dani.mijuego.game.entities.Coin;
import com.dani.mijuego.game.entities.Player;
public class CoinSystem {

    public final Array<Coin> coins = new Array<>();
    public int collected = 0;

    public interface OnCoinCollected {
        void onCoinCollected();
    }

    public void update(Player player, float killY, OnCoinCollected callback) {
        for (int i = coins.size - 1; i >= 0; i--) {
            Coin c = coins.get(i);

            if (c.platform == null) {
                coins.removeIndex(i);
                continue;
            }

            if (c.platform.rect.y + c.platform.rect.height < killY) {
                coins.removeIndex(i);
                continue;
            }

            c.sync();

            if (player != null && c.rect.overlaps(player.rect)) {
                collected++;
                if (callback != null) callback.onCoinCollected();
                coins.removeIndex(i);
            }
        }
    }
}
