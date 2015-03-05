package db;

public class Subject {
	public String name;
	public int id;
	
	public Subject(String name)
	{
		this.name = name;
	}
	
	public Subject(String name, int id)
	{
		this.name = name;
		this.id = id;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
