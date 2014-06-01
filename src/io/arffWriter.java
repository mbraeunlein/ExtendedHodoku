package io;

import java.io.*;
import java.util.*;

import model.*;

public class arffWriter {
	private PrintWriter pw = null;
	String filename = "";

	public arffWriter(String file) {
		try {
			pw = new PrintWriter(file, "UTF-8");
			filename = file;
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeToFile(HashMap<String, ArrayList<FeatureVector>> data,
			Set<String> classes, ArrayList<String> keys) {
		// head part
		pw.println("@RELATION sudoku");
		pw.println();

		Set<String> classifications = classes;
		Method[] methods = Method.values();

		for (int m = 0; m < methods.length; m++) {
			for (int i = 0; i < 9; i++) {
				pw.println("@ATTRIBUTE method" + methods[m].toString() + i
						+ " NUMERIC");
			}
		}

		for (int i = 0; i < 9; i++) {
			pw.println("@ATTRIBUTE startNumber" + i + " NUMERIC");
		}

		for (int i = 0; i < 9; i++) {
			pw.println("@ATTRIBUTE startPossibility" + i + " NUMERIC");
		}

		pw.println();
		pw.print("@ATTRIBUTE class {");
		Object[] tmpClas = classifications.toArray();
		for (int c = 0; c < keys.size(); c++) {
			pw.print(keys.get(c));
			if (c != tmpClas.length - 1) {
				pw.print(",");
			}
		}
		pw.println("}");

		// Data part
		pw.println("@DATA");

		for (int c = 0; c < tmpClas.length; c++) {
			String classification = (String) tmpClas[c];
			if (data.containsKey(classification)) {
				ArrayList<FeatureVector> cData = data.get(classification);

				for (int i = 0; i < cData.size(); i++) {
					writeFeatureVector(classification, cData.get(i));
				}
			}
		}

		pw.flush();
		pw.close();

		Logger.log(LogLevel.GeneralInformation, "Wrote to file " + filename);
	}

	public void writeToFile(HashMap<String, ArrayList<FeatureVector>> data,
			ArrayList<String> keys) {

		writeToFile(data, data.keySet(), keys);

	}

	private void writeFeatureVector(String c, FeatureVector fv) {
		Method[] methods = Method.values();

		for (int m = 0; m < methods.length; m++) {
			for (int i = 0; i < 9; i++) {
				pw.print(fv.getMethods().get(methods[m]).get(i) + ",");
			}
		}
		
		for (int i = 0; i < 9; i++) { pw.print(fv.numbers.get(i) + ","); }
		 

		for (int i = 0; i < 9; i++) {
			pw.print(fv.possibilities.get(i) + ",");
		}

		pw.println(c.toString());
	}
}
