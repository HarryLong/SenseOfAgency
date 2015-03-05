import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import db.TrialResult;
import dialogs.SliderInputDialog;
import dialogs.SliderInputDialog.CertaintyInputDialog;
import dialogs.SliderInputDialog.LoudnessInputDialog;
import dialogs.SliderInputDialog.PressedInputDialog;
import dialogs.SliderInputDialog.ToneInputDialog;


public class BlockRunnerPanel extends JPanel implements CallbackListener{
	
	ClockPanel _clockPanel;
	TrialRunner _trialRunner;
		
	int n_trials_run;
	int n_trials_to_run;
	
	List<CallbackListener> _blockCompletionListeners;
	BlockSettings _settings;
	
	List<TrialResult> _results;
	
	PressedInputDialog _pressedInputDialog;
	ToneInputDialog _toneInputDialog;
	CertaintyInputDialog _certaintyInputDialog;
	LoudnessInputDialog _loudnessInputDialog;
	
	SliderInputDialog _sliderInputDialog;
	
	int _subject_id;
	
	public BlockRunnerPanel(JFrame main_window) {		
		_blockCompletionListeners = new ArrayList<>();
		_results = new ArrayList<>();
		_clockPanel = new ClockPanel();
		_trialRunner = new TrialRunner(_clockPanel);
		_trialRunner.registerCompletionListener(this);
		
		_pressedInputDialog = new PressedInputDialog(main_window);
		_pressedInputDialog.pack();
		
		_toneInputDialog = new ToneInputDialog(main_window);
		_toneInputDialog.pack();

		_certaintyInputDialog = new CertaintyInputDialog(main_window);
		_certaintyInputDialog.pack();

		_loudnessInputDialog = new LoudnessInputDialog(main_window);
		_loudnessInputDialog.pack();

		initKeyBindings();
		initLayout();
	}
	
	private void initKeyBindings()
	{
		Action space_action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				_trialRunner.keyPressed(KeyEvent.VK_SPACE);
			}
			@Override
			public String toString() {
				return "space_press_action";
			}
		};
		Action b_action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				_trialRunner.keyPressed(KeyEvent.VK_B);
			}
			@Override
			public String toString() {
				return "b_press_action";
			}
		};
		Action y_action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				_trialRunner.keyPressed(KeyEvent.VK_Y);
			}
			@Override
			public String toString() {
				return "y_press_action";
			}
		};
		Action r_action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				_trialRunner.keyPressed(KeyEvent.VK_R);
			}
			@Override
			public String toString() {
				return "r_press_action";
			}
		};
		
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),
				space_action.toString());
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("B"),
				b_action.toString());
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Y"),
				y_action.toString());
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"),
				r_action.toString());
		

		getActionMap().put(space_action.toString(), space_action);
		getActionMap().put(b_action.toString(), b_action);
		getActionMap().put(y_action.toString(), y_action);
		getActionMap().put(r_action.toString(), r_action);
	}

	public void registerCompletionListener(CallbackListener listener)
	{
		_blockCompletionListeners.add(listener);
	}
	
	public void setSettings(BlockSettings settings, int subject_id)
	{
		_results.clear();
		_settings = settings;
		n_trials_to_run = _settings.run_count;
		_subject_id = subject_id;
		n_trials_run = 0;
		_trialRunner.setBlockSettings(_settings);
	}
	
	public int getSubjectId()
	{
		return _subject_id;
	}
	
	public BlockSettings getSettings()
	{
		return _settings;
	}
	
	public void startBlock()
	{
		runTrial();
	}
	
	private void runTrial()
	{
		n_trials_run++;
		_trialRunner.startTrial();
	}

	void initLayout()
	{	
		setLayout(new GridLayout(1, 1));
		add(_clockPanel);
	}
	
	public List<TrialResult> getBlockResults()
	{
		return _results;
	}
	
	// Called when a block has completed
	@Override
	public void callback() {
		System.out.println("Trial complete!");
		
		TrialResult trial_results = new TrialResult(_trialRunner.getResults());
		List<BlockSettingsFactory.Commands> remaining_commands = _trialRunner.getRemmainingCommands();
		for(BlockSettingsFactory.Commands command : remaining_commands)
		{
			switch(command){
			case QUESTION_TONE:
				_toneInputDialog.setVisible(true);
				trial_results.guessed_time = _toneInputDialog.getInput();
				break;
			case QUESTION_PRESS:
				_pressedInputDialog.setVisible(true);
				trial_results.guessed_time = _pressedInputDialog.getInput();
				break;
			case QUESTION_CERTAINTY:
				_certaintyInputDialog.setVisible(true);
				trial_results.certainty = _certaintyInputDialog.getInput();
				break;
			case QUESTION_LOUDNESS:
				_loudnessInputDialog.setVisible(true);
				trial_results.loudness = _loudnessInputDialog.getInput();
				break;
			default:
				System.err.println("Reached unrecognised command in Block Runner!");
				break;
			}
		}
		_results.add(trial_results);
		
		if(n_trials_run < n_trials_to_run)
		{
			runTrial();
		}
		else
		{
			for(CallbackListener l : _blockCompletionListeners)
				l.callback();
		}
	}
}
