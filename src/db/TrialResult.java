package db;

public class TrialResult {
	public long real_time;
	public long guessed_time;
	public int certainty;
	public int loudness;
	
	public TrialResult(TrialResult other)
	{
		this.real_time = other.real_time;
		this.guessed_time = other.guessed_time;
		this.certainty =other. certainty;
		this.loudness = other.loudness;
	}
	
	public TrialResult(long real_time, long guessed_time, int certainty, int loudness) {
		this.real_time = real_time;
		this.guessed_time = guessed_time;
		this.certainty = certainty;
		this.loudness = loudness;
	}
	
	public TrialResult()
	{
		reset();
	}
	
	public void reset()
	{
		real_time = guessed_time = certainty = loudness = -1;
	}
}
