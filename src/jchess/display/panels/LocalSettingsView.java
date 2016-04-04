/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jchess.display.panels;

import jchess.core.GameEngine;
import jchess.utils.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Mateusz Lach ( matlak, msl )
 */
public class LocalSettingsView extends JPanel implements ActionListener
{
    private JCheckBox isUpsideDown;
    
    private JCheckBox isDisplayLegalMovesEnabled;
    
    private JCheckBox isRenderLabelsEnabled;    
     
    private GridBagConstraints gbc;
    
    private GridBagLayout gbl;

    private GameEngine gameEngine;

    public LocalSettingsView(GameEngine gameEngine)
    {
        this.gameEngine = gameEngine;
        
        this.gbc = new GridBagConstraints();
        this.gbl = new GridBagLayout();
        
        this.setLayout(gbl);
        
        initUpsideDownControl();
        initDisplayLegalMovesControl();
        initRenderLabelsControl();
        refreshCheckBoxesState();
    }
    
    private void initUpsideDownControl()
    {
        this.isUpsideDown = new JCheckBox();
        this.isUpsideDown.setText(Settings.lang("upside_down"));
        this.isUpsideDown.setSize(this.isUpsideDown.getHeight(), this.getWidth());
        this.gbc.gridx = 0;
        this.gbc.gridy = 0;
        this.gbc.insets = new Insets(3, 3, 3, 3);
        this.gbl.setConstraints(isUpsideDown, gbc);
        this.add(isUpsideDown);
        
        isUpsideDown.addActionListener(this);
    }
    
    private void initDisplayLegalMovesControl()
    {
        this.isDisplayLegalMovesEnabled = new JCheckBox();
        this.isDisplayLegalMovesEnabled.setText(Settings.lang("display_legal_moves"));     
        
        this.gbc.gridx = 0;
        this.gbc.gridy = 1;
        this.gbl.setConstraints(isDisplayLegalMovesEnabled, gbc);
        this.add(isDisplayLegalMovesEnabled);
        
        isDisplayLegalMovesEnabled.addActionListener(this);        
    }
    
    private void initRenderLabelsControl()
    {
        this.isRenderLabelsEnabled = new JCheckBox();
        this.isRenderLabelsEnabled.setText(Settings.lang("display_labels"));     
        
        this.gbc.gridx = 0;
        this.gbc.gridy = 2;
        this.gbl.setConstraints(isRenderLabelsEnabled, gbc);
        this.add(isRenderLabelsEnabled);
        
        isRenderLabelsEnabled.addActionListener(this);        
    }
        
    private void refreshCheckBoxesState()
    {
        if (isInitiatedCorrectly())
        {
            isUpsideDown.setSelected(gameEngine.getSettings().isUpsideDown());
            isDisplayLegalMovesEnabled.setSelected(gameEngine.getSettings().isDisplayLegalMovesEnabled());
            isRenderLabelsEnabled.setSelected(gameEngine.getSettings().isRenderLabels());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        JCheckBox clickedComponent = (JCheckBox) e.getSource();
        if (clickedComponent == isUpsideDown)
        {
            gameEngine.getSettings().setUpsideDown(isUpsideDown.isSelected());
        } 
        else if (clickedComponent == isDisplayLegalMovesEnabled)
        {
            gameEngine.getSettings().setDisplayLegalMovesEnabled(isDisplayLegalMovesEnabled.isSelected());
        }
        else if (clickedComponent == isRenderLabelsEnabled) 
        {
            gameEngine.getSettings().setRenderLabels(isRenderLabelsEnabled.isSelected());
            gameEngine.getjPanelGame().resizeGame();
        }
        gameEngine.getjPanelGame().repaint();
    }
    
    @Override
    public void repaint()
    {
        refreshCheckBoxesState();
        super.repaint();
    }

    private boolean isInitiatedCorrectly()
    {
        return null != isUpsideDown && null != isDisplayLegalMovesEnabled
                && null != isRenderLabelsEnabled;
    }
}
