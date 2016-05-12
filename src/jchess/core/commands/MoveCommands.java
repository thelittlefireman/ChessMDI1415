package jchess.core.commands;

import jchess.JChessApp;
import jchess.core.Chessboard;
import jchess.core.Square;
import jchess.core.moves.Castling;
import jchess.core.moves.MovesHistoryView;
import jchess.core.pieces.Piece;
import jchess.core.pieces.implementation.Bishop;
import jchess.core.pieces.implementation.King;
import jchess.core.pieces.implementation.Knight;
import jchess.core.pieces.implementation.Pawn;
import jchess.core.pieces.implementation.Queen;
import jchess.core.pieces.implementation.Rook;
import jchess.core.pieces.implementation.Valet;

import org.apache.log4j.Logger;

/**
 * Created by thoma on 09/05/2016.
 */
public class MoveCommands implements CommandInterface {

    private static final Logger LOG = Logger.getLogger(MoveCommands.class);
    private Chessboard chessboard;

    public void setChessboard(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    private MovesHistoryView movesHistoryView;
    private boolean refresh,clearForwardHistory;
    private Square begin,end;

    public void setMovesHistoryView(MovesHistoryView movesHistoryView) {
        this.movesHistoryView = movesHistoryView;
    }

    public MoveCommands(Square begin, Square end, boolean refresh, boolean clearForwardHistory) {
        this.begin =begin;
        this.end =end;
        this.refresh =refresh;
        this.clearForwardHistory =clearForwardHistory;
    }

    public MoveCommands(Square begin, Square end) {
        this(begin, end, true);
    }


    public MoveCommands(Square begin, Square end, boolean refresh) {
        this(begin, end, refresh, true);
    }

    /**
     *
     * @param begin   square from which move piece
     * @param end     square where we want to move piece         *
     * @param refresh chessboard, default: true
     */
    public void move(Square begin, Square end, boolean refresh, boolean clearForwardHistory) {
        Castling wasCastling = Castling.NONE;
        Piece promotedPiece = null;
        boolean wasEnPassant = false;
        if (end.piece != null) {
            end.getPiece().setSquare(null);
        }

        Square tempBegin = new Square(begin);//4 movesHistoryView history
        Square tempEnd = new Square(end);  //4 movesHistoryView history

        begin.getPiece().setSquare(end);//set square of piece to ending
        end.piece = begin.piece;//for ending square set piece from beginin square
        begin.piece = null;//make null piece for begining square

        if (end.getPiece().getName().equals("King")) {
            if (!((King) end.piece).getWasMotioned()) {
                ((King) end.piece).setWasMotioned(true);
            }

            //Castling
            if (begin.getPozX() + 2 == end.getPozX()) {
                move(this.chessboard.getSquare(7, begin.getPozY()), this.chessboard.getSquare(end.getPozX() - 1, begin.getPozY()), false, false);
                wasCastling = Castling.SHORT_CASTLING;
            } else if (begin.getPozX() - 2 == end.getPozX()) {
                move(this.chessboard.getSquare(0, begin.getPozY()), this.chessboard.getSquare(end.getPozX() + 1, begin.getPozY()), false, false);
                wasCastling = Castling.LONG_CASTLING;
            }
            //endOf Castling
        } else if (end.getPiece().getName().equals("Rook")) {
            if (!((Rook) end.piece).getWasMotioned()) {
                ((Rook) end.piece).setWasMotioned(true);
            }
        } else if (end.getPiece().getName().equals("Pawn")) {
            if (this.chessboard.getTwoSquareMovedPawn() != null && this.chessboard.getSquares()[end.getPozX()][begin.getPozY()] == this.chessboard.getTwoSquareMovedPawn().getSquare()) //en passant
            {
                tempEnd.piece = this.chessboard.getSquares()[end.getPozX()][begin.getPozY()].piece; //ugly hack - put taken pawn in en passant plasty do end square

                this.chessboard.getSquare(end.getPozX(),begin.getPozY()).piece = null;
                wasEnPassant = true;
            }

            if (begin.getPozY() - end.getPozY() == 2 || end.getPozY() - begin.getPozY() == 2) //moved two square
            {
                this.chessboard.setTwoSquareMovedPawn((Pawn) end.piece);
            } else {
                this.chessboard.setTwoSquareMovedPawn(null); //erase last saved move (for En passant)
            }

            if (end.getPiece().getSquare().getPozY() == 0 || end.getPiece().getSquare().getPozY() == 7) //promote Pawn
            {
                if (clearForwardHistory) {
                    String color = String.valueOf(end.getPiece().getPlayer().getColor().getSymbolAsString().toUpperCase());
                    String newPiece = JChessApp.getJavaChessView().showPawnPromotionBox(color); //return name of new piece

                    Piece piece;
                    switch (newPiece) {
                        case "Queen":
                            piece = new Queen(this.chessboard, end.getPiece().getPlayer());
                            break;
                        case "Rook":
                            piece = new Rook(this.chessboard, end.getPiece().getPlayer());
                            break;
                        case "Bishop":
                            piece = new Bishop(this.chessboard, end.getPiece().getPlayer());
                            break;
                        case "Valet":
                            piece = new Valet(this.chessboard, end.getPiece().getPlayer());
                            break;
                        default:
                            piece = new Knight(this.chessboard, end.getPiece().getPlayer());
                            break;
                    }
                    piece.setChessboard(end.getPiece().getChessboard());
                    piece.setPlayer(end.getPiece().getPlayer());
                    piece.setSquare(end.getPiece().getSquare());
                    end.piece = piece;
                    promotedPiece = end.piece;
                }
            }
        } else if (!end.getPiece().getName().equals("Pawn")) {
            this.chessboard.setTwoSquareMovedPawn(null); //erase last saved move (for En passant)
        }

        if (refresh) {
            this.chessboard.unselect();//unselect square
            this.chessboard.repaint();
        }

        if (clearForwardHistory) {
            this.movesHistoryView.clearMoveForwardStack();
            this.movesHistoryView.addMove(tempBegin, tempEnd, true, wasCastling, wasEnPassant, promotedPiece);
        } else {
            this.movesHistoryView.addMove(tempBegin, tempEnd, false, wasCastling, wasEnPassant, promotedPiece);
        }
        
    }/*endOf-move()-*/

    /**
     * Method to simulate Move to check if it's correct etc. (usable for network game).
     */
    public boolean simulateMove() {
        try {
            this.chessboard.select(begin);
            if (this.chessboard.getActiveSquare().getPiece().getAllMoves().contains(end)) //move
            {
              this.execute();
            } else {
                LOG.debug("Bad move: beginX: " + this.begin.getPozX() + " beginY: " +  this.begin.getPozY() + " endX: " +  this.end.getPozX() + " endY: " + this.end.getPozY());
                return false;
            }
            this.chessboard.unselect();
            this.chessboard.getGameEngine().nextMove();

            return true;

        } catch (StringIndexOutOfBoundsException exc) {
            LOG.error("StringIndexOutOfBoundsException: " + exc);
            return false;
        } catch (ArrayIndexOutOfBoundsException exc) {
            LOG.error("ArrayIndexOutOfBoundsException: " + exc);
            return false;
        } catch (NullPointerException exc) {
            LOG.error("NullPointerException: " + exc + " stack: " + exc.getStackTrace());
            return false;
        }
    }


    @Override
    public void execute() {
        move(this.begin, this.end, this.refresh, this.clearForwardHistory);
    }


}
