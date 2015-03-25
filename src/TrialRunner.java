import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.attribute.standard.MediaSize.Other;

import db.TrialResult;

public class TrialRunner implements CallbackListener{	
	private ClockPanel _clockPanel;
	private TrialResult _results;
	
	private BlockSettings _block_settings;
	
	private boolean _waiting_for_key;
	
	int _command_index;;
	int _time_index;
	int _range_index;	
	
	List<CallbackListener> _trial_completion_listeners;
	
	AudioPlayer _player;
	
	Color[] _triggerClockFaceColors;
	Color[] _otherClockFaceColors;
	Map<Color, Integer> _colorToKeycodeMapper;
	int _current_color_index;
	int _count_since_trigger_color;
	boolean _trigger_color_active;
	ColorChangerTrigger _colorChangerTrigger;
	
	Thread _colorChangerThread;
	static final int _COLOR_CHANGE_MIN = 2000; //ms
	static final int _COLOR_CHANGE_MAX = 3000; //ms

	public TrialRunner(ClockPanel clockPanel)
	{
		_trial_completion_listeners = new ArrayList<CallbackListener>();
		_player = new AudioPlayer("beep_short.wav");

		_triggerClockFaceColors = new Color[] {Color.RED, Color.GREEN, Color.BLUE};
		_otherClockFaceColors = new Color[] {Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.PINK};
		_trigger_color_active = false;
		_count_since_trigger_color = 0;
		
		_colorToKeycodeMapper = new HashMap<Color, Integer>();
		_colorToKeycodeMapper.put(Color.RED, KeyEvent.VK_R);
		_colorToKeycodeMapper.put(Color.GREEN, KeyEvent.VK_G);
		_colorToKeycodeMapper.put(Color.BLUE, KeyEvent.VK_B);
		_current_color_index = 0;
		_colorChangerTrigger = new ColorChangerTrigger(this);
		_colorChangerThread = new Thread(_colorChangerTrigger);
		_colorChangerThread.start();
		
		_results = new TrialResult();
		this._clockPanel = clockPanel;
		reset();
	}
	
	public void registerCompletionListener(CallbackListener listener)
	{
		_trial_completion_listeners.add(listener);
	}
	
	public void setBlockSettings( BlockSettings block)
	{
		this._block_settings = block;
	}
	
	public List<BlockSettingsFactory.Commands> getRemmainingCommands()
	{
		return _block_settings.commands.subList(_command_index, _block_settings.commands.size());
	}

	public void startTrial()
	{
		reset();
		processCommand();
	}
	
	public void reset()
	{
		_command_index = -1;
		_time_index = _range_index = 0;
		_count_since_trigger_color = 0;
		_results.reset();
	}
	
	public TrialResult getResults()
	{
		return _results;
	}

	private void processCommand()
	{
		_command_index++;

		if(_block_settings.commands.size() <= _command_index)
		{
			System.err.println("Reached end of commands without a TRIAL_END command!");
			endTrial();
		}
		else
		{
			processCommand(_block_settings.commands.get(_command_index));
		}
	}
	
	private void endTrial()
	{
		_command_index++;
		for(CallbackListener l : _trial_completion_listeners)
			l.callback();
	}
	
	private void processCommand(BlockSettingsFactory.Commands c)
	{
		_waiting_for_key = false;

		switch(c){
		case RESET_CLOCK:
			_clockPanel.reset();
			processCommand();
			break;
		case START_CLOCK:
			_clockPanel.startClock();
			if(_block_settings.colorChanging)
				_colorChangerTrigger.callbackIn(getRandom(_COLOR_CHANGE_MIN, _COLOR_CHANGE_MAX));
			processCommand();
			break;
		case EMIT_TONE:
			emitBeep(10);
			processCommand();
			break;
		case WAIT_FOR_KEY_PRESS:
			_waiting_for_key = true;
			break;
		case STOP_CLOCK:
			_clockPanel.stopClock();
			if(_block_settings.colorChanging)
				_colorChangerTrigger.stop();
			processCommand();
			break;
		case WAIT_FIXED_DURATION:
			waitAndProcessNextCommand(getNextFixedTime());
			break;
		case WAIT_VARIABLE_DURATION:
			TimeRange range = getNextTimeRange();
			waitAndProcessNextCommand(getRandom(range._min, range._max));
			break;
		case STOP_TRIAL:
			endTrial();
			break;
		default:
			System.err.println("Reached unrecognised command in Trial Runner!");
			break;
		}
	}
	
	public static final float SAMPLE_RATE = 8000f;
	private void emitBeep(int duration)
	{	
		_player.load();
		_results.real_time = _clockPanel.getClockHandPosition();
		new Thread(_player).start();;
	}
	
	private void waitAndProcessNextCommand(int time_in_ms)
	{
		new Thread(new TimedCallback(this, time_in_ms)).start();
	}
	
	private int getRandom(int from, int to)
	{
		return (int) (from + (Math.random() * (to-from)));
	}
	
	private int getNextFixedTime()
	{
 		return _block_settings.fixed_times.get(_time_index++);
	}
	
	private TimeRange getNextTimeRange()
	{
		return _block_settings.time_ranges.get(_range_index);
	}
	
	@Override
	public void callback() {
		processCommand();
	}
	
	public void keyPressed(int keycode)
	{
		if(_waiting_for_key)
		{
			_results.real_time = _clockPanel.getClockHandPosition();
			_results.correct_key_pressed = _trigger_color_active && keycode == _colorToKeycodeMapper.get(_triggerClockFaceColors[_current_color_index]);
			processCommand();
		}
	}
	
	public int changeColor()
	{
		if(_count_since_trigger_color++ > _otherClockFaceColors.length) // Trigger response color
		{
			_trigger_color_active = true;
			_current_color_index = ((_current_color_index+1) % _triggerClockFaceColors.length);
			_count_since_trigger_color = 0;
			_clockPanel.setClockFaceColor(_triggerClockFaceColors[_current_color_index]);		
		}
		else
		{
			_trigger_color_active = false;
			_clockPanel.setClockFaceColor(_otherClockFaceColors[getRandom(0, _otherClockFaceColors.length)]);		
		}
		return getRandom(_COLOR_CHANGE_MIN, _COLOR_CHANGE_MAX);
	}
	
	public static class ColorChangerTrigger implements Runnable
	{
		private static final int _INTERVALS = 5; //ms
		private TrialRunner _trialRunner;
		long _callback_time;
		private boolean _paused;
		
		public ColorChangerTrigger(TrialRunner trialRunner)
		{
			_trialRunner = trialRunner;
			_paused = true;
		}
		
		public void callbackIn(int ms)
		{
			_callback_time = System.currentTimeMillis() + ms;
			_paused = false;
		}

		public void stop()
		{
			_paused = true;
		}
		
		@Override
		public void run() {
			while(true)
			{
				if(!_paused)
				{
					if(System.currentTimeMillis() > _callback_time)
						_callback_time = System.currentTimeMillis() + _trialRunner.changeColor();
				}
				
				try {
					Thread.sleep(_INTERVALS);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}
}
