package jchess.core;

import jchess.JChessApp;
import jchess.core.errors.ReadGameError;
import jchess.core.moves.Moves;
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
     * History of moves object
     */
    protected Moves moves;

    public GameEngine(Settings set) {
        this.blockedChessboard = false;
        settings = set;
        this.moves = new Moves(this);
        chessboard = new Chessboard(this.settings, this.moves);
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
        gameEngine.getMoves().setMoves(tempStr);
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

    public Moves getMoves() {
        return moves;
    }

    public void setMoves(Moves moves) {
        this.moves = moves;
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
        str += this.getMoves().getMovesInString();
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
        }
    }

    public boolean undo() {
        boolean status = false;

        if (this.getSettings().getGameType() == Settings.gameTypes.local) {
            status = getChessboard().undo();
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
            while (getChessboard().undo()) {
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
            while (getChessboard().redo()) {
                result = true;
            }
        } else {
            throw new UnsupportedOperationException(Settings.lang("operation_supported_only_in_local_game"));
        }

        return result;
    }

    public boolean redo() {
        boolean status = getChessboard().redo();
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

    /**
     * Method to simulate Move to check if it's correct etc. (usable for network game).
     *
     * @param beginX from which X (on chessboard) move starts
     * @param beginY from which Y (on chessboard) move starts
     * @param endX   to   which X (on chessboard) move go
     * @param endY   to   which Y (on chessboard) move go
     */
    public boolean simulateMove(int beginX, int beginY, int endX, int endY) {
        try {
            Square begin = getChessboard().getSquare(beginX, beginY);
            Square end = getChessboard().getSquare(endX, endY);
            getChessboard().select(begin);
            if (getChessboard().getActiveSquare().getPiece().getAllMoves().contains(end)) //move
            {
                getChessboard().move(begin, end);
            } else {
                LOG.debug("Bad move: beginX: " + beginX + " beginY: " + beginY + " endX: " + endX + " endY: " + endY);
                return false;
            }
            getChessboard().unselect();
            nextMove();

            return true;

        } catch (StringIndexOutOfBoundsException exc) {
            LOG.error("StringIndexOutOfBoundsException: " + exc);
            return false;
        } catch (ArrayIndexOutOfBoundsException exc) {
            LOG.error("ArrayIndexOutOfBoundsException: " + exc);
            return false;
        } catch (NullPointerException exc) {
            LOG.error("NullPointerException: " + exc + " stack: " + exc.getStackTrace());
            return false;
        }
    }
}
