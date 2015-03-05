package db;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Subject_id -> <Version_id,Block_id>
public class ExperimentData extends HashMap<Integer,HashMap<Integer,HashMap<Integer, List<TrialResult>>>> {
	
	public void addResult(int subject_id, int version_id, int block_id, TrialResult result)
	{
		if(!hasSubject(subject_id))
			addSubject(subject_id);
		if(!hasVersion(subject_id, version_id))
			addVersion(subject_id, version_id);
		if(!hasBlock(subject_id, version_id, block_id))
			addBlock(subject_id, version_id, block_id);
		
		get(subject_id).get(version_id).get(block_id).add(result);
	}
	
	public void addResults(int subject_id, int version_id, int block_id, List<TrialResult> results)
	{
		if(!hasSubject(subject_id))
			addSubject(subject_id);
		if(!hasVersion(subject_id, version_id))
			addVersion(subject_id, version_id);
		if(!hasBlock(subject_id, version_id, block_id))
			addBlock(subject_id, version_id, block_id);
		
		get(subject_id).get(version_id).get(block_id).addAll(results);
	}
	
	// Subject
	public boolean hasSubject(int subject_id)
	{
		return containsKey(subject_id);
	}
	public void addSubject(int subject_id)
	{
		put(subject_id, new HashMap<Integer, HashMap<Integer,List<TrialResult>>>());
	}
	public void removeSubject(int subject_id)
	{
		remove(subject_id);
	}
	
	// Version
	public boolean hasVersion(int subject_id, int version_id)
	{
		return hasSubject(subject_id) && get(subject_id).containsKey(version_id);
	}
	public void addVersion(int subject_id, int version_id)
	{
		get(subject_id).put(version_id, new HashMap<Integer,List<TrialResult>>());
	}
	
	// Block
	public boolean hasBlock(int subject_id, int version_id, int block_id)
	{
		return hasVersion(subject_id, version_id) && get(subject_id).get(version_id).containsKey(block_id);
	}
	public void addBlock(int subject_id, int version_id, int block_id)
	{
		get(subject_id).get(version_id).put(block_id, new ArrayList<TrialResult>());
	}
	
	public static final String _COLUMNS = "Subject, version, block, trial, real time, guessed time, certainty, loudness " + System.lineSeparator();
	public boolean export(FileWriter fw, Map<Integer, Subject> subject_id_to_subject_mapper) throws IOException
	{
		fw.write(_COLUMNS);
		for(Integer subject_id : keySet())
		{
			String subject_name = subject_id_to_subject_mapper.get(subject_id).name;
			
			for(Integer version : get(subject_id).keySet())
			{
				for(Integer block : get(subject_id).get(version).keySet())
				{
					int i = 1;
					for(TrialResult result : get(subject_id).get(version).get(block))
					{
						fw.write(subject_name + ",");
						fw.write(version  + ",");
						fw.write(block + ",");
						fw.write(i++ + ",");
						fw.write(result.real_time + ",");
						fw.write(result.guessed_time + ",");
						fw.write(result.certainty + ",");
						fw.write(result.loudness + ",");
						fw.write(System.lineSeparator());
					}
				}
			}
		}
		fw.close();
		return true;
	}
}
