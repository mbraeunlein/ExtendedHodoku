package analyze;

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

	public void loadFile(String file1, String file2) {
		DataSource source1;
		DataSource source2;

		try {
			source1 = new DataSource(file1);
			source2 = new DataSource(file2);
			train = source1.getDataSet();
			test = source2.getDataSet();

			if (train.classIndex() == -1)
				train.setClassIndex(train.numAttributes() - 1);
			if (test.classIndex() == -1)
				test.setClassIndex(test.numAttributes() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void mapClasses() {
		try {
			Classifier cls = new J48();
			cls.buildClassifier(train);

			for(int i = 0; i < test.numInstances(); i++) {
				String origin = test.classAttribute().value((int) test.instance(i).classValue());
				String estimatedClass = test.classAttribute().value((int) cls.classifyInstance(test.instance(i)));
				addResult(origin, estimatedClass);
			}

			String resultString = "";
			
			for (String origin : result.keySet()) {
				System.out.println(origin);
				int maxCount = -1;
				String bestFittingClass = "";
				for(String estimatedClass: result.get(origin).keySet()) {
					int count = result.get(origin).get(estimatedClass);
					System.out.println("\t" + estimatedClass + " -> " + count);
					if(maxCount < count) {
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
	
	private void addResult(String origin, String estimatedClass) {
		HashMap<String, Integer> classifiedCount = new HashMap<String, Integer>(); 
		if(result.containsKey(origin))
			classifiedCount = result.get(origin);
		int count = 0;
		if(classifiedCount.containsKey(estimatedClass))
			count = classifiedCount.get(estimatedClass);
		count++;
		classifiedCount.put(estimatedClass, count);
		result.put(origin, classifiedCount);
	}
}
