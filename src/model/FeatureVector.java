package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class FeatureVector {
	// Häufigkeitsverteilung
	private HashMap<Method, HashMap<Integer, Integer>> methods = new HashMap<Method, HashMap<Integer, Integer>>();
	public ArrayList<Integer> numbers = new ArrayList<Integer>();
	public ArrayList<Integer> possibilities = new ArrayList<Integer>();

	public FeatureVector() {
		Method[] m = Method.values();

		// iterate over every method
		for (int i = 0; i < m.length; i++) {
			HashMap<Integer, Integer> removedCounterList = new HashMap<Integer, Integer>();
			// iterate over every number
			for (int j = 1; j < 10; j++) {
				removedCounterList.put(j, 0);
			}
			methods.put(m[i], removedCounterList);
		}

		// iterate over every number
		for (int i = 1; i < 10; i++) {
			numbers.add(0);
		}

		// iterate over every possibilitiy
		for (int i = 1; i < 10; i++) {
			possibilities.add(0);
		}
	}
	
	// anzahl unabhängig von der konkreten zahl machen, höchste anzahl zuerst
	public HashMap<Method, ArrayList<Integer>> getMethods() {
		HashMap<Method, ArrayList<Integer>> sortedMethods = new HashMap<Method, ArrayList<Integer>>();
		
		Method[] m = Method.values();

		// iterate over every method
		for (int i = 0; i < m.length; i++) {
			ArrayList<Integer> sortedCount = new ArrayList<Integer>();
			sortedCount.add(0);
			HashMap<Integer, Integer> removedCounterList = methods.get(m[i]);

			
			Set<Integer> keys = removedCounterList.keySet();
			Iterator<Integer> it = keys.iterator();
			
			while(it.hasNext()) {
				int next = it.next();
				int count = removedCounterList.get(next);
				
				for(int j = 0; j < sortedCount.size(); j++) {
					if(sortedCount.get(j) <= count) {
						sortedCount.add(j, count);
						break;
					}
				}
			}
			
			sortedCount.remove(9);
			sortedMethods.put(m[i], sortedCount);
		}
		
		return sortedMethods;
	}

	public void addMethod(Method method, int number) {
		addMethod(method, number, 1);
	}

	public void addMethod(Method method, int number, int count) {
		HashMap<Integer, Integer> removedCounterList = new HashMap<Integer, Integer>();
		if (methods.containsKey(method))
			removedCounterList = methods.get(method);

		int removedCounter = 0;
		if (removedCounterList.containsKey(number))
			removedCounter = removedCounterList.get(number);

		removedCounter += count;

		removedCounterList.put(number, removedCounter);
		methods.put(method, removedCounterList);
	}

	public void addNumberCount(int count) {
		numbers.add(count);
		for(int i = 0; i < numbers.size(); i++) {
			if(numbers.get(i) < count) {
				numbers.add(i, count);
				return;
			}
		}
	}

	public void addPossibilityCount(int count) {
		possibilities.add(count);
		for(int i = 0; i < possibilities.size(); i++) {
			if(possibilities.get(i) < count) {
				possibilities.add(i, count);
				return;
			}
		}
	}

	@Override
	public String toString() {
		String s = "";

		s += "Used methods:\n";
		HashMap<Method, ArrayList<Integer>> methodList = getMethods();
		Method[] m = Method.values();

		// iterate over every method
		for (int i = 0; i < m.length; i++) {
			s += "\n\n\t" + m[i].toString() + "\n\n";

			ArrayList<Integer> removedCounterList = new ArrayList<Integer>();
			if (methodList.containsKey(m[i]))
				removedCounterList = methodList.get(m[i]);

			// iterate over every number
			for (int j = 0; j < 9; j++) {
				int count = removedCounterList.get(j);
				s += "\t\t" + j + ": " + count + "\n";
			}
		}

		s += "\n\n\namount of already filled in numbers at the start\n\n";
		// iterate over every number
		for (int i = 0; i < 9; i++) {
			int count = numbers.get(i);

			s += "\t" + i + ": " + count + "\n";
		}

		s += "\n\n\namount of possible fields at the start\n\n";
		// iterate over every possibility
		for (int i = 0; i < 9; i++) {
			int count = possibilities.get(i);

			s += "\t" + i + ": " + count + "\n";
		}
		return s;
	}
}
