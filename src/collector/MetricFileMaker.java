package collector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import config.StaticData;

public class MetricFileMaker {
	
	
	String outmetFile;
	String readabilityFile;
	String similarityFile;
	String topicsimilarityFile;
	String metricsFile;
	
	public MetricFileMaker()
	{
		this.outmetFile=StaticData.CHALLENGE_DATA+"/outmet.txt";
		this.readabilityFile=StaticData.CHALLENGE_DATA+"/readability.txt";
		this.similarityFile=StaticData.CHALLENGE_DATA+"/similarity.txt";
		this.topicsimilarityFile=StaticData.CHALLENGE_DATA+"/topicsimilarity.txt";
		this.metricsFile =StaticData.CHALLENGE_DATA+"/metrics-new.txt";
	}
	
	protected void collectPostMetrics()
	{
		//collect post metrics
		try {
			FileWriter fwriter=new FileWriter(new File(this.metricsFile));
			Scanner scanner1=new Scanner(new File(this.outmetFile));
			Scanner scanner2=new Scanner(new File(this.readabilityFile));
			Scanner scanner3=new Scanner(new File(this.topicsimilarityFile));
			scanner1.nextLine();
			scanner2.nextLine();
			scanner3.nextLine();
			while(scanner1.hasNext())
			{
				String line1=scanner1.nextLine();
				String line2=scanner2.nextLine();
				String line3=scanner3.nextLine();
				String myline=new String();
				String[] parts1=line1.split("\\s+");
				String[] parts2=line2.split("\\s+");
				String[] parts3=line3.split("\\s+");
				myline=parts2[1]+"\t"+parts2[2]+"\t"+parts2[3]+"\t"+parts3[1]+"\t"+parts1[2]+"\t"+parts1[3]+"\t"+parts1[4];
				fwriter.write(myline+"\n");
			}
			scanner1.close();
			scanner2.close();
			scanner3.close();
			fwriter.close();
			System.out.println("Metrics collected.");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		MetricFileMaker maker=new MetricFileMaker();
		maker.collectPostMetrics();
	}
}
