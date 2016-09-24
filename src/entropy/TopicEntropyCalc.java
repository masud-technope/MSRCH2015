package entropy;

import java.util.ArrayList;
import java.util.HashMap;

import ca.usask.cs.text.readability.test.ContentLoader;
import config.StaticData;

public class TopicEntropyCalc {

	final double BT =0.0066;
	ArrayList<Double> list;
	static HashMap<Integer, Double> alphas = new HashMap<>();

	public TopicEntropyCalc(ArrayList<Double> list) {
		this.list = list;
		loadAlphas();
	}

	public double calculateEntropy() {
		double entropy = 0;
		int count = 0;
		for (double topicprob : list) {
			if (topicprob > BT) {
				double alpha = 0;
				if (alphas.containsKey(count)) {
					alpha = alphas.get(count);
					double px = alpha * topicprob;
					double logpx = -1 * (Math.log(px)/Math.log(2));
					entropy += (px * logpx);
				}
			}
			count++;
		}
		return entropy;
	}

	protected static void loadAlphas() {
		// loading the alphas
		if (alphas.isEmpty()) {
			String alphaFile = StaticData.CHALLENGE_DATA + "/topics.txt";
			String content = ContentLoader.loadFileContent(alphaFile);
			String[] lines = content.split("\n");
			for (String line : lines) {
				String[] parts = line.split("\\s+");
				int topic = Integer.parseInt(parts[0]);
				double prob = Double.parseDouble(parts[1]);
				alphas.put(topic, prob);
			}
		}
	}

	public static void main(String[] args) {
		ArrayList<Double> list1 = new ArrayList<Double>() {
			{
				add(0.39);
				add(0.106);
				add(0.089);
				add(0.071);
				add(0.071);
			}
		};
		TopicEntropyCalc calc = new TopicEntropyCalc(list1);
		System.out.println(calc.calculateEntropy());
	}
}
