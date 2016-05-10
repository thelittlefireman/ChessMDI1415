package jchess.core.commands;

import jchess.core.Chessboard;
import jchess.core.Square;
import org.apache.log4j.Logger;

/**
 * Created by thoma on 09/05/2016.
 */
public class MoveCommandsAdapter  {
    private static final Logger LOG = Logger.getLogger(MoveCommandsAdapter.class);
    public static MoveCommands BuildMoveCommands(Chessboard chessboard, int xFrom, int yFrom, int xTo, int yTo) {
        Square fromSQ = null;
        Square toSQ = null;
        try {
            fromSQ = chessboard.getSquares()[xFrom][yFrom];
            toSQ = chessboard.getSquares()[xTo][yTo];
        } catch (java.lang.IndexOutOfBoundsException exc) {
            LOG.error("error moving piece: " + exc.getMessage());
            return null;
        }
        return new MoveCommands(fromSQ, toSQ, true);
    }
}
