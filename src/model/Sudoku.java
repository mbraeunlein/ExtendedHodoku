package model;

import io.LogLevel;
import io.Logger;

import java.util.*;

public class Sudoku {
	// Erst Reihe dann Spalte
	private Field[][] board = new Field[9][9];

	// Ein String aus 81 Ziffern zwischen 0 und 9 repräsentiert ein Sudoku Feld
	// 0 steht hierbei für nicht ausgefüllte Felder
	public Sudoku(String sudokuString) {
		try {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					board[i][j] = new Field(sudokuString.charAt(9 * i + j) - 48);
					if (board[i][j].number == 0) {
						board[i][j].posibilities.add(1);
						board[i][j].posibilities.add(2);
						board[i][j].posibilities.add(3);
						board[i][j].posibilities.add(4);
						board[i][j].posibilities.add(5);
						board[i][j].posibilities.add(6);
						board[i][j].posibilities.add(7);
						board[i][j].posibilities.add(8);
						board[i][j].posibilities.add(9);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error while parsing Sudoku." + e.getMessage());
		}
	}

	public String toString() {
		String s = "";
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (board[i][j].number == 0)
					s += "_";
				else
					s += board[i][j];
				s += " ";
			}
			s += "\n";
		}
		return s;
	}

	public void setField(int row, int column, Field field) {
		board[row][column] = field;
	}

	public void setFieldInBlock(int blockNumber, int position, Field field) {
		if (blockNumber < 0 || blockNumber > 8) {
			Logger.log(LogLevel.Error, "the block number has to be between 0 and 8");
		}
		if (position < 0 || position > 8) {
			Logger.log(LogLevel.Error, "the position has to be between 0 and 8");
		}

		// calculate x and y position of the block
		int xBlockOffset = blockNumber % 3;
		int yBlockOffset = 0;

		if (blockNumber > 2)
			yBlockOffset = 1;
		if (blockNumber > 5)
			yBlockOffset = 2;

		// calculate x and y position in the block
		int xInBlockOffset = position % 3;
		int yInBlockOffset = 0;

		if (position > 2)
			yInBlockOffset = 1;
		if (position > 5)
			yInBlockOffset = 2;

		int xOffset = 3 * xBlockOffset + xInBlockOffset;
		int yOffset = 3 * yBlockOffset + yInBlockOffset;

		board[yOffset][xOffset] = field;
	}

	public Field[] get(Figure figure, int number) {
		if (figure == Figure.Row) {
			if (number < 0 || number > 8) {
				Logger.log(LogLevel.Error, "the number has to be between 0 and 8");
			}
			Field[] row = new Field[9];
			for (int i = 0; i < 9; i++) {
				row[i] = board[number][i];
			}
			return row;
		}
		if (figure == Figure.Column) {
			if (number < 0 || number > 8) {
				Logger.log(LogLevel.Error, "the number has to be between 0 and 8");
			}
			Field[] column = new Field[9];
			for (int i = 0; i < 9; i++) {
				column[i] = board[i][number];
			}
			return column;
		}
		if (figure == Figure.Block) {
			if (number < 0 || number > 8) {
				Logger.log(LogLevel.Error, "the number has to be between 0 and 8");
			}
			Field[] block = new Field[9];

			int xStart = number % 3;
			int yStart = 0;

			if (number > 2)
				yStart = 1;
			if (number > 5)
				yStart = 2;

			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					block[3 * i + j] = board[yStart * 3 + i][xStart * 3 + j];
				}
			}
			return block;
		}

		return null;
	}

	public int getContainingBlockNumber(int row, int column) {
		int number = 0;

		if (row < 9) {
			if (column < 9)
				number = 8;
			if (column < 6)
				number = 7;
			if (column < 3)
				number = 6;
		}
		if (row < 6) {
			if (column < 9)
				number = 5;
			if (column < 6)
				number = 4;
			if (column < 3)
				number = 3;
		}
		if (row < 3) {
			if (column < 9)
				number = 2;
			if (column < 6)
				number = 1;
			if (column < 3)
				number = 0;
		}
		return number;
	}

	// return the field identified by the row and column
	public Field getField(int row, int column) {
		return board[row][column];
	}

	// the number of possible numbers to insert
	public int possibilityCount(int number) {
		int count = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if(number == 0) {
					count += board[i][j].posibilities.size();
				}
				else {
					if(board[i][j].posibilities.contains(number))
						count++;
				}
					
			}
		}
		return count;
	}

	// the number of filled fields
	public int numberCount(int number) {
		int count = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (number == 0) {
					if(board[i][j].number == 0)
						count++;
				} else {
					if (board[i][j].number == number)
						count ++;
				}
			}
		}
		return count;
	}

	// removes a possibility in a field specified by the row and the column
	public void removePosibility(int row, int column, int number) {
		if (row < 0 || row > 8)
			Logger.log(LogLevel.Error, "row has to be between 0 and 8");
		if (column < 0 || column > 8)
			Logger.log(LogLevel.Error, "column has to be between 0 and 8");
		if (number < 1 || number > 9)
			Logger.log(LogLevel.Error, "number has to be between 1 and 9");
		if (board[row][column].posibilities.size() == 0)
			return;

		ArrayList<Integer> newPosibilities = new ArrayList<Integer>();
		ArrayList<Integer> posibilities = board[row][column].posibilities;
		for (int i = 0; i < posibilities.size(); i++) {
			int pos = posibilities.get(i);
			if (pos != number)
				newPosibilities.add(pos);
		}

		if (newPosibilities.size() == 0)
			Logger.log(LogLevel.Error, "no possibilities left");

		board[row][column].posibilities = newPosibilities;
	}

	public void removePossibilityInBlock(int blockNumber, int position,
			int number) {
		if (blockNumber < 0 || blockNumber > 8) {
			Logger.log(LogLevel.Error, "the block number has to be between 0 and 8");
		}
		if (position < 0 || position > 8) {
			Logger.log(LogLevel.Error, "the position has to be between 0 and 8");
		}

		// calculate x and y position of the block
		int xBlockOffset = blockNumber % 3;
		int yBlockOffset = 0;

		if (blockNumber > 2)
			yBlockOffset = 1;
		if (blockNumber > 5)
			yBlockOffset = 2;

		// calculate x and y position in the block
		int xInBlockOffset = position % 3;
		int yInBlockOffset = 0;

		if (position > 2)
			yInBlockOffset = 1;
		if (position > 5)
			yInBlockOffset = 2;

		int xOffset = 3 * xBlockOffset + xInBlockOffset;
		int yOffset = 3 * yBlockOffset + yInBlockOffset;

		removePosibility(yOffset, xOffset, number);
	}

	// indicates weather the given number is in the figure indetified by the id
	public boolean isIn(Figure figure, int id, int number) {
		Field[] x = get(figure, id);
		for (int i = 0; i < 9; i++) {
			if (number == x[i].number)
				return true;
		}
		return false;
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Sudoku))
			return false;
		Sudoku sudoku = (Sudoku) obj;

		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				Field field = sudoku.getField(row, col);
				// are the numbers equal?
				if (field.number != 0) {
					if (field.number != this.getField(row, col).number) {
						return false;
					}
				} else {
					// are the possibilities equal?
					if (field.posibilities.size() == this.getField(row, col).posibilities
							.size()) {
						for (int pos = 0; pos < field.posibilities.size(); pos++) {
							if (field.posibilities.get(pos) != this.getField(
									row, col).posibilities.get(pos))
								return false;
						}
					} else {
						return false;
					}
				}
			}
		}

		return true;
	}

}
