/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jchess.display.views.chessboard;

import jchess.core.Chessboard;
import jchess.core.Square;
import jchess.utils.GUI;

import javax.swing.*;
import java.awt.*;

/*
 * Authors:
 * Mateusz Sławomir Lach ( matlak, msl )
 */
public abstract class ChessboardView extends JPanel
{
    /*
     * image x position (used in JChessView class!)
     */
    public static final int imgX = 5;
    /*
     * image y position (used in JChessView class!)
     */
    public static final int imgY = imgX;
    /*
     * image width
     */
    public static final int imgWidht = 480;
    /*
     * image height
     */
    public static final int imgHeight = imgWidht;
    /*
     * image of board
     */
    protected static final Image orgImage = GUI.loadImage("board.png");
    /*
     * image of highlited square
     */
    protected static final Image orgSelSquare = GUI.loadImage("sel_square.png");
    /*
     * image of square where piece can go
     */
    protected static final Image orgAbleSquare = GUI.loadImage("able_square.png");
    private static final int CENTER_POSITION = 3;
    /*
     * image of highlited square
     */
    protected static Image selSquare = orgSelSquare;
    /*
     * image of square where piece can go
     */
    protected static Image ableSquare = orgAbleSquare;
    /*
     * image of board
     */
    protected Image image = ChessboardView.orgImage;
    protected Image leftRightLabel = null;
    protected Point topLeft = new Point(0, 0);
    /*
     * height of square
     */
    protected float squareHeight;
    private Image upDownLabel = null;
    private Chessboard chessboard;
    
    abstract public Square getSquare(int clickedX, int clickedY);
    
    abstract public void unselect();
    
    abstract public int getChessboardWidht();
    
    abstract public int getChessboardHeight();

    abstract public int getChessboardWidht(boolean includeLables);

    abstract public int getChessboardHeight(boolean includeLabels);  
    
    abstract public int getSquareHeight();
    
    abstract public void resizeChessboard(int height);
    
    abstract public Point getTopLeftPoint();
    
    /**
     * Annotations to superclass JPanelGame updateing and painting the crossboard
     */
    @Override
    public void update(Graphics g)
    {
        repaint();
    }

    /**
     * @return the board
     */
    public Chessboard getChessboard()
    {
        return chessboard;
    }

    /**
     * @param chessboard the board to set
     */
    public void setChessboard(Chessboard chessboard)
    {
        this.chessboard = chessboard;
    }

    /**
     * @return the upDownLabel
     */
    public Image getUpDownLabel()
    {
        return upDownLabel;
    }

    /**
     * @param upDownLabel the upDownLabel to set
     */
    public void setUpDownLabel(Image upDownLabel)
    {
        this.upDownLabel = upDownLabel;
    }
    
    public int transposePosition(int squarePosition)
    {
        return transposePosition(squarePosition, CENTER_POSITION);
    }
    
    public int transposePosition(int squarePosition, int centerPosition)
    {
        return (-(squarePosition-centerPosition)) + centerPosition + 1;
    }
}
