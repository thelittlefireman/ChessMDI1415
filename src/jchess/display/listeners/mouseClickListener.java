package jchess.display.listeners;

import jchess.core.GameEngine;
import jchess.core.Square;
import jchess.core.pieces.implementation.King;
import jchess.utils.Settings;
import org.apache.log4j.Logger;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by Thomas on 15/04/2016.
 */
public class MouseClickListener implements MouseListener {

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(MouseClickListener.class);
    private GameEngine gameEngine;
    public MouseClickListener(GameEngine gameEngine){
        this.gameEngine=gameEngine;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
    }


    @Override
    public void mousePressed(MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON3) //right button
        {
            this.gameEngine.undo();
        } else if (event.getButton() == MouseEvent.BUTTON2 && gameEngine.getSettings().getGameType() == Settings.gameTypes.local) {
            this.gameEngine.redo();
        } else if (event.getButton() == MouseEvent.BUTTON1) //left button
        {

            if (!this.gameEngine.isBlockedChessboard()) {
                try {
                    int x = event.getX();//get X position of mouse
                    int y = event.getY();//get Y position of mouse

                    Square sq = this.gameEngine.getChessboard().getChessboardView().getSquare(x, y);
                    if ((sq == null && sq.piece == null && this.gameEngine.getChessboard().getBoard().getActiveSquare() == null)
                            || (this.gameEngine.getChessboard().getBoard().getActiveSquare() == null && sq.piece != null && sq.getPiece().getPlayer() != gameEngine.getActivePlayer())) {
                        return;
                    }

                    if (sq.piece != null && sq.getPiece().getPlayer() == this.gameEngine.getActivePlayer() && sq != this.gameEngine.getChessboard().getBoard().getActiveSquare()) {
                        this.gameEngine.getChessboard().unselect();
                        this.gameEngine.getChessboard().select(sq);
                    } else if (this.gameEngine.getChessboard().getBoard().getActiveSquare() == sq) //unselect
                    {
                        this.gameEngine.getChessboard().unselect();
                    } else if (this.gameEngine.getChessboard().getBoard().getActiveSquare() != null && this.gameEngine.getChessboard().getBoard().getActiveSquare().piece != null
                            && this.gameEngine.getChessboard().getBoard().getActiveSquare().getPiece().getAllMoves().contains(sq)) //move
                    {
                        if (gameEngine.getSettings().getGameType() == Settings.gameTypes.local) {
                            this.gameEngine.getChessboard().getBoard().move(this.gameEngine.getChessboard().getBoard().getActiveSquare(), sq);
                        }


                        this.gameEngine.getChessboard().unselect();

                        //switch player
                        gameEngine.nextMove();

                        //checkmate or stalemate
                        King king;
                        if (gameEngine.getActivePlayer() == gameEngine.getSettings().getPlayerWhite()) {
                            king = this.gameEngine.getChessboard().getBoard().getKingWhite();
                        } else {
                            king = this.gameEngine.getChessboard().getBoard().getKingBlack();
                        }

                        switch (king.isCheckmatedOrStalemated()) {
                            case 1:
                                gameEngine.endGame("Checkmate! " + king.getPlayer().getColor().toString() + " player lose!");
                                break;
                            case 2:
                                gameEngine.endGame("Stalemate! Draw!");
                                break;
                        }
                    }

                } catch (Exception exc) {
                    LOG.error("NullPointerException: " + exc.getMessage() + " stack: " + exc.getStackTrace());
                    gameEngine.getChessboard().repaint();
                    return;
                }
            } else if (gameEngine.isBlockedChessboard()) {
                LOG.debug("Chessboard is blocked");
            }
        }
        //board.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }


}
