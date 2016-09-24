package collector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import config.StaticData;
import utility.ContentLoader;

public class CSVParser {
	
	String csvFileName;
	int savedPost=0;
	
	public CSVParser(String fileName)
	{
		this.csvFileName=fileName;
	}
	
	protected void processBlock(String content)
	{
		String splitBy="\"\n\"";
		String split2By="\",\"";
		String[] lines=content.split(splitBy);
		for(String line2:lines){
			//System.out.println(line2);
			String parts[]=line2.split(split2By);
			try{
				int postID=Integer.parseInt(parts[0].trim());
				//String title=parts[1].trim();
				//int questionID=Integer.parseInt(parts[1].trim());
				//String title=parts[1];
				//String tags=parts[2];
				//int viewCount=Integer.parseInt(parts[3].trim());
				String postBody=parts[1];
				savePost(postID, postBody);
				savedPost++;
			}catch(Exception exc){
				//handle the exception
				System.err.println(exc.getMessage());
			}
		}
	}
	
	protected void savePost(int postID, String postContent)
	{
		try {
			FileWriter fwriter=new FileWriter(new File(StaticData.CHALLENGE_DATA+"/solposts/"+postID+".txt"));
			fwriter.write(postContent);
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	protected void parseCSVFile() {
		// parsing CSV file
		try {
			File f = new File(this.csvFileName);

			BufferedReader br = new BufferedReader(new FileReader(f));
			String content = new String();
			String line = new String();
			int count = 0;
			while ((line = br.readLine()) != null) {
				if(line.startsWith("//cap"))continue;
				content += line + "\n";
				count++;
				if (count % 10000 == 0) {
					processBlock(content);
					content = new String();
					System.out.println(count);
				}
			}
			processBlock(content);
			System.out.println("Saved post:" + savedPost);
			br.close();
			// now split the file

		} catch (Exception exc) {
			// handle exception
			//exc.printStackTrace();
		}
	}
	
	
	protected void extractCSVItems()
	{
		String content=ContentLoader.loadFileContent(this.csvFileName);
		System.out.println("Content loaded..");
		String[] items=content.split("\"\n\"");
		System.out.println(items.length);
	}
	
	public static void main(String[] args){
		String csvFile=StaticData.CHALLENGE_DATA+"/ans-4103.csv";
		CSVParser parser=new CSVParser(csvFile);
		//parser.extractCSVItems();
		parser.parseCSVFile();
	}
}
