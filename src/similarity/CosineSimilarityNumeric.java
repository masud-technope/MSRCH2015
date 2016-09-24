package similarity;

import java.util.ArrayList;
import java.util.HashMap;

import ca.usask.cs.text.readability.test.ContentLoader;
import config.StaticData;

public class CosineSimilarityNumeric {

	ArrayList<Double> list1;
	ArrayList<Double> list2;
	final double BT = 0.0066;
	static HashMap<Integer, Double> alphas = new HashMap<>();

	public CosineSimilarityNumeric(ArrayList<Double> list1,
			ArrayList<Double> list2) {
		this.list1 = list1;
		this.list2 = list2;
		loadAlphas();
	}

	protected double getMod(ArrayList<Double> list) {
		// calculating the mode of a vector
		double mod = 0;
		int count = 0;
		for (double d : list) {
			// if (d > BT)
			if (alphas.containsKey(count)) {
				double alpha = alphas.get(count);
				mod += d * alpha * d * alpha;
			}
			count++;
		}
		return Math.sqrt(mod);
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

	public double getCosineSimValue() {
		// calculate cosine similarity value
		double upper = 0;
		double sim = 0;
		try {
			for (int i = 0; i < list1.size(); i++) {
				if (list1.get(i) > BT) {
					if (list2.get(i) > BT) {
						double alpha = 0;
						if (alphas.containsKey(i)) {
							alpha = alphas.get(i);
							upper += list1.get(i) * alpha * alpha
									* list2.get(i);
						}
					}
				}
			}
			System.out.println(upper);
			double mod1 = getMod(list1);
			double mod2 = getMod(list2);
			System.out.println(mod1 + " " + mod2);
			if (mod1 * mod2 > 0)
				sim = upper / (mod1 * mod2);
			System.out.println(sim);
		} catch (Exception exc) {
			// handle the exception
		}
		return sim;
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

		ArrayList<Double> list2 = new ArrayList<Double>() {
			{
				add(0.011);
				add(0.002);
				add(0.004);
				add(0.005);
				add(0.002);
			}
		};
		CosineSimilarityNumeric cos = new CosineSimilarityNumeric(list1, list2);
		System.out.println(cos.getCosineSimValue());
	}
}
