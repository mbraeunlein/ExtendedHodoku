package io;

import java.io.*;
import java.util.*;

import model.*;

public class arffWriter {
	private PrintWriter pw = null;

	public arffWriter(String file) {
		try {
			pw = new PrintWriter(file, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeToFile(
			HashMap<Classification, ArrayList<FeatureVector>> data) {

		// head part
		pw.println("@RELATION sudoku");
		pw.println();

		Classification[] classifications = Classification.values();
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
		for (int c = 0; c < classifications.length; c++) {
			pw.print(classifications[c]);
			if (c != classifications.length - 1) {
				pw.print(",");
			}
		}
		pw.println("}");

		// Data part
		pw.println("@DATA");

		for (int c = 0; c < classifications.length; c++) {
			Classification classification = classifications[c];
			if (data.containsKey(classification)) {
				ArrayList<FeatureVector> cData = data.get(classification);

				for (int i = 0; i < cData.size(); i++) {
					writeFeatureVector(classification, cData.get(i));
				}
			}
		}

		pw.flush();
		pw.close();

		Logger.log(LogLevel.GeneralInformation, "Wrote to arff file");
	}

	private void writeFeatureVector(Classification c, FeatureVector fv) {
		Method[] methods = Method.values();

		for (int m = 0; m < methods.length; m++) {
			for (int i = 0; i < 9; i++) {
				pw.print(fv.getMethods().get(methods[m]).get(i) + ",");
			}
		}

		for (int i = 0; i < 9; i++) {
			pw.print(fv.numbers.get(i) + ",");
		}

		for (int i = 0; i < 9; i++) {
			pw.print(fv.possibilities.get(i) + ",");
		}

		pw.println(c.toString());
	}
}
