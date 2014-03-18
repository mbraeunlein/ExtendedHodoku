import io.LogLevel;
import io.Logger;
import io.SudokuReader;
import io.arffWriter;

import java.util.ArrayList;
import java.util.HashMap;

import analyze.Analyzer;
import model.Classification;
import model.FeatureVector;
import model.Sudoku;
import sudoku.*;

public class Main {
	// maps class names to a list of computed featurevectors
	static HashMap<String, ArrayList<FeatureVector>> classificatedTrainVectors = new HashMap<String, ArrayList<FeatureVector>>();
	static HashMap<String, ArrayList<FeatureVector>> classificatedTestVectors = new HashMap<String, ArrayList<FeatureVector>>();
	// maps class names to a list of train sudokus
	static HashMap<String, ArrayList<Sudoku2>> trainSudokus = new HashMap<String, ArrayList<Sudoku2>>();
	// maps class names to a list of test sudokus
	static HashMap<String, ArrayList<Sudoku2>> testSudokus = new HashMap<String, ArrayList<Sudoku2>>();

	public static void main(String args[]) throws Exception {
		// configure loffer
		Logger.addLogLevel(LogLevel.GeneralInformation);
		Logger.addLogLevel(LogLevel.Error);
		Logger.addLogLevel(LogLevel.Classification);
		
		// read the mode
		String mode = "";
		try {
			mode = args[0];
		} catch (Exception e) {
			Logger.log(LogLevel.Error, "please put in a mode (cross, test, map)");
			System.exit(-1);
		}

		
		
		// load train sudokus (train sudokus always have to be loaded no matter what mode)
		SudokuReader sr = new SudokuReader();
		try {
			trainSudokus = sr.read(args[1]);
		} catch(Exception e) {
			Logger.log(LogLevel.Error, "please provide a filename for training data, has to be in subfolder sudokus");
			System.exit(-1);
		}
		
		// extract feature vectors
		for (String key : trainSudokus.keySet()) {
			ArrayList<Sudoku2> sudokus = trainSudokus.get(key);
			Logger.log(LogLevel.GeneralInformation, "Solving " + sudokus.size() + " sudokus of " + key);
			classificatedTrainVectors.put(key, getFeatureVectors(sudokus));
		}
		
		// write the feature vectors to an .arff file for possible later user processing
		arffWriter aw = new arffWriter(args[1].replace(".txt", ".arff"));
		aw.writeToFile(classificatedTrainVectors);
		
		Analyzer analyzer = new Analyzer();
		
		// analyze the data depending on the mode
		switch (mode) {
		case "cross":
			analyzer.crossValidation(args[1].replace(".txt", ".arff"));
			System.exit(0);
			break;
		case "test":
			try {
				// load test file
				testSudokus = sr.read(args[2]);
				
				// extract feature vectors
				for (String key : testSudokus.keySet()) {
					ArrayList<Sudoku2> sudokus = testSudokus.get(key);
					Logger.log(LogLevel.GeneralInformation, "Solving " + sudokus.size() + " sudokus of " + key);
					classificatedTestVectors.put(key, getFeatureVectors(sudokus));
				}
				
				// write the feature vectors to an .arff file for possible later user processing
				aw = new arffWriter(args[2].replace(".txt", ".arff"));
				aw.writeToFile(classificatedTrainVectors);
				
				analyzer.test(args[1].replace(".txt", ".arff"), args[2].replace(".txt", ".arff"));
			} catch(Exception e) {
				Logger.log(LogLevel.Error, "please provide a filename for test data, has to be in subfolder sudokus");
				System.exit(-1);
			}
			
			break;
		case "map":
			try {
				// load test file
				testSudokus = sr.read(args[2]);
				/*
				// extract feature vectors
				for (String key : testSudokus.keySet()) {
					ArrayList<Sudoku2> sudokus = testSudokus.get(key);
					Logger.log(LogLevel.GeneralInformation, "Solving " + sudokus.size() + " sudokus of " + key);
					classificatedTestVectors.put(key, getFeatureVectors(sudokus));
				}
				
				// write the feature vectors to an .arff file for possible later user processing
				aw = new arffWriter(args[2].replace(".txt", ".arff"));
				aw.writeToFile(classificatedTestVectors);*/
				
				analyzer.mapClasses(args[2].replace(".txt", ".arff"), args[1].replace(".txt", ".arff"));
			} catch(Exception e) {
				Logger.log(LogLevel.Error, "please provide a filename for test data, has to be in subfolder sudokus");
				System.exit(-1);
			}
			
			
		}

		Logger.exit();
	}

	public static ArrayList<FeatureVector> getFeatureVectors(
			ArrayList<Sudoku2> sudokus) {
		ArrayList<FeatureVector> fvs = new ArrayList<FeatureVector>();

		int solvedCount = 0;
		int notSolvedCount = 0;

		for (int i = 0; i < sudokus.size(); i++) {
			FeatureVectorExtractor fvex = new FeatureVectorExtractor(
					sudokus.get(i));
			fvs.add(fvex.getFeatureVector());
			if (fvex.isSolved())
				solvedCount++;
			else
				notSolvedCount++;
		}

		Logger.log(LogLevel.GeneralInformation, "Solved " + solvedCount
				+ " sudokus");
		Logger.log(LogLevel.GeneralInformation, "Couldn´t solve "
				+ notSolvedCount + " sudokus\n");

		return fvs;
	}
}