package jchess.core.pieces.implementation;

import jchess.core.Chessboard;
import jchess.core.players.Player;
import jchess.core.pieces.Piece;
import jchess.core.pieces.traits.behaviors.implementation.ValetBehavior;
import jchess.core.visitorsPieces.VisitorPieceInterface;

/**
 * Created by thoma on 08/05/2016.
 */
public class Valet extends Piece {

    protected static final short value = 3;


    public Valet(Chessboard chessboard, Player player) {
        super(chessboard, player);
        this.symbol = "V";
        this.addBehavior(new ValetBehavior(this));
    }

    @Override
    public void accept(VisitorPieceInterface visitorPieceInterface) {
        visitorPieceInterface.visit(this);
    }
}
