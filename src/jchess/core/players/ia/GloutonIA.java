package jchess.core.players.ia;

import jchess.core.Chessboard;
import jchess.core.GameEngine;
import jchess.core.Square;
import jchess.core.commands.MoveCommands;
import jchess.core.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thoma on 09/05/2016.
 */
public class GloutonIA extends IAInterface {
    public GloutonIA(GameEngine gameEngine, String name, String color) {
        super(name, color, playerTypes.computer);
        this.gameEngine = gameEngine;
    }

    @Override
    public void playATurn() {
        List<Piece> listPieceToMove = this.gameEngine.getChessboard().getAllPieces(this.getColor());
        short actualMaxValue = 0;
        Square begin=null,end=null;
        for (Piece piece : listPieceToMove) {
            List<Square> possiblity = new ArrayList<>(piece.getAllMoves());
            for (Square square : possiblity) {
                if(square.getPiece().getValue()>actualMaxValue){
                    begin = piece.getSquare();
                    end = square;
                    actualMaxValue = square.getPiece().getValue();
                }
            }
        }
        if(begin!=null && end !=null) {
            this.gameEngine.getCommandsManager().execute(new MoveCommands(begin, end));
            this.gameEngine.nextMove();
        }else {
            new RandomIA(this.gameEngine,getName(),this.color.getColorName()).playATurn();
        }

    }
}
