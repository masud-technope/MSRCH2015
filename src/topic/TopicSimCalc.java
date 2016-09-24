package topic;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import ca.usask.cs.text.readability.test.ContentLoader;
import similarity.CosineSimilarityNumeric;
import config.StaticData;

public class TopicSimCalc {

	String qaFile;
	String topicdist;
	String simfile;
	HashMap<Integer, ArrayList<Double>> topicdistmap;
	final int MAXTOPIC = 150;
	final int TOPTOPIC = 5;
	final double BT = (double) 1 / MAXTOPIC;
	

	public TopicSimCalc(String qaFile) {
		this.qaFile = StaticData.CHALLENGE_DATA + "/" + qaFile;
		this.topicdist = StaticData.CHALLENGE_DATA + "/topicdist.txt";
		this.topicdistmap = new HashMap<>();
		this.simfile = StaticData.CHALLENGE_DATA + "/similarity/" + "sim-" + qaFile;
		this.loadTopicDist();
	}

	protected void loadTopicDist() {
		// loading topic distribution
		try {
			Scanner scanner = new Scanner(new File(this.topicdist));
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				String[] parts = line.split("\\s+");
				int postid = Integer.parseInt(parts[0].split("\\.")[0]);
				// System.out.println(postid);
				ArrayList<Double> dist = new ArrayList<>();
				for (int i = 1; i < parts.length; i++) {
					dist.add(Double.parseDouble(parts[i]));
				}
				// storing to the map
				topicdistmap.put(postid, dist);
			}
			scanner.close();

		} catch (Exception exc) {
			// handle the exception
			exc.printStackTrace();
		}
	}

	protected ArrayList<Double> keepDominantTopics(ArrayList<Double> qtopics) {
		// persisting only the dominant topics
		HashMap<Integer, Double> dist = new HashMap<>();
		for (int i = 0; i < qtopics.size(); i++) {
			if (qtopics.get(i) > BT) {
				dist.put(i, qtopics.get(i));
			}
		}
		// now sort distribution
		List<Map.Entry<Integer, Double>> list = new LinkedList<>(
				dist.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			@Override
			public int compare(Entry<Integer, Double> o1,
					Entry<Integer, Double> o2) {
				// TODO Auto-generated method stub
				Double v2 = o2.getValue();
				Double v1 = o1.getValue();
				return v2.compareTo(v1);
			}
		});

		// now choose top topics
		int limit = list.size() < TOPTOPIC ? list.size() : TOPTOPIC;
		HashSet<Integer> gold = new HashSet<>();
		for (int i = 0; i < limit; i++) {
			gold.add(list.get(i).getKey());
		}
		// now add change the question topics
		for (int i = 0; i < qtopics.size(); i++) {
			if (!gold.contains(i)) {
				qtopics.set(i, new Double(0));
			}
		}

		// return modified topic scores
		return qtopics;
	}
	
	

	

	protected ArrayList<Double> alignAnswerTopics(ArrayList<Double> qtopics,
			ArrayList<Double> atopics) {
		// align answer topics to question topics
		for (int i = 0; i < qtopics.size(); i++) {
			if (qtopics.get(i) == 0) {
				atopics.set(i, new Double(0));
			}
		}
		return atopics;
	}

	
	protected void checkSimilarity(int qid, int aid)
	{
		ArrayList<Double> list1 = new ArrayList<>();
		if (topicdistmap.containsKey(qid)) {
			list1 = topicdistmap.get(qid);
		}

		ArrayList<Double> list2 = new ArrayList<>();
		if (topicdistmap.containsKey(aid)) {
			list2 = topicdistmap.get(aid);
		}

		//filter question topics
		list1=keepDominantTopics(list1);
		System.out.println(list1);
		//align answer topics
		list2=alignAnswerTopics(list1, list2);
		System.out.println(list2);
		
		// System.out.println(qid + " " + aid);
		if (list1.size() > 0 && list2.size() > 0) {
			CosineSimilarityNumeric cos = new CosineSimilarityNumeric(
					list1, list2);
			double sim = cos.getCosineSimValue();
			System.out.println(sim);
		}
	}
	
	protected void collectSimilarities() {
		// collect dot product similarities
		try {
			Scanner scanner = new Scanner(new File(this.qaFile));
			FileWriter fwriter = new FileWriter(new File(this.simfile));
			int count = 0;
			while (scanner.hasNextLine()) {
				try {
					String line = scanner.nextLine();
					String[] parts = line.split("\\s+");
					int qid = Integer.parseInt(parts[0]);
					int aid = Integer.parseInt(parts[1]);
					ArrayList<Double> list1 = new ArrayList<>();
					if (topicdistmap.containsKey(qid)) {
						list1 = topicdistmap.get(qid);
					}

					ArrayList<Double> list2 = new ArrayList<>();
					if (topicdistmap.containsKey(aid)) {
						list2 = topicdistmap.get(aid);
					}

					//filter question topics
					list1=keepDominantTopics(list1);
					//align answer topics
					list2=alignAnswerTopics(list1, list2);
					
					// System.out.println(qid + " " + aid);
					if (list1.size() > 0 && list2.size() > 0) {
						CosineSimilarityNumeric cos = new CosineSimilarityNumeric(
								list1, list2);
						double sim = cos.getCosineSimValue();
						fwriter.write(qid + "\t" + aid + "\t" + sim + "\n");
						count++;
					}
				} catch (Exception exc) {

				}
			}
			scanner.close();
			fwriter.close();
			System.out.println("DONE for:" + count);
		} catch (Exception exc) {
			// handle the exception
			exc.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String qaFile = "qa-solved.txt";
		TopicSimCalc calc = new TopicSimCalc(qaFile);
		calc.collectSimilarities();
		/*int qid=528152;
		int aid=528181;
		calc.checkSimilarity(qid, aid);*/
	}
}
