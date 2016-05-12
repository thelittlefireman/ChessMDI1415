package jchess.core.players.ia;

import jchess.core.Chessboard;
import jchess.core.pieces.Piece;

/**
 * Created by thoma on 09/05/2016.
 */
public class RandomIA extends IAInterface {
    public RandomIA(Chessboard chessboard, String name, String color){
        super( name,  color, playerTypes.computer);
        this.chessboard =chessboard;
    }
    @Override
    public void playATurn() {
        Piece pieceToMove =  this.chessboard.getAllPieces(this.getColor()).get((int) Math.random()*this.chessboard.getAllPieces(this.getColor()).size());

    }
}
