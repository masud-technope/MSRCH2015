package metric;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import similarity.CosineSimilarityMeasure;
import stemmer.WordNormalizer;
import utility.ContentLoader;
import config.StaticData;

public class QASimilarityProvider {

	String meticFile;
	HashMap<Integer, Integer> qanswer;
	String similarityFile;

	public QASimilarityProvider(String outmet) {
		this.meticFile = outmet;
		this.qanswer = new HashMap<>();
		this.loadQAIDs();
		this.similarityFile = StaticData.CHALLENGE_DATA + "/similarity.txt";
	}

	protected void loadQAIDs() {
		// loading answer IDs
		String content = ContentLoader.loadFileContent(this.meticFile);
		String[] lines = content.split("\n");
		for (String line : lines) {
			int ansID = Integer.parseInt(line.split("\\s+")[0]);
			int questID = Integer.parseInt(line.split("\\s+")[1]);
			this.qanswer.put(ansID, questID);
		}
	}

	protected String getTextContent(String html) {
		// getting text-only content
		Document document = Jsoup.parse(html);
		document.select("code").remove();
		return document.text();
	}

	protected void getPostSimilarity() {
		// estimating post similarities
		try {
			FileWriter fwriter = new FileWriter(new File(this.similarityFile));

			for (int key : this.qanswer.keySet()) {
				int questID = this.qanswer.get(key);

				String qfile = StaticData.CHALLENGE_DATA + "/posts/" + questID
						+ ".txt";
				String afile = StaticData.CHALLENGE_DATA + "/posts/" + key
						+ ".txt";
				// normalizing the sentences
				String question = ContentLoader.loadFileContent(qfile);
				question=getTextContent(question);
				WordNormalizer norm1 = new WordNormalizer(question);
				String normQ = norm1.normalizeSentence();
				
				String answer = ContentLoader.loadFileContent(afile);
				answer=getTextContent(answer);
				WordNormalizer norm2 = new WordNormalizer(answer);
				String normA = norm2.normalizeSentence();

				double similarity = 0;
				if (!normA.isEmpty() && !normQ.isEmpty()) {
					CosineSimilarityMeasure cosMeasure = new CosineSimilarityMeasure(
							normQ, normA);
					similarity = cosMeasure.get_cosine_similarity_score(false);
				}
				String line = key + "\t" + similarity;
				fwriter.write(line + "\n");
				System.out.println(key+" "+similarity);
			}
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		String outmet=StaticData.CHALLENGE_DATA+"/outmet.txt";
		QASimilarityProvider provider=new QASimilarityProvider(outmet);
		provider.getPostSimilarity();
	}

}
