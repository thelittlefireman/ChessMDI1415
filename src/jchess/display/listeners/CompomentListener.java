package jchess.display.listeners;

import jchess.display.panels.JPanelGame;
import org.apache.log4j.Logger;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Created by Thomas on 15/04/2016.
 */
public class CompomentListener implements ComponentListener {
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(CompomentListener.class);
    private JPanelGame jPanelGame;

    public CompomentListener(JPanelGame jPanelGame) {
        this.jPanelGame = jPanelGame;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        this.jPanelGame.resizeGame();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        componentResized(e);
        this.jPanelGame.repaint();
    }

    @Override
    public void componentShown(ComponentEvent e) {
        componentResized(e);
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
}
