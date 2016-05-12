package jchess.core.commands;

import jchess.core.Chessboard;
import jchess.core.GameEngine;
import jchess.core.Square;
import jchess.core.moves.Castling;
import jchess.core.moves.Move;
import jchess.core.moves.MovesHistoryView;
import jchess.core.pieces.Piece;
import jchess.core.pieces.implementation.King;
import jchess.core.pieces.implementation.Pawn;
import jchess.core.pieces.implementation.Rook;
import jchess.utils.Settings;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Created by thoma on 09/05/2016.
 */
public class CommandsManager {
    private static final Logger LOG = Logger.getLogger(CommandsManager.class);
    private MovesHistoryView movesHistoryView;
    private GameEngine gameEngine;
    private ArrayList<String> move = new ArrayList<String>();
    public CommandsManager (GameEngine gameEngine){
        this.gameEngine = gameEngine;
        this.movesHistoryView =new MovesHistoryView(gameEngine);
    }


    public void execute(CommandInterface commandInterface) {
        if (commandInterface instanceof MoveCommands){
            ((MoveCommands) commandInterface).setMovesHistoryView(movesHistoryView);
            ((MoveCommands) commandInterface).setChessboard(gameEngine.getChessboard());
        }
        commandInterface.execute();
    }

    public synchronized boolean redo( boolean refresh) {
        if (this.gameEngine.getChessboard().getSettings().getGameType() == Settings.gameTypes.local) //redo only for local game
        {
            Move first = this.movesHistoryView.redo();
            Square from = null;
            Square to = null;

            if (first != null) {
                from = first.getFrom();
                to = first.getTo();

                new MoveCommands(this.gameEngine.getChessboard().getSquares()[from.getPozX()][from.getPozY()], this.gameEngine.getChessboard().getSquares()[to.getPozX()][to.getPozY()], true, false).execute();
                if (first.getPromotedPiece() != null) {
                    Pawn pawn = (Pawn) this.gameEngine.getChessboard().getSquares()[to.getPozX()][to.getPozY()].piece;
                    pawn.setSquare(null);

                    this.gameEngine.getChessboard().getSquare(to.getPozX(),to.getPozY()).piece = first.getPromotedPiece();
                    Piece promoted = this.gameEngine.getChessboard().getSquares()[to.getPozX()][to.getPozY()].piece;
                    promoted.setSquare(this.gameEngine.getChessboard().getSquares()[to.getPozX()][to.getPozY()]);
                }
                return true;
            }

        }
        return false;
    }

    public MovesHistoryView getMovesHistoryView() {
        return movesHistoryView;
    }

    public synchronized boolean undo(boolean refresh) //undo last move
    {
        Move last = this.movesHistoryView.undo();
        if (last != null && last.getFrom() != null) {
            Square begin = last.getFrom();
            Square end = last.getTo();
            try {
                Piece moved = last.getMovedPiece();
                this.gameEngine.getChessboard().getSquare(begin.getPozX(),begin.getPozY()).piece = moved;

                moved.setSquare(this.gameEngine.getChessboard().getSquares()[begin.getPozX()][begin.getPozY()]);

                Piece taken = last.getTakenPiece();
                if (last.getCastlingMove() != Castling.NONE) {
                    Piece rook = null;
                    if (last.getCastlingMove() == Castling.SHORT_CASTLING) {
                        rook = this.gameEngine.getChessboard().getSquares()[end.getPozX() - 1][end.getPozY()].piece;
                        this.gameEngine.getChessboard().getSquare(gameEngine.getChessboard().getSettings().getSizeN()-1,begin.getPozY()).piece = rook;
                        rook.setSquare(this.gameEngine.getChessboard().getSquares()[gameEngine.getChessboard().getSettings().getSizeN()-1][begin.getPozY()]);
                        this.gameEngine.getChessboard().getSquare(end.getPozX() - 1,end.getPozY()).piece = null;
                    } else {
                        rook = this.gameEngine.getChessboard().getSquares()[end.getPozX() + 1][end.getPozY()].piece;
                        this.gameEngine.getChessboard().getSquare(0,begin.getPozY()).piece = rook;
                        rook.setSquare(this.gameEngine.getChessboard().getSquares()[0][begin.getPozY()]);
                        this.gameEngine.getChessboard().getSquare(end.getPozX() + 1,end.getPozY()).piece = null;
                    }
                    ((King) moved).setWasMotioned(false);
                    ((Rook) rook).setWasMotioned(false);
                } else if (moved.getName().equals("Rook")) {
                    ((Rook) moved).setWasMotioned(false);
                } else if (moved.getName().equals("Pawn") && last.wasEnPassant()) {
                    Pawn pawn = (Pawn) last.getTakenPiece();
                    this.gameEngine.getChessboard().getSquare(end.getPozX(),begin.getPozY()).piece = pawn;
                    pawn.setSquare(this.gameEngine.getChessboard().getSquares()[end.getPozX()][begin.getPozY()]);

                } else if (moved.getName().equals("Pawn") && last.getPromotedPiece() != null) {
                    Piece promoted = this.gameEngine.getChessboard().getSquares()[end.getPozX()][end.getPozY()].piece;
                    promoted.setSquare(null);
                    this.gameEngine.getChessboard().getSquare(end.getPozX(),end.getPozY()).piece = null;
                }

                //check one more move back for en passant
                Move oneMoveEarlier = this.movesHistoryView.getLastMoveFromHistory();
                if (oneMoveEarlier != null && oneMoveEarlier.wasPawnTwoFieldsMove()) {
                    Piece canBeTakenEnPassant = this.gameEngine.getChessboard().getSquare(oneMoveEarlier.getTo().getPozX(), oneMoveEarlier.getTo().getPozY()).getPiece();
                    if (canBeTakenEnPassant.getName().equals("Pawn")) {
                        this.gameEngine.getChessboard().setTwoSquareMovedPawn((Pawn) canBeTakenEnPassant);
                    }
                }

                if (taken != null && !last.wasEnPassant()) {
                    this.gameEngine.getChessboard().getSquare(end.getPozX(),end.getPozY()).piece = taken;
                    taken.setSquare(this.gameEngine.getChessboard().getSquares()[end.getPozX()][end.getPozY()]);
                } else {
                    this.gameEngine.getChessboard().getSquare(end.getPozX(),end.getPozY()).piece = null;
                }


                if (refresh) {
                    this.gameEngine.getChessboard().unselect();//unselect square
                    this.gameEngine.getChessboard().repaint();
                }

            } catch (ArrayIndexOutOfBoundsException | NullPointerException exc) {
                LOG.error("error: " + exc.getClass() + " exc object: " + exc);
                return false;
            }

            return true;
        } else {
            return false;
        }
    }


}
