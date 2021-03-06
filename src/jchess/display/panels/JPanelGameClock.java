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
package jchess.display.panels;

import jchess.core.utils.Clock;
import jchess.core.players.Player;
import jchess.utils.Settings;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

import static jchess.display.windows.DrawLocalSettings.times;

/** Class to representing the full JPanelGame time
 * @param game The current JPanelGame
 */
public class JPanelGameClock extends JPanel implements Runnable
{
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(JPanelGameClock.class);

    public Clock white_clock;
    public Clock black_clock;

    public Clock getWhite_clock() {
        return white_clock;
    }

    public Clock getBlack_clock() {
        return black_clock;
    }

    private Clock runningClock;
    private Settings settings;
    private Thread thread;
    private jchess.display.panels.JPanelGame JPanelGame;
    private Graphics g;
    private String white_clock_string, black_clock_string;
    private BufferedImage background;
    private Graphics bufferedGraphics;

    JPanelGameClock(final JPanelGame JPanelGame)
    {
        super();
        this.white_clock = new Clock();//white player clock
        this.black_clock = new Clock();//black player clock
        this.runningClock = this.white_clock;//running/active clock
        this.JPanelGame = JPanelGame;
        this.settings = JPanelGame.getGameEngine().getSettings();
        this.background = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);

        int time = this.settings.getTimeForGame();

        this.setTimes(time, time);
        this.setPlayers(this.settings.getPlayerBlack(), this.settings.getPlayerWhite());

        this.thread = new Thread(this);
        if (this.settings.isTimeLimitSet())
        {
            thread.start();
        }
        this.drawBackground();

        this.setDoubleBuffered(true);
    }

    /** Method to init JPanelGame clock
     */
    public void start()
    {
        this.thread.start();
    }

    /** Method to stop JPanelGame clock
     */
    public void stop()
    {
        this.runningClock = null;

        try
        {//block this thread
            this.thread.wait();
        }
        catch (InterruptedException exc)
        {
            LOG.error("Error blocking thread: " + exc);
        }
        catch (IllegalMonitorStateException exc)
        {
            LOG.error("Error blocking thread: " + exc);
        }
    }

    /** Method of drawing graphical background of clock
     */
    void drawBackground()
    {
        Graphics gr = this.background.getGraphics();
        Graphics2D g2d = (Graphics2D) gr;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font("Serif", Font.ITALIC, 20);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(5, 30, 80, 30);
        g2d.setFont(font);

        g2d.setColor(Color.BLACK);
        g2d.fillRect(85, 30, 90, 30);
        g2d.drawRect(5, 30, 170, 30);
        g2d.drawRect(5, 60, 170, 30);
        g2d.drawLine(85, 30, 85, 90);
        font = new Font("Serif", Font.ITALIC, 16);
       // g2d.drawString(settings.getPlayerWhite().getName(), 10, 50);
       // g2d.setColor(Color.WHITE);
       // g2d.drawString(settings.getPlayerBlack().getName(), 100, 50);
        this.bufferedGraphics = this.background.getGraphics();
    }

    /**
    Annotation to superclass Graphics drawing the clock graphics
     * @param g Graphics2D Capt object to paint
     */
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        white_clock_string = this.white_clock.prepareString();
        black_clock_string = this.black_clock.prepareString();
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.background, 0, 0, this);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font("Serif", Font.ITALIC, 20);
        g2d.drawImage(this.background, 0, 0, this);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(5, 30, 80, 30);
        g2d.setFont(font);

        g2d.setColor(Color.BLACK);
        g2d.fillRect(85, 30, 90, 30);
        g2d.drawRect(5, 30, 170, 30);
        g2d.drawRect(5, 60, 170, 30);
        g2d.drawLine(85, 30, 85, 90);
        font = new Font("Serif", Font.ITALIC, 14);
        g2d.drawImage(this.background, 0, 0, this);
        g2d.setFont(font);
        g.drawString(settings.getPlayerWhite().getName(), 10, 50);
        g.setColor(Color.WHITE);
        g.drawString(settings.getPlayerBlack().getName(), 100, 50);
        g2d.setFont(font);
        g.setColor(Color.BLACK);
        g2d.drawString(white_clock_string, 10, 80);
        g2d.drawString(black_clock_string, 90, 80);
    }

    /**
    Annotation to superclass Graphics updateing clock graphisc
     * @param g Graphics2D Capt object to paint
     */
    @Override
    public void update(Graphics g)
    {
        paint(g);
    }

    /** Method of swiching the players clocks
     */
    public void switchClocks()
    {
        /*in documentation this method is called 'switch', but it's restricted name
        to switch block (in pascal called "case") - this've to be repaired in documentation by Wąsu:P*/
        if (this.runningClock == this.white_clock)
        {
            this.runningClock = this.black_clock;
        }
        else
        {
            this.runningClock = this.white_clock;
        }
        this.runningClock.setDecrementActualNumber(0);
    }

    /** Method with is setting the players clocks time
     * @param t1 Capt the player time
     * @param t2 Capt the player time
     */
    public void setTimes(int t1, int t2)
    {
        /*rather in chess JPanelGame players got the same time 4 JPanelGame, so why in documentation
         * this method've 2 parameters ? */
        this.white_clock.init(t1);
        this.black_clock.init(t2);
    }

    /** Method with is setting the players clocks
     * @param p1 Capt player information
     * @param p2 Capt player information
     */
    private void setPlayers(Player p1, Player p2)
    {
        /*in documentation it's called 'setPlayer' but when we've 'setTimes' better to use
         * one convention of naming methods - this've to be repaired in documentation by Wąsu:P
        dojdziemy do tego:D:D:D*/
        if (p1.getColor() == p1.getColor().WHITE)
        {
            this.white_clock.setPlayer(p1);
            this.black_clock.setPlayer(p2);
        }
        else
        {
            this.white_clock.setPlayer(p2);
            this.black_clock.setPlayer(p1);
        }
    }

    /** Method with is running the time on clock
     */
    public void run()
    {
        while (true)
        {
            if (this.runningClock != null)
            {
                if (this.runningClock.decrement())
                {
                    repaint();
                    try
                    {
                        thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        LOG.error("Some error in JPanelGameClock thread: " + e);
                    }
                    //if(this.JPanelGame.blockedChessboard)
                    //  this.JPanelGame.blockedChessboard = false;
                }
                if (this.runningClock != null && this.runningClock.getLeftTime() == 0)
                {
                    this.timeOver();
                }
            }
        }
    }

    /** Method of checking is the time of the JPanelGame is not over
     */
    private void timeOver()
    {
        String color = new String();
        if (this.white_clock.getLeftTime() == 0)
        {//Check which player win
            color = this.black_clock.getPlayer().getColor().toString();
        }
        else if (this.black_clock.getLeftTime() == 0)
        {
            color = this.white_clock.getPlayer().getColor().toString();
        }
        else
        {//if called in wrong moment
            LOG.debug("Time over called when player got time 2 play");
        }
        this.JPanelGame.getGameEngine().endGame("Time is over! " + color + " player win the JPanelGame.");
        this.stop();

        //JOptionPane.showMessageDialog(this, "koniec czasu");
    }
}