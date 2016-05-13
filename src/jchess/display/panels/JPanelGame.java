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

package jchess.display.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import jchess.JChessApp;
import jchess.core.Chessboard;
import jchess.core.GameEngine;
import jchess.core.Square;
import jchess.core.commands.MoveCommands;
import jchess.core.players.Player;
import jchess.display.components.HintTextArea;
import jchess.display.views.chessboard.ChessboardView;
import jchess.utils.Settings;

import org.apache.log4j.Logger;


/**
 * @author: Mateusz Sławomir Lach ( matlak, msl )
 * @author: Damian Marciniak
 * Class responsible for the starts of new games, loading games,
 * saving it, and for ending it.
 * This class is also responsible for appoing player with have
 * a move at the moment
 */
public class JPanelGame extends JPanel implements MouseListener {

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(JPanelGame.class);
    protected LocalSettingsView localSettingsView;
    private boolean firstPlay = true;
    private HintTextArea commentary;
    protected GameEngine gameEngine;
    /**
     * JPanelGame clock object
     */
    protected jchess.display.panels.JPanelGameClock JPanelGameClock;

    public HintTextArea getCommentary() {
        return commentary;
    }

    public JPanelGame(GameEngine gameEngine) {
        this.setLayout(new BorderLayout());
        
        
        this.gameEngine = gameEngine;

        ChessboardView chessboardView = gameEngine.getChessboard().getChessboardView();
        chessboardView.setPreferredSize(chessboardView.getSize());
        this.add(chessboardView, BorderLayout.WEST);

        //this.chessboard.
        JPanel panelControl = new JPanel();
        JPanelGameClock = new JPanelGameClock(this);
        JPanelGameClock.setPreferredSize(new Dimension(200, 100));
        panelControl.add(JPanelGameClock);

        JScrollPane Moves = gameEngine.getCommandsManager().getMovesHistoryView().getScrollPane();
        Moves.setPreferredSize(new Dimension(200, 250));
        panelControl.add(Moves);
        
        commentary = new HintTextArea("Commentez votre tour");
        commentary.setPreferredSize(new Dimension(200, 50));
        panelControl.add(commentary);

        
        this.localSettingsView = new LocalSettingsView(gameEngine);
        localSettingsView.setBorder(BorderFactory.createTitledBorder(Settings.lang("game_settings")));
        this.localSettingsView.setPreferredSize(new Dimension(200, 200));
        panelControl.add(localSettingsView);

        this.add(panelControl, BorderLayout.CENTER);
        this.setDoubleBuffered(true);
        chessboardView.addMouseListener(this);
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    public void newGame() {
        gameEngine.getInitialPlaceStrategy().setPieces("");

        gameEngine.setActivePlayer(gameEngine.getSettings().getPlayerWhite());
        if (gameEngine.getActivePlayer().getPlayerType() != Player.playerTypes.localUser) {
            gameEngine.setBlockedChessboard(true);
        }
        //dirty hacks starts over here :) 
        //to fix rendering artefacts on first run
        JPanelGame activeJPanelGame = JChessApp.getJavaChessView().getActiveTabGame();
        if (null != activeJPanelGame) {
            Chessboard chessboard = activeJPanelGame.getGameEngine().getChessboard();
            ChessboardView chessboardView = chessboard.getChessboardView();
            if (JChessApp.getJavaChessView().getNumberOfOpenedTabs() == 0) {
                chessboardView.resizeChessboard(chessboardView.getChessboardHeight(false));
                chessboard.repaint();
                activeJPanelGame.repaint();
            }
        }
        gameEngine.getChessboard().repaint();
        this.repaint();
        //dirty hacks ends over here :)
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
            	if (firstPlay){
            		firstPlay = false;
            		localSettingsView.disableTime();
            		if (gameEngine.getSettings().isTimeEnabled())
            			getJPanelGameClock().start();
            	}
                try {
                    int x = event.getX();//get X position of mouse
                    int y = event.getY();//get Y position of mouse

                    Square sq = this.gameEngine.getChessboard().getChessboardView().getSquare(x, y);
                    if ((sq == null && sq.piece == null && this.gameEngine.getChessboard().getActiveSquare() == null)
                            || (this.gameEngine.getChessboard().getActiveSquare() == null && sq.piece != null && sq.getPiece().getPlayer() != gameEngine.getActivePlayer())) {
                        return;
                    }

                    if (sq.piece != null && sq.getPiece().getPlayer() == this.gameEngine.getActivePlayer() && sq != this.gameEngine.getChessboard().getActiveSquare()) {
                        this.gameEngine.getChessboard().unselect();
                        this.gameEngine.getChessboard().select(sq);
                    } else if (this.gameEngine.getChessboard().getActiveSquare() == sq) //unselect
                    {
                        this.gameEngine.getChessboard().unselect();
                    } else if (this.gameEngine.getChessboard().getActiveSquare() != null && this.gameEngine.getChessboard().getActiveSquare().piece != null
                            && this.gameEngine.getChessboard().getActiveSquare().getPiece().getAllMoves().contains(sq)) //move
                    {
                        if (gameEngine.getSettings().getGameType() == Settings.gameTypes.local) {
                            this.gameEngine.getCommandsManager().execute(new MoveCommands(this.gameEngine.getChessboard().getActiveSquare(), sq));
                        }


                        this.gameEngine.getChessboard().unselect();

                        //switch player
                        gameEngine.nextMove();
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
        repaint();
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
    /**
     * @return the JPanelGameClock
     */
    public JPanelGameClock getJPanelGameClock() {
        return JPanelGameClock;
    }


    @Override
    public void repaint() {
        super.repaint();
        if (null != this.localSettingsView) {
            this.localSettingsView.repaint();
        }
        if (gameEngine != null && null != gameEngine.getChessboard()) {
            gameEngine.getChessboard().repaint();
        }
        
    }

}
