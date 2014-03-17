package io;

import java.io.*;
import java.util.*;

import sudoku.Sudoku2;

public class SudokuReader {
	private BufferedReader br = null;

	public SudokuReader(String file) {
		try {
			br = new BufferedReader(new FileReader(new File("sudokus\\" + file)));
		} catch (FileNotFoundException e) {
			System.out.println("the given file was not found");
		}
	}

	public ArrayList<Sudoku2> read() {
		ArrayList<Sudoku2> sudokus = new ArrayList<Sudoku2>();

		try {
			if (br != null) {
				String line = br.readLine();
				while (line != null) {
					Sudoku2 sudoku = new Sudoku2();
					sudoku.setSudoku(line);
					sudokus.add(sudoku);
					line = br.readLine();
				}
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

		return sudokus;
	}

	public ArrayList<Sudoku2> readFromSoEinDing() {
		ArrayList<Sudoku2> sudokus = new ArrayList<Sudoku2>();

		try {
			// read the file
			String file = "";
			if (br != null) {
				String line = br.readLine();
				while (line != null) {
					if (line.startsWith("|")) {
						file += line;
					}
					line = br.readLine();
				}
			}

			file = file.replace(" ", "0");
			file = file.replace("|", "");

			String sudokuString = "";
			
			for (int i = 0; i < file.length(); i = i + 81) {
				for (int j = 0; j < 81; j++) {
					sudokuString += file.charAt(i + j);
				}
				Sudoku2 sudoku = new Sudoku2();
				sudoku.setSudoku(sudokuString.replaceAll("0", "."));
				sudokus.add(sudoku);
				sudokuString = "";
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

		return sudokus;
	}
}
