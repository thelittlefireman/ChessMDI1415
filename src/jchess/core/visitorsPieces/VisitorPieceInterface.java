package jchess.core.visitorsPieces;

import jchess.core.pieces.implementation.*;

/**
 * Created by thoma on 09/05/2016.
 */
public interface VisitorPieceInterface {
    public void visit(King king);
    public void visit(Queen queen);
    public void visit(Valet valet);
    public void visit(Knight knight);
    public void visit(Bishop bishop);
    public void visit(Pawn pawn);
    public void visit(Rook rook);
}
