package malletwork;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import config.StaticData;
import stemmer.WordNormalizer;
import utility.ContentLoader;

public class PostContentMaker {
	
	String postFolder;
	public PostContentMaker(String folder)
	{
		this.postFolder=folder;
	}
	
	protected void extractAllPosts()
	{
		//extracting post from all content
		File dir=new File(this.postFolder);
		if(dir.isDirectory()){
			try{
			String[] files=dir.list();
			String outFile=StaticData.CHALLENGE_DATA+"/"+"corpus.txt";
			FileWriter fwriter=new FileWriter(new File(outFile));
			for(String file:files){
				int id=Integer.parseInt(file.split("\\.")[0]);
				String html=ContentLoader.loadFileContent(this.postFolder+"/"+file);
				String clean=extractPostContent(html, id);
				fwriter.write(id+",\t"+file+",\t"+clean+"\n");
			}
			fwriter.close();
			}catch(Exception exc){
				//handle the exception
			}
		}
	}
	
	protected String extractPostContent(String html, int ID)
	{
		//extracting HTML content
		Document doc=Jsoup.parse(html);
		doc.select("a,img").remove();
		String textcontent=doc.text();
		WordNormalizer normalizer=new WordNormalizer(textcontent);
		String normalized=normalizer.normalizeSentence();
		return normalized;
	}
	
	public static void main(String[] args){
		String postFolder=StaticData.CHALLENGE_DATA+"/posts";
		PostContentMaker maker=new PostContentMaker(postFolder);
		maker.extractAllPosts();
		System.out.println("Extraction is completed.");
	}
	
}
