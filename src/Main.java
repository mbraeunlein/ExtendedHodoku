import io.LogLevel;
import io.Logger;
import io.SudokuReader;
import io.arffWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import analyze.Analyzer;
import model.Classification;
import model.FeatureVector;
import model.Sudoku;
import solver.SudokuStepFinder;
import sudoku.*;

public class Main {
	// lists with the computed feature vectors
	static ArrayList<FeatureVector> hodokuEasyVectors = new ArrayList<FeatureVector>();
	static ArrayList<FeatureVector> hodokuMiddleVectors = new ArrayList<FeatureVector>();
	static ArrayList<FeatureVector> hodokuHardVectors = new ArrayList<FeatureVector>();
	static ArrayList<FeatureVector> hodokuUnfairVectors = new ArrayList<FeatureVector>();
	static ArrayList<FeatureVector> hodokuExtremeVectors = new ArrayList<FeatureVector>();

	static ArrayList<FeatureVector> soEinDingSehrEinfach = new ArrayList<FeatureVector>();
	static ArrayList<FeatureVector> soEinDingEinfach = new ArrayList<FeatureVector>();
	static ArrayList<FeatureVector> soEinDingStandard = new ArrayList<FeatureVector>();
	static ArrayList<FeatureVector> soEinDingModerat = new ArrayList<FeatureVector>();
	static ArrayList<FeatureVector> soEinDingAnspruchsvoll = new ArrayList<FeatureVector>();
	static ArrayList<FeatureVector> soEinDingSehrAnspruchsvoll = new ArrayList<FeatureVector>();
	static ArrayList<FeatureVector> soEinDingTeuflisch = new ArrayList<FeatureVector>();

	static HashMap<Classification, ArrayList<FeatureVector>> classificatedVectors = new HashMap<Classification, ArrayList<FeatureVector>>();

	static String Hodoku = "hodoku.arff";
	static String SoEinDing = "soeinding.arff";
	
	public static void main(String args[]) throws Exception {
		Logger.addLogLevel(LogLevel.GeneralInformation);
		Logger.addLogLevel(LogLevel.Error);
		// Logger.addLogLevel(LogLevel.SolvingMethods);

		// sr = new SudokuReader("sudokus.txt");
		// Sudoku2 sudoku = sr.read().get(0);
		// SudokuStepFinder sf = new SudokuStepFinder();
		// List<SolutionStep> steps = sf.getAllWings(sudoku);
		// System.out.println(steps.get(0).getStepName());
		
		//Hodoku();
		//SoEinDing();
		
		Analyzer analyzer = new Analyzer();
		analyzer.loadFile(Hodoku, SoEinDing);
		analyzer.mapClasses();
		
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

	public static void Hodoku() {
		SudokuReader sr = null;
		arffWriter aw = new arffWriter(Hodoku);
		// easy
		sr = new SudokuReader("hodoku-easy.txt");
		ArrayList<Sudoku2> sudokusHodokuEasy = sr.read();
		Logger.log(LogLevel.GeneralInformation, "classification: hodoku easy");
		Logger.log(LogLevel.GeneralInformation, sudokusHodokuEasy.size()
				+ " sudokus loaded.");
		hodokuEasyVectors = getFeatureVectors(sudokusHodokuEasy);

		// middle
		sr = new SudokuReader("hodoku-middle.txt");
		ArrayList<Sudoku2> sudokusHodokuMiddle = sr.read();
		Logger.log(LogLevel.GeneralInformation, "classification: hodoku middle");
		Logger.log(LogLevel.GeneralInformation, sudokusHodokuMiddle.size()
				+ " sudokus loaded.");
		hodokuMiddleVectors = getFeatureVectors(sudokusHodokuMiddle);

		// hard
		sr = new SudokuReader("hodoku-hard.txt");
		ArrayList<Sudoku2> sudokusHodokuHard = sr.read();
		Logger.log(LogLevel.GeneralInformation, "classification: hodoku hard");
		Logger.log(LogLevel.GeneralInformation, sudokusHodokuHard.size()
				+ " sudokus loaded.");
		hodokuHardVectors = getFeatureVectors(sudokusHodokuHard);

		// unfair
		sr = new SudokuReader("hodoku-unfair.txt");
		ArrayList<Sudoku2> sudokusHodokuUnfair = sr.read();
		Logger.log(LogLevel.GeneralInformation, "classification: hodoku unfair");
		Logger.log(LogLevel.GeneralInformation, sudokusHodokuUnfair.size()
				+ " sudokus loaded.");
		hodokuUnfairVectors = getFeatureVectors(sudokusHodokuUnfair);

		// extreme
		sr = new SudokuReader("hodoku-extreme.txt");
		ArrayList<Sudoku2> sudokusHodokuExtreme = sr.read();
		Logger.log(LogLevel.GeneralInformation,
				"classification: hodoku extreme");
		Logger.log(LogLevel.GeneralInformation, sudokusHodokuExtreme.size()
				+ " sudokus loaded.");
		hodokuExtremeVectors = getFeatureVectors(sudokusHodokuExtreme);
		
		classificatedVectors.put(Classification.hdkEasy, hodokuEasyVectors);
		classificatedVectors.put(Classification.hdkMiddle,
				hodokuMiddleVectors);
		classificatedVectors.put(Classification.hdkHard, hodokuHardVectors);
		classificatedVectors.put(Classification.hdkUnfair,
				hodokuUnfairVectors);
		classificatedVectors.put(Classification.hdkExtreme,
				hodokuExtremeVectors);
		
		aw.writeToFile(classificatedVectors);
		classificatedVectors.clear();
	}
	
	public static void SoEinDing() {
		SudokuReader sr = null;
		arffWriter aw = new arffWriter(SoEinDing);
		
		// read sudokus generated by soEinDing (classification sehr einfach)
		// from textfile
		sr = new SudokuReader("sehrEinfach.txt");
		ArrayList<Sudoku2> sudokusSoEinDingSehrEinfach = sr.readFromSoEinDing();

		Logger.log(LogLevel.GeneralInformation,
				"classification: soEinDing Sehr Einfach");
		Logger.log(LogLevel.GeneralInformation,
				sudokusSoEinDingSehrEinfach.size() + " sudokus loaded.");

		soEinDingSehrEinfach = getFeatureVectors(sudokusSoEinDingSehrEinfach);

		// read sudokus generated by soEinDing (classification einfach) from
		// textfile
		sr = new SudokuReader("einfach.txt");
		ArrayList<Sudoku2> sudokusSoEinDingEinfach = sr.readFromSoEinDing();

		Logger.log(LogLevel.GeneralInformation,
				"classification: soEinDing Einfach");
		Logger.log(LogLevel.GeneralInformation, sudokusSoEinDingEinfach.size()
				+ " sudokus loaded.");

		soEinDingEinfach = getFeatureVectors(sudokusSoEinDingEinfach);

		// read sudokus generated by soEinDing (classification standard) from
		// textfile
		sr = new SudokuReader("standard.txt");
		ArrayList<Sudoku2> sudokusSoEinDingStandard = sr.readFromSoEinDing();

		Logger.log(LogLevel.GeneralInformation,
				"classification: soEinDing Standard");
		Logger.log(LogLevel.GeneralInformation, sudokusSoEinDingStandard.size()
				+ " sudokus loaded.");

		soEinDingStandard = getFeatureVectors(sudokusSoEinDingStandard);

		// read sudokus generated by soEinDing (classification moderat) from
		// textfile
		sr = new SudokuReader("moderat.txt");
		ArrayList<Sudoku2> sudokusSoEinDingModerat = sr.readFromSoEinDing();

		Logger.log(LogLevel.GeneralInformation,
				"classification: soEinDing Moderat");
		Logger.log(LogLevel.GeneralInformation, sudokusSoEinDingModerat.size()
				+ " sudokus loaded.");

		soEinDingModerat = getFeatureVectors(sudokusSoEinDingModerat);

		// read sudokus generated by soEinDing (classification anspruchsvoll)
		// from textfile
		sr = new SudokuReader("anspruchsvoll.txt");
		ArrayList<Sudoku2> sudokusSoEinDingAnspruchsvoll = sr
				.readFromSoEinDing();

		Logger.log(LogLevel.GeneralInformation,
				"classification: soEinDing Anspruchsvoll");
		Logger.log(LogLevel.GeneralInformation,
				sudokusSoEinDingAnspruchsvoll.size() + " sudokus loaded.");

		soEinDingAnspruchsvoll = getFeatureVectors(sudokusSoEinDingAnspruchsvoll);

		// read sudokus generated by soEinDing (classification sehr
		// anspruchsvoll) from textfile
		sr = new SudokuReader("sehrAnspruchsvoll.txt");
		ArrayList<Sudoku2> sudokusSoEinDingSehrAnspruchsvoll = sr
				.readFromSoEinDing();

		Logger.log(LogLevel.GeneralInformation,
				"classification: soEinDing Sehr Anspruchsvoll");
		Logger.log(LogLevel.GeneralInformation,
				sudokusSoEinDingSehrAnspruchsvoll.size() + " sudokus loaded.");

		soEinDingSehrAnspruchsvoll = getFeatureVectors(sudokusSoEinDingSehrAnspruchsvoll);

		// read sudokus generated by soEinDing (classification teuflisch)
		// from
		// textfile
		sr = new SudokuReader("teuflisch.txt");
		ArrayList<Sudoku2> sudokusSoEinDingTeuflisch = sr.readFromSoEinDing();

		Logger.log(LogLevel.GeneralInformation,
				"classification: soEinDing Teuflisch");
		Logger.log(LogLevel.GeneralInformation,
				sudokusSoEinDingTeuflisch.size() + " sudokus loaded.");

		soEinDingTeuflisch = getFeatureVectors(sudokusSoEinDingTeuflisch);
		
		classificatedVectors.put(Classification.sedSehrEinfach,
				soEinDingSehrEinfach);
		classificatedVectors.put(Classification.sedEinfach,
				soEinDingEinfach);
		classificatedVectors.put(Classification.sedStandard,
				soEinDingStandard);
		classificatedVectors.put(Classification.sedModerat,
				soEinDingModerat);
		classificatedVectors.put(Classification.sedAnspruchsvoll,
				soEinDingAnspruchsvoll);
		classificatedVectors.put(Classification.sedSehrAnspruchsvoll,
				soEinDingSehrAnspruchsvoll);
		classificatedVectors.put(Classification.sedTeuflisch,
				soEinDingTeuflisch);
		
		aw.writeToFile(classificatedVectors);
		classificatedVectors.clear();
	}
}