import java.util.ArrayList;
import java.util.List;

public class BlockSettings {	
	public List<BlockSettingsFactory.Commands> commands;
	public List<Integer> fixed_times;
	public List<TimeRange> time_ranges;
	int run_count;
	int version;
	int block;
	boolean colorChanging;
	
	BlockSettings(int run_count, int version, int block, boolean colorChanging)
	{
		this.run_count = run_count;
		this.version = version;
		this.block = block;
		this.colorChanging = colorChanging;
		
		this.commands = new ArrayList<>();
		this.fixed_times = new ArrayList<>();
		this.time_ranges = new ArrayList<>();
	}
	
	BlockSettings( List<BlockSettingsFactory.Commands> commands, List<Integer> fixed_times, List<TimeRange> time_ranges, int run_count, int version, int block,
			boolean colorChanging)
	{
		this.commands = commands;
		this.fixed_times = fixed_times;
		this.time_ranges = time_ranges;
		this.run_count = run_count;
		this.version = version;
		this.block = block;
		this.colorChanging = colorChanging;
	}
}
