/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jchess.display.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import jchess.core.GameEngine;
import jchess.utils.Settings;

/**
 *
 * @author Mateusz Lach ( matlak, msl )
 */
public class LocalSettingsView extends JPanel implements ActionListener
{
    private JCheckBox isUpsideDown;
    
    private JCheckBox isDisplayLegalMovesEnabled;
    
    private JCheckBox isRenderLabelsEnabled;  
    
	private String[] time = {"1", "3", "5", "8", "10", "15", "20", "25", "30", "60", "120", "3600"};

	private JCheckBox isTimeEnabled;  
	
	private JComboBox<String> times;  
     
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
        initTimeEnabled();
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
    
    private void initTimeEnabled()
	{
		this.isTimeEnabled = new JCheckBox("Time Game (min)");

		this.gbc.gridx = 0;
		this.gbc.gridy = 3;
		this.gbl.setConstraints(isTimeEnabled, gbc);
		this.add(isTimeEnabled);


		isTimeEnabled.addActionListener(this);

		times = new JComboBox<String>(time);
		this.gbc.gridx = 1;
		this.gbc.gridy = 3;
		this.gbl.setConstraints(times, gbc);
		this.add(times);

		times.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (isTimeEnabled.isSelected()){
					String value = time[times.getSelectedIndex()];//set time for game
					Integer val = new Integer(value);
					gameEngine.getSettings().setTimeForGame((int) val * 60);//set time for game and mult it to seconds
					gameEngine.getjPanelGame().getJPanelGameClock().setTimes(gameEngine.getSettings().getTimeForGame(), gameEngine.getSettings().getTimeForGame());
					gameEngine.getjPanelGame().repaint();
				}

			}
		});
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
        else if (clickedComponent == isTimeEnabled){
        	gameEngine.getSettings().setTimeEnabled(isTimeEnabled.isSelected());
			String value = this.time[this.times.getSelectedIndex()];//set time for game
			Integer val = new Integer(value);
			gameEngine.getSettings().setTimeForGame((int) val * 60);//set time for game and mult it to seconds
			gameEngine.getjPanelGame().getJPanelGameClock().setTimes(gameEngine.getSettings().getTimeForGame(), gameEngine.getSettings().getTimeForGame());

			if (!isTimeEnabled.isSelected())
				gameEngine.getjPanelGame().getJPanelGameClock().setTimes(0, 0);
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
    
	public void disableTime() {
		times.setVisible(false);
		isTimeEnabled.setVisible(false);
	}
}
