package jchess.display.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class HintTextArea extends JTextArea implements FocusListener {

	private static final long serialVersionUID = 1L;
	private final String hint;
	private boolean showingHint;
	private Font font;

	public HintTextArea(final String hint) {
		super(hint);
		this.hint = hint;
		this.showingHint = true;
		
		font = new JTextField().getFont();
		setFont(font.deriveFont(Font.ITALIC));
		setForeground(Color.gray);
		setLineWrap(true);
		setWrapStyleWord(true);
		super.addFocusListener(this);
	}

	public void focusGained(FocusEvent e) {
		if (this.getText().isEmpty()) {
			super.setText("");
			showingHint = false;
			setFont(font.deriveFont(Font.PLAIN));
	        setForeground(Color.black);
		}
	}

	public void focusLost(FocusEvent e) {
		if (this.getText().isEmpty()) {
			super.setText(hint);
			setFont(font.deriveFont(Font.ITALIC));
			setForeground(Color.gray);
			showingHint = true;
		}
	}
	
	public void reset(){
		this.setText("");
		focusLost(null);
	}

	public String getText() {
		return showingHint ? "" : super.getText();
	}
}