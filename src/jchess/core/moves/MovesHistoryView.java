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
 * Authors:
 * Mateusz Sławomir Lach ( matlak, msl )
 * Damian Marciniak
 */
package jchess.core.moves;

import jchess.core.Chessboard;
import jchess.core.utils.Colors;
import jchess.core.GameEngine;
import jchess.core.Square;
import jchess.core.commands.MoveCommandsBuilder;
import jchess.core.pieces.Piece;
import jchess.core.utils.timePerStroke.TimePerStrokeSave;
import jchess.core.utils.timePerStroke.TimePerStrokeSaveWithCommentary;
import jchess.utils.Settings;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Set;
import java.util.Stack;

/**
 * Class representing the players moves, it's also checking
 * that the moves taken by player are correct.
 * All moves which was taken by current player are saving as List of Strings
 * The history of moves is printing in a table
 */
public class MovesHistoryView extends AbstractTableModel {
    private static final Logger LOG = Logger.getLogger(MovesHistoryView.class);
    protected Stack<Move> moveBackStack = new Stack<Move>();
    protected Stack<Move> moveForwardStack = new Stack<Move>();
    private ArrayList<String> strokeList = new ArrayList<String>();
    private int columnsNum = 3;
    private int rowsNum = 0;
    private String[] names = new String[]
            {
                    Settings.lang("white"), Settings.lang("black")
            };
    private MyDefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JTable table;
    private boolean enterBlack = false;
    private GameEngine gameEngine;

    public MovesHistoryView(GameEngine gameEngine) {
        super();
        this.tableModel = new MyDefaultTableModel();
        this.table = new JTable(this.tableModel);
        this.scrollPane = new JScrollPane(this.table);
        this.scrollPane.setMaximumSize(new Dimension(100, 100));
        this.table.setMinimumSize(new Dimension(100, 100));
        this.gameEngine = gameEngine;

        this.tableModel.addColumn(this.names[0]);
        this.tableModel.addColumn(this.names[1]);
        this.addTableModelListener(null);
        this.tableModel.addTableModelListener(null);
        this.scrollPane.setAutoscrolls(true);
    }

    /**
     * Method with is checking is the strokeList is correct
     *
     * @param move String which in is capt player strokeList
     * @return boolean 1 if the strokeList is correct, else 0
     */
    static public boolean isMoveCorrect(String move) {
        if (move.equals(Castling.SHORT_CASTLING.getSymbol()) || move.equals(Castling.LONG_CASTLING.getSymbol())) {
            return true;
        }
        try {
            int from = 0;
            int sign = move.charAt(from);//get First
            switch (sign)  //if sign of piece, get next
            {
                case 66: // B like Bishop
                case 75: // K like King
                case 78: // N like Knight
                case 81: // Q like Queen
                case 82:
                    from = 1;
                    break; // R like Rook
            }
            sign = move.charAt(from);
            LOG.debug("isMoveCorrect/sign: " + sign);
            if (sign < 97 || sign > 104) //if lower than 'a' or higher than 'h'
            {
                return false;
            }
            sign = move.charAt(from + 1);
            if (sign < 49 || sign > 56) //if lower than '1' or higher than '8'
            {
                return false;
            }
            if (move.length() > 3) //if is equal to 3 or lower, than it's in short notation, no more checking needed
            {
                sign = move.charAt(from + 2);
                if (sign != 45 && sign != 120) //if isn't '-' and 'x'
                {
                    return false;
                }
                sign = move.charAt(from + 3);
                if (sign < 97 || sign > 104) //if lower than 'a' or higher than 'h'
                {
                    return false;
                }
                sign = move.charAt(from + 4);
                if (sign < 49 || sign > 56) //if lower than '1' or higher than '8'
                {
                    return false;
                }
            }
        } catch (StringIndexOutOfBoundsException exc) {
            LOG.error("isMoveCorrect/StringIndexOutOfBoundsException: " + exc);
            return false;
        }

        return true;
    }

    public void draw() {
    }

    @Override
    public String getValueAt(int x, int y) {
        return this.strokeList.get((y * 2) - 1 + (x - 1));
    }

    @Override
    public int getRowCount() {
        return this.rowsNum;
    }

    @Override
    public int getColumnCount() {
        return this.columnsNum;
    }

    protected void addRow() {
        this.tableModel.addRow(new String[2]);
    }

    protected void addCastling(String move) {
        this.strokeList.remove(this.strokeList.size() - 1);//remove last element (strokeList of Rook)
        if (!this.enterBlack) {
            this.tableModel.setValueAt(move, this.tableModel.getRowCount() - 1, 1);//replace last value
        } else {
            this.tableModel.setValueAt(move, this.tableModel.getRowCount() - 1, 0);//replace last value
        }
        this.addNewStroke(move);//add new strokeList (O-O or O-O-O)
    }

    @Override
    public boolean isCellEditable(int a, int b) {
        return false;
    }

    /**
     * Method of adding new moves to the table
     *
     * @param str String which in is saved player strokeList
     */
    protected void addMove2Table(String str) {
        try {
            if (!this.enterBlack) {
                this.addRow();
                this.rowsNum = this.tableModel.getRowCount() - 1;
                this.tableModel.setValueAt(str, rowsNum, 0);
            } else {
                this.tableModel.setValueAt(str, rowsNum, 1);
                this.rowsNum = this.tableModel.getRowCount() - 1;
            }
            this.enterBlack = !this.enterBlack;
            this.table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));//scroll to down

        } catch (ArrayIndexOutOfBoundsException exc) {
            if (this.rowsNum > 0) {
                this.rowsNum--;
                addMove2Table(str);
            }
        }
    }

    /**
     * Method of adding new strokeList
     *
     * @param move String which in is capt player strokeList
     */
    public void addMove(String move) {
        if (isMoveCorrect(move)) {

            this.addMove2Table(this.addNewStroke(move));
            this.moveForwardStack.clear();
        }

    }

    public void addMove(Square begin, Square end, boolean registerInHistory, Castling castlingMove, boolean wasEnPassant, Piece promotedPiece) {
        boolean wasCastling = castlingMove != Castling.NONE;
        String locMove = begin.getPiece().getSymbol();

        if (gameEngine.getSettings().isUpsideDown()) {
            locMove += Character.toString((char) ((Chessboard.getBottom() - begin.getPozX()) + 97));//add letter of Square from which strokeList was made
            locMove += Integer.toString(begin.getPozY() + 1);//add number of Square from which strokeList was made
        } else {
            locMove += Character.toString((char) (begin.getPozX() + 97));//add letter of Square from which strokeList was made
            locMove += Integer.toString(8 - begin.getPozY());//add number of Square from which strokeList was made
        }

        if (end.piece != null) {
            locMove += "x";//take down opponent piece
        } else {
            locMove += "-";//normal strokeList
        }

        if (gameEngine.getSettings().isUpsideDown()) {
            locMove += Character.toString((char) ((Chessboard.getBottom() - end.getPozX()) + 97));//add letter of Square to which strokeList was made
            locMove += Integer.toString(end.getPozY() + 1);//add number of Square to which strokeList was made
        } else {
            locMove += Character.toString((char) (end.getPozX() + 97));//add letter of Square to which strokeList was made
            locMove += Integer.toString(8 - end.getPozY());//add number of Square to which strokeList was made
        }

        if (begin.getPiece().getSymbol().equals("") && begin.getPozX() - end.getPozX() != 0 && end.piece == null) {
            locMove += "(e.p)";//pawn take down opponent en passant
            wasEnPassant = true;
        }
        if ((!this.enterBlack && this.gameEngine.getChessboard().getKingBlack().isChecked())
                || (this.enterBlack && this.gameEngine.getChessboard().getKingWhite().isChecked())) {//if checked

            if ((!this.enterBlack && this.gameEngine.getChessboard().getKingBlack().isCheckmatedOrStalemated() == 1)
                    || (this.enterBlack && this.gameEngine.getChessboard().getKingWhite().isCheckmatedOrStalemated() == 1)) {//check if checkmated
                locMove += "#";//check mate
            } else {
                locMove += "+";//check
            }
        }
        if (castlingMove != Castling.NONE) {
            this.addCastling(castlingMove.getSymbol());
        } else {

            this.addMove2Table(this.addNewStroke(locMove));
        }
        this.scrollPane.scrollRectToVisible(new Rectangle(0, this.scrollPane.getHeight() - 2, 1, 1));

        if (registerInHistory) {
            Move moveToAdd = new Move(new Square(begin), new Square(end), begin.piece, end.piece, castlingMove, wasEnPassant, promotedPiece);
            this.moveBackStack.add(moveToAdd);
        }
    }

    public String addNewStroke(String stroke) {
        int time;
        if (this.gameEngine.getActivePlayer() == this.gameEngine.getSettings().getPlayerWhite()) {
            time = this.gameEngine.getjPanelGame().getJPanelGameClock().getWhite_clock().getDecrementActualNumber();
        } else {

            time = this.gameEngine.getjPanelGame().getJPanelGameClock().getBlack_clock().getDecrementActualNumber();
        }
        TimePerStrokeSave timePerStrokeSave = new TimePerStrokeSave(time);
        if (!this.gameEngine.getjPanelGame().getCommentary().getText().equals("")) {
            timePerStrokeSave = new TimePerStrokeSaveWithCommentary(timePerStrokeSave, this.gameEngine.getjPanelGame().getCommentary().getText());
            this.gameEngine.getjPanelGame().getCommentary().reset();
        }
        this.gameEngine.getActivePlayer().getTimePerStrokeSaveList().add(timePerStrokeSave);
        stroke = stroke + " " + timePerStrokeSave.getTimeStrokeInformation();
        this.strokeList.add(stroke);
        return stroke;
    }

    public void clearMoveForwardStack() {
        this.moveForwardStack.clear();
    }

    public JScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public ArrayList<String> getMoves() {
        return this.strokeList;
    }

    /**
     * Method to set all moves from String with validation test (usefoul for network gameEngine)
     *
     * @param moves String to set in String like PGN with full-notation format
     */
    public void setMoves(String moves) {
        int from = 0;
        int to = 0;
        int n = 1;
        ArrayList<String> tempArray = new ArrayList();
        int tempStrSize = moves.length() - 1;
        while (true) {
            from = moves.indexOf(" ", from);
            to = moves.indexOf(" ", from + 1);
            try {
                tempArray.add(moves.substring(from + 1, to).trim());
            } catch (StringIndexOutOfBoundsException exc) {
                LOG.error("setMoves/StringIndexOutOfBoundsException: error parsing file to load: " + exc);
                break;
            }
            if (n % 2 == 0) {
                from = moves.indexOf(".", to);
                if (from < to) {
                    break;
                }
            } else {
                from = to;
            }
            n += 1;
            if (from > tempStrSize || to > tempStrSize) {
                break;
            }
        }
        for (String locMove : tempArray) //test if moves are written correctly
        {
            if (!MovesHistoryView.isMoveCorrect(locMove.trim())) //if not
            {
                JOptionPane.showMessageDialog(this.gameEngine.getjPanelGame(), Settings.lang("invalid_file_to_load") + strokeList);
                return;//show message and finish reading gameEngine
            }
        }
        boolean canMove = false;
        for (String locMove : tempArray) {
            if (Castling.isCastling(locMove)) //if castling
            {
                int[] values = new int[4];
                if (locMove.equals(Castling.LONG_CASTLING.getSymbol())) {
                    if (this.gameEngine.getActivePlayer().getColor() == Colors.BLACK) //if black turn
                    {
                        values = new int[]
                                {
                                        4, 0, 2, 0
                                };//strokeList value for castling (King strokeList)
                    } else {
                        values = new int[]
                                {
                                        4, 7, 2, 7
                                };//strokeList value for castling (King strokeList)
                    }
                } else if (locMove.equals(Castling.SHORT_CASTLING.getSymbol())) //if short castling
                {
                    if (this.gameEngine.getActivePlayer().getColor() == Colors.BLACK) //if black turn
                    {
                        values = new int[]
                                {
                                        4, 0, 6, 0
                                };//strokeList value for castling (King strokeList)
                    } else {
                        values = new int[]
                                {
                                        4, 7, 6, 7
                                };//strokeList value for castling (King strokeList)
                    }
                }
                canMove = MoveCommandsBuilder.load(this.gameEngine.getChessboard()).xFrom(values[0]).yFrom(values[1]).xTo(values[2]).yTo(values[3]).buildMoveCommands().simulateMove();

                if (!canMove) //if strokeList is illegal
                {
                    JOptionPane.showMessageDialog(this.gameEngine.getjPanelGame(), Settings.lang("illegal_move_on") + locMove);
                    return;//finish reading gameEngine and show message
                }
                continue;
            }
            from = 0;
            int num = locMove.charAt(from);
            if (num <= 90 && num >= 65) {
                from = 1;
            }
            int xFrom = 9; //set to higher value than chessboard has fields, to cause error if piece won't be found
            int yFrom = 9;
            int xTo = 9;
            int yTo = 9;
            boolean pieceFound = false;
            if (locMove.length() <= 3) {
                Square[][] squares = this.gameEngine.getChessboard().getSquares();
                xTo = locMove.charAt(from) - 97;//from ASCII
                yTo = Chessboard.getBottom() - (locMove.charAt(from + 1) - 49);//from ASCII
                for (int i = 0; i < squares.length && !pieceFound; i++) {
                    for (int j = 0; j < squares[i].length && !pieceFound; j++) {
                        if (squares[i][j].piece == null || this.gameEngine.getActivePlayer().getColor() != squares[i][j].getPiece().getPlayer().getColor()) {
                            continue;
                        }
                        Set<Square> pieceMoves = squares[i][j].getPiece().getAllMoves();
                        for (Object square : pieceMoves) {
                            Square currSquare = (Square) square;
                            if (currSquare.getPozX() == xTo && currSquare.getPozY() == yTo) {
                                xFrom = squares[i][j].getPiece().getSquare().getPozX();
                                yFrom = squares[i][j].getPiece().getSquare().getPozY();
                                pieceFound = true;
                            }
                        }
                    }
                }
            } else {
                xFrom = locMove.charAt(from) - 97;//from ASCII
                yFrom = Chessboard.getBottom() - (locMove.charAt(from + 1) - 49);//from ASCII
                xTo = locMove.charAt(from + 3) - 97;//from ASCII
                yTo = Chessboard.getBottom() - (locMove.charAt(from + 4) - 49);//from ASCII
            }
            canMove = MoveCommandsBuilder.load(this.gameEngine.getChessboard()).xFrom(xFrom).yFrom(yFrom).xTo(xTo).yTo(yTo).buildMoveCommands().simulateMove();
            if (!canMove) //if strokeList is illegal
            {
                JOptionPane.showMessageDialog(this.gameEngine.getjPanelGame(), Settings.lang("illegal_move_on") + locMove);
                this.gameEngine.getChessboard().resetActiveSquare();
                return;//finish reading gameEngine and show message
            }
        }
    }

    public synchronized Move getLastMoveFromHistory() {
        try {
            Move last = this.moveBackStack.get(this.moveBackStack.size() - 1);
            return last;
        } catch (java.lang.ArrayIndexOutOfBoundsException exc) {
            return null;
        }
    }

    public synchronized Move getNextMoveFromHistory() {
        try {
            Move next = this.moveForwardStack.get(this.moveForwardStack.size() - 1);
            return next;
        } catch (ArrayIndexOutOfBoundsException exc) {
            LOG.error("ArrayIndexOutOfBoundsException: " + exc);
            return null;
        }

    }

    public synchronized Move undo() {
        try {
            Move last = this.moveBackStack.pop();
            if (last != null) {
                if (this.gameEngine.getSettings().getGameType() == Settings.gameTypes.local) //moveForward / redo available only for local gameEngine
                {
                    this.moveForwardStack.push(last);
                }
                if (this.enterBlack) {
                    this.tableModel.setValueAt("", this.tableModel.getRowCount() - 1, 0);
                    this.tableModel.removeRow(this.tableModel.getRowCount() - 1);

                    if (this.rowsNum > 0) {
                        this.rowsNum--;
                    }
                } else {
                    if (this.tableModel.getRowCount() > 0) {
                        this.tableModel.setValueAt("", this.tableModel.getRowCount() - 1, 1);
                    }
                }
                this.strokeList.remove(this.strokeList.size() - 1);
                this.enterBlack = !this.enterBlack;
            }
            return last;
        } catch (EmptyStackException exc) {
            LOG.error("EmptyStackException: " + exc);
            this.enterBlack = false;
            return null;
        } catch (ArrayIndexOutOfBoundsException exc) {
            LOG.error("ArrayIndexOutOfBoundsException: " + exc);
            return null;
        }
    }

    public synchronized Move redo() {
        try {
            if (this.gameEngine.getSettings().getGameType() == Settings.gameTypes.local) {
                Move first = this.moveForwardStack.pop();
                this.moveBackStack.push(first);

                return first;
            }
            return null;
        } catch (EmptyStackException exc) {
            LOG.error("redo: EmptyStackException: " + exc);
            return null;
        }

    }

    public void addMoves(ArrayList<String> list) {
        for (String singleMove : list) {
            if (isMoveCorrect(singleMove)) {
                this.addMove(singleMove);
            }
        }
    }

    /**
     * Method of getting the moves in string
     *
     * @return str String which in is capt player strokeList
     */
    public String getMovesInString() {
        int n = 1;
        int i = 0;
        String str = new String();
        for (String locMove : this.getMoves()) {
            if (i % 2 == 0) {
                str += n + ". ";
                n += 1;
            }
            str += locMove + " ";
            i += 1;
        }
        return str;
    }
}
/*
 * Overriding DefaultTableModel and  isCellEditable method
 * (history cannot be edited by player)
 */

class MyDefaultTableModel extends DefaultTableModel {

    MyDefaultTableModel() {
        super();
    }

    @Override
    public boolean isCellEditable(int a, int b) {
        return false;
    }
}