
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import panels.ListPanel;
import panels.ListPanelListener;
import db.DBManager;
import db.ExperimentData;
import db.Subject;
import db.TrialResult;
import dialogs.PreBlockDialogContent;


public class MainWindow extends JFrame implements CallbackListener{
	public static final String TITLE = "Sense of Agency [SoA]";
		
	private MyActionListener actionListener;
		
	BlockRunnerPanel _blockRunner;
	OverviewPanel _overviewPanel;
	private JPanel _cards;
    private Card _activeCard;
    
    ExperimentData _all_experiment_data;
    Map<Integer,Subject> _subject_id_to_subject_mapper;
    
    BlockSettingsFactory _block_settings_factory;
    
    DBManager _db;
    
    JMenuBar _menuBar;

    enum Card {
        Overview("Overview"),
        BlockRunner("BlockRunner");

        Card(String name)
        {
            this.cardName = name;
        }
        String cardName;
    }
	
	public MainWindow() {
		setTitle(TITLE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(1000, 1000));
		setLocationRelativeTo(null);	
		actionListener = new MyActionListener();
		
		// Load the settings
		
		
		_db = new DBManager();
		_all_experiment_data = _db.getData();
		_subject_id_to_subject_mapper = new HashMap<>();
		
		for(Subject s : _db.getSubjects())
			_subject_id_to_subject_mapper.put(s.id, s);
		
        _blockRunner = new BlockRunnerPanel(this);
        _blockRunner.registerCompletionListener(this);
        
        _overviewPanel = new OverviewPanel();
        
		_block_settings_factory  = new BlockSettingsFactory();
		
		initMenu();
		initLayout();		
	}

	private void initLayout()
	{
        CardLayout cl = new CardLayout();
        _cards = new JPanel(cl);
        _cards.add(Card.Overview.cardName, _overviewPanel);
        _cards.add(Card.BlockRunner.cardName, _blockRunner);
        
        add(_cards);

        setActiveCard(Card.Overview);
	}
	
    private void setActiveCard(Card card)
    {
        if(_activeCard == null || _activeCard != card)
        {
            CardLayout cl = (CardLayout) _cards.getLayout();
            cl.show(_cards, card.cardName);
            _activeCard = card;
            repaint();
            
            setJMenuBar(_activeCard == Card.Overview ? _menuBar : null);
        }
    }
    
    private void runBlock(int version, int block)
    {
    	if( JOptionPane.showConfirmDialog(this, PreBlockDialogContent.getDialogContent(version, block) +  "\nReady?") == JOptionPane.OK_OPTION)
    	{
        	_blockRunner.setSettings(_block_settings_factory.generate(version, block), _overviewPanel.getSelectedSubject().id);
        	setActiveCard(Card.BlockRunner);
        	_blockRunner.startBlock();
    	}
    }
	
	private void initMenu()
	{
		_menuBar = new JMenuBar();
		
		//********FILE MENU*****************************************************
		JMenu fileMenu = new JMenu("File");
		// Exit
		JMenuItem exportMI = new JMenuItem(new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				exportData();
			}
		});
		exportMI.setText("Export Data");
		fileMenu.add(exportMI);
		
		_menuBar.add(fileMenu);
	}
	
	private void exportData()
	{
		FileWriter fw;
		try {
			fw = getCsvFileWriter();
			if(fw != null)
			{
				if(_all_experiment_data.export(fw, _subject_id_to_subject_mapper))
					JOptionPane.showMessageDialog(this, "Exported successfully!", "Complete", JOptionPane.INFORMATION_MESSAGE);
				else
					JOptionPane.showMessageDialog(this, "Exported failed!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public FileWriter getCsvFileWriter() throws IOException
	{
		// First get the file in question
		JFileChooser fc = new JFileChooser();
		int ret = fc.showOpenDialog(this);
		if(ret != JFileChooser.APPROVE_OPTION)
			return null;
		
		File outputFile;
		String filePath = fc.getSelectedFile().getAbsolutePath();
		if(filePath.endsWith(".csv"))
			outputFile = fc.getSelectedFile();
		else
			outputFile = new File(filePath + ".csv");
		
		return new FileWriter(outputFile);
	}
	
	public void exit()
	{
		int ret = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", null, JOptionPane.YES_NO_OPTION);
		
		if(ret == JOptionPane.YES_OPTION)
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));			
	}
	
	private class MyActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent ae) {
			switch(ae.getActionCommand())
			{
			}
		}
	}
	
	@Override
	public void callback() {
		List<TrialResult> results = _blockRunner.getBlockResults();
		BlockSettings settings = _blockRunner.getSettings();
		int subject_id = _blockRunner.getSubjectId();
		
		_all_experiment_data.removeResults(subject_id, settings.version, settings.block);
		_all_experiment_data.addResults(subject_id, settings.version, settings.block, results);
		_db.insertResults(subject_id, settings.version, settings.block, results);
		_overviewPanel.refreshStatuses();
		
		JOptionPane.showMessageDialog(this, "Thanks!");
		setActiveCard(Card.Overview);
	}
	
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable() {
	    @Override
	    public void run() {
				new MainWindow().setVisible(true);;
	    	}
	    });
	}
	
	private class OverviewPanel extends JPanel implements ListPanelListener<Subject>{
		
		ListPanel<Subject> _subjects_lp;
		
		JLabel[][] _blockStatusLabels;
		JButton[][] _runButtons;
				
		public OverviewPanel()
		{
			_blockStatusLabels = new JLabel[2][4]; // 2 versions x 4 blocks
			_runButtons = new JButton[2][4]; // 2 versions x 4 blocks
			
			_subjects_lp = new ListPanel<Subject>(1, "");
			for(Subject s : _subject_id_to_subject_mapper.values())
				_subjects_lp.addElement(s);
			_subjects_lp.addListener(this);
			
			initLayout();
			setSubjectSelected(false);
		}
		
		public Subject getSelectedSubject()
		{
			return _subjects_lp.getSelectedElement();
		}
		
		private void setSubjectSelected(boolean selected)
		{
			for(int i = 0; i < _runButtons.length; i++)
				for(int ii = 0; ii < _runButtons[i].length; ii++)
					_runButtons[i][ii].setEnabled(selected);
		}
		
		private void initLayout()
		{				
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 3;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1;
			
			// TITLE
			JLabel pairTitle = new JLabel("<html><u>Subjects:</u></html>");
			pairTitle.setHorizontalAlignment(SwingConstants.CENTER);
			pairTitle.setFont(new Font(null, Font.BOLD, 30));
			add(pairTitle,c);
			
			c.gridy++;
			
			// Subjects
			add(_subjects_lp, c);
			
			c.gridy++;
			
			// Add the titles
			c.gridwidth = 1;
			JLabel block_number_title = new JLabel("Block Number");
			block_number_title.setFont(new Font(null, Font.BOLD, 20));
			block_number_title.setHorizontalAlignment(SwingConstants.CENTER);
			add(block_number_title, c);
			
			c.gridx++;
			
			JLabel status_title = new JLabel("Status");
			status_title.setFont(new Font(null, Font.BOLD, 20));
			status_title.setHorizontalAlignment(SwingConstants.CENTER);
			add(status_title, c);
			
			c.gridy++;
			
			// Block Status Panels
			for(int version = 0; version < 2; version++)
			{
				c.gridx = 0;
				c.gridwidth = 3;
				JLabel versionTitle = new JLabel("<html><u>Version " + (version+1) + ":</u></html>");
				versionTitle.setFont(new Font(null, Font.BOLD, 15));
				versionTitle.setHorizontalAlignment(SwingConstants.CENTER);
				add(versionTitle,c);
				
				for(int block = 0; block < 4; block++)
				{
					final int real_version = version+1;
					final int real_block = block+1;
					
					// Init block status panel
					_blockStatusLabels[version][block] = new JLabel();
					_blockStatusLabels[version][block].setHorizontalAlignment(SwingConstants.CENTER);
					// Init button
					_runButtons[version][block] = new JButton("Run");
					_runButtons[version][block].addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							runBlock(real_version, real_block);							
						}
					});
					
					c.gridy++;
					c.gridwidth = 1;
					c.weightx = 1;
					c.gridx = 0;
					
					JLabel block_number_label = new JLabel(Integer.toString(block+1));
					block_number_label.setHorizontalAlignment(SwingConstants.CENTER);
					add(block_number_label, c);
					
					c.gridx++;
					
					add(_blockStatusLabels[version][block],c);

					c.gridx++;
					c.weightx = 0;
					add(_runButtons[version][block],c);
					
					c.gridy++;
					c.gridwidth = 3;
					JLabel padding_label = new JLabel(" ");
					padding_label.setBackground(Color.black);
					add(padding_label, c);
					c.gridy++;
				}
			}
			
			c.gridy++;
			c.gridwidth = 3;
			c.weighty = 1;
			JLabel padding_label = new JLabel(" ");
			padding_label.setBackground(Color.black);
			add(padding_label, c);
			c.gridy++;
		}

		public void refreshStatuses()
		{
			Subject selected_subject = getSelectedSubject();
			for (int version = 0; version < 2; version++) {
				for (int block = 0; block < 4; block++) {
					if(selected_subject != null && _all_experiment_data.hasBlock(selected_subject.id, version + 1, block + 1))
					{
						_blockStatusLabels[version][block].setText("<html><font color='green'>OK</font></html>");
					}
					else
					{
						_blockStatusLabels[version][block].setText("<html><font color='red'>NO DATA</font></html>");
					}
				}
			}
		}
		
		@Override
		public void newClicked(int id) {
			String name = (String) JOptionPane.showInputDialog(this, 
										"Subject Name: ", 
										"New subject", 
										JOptionPane.QUESTION_MESSAGE,
										null, 
										null, 
										null);
			
			if(name != null)
			{
				Subject s = new Subject(name);
				_db.addNewSubject(s);
				_subject_id_to_subject_mapper.put(s.id, s);
				_all_experiment_data.addSubject(s.id);
				_subjects_lp.addElement(s);
			}
		}

		@Override
		public void deleteClicked(int id) {
			Subject selected_subject = getSelectedSubject();
			
			if(selected_subject != null && confirmDeletion())
			{
				// Remove from db
				_db.delSubject(selected_subject.id);
				_all_experiment_data.removeSubject(selected_subject.id);
				_subjects_lp.removeElement(selected_subject);
				_subject_id_to_subject_mapper.remove(selected_subject.id);
			}
		}

		@Override
		public void selectedElement(int id, Subject element) {			
			setSubjectSelected(element != null);
			refreshStatuses();
		}
		
		public boolean confirmDeletion() {
			return (JOptionPane
					.showConfirmDialog(this,
							"Are you sure you want to delete this? All associated data will be lost.") == JOptionPane.YES_OPTION);
			}
	}
}
