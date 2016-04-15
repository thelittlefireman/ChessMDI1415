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

import jchess.core.moves.Moves;
import jchess.display.views.chessboard.ChessboardView;
import jchess.display.views.chessboard.implementation.graphic2D.Chessboard2D;
import jchess.utils.Settings;
import org.apache.log4j.Logger;

import java.awt.event.ComponentEvent;

/**
 * @author: Mateusz Sławomir Lach ( matlak, msl )
 * @author: Damian Marciniak
 * Class to represent board. Chessboard is made from squares.
 * It is setting the squers of board and sets the pieces(pawns)
 * witch the owner is current player on it.
 */
public class Chessboard {
    protected static final int TOP = 0;
    protected static final int BOTTOM = 7;
    private static final Logger LOG = Logger.getLogger(Chessboard.class);


    private Board board;
    private Settings settings;

    /**
     * board view data object
     */
    private ChessboardView chessboardView;

    /**
     * Chessboard class constructor
     *
     * @param settings      reference to Settings class object for this board
     * @param moves_history reference to Moves class object for this board
     */
    public Chessboard(Settings settings, Moves moves_history) {
        this.settings = settings;
        this.chessboardView = new Chessboard2D(this);

        this.board = new Board(this, moves_history);

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





    /**
     * Method selecting piece in board
     *
     * @param sq square to select (when clicked))
     */
    public void select(Square sq) {
        this.board.select(sq);
        this.getChessboardView().repaint();
    }/*--endOf-select--*/

    public void unselect() {
        this.board.unSelect();

        this.getChessboardView().unselect();
        //this.getChessboardView().repaint();
    }/*--endOf-unselect--*/

    public void resetActiveSquare() {
        this.board.setActiveSquare(null);
    }


    public boolean redo() {
        return redo(true);
    }

    public boolean redo(boolean refresh) {
        if (this.getSettings().getGameType() == Settings.gameTypes.local) //redo only for local game
        {
            return this.board.redo();

        }
        return false;
    }

    public boolean undo() {
        return undo(true);
    }

    public Board getBoard() {
        return board;
    }

    public synchronized boolean undo(boolean refresh) //undo last move
    {
        boolean undoSucess = this.board.undo();
        if (undoSucess) {
            if (refresh) {
                refresh();

            }
        }
        return undoSucess;
    }

    public void refresh() {
        this.unselect();//unselect square
        repaint();
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


}
