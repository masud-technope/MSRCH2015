package entropy;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import config.StaticData;

public class TopicEntropyMgr {
	
	String qaFile;
	String topicdist;
	String simfile;
	HashMap<Integer, ArrayList<Double>> topicdistmap;
	final int MAXTOPIC = 150;
	final int TOPTOPIC = 5;
	final double BT = (double) 1 / MAXTOPIC;
	
	public TopicEntropyMgr(String qaFile)
	{
		this.qaFile = StaticData.CHALLENGE_DATA + "/" + qaFile;
		this.topicdist = StaticData.CHALLENGE_DATA + "/topicdist.txt";
		this.topicdistmap = new HashMap<>();
		this.simfile = StaticData.CHALLENGE_DATA + "/ent/" + "ent-" + qaFile;
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
	
	protected void collectEntropies() {
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
					
					// System.out.println(qid + " " + aid);
					if (list1.size() > 0 && list2.size() > 0) {
						TopicEntropyCalc calc1=new TopicEntropyCalc(list1);
						double ent1=calc1.calculateEntropy();
						TopicEntropyCalc calc2=new TopicEntropyCalc(list2);
						double ent2=calc2.calculateEntropy();
						fwriter.write(qid+"\t"+ent1 + "\t" +aid+"\t"+ent2 + "\n");
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
		String qaFile = "qa-unsolved.txt";
		TopicEntropyMgr manager=new TopicEntropyMgr(qaFile);
		manager.collectEntropies();
		/*int qid=528152;
		int aid=528181;
		calc.checkSimilarity(qid, aid);*/
	}

	

}
