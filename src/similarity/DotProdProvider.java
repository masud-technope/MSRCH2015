package similarity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import config.StaticData;

public class DotProdProvider {

	String topicDistFile;

	HashMap<String, ArrayList<Double>> probmap;

	public DotProdProvider(String topicDistFile) {
		this.topicDistFile = topicDistFile;
		this.probmap = new HashMap<>();
		this.loadProbabilities();
	}

	protected void loadProbabilities() {
		// loading the probabilities
		try {
			Scanner scanner = new Scanner(new File(this.topicDistFile));
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				String[] parts = line.split("\\s+");
				String key = parts[0].trim();
				ArrayList<Double> probs = new ArrayList<>();
				for (int i = 1; i < parts.length; i++) {
					probs.add(Double.parseDouble(parts[i].trim()));
				}
				// now add to HashMap
				this.probmap.put(key, probs);
			}
			scanner.close();
			System.out.println("Probabilities loaded.");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void collectDotProducts() {
		// collecting dot products
		String outmet = StaticData.CHALLENGE_DATA + "/outmet.txt";
		String topicsim = StaticData.CHALLENGE_DATA + "/topicsimilarity.txt";
		try {
			FileWriter fwriter = new FileWriter(new File(topicsim));
			Scanner scanner = new Scanner(new File(outmet));
			scanner.nextLine();
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				String[] parts = line.split("\\s+");
				int ansID = Integer.parseInt(parts[0].trim());
				int questID = Integer.parseInt(parts[1].trim());
				ArrayList<Double> A = this.probmap.get(ansID + ".txt");
				ArrayList<Double> B = this.probmap.get(questID + ".txt");
				double dot = 0;
				try{
				if (!A.isEmpty() && !B.isEmpty()) {
					dot = getDotProduct(A, B);
				}}catch(Exception exc){}
				fwriter.write(ansID + "\t" + dot + "\n");
			}
			scanner.close();
			fwriter.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected double getMod(ArrayList<Double> A) {
		double val = 0;
		for (double d : A) {
			val += d * d;
		}
		return Math.sqrt(val);
	}

	protected double getDotProduct(ArrayList<Double> A, ArrayList<Double> B) {
		double dot = 0;
		double upper = 0;
		for (int i = 0; i < A.size(); i++) {
			upper += A.get(i) * B.get(i);
		}
		double modA = getMod(A);
		double modB = getMod(B);
		dot = upper / (modA * modB);
		return dot;
	}

	public static void main(String[] args) {
		String topicDist = StaticData.CHALLENGE_DATA + "/topicdist.txt";
		DotProdProvider provider = new DotProdProvider(topicDist);
		provider.collectDotProducts();
		System.out.println("Topic similarity recorded.");

	}
}
