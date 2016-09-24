package metric;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import utility.ContentLoader;
import config.StaticData;

public class MetricProvider {

	boolean solved;
	String metricFile;
	HashMap<Integer, Integer> postUserMap;
	HashMap<Integer, Integer> voteMap;
	HashMap<Integer, Integer> reputationMap;
	HashMap<Integer, Double> entropyMap;
	HashMap<Integer, Integer> lastAcceesMap;
	HashMap<Integer, Double> nacMap;

	public MetricProvider(boolean solved) {
		this.solved = solved;
		if(solved)
		this.metricFile = StaticData.CHALLENGE_DATA + "/metrics1.txt";
		else this.metricFile = StaticData.CHALLENGE_DATA + "/metrics0.txt";
		postUserMap = new HashMap<>();
		voteMap = new HashMap<>();
		reputationMap = new HashMap<>();
		entropyMap = new HashMap<>();
		lastAcceesMap = new HashMap<>();
		this.nacMap = new HashMap<>();
	}

	protected void loadPostUsers() {
		// loading post users
		String fileName = StaticData.CHALLENGE_DATA + "/pointer/post-user-q";
		if (solved)
			fileName += "-solved.txt";
		else
			fileName += "-unsolved.txt";
		String content = ContentLoader.loadFileContent(fileName);
		String[] lines = content.split("\n");
		for (String line : lines) {
			if (!line.trim().isEmpty()) {
				String[] parts = line.split("\\s+");
				int postid = Integer.parseInt(parts[0]);
				if (parts[1].equals("null"))
					continue;
				else {
					int userid = Integer.parseInt(parts[1]);
					// storing post-user map
					postUserMap.put(postid, userid);
				}
			}
		}
		System.out.println("Load post-users successfully");
	}

	protected void loadPostVotes() {
		// loading post votes
		String fileName = StaticData.CHALLENGE_DATA + "/vote/vote-qst";
		if (solved)
			fileName += "-solved.txt";
		else
			fileName += "-unsolved.txt";
		String content = ContentLoader.loadFileContent(fileName);
		String[] lines = content.split("\n");
		for (String line : lines) {
			if (!line.trim().isEmpty()) {
				String[] parts = line.split("\\s+");
				int postid = Integer.parseInt(parts[0]);
				int vote = Integer.parseInt(parts[1]);
				voteMap.put(postid, vote);
			}
		}
		System.out.println("Load post-votes successfully");
	}

	protected void loadPostUserReputation() {
		// loading post owner reputation
		String fileName = StaticData.CHALLENGE_DATA + "/reputation/repu-user";
		if (solved)
			fileName += "-solved.txt";
		else
			fileName += "-unsolved.txt";
		String content = ContentLoader.loadFileContent(fileName);
		String[] lines = content.split("\n");
		for (String line : lines) {
			if (!line.trim().isEmpty()) {
				String[] parts = line.split("\\s+");
				int postid = Integer.parseInt(parts[0]);
				int repu = Integer.parseInt(parts[1]);
				reputationMap.put(postid, repu);
			}
		}
		System.out.println("Load post user reputation successfully");
	}

	protected void loadPostEntropy() {
		// loading post entropies
		String fileName = StaticData.CHALLENGE_DATA + "/ent/ent-qa";
		if (solved)
			fileName += "-solved.txt";
		else
			fileName += "-unsolved.txt";
		String content = ContentLoader.loadFileContent(fileName);
		String[] lines = content.split("\n");
		for (String line : lines) {
			if (!line.trim().isEmpty()) {
				String[] parts = line.split("\\s+");
				int postid = Integer.parseInt(parts[0]);
				double ent = Double.parseDouble(parts[3]);
				entropyMap.put(postid, ent);
			}
		}
		System.out.println("Load post-entropy successfully");
	}

	protected void loadLasAccessDelay() {
		// loading last access delays
		String fileName = StaticData.CHALLENGE_DATA + "/lastaccess/user";
		if (solved)
			fileName += "-solved.txt";
		else
			fileName += "-unsolved.txt";
		String content = ContentLoader.loadFileContent(fileName);
		String[] lines = content.split("\n");
		for (String line : lines) {
			if (!line.trim().isEmpty()) {
				String[] parts = line.split("\\s+");
				int postid = Integer.parseInt(parts[0]);
				int delay = Integer.parseInt(parts[1]);
				lastAcceesMap.put(postid, delay);
			}
		}
		System.out.println("Load last-access successfully");
	}

	protected void loadNACRate() {
		// loading NAC rate
		String fileName = StaticData.CHALLENGE_DATA
				+ "/ac-nac-perc/user-ac-nac";
		if (solved)
			fileName += "-solved.txt";
		else
			fileName += "-unsolved.txt";
		String content = ContentLoader.loadFileContent(fileName);
		String[] lines = content.split("\n");
		for (String line : lines) {
			if (!line.trim().isEmpty()) {
				String[] parts = line.split("\\s+");
				int userid = Integer.parseInt(parts[0]);
				double nacperc = Double.parseDouble(parts[2]);
				nacMap.put(userid, nacperc);
			}
		}
		System.out.println("Load nac-rate successfully");
	}
	
	protected void collectMetrics()
	{
		//load the items
		loadPostUsers();
		loadPostVotes();
		loadPostUserReputation();
		loadPostEntropy();
		loadLasAccessDelay();
		loadNACRate();
		
		//recording the metrics.
		try{
			FileWriter fwriter=new FileWriter(new File(metricFile));
			for(int postid:entropyMap.keySet()){
				
				double entropy=entropyMap.get(postid);
				
				double ladelay=371.33;// -1;// solved?176:378;// solved?151:312;  //solved?176:378;
				if(lastAcceesMap.containsKey(postid)){
					ladelay=lastAcceesMap.get(postid);
				}
				double vote=0;
				if(voteMap.containsKey(postid)){
					vote=voteMap.get(postid);
				}
				double reputation=11448.679;
				if(reputationMap.containsKey(postid)){
					reputation=reputationMap.get(postid);
				}
				int userid=0;
				if(postUserMap.containsKey(postid)){
					userid=postUserMap.get(postid);
				}
				double nac= .361;//-1;//solved?0.1845:0.5523;
				if(nacMap.containsKey(userid)){
					nac=nacMap.get(userid);
				}

				String line=postid+"\t"+entropy+"\t"+(nac>-1?nac:"?")+"\t"+(ladelay>-1?ladelay:"?")+"\t"+vote+"\t"+(reputation>-1?reputation:"?");
				if(solved)line+="\t"+1;
				else line+="\t"+0;
				fwriter.write(line+"\n");
			}
			fwriter.close();
		}catch(Exception exc){
			//handle the exception
			exc.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		boolean solved=false;
		MetricProvider provider=new MetricProvider(solved);
		provider.collectMetrics();
	}
}
