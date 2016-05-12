package jchess.core;

import jchess.JChessApp;
import jchess.core.commands.CommandsManager;
import jchess.core.errors.ReadGameError;
import jchess.core.pieces.implementation.King;
import jchess.core.players.Player;
import jchess.core.players.ia.IAInterface;
import jchess.core.utils.timePerStroke.TimePerStrokeSave;
import jchess.display.panels.JPanelGame;
import jchess.utils.Settings;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.*;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by Thomas on 04/04/2016.
 */
public class GameEngine {

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(GameEngine.class);
    static Random rnd = new Random();
    protected boolean firstAttempt;
    protected JPanelGame jPanelGame;
    /**
     * Settings object of the current game
     */
    protected Settings settings;
    /**
     * if chessboard is blocked - true, false otherwise
     */
    protected boolean blockedChessboard;
    /**
     * chessboard data object
     */
    protected Chessboard chessboard;
    /**
     * Currently active player object
     */
    protected Player activePlayer;
    /**
     * History of movesManager object
     */

    protected CommandsManager commandsManager;

    public CommandsManager getCommandsManager() {
        return commandsManager;
    }

    public GameEngine(Settings set) {
        this.blockedChessboard = false;
        settings = set;
        this.commandsManager = new CommandsManager(this);
        chessboard = new Chessboard(this);
        firstAttempt = true;
    }

    /**
     * Loading game method(loading game state from the earlier saved file)
     *
     * @param file File where is saved game
     */
    static public void loadGame(File file) {
        FileReader fileR = null;
        try {
            fileR = new FileReader(file);
        } catch (IOException exc) {
            LOG.error("Something wrong reading file: " + exc);
            return;
        }
        BufferedReader br = new BufferedReader(fileR);
        String tempStr = new String();
        String blackName, whiteName;
        try {
            tempStr = getLineWithVar(br, "[White");
            whiteName = getValue(tempStr);
            tempStr = getLineWithVar(br, "[Black");
            blackName = getValue(tempStr);
            tempStr = getLineWithVar(br, "1.");
        } catch (ReadGameError err) {
            LOG.error("Error reading file: " + err);
            return;
        }


        Settings locSetts = new Settings();

        Player playerBlack = locSetts.getPlayerBlack();
        Player playerWhite = locSetts.getPlayerWhite();

        playerBlack.setName(blackName);
        playerWhite.setName(whiteName);

        playerBlack.setType(Player.playerTypes.localUser);
        playerWhite.setType(Player.playerTypes.localUser);

        locSetts.setGameMode(Settings.gameModes.loadGame);
        locSetts.setGameType(Settings.gameTypes.local);

        GameEngine gameEngine = JChessApp.addNewGame(locSetts, whiteName + " vs. " + blackName);

        gameEngine.setBlockedChessboard(true);
        JChessApp.getJavaChessView().setLastTabAsActive();
        gameEngine.getCommandsManager().getMovesHistoryView().setMoves(tempStr);
        gameEngine.setBlockedChessboard(false);
        gameEngine.getChessboard().repaint();
        //newGUI.getChat().setEnabled(false);
    }

    /**
     * Method checking in with of line there is an error
     *
     * @param br     BufferedReader class object to operate on
     * @param srcStr String class object with text which variable you want to get in file
     * @return String with searched variable in file (whole line)
     * @throws ReadGameError class object when something goes wrong when reading file
     */


    static public String getLineWithVar(BufferedReader br, String srcStr) throws ReadGameError {
        String str = new String();
        while (true) {
            try {
                str = br.readLine();
            } catch (IOException exc) {
                LOG.error("Something wrong reading file: " + exc);
            }
            if (str == null) {
                throw new ReadGameError();
            }
            if (str.startsWith(srcStr)) {
                return str;
            }
        }
    }

    /**
     * Method to get value from loaded txt line
     *
     * @param line Line which is readed
     * @return result String with loaded value
     * @throws ReadGameError object class when something goes wrong
     */
    static public String getValue(String line) throws ReadGameError {
        int from = line.indexOf("\"");
        int to = line.lastIndexOf("\"");
        int size = line.length() - 1;
        String result = new String();
        if (to < from || from > size || to > size || to < 0 || from < 0) {
            throw new ReadGameError();
        }
        try {
            result = line.substring(from + 1, to);
        } catch (StringIndexOutOfBoundsException exc) {
            LOG.error("error getting value: " + exc);
            return "none";
        }
        return result;
    }

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static void newAutoGame() {

        String firstName = randomString(6);
        String secondName;
        do {
            secondName = randomString(6);
        }
        while (firstName.equals(secondName));
        firstName += "_White";
        secondName += "_Black";

        Settings sett = new Settings();
        Player pl1 = sett.getPlayerWhite();//set local player variable
        Player pl2 = sett.getPlayerBlack();//set local player variable
        sett.setGameMode(Settings.gameModes.newGame);
        //TODO: investigate and refactor
        pl1.setName(firstName);//set name of player
        pl2.setName(secondName);//set name of player
        pl1.setType(Player.playerTypes.localUser);//set type of player
        pl2.setType(Player.playerTypes.localUser);//set type of player
        sett.setGameType(Settings.gameTypes.local);
//        if (this.oponentComp.isSelected()) //if computer oponent is checked
//        {
//            pl2.setType(Player.playerTypes.computer);
//        }
        sett.setUpsideDown(false);
//        if (this.timeGame.isSelected()) //if timeGame is checked
//        {
//            String value = this.times[this.time4Game.getSelectedIndex()];//set time for game
//            Integer val = new Integer(value);
//            sett.setTimeForGame((int) val * 60);//set time for game and mult it to seconds
//            newGUI.getGameClock().setTimes(sett.getTimeForGame(), sett.getTimeForGame());
//            newGUI.getGameClock().start();
//        }
        LOG.debug("****************\nStarting new game: " + pl1.getName() + " vs. " + pl2.getName()
                + "\ntime 4 game: " + sett.getTimeForGame() + "\ntime limit set: " + sett.isTimeLimitSet()
                + "\nwhite on top?: " + sett.isUpsideDown() + "\n****************");//4test


        JChessApp.addNewGame(sett, firstName + " vs " + secondName);
        //newGUI.getChat().setEnabled(false);
        JChessApp.getJavaChessView().getActiveTabGame().repaint();
        JChessApp.getJavaChessView().setActiveTabGame(JChessApp.getJavaChessView().getNumberOfOpenedTabs() - 1);
    }

    public void changeTime(int value) {


        settings.setTimeForGame((int) value * 60);//set time for game and mult it to seconds
        getjPanelGame().getJPanelGameClock().setTimes(settings.getTimeForGame(), settings.getTimeForGame());
        getjPanelGame().getJPanelGameClock().repaint();
    }

    public boolean isBlockedChessboard() {
        return blockedChessboard;
    }

    public void setBlockedChessboard(boolean blockedChessboard) {
        this.blockedChessboard = blockedChessboard;
    }

    public Chessboard getChessboard() {
        return chessboard;
    }

    public void setChessboard(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public JPanelGame getjPanelGame() {
        return jPanelGame;
    }

    public void setjPanelGame(JPanelGame jPanelGame) {
        this.jPanelGame = jPanelGame;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(Player activePlayer) {
        this.activePlayer = activePlayer;
    }

    public Settings getSettings() {
        return settings;
    }

    /**
     * Method to Start new game
     */

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    /**
     * Method to save actual state of game
     *
     * @param path address of place where game will be saved
     */
    public void saveGame(File path) {
        File file = path;
        FileWriter fileW = null;
        try {
            fileW = new FileWriter(file);
        } catch (IOException exc) {
            LOG.error("error creating fileWriter: " + exc);
            JOptionPane.showMessageDialog(jPanelGame, Settings.lang("error_writing_to_file") + ": " + exc);
            return;
        }
        Calendar cal = Calendar.getInstance();
        String str = "";
        String info = "[Event \"JPanelGame\"]\n[Date \"" + cal.get(Calendar.YEAR) + "." + (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.DAY_OF_MONTH) + "\"]\n"
                + "[White \"" + this.getSettings().getPlayerWhite().getName() + "\"]\n[Black \"" + this.getSettings().getPlayerBlack().getName() + "\"]\n\n";
        str += info;
        str += this.getCommandsManager().getMovesHistoryView().getMovesInString();
        try {
            fileW.write(str);
            fileW.flush();
            fileW.close();
        } catch (IOException exc) {
            LOG.error("error writing to file: " + exc);
            JOptionPane.showMessageDialog(jPanelGame, Settings.lang("error_writing_to_file") + ": " + exc);
            return;
        }
        JOptionPane.showMessageDialog(jPanelGame, Settings.lang("game_saved_properly"));
    }

    /**
     * Method to end game
     *
     * @param message what to show player(s) at end of the game (for example "draw", "black wins" etc.)
     */
    public void endGame(String message) {
        setBlockedChessboard(true);
        LOG.debug(message);
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Method to swich active players after move
     */
    public void switchActive() {
        if (getActivePlayer() == getSettings().getPlayerWhite()) {
            setActivePlayer(getSettings().getPlayerBlack());
        } else {
            setActivePlayer(getSettings().getPlayerWhite());
        }

        jPanelGame.getJPanelGameClock().switchClocks();
    }

    public void newGame() {
        jPanelGame.newGame();
    }

    /**
     * Method to go to next move (checks if game is local/network etc.)
     */
    public void nextMove() {
        //PremierCoup Jouer
        if (firstAttempt) {
            if (settings.getTimeForGame() != 0) {
                getjPanelGame().getJPanelGameClock().start();
            }
            firstAttempt = false;
            jPanelGame.getJPanelGameClock().getTimeSetGame().setEnabled(false);
        }

        switchActive();

        LOG.debug("next move, active player: " + activePlayer.getName() +
                ", color: " + activePlayer.getColor().name() +
                ", type: " + activePlayer.getPlayerType().name()
        );

        if (activePlayer.getPlayerType() == Player.playerTypes.localUser) {
            this.blockedChessboard = false;
        } else if (activePlayer.getPlayerType() == Player.playerTypes.networkUser) {
            this.blockedChessboard = true;
        } else if (activePlayer.getPlayerType() == Player.playerTypes.computer) {
            this.blockedChessboard = true;
            ((IAInterface) activePlayer).playATurn();
        }

        //checkmate or stalemate
        King king;
        if (this.getActivePlayer() == this.getSettings().getPlayerWhite()) {
                     king = this.getChessboard().getKingWhite();
        } else {
            king = this.getChessboard().getKingBlack();
        }

        switch (king.isCheckmatedOrStalemated()) {
            case 1:
                this.endGame("Checkmate! " + king.getPlayer().getColor().toString() + " player lose!");
                this.getActivePlayer().setLoose(true);
                break;
            case 2:
                this.endGame("Stalemate! Draw!");
                this.getActivePlayer().setLoose(true);
                break;
        }
    }

    public boolean undo() {
        boolean status = false;

        if (this.getSettings().getGameType() == Settings.gameTypes.local) {
            status = commandsManager.undo(true);
            if (status) {
                this.switchActive();
            } else {
                getChessboard().repaint();//repaint for sure
            }
        }

        return status;
    }

    public boolean rewindToBegin() {
        boolean result = false;

        if (this.getSettings().getGameType() == Settings.gameTypes.local) {
            while (commandsManager.undo(true)) {
                result = true;
            }
        } else {
            throw new UnsupportedOperationException(Settings.lang("operation_supported_only_in_local_game"));
        }

        return result;
    }

    public boolean rewindToEnd() throws UnsupportedOperationException {
        boolean result = false;

        if (this.getSettings().getGameType() == Settings.gameTypes.local) {
            while (commandsManager.redo(true)) {
                result = true;
            }
        } else {
            throw new UnsupportedOperationException(Settings.lang("operation_supported_only_in_local_game"));
        }

        return result;
    }

    public boolean redo() {
        boolean status = commandsManager.redo(true);
        if (this.getSettings().getGameType() == Settings.gameTypes.local) {
            if (status) {
                this.nextMove();
            } else {
                getChessboard().repaint();//repaint for sure
            }
        } else {
            throw new UnsupportedOperationException(Settings.lang("operation_supported_only_in_local_game"));
        }
        return status;
    }

}
