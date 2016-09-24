package collector;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

import ca.usask.cs.text.readability.test.ContentLoader;
import config.StaticData;

public class QAListMaker {

	public static void main(String[] args) {

		String qa = StaticData.CHALLENGE_DATA + "/qa.txt";
		HashMap<Integer, Integer> qamap = new HashMap<>();
		String content = ContentLoader.loadFileContent(qa);
		String[] lines = content.split("\n");
		for (int i = 1; i < lines.length; i++) {
			String[] parts = lines[i].split("\\s+");
			int qid = Integer.parseInt(parts[0]);
			int aid = Integer.parseInt(parts[1]);
			// putting q & a pai
			qamap.put(qid, aid);
		}
		// now check browse the question and answer
		try {
			FileWriter fwriter = new FileWriter(new File(
					StaticData.CHALLENGE_DATA + "/ansonly.txt"));
			for (int key : qamap.keySet()) {
				fwriter.write(qamap.get(key)+",\n");
			}
			fwriter.close();
		} catch (Exception exc) {

		}

	}

}
