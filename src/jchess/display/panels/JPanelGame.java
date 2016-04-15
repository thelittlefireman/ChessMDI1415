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

import jchess.JChessApp;
import jchess.core.Chessboard;
import jchess.core.GameEngine;
import jchess.core.Player;
import jchess.display.listeners.CompomentListener;
import jchess.display.listeners.MouseClickListener;
import jchess.display.views.chessboard.ChessboardView;
import jchess.utils.Settings;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;


/**
 * @author: Mateusz Sławomir Lach ( matlak, msl )
 * @author: Damian Marciniak
 * Class responsible for the starts of new games, loading games,
 * saving it, and for ending it.
 * This class is also responsible for appoing player with have
 * a move at the moment
 */
public class JPanelGame extends JPanel {

    private static final Logger LOG = org.apache.log4j.Logger.getLogger(JPanelGame.class);
    protected LocalSettingsView localSettingsView;
    protected JTabbedPane tabPane;
    protected GameEngine gameEngine;
    /**
     * JPanelGame clock object
     */
    protected jchess.display.panels.JPanelGameClock JPanelGameClock;

    public JPanelGame(GameEngine gameEngine) {
        this.setLayout(null);
        this.gameEngine = gameEngine;

        ChessboardView chessboardView = gameEngine.getChessboard().getChessboardView();
        int chessboardWidth = chessboardView.getChessboardWidht(true);
        this.add(chessboardView);

        //this.board.
        JPanelGameClock = new JPanelGameClock(this);
        JPanelGameClock.setSize(new Dimension(200, 100));
        JPanelGameClock.setLocation(new Point(500, 0));
        this.add(JPanelGameClock);

        JScrollPane Moves = gameEngine.getMoves().getScrollPane();
        Moves.setSize(new Dimension(180, 350));
        Moves.setLocation(new Point(500, 121));
        this.add(Moves);


        this.tabPane = new JTabbedPane();
        this.localSettingsView = new LocalSettingsView(gameEngine);
        //this.tabPane.addTab(Settings.lang("game_chat"), this.chat);
        this.tabPane.addTab(Settings.lang("game_settings"), this.localSettingsView);
        this.tabPane.setSize(new Dimension(380, 100));
        this.tabPane.setLocation(new Point(chessboardWidth, chessboardWidth / 2));
        this.tabPane.setMinimumSize(new Dimension(400, 100));
        this.add(tabPane);


        this.setLayout(null);
        this.setDoubleBuffered(true);
        chessboardView.addMouseListener(new MouseClickListener(this.getGameEngine()));
        this.addComponentListener(new CompomentListener(this));
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    public void newGame() {
        gameEngine.getChessboard().getBoard().setPieces("", gameEngine.getSettings().getPlayerWhite(), gameEngine.getSettings().getPlayerBlack());

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





    /**
     * @return the JPanelGameClock
     */
    public JPanelGameClock getJPanelGameClock() {
        return JPanelGameClock;
    }


    @Override
    public void repaint() {
        super.repaint();
        if (null != this.tabPane) {
            this.tabPane.repaint();
        }
        if (null != this.localSettingsView) {
            this.localSettingsView.repaint();
        }
        if (gameEngine != null && null != gameEngine.getChessboard().getBoard()) {
            gameEngine.getChessboard().repaint();
        }
    }

    public void resizeGame() {
        int height = this.getHeight() >= this.getWidth() ? this.getWidth() : this.getHeight();

        int chessHeight = (int) Math.round((height * 0.88) / 8) * 8;
        int chessWidthWithLabels;
        JScrollPane movesScrollPane = gameEngine.getMoves().getScrollPane();
        ChessboardView chessboardView = gameEngine.getChessboard().getChessboardView();
        chessboardView.resizeChessboard((int) chessHeight);
        chessHeight = chessboardView.getHeight();
        chessWidthWithLabels = chessboardView.getChessboardWidht(true);
        movesScrollPane.setLocation(new Point(chessHeight + 5, 100));
        movesScrollPane.setSize(movesScrollPane.getWidth(), chessHeight - 100 - (chessWidthWithLabels / 4));
        getJPanelGameClock().setLocation(new Point(chessHeight + 5, 0));
        if (null != tabPane) {
            tabPane.setLocation(new Point(chessWidthWithLabels + 5, ((int) chessWidthWithLabels / 4) * 3));
            tabPane.setSize(new Dimension(movesScrollPane.getWidth(), chessWidthWithLabels / 4));
            tabPane.repaint();
        }
    }


}
