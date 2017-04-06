package mspInstance;

/**
 * Contains the arrival and departure time. 
 * Current implementation does not contain train units. 
 * @author daan.vandenheuvel
 *
 */
public class Train {
	private int releaseDate;  
	private int dueDate; 
	private int length; 
	private String name;
	
	private Train() {
	}

	public Train(String name, int length) {
		this(); 
		this.name = name;
		this.length = length; 
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	public int getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(int release_date) {
		this.releaseDate = release_date;
	}

	public int getDueDate() {
		return dueDate;
	}

	public void setDueDate(int due_date) {
		this.dueDate = due_date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		sb.append(this.name + ":,\t l=" + this.length + ",\t release=" + this.releaseDate + ",\t due= " + this.dueDate);
		sb.append(",\t tasks=["); 
		sb.append("]"); 
		//return sb.toString();
		return "Train " + this.name; 
	}

}
