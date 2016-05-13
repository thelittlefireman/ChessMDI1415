package jchess.core.initialPlaceStrategy;

import jchess.core.Chessboard;
import jchess.core.pieces.implementation.Pawn;
import jchess.core.pieces.implementation.Valet;

/**
 * Created by thoma on 13/05/2016.
 */
public class NewPiecePlacement extends BasePlacement {
    public NewPiecePlacement(Chessboard chessboard) {
        super(chessboard);
    }
    @Override
    public void setPieces(String places) {

        if (places.equals("")) //if newGame
        {
            this.setPieces4NewGame( getChessboard().getSettings().getPlayerWhite(), getChessboard().getSettings().getPlayerBlack());
            addNewPiece();
        } else //if loadedGame
        {
            return;
        }
    }/*--endOf-setPieces--*/

    protected void addNewPiece(){
        this.getChessboard().getSquare(0, this.getChessboard().getSettings().getSizeM()-2).setPiece(new Valet(this.getChessboard(), getChessboard().getSettings().getPlayerWhite()));
        this.getChessboard().getSquare(0, 1).setPiece(new Valet(this.getChessboard(), getChessboard().getSettings().getPlayerBlack()));
    }
}
