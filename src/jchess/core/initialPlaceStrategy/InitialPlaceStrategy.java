package jchess.core.initialPlaceStrategy;

import jchess.core.Chessboard;

/**
 * Created by thoma on 08/05/2016.
 */
public abstract class InitialPlaceStrategy {
    public InitialPlaceStrategy(Chessboard chessboard){
        this.chessboard = chessboard;
    }

    public Chessboard getChessboard() {
        return chessboard;
    }

    private Chessboard chessboard;
      public abstract void setPieces(String places);
}
