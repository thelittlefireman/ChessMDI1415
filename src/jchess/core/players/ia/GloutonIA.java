package jchess.core.players.ia;

import jchess.core.Chessboard;

/**
 * Created by thoma on 09/05/2016.
 */
public class GloutonIA extends IAInterface {
    public GloutonIA(Chessboard chessboard,String name, String color) {
        super(name, color,playerTypes.computer);
        this.chessboard =chessboard;
    }

    @Override
    public void playATurn() {

    }
}
