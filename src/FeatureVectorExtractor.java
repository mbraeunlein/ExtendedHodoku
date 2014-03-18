import java.util.ArrayList;
import java.util.List;

import io.LogLevel;
import io.Logger;
import model.FeatureVector;
import model.Method;
import solver.SudokuStepFinder;
import sudoku.Candidate;
import sudoku.ClipboardMode;
import sudoku.SolutionStep;
import sudoku.Sudoku2;

public class FeatureVectorExtractor {
	private Sudoku2 sudoku = null;
	FeatureVector fv = new FeatureVector();
	public int step = 0;
	private SudokuStepFinder sf = new SudokuStepFinder();

	public FeatureVectorExtractor(Sudoku2 sudoku) {
		this.sudoku = sudoku;
	}

	public boolean isSolved() {
		return sudoku.isSolved();
	}

	public FeatureVector getFeatureVector() {
		if (sudoku != null) {
			// compute the numbers at the start
			String sudokuString = sudoku.getInitialState();
			for (int i = 1; i < 10; i++) {
				fv.addNumberCount(sudokuString.length()
						- sudokuString.replace(i + "", "").length());
			}

			// compute possibilities at the start
			for (int i = 1; i < 10; i++)
				fv.addPossibilityCount(sudoku.getCandidateCount(i));

			boolean cont = true;
			while (cont) {
				cont = step();
			}
		}

		return fv;
	}

	public boolean step() {
		step++;
		List<SolutionStep> steps = new ArrayList<SolutionStep>();

		// ----------------------------------------- //
		// Methods to fill in digits //
		// ----------------------------------------- //
		steps = sf.findAllFullHouses(sudoku);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();
			fv.addMethod(Method.FullHouse, steps.get(0).getValues().get(0));
			Logger.log(LogLevel.SolvingMethods, steps.get(0).toString());
			return true;
		}

		steps = sf.findAllNakedSingles(sudoku);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();
			fv.addMethod(Method.NakedSingles, steps.get(0).getValues().get(0));
			Logger.log(LogLevel.SolvingMethods, steps.get(0).toString());
			return true;
		}

		steps = sf.findAllHiddenSingles(sudoku);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();
			fv.addMethod(Method.HiddenSingles, steps.get(0).getValues().get(0));
			Logger.log(LogLevel.SolvingMethods, steps.get(0).toString());
			return true;
		}

		// ----------------------------------------- //
		// Locked Candidates //
		// ----------------------------------------- //
		steps = sf.findAllLockedCandidates1(sudoku);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();
			fv.addMethod(Method.LockedCandidates1, steps.get(0).getValues()
					.get(0), steps.get(0).getCandidatesToDelete().size());
			Logger.log(LogLevel.SolvingMethods, steps.get(0).toString());
			return true;
		}
		steps = sf.findAllLockedCandidates2(sudoku);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();
			fv.addMethod(Method.LockedCandidates2, steps.get(0).getValues()
					.get(0), steps.get(0).getCandidatesToDelete().size());
			Logger.log(LogLevel.SolvingMethods, steps.get(0).toString());
			return true;
		}

		// ----------------------------------------- //
		// Naked Subsets //
		// ----------------------------------------- //
		steps = sf.findAllNakedPairs(sudoku);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();
			List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
			for (int i = 0; i < candidates.size(); i++) {
				fv.addMethod(Method.NakedPairs, candidates.get(i).getValue());
			}
			return true;
		}

		steps = sf.findAllNakedTriples(sudoku);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();
			List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
			for (int i = 0; i < candidates.size(); i++) {
				fv.addMethod(Method.NakedTriples, candidates.get(i).getValue());
			}
			return true;
		}

		steps = sf.findAllNakedQuadruples(sudoku);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();
			List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
			for (int i = 0; i < candidates.size(); i++) {
				fv.addMethod(Method.NakedQuadruples, candidates.get(i)
						.getValue());
			}
			return true;
		}

		// ----------------------------------------- //
		// Hidden Subsets //
		// ----------------------------------------- //
		steps = sf.findAllHiddenPairs(sudoku);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();
			List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
			for (int i = 0; i < candidates.size(); i++) {
				fv.addMethod(Method.HiddenPairs, candidates.get(i).getValue());
			}
			return true;
		}

		steps = sf.findAllHiddenTriples(sudoku);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();
			List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
			for (int i = 0; i < candidates.size(); i++) {
				fv.addMethod(Method.HiddenTriples, candidates.get(i).getValue());
			}
			return true;
		}

		steps = sf.findAllHiddenQuadruples(sudoku);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();
			List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
			for (int i = 0; i < candidates.size(); i++) {
				fv.addMethod(Method.HiddenQuadruples, candidates.get(i)
						.getValue());
			}
			return true;
		}

		// ----------------------------------------- //
		// Wings //
		// ----------------------------------------- //
		steps = sf.getAllWings(sudoku);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();

			List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
			switch (steps.get(0).getStepName()) {
			case "W-Wing":

				for (int i = 0; i < candidates.size(); i++) {
					fv.addMethod(Method.WWing, candidates.get(i).getValue());
				}
				break;
			case "XY-Wing":
				for (int i = 0; i < candidates.size(); i++) {
					fv.addMethod(Method.XYWing, candidates.get(i).getValue());
				}
				break;
			case "XYZ-Wing":
				for (int i = 0; i < candidates.size(); i++) {
					fv.addMethod(Method.XYZWing, candidates.get(i).getValue());
				}
				break;
			}

			return true;
		}

		// ----------------------------------------- //
		// Single Digit Patterns //
		// ----------------------------------------- //
		steps = sf.findAllSkyScrapers(sudoku);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();

			List<Candidate> candidates = steps.get(0).getCandidatesToDelete();

			for (int i = 0; i < candidates.size(); i++) {
				fv.addMethod(Method.Skyscarper, candidates.get(i).getValue());
			}

			return true;
		}

		steps = sf.findAllTwoStringKites(sudoku);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();

			List<Candidate> candidates = steps.get(0).getCandidatesToDelete();

			for (int i = 0; i < candidates.size(); i++) {
				fv.addMethod(Method.TwoStringKite, candidates.get(i).getValue());
			}

			return true;
		}

		steps = sf.getAllChains(sudoku);
		steps = filterByName("Turbot Fish", steps);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();

			List<Candidate> candidates = steps.get(0).getCandidatesToDelete();

			for (int i = 0; i < candidates.size(); i++) {
				fv.addMethod(Method.TurbotFish, candidates.get(i).getValue());
			}

			return true;
		}

		steps = sf.findAllEmptyRectangles(sudoku);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();

			List<Candidate> candidates = steps.get(0).getCandidatesToDelete();

			for (int i = 0; i < candidates.size(); i++) {
				fv.addMethod(Method.EmptyRectangle, candidates.get(i)
						.getValue());
			}

			return true;
		}

		// ----------------------------------------- //
		// Fishes //
		// ----------------------------------------- //
		List<SolutionStep> fishes = sf.getAllFishes(sudoku, 2, 7, 0, 0, null,
				-1, 0);
		steps = filterByName("X-Wing", fishes);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();

			List<Candidate> candidates = steps.get(0).getCandidatesToDelete();

			for (int i = 0; i < candidates.size(); i++) {
				fv.addMethod(Method.XWing, candidates.get(i).getValue());
			}

			return true;
		}

		steps = filterByName("Swordfish", fishes);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();

			List<Candidate> candidates = steps.get(0).getCandidatesToDelete();

			for (int i = 0; i < candidates.size(); i++) {
				fv.addMethod(Method.Swordfish, candidates.get(i).getValue());
			}

			return true;
		}

		steps = filterByName("Jellyfish", fishes);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();

			List<Candidate> candidates = steps.get(0).getCandidatesToDelete();

			for (int i = 0; i < candidates.size(); i++) {
				fv.addMethod(Method.Jellyfish, candidates.get(i).getValue());
			}

			return true;
		}

		// ----------------------------------------- //
		// Finned Fishes //
		// ----------------------------------------- //
		/*
		 * fishes = sf.getAllFishes(sudoku, 2, 7, 3, 0, null, -1, 0);
		 * 
		 * steps = filterByName("Finned X-Wing", fishes); if (steps.size() > 0)
		 * { sf.doStep(steps.get(0)); sudoku = sf.getSudoku();
		 * 
		 * List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.FinnedXWing, candidates.get(i).getValue()); }
		 * 
		 * return true; }
		 * 
		 * steps = filterByName("Finned Jellyfish", fishes); if (steps.size() >
		 * 0) { sf.doStep(steps.get(0)); sudoku = sf.getSudoku();
		 * 
		 * List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.FinnedJellyfish, candidates.get(i) .getValue());
		 * }
		 * 
		 * return true; }
		 * 
		 * steps = filterByName("Finned Swordfish", fishes); if (steps.size() >
		 * 0) { sf.doStep(steps.get(0)); sudoku = sf.getSudoku();
		 * 
		 * List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.FinnedSwordfish, candidates.get(i) .getValue());
		 * }
		 * 
		 * return true; }
		 */
		// ----------------------------------------- //
		// Sashimi Fishes //
		// ----------------------------------------- //
		/*
		 * steps = filterByName("Sashimi X-Wing", fishes); if (steps.size() > 0)
		 * { sf.doStep(steps.get(0)); sudoku = sf.getSudoku();
		 * 
		 * List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.SashimiXWing, candidates.get(i).getValue()); }
		 * 
		 * return true; }
		 * 
		 * steps = filterByName("Sashimi Jellyfish", fishes); if (steps.size() >
		 * 0) { sf.doStep(steps.get(0)); sudoku = sf.getSudoku();
		 * 
		 * List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.SashimiJellyfish, candidates.get(i) .getValue());
		 * }
		 * 
		 * return true; }
		 * 
		 * steps = filterByName("Sashimi Swordfish", fishes); if (steps.size() >
		 * 0) { sf.doStep(steps.get(0)); sudoku = sf.getSudoku();
		 * 
		 * List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.SashimiSwordfish, candidates.get(i) .getValue());
		 * }
		 * 
		 * return true; }
		 */
		// ----------------------------------------- //
		// Uniqueness Test //
		// ----------------------------------------- //
		/*
		 * steps = sf.getAllUniqueness(sudoku); steps =
		 * filterByName("Uniqueness Test 1", steps); if (steps.size() > 0) {
		 * sf.doStep(steps.get(0)); sudoku = sf.getSudoku();
		 * 
		 * List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.UniquenessTest1, candidates.get(i) .getValue());
		 * }
		 * 
		 * return true; }
		 * 
		 * steps = filterByName("Uniqueness Test 2", steps); if (steps.size() >
		 * 0) { sf.doStep(steps.get(0)); sudoku = sf.getSudoku();
		 * 
		 * List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.UniquenessTest2, candidates.get(i) .getValue());
		 * }
		 * 
		 * return true; }
		 * 
		 * steps = filterByName("Uniqueness Test 3", steps); if (steps.size() >
		 * 0) { sf.doStep(steps.get(0)); sudoku = sf.getSudoku();
		 * 
		 * List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.UniquenessTest3, candidates.get(i) .getValue());
		 * }
		 * 
		 * return true; }
		 * 
		 * steps = filterByName("Uniqueness Test 4", steps); if (steps.size() >
		 * 0) { sf.doStep(steps.get(0)); sudoku = sf.getSudoku();
		 * 
		 * List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.UniquenessTest4, candidates.get(i) .getValue());
		 * }
		 * 
		 * return true; }
		 * 
		 * steps = filterByName("Uniqueness Test 5", steps); if (steps.size() >
		 * 0) { sf.doStep(steps.get(0)); sudoku = sf.getSudoku();
		 * 
		 * List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.UniquenessTest5, candidates.get(i) .getValue());
		 * }
		 * 
		 * return true; }
		 * 
		 * steps = filterByName("Uniqueness Test 6", steps); if (steps.size() >
		 * 0) { sf.doStep(steps.get(0)); sudoku = sf.getSudoku();
		 * 
		 * List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.UniquenessTest6, candidates.get(i) .getValue());
		 * }
		 * 
		 * return true; }
		 * 
		 * steps = filterByName("Hidden Rectangle", steps); if (steps.size() >
		 * 0) { sf.doStep(steps.get(0)); sudoku = sf.getSudoku();
		 * 
		 * List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.HiddenRectangle, candidates.get(i) .getValue());
		 * }
		 * 
		 * return true; }
		 * 
		 * steps = filterByName("Avoidable Rectangle Type 1", steps); if
		 * (steps.size() > 0) { sf.doStep(steps.get(0)); sudoku =
		 * sf.getSudoku();
		 * 
		 * List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.AvoidableRectangle1, candidates.get(i)
		 * .getValue()); }
		 * 
		 * return true; }
		 * 
		 * steps = filterByName("Avoidable Rectangle Type 2", steps); if
		 * (steps.size() > 0) { sf.doStep(steps.get(0)); sudoku =
		 * sf.getSudoku();
		 * 
		 * List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.AvoidableRectangle2, candidates.get(i)
		 * .getValue()); }
		 * 
		 * return true; }
		 */

		// ----------------------------------------- //
		// Sue De Coq //
		// ----------------------------------------- //
		steps = sf.getAllSueDeCoqs(sudoku);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();

			List<Candidate> candidates = steps.get(0).getCandidatesToDelete();

			for (int i = 0; i < candidates.size(); i++) {
				fv.addMethod(Method.SueDeCoq, candidates.get(i).getValue());
			}

			return true;
		}

		// ----------------------------------------- //
		// Colors //
		// ----------------------------------------- //
		steps = sf.findAllSimpleColors(sudoku);
		if (steps.size() > 0) {
			sf.doStep(steps.get(0));
			sudoku = sf.getSudoku();

			List<Candidate> candidates = steps.get(0).getCandidatesToDelete();

			for (int i = 0; i < candidates.size(); i++) {
				fv.addMethod(Method.SimpleColors, candidates.get(i).getValue());
			}

			return true;
		}

		/*
		 * steps = sf.findAllMultiColors(sudoku); if (steps.size() > 0) {
		 * sf.doStep(steps.get(0)); sudoku = sf.getSudoku();
		 * 
		 * List<Candidate> candidates = steps.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.MultiColors, candidates.get(i).getValue()); }
		 * 
		 * return true; }
		 */

		// ----------------------------------------- //
		// Chains //
		// ----------------------------------------- //
		/*
		 * steps = sf.getAllChains(sudoku); List<SolutionStep> chains = new
		 * ArrayList<SolutionStep>();
		 * 
		 * chains = filterByName("X-Chain", steps); if (chains.size() > 0) {
		 * sf.doStep(chains.get(0)); sudoku = sf.getSudoku();
		 * 
		 * List<Candidate> candidates = chains.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.XChain, candidates.get(i).getValue()); }
		 * 
		 * return true; }
		 * 
		 * chains = filterByName("XY-Chain", steps); if (chains.size() > 0) {
		 * sf.doStep(chains.get(0)); sudoku = sf.getSudoku();
		 * 
		 * List<Candidate> candidates = chains.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.XYChain, candidates.get(i).getValue()); }
		 * 
		 * return true; }
		 * 
		 * chains = filterByName("Remote Pairs", steps); if (chains.size() > 0)
		 * { sf.doStep(chains.get(0)); sudoku = sf.getSudoku();
		 * 
		 * List<Candidate> candidates = chains.get(0).getCandidatesToDelete();
		 * 
		 * for (int i = 0; i < candidates.size(); i++) {
		 * fv.addMethod(Method.RemotePairs, candidates.get(i).getValue()); }
		 * 
		 * return true; }
		 */
		// ----------------------------------------- //
		// Almost Locked Sets //
		// ----------------------------------------- //
		try {
			steps = sf.getAllAlses(sudoku, true, false, false);
			if (steps.size() > 0) {
				sf.doStep(steps.get(0));
				sudoku = sf.getSudoku();

				List<Candidate> candidates = steps.get(0)
						.getCandidatesToDelete();

				for (int i = 0; i < candidates.size(); i++) {
					fv.addMethod(Method.ALSXZ, candidates.get(i).getValue());
				}

				return true;
			}
		} catch (IllegalArgumentException e) {
			Logger.log(LogLevel.Error, "ALS Argument Exception");
		}

		try {
			steps = sf.getAllAlses(sudoku, false, true, false);
			if (steps.size() > 0) {
				sf.doStep(steps.get(0));
				sudoku = sf.getSudoku();

				List<Candidate> candidates = steps.get(0)
						.getCandidatesToDelete();

				for (int i = 0; i < candidates.size(); i++) {
					fv.addMethod(Method.ALSXY, candidates.get(i).getValue());
				}

				return true;
			}
		} catch (IllegalArgumentException e) {
			//Logger.log(LogLevel.Error, "ALS Argument Exception");
		}

		try {
			steps = sf.getAllAlses(sudoku, false, false, true);
			if (steps.size() > 0) {
				sf.doStep(steps.get(0));
				sudoku = sf.getSudoku();

				List<Candidate> candidates = steps.get(0)
						.getCandidatesToDelete();

				for (int i = 0; i < candidates.size(); i++) {
					fv.addMethod(Method.ALSChains, candidates.get(i).getValue());
				}

				return true;
			}
		} catch (IllegalArgumentException e) {
			Logger.log(LogLevel.Error, "ALS Argument Exception");
		}

		return false;
	}

	private List<SolutionStep> filterByName(String name,
			List<SolutionStep> steps) {
		List<SolutionStep> all = new ArrayList<SolutionStep>();
		List<SolutionStep> remove = new ArrayList<SolutionStep>();

		for (int i = 0; i < steps.size(); i++) {
			all.add(steps.get(i));
			if (!steps.get(i).getStepName().equals(name))
				remove.add(steps.get(i));
		}

		all.removeAll(remove);
		return all;
	}
}
