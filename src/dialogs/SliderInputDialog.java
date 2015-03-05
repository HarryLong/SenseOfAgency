package dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SliderInputDialog extends JDialog implements ChangeListener{

	JSlider _slider;
	String _message;
	JLabel _slider_value_lbl;
	
	public SliderInputDialog(JFrame parent, String title, String message, int min, int max, int default_value) {
		super(parent, true);
		setTitle(title);

        setLocationRelativeTo(null);
		
        setMinimumSize(new Dimension(500, 0));
        
		_slider = new JSlider(SwingConstants.HORIZONTAL, min, max, default_value);
		_slider.setPaintTicks(true);
		_slider.addChangeListener(this);
				
		_message = message;
		
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
        
        // First the title
        c.gridwidth = 3;
        JLabel title_lbl = new JLabel(_message);
        add(title_lbl, c);
        
        c.gridy++;

        // Add the minimum
        c.gridwidth = 1;
        JLabel min_lbl = new JLabel(Integer.toString(_slider.getMinimum()));
        contentPane.add(min_lbl,c); 

        c.gridx++;
        c.weightx = 1;
        contentPane.add(_slider,c); 

        c.gridx++;
        
        // Add the maximum
        JLabel max_lbl = new JLabel(Integer.toString(_slider.getMaximum()));
        c.weightx = 0;
        contentPane.add(max_lbl,c); 
        
        c.gridx = 0;
        c.gridy++;
        
        c.gridwidth = 3;
        _slider_value_lbl = new JLabel(Integer.toString(_slider.getValue()));
        _slider_value_lbl.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(_slider_value_lbl,c);
        
        c.gridy++;
        
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 3;
        c.weightx = 0;
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
		return _slider.getValue();
	}
	
	@Override
	public void stateChanged(ChangeEvent arg0) {
		_slider_value_lbl.setText(Integer.toString(_slider.getValue()));
	}
	
	public static class PressedInputDialog extends SliderInputDialog
	{
		public PressedInputDialog(JFrame parent)
		{
			super(parent, "Please answer", "At what position was the clock when you pressed the button?",
					0,60,0);
		}
	}
	
	public static class ToneInputDialog extends SliderInputDialog
	{
		public ToneInputDialog(JFrame parent)
		{
			super(parent, "Please answer", "At what position was the clock when you heard the tone?",
					0,60,0);
		}
	}
	
	public static class CertaintyInputDialog extends SliderInputDialog
	{
		public CertaintyInputDialog(JFrame parent)
		{
			super(parent, "Please answer", "How confident are you that you caused the tone?",
					0,100,0);
		}
	}
	
	public static class LoudnessInputDialog extends SliderInputDialog
	{
		public LoudnessInputDialog(JFrame parent)
		{
			super(parent, "Please answer", "How loud would you rate the tone?",
					0,10,0);
		}
	}
}
