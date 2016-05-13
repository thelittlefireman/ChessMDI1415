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

package jchess;

import jchess.core.GameEngine;
import jchess.display.panels.JPanelGame;
import jchess.utils.Settings;
import org.apache.log4j.PropertyConfigurator;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * The main class of the application.
 */
public class JChessApp extends SingleFrameApplication {

    public final static String LOG_FILE = "log4j.properties";
    public final static String MAIN_PACKAGE_NAME = "jchess";
    protected static JChessView javaChessView;

    public static List<GameEngine> getActiveGameEngine() {
        return activeGameEngine;
    }

    protected static List<GameEngine> activeGameEngine;

    public JChessApp() {
        activeGameEngine = new ArrayList<>();
    }
    /**
     * @return the jcv
     */
    public static JChessView getJavaChessView()
    {
        return javaChessView;
    }

    public static GameEngine addNewGame(GameEngine gameEngine, String title) {

        JPanelGame jPanelGame = getJavaChessView().addNewTab(title, gameEngine);
        gameEngine.setjPanelGame(jPanelGame);
        activeGameEngine.add(gameEngine);
        gameEngine.newGame();

        return gameEngine;
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of JChessApp
     */
    public static JChessApp getApplication()
    {
        return Application.getInstance(JChessApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args)
    {
        activeGameEngine = new ArrayList<>();
        launch(JChessApp.class, args);
        Properties logProp = new Properties();
        try {
            logProp.load(JChessApp.class.getClassLoader().getResourceAsStream(LOG_FILE));
            PropertyConfigurator.configure(logProp);
        }
        catch (NullPointerException | IOException e)
        {
            System.err.println("Logging not enabled : "+e.getMessage());
        }
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        javaChessView = new JChessView(this);
        show(getJavaChessView());
        GameEngine.newAutoGame();
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(Window root) {
    }
}
