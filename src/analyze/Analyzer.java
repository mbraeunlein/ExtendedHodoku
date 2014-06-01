package analyze;

import io.LogLevel;
import io.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.supervised.instance.Resample;

public class Analyzer {
	private Instances train = null;
	private Instances test = null;

	// map name of original class to number of times classified to another class
	private HashMap<String, HashMap<String, Integer>> result = new HashMap<String, HashMap<String, Integer>>();

	private class Node {
		public int id = 0;
		public ArrayList<Node> children = new ArrayList<Node>();
		public String method = "";
		public Node parent = null;
		public int level = 0;

		public Node() {

		}

		public Node(int identifier, String methodName) {
			id = identifier;
			method = methodName;
		}

		public Node findId(int identifier) {
			if (this.id == identifier) {
				return this;
			}
			for (Node node : children) {
				Node ret = node.findId(identifier);
				if (ret != null)
					return ret;
			}

			return null;
		}

		public void generateStats(HashMap<String, ArrayList<Integer>> stats) {
			ArrayList<Integer> levels = new ArrayList<Integer>();
			if (stats.containsKey(method))
				levels = stats.get(method);
			levels.add(level);
			stats.put(method, levels);
			for (Node child : children) {
				child.generateStats(stats);
			}
		}
	}

	private String extractMethodName(String line) {
		String name = line.split("\"")[1];
		if (name.split(" ").length > 1) {
			name = name.split(" ")[0];
		}
		if (name.startsWith("method"))
			name = name.substring(6);
		return name;
	}

	public void analyzeAttributes(String file) {
		try {
			String treeString = "";
			ArrayList<Node> roots = new ArrayList<Node>();
			
			for (int times = 0; times < 1000; times++) {
				System.out.println("Set " + times);
				// load the file
				loadTrainFile(file);
				Resample sampler = new Resample();
				sampler.setInputFormat(train);
				String Fliteroptions = "-B 1.0 -Z 10";
				sampler.setOptions(weka.core.Utils.splitOptions(Fliteroptions));
				sampler.setRandomSeed((int) System.currentTimeMillis());
				train = Resample.useFilter(train, sampler);

				// build the classifier
				String[] options = { "-C", "0.05", "-M", "2" };
				J48 tree = new J48(); // new instance of tree
				tree.setOptions(options); // set the options
				tree.buildClassifier(train); // build classifier

				// get the decision tree
				String graph = tree.graph();
				String[] lines = graph.split("\n");
				treeString = tree.graph();
				
				// define id extractor pattern
				Pattern idPattern = Pattern.compile("N(\\d)+");

				// extract the first node
				Matcher idMatcher = idPattern.matcher(lines[1]);
				idMatcher.find();
				int id = Integer.parseInt(idMatcher.group().substring(1));
				String method = extractMethodName(lines[1]);
				Node root = new Node(id, method);

				for (int i = 2; i < lines.length - 1; i = i + 2) {
					String line1 = lines[i];
					String line2 = lines[i + 1];

					// parse child relation
					int parentId = Integer.parseInt(line1.split(" ")[0]
							.split("->")[0].substring(1));
					int childId = Integer.parseInt(line1.split(" ")[0]
							.split("->")[1].substring(1));
					// parse child node
					Node child = new Node();
					child.id = childId;
					if (line2.split("shape").length > 1) {
						// leaf node
						child.method = "leaf";
					} else {
						// inner node
						child.method = extractMethodName(line2);
					}
					Node parent = root.findId(parentId);
					if (parent == null) {
						System.out.println("Parent not found!!");
					} else {
						child.parent = parent;
						parent.children.add(child);
						child.level = parent.level + 1;
					}
				}

				roots.add(root);
			}

			// traverse the trees and get statistics
			// map method names on list of tree levels
			// list to count how many times a method occured
			HashMap<String, Integer> allstats = new HashMap<String, Integer>();
			// traverse all roots
			for (int i = 0; i < roots.size(); i++) {
				// get the current root
				Node root = roots.get(i);
				// get its stats
				HashMap<String, ArrayList<Integer>> stats = new HashMap<String, ArrayList<Integer>>();
				root.generateStats(stats);
				// traverse the stats
				for (String m : stats.keySet()) {
					// drop leaves
					if (!m.equals("leaf")) {
						// get the levels to put in the stats
						ArrayList<Integer> levels = stats.get(m);
						int count = 0;
						if (allstats.containsKey(m)) {
							count = allstats.get(m);
						}
						count += levels.size();
						allstats.put(m, count);
					}
				}
			}
			
			for(String m: allstats.keySet()) {
				if(m.substring(0, m.length() - 1).equals("ALSXZ")) {
					System.out.println(m + " " + allstats.get(m));
				}
			}
		} catch (Exception e) {
			Logger.log(LogLevel.Error, e.toString());
		}
	}

	class ValueComparator implements Comparator<String> {

		Map<String, Integer> base;

		public ValueComparator(Map<String, Integer> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}

	public void crossValidation(String file, ArrayList<String> keys) {
		try {
			// load the file
			loadTrainFile(file);

			// build the classifier
			String[] options = { "-C", "0.1", "-M", "30" };

			J48 tree = new J48(); // new instance of tree
			tree.setOptions(options); // set the options
			tree.buildClassifier(train); // build classifier

			// evaluate the classifier
			Evaluation eval = new Evaluation(train);
			eval.crossValidateModel(tree, train, 10, new Random(1));

			// print the results
			Logger.log(LogLevel.Classification, eval.toSummaryString());
			double[][] matrix = eval.confusionMatrix();

			for (int i = 0; i < matrix.length; i++) {
				double[] line = matrix[i];
				for (int j = 0; j < line.length; j++) {
					System.out.print((int) line[j] + "\t");
				}
				System.out.println();
			}

			printLatex(keys, matrix);
		} catch (Exception e) {
			Logger.log(LogLevel.Error, e.toString());
		}
	}

	private void printLatex(ArrayList<String> keys, double[][] matrix) {
		// check for valid keys
		if (keys.size() != matrix.length) {
			Logger.log(LogLevel.Error,
					"key set has not the same size as confusion matrix, cant print latex code!");
			return;
		}

		// assign letters to classes
		int asciiCount = 97;
		ArrayList<String> assignment = new ArrayList<String>();
		for (int i = 0; i < keys.size(); i++) {
			assignment.add("" + (char) asciiCount);
			asciiCount++;
		}

		// print header of table
		System.out.print("\\begin{tabular}{ l | l | ");
		for (int i = 0; i < matrix.length; i++) {
			System.out.print(" c ");
		}
		System.out.println("|}");
		System.out.println("\\multicolumn{" + (keys.size() + 2)
				+ "}{c}{\\textbf{predicted class}}\\\\");
		System.out.println("\\cline{2-" + (keys.size() + 2) + "}");
		System.out.println("\\multirow{" + (keys.size() + 1)
				+ "}{*}{\\begin{turn}{90}\\textbf{actual value}\\end{turn}}");

		System.out.print(" & ");
		for (int i = 0; i < keys.size(); i++) {
			System.out.print(" & " + assignment.get(i));
		}
		System.out.println("\\\\");

		System.out.println("\\cline{2-" + (keys.size() + 2) + "}");

		for (int i = 0; i < matrix.length; i++) {
			double[] row = matrix[i];
			System.out.print("& " + assignment.get(i));
			for (int j = 0; j < row.length; j++) {
				int number = (int) row[j];
				System.out.print(" & " + number);
			}
			System.out.println("\\\\");
		}

		System.out.println("\\cline{2-" + (keys.size() + 2) + "}");

		System.out.println("\\end{tabular}");
	}

	public void test(String trainFile, String testFile) {
		try {
			// load the files
			loadTrainFile(trainFile);
			loadTestFile(testFile);

			// build the classifier
			Classifier cls = new J48();
			cls.buildClassifier(train);

			// evaluate the classifier
			Evaluation eval = new Evaluation(train);
			eval.evaluateModel(cls, test);

			// print the results
			Logger.log(LogLevel.Classification,
					eval.toSummaryString("\nResults\n======\n", false));

		} catch (Exception e) {
			Logger.log(LogLevel.Error, e.toString());
		}
	}

	public void mapClasses(String file1, String file2) {
		try {
			// load the files
			loadTrainFile(file1);
			loadTestFile(file2);

			// build the classifier
			J48 cls = new J48();
			cls.buildClassifier(train);

			// evaluate predicted class for every instance of the test file
			for (int i = 0; i < test.numInstances(); i++) {
				// the real class name
				String origin = test.classAttribute().value(
						(int) test.instance(i).classValue());
				// the estimated class name
				Instance copiedInstance = (Instance) test.instance(i).copy();
				copiedInstance.setDataset(train);
				String estimatedClass = train.classAttribute().value(
						(int) cls.classifyInstance(copiedInstance));
				addResult(origin, estimatedClass);
			}

			// build the result string and estimate mapping
			String resultString = "";
			for (String origin : result.keySet()) {
				System.out.println(origin);
				int maxCount = -1;
				String bestFittingClass = "";
				for (String estimatedClass : result.get(origin).keySet()) {
					int count = result.get(origin).get(estimatedClass);
					System.out.println("\t" + estimatedClass + " -> " + count);
					if (maxCount < count) {
						maxCount = count;
						bestFittingClass = estimatedClass;
					}
				}
				resultString += origin + " -> " + bestFittingClass + "\n";
			}

			System.out.println(resultString);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadTrainFile(String file) {
		DataSource source;

		try {
			source = new DataSource(file);
			train = source.getDataSet();

			if (train.classIndex() == -1)
				train.setClassIndex(train.numAttributes() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadTestFile(String file) {
		DataSource source;

		try {
			source = new DataSource(file);
			test = source.getDataSet();

			if (test.classIndex() == -1)
				test.setClassIndex(test.numAttributes() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addResult(String origin, String estimatedClass) {
		HashMap<String, Integer> classifiedCount = new HashMap<String, Integer>();
		if (result.containsKey(origin))
			classifiedCount = result.get(origin);
		int count = 0;
		if (classifiedCount.containsKey(estimatedClass))
			count = classifiedCount.get(estimatedClass);
		count++;
		classifiedCount.put(estimatedClass, count);
		result.put(origin, classifiedCount);
	}
}
