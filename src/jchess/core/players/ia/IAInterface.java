package jchess.core.players.ia;

import jchess.core.Chessboard;
import jchess.core.GameEngine;
import jchess.core.players.Player;

/**
 * Created by thoma on 09/05/2016.
 */
public abstract class IAInterface extends Player{
    protected GameEngine gameEngine;
    public IAInterface(String name, String color) {
        super(name, color);
    }

    public IAInterface(String name, String color, playerTypes playerType) {
        super(name, color, playerType);
    }

    public abstract void  playATurn();
}
