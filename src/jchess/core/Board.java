package jchess.core;

import jchess.JChessApp;
import jchess.core.moves.Castling;
import jchess.core.moves.Move;
import jchess.core.moves.Moves;
import jchess.core.pieces.Piece;
import jchess.core.pieces.implementation.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Thomas on 14/04/2016.
 */
public class Board {
    private static final Logger LOG = Logger.getLogger(Board.class);
    //For En passant:
    //|-> Pawn whose in last turn moved two square
    protected Pawn twoSquareMovedPawn = null;
    protected Square squares[][];
    protected Square activeSquare;
    protected int activeSquareX;
    protected int activeSquareY;
    protected King kingWhite;
    protected King kingBlack;
    /*
     * squares of board
     */
    private Set<Square> moves;
    private jchess.core.moves.Moves Moves;
    private Chessboard chessboard;

    public Board(Chessboard chessboard, Moves moves_history) {
        this.chessboard = chessboard;
        this.Moves = moves_history;
        this.activeSquareX = 0;
        this.activeSquareY = 0;

        this.squares = new Square[8][8];//initalization of 8x8 board

        for (int i = 0; i < 8; i++) //create object for each square
        {
            for (int y = 0; y < 8; y++) {
                this.squares[i][y] = new Square(i, y, null);
            }
        }//--endOf--create object for each square
    }

    public static boolean wasEnPassant(Square sq) {
        return sq.getPiece() != null
                && sq.getPiece().getBoard().getTwoSquareMovedPawn() != null
                && sq == sq.getPiece().getBoard().getTwoSquareMovedPawn().getSquare();
    }

    public boolean redo() {
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
        return false;
    }

    /**
     * @return the kingWhite
     */
    public King getKingWhite() {
        return kingWhite;
    }

    /**
     * @return the kingBlack
     */
    public King getKingBlack() {
        return kingBlack;
    }

    /**
     * @return the twoSquareMovedPawn
     */
    public Pawn getTwoSquareMovedPawn() {
        return twoSquareMovedPawn;
    }

    public boolean undo() {
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


            } catch (ArrayIndexOutOfBoundsException | NullPointerException exc) {
                LOG.error("error: " + exc.getClass() + " exc object: " + exc);
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    public void select(Square sq) {
        this.setActiveSquare(sq);
        this.setActiveSquareX(sq.getPozX() + 1);
        this.setActiveSquareY(sq.getPozY() + 1);

        LOG.debug("active_x: " + this.getActiveSquareX() + " active_y: " + this.getActiveSquareY());//4tests
    }

    /**
     * Method setPieces on begin of new game or loaded game
     *
     * @param places  string with pieces to set on board
     * @param plWhite reference to white player
     * @param plBlack reference to black player
     */
    public void setPieces(String places, Player plWhite, Player plBlack) {

        if (places.equals("")) //if newGame
        {
            initGame(plWhite, plBlack);
        } else //if loadedGame
        {
            return;
        }
    }/*--endOf-setPieces--*/

    public void unSelect() {
        this.setActiveSquareX(0);
        this.setActiveSquareY(0);
        this.setActiveSquare(null);
    }

    public void initGame(Player plWhite, Player plBlack) {
        this.setFigures4NewGame(0, plWhite);
        this.setPawns4NewGame(1, plWhite);
        this.setFigures4NewGame(7, plBlack);
        this.setPawns4NewGame(6, plBlack);
    }

    /**
     * Method to set Figures in row (and set Queen and King to right position)
     *
     * @param i      row where to set figures (Rook, Knight etc.)
     * @param player which is owner of pawns
     */
    private void setFigures4NewGame(int i, Player player) {
        if (i != 0 && i != 7) {
            LOG.error("error setting figures like rook etc.");
            return;
        } else if (i == 0) {
            player.goDown = true;
        }

        this.getSquare(0, i).setPiece(new Rook(this, player));
        this.getSquare(7, i).setPiece(new Rook(this, player));
        this.getSquare(1, i).setPiece(new Knight(this, player));
        this.getSquare(6, i).setPiece(new Knight(this, player));
        this.getSquare(2, i).setPiece(new Bishop(this, player));
        this.getSquare(5, i).setPiece(new Bishop(this, player));


        this.getSquare(3, i).setPiece(new Queen(this, player));
        if (player.getColor() == Colors.WHITE) {
            kingWhite = new King(this, player);
            this.getSquare(4, i).setPiece(kingWhite);
        } else {
            kingBlack = new King(this, player);
            this.getSquare(4, i).setPiece(kingBlack);
        }
    }

    /**
     * method set Pawns in row
     *
     * @param i      row where to set pawns
     * @param player player which is owner of pawns
     */
    private void setPawns4NewGame(int i, Player player) {
        if (i != 1 && i != 6) {
            LOG.error("error setting pawns etc.");
            return;
        }
        for (int x = 0; x < 8; x++) {
            this.getSquare(x, i).setPiece(new Pawn(this, player));
        }
    }

    public void move(Square begin, Square end) {
        move(begin, end, true);
    }

    /**
     * Method to move piece over board
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
            fromSQ = squares[xFrom][yFrom];
            toSQ = squares[xTo][yTo];
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
     *
     * @param begin   square from which move piece
     * @param end     square where we want to move piece         *
     * @param refresh board, default: true
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
        switch (end.getPiece().getName()) {
            case "King":
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
                break;
            case "Rook":
                if (!((Rook) end.piece).getWasMotioned()) {
                    ((Rook) end.piece).setWasMotioned(true);
                }
                break;
            case "Pawn":
                if (getTwoSquareMovedPawn() != null && getSquare(end.getPozX(), begin.getPozY()) == getTwoSquareMovedPawn().getSquare()) //en passant
                {
                    tempEnd.piece = getSquare(end.getPozX(), begin.getPozY()).piece; //ugly hack - put taken pawn in en passant plasty do end square

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
                            default:
                                piece = new Knight(this, end.getPiece().getPlayer());
                                break;
                        }
                        piece.setBoard(end.getPiece().getBoard());
                        piece.setPlayer(end.getPiece().getPlayer());
                        piece.setSquare(end.getPiece().getSquare());
                        end.piece = piece;
                        promotedPiece = end.piece;
                    }
                }
                break;
        }


        if (!end.getPiece().getName().equals("Pawn")) {
            twoSquareMovedPawn = null; //erase last saved move (for En passant)
        }

        if (refresh) {
            this.chessboard.refresh();
        }

        if (clearForwardHistory) {
            this.Moves.clearMoveForwardStack();
            this.Moves.addMove(tempBegin, tempEnd, true, wasCastling, wasEnPassant, promotedPiece);
        } else {
            this.Moves.addMove(tempBegin, tempEnd, false, wasCastling, wasEnPassant, promotedPiece);
        }
    }/*endOf-move()-*/

    public Square[][] getSquares() {
        return squares;
    }

    /**
     * @return the squares
     */

    public Square getSquare(int x, int y) {
        try {
            return squares[x][y];
        } catch (ArrayIndexOutOfBoundsException exc) {
            exc.printStackTrace();
            return null;
        }
    }

    public ArrayList<Piece> getAllPieces(Colors color) {
        ArrayList<Piece> result = new ArrayList<>();
        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares[i].length; j++) {
                Square sq = squares[i][j];
                if (null != sq.getPiece() && (sq.getPiece().getPlayer().color == color || color == null)) {
                    result.add(sq.getPiece());
                }
            }
        }
        return result;
    }

    /**
     * @return the activeSquare
     */
    public Square getActiveSquare() {
        return activeSquare;
    }

    /**
     * @param activeSquare the activeSquare to set
     */
    public void setActiveSquare(Square activeSquare) {
        this.activeSquare = activeSquare;
    }

    /**
     * @return the activeSquareX
     */
    public int getActiveSquareX() {
        return activeSquareX;
    }

    /**
     * @param activeSquareX the activeSquareX to set
     */
    public void setActiveSquareX(int activeSquareX) {
        this.activeSquareX = activeSquareX;
    }

    /**
     * @return the activeSquareY
     */
    public int getActiveSquareY() {
        return activeSquareY;
    }

    /**
     * @param activeSquareY the activeSquareY to set
     */
    public void setActiveSquareY(int activeSquareY) {
        this.activeSquareY = activeSquareY;
    }

    /**
     * @return the moves
     */
    public Set<Square> getMoves() {
        return moves;
    }

    /**
     * @param moves the moves to set
     */
    public void setMoves(Set<Square> moves) {
        this.moves = moves;
    }
}
