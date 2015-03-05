package db;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

public class DBManager {

	public static final String default_db_file = "SOA.db";
	
	File dbFile;
	
	public DBManager()
	{
		this(default_db_file);
	}
	
	public DBManager(String dbFilename)
	{
		// Init the connection
		dbFile = new File(dbFilename);				
		try {
			dbFile.createNewFile();
		} catch (IOException e) {
			System.err.println("Failed to create db file " + dbFilename);
			e.printStackTrace();
			System.exit(1);
		}
		createTables();
	}
	
	public SQLiteConnection openDb(boolean allowCreate) throws SQLiteException
	{
		SQLiteConnection db = new SQLiteConnection(dbFile);
		db.open(allowCreate);
		db.exec("PRAGMA foreign_keys = ON;");
		return db;
	}
	
	private void createTables()
	{
		SQLiteConnection db = null;
		try {
			db = openDb(true);
			db.exec(DBTables.Subjects.table_creation_code);
			db.exec(DBTables.Data.table_creation_code);
		} catch (SQLiteException e) {
			System.out.println("Failed to create one of the tables");
			e.printStackTrace();
		}
		finally
		{
			if(db != null)
				db.dispose();
		}
	}
	
	/************
	 * SUBJECTS *
	 ************/
	public void addNewSubject(Subject subject)
	{
		if(subject == null || subject.name.trim().isEmpty())
		{
			System.err.println("Can't add subject to subject as player name is null or name is empty!");
			return;
		}
		SQLiteConnection db = null;
		try {
			db = openDb(true);
		} catch (SQLiteException e) {
			System.err.println("Failed to open database file: " + dbFile.getAbsolutePath());
			e.printStackTrace();
			return;
		}
		
	    try {
	    	String query = "INSERT INTO " + DBTables.Subjects.table_name + "(" + DBTables.Subjects.Column.subject_name.name + ") "
	    			+ "VALUES( ? );";
	    	
			SQLiteStatement st = db.prepare(query);
			// Perform binding
			st.bind(1, subject.name);
			st.step();
			st.dispose();
			subject.id = (int) db.getLastInsertId();
		} catch (SQLiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if(db != null)
				db.dispose();
		}
	    return;
	}
	
	public void delSubject(Subject subject)
	{
		if(subject != null)
		{
			delSubject(subject.id);
		}
		else
			System.err.println("Can't remove subject from database as subject or name is null!");
	}
	
	public void delSubject(int subjectId)
	{		
		SQLiteConnection db = null;
		try {
			db = openDb(true);
		} catch (SQLiteException e) {
			System.err.println("Failed to open database file: " + dbFile.getAbsolutePath());
			e.printStackTrace();
			return;
		}
		
	    try {
	    	String query = "DELETE FROM " + DBTables.Subjects.table_name + " WHERE " + DBTables.Subjects.Column._id.name + " = ?";
	    	
			SQLiteStatement st = db.prepare(query);
			// Perform binding
			st.bind(1, subjectId);
			st.step();
			st.dispose();
		} catch (SQLiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if(db != null)
				db.dispose();
		}
	    return;
	}
	
	public List<Subject> getSubjects()
	{
		List<Subject> subjects = new ArrayList<Subject>();

		SQLiteConnection db = null;
		try {
			db = openDb(false);
		} catch (SQLiteException e) {
			System.err.println("Failed to open database file: " + dbFile.getAbsolutePath());
			e.printStackTrace();
			return subjects;
		}
		
		String query_str = "SELECT * from " + DBTables.Subjects.table_name + ";";

		try {
			SQLiteStatement query = db.prepare(query_str);
			while(query.step())
			{
				int id = query.columnInt(DBTables.Subjects.Column._id.ordinal());
				String subjectName = query.columnString(DBTables.Subjects.Column.subject_name.ordinal());
				Subject s = new Subject(subjectName);
				s.id = id;
				subjects.add(s);
			}
			query.dispose();
		} catch (SQLiteException e) {
			System.err.println("Failed to fetch subjects from database");
			e.printStackTrace();
		}
		finally
		{
			if(db != null)
				db.dispose();
		}
		return subjects;
	}
	
	
	/*********
	 * PAIRS *
	 *********/
	public ExperimentData getData()
	{
		ExperimentData all_experiment_data = new ExperimentData();
		
		SQLiteConnection db = null;
		try {
			db = openDb(false);
		} catch (SQLiteException e) {
			System.err.println("Failed to open database file: " + dbFile.getAbsolutePath());
			e.printStackTrace();
			return all_experiment_data;
		}
		
		String query_str = "SELECT * from " + DBTables.Data.table_name + ";";
		
		try {
			SQLiteStatement query = db.prepare(query_str);
			while(query.step())
			{
				int subject_id = query.columnInt(DBTables.Data.Column.subject_id.ordinal());
				int block_v = query.columnInt(DBTables.Data.Column.block_version.ordinal());
				int block_id = query.columnInt(DBTables.Data.Column.block_id.ordinal());
				
				int real_time = query.columnInt(DBTables.Data.Column.real_time.ordinal());
				int guessed_time = query.columnInt(DBTables.Data.Column.guessed_time.ordinal());
				int certainty = query.columnInt(DBTables.Data.Column.certainty.ordinal());
				int loudness = query.columnInt(DBTables.Data.Column.loudness.ordinal());

				all_experiment_data.addResult(subject_id, block_v, block_id, new TrialResult(real_time, guessed_time, certainty, loudness));
			}
			query.dispose();
		} catch (SQLiteException e) {
			System.err.println("Failed to fetch pairs from database");
			e.printStackTrace();
		}
		finally
		{
			if(db != null)
				db.dispose();
		}
		return all_experiment_data;
	}
	
	public void insertResults(int subject_id, int version, int block, List<TrialResult> results)
	{
		// First remove any results associated
		removeResults(subject_id, version, block);
		
		for(TrialResult r : results)
			insertResult(subject_id, version, block, r);
	}
	
	public void insertResult(int subject_id, int version, int block, TrialResult result)
	{		
		SQLiteConnection db = null;
		try {
			db = openDb(true);
		} catch (SQLiteException e) {
			System.err.println("Failed to open database file: " + dbFile.getAbsolutePath());
			e.printStackTrace();
			return;
		}
		
		// Build the query
		DBTables.Data.Column[] columns = DBTables.Data.Column.values();
		String query = "INSERT INTO " + DBTables.Data.table_name + "(";
		String subQuery = "VALUES (";
    	for(int i = 1; i < columns.length; i++) 
    	{
    		if(i != 1)
    		{
    			query += ", ";
    			subQuery += ", ";
    		}
    		query += columns[i].name;
    		subQuery += "?";
    	}
    	query = query + ") " + subQuery + ");" ;

	    try {
	    	// Insert the block
			SQLiteStatement st = db.prepare(query);
			st.bind(1, subject_id);
			st.bind(2, version);
			st.bind(3, block);
			st.bind(4, result.real_time);
			st.bind(5, result.guessed_time);
			st.bind(6, result.certainty);
			st.bind(7, result.loudness);
			st.step();
			st.dispose();
		} catch (SQLiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if(db != null)
				db.dispose();		
		}
	}
	
	public void removeResults(int subject_id, int version, int block)
	{		
		SQLiteConnection db = null;
		try {
			db = openDb(true);
		} catch (SQLiteException e) {
			System.err.println("Failed to open database file: " + dbFile.getAbsolutePath());
			e.printStackTrace();
			return;
		}
		
	    try {
	    	String query = "DELETE FROM " + DBTables.Data.table_name + " WHERE " + 
	    			DBTables.Data.Column.subject_id.name + " = ? AND " +
					DBTables.Data.Column.block_version.name + " = ? AND " + 
					DBTables.Data.Column.block_id.name + " = ?;";

			SQLiteStatement st = db.prepare(query);
			// Perform binding
			st.bind(1, subject_id);
			st.bind(2, version);
			st.bind(3, block);

			st.step();
			st.dispose();
		} catch (SQLiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if(db != null)
				db.dispose();
		}
	    return;
	}
}
