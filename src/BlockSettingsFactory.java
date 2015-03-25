import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BlockSettingsFactory {
	public static final int _RUN_COUNT = 2;
	
	BlockSettings[][] _blockSettings;
	
	public static enum Commands{
		RESET_CLOCK,
		START_CLOCK,
		WAIT_FOR_KEY_PRESS,
		EMIT_TONE,
		STOP_CLOCK,
		WAIT_FIXED_DURATION,
		WAIT_VARIABLE_DURATION,
		STOP_TRIAL,
		QUESTION_TONE,
		QUESTION_PRESS,
		QUESTION_CERTAINTY,
		QUESTION_LOUDNESS,
	}
	
	public BlockSettingsFactory()
	{
		_blockSettings = new BlockSettings[2][4];
		
		_blockSettings[0][0] = getV1Block1();
		_blockSettings[0][1] = getV1Block2();
		_blockSettings[0][2] = getV1Block3();
		_blockSettings[0][3] = getV1Block4();

		_blockSettings[1][0] = getV2Block1();
		_blockSettings[1][1] = getV2Block2();
		_blockSettings[1][2] = getV2Block3();
		_blockSettings[1][3] = getV2Block4();
	}
	
	public BlockSettings generate(int version, int block)
	{
		return _blockSettings[version-1][block-1];
	}

	private static BlockSettings getV1Block1()
	{
		BlockSettings settings = new BlockSettings(_RUN_COUNT, 1, 1, true);
		
		settings.commands.add(Commands.RESET_CLOCK); // Reset clock
		
		addFixedWait(settings.commands, settings.fixed_times, 1000); // Wait 1 second
		
		settings.commands.add(Commands.START_CLOCK); // start clock
				
		settings.commands.add(Commands.WAIT_FOR_KEY_PRESS); // Wait for the user to press
		
		addRangedWait(settings.commands, settings.time_ranges, 1500, 2500); // Wait [1.5 - 2] seconds
		
		settings.commands.add(Commands.STOP_CLOCK); // Stop the clock

		addFixedWait(settings.commands, settings.fixed_times, 500); // Wait .5 seconds buffer
		
		settings.commands.add(Commands.STOP_TRIAL);
		settings.commands.add(Commands.QUESTION_PRESS);
		
		return settings;
	}
	
	private static BlockSettings getV1Block2()
	{
		BlockSettings settings = new BlockSettings(_RUN_COUNT, 1, 2, true);
		
		settings.commands.add(Commands.RESET_CLOCK); // Reset clock
		
		addFixedWait(settings.commands, settings.fixed_times, 1000); // Wait 1 second
		
		settings.commands.add(Commands.START_CLOCK); // start clock
		
		addRangedWait(settings.commands, settings.time_ranges, 3000, 6000); // Wait [3 - 6] seconds before emitting tone
		
		settings.commands.add(Commands.EMIT_TONE); // Emit sound
		
		addRangedWait(settings.commands, settings.time_ranges, 1500, 2500);
		
		settings.commands.add(Commands.STOP_CLOCK); // Stop the clock

		addFixedWait(settings.commands, settings.fixed_times, 500); // Wait .5 seconds buffer
		
		settings.commands.add(Commands.STOP_TRIAL);
		settings.commands.add(Commands.QUESTION_TONE);
		settings.commands.add(Commands.QUESTION_CERTAINTY);
		
		return settings;
	}
	
	private static BlockSettings getV1Block3()
	{
		BlockSettings settings = new BlockSettings(_RUN_COUNT, 1, 3, true);
		
		settings.commands.add(Commands.RESET_CLOCK); // Reset clock
		addFixedWait(settings.commands, settings.fixed_times, 1000); // Wait 1 second
		
		settings.commands.add(Commands.START_CLOCK); // start clock
		
		settings.commands.add(Commands.WAIT_FOR_KEY_PRESS);
		
		addFixedWait(settings.commands, settings.fixed_times, 250);
		
		settings.commands.add(Commands.EMIT_TONE); // Emit sound
		
		addRangedWait(settings.commands, settings.time_ranges, 1500, 2500);
		
		settings.commands.add(Commands.STOP_CLOCK); // Stop the clock

		addFixedWait(settings.commands, settings.fixed_times, 500); // Wait .5 seconds buffer
		
		settings.commands.add(Commands.STOP_TRIAL);
		
		settings.commands.add(Commands.QUESTION_PRESS);
		
		return settings;
	}
	
	private static BlockSettings getV1Block4()
	{
		BlockSettings settings = new BlockSettings(_RUN_COUNT, 1, 4, true);
		
		settings.commands.add(Commands.RESET_CLOCK); // Reset clock
		addFixedWait(settings.commands, settings.fixed_times, 1000); // Wait 1 second
		
		settings.commands.add(Commands.START_CLOCK); // start clock
		
		settings.commands.add(Commands.WAIT_FOR_KEY_PRESS);
		
		addFixedWait(settings.commands, settings.fixed_times, 250);
		
		settings.commands.add(Commands.EMIT_TONE); // Emit sound
		
		addRangedWait(settings.commands, settings.time_ranges, 1500, 2500);
		
		settings.commands.add(Commands.STOP_CLOCK); // Stop the clock

		addFixedWait(settings.commands, settings.fixed_times, 500); // Wait .5 seconds buffer
		
		settings.commands.add(Commands.STOP_TRIAL);
		
		settings.commands.add(Commands.QUESTION_TONE);
		settings.commands.add(Commands.QUESTION_CERTAINTY);
		
		return settings;
	}
	
	private static BlockSettings getV2Block1()
	{
		BlockSettings settings = new BlockSettings(_RUN_COUNT, 2, 1, true);
		
		settings.commands.add(Commands.RESET_CLOCK); // Reset clock
		
		addFixedWait(settings.commands, settings.fixed_times, 1000); // Wait 1 second
		
		settings.commands.add(Commands.START_CLOCK); // start clock
				
		settings.commands.add(Commands.WAIT_FOR_KEY_PRESS); // Wait for the user to press
		
		addRangedWait(settings.commands, settings.time_ranges, 1500, 2500); // Wait [1.5 - 2] seconds
		
		settings.commands.add(Commands.STOP_CLOCK); // Stop the clock

		addFixedWait(settings.commands, settings.fixed_times, 500); // Wait .5 seconds buffer
		
		settings.commands.add(Commands.STOP_TRIAL);
		settings.commands.add(Commands.QUESTION_PRESS);
		
		return settings;
	}
	
	private static BlockSettings getV2Block2()
	{
		BlockSettings settings = new BlockSettings(_RUN_COUNT, 2, 2, true);
		
		settings.commands.add(Commands.RESET_CLOCK); // Reset clock
		
		addFixedWait(settings.commands, settings.fixed_times, 1000); // Wait 1 second
		
		settings.commands.add(Commands.START_CLOCK); // start clock
		
		addRangedWait(settings.commands, settings.time_ranges, 3000, 6000); // Wait [3 - 6] seconds before emitting tone
		
		settings.commands.add(Commands.EMIT_TONE); // Emit sound
		
		addRangedWait(settings.commands, settings.time_ranges, 1500, 2500);
		
		settings.commands.add(Commands.STOP_CLOCK); // Stop the clock

		addFixedWait(settings.commands, settings.fixed_times, 500); // Wait .5 seconds buffer
		
		settings.commands.add(Commands.STOP_TRIAL);
		settings.commands.add(Commands.QUESTION_TONE);
		settings.commands.add(Commands.QUESTION_CERTAINTY);
		settings.commands.add(Commands.QUESTION_LOUDNESS);
		
		return settings;
	}
	
	private static BlockSettings getV2Block3()
	{
		BlockSettings settings = new BlockSettings(_RUN_COUNT, 2, 3, true);
		
		settings.commands.add(Commands.RESET_CLOCK); // Reset clock
		addFixedWait(settings.commands, settings.fixed_times, 1000); // Wait 1 second
		
		settings.commands.add(Commands.START_CLOCK); // start clock
		
		settings.commands.add(Commands.WAIT_FOR_KEY_PRESS);
		
		addFixedWait(settings.commands, settings.fixed_times, 250);
		
		settings.commands.add(Commands.EMIT_TONE); // Emit sound
		
		addRangedWait(settings.commands, settings.time_ranges, 1500, 2500);
		
		settings.commands.add(Commands.STOP_CLOCK); // Stop the clock

		addFixedWait(settings.commands, settings.fixed_times, 500); // Wait .5 seconds buffer
		
		settings.commands.add(Commands.STOP_TRIAL);
		
		settings.commands.add(Commands.QUESTION_PRESS);
		settings.commands.add(Commands.QUESTION_CERTAINTY);
		settings.commands.add(Commands.QUESTION_LOUDNESS);
		
		return settings;
	}
	
	private static BlockSettings getV2Block4()
	{
		BlockSettings settings = new BlockSettings(_RUN_COUNT, 2, 4, true);
		
		settings.commands.add(Commands.RESET_CLOCK); // Reset clock
		addFixedWait(settings.commands, settings.fixed_times, 1000); // Wait 1 second
		
		settings.commands.add(Commands.START_CLOCK); // start clock
		
		settings.commands.add(Commands.WAIT_FOR_KEY_PRESS);
		
		addFixedWait(settings.commands, settings.fixed_times, 250);
		
		settings.commands.add(Commands.EMIT_TONE); // Emit sound
		
		addRangedWait(settings.commands, settings.time_ranges, 1500, 2500);
		
		settings.commands.add(Commands.STOP_CLOCK); // Stop the clock

		addFixedWait(settings.commands, settings.fixed_times, 500); // Wait .5 seconds buffer
		
		settings.commands.add(Commands.STOP_TRIAL);
		
		settings.commands.add(Commands.QUESTION_TONE);
		settings.commands.add(Commands.QUESTION_CERTAINTY);
		settings.commands.add(Commands.QUESTION_LOUDNESS);
		
		return settings;
	}
	
	private static void addFixedWait(List<Commands> commands, List<Integer> fixed_times, int duration)
	{
		commands.add(Commands.WAIT_FIXED_DURATION);
		fixed_times.add(duration);
	}
	
	private static void addRangedWait(List<Commands> commands, List<TimeRange> time_ranges, int min, int max)
	{
		commands.add(Commands.WAIT_VARIABLE_DURATION);
		time_ranges.add(new TimeRange(min, max));
	}
}
