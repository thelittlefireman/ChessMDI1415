package jchess.core.players.ia;

import jchess.core.Chessboard;
import jchess.core.GameEngine;

/**
 * Created by thoma on 09/05/2016.
 */
public class MinMaxIA extends IAInterface {
    public MinMaxIA(GameEngine gameEngine, String name, String color) {
        super(name, color, playerTypes.computer);
        this.gameEngine =gameEngine;
    }

    @Override
    public void playATurn() {

    }
}
