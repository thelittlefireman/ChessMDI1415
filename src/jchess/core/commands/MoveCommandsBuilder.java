package jchess.core.commands;

import jchess.core.Chessboard;
import jchess.core.Square;
import jchess.core.moves.Move;
import org.apache.log4j.Logger;

/**
 * Created by thoma on 09/05/2016.
 */
public class MoveCommandsBuilder {
    private  int xFrom=-1, yFrom=-1, xTo=-1,  yTo=-1;
    private Chessboard chessboard;
    private MoveCommandsBuilder(Chessboard chessboard){
        this.chessboard = chessboard;
    }
    private static final Logger LOG = Logger.getLogger(MoveCommandsBuilder.class);
    public MoveCommands buildMoveCommands() {
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
    public static MoveCommands buildMoveCommands(Chessboard chessboard, String from, String to){

        return null;
    }
    public static MoveCommandsBuilder load(Chessboard chessboard){
        MoveCommandsBuilder moveCommandsBuilder = new MoveCommandsBuilder(chessboard);
        return moveCommandsBuilder;
    }

    public  MoveCommandsBuilder xFrom(int i){
        this.xFrom =i;
        return this;
    }
    public  MoveCommandsBuilder yFrom(int i){
        this.yFrom=i;
        return this;
    }
    public  MoveCommandsBuilder yTo(int i){
        this.yTo = i;
        return this;
    }
    public  MoveCommandsBuilder xTo(int i){
        this.xTo =i;
        return this;
    }

}
