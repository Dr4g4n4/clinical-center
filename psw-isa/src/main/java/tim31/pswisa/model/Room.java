package tim31.pswisa.model;

import java.util.ArrayList;
import java.util.Date;

public class Room {
	private String name;
	private String type;
	private boolean isFree;	//dostupnost sale
	private int num;	//broj sale
	
	private ArrayList<Checkup> bookedCheckups;	//zakazani pregledi
	private ArrayList<Date> freeDates;		//slobodni termini ?
	
	public Room() {
		super();
		this.bookedCheckups = new ArrayList<Checkup>();	//zakazani pregledi
		this.freeDates = new ArrayList<Date>();	
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isFree() {
		return isFree;
	}

	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public ArrayList<Checkup> getBookedCheckups() {
		return bookedCheckups;
	}

	public void setBookedCheckups(ArrayList<Checkup> bookedCheckups) {
		this.bookedCheckups = bookedCheckups;
	}

	public ArrayList<Date> getFreeDates() {
		return freeDates;
	}

	public void setFreeDates(ArrayList<Date> freeDates) {
		this.freeDates = freeDates;
	}
	
	
	
	
	
	
	
	
	
}
