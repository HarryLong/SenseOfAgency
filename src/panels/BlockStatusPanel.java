package panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class BlockStatusPanel extends JPanel{
	int _block_id;
	boolean _complete;
	JLabel status_lbl;
	
	public BlockStatusPanel(int block_id)
	{
		_block_id = block_id;
		
		initLayout();
	}
	
	private void initLayout()
	{
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		status_lbl = new JLabel();
		add(status_lbl,c);
		
		refreshStatusLbl();
	}
	
	public void setComplete(boolean complete)
	{
		_complete = complete;
		refreshStatusLbl();
	}
	
	public void refreshStatusLbl()
	{
		if(_complete)
		{
			status_lbl.setText("<html>Status: <font color='green'>OK</font></html>");
		}
		else
		{
			status_lbl.setText("<html>Status: <font color='red'>NO DATA</font></html>");
		}
	}
}
