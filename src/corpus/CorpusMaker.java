package corpus;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import utility.ContentLoader;
import config.StaticData;

public class CorpusMaker {

	String postFolder;
	String corpusFile;

	public CorpusMaker(String folder) {
		// initialization
		this.postFolder = StaticData.CHALLENGE_DATA + "/" + folder;
		this.corpusFile = StaticData.CHALLENGE_DATA + "/corpus2.txt";
	}

	protected void savePost(int postid, String content) {
		// saving the content
		try {
			FileWriter fwriter = new FileWriter(new File(corpusFile), true);
			content = postid + ",\t" + postid+".txt," + "\t" + content;
			fwriter.write(content + "\n");
			fwriter.close();
		} catch (Exception exc) {
			// handle the exception
		}
	}

	protected ArrayList<String> removeSpecialChars(String content) {
		// removing special characters
		String regex = "\\p{Punct}+";
		String[] parts = content.split(regex);
		ArrayList<String> refined = new ArrayList<>();
		for (String part : parts) {
			if (!part.trim().isEmpty()) {
				if (part.length() > 2) {
					refined.add(part);
				}
			}
		}
		// if(modifiedWord.isEmpty())modifiedWord=word;
		return refined;
	}

	protected String parseHtml(String html) {
		// parsing the HTML content
		String tokens = new String();
		try {
			Document document = Jsoup.parse(html);
			document.select("code,pre").remove();
			String textcontent = document.text();
			ArrayList<String> cleaned = removeSpecialChars(textcontent);
			for (String token : cleaned) {
				tokens += token + " ";
			}
		} catch (Exception exc) {
			// handle the exception
		}
		return tokens;
	}

	protected void extractPostContent() {
		// extracting post content
		File[] files = new File(this.postFolder).listFiles();
		int count=0;
		for (File f : files) {
			String content = ContentLoader.loadFileContent(f.getAbsolutePath());
			String cleaned=parseHtml(content);
			int postid=Integer.parseInt(f.getName().split("\\.")[0]);
			savePost(postid, cleaned);
			count++;
		}
		System.out.println("Items saved:"+count);
	}
	
	public static void main(String[] args){
		String folder="posts";
		CorpusMaker maker=new CorpusMaker(folder);
		maker.extractPostContent();
	}
}
