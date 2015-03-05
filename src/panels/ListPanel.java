package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ListPanel<T> extends JPanel implements ListSelectionListener{
	JList<T> listContent;
	DefaultListModel<T> listModel;
	
	final int id;
	final String lblContent;
	JLabel lbl;
	JButton newBtn;
	JButton deleteBtn;
	List<ListPanelListener<T>> listeners;
	
	public ListPanel(int id, String lblContent)
	{
		this.id = id;
		this.lblContent = lblContent;
		
		// Init the list and model
		listModel = new DefaultListModel<T>();
		listContent = new JList<T>();
		listContent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listContent.setModel(listModel);
		listContent.addListSelectionListener(this);
		
		listeners = new ArrayList<ListPanelListener<T>>();
		initLayout();
	}
	
	public void addListener(ListPanelListener<T> listener)
	{
		this.listeners.add(listener);
	}
	
	public void removeListener(ListPanelListener<T> listener)
	{
		this.listeners.remove(listener);
	}

	public void initLayout()
	{
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		// The List
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 3;
		c.weightx = .8;
		c.weighty = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		JScrollPane listScrollPane = new JScrollPane(listContent);
		add(listScrollPane, c);
		
		// The new button
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		c.gridx++;
		c.weightx = .2;
		newBtn = new JButton("New");
		newBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for(ListPanelListener<T> l : listeners)
					l.newClicked(id);
			}
		});
		add(newBtn,c);
		
		// The label
		c.gridy++;
		lbl = new JLabel();
		setLabel(null);
		add(lbl,c);
		// The delete button
		c.gridy++;
		deleteBtn  = new JButton("Delete");
		deleteBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for(ListPanelListener<T> l : listeners)
					l.deleteClicked(id);
			}
		});
		add(deleteBtn,c);
		deleteBtn.setEnabled(false);
	}
	
	public void setLabel(String content)
	{
		lbl.setText(lblContent + (content == null ? " " : content));
	}
	
	public T getSelectedElement()
	{
		int selectedIndex;
		if ((selectedIndex = listContent.getSelectedIndex()) != -1)
			return listModel.getElementAt(selectedIndex);
		else
			return null;
	}
	
	public void enableNew(final boolean enabled)
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				newBtn.setEnabled(enabled);
			}
		});
	}
	
	public void clear()
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
					listModel.clear();
			}
		});
	}
	
	public void setElements(final List<T> elements)
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				listModel.clear(); // First clear the list
				for(T element: elements)
					listModel.addElement(element);
			}
		});
	}
	
	public void addElement(final T element)
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				listModel.addElement(element);
			}
		});
	}
	
	public void removeElement(final T element)
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				listModel.removeElement(element);
			}
		});
	}	

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			T selectedElement = getSelectedElement();
			deleteBtn.setEnabled(selectedElement != null);
			for(ListPanelListener<T> listener : listeners)
				listener.selectedElement(id, selectedElement);
		}
	}
}
