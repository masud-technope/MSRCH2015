package collector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import config.StaticData;

public class QAMetricAnalyzer {

	String questionFile;
	String answerFile;
	HashMap<Integer, String> questiondates;
	HashMap<Integer, String> answerdates;
	HashMap<Integer,Integer> accepteds;

	public QAMetricAnalyzer() {
		this.questionFile = StaticData.CHALLENGE_DATA + "/question.txt";
		this.answerFile = StaticData.CHALLENGE_DATA + "/answer.txt";
		this.questiondates = new HashMap<>();
		this.answerdates = new HashMap<>();
		this.accepteds=new HashMap<>();
		this.loadQuestionDates();
		this.loadAnswerDates();
	}

	protected void loadQuestionDates() {
		try {
			Scanner scanner = new Scanner(new File(this.questionFile));
			scanner.nextLine();
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				String[] parts = line.split("\\s+");
				int questionID = Integer.parseInt(parts[0]);
				int acceptedID=Integer.parseInt(parts[1]);
				String created = parts[2]+" "+parts[3];
				this.questiondates.put(questionID, created);
				this.accepteds.put(acceptedID,questionID);
			}
			scanner.close();
			System.out.println("Questions info loaded");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void loadAnswerDates() {
		try {
			Scanner scanner = new Scanner(new File(this.answerFile));
			scanner.nextLine();
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				String[] parts = line.split("\\s+");
				int questionID = Integer.parseInt(parts[0]);
				String created = parts[3]+" "+parts[4];
				this.answerdates.put(questionID, created);
			}
			scanner.close();
			System.out.println("Answers info loaded");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected double getTimeGap(String startDate, String endDate){
		//calculating time gap
		double hourgaps=0;
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date start=format.parse(startDate);
			Date end=format.parse(endDate);
			double gap=((double)(end.getTime()-start.getTime())/1000)/3600;
			hourgaps=gap;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hourgaps;
	}
	
	
	protected void getMetrics()
	{
		//calculate available metrics
		String outFile=StaticData.CHALLENGE_DATA+"/outmet.txt";
		try {
			FileWriter fwriter=new FileWriter(new File(outFile));
			Scanner scanner=new Scanner(new File(this.answerFile));
			scanner.nextLine();
			String metricLine=new String();
			while(scanner.hasNext()){
				String line=scanner.nextLine();
				String[] parts=line.split("\\s+");
				int ansID=Integer.parseInt(parts[0]);
				String ansDate=this.answerdates.get(ansID);
				int questID=Integer.parseInt(parts[1]);
				String questDate=this.questiondates.get(questID);
				double qagap=getTimeGap(questDate, ansDate);
				metricLine=ansID+"\t"+questID+"\t"+parts[2]+"\t"+qagap;
				if(this.accepteds.containsKey(ansID)){
					metricLine+="\t"+1;
				}else metricLine+="\t"+0;
			
				fwriter.write(metricLine+"\n");
			}
			scanner.close();
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	 public static void main(String[] args){
		 QAMetricAnalyzer analyzer=new QAMetricAnalyzer();
		 analyzer.getMetrics();
	 }
	

}
