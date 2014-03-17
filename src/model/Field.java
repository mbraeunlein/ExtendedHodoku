package model;

import java.util.*;

public class Field {
	public int number;
	public ArrayList<Integer> posibilities = new ArrayList<Integer>();
	
	public Field() {
		number = 0;
	}
	
	public Field(int Number) {
		number = Number;
	}
	
	public String toString() {
		return "" + number;
	}
}
