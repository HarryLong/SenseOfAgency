package dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class NumberInputDialog extends JDialog implements PropertyChangeListener{

	JFormattedTextField _textField;
	String _message;
	long _min;
	long _max;
	
	public NumberInputDialog(JFrame parent, String title, String message, int min, int max, int default_value) {
		super(parent, true);
		setTitle(title);

        setLocationRelativeTo(null);
		
        setMinimumSize(new Dimension(500, 0));
        
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumIntegerDigits(2);
        format.setMaximumFractionDigits(0);

        _min = min;
        _max = max;
        _message = message;
        
        _textField = new JFormattedTextField(format);
        _textField.setColumns(2);
        _textField.setValue(default_value);
        _textField.addPropertyChangeListener("value", this);
        		
		initLayout();
	}
	
	public void initLayout()
	{
        Container contentPane = getContentPane();
		
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        
        // First the title
        contentPane.add(new JLabel(_message), c);
        
        c.gridx++;
        c.weightx = 0;

        // Add the inpt
        contentPane.add(_textField,c); 

        c.gridy++;
        c.fill = GridBagConstraints.NONE;
        JButton ok_btn = new JButton("OK");
        ok_btn.setHorizontalAlignment(SwingConstants.CENTER);
        ok_btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
        add(ok_btn, c);
	}
	
	public int getInput()
	{
		return ((Long) _textField.getValue()).intValue();
	}

	@Override
	public void propertyChange(PropertyChangeEvent pce) {
		int value = getInput();
		if(value > _max)
			_textField.setValue(_max);
		else if(value < _min)
			_textField.setValue(_min);
	}
	
	public static class PressedInputDialog extends NumberInputDialog
	{
		public PressedInputDialog(JFrame parent)
		{
			super(parent, "Please answer", "At what position was the clock when you pressed the button?",
					0,59,0);
		}
	}
	
	public static class ToneInputDialog extends NumberInputDialog
	{
		public ToneInputDialog(JFrame parent)
		{
			super(parent, "Please answer", "At what position was the clock when you heard the tone?",
					0,59,0);
		}
	}
}
