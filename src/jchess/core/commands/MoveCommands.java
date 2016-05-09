package jchess.core.commands;

import jchess.JChessApp;
import jchess.core.Square;
import jchess.core.moves.Castling;
import jchess.core.moves.Move;
import jchess.core.pieces.Piece;
import jchess.core.pieces.implementation.*;
import jchess.utils.Settings;

/**
 * Created by thoma on 09/05/2016.
 */
public class MoveCommands implements CommandInterface {
    public void move(Square begin, Square end) {
        move(begin, end, true);
    }

    /**
     * Method to move piece over chessboard
     *
     * @param xFrom from which x move piece
     * @param yFrom from which y move piece
     * @param xTo   to which x move piece
     * @param yTo   to which y move piece
     */
    public void move(int xFrom, int yFrom, int xTo, int yTo) {
        Square fromSQ = null;
        Square toSQ = null;
        try {
            fromSQ = this.getSquares()[xFrom][yFrom];
            toSQ = this.getSquares()[xTo][yTo];
        } catch (java.lang.IndexOutOfBoundsException exc) {
            LOG.error("error moving piece: " + exc.getMessage());
            return;
        }
        this.move(fromSQ, toSQ, true);
    }

    public void move(Square begin, Square end, boolean refresh) {
        this.move(begin, end, refresh, true);
    }

    /**
     * Method move piece from square to square
     * //TODO EXERCICE 4
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

        Square tempBegin = new Square(begin);//4 moves history
        Square tempEnd = new Square(end);  //4 moves history

        begin.getPiece().setSquare(end);//set square of piece to ending
        end.piece = begin.piece;//for ending square set piece from beginin square
        begin.piece = null;//make null piece for begining square

        if (end.getPiece().getName().equals("King")) {
            if (!((King) end.piece).getWasMotioned()) {
                ((King) end.piece).setWasMotioned(true);
            }

            //Castling
            if (begin.getPozX() + 2 == end.getPozX()) {
                move(getSquare(7, begin.getPozY()), getSquare(end.getPozX() - 1, begin.getPozY()), false, false);
                wasCastling = Castling.SHORT_CASTLING;
            } else if (begin.getPozX() - 2 == end.getPozX()) {
                move(getSquare(0, begin.getPozY()), getSquare(end.getPozX() + 1, begin.getPozY()), false, false);
                wasCastling = Castling.LONG_CASTLING;
            }
            //endOf Castling
        } else if (end.getPiece().getName().equals("Rook")) {
            if (!((Rook) end.piece).getWasMotioned()) {
                ((Rook) end.piece).setWasMotioned(true);
            }
        } else if (end.getPiece().getName().equals("Pawn")) {
            if (getTwoSquareMovedPawn() != null && getSquares()[end.getPozX()][begin.getPozY()] == getTwoSquareMovedPawn().getSquare()) //en passant
            {
                tempEnd.piece = getSquares()[end.getPozX()][begin.getPozY()].piece; //ugly hack - put taken pawn in en passant plasty do end square

                squares[end.pozX][begin.pozY].piece = null;
                wasEnPassant = true;
            }

            if (begin.getPozY() - end.getPozY() == 2 || end.getPozY() - begin.getPozY() == 2) //moved two square
            {
                twoSquareMovedPawn = (Pawn) end.piece;
            } else {
                twoSquareMovedPawn = null; //erase last saved move (for En passant)
            }

            if (end.getPiece().getSquare().getPozY() == 0 || end.getPiece().getSquare().getPozY() == 7) //promote Pawn
            {
                if (clearForwardHistory) {
                    String color = String.valueOf(end.getPiece().getPlayer().getColor().getSymbolAsString().toUpperCase());
                    String newPiece = JChessApp.getJavaChessView().showPawnPromotionBox(color); //return name of new piece

                    Piece piece;
                    switch (newPiece) {
                        case "Queen":
                            piece = new Queen(this, end.getPiece().getPlayer());
                            break;
                        case "Rook":
                            piece = new Rook(this, end.getPiece().getPlayer());
                            break;
                        case "Bishop":
                            piece = new Bishop(this, end.getPiece().getPlayer());
                            break;
                        case "Valet":
                            piece = new Valet(this, end.getPiece().getPlayer());
                            break;
                        default:
                            piece = new Knight(this, end.getPiece().getPlayer());
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
            twoSquareMovedPawn = null; //erase last saved move (for En passant)
        }

        if (refresh) {
            this.unselect();//unselect square
            repaint();
        }

        if (clearForwardHistory) {
            this.Moves.clearMoveForwardStack();
            this.Moves.addMove(tempBegin, tempEnd, true, wasCastling, wasEnPassant, promotedPiece);
        } else {
            this.Moves.addMove(tempBegin, tempEnd, false, wasCastling, wasEnPassant, promotedPiece);
        }
    }/*endOf-move()-*/


    @Override
    public void execute() {

    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }

    public boolean redo(boolean refresh) {
        if (this.getSettings().getGameType() == Settings.gameTypes.local) //redo only for local game
        {
            Move first = this.Moves.redo();

            Square from = null;
            Square to = null;

            if (first != null) {
                from = first.getFrom();
                to = first.getTo();

                this.move(this.getSquares()[from.getPozX()][from.getPozY()], this.getSquares()[to.getPozX()][to.getPozY()], true, false);
                if (first.getPromotedPiece() != null) {
                    Pawn pawn = (Pawn) this.getSquares()[to.getPozX()][to.getPozY()].piece;
                    pawn.setSquare(null);

                    this.squares[to.pozX][to.pozY].piece = first.getPromotedPiece();
                    Piece promoted = this.getSquares()[to.getPozX()][to.getPozY()].piece;
                    promoted.setSquare(this.getSquares()[to.getPozX()][to.getPozY()]);
                }
                return true;
            }

        }
        return false;
    }

    public synchronized boolean undo(boolean refresh) //undo last move
    {
        Move last = this.Moves.undo();

        if (last != null && last.getFrom() != null) {
            Square begin = last.getFrom();
            Square end = last.getTo();
            try {
                Piece moved = last.getMovedPiece();
                this.squares[begin.pozX][begin.pozY].piece = moved;

                moved.setSquare(this.getSquares()[begin.getPozX()][begin.getPozY()]);

                Piece taken = last.getTakenPiece();
                if (last.getCastlingMove() != Castling.NONE) {
                    Piece rook = null;
                    if (last.getCastlingMove() == Castling.SHORT_CASTLING) {
                        rook = this.getSquares()[end.getPozX() - 1][end.getPozY()].piece;
                        this.squares[7][begin.pozY].piece = rook;
                        rook.setSquare(this.getSquares()[7][begin.getPozY()]);
                        this.squares[end.pozX - 1][end.pozY].piece = null;
                    } else {
                        rook = this.getSquares()[end.getPozX() + 1][end.getPozY()].piece;
                        this.squares[0][begin.pozY].piece = rook;
                        rook.setSquare(this.getSquares()[0][begin.getPozY()]);
                        this.squares[end.pozX + 1][end.pozY].piece = null;
                    }
                    ((King) moved).setWasMotioned(false);
                    ((Rook) rook).setWasMotioned(false);
                } else if (moved.getName().equals("Rook")) {
                    ((Rook) moved).setWasMotioned(false);
                } else if (moved.getName().equals("Pawn") && last.wasEnPassant()) {
                    Pawn pawn = (Pawn) last.getTakenPiece();
                    this.squares[end.pozX][begin.pozY].piece = pawn;
                    pawn.setSquare(this.getSquares()[end.getPozX()][begin.getPozY()]);

                } else if (moved.getName().equals("Pawn") && last.getPromotedPiece() != null) {
                    Piece promoted = this.getSquares()[end.getPozX()][end.getPozY()].piece;
                    promoted.setSquare(null);
                    this.squares[end.pozX][end.pozY].piece = null;
                }

                //check one more move back for en passant
                Move oneMoveEarlier = this.Moves.getLastMoveFromHistory();
                if (oneMoveEarlier != null && oneMoveEarlier.wasPawnTwoFieldsMove()) {
                    Piece canBeTakenEnPassant = this.getSquare(oneMoveEarlier.getTo().getPozX(), oneMoveEarlier.getTo().getPozY()).getPiece();
                    if (canBeTakenEnPassant.getName().equals("Pawn")) {
                        this.twoSquareMovedPawn = (Pawn) canBeTakenEnPassant;
                    }
                }

                if (taken != null && !last.wasEnPassant()) {
                    this.squares[end.pozX][end.pozY].piece = taken;
                    taken.setSquare(this.getSquares()[end.getPozX()][end.getPozY()]);
                } else {
                    this.squares[end.pozX][end.pozY].piece = null;
                }

                if (refresh) {
                    this.unselect();//unselect square
                    repaint();
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
