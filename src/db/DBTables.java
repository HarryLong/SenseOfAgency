package db;

public class DBTables {		
	public static class Subjects  {
		public static String table_name = "subjects";
		
		public static enum Column{
			_id("id", SQLITE_TYPE.INTEGER), subject_name("subject_name", SQLITE_TYPE.TEXT);
			
			Column(String name, String type)
			{
				this.name = name;
				this.type = type;
			}
			String name;
			String type;
		}
		
		public static final String table_creation_code = 
				"CREATE TABLE IF NOT EXISTS " + table_name + "(" + 
						Column._id.name + " " + Column._id.type + " PRIMARY KEY," +
						Column.subject_name.name + " " + Column.subject_name.type +
						");";
	}
	
	public static class Data {
		public static String table_name = "data";
		
		public static enum Column{
			_id("id", SQLITE_TYPE.INTEGER), 
			subject_id("subject_1_id", SQLITE_TYPE.INTEGER),
			block_version("block_version", SQLITE_TYPE.INTEGER),
			block_id("block_id", SQLITE_TYPE.INTEGER),
			real_time("real_time", SQLITE_TYPE.INTEGER),
			guessed_time("guessed_time", SQLITE_TYPE.INTEGER),
			certainty("certainty", SQLITE_TYPE.INTEGER),
			loudness("loudness", SQLITE_TYPE.INTEGER),
			correct_key("correct_key", SQLITE_TYPE.INTEGER);
						
			Column(String name, String type)
			{
				this.name = name;
				this.type = type;
			}
			String name;
			String type;
		}
		
		public static final String table_creation_code = 
				"CREATE TABLE IF NOT EXISTS " + table_name + "(" + 
						Column._id.name + " " + Column._id.type + " PRIMARY KEY," +
						Column.subject_id.name + " " + Column.subject_id.type + "," +
						Column.block_version.name + " " + Column.block_version.type + "," +
						Column.block_id.name + " " + Column.block_id.type + "," +
						Column.real_time.name + " " + Column.real_time.type + "," +
						Column.guessed_time.name + " " + Column.guessed_time.type + "," +
						Column.certainty.name + " " + Column.certainty.type + "," +
						Column.loudness.name + " " + Column.loudness.type + "," +
						Column.correct_key.name + " " + Column.correct_key.type + "," +

						/* FOREIGN KEYS */
						"FOREIGN KEY(" +Column.subject_id.name + ") REFERENCES " + Subjects.table_name + "(" + Subjects.Column._id.name + ") ON UPDATE CASCADE ON DELETE CASCADE" +	
						");";
	}
	
	private static class SQLITE_TYPE{
		private static final String NULL = "NULL";
		private static final String INTEGER = "INTEGER";
		private static final String REAL = "REAL";
		private static final String TEXT = "TEXT";
		private static final String BLOB = "BLOB";
	}
}
