package jchess.core;

import jchess.core.visitorsPieces.VisitorPieceInterface;

/**
 * Created by thoma on 09/05/2016.
 */
public interface ChessboardParts {
    public void accept(VisitorPieceInterface visitorPieceInterface);
}
