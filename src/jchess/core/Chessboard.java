/*
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*

 */
package jchess.core;

import jchess.JChessApp;
import jchess.core.initialPlaceStrategy.BasePlacement;
import jchess.core.initialPlaceStrategy.InitialPlaceStrategy;
import jchess.core.moves.Castling;
import jchess.core.moves.Move;
import jchess.core.moves.Moves;
import jchess.core.pieces.Piece;
import jchess.core.pieces.implementation.*;
import jchess.core.visitorsPieces.VisitorPieceInterface;
import jchess.core.visitorsPieces.VisitorPieces;
import jchess.display.views.chessboard.ChessboardView;
import jchess.display.views.chessboard.implementation.graphic2D.Chessboard2D;
import jchess.utils.Settings;
import org.apache.log4j.Logger;

import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author: Mateusz Sławomir Lach ( matlak, msl )
 * @author: Damian Marciniak
 * Class to represent chessboard. Chessboard is made from squares.
 * It is setting the squers of chessboard and sets the pieces(pawns)
 * witch the owner is current player on it.
 */
public class Chessboard implements ChessboardParts {
    protected static final int TOP = 0;
    protected static final int BOTTOM = 7;
    private InitialPlaceStrategy initialPlaceStrategy;
    private static final Logger LOG = Logger.getLogger(Chessboard.class);
    /*
     * squares of chessboard
     */
    protected Square squares[][];
    protected King kingWhite;
    protected King kingBlack;
    //For En passant:
    //|-> Pawn whose in last turn moved two square
    protected Pawn twoSquareMovedPawn = null;
    protected Square activeSquare;
    protected int activeSquareX;

    public void setKingWhite(King kingWhite) {
        this.kingWhite = kingWhite;
    }

    public void setKingBlack(King kingBlack) {
        this.kingBlack = kingBlack;
    }

    protected int activeSquareY;
    private Set<Square> moves;
    private Settings settings;
    private Moves Moves;
    /**
     * chessboard view data object
     */
    private ChessboardView chessboardView;

    public InitialPlaceStrategy getInitialPlaceStrategy() {
        return initialPlaceStrategy;
    }

    /**
     * Chessboard class constructor
     *
     * @param settings      reference to Settings class object for this chessboard
     * @param moves_history reference to Moves class object for this chessboard
     */
    public Chessboard(Settings settings, Moves moves_history) {
        this.settings = settings;
        this.chessboardView = new Chessboard2D(this);

        this.activeSquareX = 0;
        this.activeSquareY = 0;


        //TODO Ici qu'on définit la taille du chessborard
        this.squares = new Square[this.settings.getSizeN()][this.settings.getSizeM()];//initalization of 8x8 chessboard

        for (int i = 0; i < this.settings.getSizeN(); i++) //create object for each square
        {
            for (int y = 0; y < this.settings.getSizeM(); y++) {
                this.squares[i][y] = new Square(i, y, null);
            }
        }//--endOf--create object for each square
        this.Moves = moves_history;
        this.initialPlaceStrategy = new BasePlacement(this);
    }/*--endOf-Chessboard--*/

    /**
     * @return the top
     */
    public static int getTop() {
        return TOP;
    }

    /**
     * @return the bottom
     */
    public static int getBottom() {
        return BOTTOM;
    }

    public static boolean wasEnPassant(Square sq) {
        return sq.getPiece() != null
                && sq.getPiece().getChessboard().getTwoSquareMovedPawn() != null
                && sq == sq.getPiece().getChessboard().getTwoSquareMovedPawn().getSquare();
    }

    /**
     * Method selecting piece in chessboard
     *
     * @param sq square to select (when clicked))
     */
    public void select(Square sq) {
        this.setActiveSquare(sq);
        this.setActiveSquareX(sq.getPozX() + 1);
        this.setActiveSquareY(sq.getPozY() + 1);

        LOG.debug("active_x: " + this.getActiveSquareX() + " active_y: " + this.getActiveSquareY());//4tests
        this.getChessboardView().repaint();
    }/*--endOf-select--*/

    public void unselect() {
        this.setActiveSquareX(0);
        this.setActiveSquareY(0);
        this.setActiveSquare(null);

        this.getChessboardView().unselect();
    }/*--endOf-unselect--*/

    public void resetActiveSquare() {
        this.setActiveSquare(null);
    }



    @Override
    public void accept(VisitorPieceInterface visitorPieceInterface) {
        for (int i = 0; i < settings.getSizeN() - 1; i++) {
            for (int j = 0; j < settings.getSizeM() - 1; j++) {
                Piece pieces = squares[i][j].getPiece();
                pieces.accept(visitorPieceInterface);
            }
        }
    }


    public boolean redo() {
        return redo(true);
    }


    public boolean undo() {
        return undo(true);
    }


    public void componentMoved(ComponentEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void componentShown(ComponentEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void componentHidden(ComponentEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the squares
     */
    public Square[][] getSquares() {
        return squares;
    }

    public Square getSquare(int x, int y) {
        try {
            return squares[x][y];
        } catch (ArrayIndexOutOfBoundsException exc) {
            return null;
        }
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

    /**
     * @return the chessboardView
     */
    public ChessboardView getChessboardView() {
        return chessboardView;
    }

    /**
     * @param chessboardView the chessboardView to set
     */
    public void setChessboardView(ChessboardView chessboardView) {
        this.chessboardView = chessboardView;
    }

    public void repaint() {
        getChessboardView().repaint();
    }

    /**
     * @return the settings
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * @param settings the settings to set
     */
    public void setSettings(Settings settings) {
        this.settings = settings;
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


}
