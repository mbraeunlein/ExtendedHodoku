package io;

import java.io.*;
import java.util.*;

import sudoku.Sudoku2;
import io.Logger;

public class SudokuReader {
	
	public HashMap<String, ArrayList<Sudoku2>> read(String file) {
		HashMap<String, ArrayList<Sudoku2>> sudokuMap = new HashMap<String, ArrayList<Sudoku2>>();
		BufferedReader br = null;

		try {
			br = new BufferedReader(
					new FileReader(new File("sudokus\\" + file)));
		} catch (FileNotFoundException e) {
			System.out.println("the given file was not found");
		}

		try {
			if (br != null) {
				String classification = "";
				ArrayList<Sudoku2> sudokus = new ArrayList<Sudoku2>();

				String line = br.readLine();
				while (line != null) {
					if (Character.isDigit(line.charAt(0))) {
						// line starts with digit, assumed to be a sudoku
						Sudoku2 sudoku = new Sudoku2();
						sudoku.setSudoku(line);
						sudokus.add(sudoku);
					} else {
						// new classification line, store the old sudokus
						if (!classification.equals("")) {
							sudokuMap.put(file.replace(".txt", "") + "_" + classification, sudokus);
							Logger.log(LogLevel.GeneralInformation, file.replace(".txt", "") + "_" + classification + " -> " + sudokus.size() + " sudokus loaded" );
							sudokus = new ArrayList<Sudoku2>();
						} else {
							if (sudokus.size() != 0)
								Logger.log(LogLevel.Error, "unclassified sudokus were read, discarding "
												+ sudokus.size() + " sudokus!!");
						}
						classification = line;
					}
					line = br.readLine();
				}

				sudokuMap.put(file.replace(".txt", "") + "_" + classification, sudokus);
				Logger.log(LogLevel.GeneralInformation, file.replace(".txt", "") + "_" + classification + " -> " + sudokus.size() + " sudokus loaded" );
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println();

		return sudokuMap;
	}
}
