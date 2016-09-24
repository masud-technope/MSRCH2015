package utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import config.StaticData;

public class QATimeGapMaker {

	protected void saveNewData(String content) {
		// saving the content
		String dataFile = StaticData.CHALLENGE_DATA + "/postdata2.txt";
		try {
			FileWriter fwriter = new FileWriter(new File(dataFile), true);
			fwriter.write(content + "\n");
			fwriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected double getTimeDifference(String startDate, String endDate) {
		// getting time difference between two dates
		double timeDiff = 0;
		try {
			SimpleDateFormat format = new SimpleDateFormat(
					"dd/MM/yyyy HH:mm");
			Date start = format.parse(startDate);
			Date end = format.parse(endDate);
			timeDiff = (((double) (end.getTime() - start.getTime()) / 1000) / 60); // calculation
																						// in
																						// minutes
		} catch (Exception exc) {
			// handle the exception
		}
		return timeDiff; // in minutes
	}

	protected void refineExpData()
	{
		//refining the experiment data
		//String postdata = StaticData.CHALLENGE_DATA + "/postdata.txt";
		String postdata = StaticData.CHALLENGE_DATA + "/notacceptedpost.txt";
		try {
			Scanner scanner = new Scanner(new File(postdata));
			scanner.nextLine();
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] parts = line.split("\\s+");
				String questionID = parts[0].trim();
				String answerID = parts[1].trim();
				String reputation = parts[2].trim();
				String questionDate = parts[3] + " " + parts[4];
				String answerDate = parts[5] + " " + parts[6];
				double elapsedHour = getTimeDifference(questionDate, answerDate);
				String newDataLine = questionID + "\t" + answerID + "\t"
						+ reputation + "\t" + elapsedHour;
				// saving new data
				saveNewData(newDataLine);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		String start="04/02/2011 21:41";
		String end="04/02/2011 22:21";
		//getTimeDifference(start, end);
		QATimeGapMaker maker=new QATimeGapMaker();
		maker.refineExpData();
		System.out.println("Data refined successfully");
	}
}
