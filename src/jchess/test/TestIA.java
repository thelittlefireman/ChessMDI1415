package jchess.test;

import jchess.JChessApp;
import jchess.core.Chessboard;
import jchess.core.GameEngine;
import jchess.core.players.ia.GloutonIA;
import jchess.core.players.ia.RandomIA;
import jchess.core.utils.Colors;
import jchess.display.panels.JPanelGame;
import jchess.utils.Settings;
import org.jdesktop.application.SingleFrameApplication;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by thoma on 13/05/2016.
 */
public class TestIA {
    private Settings settings;

    private Chessboard board;
    private GameEngine gameEngine;

    @Before
    public void setUp() {
        SingleFrameApplication.launch(JChessApp.class, new String[] {});
//        board = JChessApp.getActiveGameEngine().get(0).getChessboard();
        settings = new Settings();
        settings.setPlayerBlack( new GloutonIA(gameEngine, "computer",Colors.BLACK.getColorName()));
        settings.setPlayerWhite( new GloutonIA(gameEngine, "computer",Colors.WHITE.getColorName()));
        gameEngine = new GameEngine(settings);
        gameEngine.setjPanelGame(new JPanelGame(gameEngine));
        gameEngine.setActivePlayer(gameEngine.getSettings().getPlayerWhite());
        board = gameEngine.getChessboard(); // new Chessboard(settings, new MovesHistoryView(new JPanelGame()));

        // JPanelGame g = new JPanelGame();
        // #1 bad API design
        // g.newGame(); // fails because coupled to GUI concerns and tabs stuff
        // anyway
        gameEngine.getInitialPlaceStrategy().setPieces("");


        // #2 bad API design
        //  MovesHistoryView movesManager = new MovesHistoryView(g);
        // Chessboard board = new Chessboard(settings, movesManager);
        // g.getChessboard() != board :(
        // board.getMovesHistoryView() != movesManager :(


    }

    @Test
    public void testInitBoard() throws Exception {
        assertEquals(16, board.getAllPieces(Colors.WHITE).size());
        assertEquals(16, board.getAllPieces(Colors.BLACK).size());
        // #3 bad API design
        // assertNotNull(board.getMovesHistoryView());
    }
}
