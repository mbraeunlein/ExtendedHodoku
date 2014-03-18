package analyze;

import io.LogLevel;
import io.Logger;

import java.util.HashMap;
import java.util.Random;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;

public class Analyzer {
	private Instances train = null;
	private Instances test = null;

	// map name of original class to number of times classified to another class
	private HashMap<String, HashMap<String, Integer>> result = new HashMap<String, HashMap<String, Integer>>();

	public void crossValidation(String file) {
		try {
			// load the file
			loadTrainFile(file);

			// build the classifier
			String[] options = new String[1];
			options[0] = "-U"; // unpruned tree
			J48 tree = new J48(); // new instance of tree
			tree.setOptions(options); // set the options
			tree.buildClassifier(train); // build classifier

			// evaluate the classifier
			Evaluation eval = new Evaluation(train);
			eval.crossValidateModel(tree, train, 10, new Random(1));

			// print the results
			Logger.log(LogLevel.Classification, eval.toSummaryString());
		} catch (Exception e) {
			Logger.log(LogLevel.Error, e.toString());
		}
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
			Classifier cls = new J48();
			cls.buildClassifier(train);

			// evaluate predicted class for every instance of the test file
			for (int i = 0; i < test.numInstances(); i++) {
				String origin = test.classAttribute().value(
						(int) test.instance(i).classValue());
				String classifiedAs = train.classAttribute().value(
						(int) cls.classifyInstance(test.instance(i)));
				System.out.println(origin + " -> " + classifiedAs);
			}

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
